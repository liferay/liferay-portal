/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectViewColumn {
			"id"?: number;
			"label"?: {[key: string]: string;};
			"objectFieldName"?: string;
			"priority"?: number;

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
			baseName: "label",
			name: "label",
			type: "{[key: string]: string;}",
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
		];

		static getAttributeTypeMap() {
				return ObjectViewColumn.attributeTypeMap;
		}
	}
