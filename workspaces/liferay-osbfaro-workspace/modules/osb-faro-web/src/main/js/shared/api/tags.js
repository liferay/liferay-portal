import sendRequest from 'shared/util/request';

export function search({
	channelId,
	groupId,
	keywords = '',
	page = 1,
	pageSize = 12,
}) {
	return sendRequest({
		data: {channelId, keywords, page, pageSize},
		method: 'GET',
		path: `contacts/${groupId}/asset-summary-tags`,
	});
}

export function fetchAccountTopTags({
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
		path: `contacts/${groupId}/asset-summary-tags`,
	});
}

/**
 * `individualId` mirrors the `accountId` parameter `fetchAccountTopTags` sends:
 * the endpoint takes one scope or the other.
 */

export function fetchIndividualTopTags({
	channelId,
	groupId,
	individualId,
	rangeEnd,
	rangeKey,
	rangeStart,
	selectedMetric,
}) {
	return sendRequest({
		data: {
			channelId,
			individualId,
			pageSize: 5,
			selectedMetric,
			sort: `${selectedMetric},desc`,
			...(rangeKey ? {rangeKey} : {}),
			...(rangeEnd && rangeStart ? {rangeEnd, rangeStart} : {}),
		},
		method: 'GET',
		path: `contacts/${groupId}/asset-summary-tags`,
	});
}
