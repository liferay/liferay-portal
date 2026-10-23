import ConfirmationModal from 'shared/components/ConfirmationModal';
import React, {useEffect, useRef} from 'react';
import {useBlocker} from 'react-router-dom';
import {useModal} from '@clayui/modal';

/**
 * Blocks the navigation while there are unsaved changes and asks the user
 * whether to leave. The caller renders the returned modal.
 */
export function useUnsavedChangesPrompt(
	when: boolean,
	message?: string
): React.ReactNode {
	const modal = useModal();

	const blocker = useBlocker(
		({currentLocation, nextLocation}) =>
			when && currentLocation.pathname !== nextLocation.pathname
	);

	useEffect(() => {
		if (blocker.state === 'blocked') {
			modal.onOpenChange(true);
		}
	}, [blocker.state]);

	// The modal can also be dismissed with Esc or a click outside, which
	// leaves the navigation blocked unless it is reset when it closes.

	const wasOpenRef = useRef(false);

	useEffect(() => {
		if (wasOpenRef.current && !modal.open && blocker.state === 'blocked') {
			blocker.reset?.();
		}

		wasOpenRef.current = modal.open;
	}, [modal.open]);

	if (!modal.open) {
		return null;
	}

	return (
		<ConfirmationModal
			cancelMessage={Liferay.Language.get('stay-on-page')}
			closeAfterSubmit={false}
			message={message}
			observer={modal.observer}
			onClose={() => {
				modal.onOpenChange(false);

				blocker.reset?.();
			}}
			onSubmit={() => {
				modal.onOpenChange(false);

				blocker.proceed?.();
			}}
			submitButtonDisplay="warning"
			submitMessage={Liferay.Language.get('leave-page')}
			title={Liferay.Language.get('unsaved-changes')}
			type="warning"
		/>
	);
}

export default useUnsavedChangesPrompt;
