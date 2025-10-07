/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from './ApiHelpers';

export class ObjectEntryFolderApiHelper {
	readonly apiHelpers: ApiHelpers;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
	}

	async createObjectEntryFolder({
		parentObjectEntryFolderExternalReferenceCode = 'L_FILES',
		scopeKey,
		title,
	}: {
		parentObjectEntryFolderExternalReferenceCode?: 'L_FILES' | 'L_CONTENTS';
		scopeKey: string;
		title: string;
	}) {
		const data = JSON.stringify({
			parentObjectEntryFolderExternalReferenceCode,
			title,
		});

		return this.apiHelpers.post(
			`/o/headless-object/v1.0/scopes/${scopeKey}/object-entry-folders`,
			{data}
		);
	}

	async deleteObjectEntryFolder(objectEntryFolderId: number) {
		return this.apiHelpers.delete(
			`/o/headless-object/v1.0/object-entry-folders/${objectEntryFolderId}`
		);
	}
}
