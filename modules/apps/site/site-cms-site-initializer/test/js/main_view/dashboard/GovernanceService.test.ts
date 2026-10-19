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

describe('GovernanceService.getContentProgress', () => {
	afterEach(() => {
		jest.restoreAllMocks();
		jest.useRealTimers();
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

	it('limits the content to the one created in the last days', () => {
		jest.useFakeTimers().setSystemTime(
			new Date('2026-10-07T12:00:00.000Z')
		);

		const postSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {}, error: null} as any);

		GovernanceService.getContentProgress('someFilter', 123, 7);

		const [url] = postSpy.mock.calls[0];

		expect(
			new URL(url, 'http://localhost').searchParams.get('filter')
		).toBe(
			'someFilter and groupIds/any(g:g eq 123) and dateCreated ge 2026-09-30T12:00:00.000Z'
		);
	});
});

describe('GovernanceService.getContributors', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	function mockContributorsSearch(buckets: object[], totalCount: number) {
		return jest.spyOn(ApiHelper, 'post').mockResolvedValue({
			data: {searchFacets: {contributors: buckets}, totalCount},
			error: null,
		} as any);
	}

	it('requests the top seven contributors scoped to the given space', async () => {
		const postSpy = mockContributorsSearch([], 0);

		await GovernanceService.getContributors('someFilter', 123);

		expect(postSpy).toHaveBeenCalledWith(
			'/o/search/v1.0/search?filter=someFilter+and+groupIds%2Fany%28g%3Ag+eq+123%29&pageSize=1',
			{
				attributes: {'search.empty.search': true},
				facetConfigurations: [
					{
						aggregationName: 'contributors',
						frequencyThreshold: 1,
						maxTerms: 7,
						name: 'user',
					},
				],
			}
		);
	});

	it('resolves each contributor name and marks deleted users', async () => {
		mockContributorsSearch(
			[
				{frequency: 5, term: '1'},
				{frequency: 3, term: '2'},
			],
			10
		);

		const getSpy = jest
			.spyOn(ApiHelper, 'get')
			.mockImplementation(async (url) =>
				url.endsWith('/1')
					? ({data: {name: 'Daniel Reyes'}, error: null} as any)
					: ({
							data: null,
							error: 'an-unexpected-error-occurred',
							status: 'NOT_FOUND',
						} as any)
			);

		const {data, error} =
			await GovernanceService.getContributors('someFilter');

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-admin-user/v1.0/user-accounts/1'
		);
		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-admin-user/v1.0/user-accounts/2'
		);
		expect(error).toBeNull();
		expect(data).toEqual({
			contributors: [
				{frequency: 5, name: 'Daniel Reyes'},
				{frequency: 3, name: null},
			],
			totalCount: 10,
		});
	});

	it('returns the error when a contributor cannot be resolved', async () => {
		mockContributorsSearch([{frequency: 5, term: '1'}], 5);

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: null,
			error: 'Forbidden',
			status: 'FORBIDDEN',
		} as any);

		const {data, error} =
			await GovernanceService.getContributors('someFilter');

		expect(data).toBeNull();
		expect(error).toBe('Forbidden');
	});
});

