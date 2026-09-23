import * as data from 'test/data';
import {
	applyTimeZone,
	formatUTCDate,
	formatUTCDateFromUnix,
	generateDateRange,
	getDate,
	getDateNow,
	getDateRangeLabel,
	getDateRangeLabelFromDate,
	getFirstDate,
	getISODate,
	getLastDate,
	toUnix,
} from '../date';

describe('date', () => {
	describe('formatUTCDate', () => {
		it('should convert from the specified format and convert to the specified format', () => {
			expect(formatUTCDate('January 1, 1970', 'll', 'LL')).toBe(
				'Jan 1, 1970'
			);
		});

		it('should convert from ISO_8601 format to the specified format', () => {
			expect(formatUTCDate('1970-01-01', 'll')).toBe('Jan 1, 1970');
		});
	});

	describe('formatUTCDateFromUnix', () => {
		it('should convert and format a timestamp to the default format', () => {
			expect(formatUTCDateFromUnix(1574288551000)).toBe(
				'November 20, 2019'
			);
		});

		it('should convert and format a timestamp to the specified format', () => {
			expect(formatUTCDateFromUnix(1574288551000, 'll')).toBe(
				'Nov 20, 2019'
			);
		});
	});

	describe('generateDateRange', () => {
		it.each`
			period | interval
			${30}  | ${'days'}
			${12}  | ${'months'}
			${3}   | ${'years'}
		`(
			'should generate an array containing timestamps equal to the length of $period and sectioned by $interval',
			({interval, period}) => {
				expect(generateDateRange(period, interval).length).toBe(period);
			}
		);
	});

	describe('getDate', () => {
		const date1 = '2018-04-05T00:00';
		const date2 = '2018-04-05T00:00';

		xit('should return the date1 with timezone from Etc/GMT', () => {
			const date = getDate(date1);

			expect(date).toEqualWithoutType(
				'Thu Apr 05 2018 00:00:00 GMT+0000 (GMT)'
			);
		});

		xit('should return the date2 with timezone from Etc/GMT', () => {
			const date = getDate(date2);

			expect(date).toEqualWithoutType(
				'Thu Apr 05 2018 00:00:00 GMT+0000 (GMT)'
			);
		});
	});

	describe('getDateNow', () => {
		it('should execute without any errors', () => {
			expect(getDateNow()).toBeTruthy();
		});
	});

	describe('getISODate', () => {
		it('should return the date as an ISO String', () => {
			const expected = '2018-07-10T23:01:06.366Z';

			expect(getISODate(data.getTimestamp())).toEqual(expected);
		});
	});

	expect.extend({

		/**
		 * To Equal Without Type
		 * @param {string} received
		 * @param {string} argument
		 */
		toEqualWithoutType(received, argument) {
			const pass = received == argument;
			if (pass) {
				return {
					message: () =>
						`expected ${received} not to equal ${argument}`,
					pass: true,
				};
			}
			else {
				return {
					message: () => `expected ${received} to equal ${argument}`,
					pass: false,
				};
			}
		},
	});

	describe('getDateRangeLabel', () => {
		it('should get the date range label from an array of objects', () => {
			const dates = [
				{intervalInitDate: data.getTimestamp(-2)},
				{intervalInitDate: data.getTimestamp(-1)},
				{intervalInitDate: data.getTimestamp()},
			];

			expect(
				getDateRangeLabel(dates, 'D', 'intervalInitDate')
			).toMatchSnapshot();

			expect(
				getDateRangeLabel(dates, 'W', 'intervalInitDate')
			).toMatchSnapshot();

			expect(
				getDateRangeLabel(dates, 'M', 'intervalInitDate')
			).toMatchSnapshot();
		});
	});

	describe('getDateRangeLabelFromDate', () => {
		it('should get the date range label from a date and interval', () => {
			expect(
				getDateRangeLabelFromDate(data.getTimestamp(), 'D')
			).toMatchSnapshot();

			expect(
				getDateRangeLabelFromDate(data.getTimestamp(), 'W')
			).toMatchSnapshot();

			expect(
				getDateRangeLabelFromDate(data.getTimestamp(), 'M')
			).toMatchSnapshot();
		});
	});

	describe('toUnix', () => {
		it.each`
			date                         | expected
			${'2016-01-01'}              | ${1451606400000}
			${'2016-01-01T12:12:05.400'} | ${1451650325400}
			${''}                        | ${null}
			${'abc'}                     | ${null}
		`('should convert $date to $expected', ({date, expected}) => {
			expect(toUnix(date)).toBe(expected);
		});
	});

	describe('getFirstDate', () => {
		it('should return the date from the first item in the history array', () => {
			const {activityAggregations} = data.mockActivityHistory();

			expect(
				getFirstDate(activityAggregations, 'intervalInitDate')
			).toEqual(data.getTimestamp(-1));
		});
	});

	describe('getLastDate', () => {
		it('should return the date from the last item in the history array', () => {
			const {activityAggregations} = data.mockActivityHistory();

			expect(
				getLastDate(activityAggregations, null, 'intervalInitDate')
			).toEqual(data.getTimestamp());
		});
	});

	describe('applyTimeZone', () => {
		it.each`
			date                         | timeZoneId               | formattedDate
			${'2022-11-11T23:00:00.000'} | ${'America/Los_Angeles'} | ${'2022-11-11T15:00:00-08:00'}
			${'2022-11-11T05:00:00.000'} | ${'America/Los_Angeles'} | ${'2022-11-10T21:00:00-08:00'}
			${'2022-11-11T23:00:00.000'} | ${'America/Recife'}      | ${'2022-11-11T20:00:00-03:00'}
			${'2022-11-11T02:00:00.000'} | ${'America/Recife'}      | ${'2022-11-10T23:00:00-03:00'}
			${'2022-11-11T23:00:00.000'} | ${'UTC'}                 | ${'2022-11-11T23:00:00Z'}
			${'2022-11-11T02:00:00.000'} | ${'UTC'}                 | ${'2022-11-11T02:00:00Z'}
			${'2022-11-11T23:00:00.000'} | ${'Asia/Tokyo'}          | ${'2022-11-12T08:00:00+09:00'}
			${'2022-11-11T02:00:00.000'} | ${'Asia/Tokyo'}          | ${'2022-11-11T11:00:00+09:00'}
		`(
			'should convert $date to $formattedDate, timeZoneId is $timeZoneId',
			({date, formattedDate, timeZoneId}) => {
				expect(applyTimeZone(date, timeZoneId).format()).toBe(
					formattedDate
				);
			}
		);
	});
});
