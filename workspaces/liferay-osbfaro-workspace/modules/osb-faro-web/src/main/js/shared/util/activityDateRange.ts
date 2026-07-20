import {DEFAULT_DATE_FORMAT, formatUTCDate, getEndDate} from 'shared/util/date';
import {getSafeRangeSelectors} from 'shared/util/util';
import {Interval, RangeSelectors, SafeRangeSelectors} from 'shared/types';
import {isNil} from 'lodash';
import {RangeKeyTimeRanges} from 'shared/util/constants';

const TIME_FORMAT = 'HH:mm:ss';

/**
 * Narrows the range selectors used to fetch the activity-stream sessions to the
 * interval of the chart point the user selected. When no point is selected the
 * original range is returned untouched. Shared by the account and individual
 * activity-stream cards, which drive the same chart-to-timeline interaction.
 */
export const getSessionsDateRange = ({
	activityHistory,
	interval,
	rangeSelectors,
	selectedPoint,
}: {
	activityHistory: Array<{intervalInitDate: number}>;
	interval: Interval;
	rangeSelectors: RangeSelectors;
	selectedPoint?: number;
}): SafeRangeSelectors => {
	const {intervalInitDate} =
		(selectedPoint !== undefined && activityHistory[selectedPoint]) || {};

	const endDate = getEndDate(intervalInitDate, interval);

	const hasSelectedDate = !isNil(endDate) && !isNil(intervalInitDate);

	if (!hasSelectedDate) {
		return getSafeRangeSelectors(rangeSelectors);
	}

	const formattedRangeEnd = formatUTCDate(endDate, DEFAULT_DATE_FORMAT);
	const formattedRangeStart = formatUTCDate(
		intervalInitDate,
		DEFAULT_DATE_FORMAT
	);

	if (rangeSelectors.rangeKey === RangeKeyTimeRanges.Last24Hours) {
		return getSafeRangeSelectors({
			rangeEnd: `${formattedRangeEnd}T${formatUTCDate(
				intervalInitDate + 59 * 60000,
				TIME_FORMAT
			)}`,
			rangeKey: rangeSelectors.rangeKey,
			rangeStart: `${formattedRangeStart}T${formatUTCDate(
				intervalInitDate,
				TIME_FORMAT
			)}`,
		});
	}

	return getSafeRangeSelectors({
		rangeEnd: formattedRangeEnd,
		rangeKey: rangeSelectors.rangeKey,
		rangeStart: formattedRangeStart,
	});
};
