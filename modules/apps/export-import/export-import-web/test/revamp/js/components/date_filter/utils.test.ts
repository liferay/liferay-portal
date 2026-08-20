/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	LastRange,
	Range,
} from '../../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter/types';
import {
	dateFilterToEditingState,
	normalizeDateFilter,
} from '../../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter/utils';

describe('normalizeDateFilter', () => {
	it('returns the all type for the show all range', () => {
		expect(normalizeDateFilter({range: Range.All})).toEqual({
			dateRangeType: 'ALL',
		});
	});

	it('returns the absolute dates for the date range', () => {
		const endDate = '2000-07-28T00:00:00';
		const startDate = '2000-07-27T00:00:00';

		expect(
			normalizeDateFilter({endDate, range: Range.DateRange, startDate})
		).toEqual({
			dateRangeType: 'DATE_RANGE',
			endDate: new Date(endDate).toISOString(),
			startDate: new Date(startDate).toISOString(),
		});
	});

	it('returns only the start date for an open-ended date range', () => {
		const startDate = '2000-07-27T00:00:00';

		expect(
			normalizeDateFilter({
				endDate: '',
				range: Range.DateRange,
				startDate,
			})
		).toEqual({
			dateRangeType: 'DATE_RANGE',
			startDate: new Date(startDate).toISOString(),
		});
	});

	it('returns only the end date for an open-ended date range', () => {
		const endDate = '2000-07-27T00:00:00';

		expect(
			normalizeDateFilter({
				endDate,
				range: Range.DateRange,
				startDate: '',
			})
		).toEqual({
			dateRangeType: 'DATE_RANGE',
			endDate: new Date(endDate).toISOString(),
		});
	});

	it('returns the from last publish date type without dates', () => {
		expect(normalizeDateFilter({range: Range.FromLastPublishDate})).toEqual(
			{
				dateRangeType: 'FROM_LAST_PUBLISH_DATE',
			}
		);
	});

	it('resolves the modified last range to a start date only', () => {
		const beforeTime = Date.now();

		const normalizedDateFilter = normalizeDateFilter({
			last: LastRange.H24,
			range: Range.Last,
		});

		const afterTime = Date.now();

		const dayMilliseconds = 24 * 60 * 60 * 1000;

		const startTime = new Date(normalizedDateFilter.startDate!).getTime();

		expect(normalizedDateFilter.dateRangeType).toBe('LAST');
		expect(normalizedDateFilter.endDate).toBeUndefined();
		expect(startTime).toBeGreaterThanOrEqual(beforeTime - dayMilliseconds);
		expect(startTime).toBeLessThanOrEqual(afterTime - dayMilliseconds);
	});
});

describe('dateFilterToEditingState', () => {
	it('carries the applied range into the editing fields', () => {
		expect(
			dateFilterToEditingState({
				endDate: '2026-10-20 09:30',
				range: Range.DateRange,
				startDate: '2026-08-22 15:05',
			})
		).toEqual({
			endDate: '2026-10-20 09:30',
			last: LastRange.H12,
			range: Range.DateRange,
			startDate: '2026-08-22 15:05',
		});

		expect(
			dateFilterToEditingState({last: LastRange.D7, range: Range.Last})
		).toEqual({
			endDate: '',
			last: LastRange.D7,
			range: Range.Last,
			startDate: '',
		});
	});
});
