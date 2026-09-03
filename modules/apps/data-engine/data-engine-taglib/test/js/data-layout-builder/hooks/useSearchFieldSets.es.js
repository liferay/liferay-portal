/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetchFieldSets} from '../../../../src/main/resources/META-INF/resources/js/hooks/useSearchFieldSets.es';

const COMPANY_GROUP_ID = '99';

const getPageJSON = (id, totalCount = 1) =>
	JSON.stringify({items: [{id}], totalCount});

describe('fetchFieldSets', () => {
	let originalGetCompanyGroupId;

	beforeEach(() => {
		originalGetCompanyGroupId = themeDisplay.getCompanyGroupId;

		themeDisplay.getCompanyGroupId = () => COMPANY_GROUP_ID;
	});

	afterEach(() => {
		themeDisplay.getCompanyGroupId = originalGetCompanyGroupId;
	});

	it('fetches a single capped page per scope when browsing', async () => {
		fetch.mockResponse(async (request) =>
			getPageJSON(request.url.includes('/sites/') ? 1 : 2)
		);

		await fetchFieldSets({
			contentType: 'journal',
			dataDefinitionId: '1',
			groupId: '20',
		});

		expect(fetch).toHaveBeenCalledTimes(2);

		fetch.mock.calls.forEach(([url]) => {
			const searchParams = new URL(url).searchParams;

			expect(searchParams.get('keywords')).toBe('');
			expect(searchParams.get('page')).toBe('1');
			expect(searchParams.get('pageSize')).toBe('250');
		});
	});

	it('forwards the keywords to both scopes when searching', async () => {
		fetch.mockResponse(async (request) =>
			getPageJSON(request.url.includes('/sites/') ? 1 : 2)
		);

		await fetchFieldSets({
			contentType: 'journal',
			dataDefinitionId: '1',
			groupId: '20',
			keywords: 'zebra',
		});

		expect(fetch).toHaveBeenCalledTimes(2);

		fetch.mock.calls.forEach(([url]) => {
			expect(new URL(url).searchParams.get('keywords')).toBe('zebra');
		});
	});

	it('reports whether the results were truncated', async () => {
		fetch.mockResponse(async (request) =>
			getPageJSON(request.url.includes('/sites/') ? 11 : 12, 300)
		);

		const {fieldSets, truncated} = await fetchFieldSets({
			contentType: 'journal',
			dataDefinitionId: '1',
			groupId: '20',
			keywords: 'zebra',
		});

		expect(fieldSets.length).toBe(2);
		expect(truncated).toBe(true);
	});

	it('resolves to an empty list when no scope applies', async () => {
		themeDisplay.getCompanyGroupId = () => undefined;

		expect(
			await fetchFieldSets({
				contentType: 'journal',
				dataDefinitionId: '1',
				groupId: undefined,
			})
		).toEqual({fieldSets: [], truncated: false});

		expect(fetch).not.toHaveBeenCalled();
	});

	it('skips the company scope when the group is the company group', async () => {
		fetch.mockResponse(async () => getPageJSON(1));

		await fetchFieldSets({
			contentType: 'journal',
			dataDefinitionId: '1',
			groupId: COMPANY_GROUP_ID,
		});

		expect(fetch).toHaveBeenCalledTimes(1);

		expect(fetch.mock.calls[0][0]).toContain(`/sites/${COMPANY_GROUP_ID}/`);
	});
});
