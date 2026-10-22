/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class ObjectFieldSetting {
			"id"?: number;
			"name"?: string;
			"objectFieldId"?: number;
			"value"?: object;

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
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "objectFieldId",
			name: "objectFieldId",
			type: "number",
		},
		{
			baseName: "value",
			name: "value",
			type: "object",
		},
		];

		static getAttributeTypeMap() {
				return ObjectFieldSetting.attributeTypeMap;
		}
	}
