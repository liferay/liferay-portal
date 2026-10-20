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
	getAppliedFilterSummary,
	getValidation,
	toDateFilter,
} from '../../../../../src/main/resources/META-INF/resources/revamp/js/components/date_filter/utils';
import {toWallClockDateTime} from '../../../../../src/main/resources/META-INF/resources/revamp/js/utils/dateTime';

const HOUR = 60 * 60 * 1000;

describe('toDateFilter', () => {
	it('returns no date filter for the show all range', () => {
		expect(toDateFilter({range: Range.All}, 'UTC')).toBeUndefined();
	});

	it('resolves the date range bounds in the given time zone', () => {
		expect(
			toDateFilter(
				{
					endDate: '2026-10-20 09:30',
					range: Range.DateRange,
					startDate: '2026-08-22 15:05',
				},
				'Europe/Madrid'
			)
		).toEqual({
			endDate: '2026-10-20T07:30:00.000Z',
			startDate: '2026-08-22T13:05:00.000Z',
			type: 'DateRange',
		});
	});

	it('returns only the start date for an open-ended date range', () => {
		expect(
			toDateFilter(
				{
					endDate: '',
					range: Range.DateRange,
					startDate: '2026-08-22 15:05',
				},
				'Europe/Madrid'
			)
		).toEqual({
			startDate: '2026-08-22T13:05:00.000Z',
			type: 'DateRange',
		});
	});

	it('returns only the end date for an open-ended date range', () => {
		expect(
			toDateFilter(
				{
					endDate: '2026-10-20 09:30',
					range: Range.DateRange,
					startDate: '',
				},
				'Europe/Madrid'
			)
		).toEqual({
			endDate: '2026-10-20T07:30:00.000Z',
			type: 'DateRange',
		});
	});

	it('returns the from last publish date type without dates', () => {
		expect(toDateFilter({range: Range.FromLastPublishDate}, 'UTC')).toEqual(
			{
				type: 'FromLastPublishDate',
			}
		);
	});

	it('resolves the modified last range to its hours', () => {
		expect(
			toDateFilter({last: LastRange.D7, range: Range.Last}, 'UTC')
		).toEqual({hours: 168, type: 'LastHours'});
	});
});

describe('getValidation', () => {
	const toDateRangeEditingState = (startDate: string) => ({
		endDate: '',
		last: LastRange.H12,
		range: Range.DateRange,
		startDate,
	});

	it('rejects a start bound in the future, which the From calendar never fills in', () => {
		expect(
			getValidation(
				toDateRangeEditingState(
					toWallClockDateTime(
						new Date(Date.now() + HOUR).toISOString(),
						'UTC'
					)
				),
				'UTC'
			)
		).toEqual({
			errors: {startDate: 'dates-must-not-be-in-the-future'},
			isValid: false,
		});
	});

	it('accepts a bound that only reads as future outside the given time zone', () => {
		expect(
			getValidation(
				toDateRangeEditingState(
					toWallClockDateTime(
						new Date(Date.now() + 13 * HOUR).toISOString(),
						'UTC'
					)
				),
				'Pacific/Kiritimati'
			)
		).toEqual({errors: {}, isValid: true});
	});

	it('explains why a malformed bound is rejected', () => {
		expect(
			getValidation(toDateRangeEditingState('2026-01-01 8:00'), 'UTC')
		).toEqual({
			errors: {startDate: 'please-enter-a-valid-date'},
			isValid: false,
		});
	});

	it('rejects an end bound whose time is still ahead of now', () => {
		expect(
			getValidation(
				{
					endDate: toWallClockDateTime(
						new Date(Date.now() + HOUR).toISOString(),
						'UTC'
					),
					last: LastRange.H12,
					range: Range.DateRange,
					startDate: '',
				},
				'UTC'
			)
		).toEqual({
			errors: {endDate: 'dates-must-not-be-in-the-future'},
			isValid: false,
		});
	});

	it('accepts an end bound that has already passed', () => {
		expect(
			getValidation(
				{
					endDate: toWallClockDateTime(
						new Date(Date.now() - HOUR).toISOString(),
						'UTC'
					),
					last: LastRange.H12,
					range: Range.DateRange,
					startDate: '',
				},
				'UTC'
			)
		).toEqual({errors: {}, isValid: true});
	});

	it('rejects an end bound that falls after today', () => {
		const [tomorrow] = toWallClockDateTime(
			new Date(Date.now() + 24 * HOUR).toISOString(),
			'UTC'
		).split(' ');

		expect(
			getValidation(
				{
					endDate: `${tomorrow} 00:00`,
					last: LastRange.H12,
					range: Range.DateRange,
					startDate: '',
				},
				'UTC'
			)
		).toEqual({
			errors: {endDate: 'dates-must-not-be-in-the-future'},
			isValid: false,
		});
	});

	it('rejects a range whose bounds are equal', () => {
		expect(
			getValidation(
				{
					endDate: '2026-01-01 08:00',
					last: LastRange.H12,
					range: Range.DateRange,
					startDate: '2026-01-01 08:00',
				},
				'UTC'
			)
		).toEqual({
			errors: {
				endDate: 'date-range-is-invalid',
				startDate: 'date-range-is-invalid',
			},
			isValid: false,
		});
	});

	it('flags only the malformed bound of a range', () => {
		expect(
			getValidation(
				{
					endDate: '2026-01-01 8:00',
					last: LastRange.H12,
					range: Range.DateRange,
					startDate: '2026-01-01 08:00',
				},
				'UTC'
			)
		).toEqual({
			errors: {endDate: 'please-enter-a-valid-date'},
			isValid: false,
		});
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

describe('getAppliedFilterSummary', () => {
	const LANGUAGE_KEYS: Record<string, string> = {
		'date-range-after-x': 'Date Range: After {0}',
		'date-range-before-x': 'Date Range: Before {0}',
		'date-range-x-to-x': 'Date Range: {0} to {1}',
	};

	const getLanguageKey = Liferay.Language.get as jest.Mock;

	const defaultLanguageKeyImplementation =
		getLanguageKey.getMockImplementation();

	beforeEach(() => {
		getLanguageKey.mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	afterEach(() => {
		getLanguageKey.mockImplementation(defaultLanguageKeyImplementation);
	});

	it('formats both bounds of a date range in the portal locale', () => {
		expect(
			getAppliedFilterSummary({
				endDate: '2026-10-20 09:30',
				range: Range.DateRange,
				startDate: '2026-08-22 15:05',
			})
		).toBe('Date Range: 8/22/2026 3:05 PM to 10/20/2026 9:30 AM');
	});

	it('formats the single bound of an open-ended date range in the portal locale', () => {
		expect(
			getAppliedFilterSummary({
				endDate: '',
				range: Range.DateRange,
				startDate: '2026-08-22 15:05',
			})
		).toBe('Date Range: After 8/22/2026 3:05 PM');

		expect(
			getAppliedFilterSummary({
				endDate: '2026-10-20 09:30',
				range: Range.DateRange,
				startDate: '',
			})
		).toBe('Date Range: Before 10/20/2026 9:30 AM');
	});
});
