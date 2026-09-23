import moment from 'moment';
import momentTimezone from 'moment-timezone';
import {flow, get, head, last, rangeRight} from 'lodash/fp';
import {getLocale} from 'shared/util/locale';
import {INTERVAL_KEY_MAP} from 'shared/util/time';
import {sub} from 'shared/util/lang';

export const DATE_MASK = [
	/\d/,
	/\d/,
	/\d/,
	/\d/,
	'-',
	/\d/,
	/\d/,
	'-',
	/\d/,
	/\d/,
];

export const DATE_TIME_MASK = [
	/\d/,
	/\d/,
	/\d/,
	/\d/,
	'-',
	/\d/,
	/\d/,
	'-',
	/\d/,
	/\d/,
	' ',
	/\d/,
	/\d/,
	':',
	/\d/,
	/\d/,
];

export const DEFAULT_DATE_FORMAT = 'YYYY-MM-DD';

export const DEFAULT_TIMEZONE_ID = 'UTC';

export const ISO_8601_DATE_FORMAT = 'YYYY-MM-DDTHH:mm:ss.SSS[Z]';

// Every locale-sensitive text goes through Intl in the current user's locale,
// so any language the portal makes available formats without predefined
// lists. moment keeps parsing, date arithmetic, time zones and the
// locale-neutral token formats such as DEFAULT_DATE_FORMAT.

export class DateFormat {
	constructor(formatter) {
		this._formatter = formatter;
	}

	format(date) {
		return this._formatter(date);
	}
}

// Builds a value once per locale, so charts and tables formatting one value
// per tick or cell reuse the same Intl formatter.

function perLocale(create) {
	let cachedLocale;
	let cachedValue;

	return () => {
		const locale = getLocale();

		if (locale !== cachedLocale) {
			cachedLocale = locale;
			cachedValue = create(locale);
		}

		return cachedValue;
	};
}

// Formats the date's own wall-clock time, so a moment in UTC, in a named time
// zone or in a fixed offset renders the same fields moment would.

function createDateFormat(options) {
	const getFormatter = perLocale(
		(locale) =>
			new Intl.DateTimeFormat(locale, {...options, timeZone: 'UTC'})
	);

	return new DateFormat((date) =>
		getFormatter().format(
			new Date(date.valueOf() + date.utcOffset() * 60000)
		)
	);
}

const getAutoRelativeTimeFormat = perLocale(
	(locale) => new Intl.RelativeTimeFormat(locale, {numeric: 'auto'})
);

const getRelativeTimeFormat = perLocale(
	(locale) => new Intl.RelativeTimeFormat(locale, {numeric: 'always'})
);

const CUSTOM_DATE_FORMAT = createDateFormat({
	day: 'numeric',
	month: 'short',
	year: 'numeric',
});

const CUSTOM_DATE_TIME_FORMAT = createDateFormat({
	day: 'numeric',
	hour: 'numeric',
	minute: '2-digit',
	month: 'short',
	year: 'numeric',
});

const DAY_MONTH_FORMAT = createDateFormat({day: 'numeric', month: 'short'});

const FULL_DAY_MONTH_FORMAT = createDateFormat({day: 'numeric', month: 'long'});

const HOUR_FORMAT = createDateFormat({hour: 'numeric'});

const MONTH_YEAR_FORMAT = createDateFormat({month: 'short', year: 'numeric'});

export const LONG_DATE_FORMAT = createDateFormat({dateStyle: 'long'});

export const MONTH_FORMAT = createDateFormat({month: 'long'});

export const NUMERIC_DATE_FORMAT = createDateFormat({
	day: 'numeric',
	month: 'numeric',
	year: 'numeric',
});

export const SHORT_MONTH_FORMAT = createDateFormat({month: 'short'});

export const SHORT_NUMERIC_DATE_FORMAT = createDateFormat({
	day: 'numeric',
	month: 'numeric',
	year: '2-digit',
});

export const TIME_FORMAT = createDateFormat({timeStyle: 'short'});

const WEEKDAY_FORMAT = createDateFormat({weekday: 'long'});

export const DEFAULT_FORMAT = LONG_DATE_FORMAT;

/**
 * Formats a moment with either a DateFormat, through Intl in the current
 * user's locale, or a locale-neutral moment token string.
 * @param {moment.Moment} date
 * @param {DateFormat|string} [format]
 * @return {string} formatted date
 */
export function formatDate(date, format = DEFAULT_FORMAT) {
	if (!(format instanceof DateFormat)) {
		return date.format(format);
	}

	return date.isValid() ? format.format(date) : date.format();
}

export function getCustomDateFormat() {
	return CUSTOM_DATE_FORMAT;
}

export function getCustomDateTimeFormat() {
	return CUSTOM_DATE_TIME_FORMAT;
}

