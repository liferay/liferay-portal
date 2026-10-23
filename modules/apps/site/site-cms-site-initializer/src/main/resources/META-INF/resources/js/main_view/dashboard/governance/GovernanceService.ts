/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from '../../../common/services/ApiHelper';
import {
	EXPIRING_SOON_THRESHOLD_DAYS,
	UPCOMING_REVIEW_THRESHOLD_DAYS,
	WORKFLOW_STATUS,
} from '../../../common/utils/constants';

export type ContentAgeDateField = 'createDate' | 'modified';

type ContentAgeRange = {
	fromMonths: number;
	toMonths?: number;
};

export type AssetStatistics = {
	approvedCount: number;
	brokenLinksCount: number;
	expiredCount: number;
	expiringSoonCount: number;
	inDraftCount: number;
	longStandingDraftsCount: number;
	missingRequiredFieldsCount: number;
	overdueWorkflowTasksCount: number;
	pendingCount: number;
	reviewDateOverdueCount: number;
	scheduledCount: number;
	slaBreachedWorkflowTasksCount: number;
	totalCount: number;
	upcomingReviewCount: number;
	workflowTasksCount: number;
};

export type Contributor = {
	frequency: number;
	name: string | null;
};

export type Contributors = {
	contributors: Contributor[];
	totalCount: number;
};

export type FacetBucket = {
	displayName: string;
	frequency: number;
	term: string;
};

export type FreshnessListType =
	| 'expiredAssets'
	| 'expiringSoon'
	| 'overdueReviews'
	| 'upcomingReviews';

const CONTENT_AGE_AGGREGATION_NAME = 'contentAge';

const CONTENT_AGE_DATE_FORMAT = 'yyyyMMddHHmmss';

const CONTENT_AGE_RANGES: ContentAgeRange[] = [
	{fromMonths: 0, toMonths: 3},
	{fromMonths: 3, toMonths: 6},
	{fromMonths: 6, toMonths: 12},
	{fromMonths: 12, toMonths: 24},
	{fromMonths: 24},
];

const CONTRIBUTORS_AGGREGATION_NAME = 'contributors';

const LONG_STANDING_DRAFT_DAYS = 30;

export const GOVERNANCE_ASSET_LIST_PAGE_SIZE = 8;

const MAX_CONTRIBUTORS = 7;

export const MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

const NESTED_FIELDS = 'embedded,systemProperties.objectDefinitionBrief';

const SEARCH_URL = '/o/search/v1.0/search';

const FRESHNESS_LIST_QUERIES: Record<
	FreshnessListType,
	() => {filter: string; sort: string}
> = {
	expiredAssets: () => ({
		filter: `status eq ${WORKFLOW_STATUS.EXPIRED}`,
		sort: 'dateExpiration:asc',
	}),
	expiringSoon: () => ({
		filter: `status eq ${WORKFLOW_STATUS.APPROVED} and dateExpiration le ${getDateInDays(EXPIRING_SOON_THRESHOLD_DAYS).toISOString()}`,
		sort: 'dateExpiration:asc',
	}),
	overdueReviews: () => ({
		filter: `dateReview lt ${getDateInDays(0).toISOString()}`,
		sort: 'dateReview:asc',
	}),
	upcomingReviews: () => ({
		filter: `dateReview gt ${getDateInDays(0).toISOString()} and dateReview le ${getDateInDays(UPCOMING_REVIEW_THRESHOLD_DAYS).toISOString()}`,
		sort: 'dateReview:asc',
	}),
};

async function getAssetStatistics(
	assetLibraryId?: string,
	signal?: AbortSignal
) {
	const scope = assetLibraryId ? `assetLibraryId=${assetLibraryId}&` : '';

	return ApiHelper.get<AssetStatistics>(
		`/o/headless-cms/v1.0/asset-statistics?${scope}`,
		signal
	);
}

async function getContributors(
	filter: string,
	groupIds: number[]
): Promise<RequestResult<Contributors>> {
	const searchParams = new URLSearchParams({
		filter: getScopedFilter(filter, groupIds),
		pageSize: '1',
	});

	const {data, error} = await ApiHelper.post<{
		searchFacets?: Record<string, FacetBucket[]>;
		totalCount: number;
	}>(`${SEARCH_URL}?${searchParams}`, {
		attributes: {'search.empty.search': true},
		facetConfigurations: [
			{
				aggregationName: CONTRIBUTORS_AGGREGATION_NAME,
				frequencyThreshold: 1,
				maxTerms: MAX_CONTRIBUTORS,
				name: 'user',
			},
		],
	});

	if (!data) {
		return {data: null, error};
	}

	const buckets = data.searchFacets?.[CONTRIBUTORS_AGGREGATION_NAME] ?? [];

	const userAccounts = await Promise.all(
		buckets.map(({term}) =>
			ApiHelper.get<{name: string}>(
				`/o/headless-admin-user/v1.0/user-accounts/${term}`
			)
		)
	);

	const failedUserAccount = userAccounts.find(
		({error, status}) => error && status !== 'NOT_FOUND'
	);

	if (failedUserAccount?.error) {
		return {data: null, error: failedUserAccount.error};
	}

	return {
		data: {
			contributors: buckets.map(({frequency}, index) => ({
				frequency,
				name: userAccounts[index].data?.name ?? null,
			})),
			totalCount: data.totalCount,
		},
		error: null,
	};
}

function formatSearchDate(date: Date) {
	return date
		.toISOString()
		.replace(/\D/g, '')
		.slice(0, CONTENT_AGE_DATE_FORMAT.length);
}

function getDateMonthsAgo(now: Date, months: number) {
	const date = new Date(now);

	date.setUTCMonth(date.getUTCMonth() - months);

	return date;
}

