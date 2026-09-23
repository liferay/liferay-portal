import moment from 'moment';
import {
	applyTimeZone,
	formatCalendar,
	formatDateToTimeZone,
	formatUTCDate,
	formatRelativeTime,
	formatUTCDateFromUnix,
	getCustomDateFormat,
	getCustomDateTimeFormat,
	getDayMonthFormat,
	getDayMonthHourFormat,
	getFullDayMonthFormat,
	getHourOnlyFormat,
	getMonthNames,
	getMonthYearFormat,
	getShortWeekdayNames,
	MONTH_FORMAT,
	NUMERIC_DATE_FORMAT,
	SHORT_MONTH_FORMAT,
	SHORT_NUMERIC_DATE_FORMAT,
	TIME_FORMAT,
} from '../date';
import {
	DEFAULT_LANGUAGE_ID,
	DEFAULT_LOCALE,
	resolveLocale,
	setLocale,
} from '../locale';

const DATE = '2026-06-10T14:30:00.000Z';

const TIME_ZONE_ID = 'America/Recife';

function useLanguage(languageId) {
	setLocale(resolveLocale(languageId));
}

describe.each`
	languageId | customDate              | customDateTime                 | dayMonth        | fullDayMonth     | monthYear         | hourOnly   | dayMonthHour           | longDate                 | time
	${'en_US'} | ${'Jun 10, 2026'}       | ${'Jun 10, 2026, 2:30 PM'}     | ${'Jun 10'}     | ${'June 10'}     | ${'Jun 2026'}     | ${'2 PM'}  | ${'Jun 10, 2 PM'}      | ${'June 10, 2026'}       | ${'2:30 PM'}
	${'es_ES'} | ${'10 jun 2026'}        | ${'10 jun 2026, 14:30'}        | ${'10 jun'}     | ${'10 de junio'} | ${'jun 2026'}     | ${'14:30'} | ${'10 jun, 14:30'}     | ${'10 de junio de 2026'} | ${'14:30'}
	${'ja_JP'} | ${'2026年6月10日'}      | ${'2026年6月10日 14:30'}       | ${'6月10日'}    | ${'6月10日'}     | ${'2026年6月'}    | ${'14:30'} | ${'6月10日, 14:30'}    | ${'2026年6月10日'}       | ${'14:30'}
	${'pt_BR'} | ${'10 de jun. de 2026'} | ${'10 de jun. de 2026, 14:30'} | ${'10 de jun.'} | ${'10 de junho'} | ${'jun. de 2026'} | ${'14:30'} | ${'10 de jun., 14:30'} | ${'10 de junho de 2026'} | ${'14:30'}
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
			expect(formatUTCDate(DATE, TIME_FORMAT)).toBe(time);
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

describe('formatted date text for fixed formats in en_US', () => {
	it('renders the numeric date', () => {
		expect(formatUTCDate(DATE, NUMERIC_DATE_FORMAT)).toBe('6/10/2026');
	});

	it('renders the short numeric date', () => {
		expect(
			formatDateToTimeZone(DATE, SHORT_NUMERIC_DATE_FORMAT, TIME_ZONE_ID)
		).toBe('6/10/26');
	});

	it('renders the month names', () => {
		expect(formatUTCDate(DATE, MONTH_FORMAT)).toBe('June');
		expect(formatUTCDate(DATE, SHORT_MONTH_FORMAT)).toBe('Jun');
	});

	it('renders the time in a time zone', () => {
		expect(formatDateToTimeZone(DATE, TIME_FORMAT, TIME_ZONE_ID)).toBe(
			'11:30 AM'
		);
	});
});

describe('formatted date text in a language outside the former four', () => {
	beforeEach(() => {
		setLocale('de-DE');
	});

	afterEach(() => {
		setLocale(DEFAULT_LOCALE);
	});

	it('renders the custom date in German', () => {
		expect(formatUTCDate(DATE, getCustomDateFormat())).toBe(
			'10. Juni 2026'
		);
	});

	it('renders the time in 24-hour form', () => {
		expect(formatUTCDate(DATE, getHourOnlyFormat())).toBe('14:30');
	});

	it('renders the month names in German', () => {
		expect(getMonthNames().slice(0, 3)).toEqual([
			'Januar',
			'Februar',
			'März',
		]);
	});
});

describe('formatCalendar', () => {
	beforeEach(() => {
		jest.useFakeTimers().setSystemTime(new Date(DATE));
	});

	afterEach(() => {
		jest.useRealTimers();

		setLocale(DEFAULT_LOCALE);
	});

	it.each`
		date                          | text
		${'2026-06-10T09:00:00.000Z'} | ${'Today at 9:00 AM'}
		${'2026-06-09T09:00:00.000Z'} | ${'Yesterday at 9:00 AM'}
		${'2026-06-11T09:00:00.000Z'} | ${'Tomorrow at 9:00 AM'}
		${'2026-06-13T09:00:00.000Z'} | ${'Saturday at 9:00 AM'}
		${'2026-06-05T09:00:00.000Z'} | ${'Jun 5, 2026'}
		${'2026-07-10T09:00:00.000Z'} | ${'Jul 10, 2026'}
	`('renders $date as "$text"', ({date, text}) => {
		expect(formatCalendar(moment.utc(date))).toBe(text);
	});

	it('evaluates the day in the date time zone', () => {
		expect(
			formatCalendar(
				applyTimeZone('2026-06-10T01:30:00.000Z', TIME_ZONE_ID)
			)
		).toBe('Yesterday at 10:30 PM');
	});

	it('names the day in the current locale', () => {
		setLocale('pt-BR');

		expect(formatCalendar(moment.utc('2026-06-10T09:00:00.000Z'))).toBe(
			'Hoje at 09:00'
		);
	});
});

describe('formatRelativeTime', () => {
	beforeEach(() => {
		jest.useFakeTimers().setSystemTime(new Date(DATE));
	});

	afterEach(() => {
		jest.useRealTimers();

		setLocale(DEFAULT_LOCALE);
	});

	it.each`
		offset                       | text
		${-30 * 1000}                | ${'now'}
		${-60 * 1000}                | ${'1 minute ago'}
		${-5 * 60 * 1000}            | ${'5 minutes ago'}
		${-2 * 60 * 60 * 1000}       | ${'2 hours ago'}
		${-3 * 24 * 60 * 60 * 1000}  | ${'3 days ago'}
		${-60 * 24 * 60 * 60 * 1000} | ${'2 months ago'}
		${3 * 24 * 60 * 60 * 1000}   | ${'in 3 days'}
	`('renders an offset of $offset ms as "$text"', ({offset, text}) => {
		expect(formatRelativeTime(moment.utc(Date.parse(DATE) + offset))).toBe(
			text
		);
	});

	it('renders the relative time in the current locale', () => {
		setLocale('pt-BR');

		expect(
			formatRelativeTime(moment.utc(Date.parse(DATE) - 3 * 86400000))
		).toBe('há 3 dias');
	});
});

describe('getShortWeekdayNames', () => {
	afterEach(() => {
		setLocale(DEFAULT_LOCALE);
	});

	it('names the weekdays from Sunday in the current locale', () => {
		setLocale('ja-JP');

		expect(getShortWeekdayNames()).toEqual([
			'日',
			'月',
			'火',
			'水',
			'木',
			'金',
			'土',
		]);
	});
});
