/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum IntervalUnit {
	Custom = 'custom',
	Day = 'day',
	Month = 'month',
	Never = 'never',
	Week = 'week',
	Year = 'year',
}

export enum RepeatType {
	DayOfMonth = 'day-of-month',
	DayOfWeek = 'day-of-week',
}

export const LAST_WEEKDAY_ORDINAL = 'last';

export const MONTH_DAYS = Array.from({length: 31}, (_, index) => index + 1);

export const WEEKDAYS = [2, 3, 4, 5, 6, 7, 1];

export const YEAR_INTERVALS = Array.from({length: 10}, (_, index) => index + 1);

export type ScheduleValues = {
	cronExpression: string;
	enabled: boolean;
	endDateTime: string;
	monthDays: number[];
	months: number[];
	neverEnd: boolean;
	repeatType: RepeatType;
	startDateTime: string;
	timeZoneId: string;
	unit: IntervalUnit;
	weekday: number;
	weekdayOrdinal: string;
	weekdays: number[];
	yearInterval: number;
};

export type ScheduleValuesErrors = {
	cronExpression?: string;
	endDateTime?: string;
	startDateTime?: string;
};

export type TimeZoneOption = {
	label: string;
	value: string;
};
