/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectFieldSetting} from './ObjectFieldSetting';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectField {
			"DBType"?: 'BigDecimal' | 'Boolean' | 'Clob' | 'Date' | 'DateTime' | 'Double' | 'Integer' | 'Long' | 'String';
			"actions"?: {[key: string]: {[key: string]: string;};};
			"businessType"?: 'Aggregation' | 'Assignee' | 'Attachment' | 'AutoIncrement' | 'Boolean' | 'Date' | 'DateTime' | 'Decimal' | 'Encrypted' | 'Formula' | 'Integer' | 'LongInteger' | 'LongText' | 'MultiselectPicklist' | 'Picklist' | 'PrecisionDecimal' | 'Relationship' | 'RichText' | 'Text';
			"defaultValue"?: string;
			"externalReferenceCode"?: string;
			"id"?: number;
			"indexed"?: boolean;
			"indexedAsKeyword"?: boolean;
			"indexedLanguageId"?: string;
			"label"?: {[key: string]: string;};
			"listTypeDefinitionExternalReferenceCode"?: string;
			"listTypeDefinitionId"?: number;
			"localized"?: boolean;
			"name"?: string;
			"objectDefinitionExternalReferenceCode1"?: string;
			"objectFieldSettings"?: Array<ObjectFieldSetting>;
			"objectRelationshipExternalReferenceCode"?: string;
			"readOnly"?: 'conditional' | 'false' | 'true';
			"readOnlyConditionExpression"?: string;
			"relationshipType"?: 'oneToMany' | 'oneToOne';
			"required"?: boolean;
			"state"?: boolean;
			"system"?: boolean;
			"type"?: 'BigDecimal' | 'Boolean' | 'Clob' | 'Date' | 'DateTime' | 'Double' | 'Integer' | 'Long' | 'String';
			"unique"?: boolean;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "DBType",
			name: "DBType",
			type: "'BigDecimal' | 'Boolean' | 'Clob' | 'Date' | 'DateTime' | 'Double' | 'Integer' | 'Long' | 'String'",
		},
		{
			baseName: "actions",
			name: "actions",
			type: "{[key: string]: {[key: string]: string;};}",
		},
		{
			baseName: "businessType",
			name: "businessType",
			type: "'Aggregation' | 'Assignee' | 'Attachment' | 'AutoIncrement' | 'Boolean' | 'Date' | 'DateTime' | 'Decimal' | 'Encrypted' | 'Formula' | 'Integer' | 'LongInteger' | 'LongText' | 'MultiselectPicklist' | 'Picklist' | 'PrecisionDecimal' | 'Relationship' | 'RichText' | 'Text'",
		},
		{
			baseName: "defaultValue",
			name: "defaultValue",
			type: "string",
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
			baseName: "indexed",
			name: "indexed",
			type: "boolean",
		},
		{
			baseName: "indexedAsKeyword",
			name: "indexedAsKeyword",
			type: "boolean",
		},
		{
			baseName: "indexedLanguageId",
			name: "indexedLanguageId",
			type: "string",
		},
		{
			baseName: "label",
			name: "label",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "listTypeDefinitionExternalReferenceCode",
			name: "listTypeDefinitionExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "listTypeDefinitionId",
			name: "listTypeDefinitionId",
			type: "number",
		},
		{
			baseName: "localized",
			name: "localized",
			type: "boolean",
		},
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "objectDefinitionExternalReferenceCode1",
			name: "objectDefinitionExternalReferenceCode1",
			type: "string",
		},
		{
			baseName: "objectFieldSettings",
			name: "objectFieldSettings",
			type: "Array<ObjectFieldSetting>",
		},
		{
			baseName: "objectRelationshipExternalReferenceCode",
			name: "objectRelationshipExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "readOnly",
			name: "readOnly",
			type: "'conditional' | 'false' | 'true'",
		},
		{
			baseName: "readOnlyConditionExpression",
			name: "readOnlyConditionExpression",
			type: "string",
		},
		{
			baseName: "relationshipType",
			name: "relationshipType",
			type: "'oneToMany' | 'oneToOne'",
		},
		{
			baseName: "required",
			name: "required",
			type: "boolean",
		},
		{
			baseName: "state",
			name: "state",
			type: "boolean",
		},
		{
			baseName: "system",
			name: "system",
			type: "boolean",
		},
		{
			baseName: "type",
			name: "type",
			type: "'BigDecimal' | 'Boolean' | 'Clob' | 'Date' | 'DateTime' | 'Double' | 'Integer' | 'Long' | 'String'",
		},
		{
			baseName: "unique",
			name: "unique",
			type: "boolean",
		},
		];

		static getAttributeTypeMap() {
				return ObjectField.attributeTypeMap;
		}
	}
