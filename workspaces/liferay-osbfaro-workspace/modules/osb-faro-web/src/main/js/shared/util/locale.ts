import {LanguageIds} from 'shared/util/constants';

export const DEFAULT_LANGUAGE_ID = LanguageIds.English;

export const SUPPORTED_LOCALES: Record<LanguageIds, string> = {
	[LanguageIds.English]: 'en-US',
	[LanguageIds.Japanese]: 'ja-JP',
	[LanguageIds.Portuguese]: 'pt-BR',
	[LanguageIds.Spanish]: 'es-ES',
};

export const DEFAULT_LOCALE = SUPPORTED_LOCALES[DEFAULT_LANGUAGE_ID];

/**
 * Clamps a portal languageId to one of the 4 languages the product
 * ships, falling back to DEFAULT_LANGUAGE_ID when it is missing or not
 * one of them. Use this when the consumer needs the portal languageId
 * itself (e.g. `applyTimeZone`'s moment-locale mapping); prefer
 * `resolveLocale`/`getLocale`/`useLocale` for Intl-style formatting.
 */
export function resolveLanguageId(languageId?: string | null): LanguageIds {
	return languageId && SUPPORTED_LOCALES[languageId as LanguageIds]
		? (languageId as LanguageIds)
		: DEFAULT_LANGUAGE_ID;
}

export function resolveLocale(languageId?: string | null): string {
	return SUPPORTED_LOCALES[resolveLanguageId(languageId)];
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
