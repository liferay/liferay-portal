import sendRequest from 'shared/util/request';
import {
	buildOrderByFields,
	createOrderIOMap,
	NAME
} from 'shared/util/pagination';
import {get, pickBy} from 'lodash';

export const clearData = ({groupId, id}) =>
	sendRequest({
		method: 'POST',
		path: `contacts/${groupId}/data_source/${id}/clear`
	});

export const disconnect = ({groupId, id}) =>
	sendRequest({
		method: 'POST',
		path: `contacts/${groupId}/data_source/${id}/disconnect`
	});

export function fetch({groupId, id}) {
	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/data_source/${id}`
	});
}

export function search({
	delta,
	groupId,
	orderIOMap = createOrderIOMap(NAME),
	page = 1,
	query = '',
	...otherParams
}) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams);

	return sendRequest({
		data: {cur: page, delta, orderByFields, query, ...otherParams},
		method: 'GET',
		path: `contacts/${groupId}/data_source`
	});
}

export function fetchChannels({
	channelIds,
	groupId,
	orderIOMap = createOrderIOMap(NAME)
}) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams);

	return sendRequest({
		data: {
			channelIds,
			orderByFields
		},
		method: 'GET',
		path: `contacts/${groupId}/data_source/channels`
	});
}

export function fetchDataSourceId({groupId, token}) {
	return sendRequest({
		data: {token},
		method: 'POST',
		path: `contacts/${groupId}/data_source/data_source_id`
	});
}

export function fetchDeletePreview({groupId, id}) {
	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/data_source/${id}/delete_preview`
	});
}

export function fetchFieldValues({
	count,
	fieldName,
	fileVersionId,
	groupId,
	id
}) {
	return sendRequest({
		data: {count, fieldName, fileVersionId, id},
		method: 'GET',
		path: `contacts/${groupId}/data_source/field_values`
	});
}

export function fetchMappings({fileVersionId, groupId, id}) {
	return sendRequest({
		data: {
			fileVersionId,
			id
		},
		method: 'GET',
		path: `contacts/${groupId}/data_source/mappings`
	});
}

export function fetchMappingsLite({context, fileVersionId, groupId, id}) {
	return sendRequest({
		data: {context, fileVersionId},
		method: 'GET',
		path: `contacts/${groupId}/data_source/${id}/mappings/lite`
	});
}

