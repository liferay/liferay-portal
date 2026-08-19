/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
	ObjectDefinitionSetting,
	ObjectField,
	ObjectFolder,
	ObjectFolderAPI,
	ObjectRelationship,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect} from '@playwright/test';

import {getRandomInt} from '../utils/getRandomInt';
import {ApiHelpers} from './ApiHelpers';
export class ObjectAdminApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'object-admin/v1.0';
	}

	async getAllObjectDefinitions() {

		// Ask for every definition, not the first page of them: the default
		// page holds twenty while an environment that has been worked in holds
		// far more, and a caller looking for one by reference code then finds
		// nothing.

		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions?page=-1`
		);
	}

	async getAllObjectDefinitionsFields(objectDefinitionId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions/${objectDefinitionId}/object-fields`
		);
	}

	async getObjectDefinitionByName(name: string): Promise<ObjectDefinition> {
		const {items} = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions?filter=name eq '${name}'`
		);

		return items[0];
	}

	async patchObjectDefinitionSetting(
		objectDefinitionId: number,
		name: string,
		value: string
	) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions/${objectDefinitionId}`,
			{objectDefinitionSettings: [{name, value}]}
		);
	}

	async patchObjectRelationshipEdge(
		relationship: ObjectRelationship,
		edge: boolean
	) {
		const objectRelationshipAPIClient =
			await this.apiHelpers.buildRestClient(ObjectRelationshipAPI);

		await objectRelationshipAPIClient.putObjectRelationship(
			relationship.id!,
			{...relationship, edge}
		);
	}

	async postObjectDefinitionInheritanceRelationship(
		parent: ObjectDefinition,
		child: ObjectDefinition
	): Promise<ObjectRelationship> {
		const objectRelationshipAPIClient =
			await this.apiHelpers.buildRestClient(ObjectRelationshipAPI);

		const {body} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				parent.externalReferenceCode!,
				{
					edge: true,
					label: {en_US: 'inheritanceRelationship' + getRandomInt()},
					name: 'rel' + getRandomInt(),
					objectDefinitionExternalReferenceCode1:
						parent.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						child.externalReferenceCode,
					objectDefinitionId1: parent.id,
					objectDefinitionId2: child.id,
					objectDefinitionName2: child.name,
					type: 'oneToMany',
				}
			);

		return body;
	}

	async postObjectDefinitionObjectFieldBatch(
		objectDefinitionId: number,
		objectFields: Partial<ObjectField>[]
	): Promise<ObjectField> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions/${objectDefinitionId}/object-fields/batch`,
			{data: objectFields}
		);
	}

	async postObjectDefinitionPublish({
		objectDefinitionId,
	}: {
		objectDefinitionId: number;
	}): Promise<ObjectDefinition> {
		const objectDefinitionAPIClient =
			await this.apiHelpers.buildRestClient(ObjectDefinitionAPI);

		return (
			await objectDefinitionAPIClient.postObjectDefinitionPublish(
				objectDefinitionId
			)
		).body;
	}

	async postRandomObjectDefinition({
		className,
		enableFriendlyURLCustomization,
		objectDefinitionExternalReferenceCode = `ObjectDefinition${getRandomInt()}`,
		objectDefinitionSettings,
		objectFields,
		objectFolderExternalReferenceCode,
		panelCategoryKey,
		scope = 'company',
		status,
		titleObjectFieldName,
	}: {
		className?: string;
		enableFriendlyURLCustomization?: boolean;
		objectDefinitionExternalReferenceCode?: string;
		objectDefinitionSettings?: Partial<ObjectDefinitionSetting>[];
		objectFields?: Partial<ObjectField>[];
		objectFolderExternalReferenceCode?: string;
		panelCategoryKey?: string;
		scope?: 'company' | 'depot' | 'site';
		status: {code: number};
		titleObjectFieldName?: string;
	}) {
		const requestBody: ObjectDefinition = {
			active: true,
			className,
			enableFriendlyURLCustomization,
			externalReferenceCode: objectDefinitionExternalReferenceCode,
			label: {
				en_US: objectDefinitionExternalReferenceCode,
			},
			name: objectDefinitionExternalReferenceCode,
			objectDefinitionSettings:
				objectDefinitionSettings as ObjectDefinitionSetting[],
			objectFields: objectFields ?? [
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'textField',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: '',
					label: {en_US: 'textField'},
					listTypeDefinitionId: 0,
					localized: false,
					name: 'textField',
					required: false,
					system: false,
					type: 'String',
				},
			],
			objectFolderExternalReferenceCode,
			panelCategoryKey: panelCategoryKey ?? '',
			pluralLabel: {
				en_US: objectDefinitionExternalReferenceCode,
			},
			scope,
			status,
			titleObjectFieldName: titleObjectFieldName ?? 'id',
		};

		if (objectFolderExternalReferenceCode) {
			requestBody.objectFolderExternalReferenceCode =
				objectFolderExternalReferenceCode;
		}

		const objectDefinitionAPIClient =
			await this.apiHelpers.buildRestClient(ObjectDefinitionAPI);

		return (
			await objectDefinitionAPIClient.postObjectDefinition(requestBody)
		).body;
	}

	async postRandomObjectFolder(): Promise<ObjectFolder> {
		const objectFolderExternalReferenceCode =
			'objectFolder' + getRandomInt();

		const objectFolderAPIClient =
			await this.apiHelpers.buildRestClient(ObjectFolderAPI);

		return (
			await objectFolderAPIClient.postObjectFolder({
				externalReferenceCode: objectFolderExternalReferenceCode,
				label: {
					en_US: objectFolderExternalReferenceCode,
				},
				name: objectFolderExternalReferenceCode,
			})
		).body;
	}

	async waitForObjectDefinition(
		name: string,
		{
			interval = 500,
			timeout = 10_000,
		}: {interval?: number; timeout?: number} = {}
	): Promise<ObjectDefinition> {
		let definition: ObjectDefinition | undefined;

		await expect
			.poll(
				async () => {
					definition = await this.getObjectDefinitionByName(name);

					return Boolean(definition?.active);
				},
				{intervals: [interval], timeout}
			)
			.toBe(true);

		return definition!;
	}
}
