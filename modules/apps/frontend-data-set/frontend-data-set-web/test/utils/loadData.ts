/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import {loadData} from '../../src/main/resources/META-INF/resources/utils/loadData';

const responseData = {
	account: 25,
	order: 0,
	people: 44,
	product: 34,
};

beforeEach(() => {
	fetch.resetMocks();
	fetch.mockResponseOnce(JSON.stringify(responseData));
});

describe('loadData util', () => {
	it('is defined', () => {
		expect(loadData).toBeDefined();
	});

	it('retrieves data for a Frontend Data Set using a basic http GET request', async () => {
		const requestResult = await loadData({
			apiURL: '/o/products',
			delta: 20,
			page: 1,
		});

		expect(fetch).toHaveBeenCalledTimes(1);
		expect(fetch.mock.calls[0][1]?.method).toEqual('GET');
		expect(requestResult).toEqual({
			data: responseData,
			ok: true,
			status: 200,
		});
	});

	it('requests data for a Frontend Data Set with search parameter', async () => {
		await loadData({
			apiURL: '/o/products',
			currentURL: '/sample-portlet-page-url',
			delta: 20,
			page: 1,
			searchParam: 'foo=bar',
		});

		const requestUrl = fetch.mock.calls[0][0];

		expect(requestUrl).toContain(
			'/o/products?currentURL=%2Fsample-portlet-page-url&page=1&pageSize=20&search=foo%3Dbar'
		);
	});

	it('requests data for a Frontend Data Set with a oData filters', async () => {
		await loadData({
			apiURL: '/o/products',
			currentURL: '/sample-portlet-page-url',
			delta: 20,
			odataFiltersStrings: ['catalogId eq 32642'],
			page: 1,
		});

		const requestUrl = fetch.mock.calls[0][0];

		expect(requestUrl).toContain(
			'/o/products?currentURL=%2Fsample-portlet-page-url&filter=%28catalogId+eq+32642%29&page=1&pageSize=20'
		);
	});

	it('requests data for a Frontend Data Set with a sort parameter', async () => {
		await loadData({
			apiURL: '/o/products',
			currentURL: '/sample-portlet-page-url',
			delta: 20,
			page: 1,
			sorts: [
				{
					active: true,
					direction: 'desc',
					key: 'modifiedDate',
				},
			],
		});

		const requestUrl = fetch.mock.calls[0][0];

		expect(requestUrl).toContain(
			'/o/products?currentURL=%2Fsample-portlet-page-url&page=1&pageSize=20&sort=modifiedDate%3Adesc'
		);
	});

	it('requests a customField data for a Frontend Data Set with a sort parameter', async () => {
		await loadData({
			apiURL: '/o/products',
			currentURL: '/sample-portlet-page-url',
			delta: 20,
			page: 1,
			sorts: [
				{
					active: true,
					direction: 'asc',
					key: 'customFields.customDate',
				},
			],
		});

		const requestUrl = fetch.mock.calls[0][0];

		expect(requestUrl).toContain(
			'/o/products?currentURL=%2Fsample-portlet-page-url&page=1&pageSize=20&sort=customFields%2FcustomDate%3Aasc'
		);
	});

	it('handles "LANG" property when it comes as a sort parameter', async () => {
		await loadData({
			apiURL: '/o/products',
			delta: 20,
			page: 1,
			sorts: [
				{
					active: true,
					direction: 'asc',
					key: 'name,LANG',
				},
			],
		});

		const requestUrl = fetch.mock.calls[0][0];

		expect(requestUrl).toContain(
			'/o/products?page=1&pageSize=20&sort=name%3Aasc'
		);
	});

	it('handles simple picklist property (complex field name) when it comes as a sort parameter', async () => {
		await loadData({
			apiURL: '/o/products',
			delta: 20,
			page: 1,
			sorts: [
				{
					active: true,
					direction: 'desc',
					key: 'gender,name',
				},
			],
		});

		const requestUrl = fetch.mock.calls[0][0];

		expect(requestUrl).toContain(
			'/o/products?page=1&pageSize=20&sort=gender%3Adesc'
		);
	});

	it('requests data for a Frontend Data Set with a additional URL parameters', async () => {
		await loadData({
			additionalAPIURLParameters: 'nestedFields=skus',
			apiURL: '/o/products',
			delta: 20,
			page: 1,
		});

		const requestUrl = fetch.mock.calls[0][0];

		expect(requestUrl).toContain(
			'/o/products?page=1&pageSize=20&nestedFields=skus'
		);
	});

	it('rejects an in-flight request when the caller aborts it', async () => {

		// Unlike the browser, jest-fetch-mock ignores the signal, so the
		// mock stays pending until the abort rejects it

		fetch.resetMocks();
		fetch.mockResponse(
			(request) =>
				new Promise<string>((_resolve, reject) =>
					request.signal?.addEventListener('abort', () =>
						reject(
							new DOMException(
								'The user aborted a request.',
								'AbortError'
							)
						)
					)
				)
		);

		const abortController = new AbortController();

		const requestPromise = loadData({
			apiURL: '/o/products',
			delta: 20,
			page: 1,
			signal: abortController.signal,
		});

		abortController.abort();

		await expect(requestPromise).rejects.toMatchObject({
			name: 'AbortError',
		});
	});
});
