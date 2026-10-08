/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getRandomInt(min, max) {
	return Math.floor(Math.random() * (max - min)) + min;
}

export function getRequestBody(method) {
	const [, init] = fetch.mock.calls.findLast(
		([, init]) => init?.method === method
	);

	return JSON.parse(init.body);
}

export function processFakeRequestData(url, items, queriedItems) {
	url = new URL(url, Liferay.ThemeDisplay.getPortalURL());

	const page = url.searchParams.get('page') || 1;
	const pageSize = url.searchParams.get('pageSize') || 10;
	const query = url.searchParams.get('query');
	const lastPage = Math.ceil(items.length - 1 / pageSize);

	const itemsToBeReturned = [];

	if (query) {
		itemsToBeReturned.push(...queriedItems);
	}
	else {
		const startIndex = page * pageSize - pageSize;

		const endIndex =
			startIndex + pageSize >= items.length
				? items.length
				: startIndex + pageSize;

		itemsToBeReturned.push(...items.slice(startIndex, endIndex));
	}

	return {
		items: itemsToBeReturned,
		lastPage,
		page,
		pageSize,
		totalCount: query ? queriedItems.length : items.length,
	};
}
