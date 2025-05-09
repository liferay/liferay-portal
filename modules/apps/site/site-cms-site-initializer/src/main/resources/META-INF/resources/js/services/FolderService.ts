/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from './ApiHelper';

export type TFolder = {
	description: string;
	externalReferenceCode?: string;
	id: number;
	scopeKey?: string;
	title: string;
};

const OBJECT_ENTRY_FOLDER_URL = '/o/headless-object/v1.0/object-entry-folders';

async function getFolder(folderId: string): Promise<TFolder> {
	return await ApiHelper.get(`${OBJECT_ENTRY_FOLDER_URL}/${folderId}`);
}

async function updateFolder(folderData: TFolder) {
	return await ApiHelper.patch(
		folderData,
		`${OBJECT_ENTRY_FOLDER_URL}/${folderData.id}`
	);
}

export default {getFolder, updateFolder};
