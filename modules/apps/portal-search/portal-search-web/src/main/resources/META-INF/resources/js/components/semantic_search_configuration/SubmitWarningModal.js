/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';

export default function SubmitWarningModal({
	message,
	onClose = () => {},
	onSubmit = () => {},
	visible,
}) {
	const {observer, onClose: handleClose} = useModal({
		onClose,
	});

	if (!visible) {
		return null;
	}

	return (
		<ClayModal observer={observer} status="warning">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('warning')}
			</ClayModal.Header>

			<ClayModal.Body>{message}</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={handleClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={onSubmit}>
							{Liferay.Language.get('continue-to-save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
