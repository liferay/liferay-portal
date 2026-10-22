/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {ObjectFolderItem} from './ObjectFolderItem';

/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectFolder {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"externalReferenceCode"?: string;
			"id"?: number;
			"label"?: {[key: string]: string;};
			"name"?: string;
			"objectFolderItems"?: Array<ObjectFolderItem>;

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
			baseName: "objectFolderItems",
			name: "objectFolderItems",
			type: "Array<ObjectFolderItem>",
		},
		];

		static getAttributeTypeMap() {
				return ObjectFolder.attributeTypeMap;
		}
	}
