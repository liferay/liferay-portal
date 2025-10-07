/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FIRST_DAY_OF_WEEK_MAP,
	MONTHS_LONG_MAP,
	SECONDS_IN_DAY,
	SECONDS_IN_HOUR,
	SECONDS_IN_MINUTE,
	SECONDS_IN_MONTH,
	SECONDS_IN_WEEK,
	SECONDS_IN_YEAR,
	WEEKDAYS_SHORT_MAP,
} from './constants';
import {format} from './format';
import {parse} from './parse';

function getFirstDayOfWeek(
	locale = Liferay.ThemeDisplay.getBCP47LanguageId() as keyof typeof FIRST_DAY_OF_WEEK_MAP
): number {
	if (!(locale in FIRST_DAY_OF_WEEK_MAP)) {
		console.warn(`No locale for '${locale}' found. Defaulting to 'en-US'.`);

		locale = 'en-US';
	}

	return FIRST_DAY_OF_WEEK_MAP[locale] ?? 0;
}

function getWeekdaysShort(locale = Liferay.ThemeDisplay.getBCP47LanguageId()) {
	if (locale in WEEKDAYS_SHORT_MAP) {
		return WEEKDAYS_SHORT_MAP[locale as keyof typeof WEEKDAYS_SHORT_MAP];
	}

	const weekdaysShort = Array.from({length: 7}, (_, i) => {
		const date = new Date(2025, 0, i + 5); // 2025-01-05 is a Sunday

		return date.toLocaleDateString(locale, {weekday: 'short'});
	});

	return weekdaysShort;
}

function getMonthsLong(locale = Liferay.ThemeDisplay.getBCP47LanguageId()) {
	if (locale in MONTHS_LONG_MAP) {
		return MONTHS_LONG_MAP[locale as keyof typeof MONTHS_LONG_MAP];
	}

	const weekdaysShort = Array.from({length: 12}, (_, i) => {
		const date = new Date(2025, i);

		return date.toLocaleDateString(locale, {month: 'long'});
	});

	return weekdaysShort;
}

function fromNow(
	date: Date,
	locale = Liferay.ThemeDisplay.getBCP47LanguageId()
) {
	const now = new Date();
	const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

	const relative = new Intl.RelativeTimeFormat(locale, {numeric: 'auto'});

	let value;
	let type: Intl.RelativeTimeFormatUnit;

	if (diffInSeconds < SECONDS_IN_MINUTE) {
		value = -diffInSeconds;
		type = 'second';
	}
	else if (diffInSeconds < SECONDS_IN_HOUR) {
		value = -Math.floor(diffInSeconds / SECONDS_IN_MINUTE);
		type = 'minute';
	}
	else if (diffInSeconds < SECONDS_IN_DAY) {
		value = -Math.floor(diffInSeconds / SECONDS_IN_HOUR);
		type = 'hour';
	}
	else if (diffInSeconds < SECONDS_IN_WEEK) {
		value = -Math.floor(diffInSeconds / SECONDS_IN_DAY);
		type = 'day';
	}
	else if (diffInSeconds < SECONDS_IN_MONTH) {
		value = -Math.floor(diffInSeconds / SECONDS_IN_WEEK);
		type = 'week';
	}
	else if (diffInSeconds < SECONDS_IN_YEAR) {
		value = -Math.floor(diffInSeconds / SECONDS_IN_MONTH);
		type = 'month';
	}
	else {
		value = -Math.floor(diffInSeconds / SECONDS_IN_YEAR);
		type = 'year';
	}

	return relative.format(value, type);
}

function subDays(date: Date, days: number) {
	date.setDate(date.getDate() - days);

	return date;
}

function subMonths(date: Date, months: number) {
	date.setMonth(date.getMonth() - months);

	return date;
}

function isValid(date: any) {
	return !isNaN((date instanceof Date ? date : new Date(date)).getTime());
}

function isSameMonth(date1: Date, date2: Date) {
	const month1 = date1.getMonth();
	const month2 = date2.getMonth();
	const year1 = date1.getFullYear();
	const year2 = date2.getFullYear();

	return year1 === year2 && month1 === month2;
}

function isSameDay(date1: Date, date2: Date) {
	const resetTime = (date: Date) => {
		const newDate = new Date(date);
		newDate.setHours(0, 0, 0, 0);

		return newDate;
	};

	const resetDate1 = resetTime(date1);
	const resetDate2 = resetTime(date2);

	return resetDate1.getTime() === resetDate2.getTime();
}

function toStartOfWeek(date: Date) {
	const newDate = new Date(date);
	const dayOfWeek = newDate.getDay();
	const differenceToSunday = dayOfWeek === 0 ? 0 : dayOfWeek;

	newDate.setDate(newDate.getDate() - differenceToSunday);
	newDate.setHours(0, 0, 0, 0);

	return newDate;
}

function isSameWeek(date1: Date, date2: Date) {
	const startOfWeek1 = toStartOfWeek(date1);
	const startOfWeek2 = toStartOfWeek(date2);

	return startOfWeek1.getTime() === startOfWeek2.getTime();
}

function toEndOfWeek(date: Date) {
	const newDate = new Date(date);
	const dayOfWeek = newDate.getDay();

	const differenceToSaturday = 6 - dayOfWeek;

	newDate.setDate(newDate.getDate() + differenceToSaturday);
	newDate.setHours(23, 59, 59, 999);

	return newDate;
}

function toStartOfYear(date: Date) {
	const newDate = new Date(date);
	newDate.setMonth(0);
	newDate.setDate(1);
	newDate.setHours(0, 0, 0, 0);

	return newDate;
}

function toEndOfYear(date: Date) {
	const newDate = new Date(date);
	newDate.setMonth(11, 31);
	newDate.setHours(23, 59, 59, 999);

	return newDate;
}

function getDaysDiff(startDate: Date, secondDate: Date) {
	const diffInMilliseconds = secondDate.getTime() - startDate.getTime();

	return diffInMilliseconds / (24 * 60 * 60 * 1000);
}

function getYearDiff(startDate: Date, secondDate: Date) {
	return secondDate.getFullYear() - startDate.getFullYear();
}

function getMonthDiff(startDate: Date, secondDate: Date) {
	const yearDiff = getYearDiff(startDate, secondDate);

	let monthsDiff = secondDate.getMonth() - startDate.getMonth();

	if (yearDiff !== 0) {
		monthsDiff += 12 * yearDiff;
	}

	return monthsDiff;
}

export default {
	format,
	fromNow,
	getDaysDiff,
	getFirstDayOfWeek,
	getMonthDiff,
	getMonthsLong,
	getWeekdaysShort,
	getYearDiff,
	isSameDay,
	isSameMonth,
	isSameWeek,
	isValid,
	parse,
	subDays,
	subMonths,
	toEndOfWeek,
	toEndOfYear,
	toStartOfWeek,
	toStartOfYear,
};
