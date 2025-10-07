/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React, {useState} from 'react';

import {ModalConfirmImport} from '../modals/ModalConfirmImport';

export default function ImportButton({
	copyAsNewCheckboxId,
	deletePortletDataBeforeImportingCheckboxId,
	handleSubmitFnName,
	isAnyObjectEntrySelectedFnName,
	mirrorWithOverwritingCheckboxId,
}: {
	copyAsNewCheckboxId: string;
	deletePortletDataBeforeImportingCheckboxId: string;
	handleSubmitFnName: string;
	isAnyObjectEntrySelectedFnName: string;
	mirrorWithOverwritingCheckboxId: string;
}) {
	const [isOpen, setIsOpen] = useState(false);
	const [showCopyAsNewErrorMessage, setShowCopyAsNewErrorMessage] =
		useState(false);
	const [
		showDeleteBeforeImportErrorMessage,
		setshowDeleteBeforeImportErrorMessage,
	] = useState(false);
	const [showMirrorErrorMessage, setShowMirrorErrorMessage] = useState(false);

	const handleSubmit = () => {
		const copyAsNewCheckbox = document.getElementById(
			copyAsNewCheckboxId
		) as HTMLInputElement;

		const deleteBeforeImportCheckbox = document.getElementById(
			deletePortletDataBeforeImportingCheckboxId
		) as HTMLInputElement;

		const mirrorCheckbox = document.getElementById(
			mirrorWithOverwritingCheckboxId
		) as HTMLInputElement;

		const copyAsNewChecked = copyAsNewCheckbox?.checked;
		setShowCopyAsNewErrorMessage(copyAsNewChecked);

		const deleteBeforeImportChecked = deleteBeforeImportCheckbox?.checked;
		setshowDeleteBeforeImportErrorMessage(deleteBeforeImportChecked);

		const mirrorChecked = mirrorCheckbox?.checked;
		setShowMirrorErrorMessage(mirrorChecked);

		const isAnyChecked = (window as any)[
			isAnyObjectEntrySelectedFnName
		]?.();

		const showModal =
			isAnyChecked &&
			(mirrorChecked || copyAsNewChecked || deleteBeforeImportChecked);

		showModal ? setIsOpen(true) : (window as any)[handleSubmitFnName]?.();
	};

	return (
		<div>
			<ClayButton.Group spaced>
				<ClayButton onClick={handleSubmit}>
					{Liferay.Language.get('Import')}
				</ClayButton>
			</ClayButton.Group>

			{isOpen && (
				<ModalConfirmImport
					handleOnClose={() => setIsOpen(false)}
					handleSubmitFnName={handleSubmitFnName}
					showCopyAsNewErrorMessage={showCopyAsNewErrorMessage}
					showDeleteBeforeImportErrorMessage={
						showDeleteBeforeImportErrorMessage
					}
					showMirrorErrorMessage={showMirrorErrorMessage}
				/>
			)}
		</div>
	);
}
