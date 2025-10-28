/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {default as ClayButton} from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import PropTypes from 'prop-types';
import React from 'react';

export function StyleErrorsModal({onCloseModal, onPublish}) {
	const {observer, onClose} = useModal({
		onClose: onCloseModal,
	});

	return (
		<ClayModal
			aria-label={Liferay.Language.get('style-errors-detected')}
			observer={observer}
			status="warning"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('style-errors-detected')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="text-secondary">
					{Liferay.Language.get(
						'some-of-the-fields-have-invalid-values-if-you-continue-publishing-the-latest-valid-values-will-display'
					)}
				</p>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="warning" onClick={onPublish}>
							{Liferay.Language.get('continue')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

StyleErrorsModal.propTypes = {
	onCloseModal: PropTypes.func.isRequired,
	onPublish: PropTypes.func.isRequired,
};
