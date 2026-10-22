/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author David Truong
 * @generated
 */

	/**
	* Another server to create and publish publications.
	*/
	export class CTRemote {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"clientId"?: string;
			"clientSecret"?: string;
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"description"?: string;
			"id"?: number;
			"name"?: string;
			"ownerName"?: string;
			"url"?: string;

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
			baseName: "clientId",
			name: "clientId",
			type: "string",
		},
		{
			baseName: "clientSecret",
			name: "clientSecret",
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
			baseName: "description",
			name: "description",
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
			type: "string",
		},
		{
			baseName: "ownerName",
			name: "ownerName",
			type: "string",
		},
		{
			baseName: "url",
			name: "url",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return CTRemote.attributeTypeMap;
		}
	}
