/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class Permission {
			"actionIds"?: Array<string>;
			"roleName"?: string;
			"xml"?: object;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "actionIds",
			name: "actionIds",
			type: "Array<string>",
		},
		{
			baseName: "roleName",
			name: "roleName",
			type: "string",
		},
		{
			baseName: "xml",
			name: "xml",
			type: "object",
		},
		];

		static getAttributeTypeMap() {
				return Permission.attributeTypeMap;
		}
	}
