import {
	DEFAULT_LANGUAGE_ID,
	DEFAULT_LOCALE,
	getLanguageLabel,
	getLocale,
	resolveLanguageId,
	resolveLocale,
	setLocale,
} from '../locale';
import {LanguageIds} from 'shared/util/constants';

function makeAvailable(languages: Record<string, string>) {
	jest.replaceProperty(Liferay.Language, 'available', {
		...Liferay.Language.available,
		...languages,
	});
}

afterEach(() => {
	jest.restoreAllMocks();
});

describe('resolveLanguageId', () => {
	it.each([
		LanguageIds.English,
		LanguageIds.Japanese,
		LanguageIds.Portuguese,
		LanguageIds.Spanish,
	])('keeps %s unchanged', (languageId) => {
		expect(resolveLanguageId(languageId)).toBe(languageId);
	});

	it('keeps any language the portal makes available', () => {
		makeAvailable({de_DE: 'Deutsch (Deutschland)'});

		expect(resolveLanguageId('de_DE')).toBe('de_DE');
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

	it.each([
		['de_DE', 'de-DE'],
		['fr_CA', 'fr-CA'],
		['zh_TW', 'zh-TW'],
	])('resolves the available language %s to %s', (languageId, locale) => {
		makeAvailable({[languageId]: languageId});

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

describe('getLanguageLabel', () => {
	it('compacts a portal languageId', () => {
		expect(getLanguageLabel('en_US')).toBe('EN (US)');
		expect(getLanguageLabel('pt_BR')).toBe('PT (BR)');
	});

	it('labels a language the product does not format for', () => {
		expect(getLanguageLabel('de_DE')).toBe('DE (DE)');
	});

	it('falls back to the default language when there is none', () => {
		expect(getLanguageLabel(null)).toBe('EN (US)');
	});
});
