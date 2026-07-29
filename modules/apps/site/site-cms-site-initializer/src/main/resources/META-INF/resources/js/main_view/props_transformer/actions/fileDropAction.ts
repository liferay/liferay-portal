/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {navigate} from 'frontend-js-web';

import {ObjectEntryLinkContext} from '../../../common/services/ObjectEntryLinkService';
import {AssetLibrary} from '../../../common/types/AssetLibrary';
import multipleFilesUploadAction, {
	MultipleFileUploaderData,
} from './multipleFilesUploadAction';

export default function fileDropAction(
	additionalProps: MultipleFileUploaderData & {
		baseFolderViewURL: string;
		candidateAssetLibraries: AssetLibrary[];
		loadData?: () => void;
		objectEntryLinkProps?: ObjectEntryLinkContext;
		redirect: string;
	},
	droppedFiles: any,
	dropTarget?: any
) {
	if (!droppedFiles) {
		return;
	}

	const {
		baseAssetLibraryViewURL,
		baseFolderViewURL,
		candidateAssetLibraries,
		documentClassName,
		loadData,
		objectEntryLinkProps,
		parentObjectEntryFolderExternalReferenceCode,
		redirect,
	} = additionalProps;

	multipleFilesUploadAction(
		{
			...objectEntryLinkProps,
			assetLibraries: candidateAssetLibraries,
			baseAssetLibraryViewURL,
			documentClassName,
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
			if (loadData) {
				loadData();
			}
			else {
				navigate(
					dropTarget
						? baseFolderViewURL + dropTarget.embedded?.id
						: redirect
				);
			}
		}
	);
}
