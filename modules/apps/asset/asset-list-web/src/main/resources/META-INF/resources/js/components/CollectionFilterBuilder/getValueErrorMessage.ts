/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {format, isValid, parse} from 'date-fns';

import normalizeDateTime from './normalizeDateTime';
import {RELATIVE_DATE_VALUES} from './operators';

import type {FilterCondition, FilterProperty} from './types';

function isValidDate(value: string, isDateTime: boolean) {
	const dateFormat = isDateTime ? 'yyyy-MM-dd HH:mm' : 'yyyy-MM-dd';

	const date = parse(value, dateFormat, new Date());

	// Formatting the parsed date back rejects values that parse loosely, such
	// as single digit months.

	return isValid(date) && format(date, dateFormat) === value;
}

function isIncompleteRange([from, to]: string[]) {
	return Boolean(from) !== Boolean(to);
}

function getDateValueError(
	property: FilterProperty,
	operator: string | undefined,
	value: FilterCondition['value']
): string | null {
	const isDateTime = property.type === 'date-time';

	const values = (Array.isArray(value) ? value : [value]) as string[];

	const isValidValue = (entry: string) =>
		!entry ||
		RELATIVE_DATE_VALUES.includes(entry) ||
		isValidDate(normalizeDateTime(entry), isDateTime);

	if (!values.every(isValidValue)) {
		return Liferay.Language.get('please-enter-a-valid-date');
	}

	if (operator === 'between') {
		if (isIncompleteRange(values)) {
			return Liferay.Language.get('please-enter-a-valid-range');
		}

		const [from, to] = values.map(normalizeDateTime);

		if (from && to && from > to) {
			return Liferay.Language.get(
				'date-range-is-invalid.-from-must-be-before-to'
			);
		}
	}

	return null;
}

function getNumericValueError(
	property: FilterProperty,
	operator: string | undefined,
	value: FilterCondition['value']
): string | null {
	const values = (Array.isArray(value) ? value : [value]) as string[];

	if (
		property.type === 'integer' &&
		values.some((entry) => entry && !Number.isInteger(Number(entry)))
	) {
		return Liferay.Language.get('value-must-be-an-integer-value');
	}

	if (operator !== 'between') {
		return null;
	}

	const [from, to] = values;

	if (isIncompleteRange(values) || Number(from) > Number(to)) {
		return Liferay.Language.get('please-enter-a-valid-range');
	}

	return null;
}

export default function getValueErrorMessage(
	property: FilterProperty | undefined,
	operator: string | undefined,
	value: FilterCondition['value']
): string | null {
	if (property?.type === 'date' || property?.type === 'date-time') {
		return getDateValueError(property, operator, value);
	}

	if (
		property?.type === 'decimal' ||
		property?.type === 'integer' ||
		property?.type === 'numeric'
	) {
		return getNumericValueError(property, operator, value);
	}

	return null;
}
