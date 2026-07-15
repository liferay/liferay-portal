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

	it('scopes the request by assetLibraryId when a space is given', () => {
		const getSpy = jest
			.spyOn(ApiHelper, 'get')
			.mockResolvedValue({data: {}, error: null} as any);

		GovernanceService.getAssetStatistics('123');

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-cms/v1.0/asset-statistics?assetLibraryId=123'
		);
	});

	it('omits assetLibraryId when no space is given', () => {
		const getSpy = jest
			.spyOn(ApiHelper, 'get')
			.mockResolvedValue({data: {}, error: null} as any);

		GovernanceService.getAssetStatistics();

		expect(getSpy).toHaveBeenCalledWith(
			'/o/headless-cms/v1.0/asset-statistics'
		);
	});
});
