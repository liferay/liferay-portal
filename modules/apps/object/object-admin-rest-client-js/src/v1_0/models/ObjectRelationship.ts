/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectField} from './ObjectField';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectRelationship {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"deletionType"?: 'cascade' | 'disassociate' | 'prevent';
			"description"?: {[key: string]: string;};
			"edge"?: boolean;
			"externalReferenceCode"?: string;
			"id"?: number;
			"label"?: {[key: string]: string;};
			"name"?: string;
			"objectDefinitionExternalReferenceCode1"?: string;
			"objectDefinitionExternalReferenceCode2"?: string;
			"objectDefinitionId1"?: number;
			"objectDefinitionId2"?: number;
			"objectDefinitionModifiable2"?: boolean;
			"objectDefinitionName2"?: string;
			"objectDefinitionScope2"?: string;
			"objectDefinitionSystem2"?: boolean;
			"objectField"?: ObjectField;
			"parameterObjectFieldId"?: number;
			"parameterObjectFieldName"?: string;
			"reverse"?: boolean;
			"system"?: boolean;
			"type"?: 'oneToMany' | 'oneToOne' | 'manyToMany';

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
			baseName: "deletionType",
			name: "deletionType",
			type: "'cascade' | 'disassociate' | 'prevent'",
		},
		{
			baseName: "description",
			name: "description",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "edge",
			name: "edge",
			type: "boolean",
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
			baseName: "label",
			name: "label",
			type: "{[key: string]: string;}",
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
			baseName: "objectDefinitionExternalReferenceCode2",
			name: "objectDefinitionExternalReferenceCode2",
			type: "string",
		},
		{
			baseName: "objectDefinitionId1",
			name: "objectDefinitionId1",
			type: "number",
		},
		{
			baseName: "objectDefinitionId2",
			name: "objectDefinitionId2",
			type: "number",
		},
		{
			baseName: "objectDefinitionModifiable2",
			name: "objectDefinitionModifiable2",
			type: "boolean",
		},
		{
			baseName: "objectDefinitionName2",
			name: "objectDefinitionName2",
			type: "string",
		},
		{
			baseName: "objectDefinitionScope2",
			name: "objectDefinitionScope2",
			type: "string",
		},
		{
			baseName: "objectDefinitionSystem2",
			name: "objectDefinitionSystem2",
			type: "boolean",
		},
		{
			baseName: "objectField",
			name: "objectField",
			type: "ObjectField",
		},
		{
			baseName: "parameterObjectFieldId",
			name: "parameterObjectFieldId",
			type: "number",
		},
		{
			baseName: "parameterObjectFieldName",
			name: "parameterObjectFieldName",
			type: "string",
		},
		{
			baseName: "reverse",
			name: "reverse",
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
			type: "'oneToMany' | 'oneToOne' | 'manyToMany'",
		},
		];

		static getAttributeTypeMap() {
				return ObjectRelationship.attributeTypeMap;
		}
	}
