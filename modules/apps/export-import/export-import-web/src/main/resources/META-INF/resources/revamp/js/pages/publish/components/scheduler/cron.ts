/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IntervalUnit,
	LAST_WEEKDAY_ORDINAL,
	RepeatType,
	ScheduleValues,
	YEAR_INTERVALS,
} from './types';
import {WEEKDAY_ORDINAL_OPTIONS, getInitialScheduleValues} from './utils';

const DATE_TIME_PATTERN = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/;

const DAY_OF_WEEK_NAMES = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

const MONTH_NAMES = [
	'JAN',
	'FEB',
	'MAR',
	'APR',
	'MAY',
	'JUN',
	'JUL',
	'AUG',
	'SEP',
	'OCT',
	'NOV',
	'DEC',
];

const FIELD_BOUNDS = [
	{maximum: 59, minimum: 0, names: []},
	{maximum: 59, minimum: 0, names: []},
	{maximum: 23, minimum: 0, names: []},
	{maximum: 31, minimum: 1, names: []},
	{maximum: 12, minimum: 1, names: MONTH_NAMES},
	{maximum: 7, minimum: 1, names: DAY_OF_WEEK_NAMES},
	{maximum: 2099, minimum: 1970, names: []},
];

type DateTimeParts = {
	day: number;
	hour: number;
	minute: number;
	month: number;
	year: number;
};

export function toDateTimeParts(dateTime: string): DateTimeParts {
	const [date, time = '00:00'] = dateTime.split(' ');

	const [year, month, day] = date.split('-').map(Number);
	const [hour, minute] = time.split(':').map(Number);

	return {day, hour, minute, month, year};
}

export function isCompleteDateTime(dateTime: string): boolean {
	if (!DATE_TIME_PATTERN.test(dateTime)) {
		return false;
	}

	const {day, hour, minute, month, year} = toDateTimeParts(dateTime);

	const date = new Date(Date.UTC(year, month - 1, day, hour, minute));

	return (
		date.getUTCDate() === day &&
		date.getUTCFullYear() === year &&
		date.getUTCHours() === hour &&
		date.getUTCMinutes() === minute &&
		date.getUTCMonth() === month - 1
	);
}

export function toZonedDate(dateTime: string, timeZoneId: string): Date {
	const wallClockDate = new Date(`${dateTime.replace(' ', 'T')}:00Z`);

	const timeZoneDate = new Date(
		wallClockDate.toLocaleString('en-US', {timeZone: timeZoneId})
	);
	const utcDate = new Date(
		wallClockDate.toLocaleString('en-US', {timeZone: 'UTC'})
	);

	return new Date(
		wallClockDate.getTime() - (timeZoneDate.getTime() - utcDate.getTime())
	);
}

export function toWallClockDateTime(
	isoDateTime: string,
	timeZoneId: string
): string {
	const dateTimeFormatParts = new Intl.DateTimeFormat('en-CA', {
		day: '2-digit',
		hour: '2-digit',
		hourCycle: 'h23',
		minute: '2-digit',
		month: '2-digit',
		timeZone: timeZoneId,
		year: 'numeric',
	} as Intl.DateTimeFormatOptions).formatToParts(new Date(isoDateTime));

	const getPart = (type: string) =>
		dateTimeFormatParts.find(
			(dateTimeFormatPart) => dateTimeFormatPart.type === type
		)?.value ?? '';

	return `${getPart('year')}-${getPart('month')}-${getPart(
		'day'
	)} ${getPart('hour')}:${getPart('minute')}`;
}

function toFieldNumber(value: string, names: string[]): number {
	const nameIndex = names.indexOf(value.toUpperCase());

	if (nameIndex >= 0) {
		return nameIndex + 1;
	}

	return Number(value);
}

