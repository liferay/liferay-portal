/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {Permission} from './Permission';

/**
 * @author Alejandro Tardín
 * @generated
 */

	export class ERCSiteTestEntity {
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"description"?: string;
			"externalReferenceCode"?: string;
			"permissions"?: Array<Permission>;
			"siteExternalReferenceCode"?: string;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "dateCreated",
			name: "dateCreated",
			type: "Date",
		},
		{
			baseName: "dateModified",
			name: "dateModified",
			type: "Date",
		},
		{
			baseName: "description",
			name: "description",
			type: "string",
		},
		{
			baseName: "externalReferenceCode",
			name: "externalReferenceCode",
			type: "string",
		},
		{
			baseName: "permissions",
			name: "permissions",
			type: "Array<Permission>",
		},
		{
			baseName: "siteExternalReferenceCode",
			name: "siteExternalReferenceCode",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return ERCSiteTestEntity.attributeTypeMap;
		}
	}
