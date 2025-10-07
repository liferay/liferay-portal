/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import React, {useState} from 'react';

import {FieldFile} from '../../common/components/forms';
import ApiHelper from '../../common/services/ApiHelper';

const JSON_EXTENSION = '.json';

export default function ImportStructureModalContent({
	closeModal,
	importURL,
	loadData,
	objectFolderExternalReferenceCode,
}: {
	closeModal: () => void;
	importURL: string;
	loadData?: () => {};
	objectFolderExternalReferenceCode: string;
}) {
	const [warning, setWarning] = useState(true);
	const [jsonFile, setJsonFile] = useState<File | null>(null);
	const [errorMessage, setErrorMessage] = useState('');

	const onFileChange = (file: File | null) => {
		if (!file) {
			setErrorMessage('');
		}

		setJsonFile(file);
	};

	const onImportButtonClick = async () => {
		const formData = new FormData();

		formData.append(
			'objectFolderExternalReferenceCode',
			objectFolderExternalReferenceCode
		);

		if (jsonFile) {
			formData.append('objectDefinitionJSON', new Blob([jsonFile]));
		}

		const {error} = await ApiHelper.postFormData(formData, importURL);

		if (!error) {
			closeModal();

			openToast({
				message: Liferay.Language.get(
					'the-content-structure-was-successfully-imported-and-the-existing-content-structure-was-overwritten'
				),
				type: 'success',
			});

			loadData?.();
		}
		else {
			setErrorMessage(error);
		}
	};

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('import-and-override-content-structure')}
			</ClayModal.Header>

			{warning && (
				<ClayAlert
					displayType="warning"
					onClose={() => {
						setWarning(false);
					}}
					title={`${Liferay.Language.get('warning')}:`}
					variant="stripe"
				>
					{Liferay.Language.get(
						'import-and-override-content-structure-warning-message'
					)}
				</ClayAlert>
			)}

			<ClayModal.Body>
				<FieldFile
					errorMessage={errorMessage}
					fieldId="jsonFileId"
					label={Liferay.Language.get('json-file')}
					onFileChange={onFileChange}
					validExtensions={JSON_EXTENSION}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!jsonFile || !!errorMessage}
							displayType="primary"
							onClick={onImportButtonClick}
						>
							{Liferay.Language.get('import-and-override')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
