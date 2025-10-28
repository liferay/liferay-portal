import {align} from './align';
import {
	ALIGNMENTS_MAP,
	POSITIONS,
	RangeKeyTimeRanges
} from 'shared/util/constants';
import {flow, get, isFinite, isNil, isString, toLower, trim} from 'lodash';
import {
	RangeSelectors,
	RawRangeSelectors,
	SafeRangeSelectors
} from 'shared/types';

/**
 * Check if the value is blank.
 * @param {string|number} value
 * @returns {boolean}
 */
export const isBlank = (value: string | number): boolean =>
	isNil(value) || (isString(value) && !value.length);

/**
 * It is deprecated, you should use useQueryRangeSelectors instead.
 * @param query
 * @deprecated
 */
export const getRangeSelectorsFromQuery = query => {
	const rangeEnd = get(query, 'rangeEnd', '');
	const rangeKey = get(query, 'rangeKey', RangeKeyTimeRanges.Last30Days);
	const rangeStart = get(query, 'rangeStart', '');

	return {
		rangeEnd: rangeEnd === 'null' ? null : rangeEnd,
		rangeKey,
		rangeStart: rangeStart === 'null' ? null : rangeStart
	};
};

export const getSafeRangeSelectors = (
	rangeSelectors: RangeSelectors
): SafeRangeSelectors => {
	const rangeEnd = get(rangeSelectors, 'rangeEnd', null);
	const rangeKey = get(
		rangeSelectors,
		'rangeKey',
		RangeKeyTimeRanges.Last30Days
	);
	const rangeStart = get(rangeSelectors, 'rangeStart', null);

	return {
		rangeEnd: rangeEnd || null,
		rangeKey: rangeKey === 'CUSTOM' ? null : parseInt(rangeKey),
		rangeStart: rangeStart || null
	};
};

/**
 * Normalize RangeSelectors
 * @param {RawRangeSelectors}
 * @returns {RangeSelectors}
 */
export const normalizeRangeSelectors = (
	rangeSelectors: RawRangeSelectors
): RangeSelectors => {
	const {rangeEnd, rangeKey, rangeStart} = rangeSelectors;

	if (rangeEnd && rangeStart) {
		return {
			...rangeSelectors,
			rangeKey: RangeKeyTimeRanges.CustomRange
		};
	}

	return {
		rangeEnd: '',
		rangeKey: String(rangeKey) as RangeKeyTimeRanges,
		rangeStart: ''
	};
};

/**
 * Check if the value is blank and returns value.
 * @param {string|number} value
 * @param {string|number} defaultValue
 * @returns {string|number} Returns defaultValue if value is blank.
 */
export const getSafeDisplayValue = (
	value: string | number,
	defaultValue: string | number = '-'
): string | number => (isBlank(value) ? defaultValue : value);

export const getSafeTouchpoint = (touchpoint: string) => {
	if (!touchpoint) return null;

	try {
		const url = new URL(decodeURIComponent(touchpoint));
		const remainingUrl = url.href.replace(url.origin, '');

		return remainingUrl === '/' ? url.origin : url.origin + remainingUrl;
	} catch (e) {
		return touchpoint !== 'Any' ? decodeURIComponent(touchpoint) : null;
	}
};

/**
 * Create a Blob object from data string and temporarily attach
 * an anchor element to the DOM to click on and trigger download.
 */
export const downloadDataAsFile = ({
	data,
	name,
	type
}: {
	data: string;
	name: string;
	type: string;
}) => {
	const blob = new Blob([data], {type});

	const linkUrl = URL.createObjectURL(blob);
	const link = document.createElement('a');
	link.href = linkUrl;
	link.setAttribute('download', name);

	document.body.appendChild(link);

	link.click();

	link.parentNode.removeChild(link);
	URL.revokeObjectURL(linkUrl);
};

/**
 * Remove Protocol
 * @param {string} url
 */
export const removeProtocol = url =>
	decodeURIComponent(url).replace(/^http(s)?:\/\//i, '');

/**
 * Remove numbers using regex
 * @param {string} str
 */
export const removeNumbers = str => str.replace(/\d+/g, ' ');

/**
 * Remove spacing using regex
 * @param {string} str
 */
export const removeSpacing = str => str.replace(/\s+/g, '');

/**
 * Returns the percent number passing as
 * parameter the current number and total number.
 * @param {number} number1
 * @param {number} number2
 * @returns {number}
 */
export const getPercentage = (number1, number2) => {
	const result = (number1 / number2) * 100;

	return isFinite(result) ? result : 0;
};

/**
 * Return the truncate text
 * @param {string} str
 * @param {number} length
 * @param {number} ending
 */
export const truncateText = (str, length, ending) => {
	if (length == null) length = 100;
	if (ending == null) ending = '...';

	return str.length > length
		? str.substring(0, length - ending.length) + ending
		: str;
};

/**
 * Is Ellipsis Active
 * @param {object} event
 */
export const isEllipisActive = ({target}) =>
	target.offsetWidth < target.scrollWidth;

/**
 * Get Align Position
 * @param {string} source
 * @param {string} target
 * @param {string} suggestedPosition
 */
export const getAlignPosition = (source, target, suggestedPosition) => {
	if (!suggestedPosition) {
		suggestedPosition = 'top';
	}

	const position = align(source, target, ALIGNMENTS_MAP[suggestedPosition]);

	return POSITIONS[position];
};

/**
 * Trim and convert value to lowercase.
 * @param {string} value
 * @return {string} Lowercase & trimmed string.
 */
export const formatStringToLowercase: (value: string) => string = flow(
	toLower,
	trim
);

/**
 * Merges multiple refs to be used on one element
 */
export const mergeRef = (...refs) => instance =>
	refs.forEach(ref => {
		if (typeof ref === 'function') {
			ref(instance);
		} else if (ref) {
			ref.current = instance;
		}
	});

export function getInitials(name = '') {
	const nameArray = name.split(' ', 3);

	return nameArray
		.reduce((acc, val) => acc + val.substring(0, 1), '')
		.toUpperCase();
}
