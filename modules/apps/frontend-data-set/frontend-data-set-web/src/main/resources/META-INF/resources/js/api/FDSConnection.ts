/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFDSAtom, getOrCreateSelector} from './getFDSAtom';

import type {
	FDSConnectionFilter,
	FDSConnectionInfo,
	FDSConnectionOptions,
	FDSConnectionStatus,
	FDSState,
	FDSStateChangeCallback,
} from '@liferay/js-api/data-set';
import Atom = Liferay.State.Atom;

const DEFAULT_TIMEOUT = 10000;

interface Subscriptions {
	search: {dispose: () => void};
}

interface Selectors {
	search: Liferay.State.Selector<string>;
}

export class FDSConnection {
	private static instanceCount = 0;

	private atom!: Atom<FDSState>;
	private clearFiltersWhenDisconnect = false;
	private disconnected = false;
	private fdsName: string;
	private instanceId: number = ++FDSConnection.instanceCount;
	private isReady = false;
	private navigationHandle: {detach: () => void};
	private onFDSConnectionInfoChange: (
		fdsConnectionInfo: FDSConnectionInfo
	) => void;
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
	 * Takes the filtering over with the given expressions, replacing whatever
	 * a previous call passed. From the first call on, the filters the data set
	 * declares no longer reach the request: the consumer owns the whole filter
	 * expression.
	 */
	setFilters = (filters: Array<FDSConnectionFilter>): void => {
		if (!this.isReady) {
			return;
		}

		const current = Liferay.State.read(this.atom);

		this.clearFiltersWhenDisconnect = true;

		Liferay.State.write(this.atom, {
			...current,
			connectionFilters: filters.map(({id, odataFilterString}) => ({
				id,
				odataFilterString,
			})),
		});
	};

	/**
	 * Drops the filters this connection applies, so that the data set filters
	 * nothing: a shortcut for `setFilters([])`, and what `disconnect()` does
	 * on the way out. The filtering stays taken over, so the filters the data
	 * set declares do not come back.
	 */
	clearFilters = (): void => {
		this.setFilters([]);
	};

	disconnect = (): void => {
		if (this.disconnected) {
			return;
		}

		// Leave nothing of this connection applied: a consumer that never
		// filtered must not suppress the filters the data set declares on its
		// way out, so only a connection that did take the filtering over
		// clears it.

		if (this.clearFiltersWhenDisconnect) {
			this.clearFilters();
		}

		this.subscriptions?.search?.dispose();
		this.disconnected = true;
		this.isReady = false;
		this.navigationHandle.detach();
		this.notifyStatus('disconnected');
	};

	private warn(msg: string): void {
		console.warn('[FDSConnection', this.instanceId, ']', msg);
	}

	private notifyStatus(status: FDSConnectionStatus): void {
		this.onFDSConnectionInfoChange({
			fdsName: this.fdsName,
			instanceId: this.instanceId,
			status,
		});
	}
}