describe('GovernanceService.getContentAgeDistribution', () => {
	afterEach(() => {
		jest.restoreAllMocks();
		jest.useRealTimers();
	});

	beforeEach(() => {
		jest.useFakeTimers().setSystemTime(
			new Date('2026-10-13T10:30:45.000Z')
		);
	});

	it('requests five contiguous age ranges of the approved content on the given date field', async () => {
		const postSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {}, error: null} as any);

		await GovernanceService.getContentAgeDistribution(
			'someFilter',
			'createDate'
		);

		expect(postSpy).toHaveBeenCalledWith(
			'/o/search/v1.0/search?filter=someFilter+and+status+eq+0&pageSize=1',
			{
				attributes: {'search.empty.search': true},
				facetConfigurations: [
					{
						aggregationName: 'contentAge',
						attributes: {
							field: 'createDate',
							format: 'yyyyMMddHHmmss',
							ranges: [
								'[20260713103045 TO 20261013103045]',
								'[20260413103045 TO 20260713103045]',
								'[20251013103045 TO 20260413103045]',
								'[20241013103045 TO 20251013103045]',
								'[19700101000000 TO 20241013103045]',
							].map((range) => ({label: range, range})),
						},
						frequencyThreshold: 0,
						name: 'date-range',
					},
				],
			}
		);
	});

	it('scopes the filter by groupId when a space is given', async () => {
		const postSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {}, error: null} as any);

		await GovernanceService.getContentAgeDistribution(
			'someFilter',
			'modified',
			123
		);

		const [url, body] = postSpy.mock.calls[0];

		expect(url).toBe(
			'/o/search/v1.0/search?filter=someFilter+and+status+eq+0+and+groupIds%2Fany%28g%3Ag+eq+123%29&pageSize=1'
		);
		expect(body).toMatchObject({
			facetConfigurations: [{attributes: {field: 'modified'}}],
		});
	});

	it('maps the buckets to the requested ranges, filling the missing ones with zero', async () => {
		jest.spyOn(ApiHelper, 'post').mockResolvedValue({
			data: {
				searchFacets: {
					contentAge: [
						{
							displayName: '',
							frequency: 7,
							term: '[20241013103045 TO 20251013103045]',
						},
						{
							displayName: '',
							frequency: 3,
							term: '[20260713103045 TO 20261013103045]',
						},
					],
				},
			},
			error: null,
		} as any);

		const {data, error} = await GovernanceService.getContentAgeDistribution(
			'someFilter',
			'createDate'
		);

		expect(data).toEqual([3, 0, 0, 7, 0]);
		expect(error).toBeNull();
	});

	it('returns the error when the request fails', async () => {
		jest.spyOn(ApiHelper, 'post').mockResolvedValue({
			data: null,
			error: 'Something went wrong',
		} as any);

		const {data, error} = await GovernanceService.getContentAgeDistribution(
			'someFilter',
			'createDate'
		);

		expect(data).toBeNull();
		expect(error).toBe('Something went wrong');
	});
});

describe('GovernanceService.getAssetsByIdsURL', () => {
	it('requests the given assets with the nested fields of the dashboard lists', () => {
		expect(GovernanceService.getAssetsByIdsURL([1, 2])).toBe(
			'/o/search/v1.0/search?emptySearch=true&filter=entryClassPK+eq+1+or+entryClassPK+eq+2&nestedFields=embedded%2CsystemProperties.objectDefinitionBrief&pageSize=2'
		);
	});
});

describe('GovernanceService.getBrokenLinkAssetsURL', () => {
	it('requests the first page of broken link assets sorted by title', () => {
		expect(GovernanceService.getBrokenLinkAssetsURL()).toBe(
			'/o/headless-cms/v1.0/broken-link-assets?pageSize=8&sort=title%3Aasc'
		);
	});

	it('scopes the request by assetLibraryId when a space is given', () => {
		expect(GovernanceService.getBrokenLinkAssetsURL('123')).toBe(
			'/o/headless-cms/v1.0/broken-link-assets?pageSize=8&sort=title%3Aasc&assetLibraryId=123'
		);
	});
});

describe('GovernanceService.getMissingRequiredFieldsAssetsURL', () => {
	it('requests the first page of missing required fields assets sorted by title', () => {
		expect(GovernanceService.getMissingRequiredFieldsAssetsURL()).toBe(
			'/o/headless-cms/v1.0/missing-required-fields-assets?pageSize=8&sort=title%3Aasc'
		);
	});

	it('scopes the request by assetLibraryId when a space is given', () => {
		expect(GovernanceService.getMissingRequiredFieldsAssetsURL('123')).toBe(
			'/o/headless-cms/v1.0/missing-required-fields-assets?pageSize=8&sort=title%3Aasc&assetLibraryId=123'
		);
	});
});
