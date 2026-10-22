/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	/**
	* Key value pair to associate detailed information with a category.
	*/
	export class TaxonomyCategoryProperty {
			"externalReferenceCode"?: string;
			"key"?: string;
			"value"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "externalReferenceCode",
			name: "externalReferenceCode",
			type: "string",
		},
		{
			baseName: "key",
			name: "key",
			type: "string",
		},
		{
			baseName: "value",
			name: "value",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return TaxonomyCategoryProperty.attributeTypeMap;
		}
	}
