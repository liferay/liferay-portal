/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FilesUploaderComponent} from '@liferay/frontend-js-item-selector-web';
import {type Space, SpaceSelector} from '@liferay/site-cms-site-initializer';
import {
	FieldBase,
	MultipleFileUploader,
	UploadRequestCallback,
	openToast,
} from 'frontend-js-components-web';
import {getFileAsBase64, sub} from 'frontend-js-web';
import React, {useId, useState} from 'react';

export const FileUploaderComponentExample: FilesUploaderComponent = function ({
	files,
	onCloseUploadView,
}) {
	const [space, setSpace] = useState<Space | undefined>();
	const [groupIdError, setGroupIdError] = useState(false);

	const groupIdInputId = useId();

	const formValidation = async () => {
		const error = (space?.siteId || 0) <= 0;

		setGroupIdError(error);

		return error === false;
	};

	const uploadRequest: UploadRequestCallback = async ({fileData}) => {
		if (!space) {
			throw new Error('no space selected');
		}

		const fileBase64 = await getFileAsBase64(fileData.file);

		const response = await Liferay.Util.fetch(
			`/o/cms/basic-documents/scopes/${space.siteId}`,
			{
				body: JSON.stringify({
					file: {
						fileBase64,
						name: fileData.name,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: fileData.name,
				}),
				headers: {
					'Accept': 'application/json',
					'Accept-Language':
						Liferay.ThemeDisplay.getBCP47LanguageId(),
					'Content-Type': 'application/json',
				},
				method: 'POST',
			}
		);

		return await response.json();
	};

	const onUploadComplete = ({
		failedFiles,
		successFiles,
	}: {
		failedFiles: string[];
		successFiles: string[];
	}) => {
		if (successFiles.length) {
			let toastMessage;

			if (space) {
				if (successFiles.length === 1) {
					toastMessage = sub(
						Liferay.Language.get(
							'x-file-was-successfully-uploaded-to-x-space'
						),
						['1', space.name]
					);
				}
				else {
					toastMessage = sub(
						Liferay.Language.get(
							'x-files-were-successfully-uploaded-to-x-space'
						),
						[String(successFiles.length), space.name]
					);
				}
			}

			openToast({
				message: toastMessage,
				type: 'success',
			});
		}

		if (!failedFiles.length) {
			onCloseUploadView();
		}
	};

	return (
		<MultipleFileUploader
			filesToUpload={files}
			formValidation={formValidation}
			onModalClose={onCloseUploadView}
			onUploadComplete={onUploadComplete}
			scopeSelectorElement={
				<div className="mt-4">
					<FieldBase
						errorMessage={
							groupIdError
								? Liferay.Language.get('this-field-is-required')
								: undefined
						}
						helpMessage={Liferay.Language.get(
							'select-the-space-to-upload-the-file'
						)}
						id={groupIdInputId}
						label={Liferay.Language.get('space')}
						required
					>
						<SpaceSelector
							id={groupIdInputId}
							onSpaceChange={(space) => {
								setGroupIdError(false);
								setSpace(space);
							}}
							space={space}
						/>
					</FieldBase>
				</div>
			}
			uploadRequest={uploadRequest}
		/>
	);
};