export function getDayMonthFormat() {
	return DAY_MONTH_FORMAT;
}

export function getFullDayMonthFormat() {
	return FULL_DAY_MONTH_FORMAT;
}

export function getMonthYearFormat() {
	return MONTH_YEAR_FORMAT;
}

/**
 * Whether the current locale displays time in 12-hour AM/PM form (en-US)
 * rather than 24-hour form (pt-BR/es-ES/ja-JP).
 */
export const usesTwelveHourClock = perLocale((locale) => {
	const {hourCycle} = new Intl.DateTimeFormat(locale, {
		hour: 'numeric',
	}).resolvedOptions();

	return hourCycle === 'h11' || hourCycle === 'h12';
});

/**
 * A compact hour label for an hour-bucket (e.g. a chart axis tick). 12-hour
 * locales drop the minutes ("6 AM") since the AM/PM marker alone reads as a
 * time; 24-hour locales keep them ("06:00"), since a bare hour number would
 * not read as a time at all.
 */
export function getHourOnlyFormat() {
	return usesTwelveHourClock() ? HOUR_FORMAT : TIME_FORMAT;
}

/**
 * A day+month label followed by the hour (e.g. an "hourly bucket" tooltip:
 * "Aug 9, 2 PM" / "9 ago, 14:30").
 */
const DAY_MONTH_HOUR_FORMAT = new DateFormat(
	(date) =>
		`${formatDate(date, DAY_MONTH_FORMAT)}, ${formatDate(
			date,
			getHourOnlyFormat()
		)}`
);

export function getDayMonthHourFormat() {
	return DAY_MONTH_HOUR_FORMAT;
}

function capitalize(text) {
	return text.charAt(0).toLocaleUpperCase(getLocale()) + text.slice(1);
}

/**
 * Formats a moment relative to the current day, in the current user's
 * locale: "Today at 2:30 PM", "Yesterday at 2:30 PM", "Tomorrow at 2:30 PM",
 * a weekday within the next week, or `sameElseFormat` otherwise. Follows
 * moment's `calendar` thresholds, evaluated in the moment's own offset.
 * @param {moment.Moment} date
 * @param {DateFormat|string} [sameElseFormat]
 */
export function formatCalendar(date, sameElseFormat = CUSTOM_DATE_FORMAT) {
	const startOfToday = moment().utcOffset(date.utcOffset()).startOf('day');

	const days = date.diff(startOfToday, 'days', true);

	const atTime = (dayLabel) =>
		sub(Liferay.Language.get('x-at-x'), [
			dayLabel,
			formatDate(date, TIME_FORMAT),
		]);

	if (days >= -1 && days < 2) {
		return atTime(
			capitalize(
				getAutoRelativeTimeFormat().format(Math.floor(days), 'day')
			)
		);
	}

	if (days >= 2 && days < 7) {
		return atTime(capitalize(formatDate(date, WEEKDAY_FORMAT)));
	}

	return formatDate(date, sameElseFormat);
}

/**
 * Formats a moment as "Today" when it falls on the current day, evaluated in
 * the moment's own offset, or with `format` otherwise.
 * @param {moment.Moment} date
 * @param {DateFormat|string} [format]
 */
export function formatTodayOrDate(date, format = CUSTOM_DATE_FORMAT) {
	return date.isSame(moment().utcOffset(date.utcOffset()), 'day')
		? Liferay.Language.get('today')
		: formatDate(date, format);
}

/**
 * Formats a moment relative to now ("5 minutes ago", "in 2 days"), in the
 * current user's locale, bucketing with moment's `fromNow` thresholds.
 * @param {moment.Moment} date
 */
export function formatRelativeTime(date) {
	const duration = moment.duration(date.diff(moment()));

	const direction = duration.asMilliseconds() < 0 ? -1 : 1;

	const round = (unit) => Math.round(Math.abs(duration.as(unit)));

	const seconds = round('seconds');

	if (seconds < 45) {
		return getAutoRelativeTimeFormat().format(0, 'second');
	}

	const [value, unit] = [
		[round('minutes'), 'minute', 45],
		[round('hours'), 'hour', 22],
		[round('days'), 'day', 26],
		[round('months'), 'month', 11],
		[round('years'), 'year', Infinity],
	].find(([amount, , limit]) => amount < limit);

	return getRelativeTimeFormat().format(direction * Math.max(value, 1), unit);
}

/**
 * The month names in the current user's locale, January first.
 * @param {'long'|'short'} [month]
 */
export function getMonthNames(month = 'long') {
	const format = new Intl.DateTimeFormat(getLocale(), {
		month,
		timeZone: 'UTC',
	});

	return Array.from({length: 12}, (_, index) =>
		format.format(Date.UTC(2026, index, 1))
	);
}

