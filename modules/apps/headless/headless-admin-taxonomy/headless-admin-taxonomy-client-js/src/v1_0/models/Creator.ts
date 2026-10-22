/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */


/**
 * @author Javier Gamarra
 * @generated
 */

	/**
	* Represents the user who created the content. Properties follow the [creator](https://schema.org/creator) specification.
	*/
	export class Creator {
			"additionalName"?: string;
			"contentType"?: string;
			"externalReferenceCode"?: string;
			"familyName"?: string;
			"givenName"?: string;
			"id"?: number;
			"image"?: string;
			"name"?: string;
			"profileURL"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "additionalName",
			name: "additionalName",
			type: "string",
		},
		{
			baseName: "contentType",
			name: "contentType",
			type: "string",
		},
		{
			baseName: "externalReferenceCode",
			name: "externalReferenceCode",
			type: "string",
		},
		{
			baseName: "familyName",
			name: "familyName",
			type: "string",
		},
		{
			baseName: "givenName",
			name: "givenName",
			type: "string",
		},
		{
			baseName: "id",
			name: "id",
			type: "number",
		},
		{
			baseName: "image",
			name: "image",
			type: "string",
		},
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "profileURL",
			name: "profileURL",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return Creator.attributeTypeMap;
		}
	}
