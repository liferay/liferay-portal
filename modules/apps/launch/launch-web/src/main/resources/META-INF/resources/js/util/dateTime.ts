/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {dateUtils} from 'frontend-js-web';

import type ClayDatePicker from '@clayui/date-picker';
import type {FirstDayOfWeekLocale} from 'frontend-js-web';
import type {ComponentProps} from 'react';

export interface TimeZoneOption {
	label: string;
	value: string;
}

type DateTimeConfig = Required<
	Pick<
		ComponentProps<typeof ClayDatePicker>,
		| 'dateFormat'
		| 'firstDayOfWeek'
		| 'min'
		| 'months'
		| 'placeholder'
		| 'use12Hours'
		| 'weekdaysShort'
		| 'years'
	>
>;

interface DateTimeParts {
	day: number;
	hour: number;
	minute: number;
	month: number;
	year: number;
}

const OFFSET_SAMPLE_DISTANCE = 36 * 60 * 60 * 1000;

export function formatDateTime(
	timestamp: number,
	locale: string,
	timeZoneId: string
): string {
	return new Intl.DateTimeFormat(locale, {
		day: 'numeric',
		hour: 'numeric',
		minute: 'numeric',
		month: 'short',
		second: 'numeric',
		timeZone: timeZoneId,
		year: 'numeric',
	}).format(new Date(timestamp));
}

export function formatUTCOffset(timestamp: number, timeZoneId: string): string {
	return `UTC ${getLongOffset(timestamp, timeZoneId)}`;
}

export function getDateTimeConfig(
	locale: string,
	timeZoneId: string
): DateTimeConfig {
	const currentYear = new Date().getFullYear();
	const dateFormat = getLocaleDateFormat(locale);
	const use12Hours = is12HourLocale(locale);

	return {
		dateFormat,
		firstDayOfWeek: dateUtils.getFirstDayOfWeek(
			locale as FirstDayOfWeekLocale
		),
		min: toDisplayDateTime(
			getCurrentWallClockDateTime(timeZoneId),
			dateFormat,
			use12Hours
		),
		months: dateUtils.getMonthsLong(locale),
		placeholder: `${dateFormat} ${
			use12Hours ? 'hh:mm AM' : 'HH:mm'
		}`.toUpperCase(),
		use12Hours,
		weekdaysShort: dateUtils.getWeekdaysShort(locale),
		years: {end: currentYear + 10, start: currentYear},
	};
}

export function getLocalTime({
	locale,
	selectedTimeZoneId,
	timeZoneId,
	timestamp,
}: {
	locale: string;
	selectedTimeZoneId: string;
	timeZoneId: string;
	timestamp: number;
}): string | null {
	if (
		getTimeZoneOffset(timestamp, selectedTimeZoneId) ===
		getTimeZoneOffset(timestamp, timeZoneId)
	) {
		return null;
	}

	const [date] = toWallClockDateTime(timestamp, timeZoneId).split(' ');
	const [selectedDate] = toWallClockDateTime(
		timestamp,
		selectedTimeZoneId
	).split(' ');

	const localTime = new Intl.DateTimeFormat(locale, {
		dateStyle: date === selectedDate ? undefined : 'medium',
		timeStyle: 'short',
		timeZone: timeZoneId,
	}).format(new Date(timestamp));

	return `${localTime} (${formatUTCOffset(timestamp, timeZoneId)})`;
}

export function toTimestamp(
	dateTime: string,
	dateFormat: string,
	timeZoneId: string
): number | null {
	const dateTimeParts = parseDateTime(dateTime, dateFormat);

	if (!dateTimeParts) {
		return null;
	}

	return toZonedTimestamp(dateTimeParts, timeZoneId);
}

function getCurrentWallClockDateTime(timeZoneId: string): string {
	return toWallClockDateTime(Date.now(), timeZoneId);
}

function getLocaleDateFormat(locale: string): string {
	return new Intl.DateTimeFormat(locale, {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
	})
		.formatToParts(new Date(2024, 10, 22))
		.map((part) => {
			if (part.type === 'day') {
				return 'dd';
			}

			if (part.type === 'month') {
				return 'MM';
			}

			if (part.type === 'year') {
				return 'yyyy';
			}

			if (part.type === 'literal') {
				return part.value.replace(/[؜‎‏]/g, '').replace(/\s/g, ' ');
			}

			return '';
		})
		.join('');
}

