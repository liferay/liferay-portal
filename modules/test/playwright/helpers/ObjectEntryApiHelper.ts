/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../liferay.config';
import {ApiHelpers} from './ApiHelpers';

export class ObjectEntryApiHelper {
	readonly apiHelpers: ApiHelpers;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
	}

	async deleteObjectEntry(applicationName: string, objectEntryId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${applicationName}/${objectEntryId}`
		);
	}

	async deleteObjectEntryByExternalReferenceCode(
		applicationName: string,
		scopeKey: string,
		externalReferenceCode: string
	) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${applicationName}/scopes/${scopeKey}/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async getObjectDefinitionObjectEntries(applicationName: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${applicationName}/`
		);
	}

	async getObjectDefinitionObjectEntriesByScope(
		applicationName: string,
		scopeKey: string
	) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}/${applicationName}/scopes/${scopeKey}`
		);
	}

	async getObjectEntryByExternalReferenceCode(
		applicationName: string,
		externalReferenceCode: string
	) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${applicationName}/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async postObjectDefinitionRandomObjectEntries(
		fieldName: any,
		fieldValue: String,
		restContextPath: String
	) {
		const data = {
			[fieldName]: fieldValue,
		};

		return this.apiHelpers.postResponse(
			`${liferayConfig.environment.baseUrl}${restContextPath}`,
			{data}
		);
	}

	async postObjectEntry(
		data: DataObject,
		applicationName: string,
		scopeKey?: string
	): Promise<ObjectEntry> {
		if (scopeKey) {
			return this.apiHelpers.post(
				`${this.apiHelpers.baseUrl}${applicationName}/scopes/${scopeKey}`,
				{data}
			);
		}

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${applicationName}/`,
			{data}
		);
	}

	async putObjectEntry(
		data: DataObject,
		applicationName: string,
		objectEntryId: number
	): Promise<ObjectEntry> {
		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${applicationName}/${objectEntryId}`,
			{data}
		);
	}
}
