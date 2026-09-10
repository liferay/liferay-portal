/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	export class OneOfPropertyTestEntity {
			"name"?: string;
			"oneOfProperty"?: any;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "oneOfProperty",
			name: "oneOfProperty",
			type: "any",
		},
		];

		static getAttributeTypeMap() {
				return OneOfPropertyTestEntity.attributeTypeMap;
		}
	}
