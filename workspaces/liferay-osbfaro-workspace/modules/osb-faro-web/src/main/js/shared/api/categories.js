import sendRequest from 'shared/util/request';

export function search({
	channelId,
	groupId,
	keywords = '',
	page = 1,
	pageSize = 20,
	vocabularyId = '',
}) {
	return sendRequest({
		data: {channelId, keywords, page, pageSize, vocabularyId},
		method: 'GET',
		path: `contacts/${groupId}/asset-summary-categories`,
	});
}

export function fetchAccountTopCategories({
	accountId,
	channelId,
	groupId,
	rangeEnd,
	rangeKey,
	rangeStart,
	selectedMetric,
}) {
	return sendRequest({
		data: {
			accountId,
			channelId,
			pageSize: 5,
			selectedMetric,
			sort: `${selectedMetric},desc`,
			...(rangeKey ? {rangeKey} : {}),
			...(rangeEnd && rangeStart ? {rangeEnd, rangeStart} : {}),
		},
		method: 'GET',
		path: `contacts/${groupId}/asset-summary-categories`,
	});
}