export function fetchProgress({groupId, id}) {
	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/data_source/${id}/progress`
	});
}

export function fetchOrganizations({cur, delta, groupId, id, name}) {
	return sendRequest({
		data: {
			cur,
			delta,
			name
		},
		method: 'GET',
		path: `contacts/${groupId}/data_source/${
			id ? `${id}/` : ''
		}organizations`
	});
}

export function fetchOrganizationsById({groupId, id, organizationIds}) {
	return sendRequest({
		data: {organizationIds},
		method: 'POST',
		path: `contacts/${groupId}/data_source/${id}/organizations_by_ids`
	});
}

export function fetchSites({cur, delta, groupId, id, name}) {
	return sendRequest({
		data: {
			cur,
			delta,
			name
		},
		method: 'GET',
		path: `contacts/${groupId}/data_source/${id ? `${id}/` : ''}groups`
	});
}

export function fetchSitesById({groupId, id, siteIds}) {
	return sendRequest({
		data: {groupIds: siteIds},
		method: 'POST',
		path: `contacts/${groupId}/data_source/${id}/groups_by_ids`
	});
}

export const fetchToken = (groupId, dataSourceId) =>
	sendRequest({
		contentType: 'text/plain',
		method: 'GET',
		path: `contacts/${groupId}/data_source/${
			dataSourceId ? `${dataSourceId}/` : ''
		}token`
	});

export function fetchUserGroups({cur, delta, groupId, id, name}) {
	return sendRequest({
		data: {
			cur,
			delta,
			name
		},
		method: 'GET',
		path: `contacts/${groupId}/data_source/${id ? `${id}/` : ''}user_groups`
	});
}

export function fetchUserGroupsById({groupId, id, userGroupIds}) {
	return sendRequest({
		data: {userGroupIds},
		method: 'POST',
		path: `contacts/${groupId}/data_source/${id}/user_groups_by_ids`
	});
}

export function fetchLiferaySyncCounts({contactsConfiguration, groupId, id}) {
	return sendRequest({
		data: {contactsConfiguration},
		method: 'POST',
		path: `contacts/${groupId}/data_source/${id}/liferay/sync_counts`
	});
}

export function createCSV({fieldMappingMaps, fileVersionId, groupId, name}) {
	const data = pickBy(
		{
			fieldMappingMaps,
			fileVersionId
		},
		Boolean
	);

	return sendRequest({
		data: {
			...data,
			name
		},
		method: 'POST',
		path: `contacts/${groupId}/data_source/csv`
	});
}

export function createLiferay({
	credentials,
	fieldMappingMaps,
	groupId,
	name,
	status,
	url
}) {
	const data = pickBy(
		{
			credentials,
			fieldMappingMaps,
			status,
			url
		},
		Boolean
	);

	return sendRequest({
		data: {
			...data,
			name
		},
		method: 'POST',
		path: `contacts/${groupId}/data_source/liferay`
	});
}

export function createSalesforce({
	accountsConfiguration,
	contactsConfiguration,
	credentials,
	fieldMappingMaps,
	groupId,
	name,
	status,
	url
}) {
	const data = pickBy(
		{
			accountsConfiguration,
			contactsConfiguration,
			credentials,
			fieldMappingMaps,
			status,
			url
		},
		Boolean
	);

	return sendRequest({
		data: {
			...data,
			name
		},
		method: 'POST',
		path: `contacts/${groupId}/data_source/salesforce`
	});
}

export function updateCSV({fieldMappingMaps, groupId, id, name, status}) {
	const data = pickBy(
		{
			fieldMappingMaps,
			status
		},
		Boolean
	);

	return sendRequest({
		data: {
			...data,
			name
		},
		method: 'PATCH',
		path: `contacts/${groupId}/data_source/${id}/csv`
	});
}

export function updateLiferay({
	analyticsConfiguration,
	channelsConfiguration,
	contactsConfiguration,
	credentials,
	fieldMappingMaps,
	groupId,
	id,
	name,
	status,
	url
}) {
	const data = pickBy(
		{
			analyticsConfiguration,
			channelsConfiguration,
			contactsConfiguration,
			credentials: get(
				credentials,
				'oAuthToken',
				get(credentials, 'oAuthCode', get(credentials, 'password'))
			)
				? credentials
				: null,
			fieldMappingMaps,
			status,
			url
		},
		Boolean
	);

	return sendRequest({
		data: {
			...data,
			name
		},
		method: 'PATCH',
		path: `contacts/${groupId}/data_source/${id}/liferay`
	});
}

export function updateSalesforce({
	accountsConfiguration,
	channelsConfiguration,
	contactsConfiguration,
	credentials,
	fieldMappingMaps,
	groupId,
	id,
	name,
	status,
	url
}) {
	const data = pickBy(
		{
			accountsConfiguration,
			channelsConfiguration,
			contactsConfiguration,
			credentials,
			fieldMappingMaps,
			status,
			url
		},
		Boolean
	);

	return sendRequest({
		data: {
			...data,
			name
		},
		method: 'PATCH',
		path: `contacts/${groupId}/data_source/${id}/salesforce`
	});
}

export function fetchChannelDatasources({
	delta,
	groupId,
	id,
	orderIOMap = createOrderIOMap(NAME),
	page,
	query = ''
}) {
	const orderParams = orderIOMap.first();
	const orderByFields = buildOrderByFields(orderParams);

	return sendRequest({
		data: {
			cur: page,
			delta,
			name: query,
			orderByFields
		},
		method: 'GET',
		path: `contacts/${groupId}/data_source/${id}/channel-data-sources`
	});
}

export function fetchCompanies({groupId}) {
	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/data_source/companies`
	});
}

export function fetchOAuthUrl({
	baseUrl,
	callbackUrl,
	consumerKey,
	consumerSecret,
	groupId,
	type
}) {
	return sendRequest({
		data: {
			baseURL: baseUrl,
			oAuthCallbackURL: callbackUrl,
			oAuthConsumerKey: consumerKey,
			oAuthConsumerSecret: consumerSecret
		},
		method: 'GET',
		path: `contacts/${groupId}/data_source/${type.toLowerCase()}/oauth_request_token_credentials`
	});
}

function delete$({groupId, id}) {
	return sendRequest({
		method: 'DELETE',
		path: `contacts/${groupId}/data_source/${id}`
	});
}

export function fetchChannelsMetric({groupId, id}) {
	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/data_source/${id}/metrics`
	});
}

export {delete$ as delete};
