import {useUnsavedChangesPrompt} from 'shared/hooks/useUnsavedChangesPrompt';

const NavigationWarning = ({
	message = Liferay.Language.get(
		'you-have-unsaved-changes-that-will-be-discarded-by-navigating-away-from-this-page.-do-you-want-to-leave-and-discard-your-changes'
	),
	when
}) => {
	useUnsavedChangesPrompt(when, message);

	return null;
};

export default NavigationWarning;
