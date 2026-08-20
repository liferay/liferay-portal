/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	LastRange,
	Range,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter';
import {toDateFilterValues} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/toDateFilterValues';

describe('toDateFilterValues', () => {
	it('defaults to the all range', () => {
		expect(toDateFilterValues({})).toEqual({range: Range.All});

		expect(toDateFilterValues({range: ['all']})).toEqual({
			range: Range.All,
		});
	});

	it('maps the from last publish date range', () => {
		expect(toDateFilterValues({range: ['fromLastPublishDate']})).toEqual({
			range: Range.FromLastPublishDate,
		});
	});

	it('maps the last hours to the matching option', () => {
		expect(toDateFilterValues({last: ['24'], range: ['last']})).toEqual({
			last: LastRange.H24,
			range: Range.Last,
		});

		expect(toDateFilterValues({last: ['168'], range: ['last']})).toEqual({
			last: LastRange.D7,
			range: Range.Last,
		});
	});

	it('maps unmapped last hours to the closest option', () => {
		expect(toDateFilterValues({last: ['11'], range: ['last']})).toEqual({
			last: LastRange.H12,
			range: Range.Last,
		});

		expect(toDateFilterValues({last: ['31'], range: ['last']})).toEqual({
			last: LastRange.H24,
			range: Range.Last,
		});

		expect(toDateFilterValues({last: ['300'], range: ['last']})).toEqual({
			last: LastRange.D7,
			range: Range.Last,
		});
	});

	it('falls back to the all range for unusable last hours', () => {
		expect(toDateFilterValues({range: ['last']})).toEqual({
			range: Range.All,
		});

		expect(toDateFilterValues({last: ['0'], range: ['last']})).toEqual({
			range: Range.All,
		});
	});

	it('rebuilds the date range from the stored calendar fields', () => {
		expect(
			toDateFilterValues({
				endDateAmPm: ['0'],
				endDateDay: ['20'],
				endDateHour: ['9'],
				endDateMinute: ['30'],
				endDateMonth: ['9'],
				endDateYear: ['2026'],
				range: ['dateRange'],
				startDateAmPm: ['1'],
				startDateDay: ['22'],
				startDateHour: ['3'],
				startDateMinute: ['5'],
				startDateMonth: ['7'],
				startDateYear: ['2026'],
			})
		).toEqual({
			endDate: '2026-10-20 09:30',
			range: Range.DateRange,
			startDate: '2026-08-22 15:05',
		});
	});
});
