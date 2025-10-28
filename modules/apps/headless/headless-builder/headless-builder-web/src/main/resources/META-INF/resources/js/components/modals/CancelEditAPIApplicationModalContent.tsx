/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

export function CancelEditAPIApplicationModalContent({
	closeModal,
	onConfirm,
}: {
	closeModal: voidReturn;
	onConfirm: voidReturn;
}) {
	const handleClick = () => {
		onConfirm();
		closeModal();
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('cancel-changes')}
			</ClayModal.Header>

			<div className="modal-body">
				<p>
					{Liferay.Language.get(
						'all-your-unsaved-changes-will-be-lost-unless-you-save-or-publish-them-before-leaving'
					)}
				</p>

				<p>
					{Liferay.Language.get(
						'are-you-sure-you-want-to-continue-without-saving'
					)}
				</p>
			</div>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							id="modalGiveUpCancelEditButton"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="warning"
							id="modalConfirmCancelEditButton"
							onClick={handleClick}
							type="button"
						>
							{Liferay.Language.get('continue-without-saving')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
