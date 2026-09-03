/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getItems} from '../../../../src/main/resources/META-INF/resources/js/utils/client.es';

const ENDPOINT = '/o/data-engine/v2.0/data-definitions/by-content-type/journal';

describe('getItems', () => {
	it('passes the abort signal to the request', async () => {
		fetch.mockResponseOnce(JSON.stringify({items: [], totalCount: 0}));

		const abortController = new AbortController();

		await getItems(ENDPOINT, '', {signal: abortController.signal});

		expect(fetch.mock.calls[0][1].signal).toBe(abortController.signal);
	});

	it('rejects instead of resolving with an empty list when a request fails', async () => {
		fetch.mockResponseOnce(JSON.stringify({title: 'Forbidden'}), {
			status: 403,
		});

		await expect(getItems(ENDPOINT)).rejects.toEqual({title: 'Forbidden'});
	});

	it('resolves with an empty list when the response has no items', async () => {
		fetch.mockResponseOnce(JSON.stringify({}));

		expect(await getItems(ENDPOINT)).toEqual({items: [], totalCount: 0});
	});

	it('returns the items and total count of a single capped page', async () => {
		const items = [{id: 1}, {id: 2}];

		fetch.mockResponseOnce(JSON.stringify({items, totalCount: 300}));

		expect(await getItems(ENDPOINT)).toEqual({items, totalCount: 300});

		expect(fetch).toHaveBeenCalledTimes(1);

		const searchParams = new URL(fetch.mock.calls[0][0]).searchParams;

		expect(searchParams.get('keywords')).toBe('');
		expect(searchParams.get('page')).toBe('1');
		expect(searchParams.get('pageSize')).toBe('250');
	});

	it('sends the encoded keywords it is given', async () => {
		fetch.mockResponseOnce(JSON.stringify({items: [], totalCount: 0}));

		await getItems(ENDPOINT, 'a b&c');

		const url = new URL(fetch.mock.calls[0][0]);

		expect(url.searchParams.get('keywords')).toBe('a b&c');
	});
});
