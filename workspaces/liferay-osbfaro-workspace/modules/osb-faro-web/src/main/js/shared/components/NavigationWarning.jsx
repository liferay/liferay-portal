import {useUnsavedChangesPrompt} from 'shared/hooks/useUnsavedChangesPrompt';

const NavigationWarning = ({
	message = Liferay.Language.get(
		'you-have-unsaved-changes-that-will-be-discarded-by-navigating-away-from-this-page.-do-you-want-to-leave-and-discard-your-changes'
	),
	when
}) => {
	return useUnsavedChangesPrompt(when, message);
};

export default NavigationWarning;
