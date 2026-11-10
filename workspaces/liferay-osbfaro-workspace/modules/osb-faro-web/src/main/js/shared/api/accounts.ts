import FaroConstants from 'shared/util/constants';
import sendRequest from 'shared/util/request';
import {ACCOUNTS} from 'shared/util/router';
import {buildOrderByFields, buildSortString} from 'shared/util/pagination';
import {ENABLE_LIFECYCLE_STAGES_MOCK_DATA} from 'shared/util/feature-flags';
import {escapeSingleQuotes} from 'segment/segment-editor/dynamic/utils/odata';
import {getMockLifecycleStageFieldValues} from 'lifecycle/utils/lifecycleMock';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';

const {
	pagination: {cur: DEFAULT_PAGE, delta: DEFAULT_DELTA},
} = FaroConstants;

interface IAccountParams {
	accountId?: string;
	channelId?: string;
	groupId?: string;
}

interface IFetchFieldValuesParams {
	channelId?: string;
	delta?: number;
	fieldMappingFieldName?: string;
	groupId?: string;
	page?: number;
	query?: string;
}

interface IFetchLifecycleStageFieldValuesParams {
	accountLifecycleId?: string;
	channelId?: string;
	groupId?: string;
}

interface IFetchLifecycleStatusParams {
	accountId?: string;
	accountLifecycleId: string;
	groupId?: string;
}

interface IFetchMetricsParams {
	campaignId?: string;
	channelId?: string;
	groupId?: string;
}

interface ISearchParams {
	[key: string]: any;
	channelId?: string;
	delta?: number;
	groupId?: string;
	orderIOMap: OrderedMap<string, OrderParams>;
	page?: number;
	query?: string;
}

interface ISearchAccountsParams {
	assetId?: string | null;
	assetTitle?: string;
	assetType?: string | null;
	channelId?: string;
	groupId?: string;
	page?: number;
	pageSize?: number;
	query?: string;
}

interface ISearchByFilterParams {
	channelId?: string;
	filter?: string;
	groupId?: string;
	includeAnonymousUsers?: boolean;
	orderIOMap?: OrderedMap<string, OrderParams>;
	page?: number;
	pageSize?: number;
	query?: string;
}

export function fetch({accountId, channelId, groupId}: IAccountParams) {
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/account/${accountId}`,
	});
}

export function fetchAccountIndividualMetrics({
	accountId,
	channelId,
	groupId,
}: IAccountParams) {
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/account/${accountId}/overview`,
	});
}

export function fetchDetails({accountId, channelId, groupId}: IAccountParams) {
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/account/${accountId}/details`,
	});
}

export function fetchFieldValues({
	channelId,
	delta = DEFAULT_DELTA,
	fieldMappingFieldName,
	groupId,
	page = DEFAULT_PAGE,
	query = '',
}: IFetchFieldValuesParams) {
	return sendRequest({
		data: {
			channelId,
			cur: page,
			delta,
			fieldMappingFieldName,
			query: escapeSingleQuotes(query),
		},
		method: 'GET',
		path: `contacts/${groupId}/account/field_values`,
	});
}

export async function fetchLifecycleStageFieldValues({
	accountLifecycleId,
	channelId,
	groupId,
}: IFetchLifecycleStageFieldValuesParams) {
	if (ENABLE_LIFECYCLE_STAGES_MOCK_DATA) {
		return getMockLifecycleStageFieldValues();
	}

	return sendRequest({
		data: {
			accountLifecycleId,
			channelId,
			fieldMappingFieldName: 'lifecycleStatus',
		},
		method: 'GET',
		path: `contacts/${groupId}/account/fds_field_values`,
	});
}

export async function fetchLifecycleStatus({
	accountId,
	accountLifecycleId,
	groupId,
}: IFetchLifecycleStatusParams) {
	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/account/${accountId}/account-lifecycles/${accountLifecycleId}`,
	});
}

export function fetchMetrics({
	campaignId,
	channelId,
	groupId,
}: IFetchMetricsParams) {
	return sendRequest({
		data: {campaignId, channelId},
		method: 'GET',
		path: `contacts/${groupId}/account/metrics`,
	});
}

export function search({
	channelId = '',
	delta = DEFAULT_DELTA,
	groupId,
	orderIOMap,
	page = DEFAULT_PAGE,
	query = '',
	...otherParams
}: ISearchParams) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams, ACCOUNTS);

	return sendRequest({
		data: {
			channelId,
			cur: page,
			delta,
			orderByFields,
			query,
			...otherParams,
		},
		method: 'POST',
		path: `contacts/${groupId}/account/search`,
	});
}

export function searchAccounts({
	assetId,
	assetTitle,
	assetType,
	channelId = '',
	groupId,
	page = 0,
	pageSize = DEFAULT_DELTA,
	query = '',
}: ISearchAccountsParams) {
	return sendRequest({
		data: {
			assetId,
			assetTitle,
			assetType,
			channelId,
			keywords: query,
			page,
			pageSize,
		},
		method: 'GET',
		path: `contacts/${groupId}/account/account-names`,
	});
}

export function searchByFilter({
	channelId = '',
	filter = '',
	groupId,
	includeAnonymousUsers = false,
	orderIOMap,
	page = 0,
	pageSize = 0,
	query = '',
}: ISearchByFilterParams) {
	return sendRequest({
		data: {
			channelId,
			filter,
			includeAnonymousUsers,
			page,
			pageSize: Math.max(1, pageSize),
			search: query,
			sort: orderIOMap
				? buildSortString(orderIOMap.first(), ACCOUNTS)
				: '',
		},
		method: 'GET',
		path: `contacts/${groupId}/account/search`,
	});
}
