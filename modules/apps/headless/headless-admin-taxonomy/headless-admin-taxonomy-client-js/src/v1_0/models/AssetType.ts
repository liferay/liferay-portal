/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	/**
	* Represents the asset type associated with a `TaxonomyCategory`.
	*/
	export class AssetType {
			"required"?: boolean;
			"subtype"?: string;
			"type"?: string;
			"typeId"?: number;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "required",
			name: "required",
			type: "boolean",
		},
		{
			baseName: "subtype",
			name: "subtype",
			type: "string",
		},
		{
			baseName: "type",
			name: "type",
			type: "string",
		},
		{
			baseName: "typeId",
			name: "typeId",
			type: "number",
		},
		];

		static getAttributeTypeMap() {
				return AssetType.attributeTypeMap;
		}
	}
