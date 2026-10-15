/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	LastRange,
	Range,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter';
import {toDateFilterValues} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/toDateFilterValues';

const HOUR = 60 * 60 * 1000;

function hoursAgo(hours: number): string {
	return new Date(Date.now() - hours * HOUR).toISOString();
}

describe('toDateFilterValues', () => {
	it('defaults to the all range', () => {
		expect(toDateFilterValues({name: 'Weekly'}, 'UTC')).toEqual({
			range: Range.All,
		});

		expect(
			toDateFilterValues({dateRangeType: 'ALL', name: 'Weekly'}, 'UTC')
		).toEqual({range: Range.All});
	});

	it('maps the from last publish date range', () => {
		expect(
			toDateFilterValues(
				{dateRangeType: 'FROM_LAST_PUBLISH_DATE', name: 'Weekly'},
				'UTC'
			)
		).toEqual({range: Range.FromLastPublishDate});
	});

	it('maps the last hours to the matching option', () => {
		expect(
			toDateFilterValues(
				{
					dateRangeType: 'LAST',
					name: 'Weekly',
					startDate: hoursAgo(24),
				},
				'UTC'
			)
		).toEqual({last: LastRange.H24, range: Range.Last});

		expect(
			toDateFilterValues(
				{
					dateRangeType: 'LAST',
					name: 'Weekly',
					startDate: hoursAgo(168),
				},
				'UTC'
			)
		).toEqual({last: LastRange.D7, range: Range.Last});
	});

	it('maps unmapped last hours to the closest option', () => {
		expect(
			toDateFilterValues(
				{
					dateRangeType: 'LAST',
					name: 'Weekly',
					startDate: hoursAgo(11),
				},
				'UTC'
			)
		).toEqual({last: LastRange.H12, range: Range.Last});

		expect(
			toDateFilterValues(
				{
					dateRangeType: 'LAST',
					name: 'Weekly',
					startDate: hoursAgo(300),
				},
				'UTC'
			)
		).toEqual({last: LastRange.D7, range: Range.Last});
	});

	it('falls back to the all range for an unusable last start date', () => {
		expect(
			toDateFilterValues({dateRangeType: 'LAST', name: 'Weekly'}, 'UTC')
		).toEqual({range: Range.All});

		expect(
			toDateFilterValues(
				{
					dateRangeType: 'LAST',
					name: 'Weekly',
					startDate: new Date().toISOString(),
				},
				'UTC'
			)
		).toEqual({range: Range.All});
	});

	it('rebuilds the date range in the given time zone', () => {
		expect(
			toDateFilterValues(
				{
					dateRangeType: 'DATE_RANGE',
					endDate: '2026-10-20T09:30:00Z',
					name: 'Weekly',
					startDate: '2026-08-22T13:05:00Z',
				},
				'Europe/Madrid'
			)
		).toEqual({
			endDate: '2026-10-20 11:30',
			range: Range.DateRange,
			startDate: '2026-08-22 15:05',
		});
	});

	it('leaves a missing bound of the date range empty', () => {
		expect(
			toDateFilterValues(
				{
					dateRangeType: 'DATE_RANGE',
					endDate: '2026-10-20T09:30:00Z',
					name: 'Weekly',
				},
				'UTC'
			)
		).toEqual({
			endDate: '2026-10-20 09:30',
			range: Range.DateRange,
			startDate: '',
		});
	});

	it('ignores an unreadable date rather than building a broken date', () => {
		expect(
			toDateFilterValues(
				{
					dateRangeType: 'DATE_RANGE',
					name: 'Weekly',
					startDate: 'not-a-date',
				},
				'UTC'
			)
		).toEqual({
			endDate: '',
			range: Range.DateRange,
			startDate: '',
		});
	});
});
