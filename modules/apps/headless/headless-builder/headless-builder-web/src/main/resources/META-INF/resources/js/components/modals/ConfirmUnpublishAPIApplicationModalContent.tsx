/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

export function ConfirmUnpublishAPIApplicationModalContent({
	closeModal,
	handlePublish,
}: {
	closeModal: voidReturn;
	handlePublish: voidReturn;
}) {
	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('unpublish-api-application')}
			</ClayModal.Header>

			<div className="modal-body">
				<p>
					{Liferay.Language.get(
						'unpublishing-will-remove-this-api-from-the-catalog-and-will-also-hide-all-the-schemas-and-endpoints-within-it'
					)}
				</p>

				<p>
					{Liferay.Language.get(
						'also,-all-the-assets-that-used-it-will-not-work'
					)}
				</p>
			</div>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							id="modalCancelUnpublishButton"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="warning"
							id="modalConfirmUnpublishButton"
							onClick={handlePublish}
							type="button"
						>
							{Liferay.Language.get('unpublish')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