function getLongOffset(timestamp: number, timeZoneId: string): string {
	const offset = new Intl.DateTimeFormat('en-US', {
		timeZone: timeZoneId,
		timeZoneName: 'longOffset',
	})
		.formatToParts(new Date(timestamp))
		.find(
			(dateTimeFormatPart) => dateTimeFormatPart.type === 'timeZoneName'
		)?.value;

	return offset?.replace('GMT', '') || '+00:00';
}

function getTimeZoneOffset(timestamp: number, timeZoneId: string): number {
	const match = getLongOffset(timestamp, timeZoneId).match(
		/^([+-])(\d{2}):(\d{2})$/
	);

	if (!match) {
		return 0;
	}

	const [, sign, hours, minutes] = match;

	return (
		(sign === '-' ? -1 : 1) * (Number(hours) * 60 + Number(minutes)) * 60000
	);
}

function is12HourLocale(locale: string): boolean {
	return (
		new Intl.DateTimeFormat(locale, {
			hour: 'numeric',
		}).resolvedOptions().hour12 === true
	);
}

function parseDateTime(
	dateTime: string,
	dateFormat: string
): DateTimeParts | null {
	const match = dateTime
		.trim()
		.match(/^(.+?)\s+(\d{1,2}):(\d{2})(?:\s*(AM|PM))?$/i);

	if (!match) {
		return null;
	}

	const [, dateText, hourText, minuteText, period] = match;

	const tokens = dateFormat.match(/dd|MM|yyyy/g);
	const values = dateText.match(/\d+/g);

	if (
		!tokens ||
		!values ||
		values.length !== tokens.length ||
		values[tokens.indexOf('yyyy')].length !== 4
	) {
		return null;
	}

	const getValue = (token: string) => Number(values[tokens.indexOf(token)]);

	const day = getValue('dd');
	const month = getValue('MM');
	const year = getValue('yyyy');

	let hour = Number(hourText);
	const minute = Number(minuteText);

	if (period) {
		if (hour < 1 || hour > 12) {
			return null;
		}

		hour = (hour % 12) + (period.toUpperCase() === 'PM' ? 12 : 0);
	}

	const date = new Date(Date.UTC(year, month - 1, day));

	if (
		date.getUTCDate() !== day ||
		date.getUTCFullYear() !== year ||
		date.getUTCMonth() !== month - 1 ||
		hour > 23 ||
		minute > 59
	) {
		return null;
	}

	return {day, hour, minute, month, year};
}

function to12HourTime(time: string): string {
	const [hour, minute] = time.split(':');

	const hour12 = String(Number(hour) % 12 || 12).padStart(2, '0');

	return `${hour12}:${minute} ${Number(hour) < 12 ? 'AM' : 'PM'}`;
}

function toDisplayDateTime(
	dateTime: string,
	dateFormat: string,
	use12Hours: boolean
): string {
	const [date, time] = dateTime.split(' ');
	const [year, month, day] = date.split('-');

	const displayDate = dateFormat
		.replace('dd', day)
		.replace('MM', month)
		.replace('yyyy', year);

	return `${displayDate} ${use12Hours ? to12HourTime(time) : time}`;
}

function toWallClockDateTime(timestamp: number, timeZoneId: string): string {
	const dateTimeFormatParts = new Intl.DateTimeFormat('en-CA', {
		day: '2-digit',
		hour: '2-digit',
		hourCycle: 'h23',
		minute: '2-digit',
		month: '2-digit',
		timeZone: timeZoneId,
		year: 'numeric',
	}).formatToParts(new Date(timestamp));

	const getPart = (type: string) =>
		dateTimeFormatParts.find(
			(dateTimeFormatPart) => dateTimeFormatPart.type === type
		)?.value ?? '';

	return `${getPart('year').padStart(4, '0')}-${getPart('month')}-${getPart(
		'day'
	)} ${getPart('hour')}:${getPart('minute')}`;
}

function toZonedTimestamp(
	{day, hour, minute, month, year}: DateTimeParts,
	timeZoneId: string
): number {
	const wallClockTime = Date.UTC(year, month - 1, day, hour, minute);

	const timestamps = [
		wallClockTime - OFFSET_SAMPLE_DISTANCE,
		wallClockTime + OFFSET_SAMPLE_DISTANCE,
	].map(
		(sampleTime) =>
			wallClockTime - getTimeZoneOffset(sampleTime, timeZoneId)
	);

	const matchingTimestamps = timestamps.filter(
		(timestamp) =>
			getTimeZoneOffset(timestamp, timeZoneId) ===
			wallClockTime - timestamp
	);

	return matchingTimestamps.length
		? Math.min(...matchingTimestamps)
		: timestamps[0];
}
