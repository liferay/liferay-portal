/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectLayoutTab} from './ObjectLayoutTab';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectLayout {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"defaultObjectLayout"?: boolean;
			"id"?: number;
			"name"?: {[key: string]: string;};
			"objectDefinitionExternalReferenceCode"?: string;
			"objectDefinitionId"?: number;
			"objectLayoutTabs"?: Array<ObjectLayoutTab>;

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
			baseName: "defaultObjectLayout",
			name: "defaultObjectLayout",
			type: "boolean",
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
			baseName: "objectLayoutTabs",
			name: "objectLayoutTabs",
			type: "Array<ObjectLayoutTab>",
		},
		];

		static getAttributeTypeMap() {
				return ObjectLayout.attributeTypeMap;
		}
	}
