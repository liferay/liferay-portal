import {
	formatDateToTimeZone,
	formatUTCDate,
	formatUTCDateFromUnix,
	getCustomDateFormat,
	getCustomDateTimeFormat,
	getDayMonthFormat,
	getDayMonthHourFormat,
	getFullDayMonthFormat,
	getHourOnlyFormat,
	getMonthYearFormat,
	setMomentLocale,
} from '../date';
import {DEFAULT_LANGUAGE_ID, resolveLocale, setLocale} from '../locale';

const DATE = '2026-06-10T14:30:00.000Z';

const TIME_ZONE_ID = 'America/Recife';

function useLanguage(languageId) {
	setLocale(resolveLocale(languageId));
	setMomentLocale(languageId);
}

describe.each`
	languageId | customDate         | customDateTime             | dayMonth     | fullDayMonth     | monthYear         | hourOnly   | dayMonthHour        | longDate                 | time
	${'en_US'} | ${'Jun 10, 2026'}  | ${'Jun 10, 2026, 2:30 PM'} | ${'Jun 10'}  | ${'June 10'}     | ${'Jun 2026'}     | ${'2 PM'}  | ${'Jun 10, 2 PM'}   | ${'June 10, 2026'}       | ${'2:30 PM'}
	${'es_ES'} | ${'10 jun. 2026'}  | ${'10 jun. 2026, 14:30'}   | ${'10 jun.'} | ${'10 de junio'} | ${'jun. de 2026'} | ${'14:30'} | ${'10 jun., 14:30'} | ${'10 de junio de 2026'} | ${'14:30'}
	${'ja_JP'} | ${'2026年6月10日'} | ${'2026年6月10日 14:30'}   | ${'6月10日'} | ${'6月10日'}     | ${'2026年6月'}    | ${'14:30'} | ${'6月10日, 14:30'} | ${'2026年6月10日'}       | ${'14:30'}
	${'pt_BR'} | ${'10 jun 2026'}   | ${'10 jun 2026, 14:30'}    | ${'10 jun'}  | ${'10 de junho'} | ${'jun de 2026'}  | ${'14:30'} | ${'10 jun, 14:30'}  | ${'10 de junho de 2026'} | ${'14:30'}
`(
	'formatted date text in $languageId',
	({
		customDate,
		customDateTime,
		dayMonth,
		dayMonthHour,
		fullDayMonth,
		hourOnly,
		languageId,
		longDate,
		monthYear,
		time,
	}) => {
		beforeEach(() => {
			useLanguage(languageId);
		});

		afterEach(() => {
			useLanguage(DEFAULT_LANGUAGE_ID);
		});

		it('renders the custom date', () => {
			expect(formatUTCDate(DATE, getCustomDateFormat())).toBe(customDate);
		});

		it('renders the custom date with time', () => {
			expect(formatUTCDate(DATE, getCustomDateTimeFormat())).toBe(
				customDateTime
			);
		});

		it('renders the day and month', () => {
			expect(formatUTCDate(DATE, getDayMonthFormat())).toBe(dayMonth);
		});

		it('renders the day and the spelled-out month', () => {
			expect(formatUTCDate(DATE, getFullDayMonthFormat())).toBe(
				fullDayMonth
			);
		});

		it('renders the month and year', () => {
			expect(formatUTCDate(DATE, getMonthYearFormat())).toBe(monthYear);
		});

		it('renders the hour', () => {
			expect(formatUTCDate(DATE, getHourOnlyFormat())).toBe(hourOnly);
		});

		it('renders the day, month and hour', () => {
			expect(formatUTCDate(DATE, getDayMonthHourFormat())).toBe(
				dayMonthHour
			);
		});

		it('renders the long date by default', () => {
			expect(formatUTCDate(DATE)).toBe(longDate);
		});

		it('renders the long date by default from a unix timestamp', () => {
			expect(formatUTCDateFromUnix(Date.parse(DATE))).toBe(longDate);
		});

		it('renders the time', () => {
			expect(formatUTCDate(DATE, 'LT')).toBe(time);
		});

		it('renders the custom date in a time zone', () => {
			expect(
				formatDateToTimeZone(
					'2026-06-10T01:30:00.000Z',
					getCustomDateFormat(),
					TIME_ZONE_ID
				)
			).toBe(
				formatUTCDate('2026-06-09T12:00:00.000Z', getCustomDateFormat())
			);
		});
	}
);

describe('formatted date text for fixed patterns in en_US', () => {
	it('renders the numeric date with the time', () => {
		expect(formatUTCDate(DATE, 'l - LT')).toBe('6/10/2026 - 2:30 PM');
	});

	it('renders the short numeric date', () => {
		expect(formatDateToTimeZone(DATE, 'M/D/YY', TIME_ZONE_ID)).toBe(
			'6/10/26'
		);
	});

	it('renders the month names', () => {
		expect(formatUTCDate(DATE, 'MMMM')).toBe('June');
		expect(formatUTCDate(DATE, 'MMM')).toBe('Jun');
	});

	it('renders the time in a time zone', () => {
		expect(formatDateToTimeZone(DATE, 'LT', TIME_ZONE_ID)).toBe('11:30 AM');
	});
});
