/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Alejandro Tardín
 * @generated
 */

	/**
	* https://www.schema.org/Document
	*/
	export class BatchTestEntity {
			"externalReferenceCode"?: string;
			"id"?: number;
			"name"?: string;

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
		];

		static getAttributeTypeMap() {
				return BatchTestEntity.attributeTypeMap;
		}
	}
