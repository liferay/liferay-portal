/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React from 'react';

import {getDeleteTaskConfirmationMessage} from '../../utils/subtasksUtil';

export default function DeleteTaskModal({
	closeModal,
	onSubmit,
	subtasksCount = 0,
	title,
}: {
	closeModal: () => void;
	onSubmit: () => void;
	subtasksCount?: number;
	title: string;
}) {
	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(
					Liferay.Language.get('delete-asset-confirmation-title'),
					title
				)}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>{getDeleteTaskConfirmationMessage(title, subtasksCount)}</p>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="danger" onClick={onSubmit}>
							{Liferay.Language.get('delete')}
						</ClayButton>
					</ClayButton.Group>
				}
			></ClayModal.Footer>
		</>
	);
}
