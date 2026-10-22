/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {Status} from './Status';

/**
 * @author David Truong
 * @generated
 */

	/**
	* Represents publications that have been published.
	*/
	export class CTProcess {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"ctCollectionId"?: number;
			"datePublished"?: Date;
			"description"?: string;
			"id"?: number;
			"name"?: string;
			"ownerName"?: string;
			"status"?: Status;

		static "discriminator": string | undefined = undefined;

	static "attributeTypeMap": Array<{
		baseName: string;
		name: string;
		type: string;
	}> = [
		{
			baseName: "actions",
			name: "actions",
			type: "{[key: string]: {[key: string]: string;};}",
		},
		{
			baseName: "ctCollectionId",
			name: "ctCollectionId",
			type: "number",
		},
		{
			baseName: "datePublished",
			name: "datePublished",
			type: "Date",
		},
		{
			baseName: "description",
			name: "description",
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
			baseName: "ownerName",
			name: "ownerName",
			type: "string",
		},
		{
			baseName: "status",
			name: "status",
			type: "Status",
		},
		];

		static getAttributeTypeMap() {
				return CTProcess.attributeTypeMap;
		}
	}
