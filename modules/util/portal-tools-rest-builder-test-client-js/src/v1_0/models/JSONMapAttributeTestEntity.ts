/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	/**
	* Test Component to test the generation of getValue method on Entities when one or multiple JSON Maps are present.
	*/
	export class JSONMapAttributeTestEntity {
			"description"?: string;
			"name"?: string;
			"properties1"?: {[key: string]: object;};
			"properties2"?: {[key: string]: object;};

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "description",
			name: "description",
			type: "string",
		},
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "properties1",
			name: "properties1",
			type: "{[key: string]: object;}",
		},
		{
			baseName: "properties2",
			name: "properties2",
			type: "{[key: string]: object;}",
		},
		];

		static getAttributeTypeMap() {
				return JSONMapAttributeTestEntity.attributeTypeMap;
		}
	}