function toFieldNumbers(field: string, fieldIndex: number): number[] | null {
	if (field === '*' || field === '?') {
		return [];
	}

	const {maximum, minimum, names} = FIELD_BOUNDS[fieldIndex];

	const numbers = new Set<number>();

	for (const term of field.split(',')) {
		const [range, stepValue] = term.split('/');
		const [startValue, endValue] = range.split('-');

		const start = toFieldNumber(startValue, names);
		const step = stepValue ? Number(stepValue) : 1;

		let end = start;

		if (endValue !== undefined) {
			end = toFieldNumber(endValue, names);
		}
		else if (stepValue) {
			end = maximum;
		}

		if (
			!Number.isInteger(start) ||
			!Number.isInteger(end) ||
			!Number.isInteger(step) ||
			start < minimum ||
			end > maximum ||
			end < start ||
			step < 1
		) {
			return null;
		}

		for (let number = start; number <= end; number += step) {
			numbers.add(number);
		}
	}

	return [...numbers].sort((first, second) => first - second);
}

function toNumberListField(numbers: number[], maximum: number): string {
	if (!numbers.length || numbers.length === maximum) {
		return '*';
	}

	return [...numbers].sort((first, second) => first - second).join(',');
}

function toDayOfWeekExpression(scheduleValues: ScheduleValues): string {
	const dayOfWeekAbbreviation = DAY_OF_WEEK_NAMES[scheduleValues.weekday - 1];

	if (scheduleValues.weekdayOrdinal === LAST_WEEKDAY_ORDINAL) {
		return `${dayOfWeekAbbreviation}L`;
	}

	return `${dayOfWeekAbbreviation}#${scheduleValues.weekdayOrdinal}`;
}

function fromDayOfWeekExpression(
	dayOfWeekExpression: string
): Partial<ScheduleValues> {
	let dayOfWeekAbbreviation = dayOfWeekExpression;
	let weekdayOrdinal = '1';

	if (dayOfWeekExpression.includes('#')) {
		[dayOfWeekAbbreviation, weekdayOrdinal] =
			dayOfWeekExpression.split('#');

		if (
			!WEEKDAY_ORDINAL_OPTIONS.some(({value}) => value === weekdayOrdinal)
		) {
			weekdayOrdinal = '1';
		}
	}
	else if (dayOfWeekExpression.endsWith('L')) {
		dayOfWeekAbbreviation = dayOfWeekExpression.slice(0, -1);
		weekdayOrdinal = LAST_WEEKDAY_ORDINAL;
	}

	return {
		repeatType: RepeatType.DayOfWeek,
		weekday: toFieldNumber(dayOfWeekAbbreviation, DAY_OF_WEEK_NAMES) || 2,
		weekdayOrdinal,
	};
}

function isOrdinalDayOfWeek(dayOfWeek: string): boolean {
	return dayOfWeek.includes('#') || /[A-Z0-9]L$/i.test(dayOfWeek);
}

function toCanonicalCronExpression(cronExpression: string): string | null {
	const fields = cronExpression.trim().toUpperCase().split(/\s+/);

	if (fields.length < 6 || fields.length > 7) {
		return null;
	}

	if (fields.length === 6) {
		fields.push('*');
	}

	return fields.reduce((canonical: string | null, field, fieldIndex) => {
		if (canonical === null) {
			return null;
		}

		if (fieldIndex === 5 && isOrdinalDayOfWeek(field)) {
			return `${canonical} ${field}`;
		}

		const numbers = toFieldNumbers(field, fieldIndex);

		if (numbers === null) {
			return null;
		}

		return `${canonical} ${numbers.join(',') || '*'}`;
	}, '');
}

export function fromCronExpression(
	cronExpression: string,
	startDateTime: string
): Partial<ScheduleValues> {
	const canonicalCronExpression = toCanonicalCronExpression(cronExpression);

	if (canonicalCronExpression === null) {
		return {cronExpression, unit: IntervalUnit.Custom};
	}

	const scheduleValues = fromSupportedCronExpression(cronExpression);

	if (
		toCanonicalCronExpression(
			toCronExpression({
				...getInitialScheduleValues('UTC'),
				startDateTime,
				...scheduleValues,
			})
		) !== canonicalCronExpression
	) {
		return {cronExpression, unit: IntervalUnit.Custom};
	}

	return scheduleValues;
}

