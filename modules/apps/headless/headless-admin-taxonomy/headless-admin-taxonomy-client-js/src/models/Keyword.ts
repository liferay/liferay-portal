/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {AssetLibrary} from './AssetLibrary';
			import {Creator} from './Creator';
			import {Project} from './Project';

/**
 * @author Javier Gamarra
 * @generated
 */

	/**
	* Represents a keyword that describes content. Properties follow the [keywords](https://schema.org/keywords) specification.
	*/
	export class Keyword {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"assetLibraries"?: Array<AssetLibrary>;
			"assetLibraryKey"?: string;
			"creator"?: Creator;
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"externalReferenceCode"?: string;
			"id"?: number;
			"keywordUsageCount"?: number;
			"name"?: string;
			"projects"?: Array<Project>;
			"siteExternalReferenceCode"?: string;
			"siteId"?: number;
			"subscribed"?: boolean;

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
			baseName: "assetLibraries",
			name: "assetLibraries",
			type: "Array<AssetLibrary>",
		},
		{
			baseName: "assetLibraryKey",
			name: "assetLibraryKey",
			type: "string",
		},
		{
			baseName: "creator",
			name: "creator",
			type: "Creator",
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
			baseName: "keywordUsageCount",
			name: "keywordUsageCount",
			type: "number",
		},
		{
			baseName: "name",
			name: "name",
			type: "string",
		},
		{
			baseName: "projects",
			name: "projects",
			type: "Array<Project>",
		},
		{
			baseName: "siteExternalReferenceCode",
			name: "siteExternalReferenceCode",
			type: "string",
		},
		{
			baseName: "siteId",
			name: "siteId",
			type: "number",
		},
		{
			baseName: "subscribed",
			name: "subscribed",
			type: "boolean",
		},
		];

		static getAttributeTypeMap() {
				return Keyword.attributeTypeMap;
		}
	}
