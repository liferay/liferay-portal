import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {useUnsafeQueryRangeSelectors} from 'shared/hooks/useUnsafeQueryRangeSelectors';

export const DEFAULT_RANGE_SELECTORS: RangeSelectors = {
	rangeEnd: null as unknown as string,
	rangeKey: RangeKeyTimeRanges.Last30Days,
	rangeStart: null as unknown as string,
};

export const useQueryRangeSelectors = (): RangeSelectors => {
	const rangeSelectors = useUnsafeQueryRangeSelectors();

	return rangeSelectors || DEFAULT_RANGE_SELECTORS;
};
