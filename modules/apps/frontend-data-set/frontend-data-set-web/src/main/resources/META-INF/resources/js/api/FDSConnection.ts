/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFDSAtom, getOrCreateSelector} from './getFDSAtom';

import type {
	FDSConnectionFilter,
	FDSConnectionInfo,
	FDSConnectionOptions,
	FDSConnectionOwnership,
	FDSConnectionStatus,
	FDSState,
	FDSStateChangeCallback,
} from '@liferay/js-api/data-set';
import Atom = Liferay.State.Atom;

const DEFAULT_TIMEOUT = 10000;

// What a connection owns when it does not say: the search, which is what
// every connection has always driven through its state change callback.

const DEFAULT_OWNERSHIP: ReadonlyArray<FDSConnectionOwnership> = ['search'];

interface Subscriptions {
	restoredConnectionState?: {dispose: () => void};
	search: {dispose: () => void};
}

type RestoredConnectionState = Readonly<Record<string, unknown>> | null;

interface Selectors {
	restoredConnectionState: Liferay.State.Selector<
		RestoredConnectionState | undefined
	>;
	search: Liferay.State.Selector<string>;
}

export class FDSConnection {

	// The filtering of a data set has one owner at a time, and this is who:
	// the connection it was granted to, under the name of the data set it
	// filters. One app may own the filtering of several data sets, and every
	// data set has at most the one owner, so the data set is what a claim is
	// keyed by and the connection itself is what answers for it.
	//
	// A claim is taken and given back within one turn of the event loop, so
	// no other connection ever runs in between: a page ends up with a single
	// owner without any locking.

	private static filteringOwners = new Map<string, FDSConnection>();
	private static instanceCount = 0;

	private appId?: string;
	private atom!: Atom<FDSState>;
	private disconnected = false;
	private element?: HTMLElement;
	private fdsName: string;
	private instanceId: number = ++FDSConnection.instanceCount;
	private isReady = false;
	private navigationHandle: {detach: () => void};
	private onFDSConnectionInfoChange: (
		fdsConnectionInfo: FDSConnectionInfo
	) => void;
	private requestedOwnership: ReadonlyArray<FDSConnectionOwnership>;
	private restore?: (connectionState: unknown) => void;
	private selectors!: Selectors;
	private subscriptions!: Subscriptions;

	constructor(
		fdsName: string,
		fdsStateChangeCallback: FDSStateChangeCallback,
		onFDSConnectionInfoChange: (
			fdsConnectionInfo: FDSConnectionInfo
		) => void,
		options: FDSConnectionOptions = {}
	) {
		this.appId = options.appId;
		this.element = options.element;
		this.fdsName = fdsName;
		this.onFDSConnectionInfoChange = onFDSConnectionInfoChange;
		this.requestedOwnership = options.owns ?? DEFAULT_OWNERSHIP;
		this.restore = fdsStateChangeCallback.restore;
		this.notifyStatus('connecting');

		getFDSAtom(fdsName, {timeout: options.timeout ?? DEFAULT_TIMEOUT})
			.then((atom: Atom<FDSState>) => {
				if (this.disconnected) {
					return;
				}

				this.atom = atom;

				this.selectors = {
					restoredConnectionState: getOrCreateSelector(
						`${atom.key}_restoredConnectionState`,
						(get) => get(atom).restoredConnectionState
					),
					search: getOrCreateSelector(
						`${atom.key}_searchQuery`,
						(get) => get(atom).search.query
					),
				};

				// mark connection as ready, so getters/setters are unblocked and available to callbacks

				this.isReady = true;

				this.subscriptions = {
					search: Liferay.State.subscribe(
						this.selectors.search,
						fdsStateChangeCallback.search
					),
				};

				// Before the restore below, since a refused connection must
				// not consume what the URL left for the owner.

				this.acquireFilteringOwnership();

				// initialize consumer's state

				if (this.ownsFiltering()) {

					// The browser's back and forward buttons move a data set
					// between filters the same way they move it between
					// searches, and the data set offers each one it lands on
					// here. Subscribed before what is on offer is handed
					// over, so that an offer made while it is being handed
					// over is not missed.

					this.subscriptions.restoredConnectionState =
						Liferay.State.subscribe(
							this.selectors.restoredConnectionState,
							this.handleRestoredConnectionState
						);

					const restoredConnectionState = Liferay.State.read(
						this.selectors.restoredConnectionState
					);

					// Handed over as it is, an offer that holds no key of
					// this connection included: a consumer told nothing at
					// all cannot tell an address that filters nothing from
					// one it has not been offered yet. What
					// handleRestoredConnectionState guards is the changes
					// that follow, where a key of another connection is none
					// of this one's business.

					if (restoredConnectionState !== undefined) {
						this.restoreConnectionState(restoredConnectionState);
					}
				}

				fdsStateChangeCallback.search(this.getSearch() || '');

				this.notifyStatus(
					this.isFilteringRefused() ? 'refused' : 'ready'
				);
			})
			.catch((error: Error) => {
				if (this.disconnected) {
					return;
				}

				this.warn(
					'Connection timed out for ' + fdsName + ': ' + error.message
				);

				this.notifyStatus('timeout');
			});

		// ensure consumers don't need to dispose the subscriptions on SPA navigations

		this.navigationHandle = Liferay.on('beforeNavigate', () => {
			this.disconnect();
		});
	}

