/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {NestedTestEntity} from './NestedTestEntity';
			import {StringTestEntity} from './StringTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

	export class TestEntity {
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"description"?: string;
			"documentId"?: number;
			"id"?: number;
			"jsonProperty"?: string;
			"name"?: string;
			"nestedTestEntity"?: NestedTestEntity;
			"self"?: string;
			"stringTestEntities"?: Array<StringTestEntity>;
			"stringTestEntity"?: StringTestEntity;
			"testEntities"?: TestEntity;
			"type"?: 'ChildTestEntity1' | 'ChildTestEntity2' | 'ChildTestEntity3';

		static "discriminator": string | undefined = "type";

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
		{
			baseName: "nestedTestEntity",
			name: "nestedTestEntity",
			type: "NestedTestEntity",
		},
		{
			baseName: "self",
			name: "self",
			type: "string",
		},
		{
			baseName: "stringTestEntities",
			name: "stringTestEntities",
			type: "Array<StringTestEntity>",
		},
		{
			baseName: "stringTestEntity",
			name: "stringTestEntity",
			type: "StringTestEntity",
		},
		{
			baseName: "testEntities",
			name: "testEntities",
			type: "TestEntity",
		},
		{
			baseName: "type",
			name: "type",
			type: "'ChildTestEntity1' | 'ChildTestEntity2' | 'ChildTestEntity3'",
		},
		];

		static getAttributeTypeMap() {
				return TestEntity.attributeTypeMap;
		}
	}
