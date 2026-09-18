import {
	DEFAULT_LANGUAGE_ID,
	DEFAULT_LOCALE,
	getLanguageDisplayName,
	getLanguageLabel,
	getLocale,
	resolveLanguageId,
	resolveLocale,
	setLocale,
} from '../locale';
import {LanguageIds} from 'shared/util/constants';

describe('resolveLanguageId', () => {
	it.each([
		LanguageIds.English,
		LanguageIds.Japanese,
		LanguageIds.Portuguese,
		LanguageIds.Spanish,
	])('keeps %s unchanged', (languageId) => {
		expect(resolveLanguageId(languageId)).toBe(languageId);
	});

	it.each([null, undefined, '', 'de_DE', 'not-a-real-language'])(
		'falls back to the default language id for %p',
		(languageId) => {
			expect(resolveLanguageId(languageId)).toBe(DEFAULT_LANGUAGE_ID);
		}
	);
});

describe('resolveLocale', () => {
	it.each([
		[LanguageIds.English, 'en-US'],
		[LanguageIds.Japanese, 'ja-JP'],
		[LanguageIds.Portuguese, 'pt-BR'],
		[LanguageIds.Spanish, 'es-ES'],
	])('resolves %s to %s', (languageId, locale) => {
		expect(resolveLocale(languageId)).toBe(locale);
	});

	it.each([null, undefined, '', 'de_DE', 'not-a-real-language'])(
		'falls back to the default locale for %p',
		(languageId) => {
			expect(resolveLocale(languageId)).toBe(DEFAULT_LOCALE);
		}
	);
});

describe('getLocale/setLocale', () => {
	afterEach(() => {
		setLocale(DEFAULT_LOCALE);
	});

	it('defaults to the default locale before setLocale is ever called', () => {
		expect(getLocale()).toBe(DEFAULT_LOCALE);
	});

	it('returns whatever locale was last pushed in via setLocale', () => {
		setLocale('pt-BR');

		expect(getLocale()).toBe('pt-BR');

		setLocale('ja-JP');

		expect(getLocale()).toBe('ja-JP');
	});
});

describe('getLanguageDisplayName', () => {
	it.each([
		[LanguageIds.English, 'English'],
		[LanguageIds.Japanese, '日本語'],
		[LanguageIds.Portuguese, 'Português'],
		[LanguageIds.Spanish, 'Español'],
	])('names %s in its own language', (languageId, displayName) => {
		expect(getLanguageDisplayName(languageId)).toBe(displayName);
	});

	it.each([null, undefined, '', 'de_DE'])(
		'falls back to the default language for %p',
		(languageId) => {
			expect(getLanguageDisplayName(languageId)).toBe(
				getLanguageDisplayName(DEFAULT_LANGUAGE_ID)
			);
		}
	);
});

describe('getLanguageLabel', () => {
	it('compacts a portal languageId', () => {
		expect(getLanguageLabel('en_US')).toBe('EN (US)');
		expect(getLanguageLabel('pt_BR')).toBe('PT (BR)');
	});

	it('falls back to the default language when there is none', () => {
		expect(getLanguageLabel(null)).toBe('EN (US)');
	});
});