function fromSupportedCronExpression(
	cronExpression: string
): Partial<ScheduleValues> {
	const [, , , dayOfMonth, month, dayOfWeek, year = '*'] = cronExpression
		.trim()
		.toUpperCase()
		.split(/\s+/);

	if (year !== '*' && !year.includes('/')) {
		return {unit: IntervalUnit.Never};
	}

	const months = toFieldNumbers(month, 4) ?? [];

	let yearInterval = Number(year.split('/')[1]) || 1;

	if (!YEAR_INTERVALS.includes(yearInterval)) {
		yearInterval = 1;
	}

	if (dayOfWeek !== '?' && dayOfWeek !== '*') {
		if (isOrdinalDayOfWeek(dayOfWeek)) {
			return {
				...fromDayOfWeekExpression(dayOfWeek),
				months,
				unit: year.includes('/')
					? IntervalUnit.Year
					: IntervalUnit.Month,
				yearInterval,
			};
		}

		return {
			unit: IntervalUnit.Week,
			weekdays: toFieldNumbers(dayOfWeek, 5) ?? [2],
		};
	}

	const monthDays = toFieldNumbers(dayOfMonth, 3) ?? [];

	if (year.includes('/')) {
		return {
			monthDays,
			months,
			repeatType: RepeatType.DayOfMonth,
			unit: IntervalUnit.Year,
			yearInterval,
		};
	}

	if (!months.length && !monthDays.length) {
		return {monthDays, months, unit: IntervalUnit.Day};
	}

	return {
		monthDays,
		months,
		repeatType: RepeatType.DayOfMonth,
		unit: IntervalUnit.Month,
	};
}

export function toCronExpression(scheduleValues: ScheduleValues): string {
	if (scheduleValues.unit === IntervalUnit.Custom) {
		return scheduleValues.cronExpression;
	}

	const {day, hour, minute, month, year} = toDateTimeParts(
		scheduleValues.startDateTime
	);

	if (scheduleValues.unit === IntervalUnit.Never) {
		return `0 ${minute} ${hour} ${day} ${month} ? ${year}`;
	}

	if (scheduleValues.unit === IntervalUnit.Week) {
		const days = scheduleValues.weekdays.length
			? scheduleValues.weekdays
			: [2];

		const dayOfWeek = [...days]
			.sort((first, second) => first - second)
			.map((weekday) => DAY_OF_WEEK_NAMES[weekday - 1])
			.join(',');

		return `0 ${minute} ${hour} ? * ${dayOfWeek} *`;
	}

	if (scheduleValues.unit === IntervalUnit.Day) {
		return `0 ${minute} ${hour} * * ? *`;
	}

	if (scheduleValues.unit === IntervalUnit.Year) {
		const month = scheduleValues.months[0] ?? 1;

		const yearField = `${year}/${
			scheduleValues.yearInterval > 0 ? scheduleValues.yearInterval : 1
		}`;

		if (scheduleValues.repeatType === RepeatType.DayOfWeek) {
			return `0 ${minute} ${hour} ? ${month} ${toDayOfWeekExpression(
				scheduleValues
			)} ${yearField}`;
		}

		return `0 ${minute} ${hour} ${scheduleValues.monthDays[0] ?? 1} ${month} ? ${yearField}`;
	}

	const monthsField = toNumberListField(scheduleValues.months, 12);

	if (scheduleValues.repeatType === RepeatType.DayOfWeek) {
		return `0 ${minute} ${hour} ? ${monthsField} ${toDayOfWeekExpression(
			scheduleValues
		)} *`;
	}

	return `0 ${minute} ${hour} ${toNumberListField(
		scheduleValues.monthDays,
		31
	)} ${monthsField} ? *`;
}
