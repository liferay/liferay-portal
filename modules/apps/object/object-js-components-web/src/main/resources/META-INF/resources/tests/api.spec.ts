/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {deleteItem, fetchJSON, getList, save} from '../utils/api';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	fetch: jest.fn(() =>
		Promise.resolve({
			json: () => Promise.resolve({items: []}),
			ok: true,
			status: 200,
		})
	),
}));

function mockResponseOnce({
	body,
	ok,
	status,
}: {
	body: string;
	ok: boolean;
	status: number;
}) {
	(fetch as jest.Mock).mockResolvedValueOnce({
		json: () => Promise.resolve(JSON.parse(body)),
		ok,
		status,
	});
}

describe('deleteItem', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	test('rejects with a generic message when the error body is not JSON', async () => {
		mockResponseOnce({
			body: '<html>Bad Request</html>',
			ok: false,
			status: 400,
		});

		await expect(
			deleteItem('/o/object-admin/v1.0/object-fields/1')
		).rejects.toThrow('an-error-occurred');
	});

	test('rejects with the API title when the error body is JSON', async () => {
		mockResponseOnce({
			body: JSON.stringify({title: 'Field in use'}),
			ok: false,
			status: 400,
		});

		await expect(
			deleteItem('/o/object-admin/v1.0/object-fields/1')
		).rejects.toThrow('Field in use');
	});
});

describe('fetchJSON', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	test('returns the parsed body when the response is ok', async () => {
		mockResponseOnce({
			body: JSON.stringify({id: 42}),
			ok: true,
			status: 200,
		});

		await expect(
			fetchJSON('/o/object-admin/v1.0/object-fields/42')
		).resolves.toEqual({id: 42});
	});
});

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

describe('save', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	test('rejects with a generic message when the error body is not JSON', async () => {
		mockResponseOnce({
			body: '<html>Bad Request</html>',
			ok: false,
			status: 400,
		});

		await expect(
			save({item: {}, url: '/o/object-admin/v1.0/object-definitions/1'})
		).rejects.toMatchObject({message: 'an-error-occurred'});
	});

	test('rejects with the API error details when the error body is JSON', async () => {
		mockResponseOnce({
			body: JSON.stringify({
				detail: '[{"errorMessage": "Duplicate value"}]',
				title: 'The Email field value must be unique',
			}),
			ok: false,
			status: 400,
		});

		await expect(
			save({item: {}, url: '/o/object-admin/v1.0/object-definitions/1'})
		).rejects.toMatchObject({
			detail: '[{"errorMessage": "Duplicate value"}]',
			message: 'The Email field value must be unique',
		});
	});
});
