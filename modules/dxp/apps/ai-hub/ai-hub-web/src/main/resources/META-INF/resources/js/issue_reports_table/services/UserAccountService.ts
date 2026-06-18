/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const screenNameCache = new Map<number, string>();
const pendingRequests = new Map<number, Promise<string>>();

function getCachedScreenName(userId?: number): string {
	return userId ? screenNameCache.get(userId) ?? '' : '';
}

function getScreenName(userId: number): Promise<string> {
	if (screenNameCache.has(userId)) {
		return Promise.resolve(screenNameCache.get(userId) ?? '');
	}

	let promise = pendingRequests.get(userId);

	if (!promise) {
		promise = fetch(
			`/o/headless-admin-user/v1.0/user-accounts/${userId}?fields=alternateName`,
			{headers: new Headers({Accept: 'application/json'})}
		)
			.then((response) => response.json())
			.then((data) => {
				const alternateName = data?.alternateName ?? '';

				screenNameCache.set(userId, alternateName);
				pendingRequests.delete(userId);

				return alternateName;
			})
			.catch(() => {
				pendingRequests.delete(userId);

				return '';
			});

		pendingRequests.set(userId, promise);
	}

	return promise;
}

export {getCachedScreenName, getScreenName};
