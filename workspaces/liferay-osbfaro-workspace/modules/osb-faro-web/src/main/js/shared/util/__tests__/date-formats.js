import moment from 'moment';
import {
	getCustomDateFormat,
	getCustomDateTimeFormat,
	getDayMonthFormat,
	getFullDayMonthFormat,
	getHourOnlyFormat,
	getMonthYearFormat,
	usesTwelveHourClock,
} from '../date';
import {LanguageIds} from 'shared/util/constants';
import {localeToLanguageId} from 'shared/util/locale';

const DATE = '2026-06-10T14:30:00.000Z';

describe('locale-aware date/time format lookups', () => {
	afterEach(() => {
		moment.locale('en');
	});

	describe('en-US', () => {
		beforeEach(() => {
			moment.locale('en');
		});

		it('usesTwelveHourClock should be true', () => {
			expect(usesTwelveHourClock()).toBe(true);
		});

		it('getHourOnlyFormat should omit minutes', () => {
			expect(moment.utc(DATE).format(getHourOnlyFormat())).toBe('2 PM');
		});

		it('getDayMonthFormat should be month-first', () => {
			expect(moment.utc(DATE).format(getDayMonthFormat())).toBe('Jun 10');
		});

		it('getFullDayMonthFormat should spell out the month', () => {
			expect(moment.utc(DATE).format(getFullDayMonthFormat())).toBe(
				'June 10'
			);
		});

		it('getMonthYearFormat should format correctly', () => {
			expect(moment.utc(DATE).format(getMonthYearFormat())).toBe(
				'Jun 2026'
			);
		});

		it('getCustomDateFormat should format correctly', () => {
			expect(moment.utc(DATE).format(getCustomDateFormat())).toBe(
				'Jun 10, 2026'
			);
		});

		it('getCustomDateTimeFormat should format correctly', () => {
			expect(moment.utc(DATE).format(getCustomDateTimeFormat())).toBe(
				'Jun 10, 2026, 2:30 PM'
			);
		});
	});

	describe('es-ES', () => {
		beforeEach(() => {
			moment.locale('es');
		});

		it('usesTwelveHourClock should be false', () => {
			expect(usesTwelveHourClock()).toBe(false);
		});

		it('getHourOnlyFormat should include minutes, 24-hour', () => {
			expect(moment.utc(DATE).format(getHourOnlyFormat())).toBe('14:30');
		});

		it('getDayMonthFormat should be day-first', () => {
			expect(moment.utc(DATE).format(getDayMonthFormat())).toBe(
				'10 jun.'
			);
		});

		it('getFullDayMonthFormat should use the "de" connector', () => {
			expect(moment.utc(DATE).format(getFullDayMonthFormat())).toBe(
				'10 de junio'
			);
		});

		it('getMonthYearFormat should use the "de" connector', () => {
			expect(moment.utc(DATE).format(getMonthYearFormat())).toBe(
				'jun. de 2026'
			);
		});

		it('getCustomDateFormat should drop "de" connectors (compact convention)', () => {
			expect(moment.utc(DATE).format(getCustomDateFormat())).toBe(
				'10 jun. 2026'
			);
		});

		it('getCustomDateTimeFormat should format correctly', () => {
			expect(moment.utc(DATE).format(getCustomDateTimeFormat())).toBe(
				'10 jun. 2026, 14:30'
			);
		});
	});

	describe('pt-BR', () => {
		beforeEach(() => {
			moment.locale('pt-br');
		});

		it('usesTwelveHourClock should be false', () => {
			expect(usesTwelveHourClock()).toBe(false);
		});

		it('getHourOnlyFormat should include minutes, 24-hour', () => {
			expect(moment.utc(DATE).format(getHourOnlyFormat())).toBe('14:30');
		});

		it('getDayMonthFormat should be day-first', () => {
			expect(moment.utc(DATE).format(getDayMonthFormat())).toBe('10 jun');
		});

		it('getCustomDateFormat should drop "de" connectors (compact convention)', () => {
			expect(moment.utc(DATE).format(getCustomDateFormat())).toBe(
				'10 jun 2026'
			);
		});

		it('getCustomDateTimeFormat should format correctly', () => {
			expect(moment.utc(DATE).format(getCustomDateTimeFormat())).toBe(
				'10 jun 2026, 14:30'
			);
		});
	});

	describe('ja-JP', () => {
		beforeEach(() => {
			moment.locale('ja');
		});

		it('usesTwelveHourClock should be false', () => {
			expect(usesTwelveHourClock()).toBe(false);
		});

		it('getHourOnlyFormat should include minutes, 24-hour', () => {
			expect(moment.utc(DATE).format(getHourOnlyFormat())).toBe('14:30');
		});

		it('getDayMonthFormat should render 月/日 characters', () => {
			expect(moment.utc(DATE).format(getDayMonthFormat())).toBe(
				'6月10日'
			);
		});

		it('getMonthYearFormat should render 年/月 characters', () => {
			expect(moment.utc(DATE).format(getMonthYearFormat())).toBe(
				'2026年6月'
			);
		});

		it('getCustomDateFormat should render 年/月/日 characters', () => {
			expect(moment.utc(DATE).format(getCustomDateFormat())).toBe(
				'2026年6月10日'
			);
		});

		it('getCustomDateTimeFormat should use a bare space, not a comma', () => {
			expect(moment.utc(DATE).format(getCustomDateTimeFormat())).toBe(
				'2026年6月10日 14:30'
			);
		});
	});
});

describe('localeToLanguageId', () => {
	it('should reverse resolveLocale for every supported locale', () => {
		expect(localeToLanguageId('en-US')).toBe(LanguageIds.English);
		expect(localeToLanguageId('ja-JP')).toBe(LanguageIds.Japanese);
		expect(localeToLanguageId('pt-BR')).toBe(LanguageIds.Portuguese);
		expect(localeToLanguageId('es-ES')).toBe(LanguageIds.Spanish);
	});

	it('should fall back to English for an unsupported locale', () => {
		expect(localeToLanguageId('fr-FR')).toBe(LanguageIds.English);
	});
});
