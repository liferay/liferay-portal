/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../../common/services/ApiHelper';

export type AssetStatistics = {
	approvedCount: number;
	brokenLinksCount: number;
	duplicatedCount?: number;
	expiredCount: number;
	expiringSoonCount: number;
	inDraftCount: number;
	pendingCount: number;
	reviewDateOverdueCount: number;
	scheduledCount: number;
	totalCount: number;
	upcomingReviewCount: number;
};

const NEEDS_REVIEW_PAGE_SIZE = 8;

const NESTED_FIELDS = 'embedded,systemProperties.objectDefinitionBrief';

const SEARCH_URL = '/o/search/v1.0/search';

async function getAssetStatistics(
	assetLibraryId?: string,
	signal?: AbortSignal
) {
	const scope = assetLibraryId ? `assetLibraryId=${assetLibraryId}&` : '';

	const [statistics, similarityClusters] = await Promise.all([
		ApiHelper.get<AssetStatistics>(
			`/o/headless-cms/v1.0/asset-statistics?${scope}`,
			signal
		),
		ApiHelper.get<{totalCount: number}>(
			`/o/headless-cms/v1.0/similarity-clusters?${scope}pageSize=1`,
			signal
		),
	]);

	if (!statistics.data) {
		return statistics;
	}

	return {
		...statistics,
		data: {
			...statistics.data,
			duplicatedCount: similarityClusters.data?.totalCount,
		},
	};
}

function getScopedFilter(filter: string, groupId?: number) {
	if (!Number(groupId)) {
		return filter;
	}

	return `${filter} and groupIds/any(g:g eq ${Number(groupId)})`;
}

function getSearchURL(filter: string, sort: string, groupId?: number) {
	const searchParams = new URLSearchParams({
		emptySearch: 'true',
		filter: getScopedFilter(filter, groupId),
		nestedFields: NESTED_FIELDS,
		pageSize: String(NEEDS_REVIEW_PAGE_SIZE),
		sort,
	});

	return `${SEARCH_URL}?${searchParams}`;
}

export default {getAssetStatistics, getSearchURL};