	getSearch = (): string | null => {
		if (!this.isReady) {
			return null;
		}

		return Liferay.State.read(this.selectors.search);
	};

	setSearch = (query: string): void => {
		if (!this.isReady) {
			return;
		}

		const current = Liferay.State.read(this.atom);

		Liferay.State.write(this.atom, {
			...current,
			search: {...current.search, query},
		});
	};

	/**
	 * Applies the given expressions, replacing whatever a previous call
	 * passed. The filters the data set declares never reach the request while
	 * this connection owns the filtering: the consumer owns the whole filter
	 * expression.
	 *
	 * Only a connection that was granted the filtering may filter, so that a
	 * data set has one filtering owner and shows a filter UI only when that
	 * owner is itself. Asking for it with `owns: ['filters']` is not enough:
	 * an `appId` is needed to own it, and another connection may already
	 * have. A connection refused the filtering settles at the `refused`
	 * status, and this call does nothing for it.
	 *
	 * Whatever the consumer passes as `connectionState` is kept in the page
	 * URL for as long as these filters reach the request, and comes back
	 * through the `restore` state change callback on the next visit.
	 */
	setFilters = (
		filters: Array<FDSConnectionFilter>,
		connectionState?: unknown
	): void => {
		if (!this.isReady) {
			return;
		}

		if (!this.ownsFiltering()) {
			this.warn(
				'Ignored setFilters() for ' +
					this.fdsName +
					': ' +
					(!this.requestedOwnership.includes('filters')
						? "connect with owns: ['filters'] to take the" +
							' filtering over'
						: this.appId
							? 'another connection owns the filtering'
							: 'connect with an appId to own the filtering,' +
								' since what a connection filters by is kept' +
								' in the URL under it')
			);

			return;
		}

		this.writeConnectionFilters(
			filters.map(({id, odataFilterString}) => ({id, odataFilterString})),
			connectionState
		);
	};

	/**
	 * Drops the filters this connection applies, so that the data set filters
	 * nothing: a shortcut for `setFilters([])`. The filtering stays taken
	 * over, so the filters the data set declares do not come back and its
	 * filter UI stays hidden.
	 */
	clearFilters = (): void => {
		this.setFilters([]);
	};

