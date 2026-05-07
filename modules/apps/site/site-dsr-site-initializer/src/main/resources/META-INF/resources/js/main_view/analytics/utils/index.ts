/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import moment from 'moment';

import DateRangeAnalyticsFilter from '../components/filters/DateRangeAnalyticsFilter';
import RoomAnalyticsFilter from '../components/filters/RoomAnalyticsFilter';
import UserAnalyticsFilter from '../components/filters/UserAnalyticsFilter';
import {
	AnalyticsFilters,
	DateRangePreset,
	TAnalyticsFilter,
	TAnalyticsFilterValue,
	TDateRangeAnalyticsFilterValue,
	TDateRangePreset,
} from '../types';

export function computePreset(amount: number, unit: any, startOf: any) {
	return {
		from: moment()
			.subtract(amount, unit)
			.startOf(startOf)
			.format('YYYY-MM-DD'),
		to: moment().format('YYYY-MM-DD'),
	} as TDateRangeAnalyticsFilterValue;
}

export const DATE_RANGE_PRESETS: TDateRangePreset = {
	[DateRangePreset.ALL_TIME]: null,
	[DateRangePreset.CUSTOM_RANGE]: null,
	[DateRangePreset.LAST_WEEK]: computePreset(1, 'week', 'day'),
	[DateRangePreset.LAST_2_WEEKS]: computePreset(2, 'week', 'day'),
	[DateRangePreset.LAST_30_DAYS]: computePreset(30, 'day', 'day'),
	[DateRangePreset.LAST_3_MONTHS]: computePreset(3, 'month', 'day'),
	[DateRangePreset.LAST_6_MONTHS]: computePreset(6, 'week', 'day'),
	[DateRangePreset.LAST_YEAR]: computePreset(1, 'year', 'day'),
};

const DEFAULT_FILTERS: TAnalyticsFilter = {
	[AnalyticsFilters.DATE_RANGE]: {
		active: true,
		component: DateRangeAnalyticsFilter,
		value: {
			preset: DateRangePreset.LAST_WEEK,
			...computePreset(1, 'week', 'day'),
		},
	},
	[AnalyticsFilters.USER]: {
		active: true,
		component: UserAnalyticsFilter,
		value: [],
	},
	[AnalyticsFilters.ROOM]: {
		active: true,
		component: RoomAnalyticsFilter,
		value: {
			room: null,
		},
	},
};

export function toFilters(filtersJSONString: string | null): TAnalyticsFilter {
	if (!filtersJSONString) {
		return DEFAULT_FILTERS;
	}

	try {
		const filterValues: TAnalyticsFilterValue =
			JSON.parse(filtersJSONString);

		return Object.entries(filterValues).reduce(
			(filters, [key, valueObj]) => ({
				...filters,
				[key]: {
					...DEFAULT_FILTERS[key as AnalyticsFilters],
					...valueObj,
				},
			}),
			DEFAULT_FILTERS
		) as TAnalyticsFilter;
	}
	catch (_ignore) {
		console.error('Unable to parse filters.');
	}

	return DEFAULT_FILTERS;
}

export function toStoredFilters(
	filters: TAnalyticsFilter
): TAnalyticsFilterValue {
	return Object.entries(filters).reduce((storedFilters, [key, {value}]) => {
		return {
			...storedFilters,
			[key]: {value},
		};
	}, {} as TAnalyticsFilterValue);
}
