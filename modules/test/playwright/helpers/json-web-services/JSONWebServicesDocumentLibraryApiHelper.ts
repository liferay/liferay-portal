/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import {ApiHelpers} from '../ApiHelpers';

export enum DLFILE_STATUS {
	APPROVED = '0',
	DENIED = '4',
	DRAFT = '2',
	EXPIRED = '3',
	IN_TRASH = '8',
	INACTIVE = '5',
	INCOMPLETE = '6',
	PENDING = '1',
	SCHEDULED = '7',
}

type TDLFileEntry = {
	fileEntryId?: string;
};

type TDLFileVersion = {
	fileVersionId?: string;
};

export class JSONWebServicesDocumentLibraryApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly baseAppPath: string;
	readonly baseFileEntryPath: string;
	readonly baseFileVersionPath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.baseAppPath = '/api/jsonws/dlapp';
		this.baseFileEntryPath = '/api/jsonws/dlfileentry';
		this.baseFileVersionPath = '/api/jsonws/dlfileversion';
	}

	async getLastestFileVersion(fileEntryId: string): Promise<TDLFileVersion> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('fileEntryId', fileEntryId);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.baseFileVersionPath}/get-latest-file-version`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async moveFileEntry(
		fileEntryId: number | string,
		newFolderId: number | string
	): Promise<TDLFileEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('fileEntryId', String(fileEntryId));
		urlSearchParams.append('newFolderId', String(newFolderId));
		urlSearchParams.append('serviceContext', '{}');

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.baseAppPath}/move-file-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async updateStatus(
		userId: string,
		fileVersionId: string,
		status: DLFILE_STATUS
	): Promise<TDLFileEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('userId', userId);
		urlSearchParams.append('fileVersionId', fileVersionId);
		urlSearchParams.append('status', status);
		urlSearchParams.append('workflowContext', '{}');

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.baseFileEntryPath}/update-status`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