	disconnect = (): void => {
		if (this.disconnected) {
			return;
		}

		// Hand the filtering back on the way out, so that a data set left
		// without a consumer applies the filters it declares again and offers
		// the UI for them. A connection that never owned the filtering has
		// nothing to hand back.

		if (this.ownsFiltering() && this.isReady) {
			this.releaseFiltering();
		}

		this.subscriptions?.restoredConnectionState?.dispose();
		this.subscriptions?.search?.dispose();
		this.disconnected = true;
		this.isReady = false;
		this.navigationHandle.detach();
		this.notifyStatus('disconnected');
	};

	private restoreConnectionState(
		restoredConnectionState: RestoredConnectionState
	): void {
		const connectionState =
			restoredConnectionState === null
				? null
				: restoredConnectionState[this.appId!] ?? null;

		if (this.restore) {
			this.restore(connectionState);
		}
		else if (connectionState !== null) {
			this.warn(
				'Dropped the filters restored for ' +
					this.fdsName +
					': connect with a restore state change callback to put' +
					' them back'
			);
		}

		this.dropRestoredConnectionState();
	}

	private dropRestoredConnectionState(): void {
		const fdsState = {...Liferay.State.read(this.atom)};

		const remaining = this.withoutOwnKey(fdsState.restoredConnectionState);

		if (remaining) {
			fdsState.restoredConnectionState = remaining;
		}
		else {
			delete fdsState.restoredConnectionState;
		}

		Liferay.State.write(this.atom, fdsState);
	}

	/**
	 * The given slice without this connection's key, or nothing at all once
	 * no other key is left. Both the state a connection leaves behind and the
	 * state it is offered are keyed by `appId`, so neither may be dropped
	 * whole: a key is only ever this connection's to take.
	 */
	private withoutOwnKey<T extends object>(
		keyedByAppId: T | null | undefined
	): T | undefined {
		if (!keyedByAppId) {
			return undefined;
		}

		const remaining = Object.fromEntries(
			Object.entries(keyedByAppId).filter(
				([appId]) => appId !== this.appId
			)
		);

		return Object.keys(remaining).length ? (remaining as T) : undefined;
	}

	private handleRestoredConnectionState = (
		restoredConnectionState: RestoredConnectionState | undefined
	): void => {

		// Dropping it above sets this to nothing, which comes back here:
		// there is no consumer left to tell.

		if (restoredConnectionState === undefined) {
			return;
		}

		if (
			restoredConnectionState !== null &&
			!(this.appId! in restoredConnectionState)
		) {
			return;
		}

		this.restoreConnectionState(restoredConnectionState);
	};

	private releaseFiltering(): void {
		FDSConnection.filteringOwners.delete(this.fdsName);

		const fdsState = {...Liferay.State.read(this.atom)};

		// The filters go whole, so that the ones the data set declares reach
		// the request again, and the claim goes with them: a claim left behind
		// would keep the filtering from every consumer that connects later.

		delete fdsState.connectionFilters;
		delete fdsState.filteringOwnerAppId;

		const remaining = this.withoutOwnKey(fdsState.connectionState);

		if (remaining) {
			fdsState.connectionState = remaining;
		}
		else {
			delete fdsState.connectionState;
		}

		Liferay.State.write(this.atom, fdsState);
	}

	/**
	 * Grants the filtering of the data set to this connection, unless a
	 * reason to refuse it comes first, so that a data set never has two
	 * filtering owners. A connection that is refused settles at the
	 * `refused` status and every filter call it makes is ignored.
	 *
	 * Reading the claim and taking it happen without an await in between, so
	 * a second connection cannot come upon the data set between the two.
	 */
	private acquireFilteringOwnership(): void {
		if (!this.requestedOwnership.includes('filters')) {
			return;
		}

		if (!this.appId) {
			this.refuseFiltering(
				'connect with an appId to own the filtering, since what' +
					' a connection filters by is kept in the URL under it'
			);

			return;
		}

		if (this.isFilteringOwnedByAnotherConnection()) {
			this.refuseFiltering(
				'another connection already owns it, and a data set can' +
					' only have one filtering owner'
			);

			this.warnFilteringTaken();

			return;
		}

		FDSConnection.filteringOwners.set(this.fdsName, this);

		// Said in the state of the data set as well, since the data set is
		// the side that keeps what this connection remembers in the URL and
		// offers it back: what it holds is filed under an app id, and this is
		// how it knows which one to expect a connection for. Another copy of
		// this module on the page reads it for the same reason it cannot read
		// the owners this one keeps.

		const fdsState = {...Liferay.State.read(this.atom)};

		fdsState.filteringOwnerAppId = this.appId;

		// Any filters left in the state were applied by a connection that is
		// no longer the owner, and this one has not said what it filters by.

		delete fdsState.connectionFilters;

		Liferay.State.write(this.atom, fdsState);
	}

