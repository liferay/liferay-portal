/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DateFilterValues, LastRange, Range} from '../components/date_filter';

const LAST_RANGES: Array<{hours: number; lastRange: LastRange}> = [
	{hours: 12, lastRange: LastRange.H12},
	{hours: 24, lastRange: LastRange.H24},
	{hours: 48, lastRange: LastRange.H48},
	{hours: 168, lastRange: LastRange.D7},
];

function toLastRange(hours: number): LastRange | null {
	if (!(hours > 0)) {
		return null;
	}

	const [closestLastRange] = [...LAST_RANGES].sort(
		(first, second) =>
			Math.abs(first.hours - hours) - Math.abs(second.hours - hours)
	);

	return closestLastRange.lastRange;
}

function toDateTime(
	publishParameters: Record<string, string[]>,
	prefix: string
): string {
	const getNumber = (name: string) =>
		Number(publishParameters[`${prefix}${name}`]?.[0] ?? 0);

	const year = getNumber('Year');

	if (!year) {
		return '';
	}

	const hour = (getNumber('Hour') % 12) + (getNumber('AmPm') ? 12 : 0);

	const pad = (value: number) => String(value).padStart(2, '0');

	return `${year}-${pad(getNumber('Month') + 1)}-${pad(
		getNumber('Day')
	)} ${pad(hour)}:${pad(getNumber('Minute'))}`;
}

export function toDateFilterValues(
	publishParameters: Record<string, string[]>
): DateFilterValues {
	const range = publishParameters.range?.[0];

	if (range === 'dateRange') {
		return {
			endDate: toDateTime(publishParameters, 'endDate'),
			range: Range.DateRange,
			startDate: toDateTime(publishParameters, 'startDate'),
		};
	}

	if (range === 'last') {
		const lastRange = toLastRange(Number(publishParameters.last?.[0]));

		if (lastRange) {
			return {last: lastRange, range: Range.Last};
		}
	}

	if (range === 'fromLastPublishDate') {
		return {range: Range.FromLastPublishDate};
	}

	return {range: Range.All};
}
