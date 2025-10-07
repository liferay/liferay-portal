/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {getList} from '../utils/api';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	fetch: jest.fn(() =>
		Promise.resolve({
			json: () => Promise.resolve({items: []}),
		})
	),
}));

describe('getList', () => {
	const baseURL = 'https://api.example.com/items';

	afterEach(() => {
		jest.clearAllMocks();
	});

	test('call fetch with the base URL when no parameters are provided', async () => {
		await getList(baseURL);

		expect(fetch).toHaveBeenLastCalledWith(baseURL, expect.any(Object));
		expect(fetch).toHaveBeenCalledTimes(1);
	});

	test('appends a single parameter', async () => {
		const parameters = {search: 'test-query'};

		await getList(baseURL, parameters);

		expect(fetch).toHaveBeenLastCalledWith(
			`${baseURL}?search=test-query`,
			expect.any(Object)
		);
	});

	test('appends multiple parameters', async () => {
		const parameters = {
			filter: 'active',
			pageSize: '20',
			sort: 'name',
		};

		await getList(baseURL, parameters);

		expect(fetch).toHaveBeenLastCalledWith(
			`${baseURL}?filter=active&pageSize=20&sort=name`,
			expect.any(Object)
		);
	});

	test('ignores undefined or empty string parameters', async () => {
		const parameters = {
			filter: 'active',
			search: '',
			sort: undefined,
		};

		await getList(baseURL, parameters);

		expect(fetch).toHaveBeenLastCalledWith(
			`${baseURL}?filter=active`,
			expect.any(Object)
		);
	});

	test('URI-encodes special characters in parameters', async () => {
		const parameters = {search: 'query with spaces & symbols'};

		await getList(baseURL, parameters);

		const expectedURL = `${baseURL}?search=query%20with%20spaces%20%26%20symbols`;

		expect(fetch).toHaveBeenLastCalledWith(expectedURL, expect.any(Object));
	});
});
