/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	export class Filter {
			"maxResults"?: number;
			"supported"?: boolean;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "maxResults",
			name: "maxResults",
			type: "number",
		},
		{
			baseName: "supported",
			name: "supported",
			type: "boolean",
		},
		];

		static getAttributeTypeMap() {
				return Filter.attributeTypeMap;
		}
	}
