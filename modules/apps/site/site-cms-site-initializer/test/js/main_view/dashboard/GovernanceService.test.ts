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
