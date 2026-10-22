/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	export class TestEntityAddress {
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"description"?: string;
			"documentId"?: number;
			"id"?: number;
			"jsonProperty"?: string;
			"name"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
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
			baseName: "documentId",
			name: "documentId",
			type: "number",
		},
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "jsonProperty",
			name: "jsonProperty",
			type: "string",
		},
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return TestEntityAddress.attributeTypeMap;
		}
	}
