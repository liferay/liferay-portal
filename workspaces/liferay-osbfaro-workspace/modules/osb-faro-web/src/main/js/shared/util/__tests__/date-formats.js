import moment from 'moment';
import {DEFAULT_LOCALE, setLocale} from 'shared/util/locale';
import {
	formatDate,
	getCustomDateFormat,
	getCustomDateTimeFormat,
	getDayMonthFormat,
	getFullDayMonthFormat,
	getHourOnlyFormat,
	getMonthYearFormat,
	usesTwelveHourClock,
} from '../date';

const DATE = '2026-06-10T14:30:00.000Z';

describe('locale-aware date/time format lookups', () => {
	afterEach(() => {
		setLocale(DEFAULT_LOCALE);
	});

	describe('en-US', () => {
		beforeEach(() => {
			setLocale('en-US');
		});

		it('usesTwelveHourClock should be true', () => {
			expect(usesTwelveHourClock()).toBe(true);
		});

		it('getHourOnlyFormat should omit minutes', () => {
			expect(formatDate(moment.utc(DATE), getHourOnlyFormat())).toBe(
				'2 PM'
			);
		});

		it('getDayMonthFormat should be month-first', () => {
			expect(formatDate(moment.utc(DATE), getDayMonthFormat())).toBe(
				'Jun 10'
			);
		});

		it('getFullDayMonthFormat should spell out the month', () => {
			expect(formatDate(moment.utc(DATE), getFullDayMonthFormat())).toBe(
				'June 10'
			);
		});

		it('getMonthYearFormat should format correctly', () => {
			expect(formatDate(moment.utc(DATE), getMonthYearFormat())).toBe(
				'Jun 2026'
			);
		});

		it('getCustomDateFormat should format correctly', () => {
			expect(formatDate(moment.utc(DATE), getCustomDateFormat())).toBe(
				'Jun 10, 2026'
			);
		});

		it('getCustomDateTimeFormat should format correctly', () => {
			expect(
				formatDate(moment.utc(DATE), getCustomDateTimeFormat())
			).toBe('Jun 10, 2026, 2:30 PM');
		});
	});

	describe('es-ES', () => {
		beforeEach(() => {
			setLocale('es-ES');
		});

		it('usesTwelveHourClock should be false', () => {
			expect(usesTwelveHourClock()).toBe(false);
		});

		it('getHourOnlyFormat should include minutes, 24-hour', () => {
			expect(formatDate(moment.utc(DATE), getHourOnlyFormat())).toBe(
				'14:30'
			);
		});

		it('getDayMonthFormat should be day-first', () => {
			expect(formatDate(moment.utc(DATE), getDayMonthFormat())).toBe(
				'10 jun'
			);
		});

		it('getFullDayMonthFormat should use the "de" connector', () => {
			expect(formatDate(moment.utc(DATE), getFullDayMonthFormat())).toBe(
				'10 de junio'
			);
		});

		it('getMonthYearFormat should use the "de" connector', () => {
			expect(formatDate(moment.utc(DATE), getMonthYearFormat())).toBe(
				'jun 2026'
			);
		});

		it('getCustomDateFormat should drop "de" connectors (compact convention)', () => {
			expect(formatDate(moment.utc(DATE), getCustomDateFormat())).toBe(
				'10 jun 2026'
			);
		});

		it('getCustomDateTimeFormat should format correctly', () => {
			expect(
				formatDate(moment.utc(DATE), getCustomDateTimeFormat())
			).toBe('10 jun 2026, 14:30');
		});
	});

	describe('pt-BR', () => {
		beforeEach(() => {
			setLocale('pt-BR');
		});

		it('usesTwelveHourClock should be false', () => {
			expect(usesTwelveHourClock()).toBe(false);
		});

		it('getHourOnlyFormat should include minutes, 24-hour', () => {
			expect(formatDate(moment.utc(DATE), getHourOnlyFormat())).toBe(
				'14:30'
			);
		});

		it('getDayMonthFormat should be day-first', () => {
			expect(formatDate(moment.utc(DATE), getDayMonthFormat())).toBe(
				'10 de jun.'
			);
		});

		it('getCustomDateFormat should drop "de" connectors (compact convention)', () => {
			expect(formatDate(moment.utc(DATE), getCustomDateFormat())).toBe(
				'10 de jun. de 2026'
			);
		});

		it('getCustomDateTimeFormat should format correctly', () => {
			expect(
				formatDate(moment.utc(DATE), getCustomDateTimeFormat())
			).toBe('10 de jun. de 2026, 14:30');
		});
	});

	describe('ja-JP', () => {
		beforeEach(() => {
			setLocale('ja-JP');
		});

		it('usesTwelveHourClock should be false', () => {
			expect(usesTwelveHourClock()).toBe(false);
		});

		it('getHourOnlyFormat should include minutes, 24-hour', () => {
			expect(formatDate(moment.utc(DATE), getHourOnlyFormat())).toBe(
				'14:30'
			);
		});

		it('getDayMonthFormat should render 月/日 characters', () => {
			expect(formatDate(moment.utc(DATE), getDayMonthFormat())).toBe(
				'6月10日'
			);
		});

		it('getMonthYearFormat should render 年/月 characters', () => {
			expect(formatDate(moment.utc(DATE), getMonthYearFormat())).toBe(
				'2026年6月'
			);
		});

		it('getCustomDateFormat should render 年/月/日 characters', () => {
			expect(formatDate(moment.utc(DATE), getCustomDateFormat())).toBe(
				'2026年6月10日'
			);
		});

		it('getCustomDateTimeFormat should use a bare space, not a comma', () => {
			expect(
				formatDate(moment.utc(DATE), getCustomDateTimeFormat())
			).toBe('2026年6月10日 14:30');
		});
	});
});
