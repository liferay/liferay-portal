/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import React from 'react';

interface ModalConfirmImportProps {
	handleOnClose: () => void;
	handleSubmitFnName: string;
	showCopyAsNewErrorMessage?: boolean;
	showDeleteBeforeImportErrorMessage?: boolean;
	showMirrorErrorMessage?: boolean;
}

export function ModalConfirmImport({
	handleOnClose,
	handleSubmitFnName,
	showCopyAsNewErrorMessage = false,
	showDeleteBeforeImportErrorMessage = false,
	showMirrorErrorMessage = false,
}: ModalConfirmImportProps) {
	const {observer} = useModal();

	return (
		<ClayModalProvider>
			<ClayModal center observer={observer} status="warning">
				<ClayModal.Header>
					{Liferay.Language.get('important-info-about-your-import')}
				</ClayModal.Header>

				<ClayModal.Body>
					<p>
						{Liferay.Language.get(
							'important-info-about-your-import-description'
						)}
					</p>

					<ul>
						{showDeleteBeforeImportErrorMessage && (
							<li>
								<strong>
									{Liferay.Language.get(
										'delete-application-data-before-importing'
									)}

									{': '}
								</strong>

								{Liferay.Language.get(
									'delete-application-data-before-importing-description'
								)}
							</li>
						)}

						{(showMirrorErrorMessage ||
							showCopyAsNewErrorMessage) && (
							<li>
								<strong>
									{Liferay.Language.get('update-data-mirror')}

									{': '}
								</strong>

								{Liferay.Language.get(
									'objects-entries-are-always-mirrored-regardless-of-the-selection'
								)}
							</li>
						)}
					</ul>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={handleOnClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								displayType="warning"
								onClick={() =>
									(window as any)[handleSubmitFnName]?.()
								}
							>
								{Liferay.Language.get('import')}
							</ClayButton>
						</ClayButton.Group>
					}
				></ClayModal.Footer>
			</ClayModal>
		</ClayModalProvider>
	);
}
