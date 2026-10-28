import sendRequest from 'shared/util/request';

export function fetch({groupId, ...otherParams}) {
	return sendRequest({
		data: {
			...otherParams,
		},
		method: 'GET',
		path: `contacts/${groupId}/individual/distribution`,
	});
}
