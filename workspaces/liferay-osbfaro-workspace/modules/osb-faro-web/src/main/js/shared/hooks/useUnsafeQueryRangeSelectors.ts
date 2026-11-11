import {RangeSelectors} from 'shared/types';
import {useQueryParams} from 'shared/hooks/useQueryParams';

/**
 * Used to get undefined if there is no rangeKey on query
 * @returns {RangeSelectors | undefined}
 */

export const useUnsafeQueryRangeSelectors = (): RangeSelectors | undefined => {
	const {rangeEnd, rangeKey, rangeStart} = useQueryParams();

	if (!rangeKey) return;

	return {rangeEnd, rangeKey, rangeStart};
};
