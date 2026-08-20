/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isCompleteDateTime, toZonedDate} from './cron';
import {
	IntervalUnit,
	LAST_WEEKDAY_ORDINAL,
	RepeatType,
	ScheduleValues,
	ScheduleValuesErrors,
} from './types';

const FIRST_SUNDAY_OF_JANUARY_2026 = 4;

export const MONTHS = [
	{label: Liferay.Language.get('january'), value: 1},
	{label: Liferay.Language.get('february'), value: 2},
	{label: Liferay.Language.get('march'), value: 3},
	{label: Liferay.Language.get('april'), value: 4},
	{label: Liferay.Language.get('may'), value: 5},
	{label: Liferay.Language.get('june'), value: 6},
	{label: Liferay.Language.get('july'), value: 7},
	{label: Liferay.Language.get('august'), value: 8},
	{label: Liferay.Language.get('september'), value: 9},
	{label: Liferay.Language.get('october'), value: 10},
	{label: Liferay.Language.get('november'), value: 11},
	{label: Liferay.Language.get('december'), value: 12},
];

export const REPEAT_OPTIONS = [
	{label: Liferay.Language.get('never'), value: IntervalUnit.Never},
	{label: Liferay.Language.get('daily'), value: IntervalUnit.Day},
	{label: Liferay.Language.get('weekly'), value: IntervalUnit.Week},
	{label: Liferay.Language.get('monthly'), value: IntervalUnit.Month},
	{label: Liferay.Language.get('yearly'), value: IntervalUnit.Year},
	{label: Liferay.Language.get('custom'), value: IntervalUnit.Custom},
];

export const REPEAT_TYPE_OPTIONS = [
	{label: Liferay.Language.get('day-of-month'), value: RepeatType.DayOfMonth},
	{label: Liferay.Language.get('day-of-week'), value: RepeatType.DayOfWeek},
];

export const WEEKDAY_ORDINAL_OPTIONS = [
	{label: Liferay.Language.get('first'), value: '1'},
	{label: Liferay.Language.get('second'), value: '2'},
	{label: Liferay.Language.get('third'), value: '3'},
	{label: Liferay.Language.get('fourth'), value: '4'},
	{label: Liferay.Language.get('last'), value: LAST_WEEKDAY_ORDINAL},
];

export function getScheduleValuesErrors(
	scheduleValues: ScheduleValues
): ScheduleValuesErrors {
	const scheduleValuesErrors: ScheduleValuesErrors = {};

	if (!scheduleValues.enabled) {
		return scheduleValuesErrors;
	}

	if (!scheduleValues.startDateTime) {
		scheduleValuesErrors.startDateTime = Liferay.Language.get(
			'please-set-a-start-date-and-time-to-schedule-the-publication'
		);
	}
	else if (!isCompleteDateTime(scheduleValues.startDateTime)) {
		scheduleValuesErrors.startDateTime = Liferay.Language.get(
			'please-enter-a-valid-date'
		);
	}
	else if (
		toZonedDate(
			scheduleValues.startDateTime,
			scheduleValues.timeZoneId
		).getTime() < Date.now()
	) {
		scheduleValuesErrors.startDateTime = Liferay.Language.get(
			'the-publish-time-must-be-in-the-future'
		);
	}

	if (
		scheduleValues.unit === IntervalUnit.Custom &&
		!scheduleValues.cronExpression.trim()
	) {
		scheduleValuesErrors.cronExpression = Liferay.Language.get(
			'this-field-is-required'
		);
	}

	if (!scheduleValues.neverEnd) {
		if (!isCompleteDateTime(scheduleValues.endDateTime)) {
			scheduleValuesErrors.endDateTime = Liferay.Language.get(
				'please-enter-a-valid-date'
			);
		}
		else if (
			isCompleteDateTime(scheduleValues.startDateTime) &&
			toZonedDate(
				scheduleValues.endDateTime,
				scheduleValues.timeZoneId
			).getTime() <=
				toZonedDate(
					scheduleValues.startDateTime,
					scheduleValues.timeZoneId
				).getTime()
		) {
			scheduleValuesErrors.endDateTime = Liferay.Language.get(
				'the-end-date-cannot-be-earlier-than-the-start-date'
			);
		}
	}

	return scheduleValuesErrors;
}

export function getInitialScheduleValues(
	timeZoneId: string,
	enabled = false
): ScheduleValues {
	return {
		cronExpression: '',
		enabled,
		endDateTime: '',
		monthDays: [1],
		months: [],
		neverEnd: true,
		repeatType: RepeatType.DayOfMonth,
		startDateTime: '',
		timeZoneId,
		unit: IntervalUnit.Never,
		weekday: 2,
		weekdayOrdinal: '1',
		weekdays: [2],
		yearInterval: 1,
	};
}

export function getIntervalText(
	interval: number,
	unit: IntervalUnit,
	locale: string
): string {
	return new Intl.NumberFormat(locale, {
		style: 'unit',
		unit,
		unitDisplay: 'long',
	} as Intl.NumberFormatOptions).format(interval);
}

export function getWeekdayName(weekday: number, locale: string): string {
	return new Date(
		2026,
		0,
		FIRST_SUNDAY_OF_JANUARY_2026 + weekday - 1
	).toLocaleDateString(locale, {weekday: 'long'});
}
