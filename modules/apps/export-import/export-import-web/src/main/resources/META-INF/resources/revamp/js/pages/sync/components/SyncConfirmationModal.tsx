/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

export default function SyncConfirmationModal({
	onClose,
	onConfirm,
	siteTemplateName,
}: {
	onClose: () => void;
	onConfirm: () => void;
	siteTemplateName: string;
}) {
	const {observer, onClose: closeModal} = useModal({onClose});

	const [confirmed, setConfirmed] = useState(false);

	return (
		<ClayModal observer={observer} status="warning">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('sync-linked-sites')}
			</ClayModal.Header>

			<ClayModal.Body>
				{sub(
					Liferay.Language.get(
						'syncing-applies-the-selected-data-from-x-to-its-linked-sites.-it-overwrites-any-changes-made-directly-to-them-on-those-sites'
					),
					<strong key="siteTemplateName">{siteTemplateName}</strong>
				)}
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

						<ClayButton
							disabled={confirmed}
							displayType="warning"
							onClick={() => {
								setConfirmed(true);

								onConfirm();

								closeModal();
							}}
						>
							{Liferay.Language.get('sync-sites')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
