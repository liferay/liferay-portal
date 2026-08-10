import moment from 'moment';
import {BAR_COLORS} from 'shared/util/recharts';
import {
	getCustomDateFormat,
	getDayMonthFormat,
	getDayMonthHourFormat,
	getHourOnlyFormat,
	getMonthYearFormat,
} from 'shared/util/date';
import {getIntervalHandle} from './intervals';
import {Interval, RangeSelectors} from 'shared/types';
import {INTERVAL_KEY_MAP, isMonthlyRangeKey} from 'shared/util/time';
import {isNumber, round} from 'lodash';
import {Map} from 'immutable';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {
	formatPercentFromRatio,
	toDuration as toDurationRaw,
	toRounded,
	toThousands,
} from 'shared/util/numbers';

const toDuration = toDurationRaw as (
	time: number | string,
	format?: string,
	measurement?: string
) => string;

export type DataTooltip = {
	id: string;
	index: number;
	name: string;
	value: number;
	x: number | string | Date;
};

export enum MetricValueType {
	Number = 'number',
	Percentage = 'percentage',
	Time = 'time',
	Ratings = 'ratings',
}

export const CHART_COLORS = [
	'#4B9BFF',
	'#FFB46E',
	'#FF5F5F',
	'#50D2A0',
	'#FF73C3',
	'#9CE269',
	'#B077FF',
	'#FFD76E',
	'#5FC8FF',
];

export const CHART_COLOR_NAMES = {
	greyjoy: '#000000',
	lannister: '#FF5F5F',
	martell: '#50D2A0',
	martellD2: '#31BE88',
	martellD4: '#26966B',
	martellL1: '#64D7AB',
	martellL2: '#79DCB6',
	martellL4: '#A1E7CC',
	mormont: '#FFB46E',
	mormontD2: '#FF9A3B',
	mormontL2: '#FFCEA1',
	mormontL4: '#FFE9D4',
	stark: '#4B9BFF',
	starkD2: '#187FFF',
	starkL2: '#7EB7FF',
	starkL4: '#B1D4FF',
};

export const Colors = {
	danger: '#DA1414',
	gray: '#AEB0BB',
	mapEmpty: '#E1E1E1',
	mapMax: '#0065E4',
	mapMin: '#B1D4FF',
	mapSelected: '#4B9BFF',
	mormont: '#FFB46E',
	negative: '#DA1414',
	neutral: '#6B6C7E',
	pallete: [
		'#4B9BFF',
		'#FFB46E',
		'#FF5F5F',
		'#50D2A0',
		'#FF73C3',
		'#9CE268',
		'#B077FF',
		'#FFD76E',
		'#5FC8FF',
	],
	positive: '#287D3C',
	primary: '#4B9BFF',
	secondary: '#CCCCCC',
	warning: '#B95000',
};

export const dateRangeFormatter = (
	dateStart: Date,
	dateEnd: Date,
	withYear: boolean = false
): string => {

	// TODO: Add timezone param

	const dayFormat = 'D';
	const dayMonthFormat = getDayMonthFormat();
	const dayMonthYearFormat = getCustomDateFormat();

	const format = (date: Date, momentFormat: string) =>
		moment.utc(date).format(momentFormat);

	return `${
		withYear
			? format(dateStart, dayMonthYearFormat)
			: format(dateStart, dayMonthFormat)
	} - ${
		moment(dateStart).get('month') !== moment(dateEnd).get('month')
			? withYear
				? format(dateEnd, dayMonthYearFormat)
				: format(dateEnd, dayMonthFormat)
			: format(dateEnd, dayFormat)
	}`;
};

/**
 * Format Tooltip Date
 * @param {date} date
 * @param {string} rangeKey
 */
export const formatTooltipDate = (
	date: number | string | Date,
	rangeKey: RangeKeyTimeRanges
) => {
	if (
		rangeKey === RangeKeyTimeRanges.Last24Hours ||
		rangeKey === RangeKeyTimeRanges.Yesterday
	) {

		// display hours for Last 24 hours and yesterday

		return moment.utc(date).format(getDayMonthHourFormat());
	}

	return moment.utc(date).format(getCustomDateFormat());
};

