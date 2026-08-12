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

export type DuplicateTitle = {
	frequency: number;
	term: string;
};

export type DuplicateTopicAsset = {
	dateModified: string;
	embedded?: {id: number};
	entryClassName: string;
	title: string;
};

export type StatusFacetBucket = {
	displayName: string;
	frequency: number;
	term: string;
};

const DUPLICATE_TITLES_AGGREGATION_NAME = 'duplicateTitles';

const MAX_FACET_TERMS = 10000;

const MINIMUM_DUPLICATE_FREQUENCY = 2;

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

function getContentProgress(filter: string, groupId?: number) {
	const searchParams = new URLSearchParams({
		filter: getScopedFilter(filter, groupId),
	});

	// Facet configurations only work in the POST body, where the empty-search
	// switch must travel as an attribute (the query parameter is ignored).

	return ApiHelper.post<{
		searchFacets?: {statusFacet?: StatusFacetBucket[]};
	}>(`${SEARCH_URL}?${searchParams}`, {
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
	});
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

async function getCMSEntryClassNames(
	ercContentStructures: string,
	ercFileTypes: string,
	signal?: AbortSignal
) {
	const filter = encodeURIComponent(
		`objectFolderExternalReferenceCode eq '${ercContentStructures}' or objectFolderExternalReferenceCode eq '${ercFileTypes}'`
	);

	const {data} = await ApiHelper.get<{items: {className: string}[]}>(
		`/o/object-admin/v1.0/object-definitions?filter=${filter}&pageSize=-1`,
		signal
	);

	return (data?.items ?? []).map(({className}) => className).join(',');
}

async function getDuplicateTitles({
	entryClassNames,
	signal,
	siteId,
}: {
	entryClassNames: string;
	signal?: AbortSignal;
	siteId?: number;
}): Promise<DuplicateTitle[] | undefined> {
	if (!entryClassNames) {
		return undefined;
	}

	const searchParams = new URLSearchParams({
		entryClassNames,
		pageSize: '1',
	});

	if (siteId) {
		searchParams.set('filter', `groupIds/any(g:g eq ${siteId})`);
	}

	const {data} = await ApiHelper.post<{
		searchFacets?: Record<string, DuplicateTitle[]>;
	}>(
		`/o/search/v1.0/search?${searchParams}`,
		{
			attributes: {'search.empty.search': true},
			facetConfigurations: [
				{
					aggregationName: DUPLICATE_TITLES_AGGREGATION_NAME,
					attributes: {
						field: `localized_title_${Liferay.ThemeDisplay.getLanguageId()}_sortable.keyword_lowercase`,
					},
					frequencyThreshold: MINIMUM_DUPLICATE_FREQUENCY,
					maxTerms: MAX_FACET_TERMS,
					name: 'custom',
				},
			],
		},
		signal
	);

	if (!data) {
		return undefined;
	}

	return data.searchFacets?.[DUPLICATE_TITLES_AGGREGATION_NAME] ?? [];
}

async function getDuplicateTopicsCount({
	entryClassNames,
	signal,
	siteId,
}: {
	entryClassNames: string;
	signal?: AbortSignal;
	siteId?: number;
}) {
	const titles = await getDuplicateTitles({entryClassNames, signal, siteId});

	if (!titles) {
		return undefined;
	}

	return titles.reduce((count, {frequency}) => count + frequency, 0);
}

export default {
	getAssetStatistics,
	getCMSEntryClassNames,
	getContentProgress,
	getDuplicateTitles,
	getDuplicateTopicsCount,
	getSearchURL,
};
