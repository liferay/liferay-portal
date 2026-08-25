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
	search: {dispose: () => void};
}

interface Selectors {
	search: Liferay.State.Selector<string>;
}

export class FDSConnection {
	private static instanceCount = 0;

	private atom!: Atom<FDSState>;
	private disconnected = false;
	private fdsName: string;
	private instanceId: number = ++FDSConnection.instanceCount;
	private isReady = false;
	private navigationHandle: {detach: () => void};
	private onFDSConnectionInfoChange: (
		fdsConnectionInfo: FDSConnectionInfo
	) => void;
	private ownsFiltering: boolean;
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
		this.fdsName = fdsName;
		this.onFDSConnectionInfoChange = onFDSConnectionInfoChange;
		this.ownsFiltering = (options.owns ?? DEFAULT_OWNERSHIP).includes(
			'filters'
		);
		this.notifyStatus('connecting');

		getFDSAtom(fdsName, {timeout: options.timeout ?? DEFAULT_TIMEOUT})
			.then((atom: Atom<FDSState>) => {
				if (this.disconnected) {
					return;
				}

				this.atom = atom;

				this.selectors = {
					search: getOrCreateSelector(
						`${atom.key}_searchQuery`,
						(get) => get(atom).search.query
					),
				};

				// mark connection as ready, so getters/setters are unblocked and available to callbacks

				this.isReady = true;

				// Take the filtering over before the consumer hears the
				// connection is ready, so that the data set drops its filter
				// UI as part of connecting rather than at the first
				// setFilters() call, which would make the dropdown flash.

				if (this.ownsFiltering) {
					this.writeConnectionFilters([]);
				}

				this.subscriptions = {
					search: Liferay.State.subscribe(
						this.selectors.search,
						fdsStateChangeCallback.search
					),
				};

				// initialize consumer's state

				fdsStateChangeCallback.search(this.getSearch() || '');

				// then inform consumer everything is settled

				this.notifyStatus('ready');
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
	 * Only a connection that declared `owns: ['filters']` may filter, so that
	 * a data set has one filtering owner and shows a filter UI only when that
	 * owner is itself.
	 */
	setFilters = (filters: Array<FDSConnectionFilter>): void => {
		if (!this.isReady) {
			return;
		}

		if (!this.ownsFiltering) {
			this.warn(
				'Ignored setFilters() for ' +
					this.fdsName +
					": connect with owns: ['filters'] to take the filtering over"
			);

			return;
		}

		this.writeConnectionFilters(
			filters.map(({id, odataFilterString}) => ({id, odataFilterString}))
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

		this.subscriptions?.search?.dispose();
		this.disconnected = true;
		this.isReady = false;
		this.navigationHandle.detach();
		this.notifyStatus('disconnected');
	};

	private releaseFiltering(): void {
		const fdsState = {...Liferay.State.read(this.atom)};

		delete fdsState.connectionFilters;

		Liferay.State.write(this.atom, fdsState);
	}

	private warn(msg: string): void {
		console.warn('[FDSConnection', this.instanceId, ']', msg);
	}

	private writeConnectionFilters(
		connectionFilters: Array<FDSConnectionFilter>
	): void {
		Liferay.State.write(this.atom, {
			...Liferay.State.read(this.atom),
			connectionFilters,
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
