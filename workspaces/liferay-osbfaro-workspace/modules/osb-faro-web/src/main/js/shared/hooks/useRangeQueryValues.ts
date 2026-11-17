import {omit, pickBy} from 'lodash';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

// The API answers `CUSTOM` with a 500, so a custom range reaches it as its
// bounds alone.

export const getAPIRangeParams = (rangeSelectors: RangeSelectors) => {
	const routeRangeParams = pickBy(rangeSelectors);

	return routeRangeParams.rangeKey === RangeKeyTimeRanges.CustomRange
		? omit(routeRangeParams, 'rangeKey')
		: routeRangeParams;
};

export const useRangeQueryValues = () => {
	const rangeSelectors = useQueryRangeSelectors();

	return {
		apiRangeParams: getAPIRangeParams(rangeSelectors),
		rangeSelectors,
		routeRangeParams: pickBy(rangeSelectors),
	};
};
