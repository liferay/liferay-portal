import Constants, {SegmentTypes, TimeIntervals} from 'shared/util/constants';
import sendRequest from 'shared/util/request';
import {buildOrderByFields, NAME} from 'shared/util/pagination';
import {INDIVIDUALS, SEGMENTS} from 'shared/util/router';

const {
	pagination: {cur: DEFAULT_PAGE, delta: DEFAULT_DELTA, orderDefault}
} = Constants;

const DEFAULT_MAX = 30;

const DEFAULT_INTERVAL = TimeIntervals.Day;

export function addIndividuals({groupId, individualIds, selectedSegmentId}) {
	return sendRequest({
		data: {
			individualIds
		},
		method: 'PUT',
		path: `contacts/${groupId}/individual_segment/${selectedSegmentId}/memberships`
	});
}

function delete$({groupId, ids}) {
	return sendRequest({
		data: {
			ids
		},
		method: 'DELETE',
		path: `contacts/${groupId}/individual_segment`
	});
}

export {delete$ as delete};

export function fetch({groupId, includeReferencedObjects = false, segmentId}) {
	return sendRequest({
		data: {includeReferencedObjects},
		method: 'GET',
		path: `contacts/${groupId}/individual_segment/${segmentId}`
	}).then(({filter, ...otherParams}) => ({
		criteriaString: filter,
		...otherParams
	}));
}

export function create({
	channelId = '',
	criteriaString = '',
	groupId,
	includeAnonymousUsers = false,
	individualIds = [],
	name,
	segmentType
}) {
	const data =
		segmentType === SegmentTypes.Batch
			? {
					channelId,
					filter: criteriaString,
					includeAnonymousUsers,
					name,
					segmentType
			  }
			: {
					channelId,
					individualIds,
					name,
					segmentType
			  };

	return sendRequest({
		data,
		method: 'POST',
		path: `contacts/${groupId}/individual_segment`
	});
}

export function update({
	channelId = '',
	criteriaString = '',
	groupId,
	id,
	includeAnonymousUsers = false,
	name,
	segmentType
}) {
	const data =
		segmentType === SegmentTypes.Batch
			? {
					channelId,
					filter: criteriaString,
					includeAnonymousUsers,
					name,
					segmentType
			  }
			: {
					channelId,
					name,
					segmentType
			  };

	return sendRequest({
		data,
		method: 'PUT',
		path: `contacts/${groupId}/individual_segment/${id}`
	});
}

export function updateChannel({channelId, groupId, id}) {
	return sendRequest({
		method: 'PUT',
		path: `contacts/${groupId}/individual_segment/${id}/channel/${channelId}`
	});
}

export function addMemberships({groupId, id, individualIds}) {
	return sendRequest({
		data: {
			individualIds
		},
		method: 'PUT',
		path: `contacts/${groupId}/individual_segment/${id}/memberships`
	});
}

export function removeMemberships({groupId, id, individualIds}) {
	return sendRequest({
		data: {
			individualIds
		},
		method: 'DELETE',
		path: `contacts/${groupId}/individual_segment/${id}/memberships`
	});
}

export function fetchMembershipChanges({
	delta,
	endDate,
	groupId,
	id,
	orderIOMap,
	query,
	startDate
}) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams, INDIVIDUALS);

	return sendRequest({
		data: {
			delta,
			endDate,
			groupId,
			id,
			orderByFields,
			query,
			startDate
		},
		method: 'GET',
		path: `contacts/${groupId}/individual_segment/${id}/memberships/changes`
	});
}

export function fetchMembershipChangesAggregations({
	channelId = '',
	groupId,
	id,
	interval = DEFAULT_INTERVAL,
	max = DEFAULT_MAX
}) {
	return sendRequest({
		data: {
			channelId,
			interval,
			max
		},
		method: 'GET',
		path: `contacts/${groupId}/individual_segment/${id}/memberships/changes/aggregations`
	});
}

export function search({
	delta = DEFAULT_DELTA,
	groupId,
	orderIOMap,
	page = DEFAULT_PAGE,
	query = '',
	...otherParams
}) {
	const orderParams = orderIOMap.first();

	const orderByFields = buildOrderByFields(orderParams, SEGMENTS);

	return sendRequest({
		data: {cur: page, delta, orderByFields, query, ...otherParams},
		method: 'GET',
		path: `contacts/${groupId}/individual_segment`
	});
}

export function searchUnassigned({
	delta = DEFAULT_DELTA,
	groupId,
	orderByFields = [
		{
			fieldName: NAME,
			orderBy: orderDefault,
			system: true
		}
	],
	page = DEFAULT_PAGE,
	query = '',
	...otherParams
}) {
	return sendRequest({
		data: {cur: page, delta, orderByFields, query, ...otherParams},
		method: 'GET',
		path: `contacts/${groupId}/individual_segment/unassigned`
	});
}
