/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DateFilterValues,
	HOURS_BY_LAST_RANGE,
	LastRange,
	Range,
} from '../components/date_filter';
import {DateFilter} from '../types/exportImportProcess';
import {toWallClockDateTime} from './dateTime';

function toLastRange(hours: number): LastRange | null {
	if (!(hours > 0)) {
		return null;
	}

	const [closestLastRange] = Object.values(LastRange).sort(
		(first, second) =>
			Math.abs(HOURS_BY_LAST_RANGE[first] - hours) -
			Math.abs(HOURS_BY_LAST_RANGE[second] - hours)
	);

	return closestLastRange;
}

function toDateTime(
	isoDateTime: string | undefined,
	timeZoneId: string
): string {
	if (!isoDateTime || Number.isNaN(Date.parse(isoDateTime))) {
		return '';
	}

	return toWallClockDateTime(isoDateTime, timeZoneId);
}

export function toDateFilterValues(
	dateFilter: DateFilter | undefined,
	timeZoneId: string
): DateFilterValues {
	if (dateFilter?.type === 'DateRange') {
		return {
			endDate: toDateTime(dateFilter.endDate, timeZoneId),
			range: Range.DateRange,
			startDate: toDateTime(dateFilter.startDate, timeZoneId),
		};
	}

	if (dateFilter?.type === 'LastHours') {
		const lastRange = toLastRange(dateFilter.hours);

		if (lastRange) {
			return {last: lastRange, range: Range.Last};
		}
	}

	if (dateFilter?.type === 'FromLastPublishDate') {
		return {range: Range.FromLastPublishDate};
	}

	return {range: Range.All};
}
