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

const DEFAULT_OWNERSHIP: ReadonlyArray<FDSConnectionOwnership> = ['search'];

interface Subscriptions {
	offeredCustomConfigs?: {dispose: () => void};
	search: {dispose: () => void};
}

type FDSConnectionCustomConfig = unknown;

type FDSConnectionCustomConfigs = Readonly<
	Record<string, FDSConnectionCustomConfig>
> | null;

interface Selectors {
	offeredCustomConfigs: Liferay.State.Selector<
		FDSConnectionCustomConfigs | undefined
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

	private apply?: (customConfig: FDSConnectionCustomConfig) => void;
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
		this.apply = fdsStateChangeCallback.apply;
		this.appId = options.appId;
		this.element = options.element;
		this.fdsName = fdsName;
		this.onFDSConnectionInfoChange = onFDSConnectionInfoChange;
		this.requestedOwnership = options.owns ?? DEFAULT_OWNERSHIP;
		this.notifyStatus('connecting');

		getFDSAtom(fdsName, {timeout: options.timeout ?? DEFAULT_TIMEOUT})
			.then((atom: Atom<FDSState>) => {
				if (this.disconnected) {
					return;
				}

				this.atom = atom;

				this.selectors = {
					offeredCustomConfigs: getOrCreateSelector(
						`${atom.key}_offeredCustomConfigs`,
						(get) => get(atom).offeredCustomConfigs
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

				// Before applying below, since a refused connection must not
				// consume what the URL left for the owner.

				this.acquireFilteringOwnership();

				// initialize consumer's state

				if (this.ownsFiltering()) {
					this.subscriptions.offeredCustomConfigs =
						Liferay.State.subscribe(
							this.selectors.offeredCustomConfigs,
							this.handleOfferedCustomConfigs
						);

					const offeredCustomConfigs = Liferay.State.read(
						this.selectors.offeredCustomConfigs
					);

					if (offeredCustomConfigs !== undefined) {
						this.applyOwnCustomConfig(offeredCustomConfigs);
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
	 */
	setFilters = (
		filters: Array<FDSConnectionFilter>,
		customConfig?: FDSConnectionCustomConfig
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
			customConfig
		);
	};

	clearFilters = (): void => {
		this.setFilters([]);
	};

	disconnect = (): void => {
		if (this.disconnected) {
			return;
		}

		if (this.ownsFiltering() && this.isReady) {
			this.releaseFiltering();
		}

		this.subscriptions?.offeredCustomConfigs?.dispose();
		this.subscriptions?.search?.dispose();
		this.disconnected = true;
		this.isReady = false;
		this.navigationHandle.detach();
		this.notifyStatus('disconnected');
	};

	private applyOwnCustomConfig(
		offeredCustomConfigs: FDSConnectionCustomConfigs
	): void {
		const customConfig =
			offeredCustomConfigs === null
				? null
				: offeredCustomConfigs[this.appId!] ?? null;

		if (this.apply) {
			this.apply(customConfig);
		}
		else if (customConfig !== null) {
			this.warn(
				'Dropped the custom config offered for ' +
					this.fdsName +
					': connect with an apply state change callback to put' +
					' its filters back'
			);
		}

		this.dropOwnOfferedCustomConfig();
	}

	private dropOwnOfferedCustomConfig(): void {
		const fdsState = {...Liferay.State.read(this.atom)};

		const remaining = this.withoutOwnKey(fdsState.offeredCustomConfigs);

		if (remaining) {
			fdsState.offeredCustomConfigs = remaining;
		}
		else {
			delete fdsState.offeredCustomConfigs;
		}

		Liferay.State.write(this.atom, fdsState);
	}

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

	private handleOfferedCustomConfigs = (
		offeredCustomConfigs: FDSConnectionCustomConfigs | undefined
	): void => {

		// Dropping it above sets this to nothing, which comes back here:
		// there is no consumer left to tell.

		if (offeredCustomConfigs === undefined) {
			return;
		}

		if (
			offeredCustomConfigs !== null &&
			!(this.appId! in offeredCustomConfigs)
		) {
			return;
		}

		this.applyOwnCustomConfig(offeredCustomConfigs);
	};

	private releaseFiltering(): void {
		FDSConnection.filteringOwners.delete(this.fdsName);

		const fdsState = {...Liferay.State.read(this.atom)};

		delete fdsState.connectionFilters;
		delete fdsState.filteringOwnerAppId;

		const remaining = this.withoutOwnKey(fdsState.appliedCustomConfigs);

		if (remaining) {
			fdsState.appliedCustomConfigs = remaining;
		}
		else {
			delete fdsState.appliedCustomConfigs;
		}

		Liferay.State.write(this.atom, fdsState);
	}

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

		const fdsState = {...Liferay.State.read(this.atom)};

		fdsState.filteringOwnerAppId = this.appId;

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
		customConfig?: FDSConnectionCustomConfig
	): void {
		const fdsState = {...Liferay.State.read(this.atom), connectionFilters};

		const remaining = this.withoutOwnKey(fdsState.appliedCustomConfigs);

		// The state read back is deeply readonly, which maps a value the data
		// set keeps without reading to Readonly<unknown>, and nothing unknown
		// satisfies that. Saying so here is the whole of it: what a consumer
		// asks to have remembered is opaque going in and coming out.

		const customConfigs =
			customConfig === undefined
				? remaining
				: {
						...remaining,
						[this.appId!]: customConfig as Readonly<unknown>,
					};

		if (customConfigs) {
			fdsState.appliedCustomConfigs = customConfigs;
		}
		else {
			delete fdsState.appliedCustomConfigs;
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
