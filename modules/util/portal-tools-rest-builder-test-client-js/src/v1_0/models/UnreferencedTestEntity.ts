/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	export class UnreferencedTestEntity {
			"description"?: string;
			"id"?: number;
			"property_with_hyphens"?: string;

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
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "property_with_hyphens",
			name: "property_with_hyphens",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return UnreferencedTestEntity.attributeTypeMap;
		}
	}
