/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {Status} from './Status';

/**
 * @author David Truong
 * @generated
 */

	export class CTEntry {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"changeType"?: string;
			"ctCollectionId"?: number;
			"ctCollectionName"?: string;
			"ctCollectionStatus"?: Status;
			"ctCollectionStatusDate"?: Date;
			"ctCollectionStatusUserName"?: string;
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"editURL"?: string;
			"hideable"?: boolean;
			"id"?: number;
			"modelClassNameId"?: number;
			"modelClassPK"?: number;
			"ownerId"?: number;
			"ownerName"?: string;
			"siteId"?: number;
			"siteName"?: string;
			"status"?: Status;
			"statusMessage"?: string;
			"title"?: string;
			"typeName"?: string;

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
			baseName: "changeType",
			name: "changeType",
			type: "string",
		},
		{
			baseName: "ctCollectionId",
			name: "ctCollectionId",
			type: "number",
		},
		{
			baseName: "ctCollectionName",
			name: "ctCollectionName",
			type: "string",
		},
		{
			baseName: "ctCollectionStatus",
			name: "ctCollectionStatus",
			type: "Status",
		},
		{
			baseName: "ctCollectionStatusDate",
			name: "ctCollectionStatusDate",
			type: "Date",
		},
		{
			baseName: "ctCollectionStatusUserName",
			name: "ctCollectionStatusUserName",
			type: "string",
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
			baseName: "editURL",
			name: "editURL",
			type: "string",
		},
		{
			baseName: "hideable",
			name: "hideable",
			type: "boolean",
		},
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "modelClassNameId",
			name: "modelClassNameId",
			type: "number",
		},
		{
			baseName: "modelClassPK",
			name: "modelClassPK",
			type: "number",
		},
		{
			baseName: "ownerId",
			name: "ownerId",
			type: "number",
		},
		{
			baseName: "ownerName",
			name: "ownerName",
			type: "string",
		},
		{
			baseName: "siteId",
			name: "siteId",
			type: "number",
		},
		{
			baseName: "siteName",
			name: "siteName",
			type: "string",
		},
		{
			baseName: "status",
			name: "status",
			type: "Status",
		},
		{
			baseName: "statusMessage",
			name: "statusMessage",
			type: "string",
		},
		{
			baseName: "title",
			name: "title",
			type: "string",
		},
		{
			baseName: "typeName",
			name: "typeName",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return CTEntry.attributeTypeMap;
		}
	}
