import sendRequest from 'shared/util/request';
import {
	buildOrderByFields,
	getDefaultSortOrder,
	NAME,
} from 'shared/util/pagination';
import {USERS} from 'shared/util/constants';

export function clear({groupId, ids}) {
	return sendRequest({
		data: {ids},
		method: 'POST',
		path: `main/${groupId}/channel/clear`,
	});
}

export function create({groupId, name}) {
	return sendRequest({
		data: {name},
		method: 'POST',
		path: `main/${groupId}/channel`,
	});
}

function delete$({groupId, ids}) {
	return sendRequest({
		data: {ids},
		method: 'DELETE',
		path: `main/${groupId}/channel`,
	});
}
export {delete$ as delete};

export function fetchAll({groupId}) {
	return sendRequest({
		data: {
			cur: '-1',
			delta: '-1',
			orderByFields: buildOrderByFields({
				field: NAME,
				sortOrder: getDefaultSortOrder(NAME),
			}),
		},
		method: 'GET',
		path: `main/${groupId}/channel`,
	});
}

export function search({groupId, orderIOMap, ...otherParams}) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams);

	return sendRequest({
		data: {...otherParams, orderByFields},
		method: 'GET',
		path: `main/${groupId}/channel`,
	});
}

export function fetch({channelId, groupId, ...data}) {
	return sendRequest({
		data,
		method: 'GET',
		path: `main/${groupId}/channel/${channelId}`,
	});
}

export function fetchUsers({
	channelId,
	groupId,
	orderIOMap,
	page,
	...otherParams
}) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams, USERS);

	return sendRequest({
		data: {
			cur: page,
			orderByFields,
			...otherParams,
		},
		method: 'GET',
		path: `main/${groupId}/channel/${channelId}/users`,
	});
}

export function deleteUsers({channelId, groupId, userIds}) {
	return sendRequest({
		data: {userIds},
		method: 'DELETE',
		path: `main/${groupId}/channel/${channelId}/users`,
	});
}

export function addUsers({channelId, groupId, userIds}) {
	return sendRequest({
		data: {userIds},
		method: 'POST',
		path: `main/${groupId}/channel/${channelId}/users`,
	});
}

export function update({groupId, id, ...data}) {
	return sendRequest({
		data,
		method: 'PATCH',
		path: `main/${groupId}/channel/${id}`,
	});
}