/**
 * The short weekday names in the current user's locale, Sunday first.
 */
export function getShortWeekdayNames() {
	const format = new Intl.DateTimeFormat(getLocale(), {
		timeZone: 'UTC',
		weekday: 'short',
	});

	return Array.from({length: 7}, (_, index) =>
		format.format(Date.UTC(2026, 5, 7 + index))
	);
}

export const WEEKDAYS = [
	Liferay.Language.get('sunday'),
	Liferay.Language.get('monday'),
	Liferay.Language.get('tuesday'),
	Liferay.Language.get('wednesday'),
	Liferay.Language.get('thursday'),
	Liferay.Language.get('friday'),
	Liferay.Language.get('saturday'),
];

export function convertMillisecondsToDays(milliseconds) {
	return Math.round(milliseconds / 1000 / 60 / 60 / 24);
}

export function convertMillisecondsToHours(milliseconds) {
	return Math.round(milliseconds / 1000 / 60 / 60);
}

export function convertMillisecondsToMonths(milliseconds) {
	return Math.round(milliseconds / 1000 / 60 / 60 / 24 / 30);
}

/**
 * Formats a date in UTC
 * @param {number|string|Date|moment.Moment} date
 * @param {DateFormat|string} [format]
 * @param {string|moment.MomentBuiltinFormat} [inputFormatter]
 * @return {string} formatted date
 */
export function formatUTCDate(date, format = DEFAULT_FORMAT, inputFormatter) {
	return formatDate(moment.utc(date, inputFormatter), format);
}

/**
 * @param {number|string} date
 * @param {DateFormat|string} [format]
 * @return {string} formatted date
 */
export function formatUTCDateFromUnix(date, format = DEFAULT_FORMAT) {
	return formatUTCDate(date, format, 'x');
}

/**
 * @param {*} date
 * @param {DateFormat|string} [format]
 * @param {string} [timeZoneId]
 * @return {string} formatted date
 */
export function formatDateToTimeZone(
	date,
	format = DEFAULT_FORMAT,
	timeZoneId = DEFAULT_TIMEZONE_ID
) {
	return formatDate(applyTimeZone(date, timeZoneId), format);
}

export function applyTimeZone(date, timeZoneId = DEFAULT_TIMEZONE_ID) {
	return momentTimezone.utc(date).tz(timeZoneId);
}

export function generateDateRange(period = 30, interval = 'days') {
	return rangeRight(0, period).map((cur) =>
		moment.utc().startOf(interval).subtract(cur, interval).valueOf()
	);
}

/**
 * Get Date
 * @param {string | number} [date]
 */
export function getDate(date) {
	return moment.utc(date).toDate();
}

/**
 * Get ISO Date
 * @param {string} date
 */
export function getISODate(date) {
	return moment.utc(date).toISOString();
}

/**
 * Get Date now.
 * @returns {Moment} Date at time of calling.
 */
export function getDateNow() {
	return moment.utc();
}

export function getDateRangeLabel(dates, interval, key) {
	const firstDate = flow(head, get(key), formatUTCDate)(dates);
	const lastDate = formatUTCDate(getLastDate(dates, interval, key));

	return `${firstDate} - ${lastDate}`;
}

export function getDateRangeLabelFromDate(date, interval) {
	const firstDate = formatUTCDateFromUnix(date);

	if (interval === INTERVAL_KEY_MAP.day) {
		return `${firstDate}`;
	}

	const lastDate = formatUTCDate(getEndDate(date, interval));

	return `${firstDate} - ${lastDate}`;
}

export function getEndDate(date, interval) {
	if (interval === INTERVAL_KEY_MAP.week) {
		return moment.utc(date).add('6', 'days');
	}
	else if (interval === INTERVAL_KEY_MAP.month) {
		return moment.utc(date).endOf('month');
	}

	return date;
}

/**
 *  Gets the first date of the array.
 *  @param {Array.<Aggregation>} aggregations - Array of objects.
 *  @returns {number} Date in unix time.
 */
export function getFirstDate(dates, key) {
	return flow(head, get(key))(dates);
}

/**
 *  Gets the last date of the array.
 *  @param {Array.<Aggregation>} aggregations - Array of objects.
 *  @returns {number} Date in unix time.
 */
export function getLastDate(dates, interval, key) {
	const date = flow(last, get(key))(dates);

	return getEndDate(date, interval);
}

/**
 * Get total days to date
 * @param {object} date
 */
export function getTotalDaysToDate(createDate) {
	const duration = moment.duration({
		from: moment(createDate).clone(),
		to: new Date(),
	});

	return Math.floor(duration.asDays());
}

export function toUnix(stringOrMoment) {
	return moment.utc(stringOrMoment).valueOf() || null;
}
