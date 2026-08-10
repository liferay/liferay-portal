import moment from 'moment';
import {
	formatDateRange,
	formatDateWithTimezone,
	formatTimeRange,
	getFilteredItems,
	getSelectedItem,
} from '../utils';
import {RangeKeyTimeRanges} from 'shared/util/constants';

const timeRange = [
	{
		endDate: '2024-01-16T18:25:43.325',
		rangeKey: 0,
		startDate: '2024-01-15T19:00',
	},
	{
		endDate: '2024-01-15T23:59:59.999999999',
		rangeKey: 1,
		startDate: '2024-01-15T00:00',
	},
	{
		endDate: '2024-01-15T23:59:59.999999',
		rangeKey: 7,
		startDate: '2024-01-09T00:00',
	},
	{
		endDate: '2024-01-15T23:59:59.999999',
		rangeKey: 28,
		startDate: '2023-12-19T00:00',
	},
	{
		endDate: '2024-01-15T23:59:59.999999',
		rangeKey: 30,
		startDate: '2023-12-17T00:00',
	},
	{
		endDate: '2024-01-15T23:59:59.999999',
		rangeKey: 90,
		startDate: '2023-10-18T00:00',
	},
	{
		endDate: '2024-01-15T23:59:59.999999',
		rangeKey: 180,
		startDate: '2023-07-20T00:00',
	},
	{
		endDate: '2024-01-15T23:59:59.999999',
		rangeKey: 365,
		startDate: '2023-01-15T23:59:59.999999',
	},
];

describe('formatDateWithTimezone', () => {
	it.each`
		timeZoneId               | result                         | isAfter
		${'Asia/Tokyo'}          | ${'1970-01-01T09:00:00+00:00'} | ${true}
		${'Australia/Sydney'}    | ${'1970-01-01T10:00:00+00:00'} | ${true}
		${'Asia/Shanghai'}       | ${'1970-01-01T08:00:00+00:00'} | ${true}
		${'Asia/Riyadh'}         | ${'1970-01-01T03:00:00+00:00'} | ${true}
		${'Pacific/Midway'}      | ${'1969-12-31T13:00:00+00:00'} | ${false}
		${'America/Los_Angeles'} | ${'1969-12-31T16:00:00+00:00'} | ${false}
		${'America/Vancouver'}   | ${'1969-12-31T16:00:00+00:00'} | ${false}
		${'America/Recife'}      | ${'1969-12-31T21:00:00+00:00'} | ${false}
	`(
		'returns formatted dates for timezone $timeZoneId',
		({isAfter, result, timeZoneId}) => {
			expect(
				formatDateWithTimezone(timeZoneId, moment(0)).format()
			).toEqual(result);

			expect(
				formatDateWithTimezone(timeZoneId, moment(0)).isAfter(moment(0))
			).toBe(isAfter);
		}
	);
});

describe('formatDateRange', () => {
	it('returns formatted dates for timeRange', () => {
		expect(formatTimeRange(timeRange)).toEqual([
			{
				description: 'Jan 15, 7 PM - Jan 16, 6 PM',
				label: 'Last 24 hours',
				value: '0',
			},
			{
				description: 'Jan 15, 12 AM - Jan 15, 11 PM',
				label: 'Yesterday',
				value: '1',
			},
			{description: 'Jan 9 - Jan 15', label: 'Last 7 days', value: '7'},
			{
				description: 'Dec 19 - Jan 15',
				label: 'Last 28 days',
				value: '28',
			},
			{
				description: 'Dec 17 - Jan 15',
				label: 'Last 30 days',
				value: '30',
			},
			{
				description: 'Oct 18 - Jan 15',
				label: 'Last 90 days',
				value: '90',
			},
			{
				description: 'Jul 20 - Jan 15',
				label: 'Last 180 days',
				value: '180',
			},
			{description: 'Jan 15 - Jan 15', label: 'Last Year', value: '365'},
		]);
	});

	it('returns formatted dates for timeRange with custom range', () => {
		expect(
			formatTimeRange([
				{
					endDate: '2024-01-15T23:59:59.999999',
					rangeKey: 'CUSTOM',
					startDate: '2023-01-15T23:59:59.999999',
				},
			])
		).toEqual([
			{description: 'Jan 15 - Jan 15', label: undefined, value: 'CUSTOM'},
		]);
	});
});

describe('formatDateRange', () => {
	it('returns formatted date range for last 24 hours', () => {
		expect(formatDateRange(moment(0), 0)).toEqual('Jan 1, 12 AM');
	});

	it('returns formatted date range for yesterday', () => {
		expect(formatDateRange(moment(0), 1)).toEqual('Jan 1, 12 AM');
	});

	it('returns formatted date range for last 30 days', () => {
		expect(formatDateRange(moment(0), 30)).toEqual('Jan 1');
	});
});

