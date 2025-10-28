/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';

export default function Modal({
	body,
	callback,
	onClose,
	status = 'info',
	textPrimaryButton = 'Save',
	textSecondaryButton = 'Cancel',
	title,
	visible,
}) {
	const {observer, onClose: close} = useModal({
		onClose,
	});

	return (
		<>
			{visible && (
				<ClayModal
					className="d-flex justify-content-center"
					observer={observer}
					status={status}
				>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{title}
					</ClayModal.Header>

					<ClayModal.Body>{body}</ClayModal.Body>

					<ClayModal.Footer
						first={
							<ClayButton
								aria-label={textSecondaryButton}
								displayType="secondary"
								onClick={close}
							>
								{textSecondaryButton}
							</ClayButton>
						}
						last={
							<ClayButton
								aria-label={textPrimaryButton}
								displayType="primary"
								onClick={() => {
									callback();
									close();
								}}
							>
								{textPrimaryButton}
							</ClayButton>
						}
					/>
				</ClayModal>
			)}
		</>
	);
}
