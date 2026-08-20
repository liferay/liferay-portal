/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {isCompleteDateTime, toDateTimeParts, toZonedDate} from './cron';
import {
	IntervalUnit,
	MONTH_DAYS,
	RepeatType,
	ScheduleValues,
	WEEKDAYS,
} from './types';
import {
	MONTHS,
	WEEKDAY_ORDINAL_OPTIONS,
	getIntervalText,
	getWeekdayName,
} from './utils';

function toLocalDate(dateTime: string): Date {
	const {day, hour, minute, month, year} = toDateTimeParts(dateTime);

	return new Date(year, month - 1, day, hour, minute);
}

function getListText(labels: string[], locale: string): string {

	// @ts-ignore

	if (typeof Intl.ListFormat === 'function') {

		// @ts-ignore

		return new Intl.ListFormat(locale, {
			style: 'long',
			type: 'conjunction',
		}).format(labels);
	}

	return labels.join(', ');
}

function getWeekdayListText(weekdays: number[], locale: string): string {
	return getListText(
		WEEKDAYS.filter((weekday) => weekdays.includes(weekday)).map(
			(weekday) => getWeekdayName(weekday, locale)
		),
		locale
	);
}

function getWeekdayOrdinalText(
	scheduleValues: ScheduleValues,
	locale: string
): string {
	const weekdayOrdinalOption = WEEKDAY_ORDINAL_OPTIONS.find(
		({value}) => value === scheduleValues.weekdayOrdinal
	);

	return `${weekdayOrdinalOption?.label ?? scheduleValues.weekdayOrdinal} ${getWeekdayName(scheduleValues.weekday, locale)}`;
}

function getMonthListText(months: number[], locale: string): string {
	return getListText(
		[...months]
			.sort((first, second) => first - second)
			.map((month) => MONTHS[month - 1].label),
		locale
	);
}

function toMonthDayRanges(monthDays: number[]): string[] {
	const sortedMonthDays = [...monthDays].sort(
		(first, second) => first - second
	);

	return sortedMonthDays
		.reduce((ranges: number[][], monthDay) => {
			const range = ranges[ranges.length - 1];

			if (range && monthDay === range[range.length - 1] + 1) {
				range.push(monthDay);
			}
			else {
				ranges.push([monthDay]);
			}

			return ranges;
		}, [])
		.flatMap((range) =>
			range.length > 2
				? [`${range[0]}-${range[range.length - 1]}`]
				: range.map(String)
		);
}

function getMonthDayListText(monthDays: number[], locale: string): string {
	if (monthDays.length === 1) {
		return sub(Liferay.Language.get('day-x'), String(monthDays[0]));
	}

	return sub(
		Liferay.Language.get('days-x'),
		getListText(toMonthDayRanges(monthDays), locale)
	);
}

function getUnitText(scheduleValues: ScheduleValues, locale: string): string {
	if (scheduleValues.unit === IntervalUnit.Day) {
		return Liferay.Language.get('day');
	}

	if (scheduleValues.unit === IntervalUnit.Week) {
		return Liferay.Language.get('week');
	}

	if (scheduleValues.unit === IntervalUnit.Month) {
		return Liferay.Language.get('month');
	}

	if (scheduleValues.yearInterval > 1) {
		return getIntervalText(
			scheduleValues.yearInterval,
			IntervalUnit.Year,
			locale
		);
	}

	return Liferay.Language.get('year');
}

function getMonthlyRepeatText(
	scheduleValues: ScheduleValues,
	locale: string
): string {
	const everyMonth =
		!scheduleValues.months.length ||
		scheduleValues.months.length === MONTHS.length;

	const monthListText = getMonthListText(scheduleValues.months, locale);
	const unitText = getUnitText(scheduleValues, locale);

	if (scheduleValues.repeatType === RepeatType.DayOfWeek) {
		const weekdayOrdinalText = getWeekdayOrdinalText(
			scheduleValues,
			locale
		);

		if (everyMonth) {
			return sub(
				Liferay.Language.get('the-process-repeats-every-x-on-the-x'),
				unitText,
				weekdayOrdinalText
			);
		}

		return sub(
			Liferay.Language.get('the-process-repeats-in-x-on-the-x'),
			monthListText,
			weekdayOrdinalText
		);
	}

	if (
		!scheduleValues.monthDays.length ||
		scheduleValues.monthDays.length === MONTH_DAYS.length
	) {
		if (everyMonth) {
			return sub(
				Liferay.Language.get('the-process-repeats-every-x'),
				unitText
			);
		}

		return sub(
			Liferay.Language.get('the-process-repeats-in-x'),
			monthListText
		);
	}

	const monthDayListText = getMonthDayListText(
		scheduleValues.monthDays,
		locale
	);

	if (everyMonth) {
		return sub(
			Liferay.Language.get('the-process-repeats-every-x-on-x'),
			unitText,
			monthDayListText
		);
	}

	return sub(
		Liferay.Language.get('the-process-repeats-in-x-on-x'),
		monthListText,
		monthDayListText
	);
}

