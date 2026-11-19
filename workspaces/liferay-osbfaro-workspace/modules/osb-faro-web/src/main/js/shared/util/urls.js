import {RangeKeyTimeRanges} from 'shared/util/constants';
import {setUriQueryValue, setUriQueryValues} from 'shared/util/url';
import {toRoute} from 'shared/util/router';

/**
 * Get URL
 * @param {string} path
 * @param {object} router
 */
export const getUrl = (path, {params, query}) => {
	const {rangeKey} = query;
	const {assetId, channelId, groupId, id, title, touchpoint} = params;

	const routeParams = {
		assetId,
		channelId,
		groupId,
		id,
		title,
		touchpoint,
	};

	return rangeKey
		? setUriQueryInRoute(path, query, routeParams)
		: toRoute(path, routeParams);
};

const setUriQueryInRoute = (path, query, routeParams) => {
	const {rangeKey} = query;

	if (rangeKey === RangeKeyTimeRanges.CustomRange) {
		return setUriQueryValues(query, toRoute(path, routeParams));
	}
	else {
		return setUriQueryValue(
			toRoute(path, routeParams),
			'rangeKey',
			rangeKey
		);
	}
};
