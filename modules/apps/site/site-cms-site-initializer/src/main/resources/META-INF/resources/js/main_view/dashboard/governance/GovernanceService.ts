/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../../common/services/ApiHelper';

export type AssetStatistics = {
	approvedCount: number;
	expiredCount: number;
	expiringSoonCount: number;
	inDraftCount: number;
	pendingCount: number;
	reviewDateOverdueCount: number;
	scheduledCount: number;
	totalCount: number;
	upcomingReviewCount: number;
};

function getAssetStatistics(assetLibraryId?: string) {
	const query = assetLibraryId ? `?assetLibraryId=${assetLibraryId}` : '';

	return ApiHelper.get<AssetStatistics>(
		`/o/headless-cms/v1.0/asset-statistics${query}`
	);
}

export default {getAssetStatistics};
