/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

export class ObjectEntryFolderApiHelper {
	readonly apiHelpers: ApiHelpers;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
	}

	async getObjectEntryFolderByExternalReferenceCode({
		externalReferenceCode,
		scopeKey,
	}: {
		externalReferenceCode: string;
		scopeKey: string;
	}) {
		return this.apiHelpers.get(
			`/o/headless-object/v1.0/scopes/${scopeKey}/object-entry-folder/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async postObjectEntryFolderCollaborators(
		data: DataObject[],
		objectEntryFolderId: number
	) {
		return this.apiHelpers.post(
			`/o/headless-object/v1.0/object-entry-folders/${objectEntryFolderId}/collaborators`,
			{data}
		);
	}

	async createObjectEntryFolder({
		parentObjectEntryFolderExternalReferenceCode = 'L_FILES',
		scopeKey,
		title,
	}: {
		parentObjectEntryFolderExternalReferenceCode?: string;
		scopeKey: string;
		title: string;
	}) {
		const data = JSON.stringify({
			parentObjectEntryFolderExternalReferenceCode,
			title,
		});

		const objectEntryFolder = await this.apiHelpers.post(
			`/o/headless-object/v1.0/scopes/${scopeKey}/object-entry-folders`,
			{data}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: objectEntryFolder.id,
				type: 'objectEntryFolder',
			});
		}

		return objectEntryFolder;
	}

	async deleteObjectEntryFolder(objectEntryFolderId: number) {
		return this.apiHelpers.delete(
			`/o/headless-object/v1.0/object-entry-folders/${objectEntryFolderId}`
		);
	}
}
