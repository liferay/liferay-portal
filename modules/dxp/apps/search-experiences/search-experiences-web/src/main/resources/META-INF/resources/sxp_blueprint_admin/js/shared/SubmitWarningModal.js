/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';

import ErrorListItem from './ErrorListItem';

const SubmitWarningModal = ({
	errors,
	isSubmitting,
	message,
	onClose = () => {},
	onSubmit = () => {},
	visible,
}) => {
	const {observer, onClose: handleClose} = useModal({
		onClose,
	});

	if (!visible) {
		return null;
	}

	return (
		<ClayModal observer={observer} size="md" status="warning">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('warning')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>{message}</p>

				{errors.map((error, index) => (
					<ErrorListItem error={error} key={index} />
				))}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={handleClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton disabled={isSubmitting} onClick={onSubmit}>
							{Liferay.Language.get('continue-to-save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default SubmitWarningModal;
