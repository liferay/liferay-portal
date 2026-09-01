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
	private static instanceCount = 0;

	private appId?: string;
	private atom!: Atom<FDSState>;
	private disconnected = false;
	private fdsName: string;
	private hasWrittenFilters = false;
	private instanceId: number = ++FDSConnection.instanceCount;
	private isReady = false;
	private navigationHandle: {detach: () => void};
	private onFDSConnectionInfoChange: (
		fdsConnectionInfo: FDSConnectionInfo
	) => void;
	private ownsFiltering: boolean;
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
		this.fdsName = fdsName;
		this.onFDSConnectionInfoChange = onFDSConnectionInfoChange;
		this.requestedOwnership = options.owns ?? DEFAULT_OWNERSHIP;
		this.ownsFiltering = this.requestedOwnership.includes('filters');
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

				// Before the restore below, since a refused connection must
				// not consume what the URL left for the owner.

				this.resolveFilteringOwnership();

				if (this.ownsFiltering) {
					const restoredConnectionState = Liferay.State.read(
						this.selectors.restoredConnectionState
					);

					if (restoredConnectionState !== undefined) {
						this.restoreConnectionState(restoredConnectionState);
					}

					if (!this.hasWrittenFilters) {
						this.writeConnectionFilters([]);
					}
				}

				this.subscriptions = {
					search: Liferay.State.subscribe(
						this.selectors.search,
						fdsStateChangeCallback.search
					),
				};

				// The browser's back and forward buttons move a data set
				// between filters the same way they move it between searches,
				// and the data set offers each one it lands on here.

				if (this.ownsFiltering) {
					this.subscriptions.restoredConnectionState =
						Liferay.State.subscribe(
							this.selectors.restoredConnectionState,
							this.handleRestoredConnectionState
						);
				}

				// initialize consumer's state

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

		if (!this.ownsFiltering) {
			this.warn(
				'Ignored setFilters() for ' +
					this.fdsName +
					': ' +
					(this.requestedOwnership.includes('filters')
						? 'another connection owns the filtering'
						: "connect with owns: ['filters'] to take the" +
							' filtering over')
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

		if (this.ownsFiltering && this.isReady) {
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
		this.hasWrittenFilters = false;

		const fdsState = {...Liferay.State.read(this.atom)};

		// The filters go whole, since their presence is what says the
		// filtering is owned at all, and this connection is the owner.

		delete fdsState.connectionFilters;

		const remaining = this.withoutOwnKey(fdsState.connectionState);

		if (remaining) {
			fdsState.connectionState = remaining;
		}
		else {
			delete fdsState.connectionState;
		}

		Liferay.State.write(this.atom, fdsState);
	}

	private resolveFilteringOwnership(): void {
		if (this.ownsFiltering) {
			if (!this.appId) {
				this.refuseFiltering(
					'connect with an appId to own the filtering, since what' +
						' a connection filters by is kept in the URL under it'
				);
			}
			else if (this.isFilteringOwned()) {
				this.refuseFiltering(
					'another connection already owns it, and a data set can' +
						' only have one filtering owner'
				);

				this.warnFilteringTaken();
			}
		}
	}

	private refuseFiltering(reason: string): void {
		this.ownsFiltering = false;

		this.warn(
			'Refused the filtering of ' +
				this.fdsName +
				' to this connection: ' +
				reason
		);
	}

	private isFilteringOwned(): boolean {
		return Liferay.State.read(this.atom).connectionFilters !== undefined;
	}

	private isFilteringRefused(): boolean {
		return (
			this.requestedOwnership.includes('filters') && !this.ownsFiltering
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
		this.hasWrittenFilters = true;

		Liferay.State.write(this.atom, {
			...Liferay.State.read(this.atom),
			connectionFilters,
			connectionState:
				connectionState === undefined
					? undefined
					: {[this.appId!]: connectionState},
		});
	}

	private notifyStatus(status: FDSConnectionStatus): void {
		this.onFDSConnectionInfoChange({
			fdsName: this.fdsName,
			instanceId: this.instanceId,
			status,
		});
	}
}
