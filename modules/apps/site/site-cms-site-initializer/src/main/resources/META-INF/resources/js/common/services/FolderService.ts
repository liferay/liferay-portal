/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from './ApiHelper';

export type TFolder = {
	description: string;
	externalReferenceCode?: string;
	id: number;
	label?: string;
	scope?: {
		externalReferenceCode: string;
	};
	scopeKey?: string;
	title: string;
};

const OBJECT_ENTRY_FOLDER_URL = '/o/headless-object/v1.0/object-entry-folders';

async function copyFolder<DataType = unknown>(
	objectEntryFolderId: number,
	parentObjectEntryFolderId: number
) {
	return await ApiHelper.post<DataType>(
		`${OBJECT_ENTRY_FOLDER_URL}/${objectEntryFolderId}/by-parent-object-entry-folder-id/${parentObjectEntryFolderId}/copy`
	);
}

async function copyReplaceFolder<DataType = unknown>(
	objectEntryFolderId: number,
	parentObjectEntryFolderId: number
) {
	return await ApiHelper.post<DataType>(
		`${OBJECT_ENTRY_FOLDER_URL}/${objectEntryFolderId}/by-parent-object-entry-folder-id/${parentObjectEntryFolderId}/copy-replace`
	);
}

async function createFolder<DataType = unknown>(
	groupId: number,
	parentObjectEntryFolderExternalReferenceCode: string,
	title: string
) {
	return await ApiHelper.post<DataType>(
		`/o/headless-object/v1.0/scopes/${groupId}/object-entry-folders`,
		{
			parentObjectEntryFolderExternalReferenceCode,
			title,
		}
	);
}

async function getFolder(folderId: string): Promise<TFolder> {
	const {data, error} = await ApiHelper.get<TFolder>(
		`${OBJECT_ENTRY_FOLDER_URL}/${folderId}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function moveFolder<DataType = unknown>(
	objectEntryFolderId: number,
	parentObjectEntryFolderId: number
) {
	return await ApiHelper.post<DataType>(
		`${OBJECT_ENTRY_FOLDER_URL}/${objectEntryFolderId}/by-parent-object-entry-folder-id/${parentObjectEntryFolderId}/move`
	);
}

async function moveReplaceFolder<DataType = unknown>(
	objectEntryFolderId: number,
	parentObjectEntryFolderId: number
) {
	return await ApiHelper.post<DataType>(
		`${OBJECT_ENTRY_FOLDER_URL}/${objectEntryFolderId}/by-parent-object-entry-folder-id/${parentObjectEntryFolderId}/move-replace`
	);
}

async function searchFolder<DataType = unknown>(
	groupId: number,
	folderName: string,
	parentFolderId: number
) {
	return await ApiHelper.get<DataType>(
		`/o/headless-object/v1.0/scopes/${groupId}/object-entry-folders?filter=title eq '${folderName}' and folderId eq ${parentFolderId}`
	);
}

async function updateFolder(folderData: TFolder) {
	return await ApiHelper.patch(
		folderData,
		`${OBJECT_ENTRY_FOLDER_URL}/${folderData.id}`
	);
}

export default {
	copyFolder,
	copyReplaceFolder,
	createFolder,
	getFolder,
	moveFolder,
	moveReplaceFolder,
	searchFolder,
	updateFolder,
};
