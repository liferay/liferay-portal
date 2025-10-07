/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {navigate} from 'frontend-js-web';

import multipleFilesUploadAction, {
	MultipleFileUploaderData,
} from './multipleFilesUploadAction';

export default function fileDropAction(
	additionalProps: MultipleFileUploaderData & {
		baseFolderViewURL: string;
		redirect: string;
	},
	droppedFiles: any,
	dropTarget?: any
) {
	if (!droppedFiles) {
		return;
	}

	const {
		assetLibraries,
		baseAssetLibraryViewURL,
		baseFolderViewURL,
		parentObjectEntryFolderExternalReferenceCode,
		redirect,
	} = additionalProps;

	multipleFilesUploadAction(
		{
			assetLibraries,
			baseAssetLibraryViewURL,
			filesToUpload: droppedFiles.map((file: any) => ({
				errorMessage: '',
				failed: false,
				file,
				name: file.name,
				size: file.size,
			})),
			parentObjectEntryFolderExternalReferenceCode: dropTarget
				? dropTarget.embedded?.externalReferenceCode
				: parentObjectEntryFolderExternalReferenceCode,
		},
		() => {
			navigate(
				dropTarget
					? baseFolderViewURL + dropTarget.embedded?.id
					: redirect
			);
		}
	);
}
