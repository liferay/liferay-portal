import FaroConstants from 'shared/util/constants';
import sendRequest from 'shared/util/request';
import {ACCOUNTS} from 'shared/util/router';
import {buildOrderByFields, buildSortString} from 'shared/util/pagination';
import {ENABLE_LIFECYCLE_STAGES_MOCK_DATA} from 'shared/util/feature-flags';
import {escapeSingleQuotes} from 'segment/segment-editor/dynamic/utils/odata';
import {getMockLifecycleStageFieldValues} from 'lifecycle/utils/lifecycleMock';

const {
	pagination: {cur: DEFAULT_PAGE, delta: DEFAULT_DELTA},
} = FaroConstants;

export function fetch({accountId, channelId, groupId}) {
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/account/${accountId}`,
	});
}

export function fetchAccountIndividualMetrics({accountId, channelId, groupId}) {
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/account/${accountId}/overview`,
	});
}

export function fetchDetails({accountId, channelId, groupId}) {
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
}) {
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
}) {
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
}) {
	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/account/${accountId}/account-lifecycles/${accountLifecycleId}`,
	});
}

export function fetchMetrics({channelId, groupId}) {
	return sendRequest({
		data: {channelId},
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
}) {
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
}) {
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
}) {
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
