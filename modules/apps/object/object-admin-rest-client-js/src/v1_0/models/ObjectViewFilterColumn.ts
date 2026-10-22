/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectViewFilterColumn {
			"filterType"?: 'excludes' | 'includes';
			"id"?: number;
			"json"?: string;
			"objectFieldName"?: string;
			"valueSummary"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "filterType",
			name: "filterType",
			type: "'excludes' | 'includes'",
		},
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "json",
			name: "json",
			type: "string",
		},
		{
			baseName: "objectFieldName",
			name: "objectFieldName",
			type: "string",
		},
		{
			baseName: "valueSummary",
			name: "valueSummary",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return ObjectViewFilterColumn.attributeTypeMap;
		}
	}
