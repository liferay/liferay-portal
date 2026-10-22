/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectViewColumn} from './ObjectViewColumn';
			import {ObjectViewFilterColumn} from './ObjectViewFilterColumn';
			import {ObjectViewSortColumn} from './ObjectViewSortColumn';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectView {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"defaultObjectView"?: boolean;
			"externalReferenceCode"?: string;
			"id"?: number;
			"name"?: {[key: string]: string;};
			"objectDefinitionExternalReferenceCode"?: string;
			"objectDefinitionId"?: number;
			"objectViewColumns"?: Array<ObjectViewColumn>;
			"objectViewFilterColumns"?: Array<ObjectViewFilterColumn>;
			"objectViewSortColumns"?: Array<ObjectViewSortColumn>;

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
			baseName: "defaultObjectView",
			name: "defaultObjectView",
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
			baseName: "objectViewColumns",
			name: "objectViewColumns",
			type: "Array<ObjectViewColumn>",
		},
		{
			baseName: "objectViewFilterColumns",
			name: "objectViewFilterColumns",
			type: "Array<ObjectViewFilterColumn>",
		},
		{
			baseName: "objectViewSortColumns",
			name: "objectViewSortColumns",
			type: "Array<ObjectViewSortColumn>",
		},
		];

		static getAttributeTypeMap() {
				return ObjectView.attributeTypeMap;
		}
	}
