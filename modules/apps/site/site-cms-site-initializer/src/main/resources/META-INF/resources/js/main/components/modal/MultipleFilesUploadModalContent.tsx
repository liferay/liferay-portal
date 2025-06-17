/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import {AssetLibrary} from '../../../types/AssetLibrary';
import MultipleFileUploader from '../MultipleFileUploader';

export default function MultipleFilesUploadModalContent({
	assetLibraries,
	baseAssetLibraryViewURL,
	loadData,
	onModalClose,
	parentObjectEntryFolderExternalReferenceCode,
}: {
	assetLibraries: AssetLibrary[];
	baseAssetLibraryViewURL: string;
	loadData?: () => void;
	onModalClose: () => void;
	parentObjectEntryFolderExternalReferenceCode: string;
}) {
	const getAssetLibraryLink = (assetLibrary: AssetLibrary) => {
		return `<a href="${baseAssetLibraryViewURL}${assetLibrary.groupId}" class="alert-link lead"><strong>${assetLibrary.name}</strong></a>`;
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
				onModalClose={onModalClose}
				onUploadComplete={onUploadComplete}
				parentObjectEntryFolderExternalReferenceCode={
					parentObjectEntryFolderExternalReferenceCode
				}
			/>
		</>
	);
}
