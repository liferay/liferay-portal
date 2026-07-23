import FaroConstants from 'shared/util/constants';
import sendRequest from 'shared/util/request';
import {ACCOUNTS} from 'shared/util/router';
import {buildOrderByFields} from 'shared/util/pagination';
import {escapeSingleQuotes} from 'segment/segment-editor/dynamic/utils/odata';

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

export function fetchDetails({accountId, channelId, groupId}) {
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/account/${accountId}/details`,
	});
}

export function fetchFieldValues({
	channelId,
	fieldMappingFieldName,
	groupId,
	query,
}) {
	return sendRequest({
		data: {
			channelId,
			delta: DEFAULT_DELTA,
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