export const formatXAxisDate = (
	dateKey: number | string,
	rangeKey: string,
	interval: Interval,
	dateKeysIMap: Map<number, [number, number | null]>
) => {

	// display date and month

	let formatter = (date: Date) =>
		moment.utc(date).format(getDayMonthFormat());
	const monthFormat = (date: Date) => moment.utc(date).format('MMM');

	const dates = dateKeysIMap.get(Number(dateKey));
	const dateStart = dates ? dates[0] : 0;
	const dateEnd = dates ? dates[1] : null;

	switch (rangeKey) {
		case RangeKeyTimeRanges.CustomRange:
		case RangeKeyTimeRanges.Last7Days:
		case RangeKeyTimeRanges.Last28Days:
		case RangeKeyTimeRanges.Last30Days:
		case RangeKeyTimeRanges.Last90Days:
		case RangeKeyTimeRanges.Last180Days:
		case RangeKeyTimeRanges.LastYear:
			if (interval === INTERVAL_KEY_MAP.week) {

				// display date range

				// TODO: Add timezone param

				return dateRangeFormatter(
					new Date(dateStart),
					new Date(dateEnd ?? dateStart),
					false
				);
			}
			if (interval === INTERVAL_KEY_MAP.month) {

				// display month

				return monthFormat(new Date(dateStart));
			}
			break;
		case RangeKeyTimeRanges.Last24Hours:
		case RangeKeyTimeRanges.Yesterday:

			// display hours

			formatter = (date: Date) =>
				moment.utc(date).format(getHourOnlyFormat());
			break;
		default:
			break;
	}

	return formatter(new Date(dateStart));
};

/**
 * Return the formatted numbers to display on charts.
 * Per design requets these numbers shouldn't have decimal
 * precision.
 * @param {string} type
 */
export const getAxisFormatter = (type: string): ((value: number) => string) => {
	if (type === 'percentage') {
		return (value: number) => formatPercentFromRatio(value);
	}
	else if (type === 'time') {
		return (value: number) => {
			const displayMilliseconds =
				value < 2e3 && value !== 1000 ? 'S[ms]' : '';

			const format = `DD[days] hh[h] mm[m] ss[s] ${displayMilliseconds}`;

			return toDuration(value, format);
		};
	}
	else if (type == 'ratings') {
		return (value: number) => `${toRounded(value * 10, 2)}`;
	}

	return getMetricFormatter(type);
};

/**
 * Return the chart axis measures from a max value
 * @param {number} value
 */
export const getAxisMeasures = (value: number) => {
	const numChars = Math.floor(value).toString().length;
	const decOrder = Math.pow(10, numChars - 1);
	let maxValue = decOrder * Math.floor(value / decOrder) + decOrder;
	let firstDic = maxValue / decOrder;

	if ([3, 7, 9].indexOf(firstDic) > -1) {
		firstDic += 1;
	}

	maxValue = firstDic * decOrder;
	let intervalCount = 4;

	if ([1, 5, 10].indexOf(firstDic) > -1) {
		intervalCount = 5;
	}

	const intervalValue = maxValue / intervalCount;

	// avoid extra intervals

	for (let i = 0; i < intervalCount; i++) {
		let tempMaxValue = intervalValue * (i + 1);
		if (tempMaxValue % 1 !== 0) {
			tempMaxValue = parseFloat(tempMaxValue.toFixed(1));
		}
		if (tempMaxValue > value) {
			maxValue = tempMaxValue;
			intervalCount = i + 1;
			break;
		}
	}

	const intervals = [];
	intervals.push(0);

	for (let i = 0; i < intervalCount; i++) {
		intervals.push(intervalValue * (i + 1));
	}

	return {
		intervalCount,
		intervals,
		intervalValue,
		maxValue,
	};
};

/**
 * Return the chart max value from a data
 * @param {Array} data
 */
export const getAxisMeasuresFromData = (data: number[][]) =>
	getAxisMeasures(
		Math.max(
			...data
				.reduce<number[]>((prev, next) => prev.concat(next), [])
				.filter((value: unknown) => typeof value === 'number')
		)
	);

export const getBarColor = (
	currentBarIndex: number,
	hoverIndex: number,
	selectedPoint?: number,
	color: keyof typeof BAR_COLORS = 'blue'
): string => {
	if (selectedPoint === currentBarIndex) {
		return BAR_COLORS[color].selected;
	}
	else if (currentBarIndex === hoverIndex) {
		return BAR_COLORS[color].hover;
	}
	else if (isNumber(selectedPoint)) {
		return BAR_COLORS[color].notSelected;
	}

	return BAR_COLORS[color].default;
};

