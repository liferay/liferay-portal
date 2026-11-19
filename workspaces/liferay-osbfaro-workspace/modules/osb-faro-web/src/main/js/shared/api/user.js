import sendRequest from 'shared/util/request';
import {buildOrderByFields} from 'shared/util/pagination';
import {UserStatuses, USERS} from 'shared/util/constants';

function delete$({groupId, ids}) {
	return sendRequest({
		data: {ids},
		method: 'DELETE',
		path: `main/${groupId}/user`,
	});
}

export {delete$ as delete};

export function fetchCurrentUser({groupId}) {
	return sendRequest({
		method: 'GET',
		path: `main/${groupId}/user/current`,
	});
}

export function fetchMany({
	delta,
	groupId,
	orderIOMap,
	page,
	query,
	statuses = [UserStatuses.Approved, UserStatuses.Pending],
}) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams, USERS);

	return sendRequest({
		data: {
			cur: page,
			delta,
			orderByFields,
			query,
			statuses,
		},
		method: 'GET',
		path: `main/${groupId}/user`,
	});
}

export function fetchCount({groupId, query = '', statuses = [0, 1]}) {
	return sendRequest({
		data: {
			query,
			statuses,
		},
		method: 'GET',
		path: `main/${groupId}/user/count`,
	});
}

export function accept({groupId, id}) {
	return sendRequest({
		data: {id},
		method: 'POST',
		path: `main/${groupId}/user/${id}/accept`,
	});
}

export function inviteMany({emailAddresses, groupId, roleName}) {
	return sendRequest({
		data: {emailAddresses, roleName},
		method: 'POST',
		path: `main/${groupId}/user`,
	});
}

export function updateLanguage({languageId}) {
	return sendRequest({
		baseURL: '/c/portal',
		contentType: '',
		data: {languageId, redirect: location.pathname},
		method: 'GET',
		path: 'update_language',
	});
}

export function updateMany({groupId, ids, roleName}) {
	return sendRequest({
		data: {ids, roleName},
		method: 'PUT',
		path: `main/${groupId}/user`,
	});
}
