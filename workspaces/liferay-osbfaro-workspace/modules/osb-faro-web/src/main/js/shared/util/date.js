import 'moment/locale/es';
import 'moment/locale/ja';
import 'moment/locale/pt-br';
import moment from 'moment';
import momentTimezone from 'moment-timezone';
import {flow, get, head, last, rangeRight} from 'lodash/fp';
import {getLocale, localeToLanguageId} from 'shared/util/locale';
import {INTERVAL_KEY_MAP} from 'shared/util/time';
import {LanguageIds} from 'shared/util/constants';

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

export const DEFAULT_FORMAT = 'LL';

export const DEFAULT_LANGUAGE_ID = LanguageIds.English;

export const DEFAULT_TIMEZONE_ID = 'UTC';

const FORMATTED_LANGUAGE_IDS = {
	[LanguageIds.English]: 'en',
	[LanguageIds.Japanese]: 'ja',
	[LanguageIds.Portuguese]: 'pt-br',
	[LanguageIds.Spanish]: 'es',
};

export const ISO_8601_DATE_FORMAT = 'YYYY-MM-DDTHH:mm:ss.SSS[Z]';

// Looks up a format string for the active locale, falling back to
// English. Every `getXFormat` below is one of these lookup tables plus
// this same fallback rule, so they all delegate to it instead of
// repeating `TABLE[moment.locale()] || TABLE.en`.

function getLocaleFormat(formatsByLocale) {
	return formatsByLocale[moment.locale()] || formatsByLocale.en;
}

// Chart axes/tooltips need a "day + month, no year" and a "month +
// year, no day" format, which moment doesn't expose as a named token
// (its named tokens ('ll'/'LL') always include the year). Order and
// connector words are looked up per locale, same pattern as
// FORMATTED_LANGUAGE_IDS above.

const DAY_MONTH_FORMATS = {
	en: 'MMM D',
	es: 'D MMM',
	ja: 'M月D日',
	'pt-br': 'D MMM',
};

const FULL_DAY_MONTH_FORMATS = {
	en: 'MMMM D',
	es: 'D [de] MMMM',
	ja: 'M月D日',
	'pt-br': 'D [de] MMMM',
};

const MONTH_YEAR_FORMATS = {
	en: 'MMM YYYY',
	es: 'MMM [de] YYYY',
	ja: 'YYYY年M月',
	'pt-br': 'MMM [de] YYYY',
};

// The app's general-purpose "readable date" fallback (day + short month
// + year, no time). Deliberately not moment's own 'll' token: 'll'
// spells out the grammatically complete es/pt-br form ("21 de may. de
// 2026"), but the product's compact-date convention (matching the
// acceptance criteria's own "10 Jun 2026" example) drops the "de"
// connectors ("21 may. 2026") — Japanese is unaffected, since its
// correct form has no connector words either way.

const CUSTOM_DATE_FORMATS = {
	en: 'MMM D, YYYY',
	es: 'D MMM YYYY',
	ja: 'YYYY年M月D日',
	'pt-br': 'D MMM YYYY',
};

export function getCustomDateFormat() {
	return getLocaleFormat(CUSTOM_DATE_FORMATS);
}

// Same compact convention as getCustomDateFormat, with the locale-aware
// time appended. Not a plain `${getCustomDateFormat()}, LT` concat: the
// date/time connector itself varies by locale (a Western comma reads
// oddly stitched into Japanese, which conventionally uses a bare space).

const CUSTOM_DATE_TIME_FORMATS = {
	en: 'MMM D, YYYY, LT',
	es: 'D MMM YYYY, LT',
	ja: 'YYYY年M月D日 LT',
	'pt-br': 'D MMM YYYY, LT',
};

export function getCustomDateTimeFormat() {
	return getLocaleFormat(CUSTOM_DATE_TIME_FORMATS);
}

export function getDayMonthFormat() {
	return getLocaleFormat(DAY_MONTH_FORMATS);
}

export function getFullDayMonthFormat() {
	return getLocaleFormat(FULL_DAY_MONTH_FORMATS);
}

export function getMonthYearFormat() {
	return getLocaleFormat(MONTH_YEAR_FORMATS);
}

/**
 * Whether the active locale displays time in 12-hour AM/PM form (en-US)
 * rather than 24-hour form (pt-BR/es-ES/ja-JP), detected from moment's
 * own locale data rather than hardcoded per language.
 */
export function usesTwelveHourClock() {
	return /a/i.test(moment.localeData().longDateFormat('LT'));
}

/**
 * A compact hour label for an hour-bucket (e.g. a chart axis tick or an
 * hourly range description). For 12-hour locales this drops the
 * minutes ("6 AM") since the AM/PM marker alone reads unambiguously as
 * a time; 24-hour locales have no such marker, so a bare hour number
 * ("6") would not read as a time at all — those use moment's full
 * `'LT'` token instead ("06:00"), matching the AC's own 24h example
 * ("14:30").
 */
export function getHourOnlyFormat() {
	return usesTwelveHourClock() ? 'h A' : 'LT';
}

/**
 * A day+month label followed by the hour (e.g. an "hourly bucket"
 * tooltip or range description: "Aug 9, 2 PM" / "9 ago, 14:30").
 */
export function getDayMonthHourFormat() {
	return `${getDayMonthFormat()}, ${getHourOnlyFormat()}`;
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

moment.locale(FORMATTED_LANGUAGE_IDS[DEFAULT_LANGUAGE_ID]);

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
 * Formats unix timestamp to specified moment format
 * @param {number|string|Date} date
 * @param {string|moment.MomentBuiltinFormat} format
 * @param {string|moment.MomentBuiltinFormat} [inputFormatter]
 * @return {string} formatted date
 */
export function formatUTCDate(date, format = DEFAULT_FORMAT, inputFormatter) {
	return moment.utc(date, inputFormatter).format(format);
}

export function formatUTCDateFromUnix(date, format = DEFAULT_FORMAT) {
	return formatUTCDate(date, format, 'x');
}

export function formatDateToTimeZone(
	date,
	format = DEFAULT_FORMAT,
	timeZoneId = DEFAULT_TIMEZONE_ID
) {
	return applyTimeZone(date, timeZoneId).format(format);
}

export function applyTimeZone(
	date,
	timeZoneId = DEFAULT_TIMEZONE_ID,
	languageId = localeToLanguageId(getLocale())
) {
	return momentTimezone
		.utc(date)
		.tz(timeZoneId)
		.locale(FORMATTED_LANGUAGE_IDS[languageId]);
}

/**
 * Updates moment's global locale, so every bare `moment(...)` call
 * (`.calendar()`, `.fromNow()`, `.format('ll')`, etc.) across the app
 * picks up the current user's language instead of the English default
 * set at module load. Called whenever the current user's languageId
 * loads/changes, alongside `setLocale` in `shared/util/locale`.
 * @param {string} languageId
 */
export function setMomentLocale(languageId) {
	moment.locale(FORMATTED_LANGUAGE_IDS[languageId]);
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
