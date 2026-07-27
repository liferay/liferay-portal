/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

			import {Creator} from './Creator';
			import {Permission} from './Permission';
			import {TaxonomyCategoryProperty} from './TaxonomyCategoryProperty';

/**
 * @author Javier Gamarra
 * @generated
 */

	/**
	* Represents a category, which is a hierarchical classification that can be associated with particular asset types. Properties follow the [category](https://schema.org/category) specification.
	*/
	export class TaxonomyCategory {
			"actions"?: {[key: string]: {[key: string]: string;};};
			"assetLibraryKey"?: string;
			"availableLanguages"?: Array<string>;
			"creator"?: Creator;
			"dateCreated"?: Date;
			"dateModified"?: Date;
			"description"?: string;
			"description_i18n"?: {[key: string]: string;};
			"externalReferenceCode"?: string;
			"friendlyUrlPath"?: string;
			"friendlyUrlPath_i18n"?: {[key: string]: string;};
			"id"?: string;
			"name"?: string;
			"name_i18n"?: {[key: string]: string;};
			"numberOfTaxonomyCategories"?: number;
			"parentTaxonomyCategory"?: object;
			"parentTaxonomyVocabulary"?: object;
			"path"?: string;
			"permissions"?: Array<Permission>;
			"siteExternalReferenceCode"?: string;
			"siteId"?: number;
			"system"?: boolean;
			"taxonomyCategoryProperties"?: Array<TaxonomyCategoryProperty>;
			"taxonomyCategoryUsageCount"?: number;
			"taxonomyVocabularyId"?: number;
			"uuid"?: string;
			"viewableBy"?: 'Anyone' | 'Members' | 'Owner';

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
			baseName: "assetLibraryKey",
			name: "assetLibraryKey",
			type: "string",
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
			baseName: "friendlyUrlPath",
			name: "friendlyUrlPath",
			type: "string",
		},
		{
			baseName: "friendlyUrlPath_i18n",
			name: "friendlyUrlPath_i18n",
			type: "{[key: string]: string;}",
		},
		{
			baseName: "id",
			name: "id",
			type: "string",
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
			baseName: "parentTaxonomyCategory",
			name: "parentTaxonomyCategory",
			type: "object",
		},
		{
			baseName: "parentTaxonomyVocabulary",
			name: "parentTaxonomyVocabulary",
			type: "object",
		},
		{
			baseName: "path",
			name: "path",
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
			baseName: "taxonomyCategoryProperties",
			name: "taxonomyCategoryProperties",
			type: "Array<TaxonomyCategoryProperty>",
		},
		{
			baseName: "taxonomyCategoryUsageCount",
			name: "taxonomyCategoryUsageCount",
			type: "number",
		},
		{
			baseName: "taxonomyVocabularyId",
			name: "taxonomyVocabularyId",
			type: "number",
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
		];

		static getAttributeTypeMap() {
				return TaxonomyCategory.attributeTypeMap;
		}
	}
