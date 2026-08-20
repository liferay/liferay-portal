/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {dateUtils, sub} from 'frontend-js-web';

import {
	DateFilterValues,
	EditingState,
	LastRange,
	NormalizedDateFilter,
	Range,
} from './types';

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

const MILLISECONDS_BY_LAST_RANGE: Record<LastRange, number> = {
	[LastRange.H12]: 12 * 60 * 60 * 1000,
	[LastRange.H24]: 24 * 60 * 60 * 1000,
	[LastRange.H48]: 48 * 60 * 60 * 1000,
	[LastRange.D7]: 7 * 24 * 60 * 60 * 1000,
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

export function normalizeDateFilter(
	dateFilter: DateFilterValues
): NormalizedDateFilter {
	if (dateFilter.range === Range.DateRange) {
		const {endDate, startDate} = dateFilter;

		return {
			dateRangeType: 'DATE_RANGE',
			endDate: endDate ? new Date(endDate).toISOString() : undefined,
			startDate: startDate
				? new Date(startDate).toISOString()
				: undefined,
		};
	}

	if (dateFilter.range === Range.FromLastPublishDate) {
		return {
			dateRangeType: 'FROM_LAST_PUBLISH_DATE',
		};
	}

	if (dateFilter.range === Range.Last) {
		return {
			dateRangeType: 'LAST',
			startDate: new Date(
				Date.now() - MILLISECONDS_BY_LAST_RANGE[dateFilter.last]
			).toISOString(),
		};
	}

	return {
		dateRangeType: 'ALL',
	};
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
				startDate,
				endDate,
			]);
		}

		if (startDate) {
			return sub(Liferay.Language.get('date-range-after-x'), startDate);
		}

		if (endDate) {
			return sub(Liferay.Language.get('date-range-before-x'), endDate);
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

export function getValidation(editingState: EditingState): {
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

	const isStartValid = !startDate || dateUtils.isValid(startDate);
	const isEndValid = !endDate || dateUtils.isValid(endDate);

	if (!isStartValid || !isEndValid) {
		return {errors, isValid: false};
	}

	const startDateObj = startDate ? new Date(startDate) : null;
	const endDateObj = endDate ? new Date(endDate) : null;

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

	if (startDateObj && endDateObj && startDateObj > endDateObj) {
		const rangeError = Liferay.Language.get('date-range-is-invalid');

		errors.startDate = rangeError;
		errors.endDate = rangeError;
	}

	return {
		errors,
		isValid: !Object.keys(errors).length,
	};
}
