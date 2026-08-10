import moment, {Moment} from 'moment';
import momentTimezone from 'moment-timezone';
import {
	getCustomDateFormat,
	getDate,
	getDayMonthFormat,
	getDayMonthHourFormat,
} from 'shared/util/date';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {TIME_RANGE_LABELS} from 'shared/util/constants';

type TimeRange = {
	description: string;
	value: RangeKeyTimeRanges;
	label: string;
};

export function formatDateWithTimezone(
	timeZoneId: string,
	date = momentTimezone.utc()
): Moment {
	const currentDateWithTimeZone = momentTimezone.tz(date, timeZoneId);
	const hours = currentDateWithTimeZone.utcOffset() / 60;
	const signal = hours < 0 ? 'subtract' : 'add';

	return date.clone()[signal](Math.abs(hours), 'hours');
}

export function formatDateRange(date: any, rangeKey: string | number) {
	if (
		`${rangeKey}` === RangeKeyTimeRanges.Last24Hours ||
		`${rangeKey}` === RangeKeyTimeRanges.Yesterday
	) {
		return moment.utc(date).format(getDayMonthHourFormat());
	}

	return moment.utc(date).format(getDayMonthFormat());
}

type RawTimeRange = {
	endDate: string;
	rangeKey: string | number;
	startDate: string;
};

export function formatTimeRange(timeRange: RawTimeRange[]) {
	return timeRange
		.map(({endDate, rangeKey, startDate}: RawTimeRange) => {
			const start = formatDateRange(getDate(startDate), rangeKey);
			const end = formatDateRange(getDate(endDate), rangeKey);
			const label = `${start} - ${end}`;

			return {
				description: startDate && endDate && label,
				label: TIME_RANGE_LABELS[
					rangeKey as keyof typeof TIME_RANGE_LABELS
				],
				value: `${rangeKey}` as RangeKeyTimeRanges,
			};
		})
		.sort(
			(a: {value: string}, b: {value: string}) =>
				parseInt(a.value) - parseInt(b.value)
		);
}

const filterItemsByRetention = (
	timeRange: Array<TimeRange>,
	retentionPeriod: number
): Array<TimeRange> =>
	timeRange.filter(
		(item) =>
			!(
				retentionPeriod === 7 &&
				item.value === RangeKeyTimeRanges.LastYear
			)
	);

const filterLegacyItems = (
	timeRange: Array<TimeRange>,
	retentionPeriod: number
): Array<TimeRange> =>
	filterItemsByRetention(
		timeRange.filter(({value}) =>
			[
				RangeKeyTimeRanges.Last24Hours,
				RangeKeyTimeRanges.Yesterday,
				RangeKeyTimeRanges.Last7Days,
				RangeKeyTimeRanges.Last28Days,
				RangeKeyTimeRanges.Last30Days,
				RangeKeyTimeRanges.Last90Days,
			].includes(value)
		),
		retentionPeriod
	);

const filterMoreItems = (
	timeRange: Array<TimeRange>,
	retentionPeriod: number
): Array<TimeRange> =>
	filterItemsByRetention(
		timeRange.filter(({label}) => Boolean(label)),
		retentionPeriod
	);

const filterItems = (
	timeRange: Array<TimeRange>,
	rangeKey: RangeKeyTimeRanges,
	rangeKeys: Array<RangeKeyTimeRanges>,
	retentionPeriod: number
) =>
	filterItemsByRetention(
		timeRange.filter(
			({value}) => value === rangeKey || rangeKeys.includes(value)
		),
		retentionPeriod
	);

type GetFilteredItems = (props: {
	legacy: boolean;
	rangeKey: RangeKeyTimeRanges;
	rangeKeys?: Array<RangeKeyTimeRanges>;
	retentionPeriod: number;
	seeMore: boolean;
	timeRange: Array<TimeRange>;
}) => Array<TimeRange>;

export const getFilteredItems: GetFilteredItems = ({
	legacy,
	rangeKey,
	rangeKeys = [
		RangeKeyTimeRanges.Last24Hours,
		RangeKeyTimeRanges.Last7Days,
		RangeKeyTimeRanges.Last30Days,
		RangeKeyTimeRanges.Last90Days,
	],
	retentionPeriod,
	seeMore,
	timeRange,
}) => {
	if (legacy) {
		return filterLegacyItems(timeRange, retentionPeriod);
	}

	if (seeMore) {
		return filterMoreItems(timeRange, retentionPeriod);
	}

	return filterItems(timeRange, rangeKey, rangeKeys, retentionPeriod);
};

interface IGetSelectedItemProps {
	rangeEnd: string | null;
	rangeKey: string | number;
	rangeStart: string | null;
	timeRange: Array<{[key: string]: any}>;
}

export function getSelectedItem({
	rangeEnd,
	rangeKey,
	rangeStart,
	timeRange,
}: IGetSelectedItemProps) {
	if (rangeKey === 'CUSTOM') {
		return {
			label: `${moment(rangeStart).format(getCustomDateFormat())} - ${moment(
				rangeEnd
			).format(getCustomDateFormat())}`,
			value: 'CUSTOM',
		};
	}

	return timeRange.find((item) => item.value === rangeKey) || timeRange[0];
}
