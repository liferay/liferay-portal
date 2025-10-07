/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import ApiHelper from '../../common/services/ApiHelper';
import {AssetLibrary} from '../../common/types/AssetLibrary';
import MultipleFileUploader, {
	FileData,
} from '../multiple_file_uploader/MultipleFileUploader';

const getBase64 = (file: File): Promise<string> => {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = () => {
			if (typeof reader.result === 'string') {
				resolve(reader.result.split(',')[1]);
			}
			else {
				reject(new Error('FileReader did not return a string.'));
			}
		};
		reader.onerror = reject;
		reader.readAsDataURL(file);
	});
};

export default function MultipleFilesUploadModalContent({
	assetLibraries,
	baseAssetLibraryViewURL,
	filesToUpload,
	loadData,
	onModalClose,
	parentObjectEntryFolderExternalReferenceCode,
}: {
	assetLibraries: AssetLibrary[];
	baseAssetLibraryViewURL: string;
	filesToUpload?: FileData[];
	loadData?: () => void;
	onModalClose: () => void;
	parentObjectEntryFolderExternalReferenceCode: string;
}) {
	const getAssetLibraryLink = (assetLibrary: AssetLibrary) => {
		return `<a href="${baseAssetLibraryViewURL}${assetLibrary.groupId}" class="alert-link lead"><strong>${assetLibrary.name}</strong></a>`;
	};

	const uploadRequest = async ({
		fileData,
		groupId,
	}: {
		fileData: FileData;
		groupId: string;
	}) => {
		const fileBase64 = await getBase64(fileData.file);

		return await ApiHelper.post(
			`/o/cms/basic-documents/scopes/${groupId}`,
			{
				file: {
					fileBase64,
					name: fileData.name,
				},
				objectEntryFolderExternalReferenceCode:
					parentObjectEntryFolderExternalReferenceCode || 'L_FILES',
				title: fileData.name,
			}
		);
	};

	const onUploadComplete = ({
		assetLibrary,
		failedFiles,
		successFiles,
	}: {
		assetLibrary: AssetLibrary;
		failedFiles: string[];
		successFiles: string[];
	}) => {
		if (successFiles.length) {
			loadData?.();

			let toastMessage;

			if (successFiles.length === 1) {
				toastMessage = sub(
					Liferay.Language.get(
						'x-file-was-successfully-uploaded-to-x-space'
					),
					['1', getAssetLibraryLink(assetLibrary)]
				);
			}
			else {
				toastMessage = sub(
					Liferay.Language.get(
						'x-files-were-successfully-uploaded-to-x-space'
					),
					[
						String(successFiles.length),
						getAssetLibraryLink(assetLibrary),
					]
				);
			}

			openToast({
				message: toastMessage,
				type: 'success',
			});
		}

		if (!failedFiles.length) {
			onModalClose();
		}
	};

	return (
		<>
			<ClayModal.Header>
				{sub(
					Liferay.Language.get('upload-x'),
					Liferay.Language.get('multiple-files')
				)}
			</ClayModal.Header>

			<MultipleFileUploader
				assetLibraries={assetLibraries}
				filesToUpload={filesToUpload}
				onModalClose={onModalClose}
				onUploadComplete={onUploadComplete}
				uploadRequest={uploadRequest}
			/>
		</>
	);
}