	private ownsFiltering(): boolean {
		return FDSConnection.filteringOwners.get(this.fdsName) === this;
	}

	private refuseFiltering(reason: string): void {
		this.warn(
			'Refused the filtering of ' +
				this.fdsName +
				' to this connection: ' +
				reason
		);
	}

	/**
	 * Whether the filtering of this data set is already owned by a
	 * connection other than this one, whether of this page or of another
	 * copy of this module on it.
	 *
	 * A connection whose element has left the document is not one of them. A
	 * client extension is a custom element, and one taken off the page
	 * without disconnecting would otherwise leave a claim that outlives it
	 * and locks the data set out until the next reload. Its element leaving
	 * is the only account of that anyone gets, so the claim is handed back
	 * here the way disconnecting would have handed it back.
	 */
	private isFilteringOwnedByAnotherConnection(): boolean {
		const owner = FDSConnection.filteringOwners.get(this.fdsName);

		// Nothing asks this after taking the claim today, and this is what
		// keeps the reclaiming below from tearing down the very connection
		// that asked.

		if (owner === this) {
			return false;
		}

		if (owner) {
			if (!owner.element || owner.element.isConnected) {
				return true;
			}

			owner.disconnect();
		}

		// Another copy of this module on the page keeps its own owners, out of
		// reach of this one, so what says the filtering is owned then is the
		// claim its owner left in the state of the data set. A claim of that
		// copy can only be respected here, never reclaimed.

		return Liferay.State.read(this.atom).filteringOwnerAppId !== undefined;
	}

	private isFilteringRefused(): boolean {
		return (
			this.requestedOwnership.includes('filters') && !this.ownsFiltering()
		);
	}

	private warn(msg: string): void {
		console.warn(
			'[FDSConnection',
			(this.appId ?? 'no-appId') + '#' + this.instanceId,
			']',
			msg
		);
	}

	private warnFilteringTaken(): void {
		Liferay.Util.openToast({
			message: Liferay.Language.get(
				'another-widget-is-already-filtering-this-data-set'
			),
			type: 'warning',
		});
	}

	private writeConnectionFilters(
		connectionFilters: Array<FDSConnectionFilter>,
		connectionState?: unknown
	): void {
		const fdsState = {...Liferay.State.read(this.atom), connectionFilters};

		// The state of the data set holds one key per connection and the URL
		// is written from the whole map, so the only key this connection may
		// write is its own. Remembering nothing takes that key out and leaves
		// every other one where it was, rather than emptying the map: what
		// another connection asked to have remembered is not this one's to
		// drop, and a URL is shared by everything on the page.

		const remaining = this.withoutOwnKey(fdsState.connectionState);

		const connectionStates =
			connectionState === undefined
				? remaining
				: {...remaining, [this.appId!]: connectionState};

		if (connectionStates) {
			fdsState.connectionState = connectionStates;
		}
		else {
			delete fdsState.connectionState;
		}

		Liferay.State.write(this.atom, fdsState);
	}

	private notifyStatus(status: FDSConnectionStatus): void {
		this.onFDSConnectionInfoChange({
			fdsName: this.fdsName,
			instanceId: this.instanceId,
			status,
		});
	}
}
