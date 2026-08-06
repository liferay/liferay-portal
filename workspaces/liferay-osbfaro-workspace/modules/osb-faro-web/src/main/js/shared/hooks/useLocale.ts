import {LanguageIds} from 'shared/util/constants';
import {resolveLanguageId, resolveLocale} from 'shared/util/locale';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';

/**
 * Resolves the current user's locale from their account language
 * preference. Backed by useCurrentUser, so it re-renders with the new
 * value whenever the underlying Redux state changes.
 */
export const useLocale = (): string => {
	const {languageId} = useCurrentUser();

	return resolveLocale(languageId);
};

/**
 * Same resolution as useLocale, but returns the portal languageId
 * (e.g. `en_US`) instead of the BCP-47 locale. Use this for consumers
 * that expect a LanguageIds value, such as `applyTimeZone`.
 */
export const useLanguageId = (): LanguageIds => {
	const {languageId} = useCurrentUser();

	return resolveLanguageId(languageId);
};
