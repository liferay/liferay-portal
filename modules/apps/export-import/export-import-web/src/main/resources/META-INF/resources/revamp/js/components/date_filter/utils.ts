/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {DateFilter} from '../../types/exportImportProcess';
import {
	isCompleteDateTime,
	toDateText,
	toTimeText,
	toWallClockDate,
	toZonedDate,
} from '../../utils/dateTime';
import {DateFilterValues, EditingState, LastRange, Range} from './types';

export const RANGE_OPTIONS = [
	{
		label: Liferay.Language.get('show-all'),
		value: Range.All,
	},
	{
		label: Liferay.Language.get('date-range'),
		value: Range.DateRange,
	},
	{
		label: Liferay.Language.get('modified-last'),
		value: Range.Last,
	},
];

export const HOURS_BY_LAST_RANGE: Record<LastRange, number> = {
	[LastRange.H12]: 12,
	[LastRange.H24]: 24,
	[LastRange.H48]: 48,
	[LastRange.D7]: 7 * 24,
};

export const LAST_RANGE_OPTIONS = [
	{
		label: sub(Liferay.Language.get('x-hours'), '12'),
		value: LastRange.H12,
	},
	{
		label: sub(Liferay.Language.get('x-hours'), '24'),
		value: LastRange.H24,
	},
	{
		label: sub(Liferay.Language.get('x-hours'), '48'),
		value: LastRange.H48,
	},
	{
		label: sub(Liferay.Language.get('x-days'), '7'),
		value: LastRange.D7,
	},
];

export function toDateFilter(
	dateFilterValues: DateFilterValues,
	timeZoneId: string
): DateFilter | undefined {
	if (dateFilterValues.range === Range.DateRange) {
		const {endDate, startDate} = dateFilterValues;

		return {
			endDate: endDate
				? toZonedDate(endDate, timeZoneId).toISOString()
				: undefined,
			startDate: startDate
				? toZonedDate(startDate, timeZoneId).toISOString()
				: undefined,
			type: 'DateRange',
		};
	}

	if (dateFilterValues.range === Range.FromLastPublishDate) {
		return {
			type: 'FromLastPublishDate',
		};
	}

	if (dateFilterValues.range === Range.Last) {
		return {
			hours: HOURS_BY_LAST_RANGE[dateFilterValues.last],
			type: 'LastHours',
		};
	}

	return undefined;
}

export function dateFilterToEditingState(
	dateFilterValues: DateFilterValues
): EditingState {
	const editingState: EditingState = {
		endDate: '',
		last: LastRange.H12,
		range: dateFilterValues.range,
		startDate: '',
	};

	if (dateFilterValues.range === Range.DateRange) {
		editingState.endDate = dateFilterValues.endDate;
		editingState.startDate = dateFilterValues.startDate;
	}
	else if (dateFilterValues.range === Range.Last) {
		editingState.last = dateFilterValues.last;
	}

	return editingState;
}

export function editingToDateFilter(
	editingState: EditingState
): DateFilterValues {
	const {endDate, last, range, startDate} = editingState;

	if (range === Range.DateRange) {
		return {endDate, range: Range.DateRange, startDate};
	}

	if (range === Range.FromLastPublishDate) {
		return {range: Range.FromLastPublishDate};
	}

	if (range === Range.Last) {
		return {last, range: Range.Last};
	}

	return {range: Range.All};
}

export function getAppliedFilterSummary(
	dateFilterValues: DateFilterValues
): string {
	if (dateFilterValues.range === Range.DateRange) {
		const {endDate, startDate} = dateFilterValues;

		if (startDate && endDate) {
			return sub(Liferay.Language.get('date-range-x-to-x'), [
				getDateTimeText(startDate),
				getDateTimeText(endDate),
			]);
		}

		if (startDate) {
			return sub(
				Liferay.Language.get('date-range-after-x'),
				getDateTimeText(startDate)
			);
		}

		if (endDate) {
			return sub(
				Liferay.Language.get('date-range-before-x'),
				getDateTimeText(endDate)
			);
		}
	}

	if (dateFilterValues.range === Range.Last) {
		const lastRangeOption = LAST_RANGE_OPTIONS.find(
			(lastRangeOption) => lastRangeOption.value === dateFilterValues.last
		);

		return `${Liferay.Language.get('modified-last')}: ${
			lastRangeOption?.label ?? ''
		}`;
	}

	if (dateFilterValues.range === Range.FromLastPublishDate) {
		return Liferay.Language.get('from-last-publish-date');
	}

	return '';
}

export function getIsDirty(
	editingState: EditingState,
	dateFilterValues: DateFilterValues
): boolean {
	if (editingState.range !== dateFilterValues.range) {
		return true;
	}

	if (
		dateFilterValues.range === Range.DateRange &&
		editingState.range === Range.DateRange
	) {
		return (
			editingState.startDate !== dateFilterValues.startDate ||
			editingState.endDate !== dateFilterValues.endDate
		);
	}

	if (
		dateFilterValues.range === Range.Last &&
		editingState.range === Range.Last
	) {
		return editingState.last !== dateFilterValues.last;
	}

	return false;
}

export function getValidation(
	editingState: EditingState,
	timeZoneId: string
): {
	errors: {endDate?: string; startDate?: string};
	isValid: boolean;
} {
	const errors: {endDate?: string; startDate?: string} = {};

	if (editingState.range !== Range.DateRange) {
		return {errors, isValid: true};
	}

	const {endDate, startDate} = editingState;

	if (!startDate && !endDate) {
		return {errors, isValid: false};
	}

	const isStartValid = !startDate || isCompleteDateTime(startDate);
	const isEndValid = !endDate || isCompleteDateTime(endDate);

	if (!isStartValid) {
		errors.startDate = Liferay.Language.get('please-enter-a-valid-date');
	}

	if (!isEndValid) {
		errors.endDate = Liferay.Language.get('please-enter-a-valid-date');
	}

	if (!isStartValid || !isEndValid) {
		return {errors, isValid: false};
	}

	const startDateObj = startDate ? toZonedDate(startDate, timeZoneId) : null;
	const endDateObj = endDate ? toZonedDate(endDate, timeZoneId) : null;

	if (startDateObj && startDateObj > new Date()) {
		errors.startDate = Liferay.Language.get(
			'dates-must-not-be-in-the-future'
		);
	}

	if (endDateObj && endDateObj > new Date()) {
		errors.endDate = Liferay.Language.get(
			'dates-must-not-be-in-the-future'
		);
	}

	if (startDateObj && endDateObj && startDateObj >= endDateObj) {
		const rangeError = Liferay.Language.get('date-range-is-invalid');

		errors.startDate = rangeError;
		errors.endDate = rangeError;
	}

	return {
		errors,
		isValid: !Object.keys(errors).length,
	};
}

function getDateTimeText(dateTime: string): string {
	const date = toWallClockDate(dateTime);

	return `${toDateText(date)} ${toTimeText(date)}`;
}
