import moment from 'moment';
import momentDurationFormatSetup from 'moment-duration-format';
import {getLocale, SUPPORTED_LOCALES} from 'shared/util/locale';
import {isNumber} from 'lodash';
import {LanguageIds} from 'shared/util/constants';

momentDurationFormatSetup(moment);

/**
 * Remove Zero Precision
 * @param {string} str
 */
const removeZeroPrecision = (str, locale) => {
	const parts = new Intl.NumberFormat(locale).formatToParts(1.1);
	const decimalPart = parts.find((part) => part.type === 'decimal');
	const decimalSeparator = decimalPart ? decimalPart.value : '.';

	const regex = new RegExp(
		`${decimalSeparator.replace('.', '\\.')}(0)+(k|G|M)?$`
	);

	return str.replace(regex, '$2');
};

/**
 * To Rounded Single Precision
 * @param {number} number
 */
export const toRounded = (number, precision = 1, locale = getLocale()) => {
	const formatted = new Intl.NumberFormat(locale, {
		maximumFractionDigits: precision,
		minimumFractionDigits: precision,
		useGrouping: 'always',
	}).format(number);

	return removeZeroPrecision(formatted.trim(), locale);
};

/**
 * To Locale
 * @param {number} number
 * @param {number} maximumFractionDigits
 */
export const toLocale = (
	number,
	locale = getLocale(),
	maximumFractionDigits = 6
) =>
	new Intl.NumberFormat(locale, {
		maximumFractionDigits,
		useGrouping: 'always',
	}).format(number);

const WESTERN_TIERS = [
	{factor: 1e-12, suffix: 'T', threshold: 1e12},
	{factor: 1e-9, suffix: 'B', threshold: 1e9},
	{factor: 1e-6, suffix: 'M', threshold: 1e6},
	{factor: 1e-3, suffix: 'K', threshold: 1e3},
];

// Japanese groups by 4 digits, not 3: 万 (10,000) and 億 (100,000,000).

const JAPANESE_TIERS = [
	{factor: 1e-8, suffix: '億', threshold: 1e8},
	{factor: 1e-4, suffix: '万', threshold: 1e4},
];

const getTier = (number, tiers) =>
	tiers.find(({threshold}) => number >= threshold);

// Truncates rather than rounds, per design: 4.89M displays as 4.8M, not 4.9M.

const formatMantissa = (number, locale) =>
	new Intl.NumberFormat(locale, {
		maximumFractionDigits: 1,
		roundingMode: 'trunc',
		useGrouping: 'always',
	}).format(number);

/**
 * To Thousands
 * Formats the given number to an abbreviated number if the value is
 * greater than or equal to 1000 (10,000 for Japanese). Truncates rather
 * than rounds, e.g. 1,489,000 => 1.4M, not 1.5M.
 * @param {number} number
 * @returns {string}
 */
export const toThousands = (number, locale = getLocale()) =>
	toThousandsBase(number, (factor) => number * factor, locale);

export const toThousandsBase = (number, setFactor, locale = getLocale()) => {
	if (!isNumber(number)) {
		return '';
	}

	const isJapanese = locale === SUPPORTED_LOCALES[LanguageIds.Japanese];
	const tier = getTier(number, isJapanese ? JAPANESE_TIERS : WESTERN_TIERS);

	if (!tier) {
		return formatMantissa(number, locale);
	}

	return `${formatMantissa(setFactor(tier.factor), locale)}${tier.suffix}`;
};

/**
 * To Fixes Point
 * @param {number} number
 */
export const toFixedPoint = (number, locale = getLocale()) => {
	const formatted = new Intl.NumberFormat(locale, {
		maximumFractionDigits: 0,
		useGrouping: true,
	}).format(number);

	return removeZeroPrecision(formatted, locale).trim().toUpperCase();
};

/**
 * To Int
 * @param {string} str
 */
export const toInt = (str) => parseInt(str, 10);

/**
 * To Duration
 * @param {string} time
 * @param {string} measurement
 */
export const toDuration = (
	time,
	format = 'DD[d] hh[h] mm[m] ss[s]',
	measurement = 'milliseconds'
) => {
	if (time === 0) {
		format = 'DD[d] hh[h] mm[m] s[s]';
	}

	return moment.duration(time, measurement).format(format);
};

const multipliers = {
	B: 1000000000,
	K: 1000,
	M: 1000000,
};

/**
 * Undo Thousands
 * @param {string} formatted
 */
export const undoThousands = (formatted) => {
	if (!formatted) {
		return 0;
	}

	const regex = /(\d+((.|,)\d+)?)([a-zA-Z])?/;
	const matches = formatted.match(regex);

	if (!matches) {
		return 0;
	}

	const number = parseFloat(matches[1]);
	const multiplier = matches[4];

	if (multiplier) {
		return number * (multipliers[multiplier] || 1);
	}

	return toInt(number);
};

/**
 * Formats a 0-1 ratio as a locale-aware percentage string, e.g.
 * formatPercentFromRatio(0.073) => '7.3%' (en-US) / '7,3 %' (es-ES).
 * @param {number} ratio
 * @param {number} precision
 */
export const formatPercentFromRatio = (
	ratio,
	precision = 1,
	locale = getLocale()
) =>
	new Intl.NumberFormat(locale, {
		maximumFractionDigits: precision,
		style: 'percent',
		useGrouping: 'always',
	}).format(ratio);

/**
 * Formats a 0-100 value as a locale-aware percentage string, e.g.
 * formatPercent(7.3) => '7.3%' (en-US) / '7,3 %' (es-ES).
 * @param {number} value
 * @param {number} precision
 */
export const formatPercent = (value, precision = 1, locale = getLocale()) =>
	formatPercentFromRatio(value / 100, precision, locale);

/**
 * Formats a number as a locale-aware currency string. Falls back to a
 * plain locale-aware number when no currency code is given.
 * @param {number} value
 * @param {string} [currencyCode]
 */
export const toCurrency = (value, currencyCode, locale = getLocale()) =>
	new Intl.NumberFormat(locale, {
		useGrouping: 'always',
		...(currencyCode
			? {currency: currencyCode, style: 'currency'}
			: undefined),
	}).format(value);

/**
 * Calculates the percentage. Returns null if the percentage is not
 * finite. Callers that need to display it should format the result
 * with `formatPercent`/`formatPercentFromRatio`.
 * @param {number} curVal
 * @param {number} totalVal
 * @return {number|null} percentage
 */
export function getFinitePercent(curVal, totalVal) {
	const percentage = (curVal / totalVal) * 100;

	return isFinite(percentage) ? percentage : null;
}
