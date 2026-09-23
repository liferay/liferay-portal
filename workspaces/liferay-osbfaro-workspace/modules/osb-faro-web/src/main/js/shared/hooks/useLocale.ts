import {resolveLocale} from 'shared/util/locale';
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
