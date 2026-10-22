/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectValidationRuleSetting} from './ObjectValidationRuleSetting';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectValidationRule {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"active"?: boolean;
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"engine"?: string;
			"engineLabel"?: string;
			"errorLabel"?: {[key: string]: string;};
			"externalReferenceCode"?: string;
			"id"?: number;
			"name"?: {[key: string]: string;};
			"objectDefinitionExternalReferenceCode"?: string;
			"objectDefinitionId"?: number;
			"objectValidationRuleSettings"?: Array<ObjectValidationRuleSetting>;
			"outputType"?: 'fullValidation' | 'partialValidation';
			"script"?: string;
			"system"?: boolean;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "actions",
			name: "actions",
			type: "{[key: string]: {[key: string]: string;};}",
		},
		{
			baseName: "active",
			name: "active",
			type: "boolean",
		},
		{
			baseName: "dateCreated",
			name: "dateCreated",
			type: "Date",
		},
		{
			baseName: "dateModified",
			name: "dateModified",
			type: "Date",
		},
		{
			baseName: "engine",
			name: "engine",
			type: "string",
		},
		{
			baseName: "engineLabel",
			name: "engineLabel",
			type: "string",
		},
		{
			baseName: "errorLabel",
			name: "errorLabel",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "externalReferenceCode",
			name: "externalReferenceCode",
			type: "string",
		},
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "name",
			name: "name",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "objectDefinitionExternalReferenceCode",
			name: "objectDefinitionExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "objectDefinitionId",
			name: "objectDefinitionId",
			type: "number",
		},
		{
			baseName: "objectValidationRuleSettings",
			name: "objectValidationRuleSettings",
			type: "Array<ObjectValidationRuleSetting>",
		},
		{
			baseName: "outputType",
			name: "outputType",
			type: "'fullValidation' | 'partialValidation'",
		},
		{
			baseName: "script",
			name: "script",
			type: "string",
		},
		{
			baseName: "system",
			name: "system",
			type: "boolean",
		},
		];

		static getAttributeTypeMap() {
				return ObjectValidationRule.attributeTypeMap;
		}
	}
