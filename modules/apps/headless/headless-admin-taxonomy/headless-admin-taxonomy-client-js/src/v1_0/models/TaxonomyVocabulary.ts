/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {AssetLibrary} from './AssetLibrary';
			import {AssetType} from './AssetType';
			import {Creator} from './Creator';
			import {Permission} from './Permission';
			import {Project} from './Project';

/**
 * @author Javier Gamarra
 * @generated
 */

	/**
	* Represents a vocabulary, which is a grouping of categories for a specific purpose (e.g., classification, sorting, etc.).
	*/
	export class TaxonomyVocabulary {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"assetLibraries"?: Array<AssetLibrary>;
			"assetLibraryKey"?: string;
			"assetTypes"?: Array<AssetType>;
			"availableLanguages"?: Array<string>;
			"creator"?: Creator;
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"description"?: string;
			"description_i18n"?: {[key: string]: string;};
			"externalReferenceCode"?: string;
			"id"?: number;
			"multiValued"?: boolean;
			"name"?: string;
			"name_i18n"?: {[key: string]: string;};
			"numberOfTaxonomyCategories"?: number;
			"permissions"?: Array<Permission>;
			"projects"?: Array<Project>;
			"siteExternalReferenceCode"?: string;
			"siteId"?: number;
			"system"?: boolean;
			"uuid"?: string;
			"viewableBy"?: 'Anyone' | 'Members' | 'Owner';
			"visibilityType"?: 'EMPTY' | 'INTERNAL' | 'PUBLIC';

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
			baseName: "assetTypes",
			name: "assetTypes",
			type: "Array<AssetType>",
		},
		{
			baseName: "availableLanguages",
			name: "availableLanguages",
			type: "Array<string>",
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
			baseName: "description",
			name: "description",
			type: "string",
		},
		{
			baseName: "description_i18n",
			name: "description_i18n",
			type: "{[key: string]: string;}",
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
			baseName: "multiValued",
			name: "multiValued",
			type: "boolean",
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
			baseName: "numberOfTaxonomyCategories",
			name: "numberOfTaxonomyCategories",
			type: "number",
		},
		{
			baseName: "permissions",
			name: "permissions",
			type: "Array<Permission>",
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
			baseName: "system",
			name: "system",
			type: "boolean",
		},
		{
			baseName: "uuid",
			name: "uuid",
			type: "string",
		},
		{
			baseName: "viewableBy",
			name: "viewableBy",
			type: "'Anyone' | 'Members' | 'Owner'",
		},
		{
			baseName: "visibilityType",
			name: "visibilityType",
			type: "'EMPTY' | 'INTERNAL' | 'PUBLIC'",
		},
		];

		static getAttributeTypeMap() {
				return TaxonomyVocabulary.attributeTypeMap;
		}
	}
