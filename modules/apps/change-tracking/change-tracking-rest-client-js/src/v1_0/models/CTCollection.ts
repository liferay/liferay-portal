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
	* Represents a set of changes tracked for a publication.
	*/
	export class CTCollection {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"dateScheduled"?: Date;
			"description"?: string;
			"externalReferenceCode"?: string;
			"id"?: number;
			"name"?: string;
			"ownerName"?: string;
			"status"?: Status;
			"statusMessage"?: string;

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
			baseName: "dateScheduled",
			name: "dateScheduled",
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
		{
			baseName: "statusMessage",
			name: "statusMessage",
			type: "string",
		},
		];

		static getAttributeTypeMap() {
				return CTCollection.attributeTypeMap;
		}
	}
