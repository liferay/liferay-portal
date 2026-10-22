/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	export class AssetLibrary {
			"externalReferenceCode"?: string;
			"id"?: number;
			"name"?: string;
			"name_i18n"?: {[key: string]: string;};
			"scopeKey"?: string;

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
			baseName: "name_i18n",
			name: "name_i18n",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "scopeKey",
			name: "scopeKey",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return AssetLibrary.attributeTypeMap;
		}
	}
