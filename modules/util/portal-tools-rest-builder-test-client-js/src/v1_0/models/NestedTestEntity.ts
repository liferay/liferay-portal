/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {TestEntity} from './TestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

	/**
	* https://www.schema.org/Folder
	*/
	export class NestedTestEntity {
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"description"?: string;
			"id"?: number;
			"name"?: string;
			"password"?: string;
			"testEntity"?: TestEntity;

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
			baseName: "password",
			name: "password",
			type: "string",
		},
		{
			baseName: "testEntity",
			name: "testEntity",
			type: "TestEntity",
		},
		];

		static getAttributeTypeMap() {
				return NestedTestEntity.attributeTypeMap;
		}
	}
