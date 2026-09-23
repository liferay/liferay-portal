import {LanguageIds} from 'shared/util/constants';

export const DEFAULT_LANGUAGE_ID = LanguageIds.English;

export const DEFAULT_LOCALE = 'en-US';

/**
 * Keeps a portal languageId when it is one of the languages the portal
 * makes available, falling back to DEFAULT_LANGUAGE_ID when it is
 * missing or not available. Use this when the consumer needs the portal
 * languageId itself; prefer `resolveLocale`/`getLocale`/`useLocale` for
 * Intl-style formatting.
 */
export function resolveLanguageId(languageId?: string | null): string {
	return languageId && languageId in Liferay.Language.available
		? languageId
		: DEFAULT_LANGUAGE_ID;
}

export function resolveLocale(languageId?: string | null): string {
	return resolveLanguageId(languageId).replace(/_/g, '-');
}

export function getLanguageLabel(languageId?: string | null): string {
	const [language, country] = (languageId || DEFAULT_LANGUAGE_ID).split('_');

	return `${language.toUpperCase()} (${country})`;
}

/**
 * Reverses `resolveLocale`: given a BCP-47 locale, returns the portal
 * languageId it came from (e.g. moment's locale packs, which are keyed
 * by languageId rather than by the Intl-style locale string).
 */
export function localeToLanguageId(locale: string): string {
	return locale.replace(/-/g, '_');
}

let currentLocale: string = DEFAULT_LOCALE;

/**
 * Pushes the current user's resolved locale in from outside (the app
 * bootstrap, on every current-user load/change). Keeping this
 * store-free avoids the import cycle a direct Redux read would create
 * here (`numbers.ts` -> `locale.ts` -> `shared/store` -> `reducers` ->
 * ... -> `numbers.ts`), since nearly every formatter consumer sits
 * somewhere on that cycle.
 */
export function setLocale(locale: string): void {
	currentLocale = locale;
}

/**
 * Reads the locale last pushed in by `setLocale`. Use this outside
 * React components (e.g. numbers.ts formatters that run inside
 * non-hook closures); components should prefer the `useLocale` hook
 * instead, which reacts to Redux state directly.
 */
export function getLocale(): string {
	return currentLocale;
}