function getContentAgeSearchRanges(now: Date) {
	return CONTENT_AGE_RANGES.map(({fromMonths, toMonths}) => {
		const from = toMonths ? getDateMonthsAgo(now, toMonths) : new Date(0);

		return `[${formatSearchDate(from)} TO ${formatSearchDate(getDateMonthsAgo(now, fromMonths))}]`;
	});
}

async function getContentAgeDistribution(
	filter: string,
	dateField: ContentAgeDateField,
	groupIds: number[]
): Promise<RequestResult<number[]>> {
	const searchParams = new URLSearchParams({
		filter: getScopedFilter(
			`${filter} and status eq ${WORKFLOW_STATUS.APPROVED}`,
			groupIds
		),
		pageSize: '1',
	});

	const ranges = getContentAgeSearchRanges(new Date(Date.now()));

	const {data, error} = await ApiHelper.post<{
		searchFacets?: Record<string, FacetBucket[]>;
	}>(`${SEARCH_URL}?${searchParams}`, {
		attributes: {'search.empty.search': true},
		facetConfigurations: [
			{
				aggregationName: CONTENT_AGE_AGGREGATION_NAME,
				attributes: {
					field: dateField,
					format: CONTENT_AGE_DATE_FORMAT,
					ranges: ranges.map((range) => ({label: range, range})),
				},
				frequencyThreshold: 0,
				name: 'date-range',
			},
		],
	});

	if (!data) {
		return {data: null, error};
	}

	const buckets = data.searchFacets?.[CONTENT_AGE_AGGREGATION_NAME] ?? [];

	return {
		data: ranges.map(
			(range) => buckets.find(({term}) => term === range)?.frequency ?? 0
		),
		error: null,
	};
}

function getContentProgress(
	filter: string,
	groupIds: number[],
	createdInLastDays?: number
) {
	let scopedFilter = getScopedFilter(filter, groupIds);

	if (createdInLastDays) {
		const createdSince = new Date(
			Date.now() - createdInLastDays * MILLISECONDS_PER_DAY
		);

		scopedFilter = `${scopedFilter} and dateCreated ge ${createdSince.toISOString()}`;
	}

	const searchParams = new URLSearchParams({filter: scopedFilter});

	// Facet configurations only work in the POST body, where the empty-search
	// switch must travel as an attribute (the query parameter is ignored).

	return ApiHelper.post<{
		searchFacets?: {statusFacet?: FacetBucket[]};
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

function getScopedFilter(filter: string, groupIds: number[]) {
	if (!groupIds.length) {
		return filter;
	}

	const groupFilter = groupIds
		.map((groupId) => `groupIds/any(g:g eq ${groupId})`)
		.join(' or ');

	if (groupIds.length === 1) {
		return `${filter} and ${groupFilter}`;
	}

	return `${filter} and (${groupFilter})`;
}

function getLongStandingDraftsThresholdDate() {
	return new Date(
		Date.now() - LONG_STANDING_DRAFT_DAYS * MILLISECONDS_PER_DAY
	);
}

function getAssetListURL(filter: string, sort: string, groupIds: number[]) {
	const searchParams = new URLSearchParams({
		emptySearch: 'true',
		filter: getScopedFilter(filter, groupIds),
		nestedFields: NESTED_FIELDS,
		pageSize: String(GOVERNANCE_ASSET_LIST_PAGE_SIZE),
		sort,
	});

	return `${SEARCH_URL}?${searchParams}`;
}

function getAssetsByIdsURL(ids: number[]) {
	const searchParams = new URLSearchParams({
		emptySearch: 'true',
		filter: ids.map((id) => `entryClassPK eq ${id}`).join(' or '),
		nestedFields: NESTED_FIELDS,
		pageSize: String(ids.length),
	});

	return `${SEARCH_URL}?${searchParams}`;
}

function getQualityAssetsURL(path: string, assetLibraryId?: string) {
	const searchParams = new URLSearchParams({
		pageSize: String(GOVERNANCE_ASSET_LIST_PAGE_SIZE),
		sort: 'title:asc',
	});

	if (assetLibraryId) {
		searchParams.set('assetLibraryId', assetLibraryId);
	}

	return `/o/headless-cms/v1.0/${path}?${searchParams}`;
}

function getBrokenLinkAssetsURL(assetLibraryId?: string) {
	return getQualityAssetsURL('broken-link-assets', assetLibraryId);
}

function getMissingRequiredFieldsAssetsURL(assetLibraryId?: string) {
	return getQualityAssetsURL(
		'missing-required-fields-assets',
		assetLibraryId
	);
}

function getDateInDays(days: number) {
	return new Date(Date.now() + days * MILLISECONDS_PER_DAY);
}

function getFreshnessListURL(
	type: FreshnessListType,
	filter: string,
	groupIds: number[]
) {
	const {filter: typeFilter, sort} = FRESHNESS_LIST_QUERIES[type]();

	return getAssetListURL(`${filter} and ${typeFilter}`, sort, groupIds);
}

function getLongStandingDraftsURL(filter: string, groupIds: number[]) {
	return getAssetListURL(
		`${filter} and status eq ${WORKFLOW_STATUS.DRAFT} and dateModified lt ${getLongStandingDraftsThresholdDate().toISOString()}`,
		'dateModified:asc',
		groupIds
	);
}

export default {
	getAssetStatistics,
	getAssetsByIdsURL,
	getBrokenLinkAssetsURL,
	getContentAgeDistribution,
	getContentProgress,
	getContributors,
	getFreshnessListURL,
	getLongStandingDraftsThresholdDate,
	getLongStandingDraftsURL,
	getMissingRequiredFieldsAssetsURL,
};
