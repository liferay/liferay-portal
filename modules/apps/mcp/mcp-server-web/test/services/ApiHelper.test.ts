/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import ApiHelper from '../../src/main/resources/META-INF/resources/js/services/ApiHelper';

describe('ApiHelper', () => {
	const {location} = window;

	beforeAll(() => {
		delete (window as any).location;

		(window as any).location = {...location, reload: jest.fn()};
	});

	afterAll(() => {
		(window as any).location = location;
	});

	it('reloads the page when the session has expired', async () => {
		fetch.mockResponseOnce(JSON.stringify({title: 'Unauthorized'}), {
			status: 401,
		});

		const result = await ApiHelper.get('/o/items');

		expect(window.location.reload).toHaveBeenCalled();
		expect(result).toMatchObject({data: null, error: 'Unauthorized'});
	});

	it('returns the generic error when the error response has no JSON body', async () => {
		fetch.mockResponseOnce('Internal Server Error', {status: 500});

		const result = await ApiHelper.get('/o/items');

		expect(result).toEqual({
			data: null,
			error: 'an-unexpected-error-occurred',
		});
	});

	it('returns the generic error when the request fails', async () => {
		fetch.mockRejectOnce(new Error('Network failure'));

		const result = await ApiHelper.get('/o/items');

		expect(result).toEqual({
			data: null,
			error: 'an-unexpected-error-occurred',
		});
	});

	it('returns empty data when the response has no content', async () => {
		fetch.mockResponseOnce('', {status: 204});

		const result = await ApiHelper.del('/o/items/1');

		expect(result).toEqual({data: {}, error: null});
	});
});
