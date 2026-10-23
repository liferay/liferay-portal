/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import {fetchAllItems} from '../../src/main/resources/META-INF/resources/js/services/fetchAllItems';

function buildURL(page: number, pageSize: number) {
	return `/o/items?page=${page}&pageSize=${pageSize}`;
}

function page(items: string[], lastPage: number, totalCount: number) {
	return JSON.stringify({items, lastPage, totalCount});
}

describe('fetchAllItems', () => {
	it('requests every page and accumulates the items', async () => {
		fetch.mockResponses(
			page(['a', 'b'], 3, 5),
			page(['c', 'd'], 3, 5),
			page(['e'], 3, 5)
		);

		const result = await fetchAllItems<string>(buildURL);

		expect(result).toEqual({
			data: {items: ['a', 'b', 'c', 'd', 'e'], totalCount: 5},
			error: null,
		});
		expect(fetch.mock.calls.map(([url]) => url)).toEqual([
			'/o/items?page=1&pageSize=200',
			'/o/items?page=2&pageSize=200',
			'/o/items?page=3&pageSize=200',
		]);
	});

	it('stops at the first page that fails', async () => {
		fetch.mockResponses(page(['a'], 3, 3), [
			JSON.stringify({title: 'Page not found'}),
			{status: 404},
		]);

		const result = await fetchAllItems<string>(buildURL);

		expect(result).toMatchObject({data: null, error: 'Page not found'});
		expect(fetch).toHaveBeenCalledTimes(2);
	});
});
