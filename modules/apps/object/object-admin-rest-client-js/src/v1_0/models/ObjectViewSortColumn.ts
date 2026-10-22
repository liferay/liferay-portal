/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectViewSortColumn {
			"id"?: number;
			"objectFieldName"?: string;
			"priority"?: number;
			"sortOrder"?: 'asc' | 'desc';

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "objectFieldName",
			name: "objectFieldName",
			type: "string",
		},
		{
			baseName: "priority",
			name: "priority",
			type: "number",
		},
		{
			baseName: "sortOrder",
			name: "sortOrder",
			type: "'asc' | 'desc'",
		},
		];

		static getAttributeTypeMap() {
				return ObjectViewSortColumn.attributeTypeMap;
		}
	}
