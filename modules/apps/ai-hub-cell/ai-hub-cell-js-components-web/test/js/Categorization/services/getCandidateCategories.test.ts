/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {getCandidateCategories} from '../../../../src/main/resources/META-INF/resources/js/Categorization/services/getCandidateCategories';

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

function page(items: unknown[], lastPage = 1) {
	return {
		json: () => Promise.resolve({items, lastPage}),
		ok: true,
	};
}

describe('getCandidateCategories', () => {
	beforeEach(() => {
		mockFetch.mockReset();

		global.Liferay = {
			...global.Liferay,
			ThemeDisplay: {
				...global.Liferay?.ThemeDisplay,
				getPortalURL: () => 'http://localhost:8080',
			},
		} as never;
	});

	it('queries the asset-libraries endpoint and maps id, name, vocabulary', async () => {
		mockFetch.mockResolvedValue(
			page([
				{
					id: 39001,
					name: 'International',
					parentTaxonomyVocabulary: {name: 'Travel'},
				},
			]) as never
		);

		const result = await getCandidateCategories({
			classNameId: 123,
			cmsGroupId: 20124,
			scopeId: 555,
		});

		expect(result).toEqual([
			{id: 39001, name: 'International', vocabulary: 'Travel'},
		]);

		const calledURL = decodeURIComponent(
			mockFetch.mock.calls[0][0] as string
		);

		expect(calledURL).toContain('/asset-libraries/555/taxonomy-categories');
		expect(calledURL).toContain("assetTypes in ('0', '123')");
	});

	it('uses the sites endpoint with an assetLibraries filter for a negative scope', async () => {
		mockFetch.mockResolvedValue(page([]) as never);

		await getCandidateCategories({cmsGroupId: 20124, scopeId: -1});

		const calledURL = decodeURIComponent(
			mockFetch.mock.calls[0][0] as string
		);

		expect(calledURL).toContain('/sites/20124/taxonomy-categories');
		expect(calledURL).toContain("assetLibraries in ('-1')");
		expect(calledURL).toContain("assetTypes in ('0')");
	});

	it('caps the candidate set at the requested max', async () => {
		const items = Array.from({length: 5}, (_, index) => ({
			id: index + 1,
			name: `c${index}`,
		}));

		mockFetch.mockResolvedValue(page(items) as never);

		const result = await getCandidateCategories({
			cmsGroupId: 20124,
			max: 2,
			scopeId: 555,
		});

		expect(result).toHaveLength(2);
	});
});
