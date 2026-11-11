import {omit, pickBy} from 'lodash';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

export const useRangeQueryValues = () => {
	const rangeSelectors = useQueryRangeSelectors();

	const routeRangeParams = pickBy(rangeSelectors);

	// The API answers `CUSTOM` with a 500, so a custom range reaches it as its
	// bounds alone.

	const apiRangeParams =
		routeRangeParams.rangeKey === RangeKeyTimeRanges.CustomRange
			? omit(routeRangeParams, 'rangeKey')
			: routeRangeParams;

	return {
		apiRangeParams,
		rangeSelectors,
		routeRangeParams,
	};
};
