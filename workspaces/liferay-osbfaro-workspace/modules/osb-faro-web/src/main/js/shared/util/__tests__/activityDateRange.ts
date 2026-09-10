import {getSessionsDateRange} from '../activityDateRange';
import {RangeKeyTimeRanges} from 'shared/util/constants';

const HOUR = 60 * 60 * 1000;

const NOON = Date.UTC(2026, 8, 9, 12);

const ACTIVITY_HISTORY = [
	{intervalInitDate: NOON - HOUR},
	{intervalInitDate: NOON},
	{intervalInitDate: NOON + HOUR},
];

describe('getSessionsDateRange', () => {
	it('scopes the range to the selected hour for Last24Hours', () => {
		expect(
			getSessionsDateRange({
				activityHistory: ACTIVITY_HISTORY,
				interval: 'D',
				rangeSelectors: {
					rangeEnd: null,
					rangeKey: RangeKeyTimeRanges.Last24Hours,
					rangeStart: null,
				},
				selectedPoint: 1,
			})
		).toEqual({
			rangeEnd: '2026-09-09T12:59:59',
			rangeKey: 0,
			rangeStart: '2026-09-09T12:00:00',
		});
	});

	it('scopes the range to the selected hour for Yesterday', () => {
		expect(
			getSessionsDateRange({
				activityHistory: ACTIVITY_HISTORY,
				interval: 'D',
				rangeSelectors: {
					rangeEnd: null,
					rangeKey: RangeKeyTimeRanges.Yesterday,
					rangeStart: null,
				},
				selectedPoint: 1,
			})
		).toEqual({
			rangeEnd: '2026-09-09T12:59:59',
			rangeKey: 1,
			rangeStart: '2026-09-09T12:00:00',
		});
	});

	it('covers the last minute of the selected hour', () => {
		const {rangeEnd} = getSessionsDateRange({
			activityHistory: ACTIVITY_HISTORY,
			interval: 'D',
			rangeSelectors: {
				rangeEnd: null,
				rangeKey: RangeKeyTimeRanges.Last24Hours,
				rangeStart: null,
			},
			selectedPoint: 1,
		});

		expect(new Date(`${rangeEnd}Z`).getTime()).toBeGreaterThan(
			NOON + 59 * 60 * 1000
		);
	});

	it('scopes the range to the selected day for a daily range key', () => {
		expect(
			getSessionsDateRange({
				activityHistory: ACTIVITY_HISTORY,
				interval: 'D',
				rangeSelectors: {
					rangeEnd: null,
					rangeKey: RangeKeyTimeRanges.Last30Days,
					rangeStart: null,
				},
				selectedPoint: 1,
			})
		).toEqual({
			rangeEnd: '2026-09-09',
			rangeKey: 30,
			rangeStart: '2026-09-09',
		});
	});

	it('returns the original range when no point is selected', () => {
		expect(
			getSessionsDateRange({
				activityHistory: ACTIVITY_HISTORY,
				interval: 'D',
				rangeSelectors: {
					rangeEnd: '2026-09-09',
					rangeKey: RangeKeyTimeRanges.Yesterday,
					rangeStart: '2026-09-09',
				},
			})
		).toEqual({
			rangeEnd: '2026-09-09',
			rangeKey: 1,
			rangeStart: '2026-09-09',
		});
	});
});
