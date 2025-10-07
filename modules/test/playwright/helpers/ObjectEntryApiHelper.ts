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

	async getObjectDefinitionObjectEntries(
		applicationName: string,
		searchParams?: URLSearchParams
	) {
		if (searchParams) {
			return this.apiHelpers.get(
				`${this.apiHelpers.baseUrl}${applicationName}/?${searchParams.toString()}`
			);
		}

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

	async getObjectEntryByExternalReferenceCode({
		applicationName,
		externalReferenceCode,
		nestedField,
	}: {
		applicationName: string;
		externalReferenceCode: string;
		nestedField?: string;
	}) {
		if (nestedField) {
			return this.apiHelpers.get(
				`${this.apiHelpers.baseUrl}${applicationName}/by-external-reference-code/${externalReferenceCode}?nestedFields=${nestedField}`
			);
		}

		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${applicationName}/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async getObjectEntryById(applicationName: string, id: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${applicationName}/${id}`
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

	async patchObjectEntry(
		data: DataObject,
		applicationName: string,
		objectEntryId: number,
		scopeKey?: string
	): Promise<ObjectEntry> {
		if (scopeKey) {
			return this.apiHelpers.patch(
				`${this.apiHelpers.baseUrl}${applicationName}/scopes/${scopeKey}/${objectEntryId}`,
				data
			);
		}

		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${applicationName}/${objectEntryId}`,
			data
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

	async putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode({
		applicationName,
		currentExternalReferenceCode,
		objectRelationshipName,
		relatedExternalReferenceCode,
	}: {
		applicationName: string;
		currentExternalReferenceCode: string;
		objectRelationshipName: string;
		relatedExternalReferenceCode: string;
	}): Promise<ObjectEntry> {
		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${applicationName}/by-external-reference-code/${currentExternalReferenceCode}/${objectRelationshipName}/${relatedExternalReferenceCode}`
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
