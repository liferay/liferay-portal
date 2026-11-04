/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {BaseApiHelpers} from './ApiHelpers';
import {getRandomInt} from './utils/getRandomInt';

type ObjectDefinition = {[key: string]: any};

type ObjectDefinitionSetting = {[key: string]: any};

type ObjectField = {[key: string]: any};

type ObjectFolder = {[key: string]: any};

type ObjectRelationship = {[key: string]: any};

export class ObjectAdminApiHelper {
	readonly apiHelpers: BaseApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: BaseApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'object-admin/v1.0';
	}

	async getAllObjectDefinitions() {
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
		await this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-relationships/${relationship.id}`,
			{data: {...relationship, edge}, failOnStatusCode: true}
		);
	}

	async postObjectDefinitionInheritanceRelationship(
		parent: ObjectDefinition,
		child: ObjectDefinition
	): Promise<ObjectRelationship> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions/by-external-reference-code/${parent.externalReferenceCode}/object-relationships`,
			{
				data: {
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
				},
				failOnStatusCode: true,
			}
		);
	}

	async postObjectDefinitionObjectField(
		objectDefinitionId: number,
		objectField: Partial<ObjectField>
	): Promise<ObjectField> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions/${objectDefinitionId}/object-fields`,
			{data: objectField}
		);
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
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions/${objectDefinitionId}/publish`,
			{failOnStatusCode: true}
		);
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

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-definitions`,
			{data: requestBody, failOnStatusCode: true}
		);
	}

	async postRandomObjectFolder(): Promise<ObjectFolder> {
		const objectFolderExternalReferenceCode =
			'objectFolder' + getRandomInt();

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/object-folders`,
			{
				data: {
					externalReferenceCode: objectFolderExternalReferenceCode,
					label: {
						en_US: objectFolderExternalReferenceCode,
					},
					name: objectFolderExternalReferenceCode,
				},
				failOnStatusCode: true,
			}
		);
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
