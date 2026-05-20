import sendRequest from 'shared/util/request';
import {pickBy} from 'lodash';
import {RESTParams} from 'shared/types';

interface CreateConnectorParams {
	credentials: {[key: string]: any};
	groupId: string;
	name: string;
}

interface UpdateConnectorParams {
	channelsConfiguration?: {[key: string]: any};
	credentials?: {[key: string]: any};
	groupId: string;
	id: string;
	name?: string;
	status?: string;
}

export function createConnector(
	slug: string,
	{credentials, groupId, name}: CreateConnectorParams
) {
	const data = pickBy(
		{
			credentials
		},
		Boolean
	);

	return sendRequest({
		data: {
			...data,
			name
		},
		method: 'POST',
		path: `contacts/${groupId}/data_source/${slug}`
	});
}

export function updateConnector(
	slug: string,
	{
		channelsConfiguration,
		credentials,
		groupId,
		id,
		name,
		status
	}: UpdateConnectorParams
) {
	const data = pickBy(
		{
			channelsConfiguration,
			credentials,
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
		path: `contacts/${groupId}/data_source/${id}/${slug}`
	});
}

export function fetchConnectorEntityCount(
	entity: string,
	{groupId, id}: {groupId: string; id: string}
) {
	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/data-source-metrics/${id}/${entity}_count`
	});
}

export function generateConnectorToken({
	groupId,
	type
}: RESTParams & {type: string}) {
	return sendRequest({
		method: 'POST',
		path: `main/${groupId}/oauth2/tokens/new?type=${type}&expiresIn=`
	});
}
