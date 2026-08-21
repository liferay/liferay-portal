/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {getItems} from '../../../src/main/resources/META-INF/resources/js/utils/client.es';

jest.mock('frontend-js-web', () => ({
	fetch: jest.fn(),
}));

function makeResponse(data) {
	return {
		json: () => Promise.resolve(data),
	};
}

describe('getItems', () => {
	beforeEach(() => {
		fetch.mockReset();
	});

	it('returns all items when totalCount fits within a single page', async () => {
		const items = [{id: 1}, {id: 2}];

		fetch.mockResolvedValue(makeResponse({items, totalCount: 2}));

		const result = await getItems('/o/data-engine/v2.0/test-api');

		expect(result).toEqual(items);
		expect(fetch).toHaveBeenCalledTimes(1);
	});

	it('fetches additional pages and concatenates items when totalCount exceeds pageSize', async () => {
		const page1Items = [{id: 1}, {id: 2}];
		const page2Items = [{id: 3}];

		fetch
			.mockResolvedValueOnce(
				makeResponse({items: page1Items, totalCount: 3})
			)
			.mockResolvedValueOnce(
				makeResponse({items: page2Items, totalCount: 3})
			);

		const result = await getItems('/o/data-engine/v2.0/test-api', '', {
			pageSize: 2,
		});

		const [page1Url] = fetch.mock.calls[0];
		const [page2Url] = fetch.mock.calls[1];

		expect(page1Url).toContain('page=1');
		expect(page2Url).toContain('page=2');
		expect(result).toEqual([...page1Items, ...page2Items]);
		expect(fetch).toHaveBeenCalledTimes(2);
	});

	it('defaults keywords to an empty string when the argument is omitted', async () => {
		fetch.mockResolvedValue(makeResponse({items: [], totalCount: 0}));

		await getItems('/o/data-engine/v2.0/test-api');

		const [url] = fetch.mock.calls[0];

		expect(url).toContain('keywords=');
		expect(url).not.toContain('keywords=undefined');
	});

	it('threads the AbortSignal through to every fetch call', async () => {
		const page1Items = [{id: 1}, {id: 2}];
		const page2Items = [{id: 3}];

		fetch
			.mockResolvedValueOnce(
				makeResponse({items: page1Items, totalCount: 3})
			)
			.mockResolvedValueOnce(
				makeResponse({items: page2Items, totalCount: 3})
			);

		const controller = new AbortController();

		await getItems('/o/data-engine/v2.0/test-api', '', {
			pageSize: 2,
			signal: controller.signal,
		});

		expect(fetch).toHaveBeenCalledTimes(2);

		fetch.mock.calls.forEach(([, options]) => {
			expect(options.signal).toBe(controller.signal);
		});
	});
});
