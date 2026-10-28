import sendRequest from 'shared/util/request';
import {buildOrderByFields} from 'shared/util/pagination';
import {pickBy} from 'lodash';
import {TimeIntervals} from 'shared/util/constants';

export const DEFAULT_ACTIVITY_MAX = 30;

export const DEFAULT_ACTIVITY_INTERVAL = TimeIntervals.Day;

export function fetchHistory({
	channelId = '',
	contactsEntityId,
	contactsEntityType,
	groupId,
	interval = DEFAULT_ACTIVITY_INTERVAL,
	max = DEFAULT_ACTIVITY_MAX,
	rangeEnd = null,
	rangeStart = null,
}) {
	return sendRequest({
		data: {
			channelId,
			contactsEntityId,
			contactsEntityType,
			interval,
			...pickBy({
				max,
				rangeEnd,
				rangeStart,
			}),
		},
		method: 'GET',
		path: `contacts/${groupId}/activity/history`,
	});
}

export function searchAssets({
	applicationId,
	channelId = '',
	eventId,
	groupId,
	orderIOMap,
	...otherParams
}) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams);

	return sendRequest({
		data: {
			...otherParams,
			applicationId,
			channelId,
			eventId,
			orderByFields,
		},
		method: 'GET',
		path: `contacts/${groupId}/activity/asset`,
	});
}

export function searchCount({action, groupId, ...otherParams}) {
	return sendRequest({
		data: {
			...otherParams,
			action,
		},
		method: 'GET',
		path: `contacts/${groupId}/activity/count`,
	});
}