function getYearlyRepeatText(
	scheduleValues: ScheduleValues,
	locale: string
): string {
	const monthListText = getMonthListText(
		scheduleValues.months.length ? scheduleValues.months : [1],
		locale
	);
	const unitText = getUnitText(scheduleValues, locale);

	if (scheduleValues.repeatType === RepeatType.DayOfWeek) {
		return sub(
			Liferay.Language.get('the-process-repeats-every-x-in-x-on-the-x'),
			unitText,
			monthListText,
			getWeekdayOrdinalText(scheduleValues, locale)
		);
	}

	return sub(
		Liferay.Language.get('the-process-repeats-every-x-in-x-on-x'),
		unitText,
		monthListText,
		getMonthDayListText(
			scheduleValues.monthDays.length ? scheduleValues.monthDays : [1],
			locale
		)
	);
}

function getRepeatText(scheduleValues: ScheduleValues, locale: string): string {
	if (scheduleValues.unit === IntervalUnit.Day) {
		return sub(
			Liferay.Language.get('the-process-repeats-every-x'),
			getUnitText(scheduleValues, locale)
		);
	}

	if (scheduleValues.unit === IntervalUnit.Week) {
		return sub(
			Liferay.Language.get('the-process-repeats-every-x-on-x'),
			getUnitText(scheduleValues, locale),
			getWeekdayListText(scheduleValues.weekdays, locale)
		);
	}

	if (scheduleValues.unit === IntervalUnit.Year) {
		return getYearlyRepeatText(scheduleValues, locale);
	}

	return getMonthlyRepeatText(scheduleValues, locale);
}

export function getScheduleSummary(
	scheduleValues: ScheduleValues
): string | null {
	if (!scheduleValues.enabled || !scheduleValues.startDateTime) {
		return null;
	}

	if (!isCompleteDateTime(scheduleValues.startDateTime)) {
		return null;
	}

	const startDate = toLocalDate(scheduleValues.startDateTime);

	if (
		toZonedDate(
			scheduleValues.startDateTime,
			scheduleValues.timeZoneId
		).getTime() < Date.now()
	) {
		return null;
	}

	const locale = Liferay.ThemeDisplay.getBCP47LanguageId();

	const startDateText = startDate.toLocaleDateString(locale);
	const timeText = startDate.toLocaleTimeString(locale, {
		hour: 'numeric',
		minute: '2-digit',
	});

	if (scheduleValues.unit === IntervalUnit.Never) {
		return sub(
			Liferay.Language.get(
				'the-process-runs-once-on-x-at-x-and-does-not-repeat'
			),
			startDateText,
			timeText
		);
	}

	const endDate =
		!scheduleValues.neverEnd &&
		isCompleteDateTime(scheduleValues.endDateTime)
			? toLocalDate(scheduleValues.endDateTime)
			: null;

	const startsText = endDate
		? sub(
				Liferay.Language.get(
					'the-process-starts-on-x-at-x-and-ends-on-x-at-x'
				),
				startDateText,
				timeText,
				endDate.toLocaleDateString(locale),
				endDate.toLocaleTimeString(locale, {
					hour: 'numeric',
					minute: '2-digit',
				})
			)
		: sub(
				Liferay.Language.get(
					'the-process-starts-on-x-at-x-and-never-ends'
				),
				startDateText,
				timeText
			);

	if (scheduleValues.unit === IntervalUnit.Custom) {
		return startsText;
	}

	return `${getRepeatText(scheduleValues, locale)} ${startsText}`;
}