/**
 * Return the formatted array to display on charts.
 * @param {string} type
 */
export const getDataFormatter = (type: string) => {
	if (type === 'time') {
		return (arr: number[]) =>
			arr.map((value: number) => Math.round(value / 1e3) * 1e3);
	}

	return (arr: number[]) => arr;
};

/**
 * Get Date Title
 * @param {array} dates
 * @param {string} rangeKey
 */
export const getDateTitle = (
	dates: [number, number | null] | undefined,
	rangeKey: RangeKeyTimeRanges,
	interval: Interval
): string => {
	if (!dates) {
		return '';
	}

	const [startDate, endDate] = dates;

	if (isMonthlyRangeKey(rangeKey) && interval === INTERVAL_KEY_MAP.week) {
		return dateRangeFormatter(
			new Date(startDate),
			new Date(endDate ?? startDate),
			true
		);
	}
	else if (interval === INTERVAL_KEY_MAP.month) {
		return moment.utc(startDate).format(getMonthYearFormat());
	}

	return formatTooltipDate(startDate, rangeKey);
};

/**
 * Get Intervals
 * @param {string} rangeKey
 * @param {array} arr
 */
export const getIntervals = (
	rangeKey: RangeSelectors['rangeKey'],
	arr: Array<number | null>,
	timeInterval: Interval,
	dateKeysIMap: any
): Array<number | null> => {
	if (arr.length) {
		const firstDate = moment(arr[0]);
		const [lastPeriodStart, lastPeriodEnd] = dateKeysIMap.get(
			arr[arr.length - 1]
		);
		const lastDate = lastPeriodEnd
			? moment(lastPeriodEnd)
			: moment(lastPeriodStart);
		const duration = lastDate.diff(firstDate, 'days') + 1;

		const validTimeInterval = [
			RangeKeyTimeRanges.Last24Hours,
			RangeKeyTimeRanges.Yesterday,
		].includes(rangeKey)
			? INTERVAL_KEY_MAP.day
			: timeInterval;

		const intervalHandle = getIntervalHandle(
			rangeKey,
			duration,
			validTimeInterval
		);

		return intervalHandle
			? intervalHandle(arr.filter((v): v is number => v !== null))
			: arr;
	}

	return arr;
};

/**
 * Return the Locations data
 */
type LocationMetric = {value: number; valueKey: string};
type LocationDataItem = {
	color?: string;
	group: string;
	id: string;
	name: string;
	total: number;
	value: number;
};

export const getLocationsData = (
	metrics: LocationMetric[],
	location = 'Any'
) => {
	let total = 0;

	metrics.forEach(({value}: LocationMetric) => {
		total += value;
	});

	const data: LocationDataItem[] = metrics.map(
		({value, valueKey}: LocationMetric) => ({
			group: valueKey,
			id: valueKey,
			name: valueKey,
			total: value,
			value: round((value / total) * 100, 1),
		})
	);

	let othersLabel;

	if (location === 'Any') {
		othersLabel = Liferay.Language.get('other-countries');
	}
	else {
		othersLabel = Liferay.Language.get('other-regions');
	}

	const others = metrics.filter(
		(value: LocationMetric, index: number) => index >= 5
	);

	if (others.length > 0) {
		let totalOthers = 0;
		others.forEach(({value}: LocationMetric) => {
			totalOthers += value;
		});

		data.push({
			color: '#CCCCCC',
			group: othersLabel,
			id: 'others',
			name: othersLabel,
			total: totalOthers,
			value: round((totalOthers / total) * 100, 1),
		});
	}

	return data;
};

/**
 * Return the metric formatter
 * @param {string} type
 */
export const getMetricFormatter = (
	type: string
): ((value: number) => string) => {
	if (type === 'number') {
		return (value: number) => `${toThousands(value)}`;
	}
	else if (type === 'percentage') {
		return (value: number) => formatPercentFromRatio(value);
	}
	else if (type === 'time') {
		return (value: number) => toDuration(value);
	}
	else if (type == 'ratings') {
		return (value: number) => `${toRounded(value * 10, 2)}/10`;
	}
	else {
		return (value: number) => String(value);
	}
};
