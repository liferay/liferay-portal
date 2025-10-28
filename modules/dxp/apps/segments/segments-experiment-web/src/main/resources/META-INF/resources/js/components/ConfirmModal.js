/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

function ConfirmModal({
	children,
	modalObserver,
	onCancel,
	onConfirm,
	submitTitle,
	title,
}) {
	return (
		<ClayModal observer={modalObserver} status="warning">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>{children}</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onCancel}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="warning" onClick={onConfirm}>
							{submitTitle}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

export {ConfirmModal};
