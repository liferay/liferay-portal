/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import GovernanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';

describe('GovernanceService.getAssetStatistics', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('scopes both requests by assetLibraryId when a space is given', () => {
		const getSpy = jest
			.spyOn(ApiHelper, 'get')
			.mockResolvedValue({data: {}, error: null} as any);

		GovernanceService.getAssetStatistics('123');

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-cms/v1.0/asset-statistics?assetLibraryId=123&',
			undefined
		);

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-cms/v1.0/similarity-clusters?assetLibraryId=123&pageSize=1',
			undefined
		);
	});

	it('omits assetLibraryId when no space is given', () => {
		const getSpy = jest
			.spyOn(ApiHelper, 'get')
			.mockResolvedValue({data: {}, error: null} as any);

		GovernanceService.getAssetStatistics();

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-cms/v1.0/asset-statistics?',
			undefined
		);

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-cms/v1.0/similarity-clusters?pageSize=1',
			undefined
		);
	});

	it('passes the abort signal to both requests', () => {
		const getSpy = jest
			.spyOn(ApiHelper, 'get')
			.mockResolvedValue({data: {}, error: null} as any);

		const {signal} = new AbortController();

		GovernanceService.getAssetStatistics(undefined, signal);

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-cms/v1.0/asset-statistics?',
			signal
		);

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-cms/v1.0/similarity-clusters?pageSize=1',
			signal
		);
	});

	it('counts the assets in similarity clusters as the duplicated count', async () => {
		jest.spyOn(ApiHelper, 'get').mockImplementation(((url: string) => {
			if (url.includes('similarity-clusters')) {
				return Promise.resolve({data: {totalCount: 5}, error: null});
			}

			return Promise.resolve({data: {brokenLinksCount: 0}, error: null});
		}) as any);

		const {data} = await GovernanceService.getAssetStatistics();

		expect(data?.duplicatedCount).toBe(5);
	});

	it('leaves the duplicated count unset when the clusters are unavailable', async () => {
		jest.spyOn(ApiHelper, 'get').mockImplementation(((url: string) => {
			if (url.includes('similarity-clusters')) {
				return Promise.resolve({data: null, error: 'Not Found'});
			}

			return Promise.resolve({data: {brokenLinksCount: 0}, error: null});
		}) as any);

		const {data} = await GovernanceService.getAssetStatistics();

		expect(data?.duplicatedCount).toBeUndefined();
	});
});

describe('GovernanceService.getDuplicateTopicsCount', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	const ENTRY_CLASS_NAMES =
		'com.liferay.object.ObjectDefinition#A,com.liferay.object.ObjectDefinition#B';

	function mockAggregation(
		searchFacets: Record<string, {frequency: number}[]> | null
	) {
		return jest.spyOn(ApiHelper, 'post').mockResolvedValue({
			data: searchFacets && {searchFacets},
			error: searchFacets ? null : 'an-unexpected-error-occurred',
		} as any);
	}

	it('counts the assets that share a title, not the groups', async () => {
		mockAggregation({duplicateTitles: [{frequency: 3}, {frequency: 2}]});

		expect(
			await GovernanceService.getDuplicateTopicsCount({
				entryClassNames: ENTRY_CLASS_NAMES,
			})
		).toBe(5);
	});

	it('counts no duplicate title when the aggregation comes back empty', async () => {
		mockAggregation({});

		expect(
			await GovernanceService.getDuplicateTopicsCount({
				entryClassNames: ENTRY_CLASS_NAMES,
			})
		).toBe(0);
	});

	it('reports no count when the request fails', async () => {
		mockAggregation(null);

		expect(
			await GovernanceService.getDuplicateTopicsCount({
				entryClassNames: ENTRY_CLASS_NAMES,
			})
		).toBeUndefined();
	});

	it('aggregates the titles of the CMS content types in the current language', async () => {
		const postSpy = mockAggregation({duplicateTitles: []});

		await GovernanceService.getDuplicateTopicsCount({
			entryClassNames: ENTRY_CLASS_NAMES,
		});

		const [url, body] = postSpy.mock.calls[0];

		expect(url).toContain(
			'entryClassNames=com.liferay.object.ObjectDefinition%23A%2Ccom.liferay.object.ObjectDefinition%23B'
		);

		expect(body).toEqual({
			attributes: {'search.empty.search': true},
			facetConfigurations: [
				expect.objectContaining({
					attributes: {
						field: 'localized_title_en_US_sortable.keyword_lowercase',
					},
					frequencyThreshold: 2,
				}),
			],
		});
	});

	it('scopes the aggregation by the selected space', async () => {
		const postSpy = mockAggregation({duplicateTitles: []});

		await GovernanceService.getDuplicateTopicsCount({
			entryClassNames: ENTRY_CLASS_NAMES,
			siteId: 456,
		});

		expect(postSpy.mock.calls[0][0]).toContain(
			'filter=groupIds%2Fany%28g%3Ag+eq+456%29'
		);
	});
});

describe('GovernanceService.getCMSEntryClassNames', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('asks for the CMS content types of the content structures and file types', async () => {
		const getSpy = jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				items: [
					{className: 'com.liferay.object.ObjectDefinition#A'},
					{className: 'com.liferay.object.ObjectDefinition#B'},
				],
			},
			error: null,
		} as any);

		const entryClassNames = await GovernanceService.getCMSEntryClassNames(
			'ERC_CONTENT_STRUCTURES',
			'ERC_FILE_TYPES'
		);

		expect(getSpy.mock.calls[0][0]).toContain(
			"objectFolderExternalReferenceCode%20eq%20'ERC_CONTENT_STRUCTURES'%20or%20objectFolderExternalReferenceCode%20eq%20'ERC_FILE_TYPES'"
		);

		expect(entryClassNames).toBe(
			'com.liferay.object.ObjectDefinition#A,com.liferay.object.ObjectDefinition#B'
		);
	});
});

describe('GovernanceService.getContentProgress', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('requests the status facet as an empty search', () => {
		const postSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {}, error: null} as any);

		GovernanceService.getContentProgress('someFilter');

		expect(postSpy).toHaveBeenCalledWith(
			'/o/search/v1.0/search?filter=someFilter',
			{
				attributes: {'search.empty.search': true},
				facetConfigurations: [
					{
						aggregationName: 'statusFacet',
						attributes: {field: 'status'},
						frequencyThreshold: 0,
						maxTerms: 50,
						name: 'custom',
					},
				],
			}
		);
	});

	it('scopes the filter by groupId when a space is given', () => {
		const postSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {}, error: null} as any);

		GovernanceService.getContentProgress('someFilter', 123);

		const [url] = postSpy.mock.calls[0];

		expect(url).toBe(
			'/o/search/v1.0/search?filter=someFilter+and+groupIds%2Fany%28g%3Ag+eq+123%29'
		);
	});
});