describe('getFilteredItems', () => {
	it('filter items', () => {
		expect(
			getFilteredItems({
				legacy: false,
				rangeKey: RangeKeyTimeRanges.Last30Days,
				retentionPeriod: 13,
				seeMore: false,
				timeRange: formatTimeRange(timeRange),
			})
		).toEqual([
			{
				description: 'Jan 15, 7 PM - Jan 16, 6 PM',
				label: 'Last 24 hours',
				value: '0',
			},

			{description: 'Jan 9 - Jan 15', label: 'Last 7 days', value: '7'},

			{
				description: 'Dec 17 - Jan 15',
				label: 'Last 30 days',
				value: '30',
			},
			{
				description: 'Oct 18 - Jan 15',
				label: 'Last 90 days',
				value: '90',
			},
		]);
	});

	it('get legacy filtered items', () => {
		expect(
			getFilteredItems({
				legacy: true,
				rangeKey: RangeKeyTimeRanges.Last30Days,
				retentionPeriod: 7,
				seeMore: false,
				timeRange: formatTimeRange(timeRange),
			})
		).toEqual([
			{
				description: 'Jan 15, 7 PM - Jan 16, 6 PM',
				label: 'Last 24 hours',
				value: '0',
			},
			{
				description: 'Jan 15, 12 AM - Jan 15, 11 PM',
				label: 'Yesterday',
				value: '1',
			},
			{description: 'Jan 9 - Jan 15', label: 'Last 7 days', value: '7'},
			{
				description: 'Dec 19 - Jan 15',
				label: 'Last 28 days',
				value: '28',
			},
			{
				description: 'Dec 17 - Jan 15',
				label: 'Last 30 days',
				value: '30',
			},
			{
				description: 'Oct 18 - Jan 15',
				label: 'Last 90 days',
				value: '90',
			},
		]);
	});

	it('see more filtered items with 7 months of retention period', () => {
		expect(
			getFilteredItems({
				legacy: false,
				rangeKey: RangeKeyTimeRanges.Last30Days,
				retentionPeriod: 7,
				seeMore: true,
				timeRange: formatTimeRange(timeRange),
			})
		).toEqual([
			{
				description: 'Jan 15, 7 PM - Jan 16, 6 PM',
				label: 'Last 24 hours',
				value: '0',
			},
			{
				description: 'Jan 15, 12 AM - Jan 15, 11 PM',
				label: 'Yesterday',
				value: '1',
			},
			{description: 'Jan 9 - Jan 15', label: 'Last 7 days', value: '7'},
			{
				description: 'Dec 19 - Jan 15',
				label: 'Last 28 days',
				value: '28',
			},
			{
				description: 'Dec 17 - Jan 15',
				label: 'Last 30 days',
				value: '30',
			},
			{
				description: 'Oct 18 - Jan 15',
				label: 'Last 90 days',
				value: '90',
			},
			{
				description: 'Jul 20 - Jan 15',
				label: 'Last 180 days',
				value: '180',
			},
		]);
	});

	it('see more filtered items excludes ranges without a preset label', () => {
		expect(
			getFilteredItems({
				legacy: false,
				rangeKey: RangeKeyTimeRanges.Last30Days,
				retentionPeriod: 13,
				seeMore: true,
				timeRange: formatTimeRange([
					...timeRange,
					{
						endDate: '2024-01-15T23:59:59.999999',
						rangeKey: 'CUSTOM',
						startDate: '2023-01-15T23:59:59.999999',
					},
				]),
			})
		).toEqual([
			{
				description: 'Jan 15, 7 PM - Jan 16, 6 PM',
				label: 'Last 24 hours',
				value: '0',
			},
			{
				description: 'Jan 15, 12 AM - Jan 15, 11 PM',
				label: 'Yesterday',
				value: '1',
			},
			{description: 'Jan 9 - Jan 15', label: 'Last 7 days', value: '7'},
			{
				description: 'Dec 19 - Jan 15',
				label: 'Last 28 days',
				value: '28',
			},
			{
				description: 'Dec 17 - Jan 15',
				label: 'Last 30 days',
				value: '30',
			},
			{
				description: 'Oct 18 - Jan 15',
				label: 'Last 90 days',
				value: '90',
			},
			{
				description: 'Jul 20 - Jan 15',
				label: 'Last 180 days',
				value: '180',
			},
			{description: 'Jan 15 - Jan 15', label: 'Last Year', value: '365'},
		]);
	});

	it('see more filtered items with 13 months of retention period', () => {
		expect(
			getFilteredItems({
				legacy: false,
				rangeKey: RangeKeyTimeRanges.Last30Days,
				retentionPeriod: 13,
				seeMore: true,
				timeRange: formatTimeRange(timeRange),
			})
		).toEqual([
			{
				description: 'Jan 15, 7 PM - Jan 16, 6 PM',
				label: 'Last 24 hours',
				value: '0',
			},
			{
				description: 'Jan 15, 12 AM - Jan 15, 11 PM',
				label: 'Yesterday',
				value: '1',
			},
			{description: 'Jan 9 - Jan 15', label: 'Last 7 days', value: '7'},
			{
				description: 'Dec 19 - Jan 15',
				label: 'Last 28 days',
				value: '28',
			},
			{
				description: 'Dec 17 - Jan 15',
				label: 'Last 30 days',
				value: '30',
			},
			{
				description: 'Oct 18 - Jan 15',
				label: 'Last 90 days',
				value: '90',
			},
			{
				description: 'Jul 20 - Jan 15',
				label: 'Last 180 days',
				value: '180',
			},
			{description: 'Jan 15 - Jan 15', label: 'Last Year', value: '365'},
		]);
	});
});

describe('getSelectedItem', () => {
	it('returns selected item', () => {
		expect(
			getSelectedItem({
				rangeEnd: '2024-01-16T18:25:43.325',
				rangeKey: 30,
				rangeStart: '2024-01-15T19:00',
				timeRange,
			})
		).toEqual({
			endDate: '2024-01-16T18:25:43.325',
			rangeKey: 0,
			startDate: '2024-01-15T19:00',
		});
	});
});
