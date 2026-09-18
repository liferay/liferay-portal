/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {FDSState} from '@liferay/js-api/data-set/connection';

const DEFAULT_TIMEOUT = 5000;
const DEFAULT_INTERVAL = 100;

/**
 * Waits for the FDS atom that the widget registers under
 * `${id}_fdsState` to appear in the global `Liferay.State` registry,
 * polling until it is found or the timeout elapses.
 *
 * The runtime must go through the global singleton (not the imported
 * copy of `@liferay/frontend-js-state-web`) so that consumers share the
 * same atom registry that the Frontend Data Set widget populates.
 */
export function getFDSAtom(
	id: string,
	options?: {interval?: number; timeout?: number}
): Promise<Liferay.State.Atom<FDSState>> {
	const timeout = options?.timeout ?? DEFAULT_TIMEOUT;
	const interval = options?.interval ?? DEFAULT_INTERVAL;

	const key = `${id}_fdsState`;

	return new Promise((resolve, reject) => {
		const existing = Liferay?.State?.__unsafe__?.getAtomOrSelectorKey(key);

		if (existing) {
			return resolve(existing as Liferay.State.Atom<FDSState>);
		}

		const startTime = Date.now();

		const poll = setInterval(() => {
			const atom = Liferay?.State?.__unsafe__?.getAtomOrSelectorKey(key);

			if (atom) {
				clearInterval(poll);

				return resolve(atom as Liferay.State.Atom<FDSState>);
			}

			if (Date.now() - startTime >= timeout) {
				clearInterval(poll);

				reject(
					new Error(
						`FDS atom "${key}" was not found within ${timeout}ms`
					)
				);
			}
		}, interval);
	});
}

export function getOrCreateSelector<T>(
	key: string,
	deriveValue: (get: Liferay.State.Getter) => T
): Liferay.State.Selector<T> {
	const existing = Liferay.State.__unsafe__.getAtomOrSelectorKey(key);

	return (
		(existing as Liferay.State.Selector<T> | null) ??
		Liferay.State.selector<T>(key, deriveValue)
	);
}
