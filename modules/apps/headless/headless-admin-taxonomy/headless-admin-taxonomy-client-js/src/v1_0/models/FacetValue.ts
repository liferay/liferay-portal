/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class FacetValue {
			"numberOfOccurrences"?: number;
			"term"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "numberOfOccurrences",
			name: "numberOfOccurrences",
			type: "number",
		},
		{
			baseName: "term",
			name: "term",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return FacetValue.attributeTypeMap;
		}
	}
