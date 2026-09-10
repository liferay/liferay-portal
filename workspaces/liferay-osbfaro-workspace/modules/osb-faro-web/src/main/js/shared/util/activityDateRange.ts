import moment from 'moment';
import {DEFAULT_DATE_FORMAT, formatUTCDate, getEndDate} from 'shared/util/date';
import {getSafeRangeSelectors} from 'shared/util/util';
import {Interval, RangeSelectors, SafeRangeSelectors} from 'shared/types';
import {isHourlyRangeKey} from 'shared/util/time';
import {isNil} from 'lodash';

const DATE_TIME_FORMAT = `${DEFAULT_DATE_FORMAT}[T]HH:mm:ss`;

/**
 * Narrows the range selectors used to fetch the activity-stream sessions to the
 * interval of the chart point the user selected. When no point is selected the
 * original range is returned untouched. Shared by the account and individual
 * activity-stream cards, which drive the same chart-to-timeline interaction.
 *
 * The hourly range keys plot one point per hour, so their selectors carry the
 * time as well as the date, spanning the whole hour the point stands for. Every
 * other key plots one point per day and stays date only.
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

	if (isHourlyRangeKey(rangeSelectors.rangeKey)) {
		return getSafeRangeSelectors({
			rangeEnd: formatUTCDate(
				moment.utc(intervalInitDate).endOf('hour'),
				DATE_TIME_FORMAT
			),
			rangeKey: rangeSelectors.rangeKey,
			rangeStart: formatUTCDate(intervalInitDate, DATE_TIME_FORMAT),
		});
	}

	return getSafeRangeSelectors({
		rangeEnd: formatUTCDate(endDate, DEFAULT_DATE_FORMAT),
		rangeKey: rangeSelectors.rangeKey,
		rangeStart: formatUTCDate(intervalInitDate, DEFAULT_DATE_FORMAT),
	});
};
