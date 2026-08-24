/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

interface postSiteTaxonomyVocabularyProps {
	assetLibraries?: AssetLibrary[];
	assetTypes?: AssetType[];
	externalReferenceCode?: string;
	multiValued?: boolean;
	name: string;
	siteId: string;
	system?: boolean;
	visibilityType?: string;
}

export interface postTaxonomyCategoryTaxonomyCategory {
	name: string;
	name_i18n?: {[key: string]: string};
	parentTaxonomyCategoryId: number;
}

export interface postTaxonomyVocabularyProps {
	assetLibraries?: AssetLibrary[];
	assetTypes?: AssetType[];
	name: string;
	name_i18n?: {[key: string]: string};
	visibilityType?: string;
}

export interface postTaxonomyVocabularyTaxonomyCategoryProps {
	name: string;
	name_i18n?: {[key: string]: string};
	system?: boolean;
	vocabularyId: number;
}

export type TTaxonomyVocabulary = {
	externalReferenceCode: string;
	id: number;
	name: string;
};

interface patchTaxonomyCategoryProps {
	friendlyUrlPath?: string;
	id: number;
	name?: string;
}

interface postAssetLibraryKeywordProps {
	depotEntryId: string;
	name: string;
}

interface postSiteKeywordProps {
	assetLibraries?: Array<{
		externalReferenceCode?: string;
		id?: number;
		scopeKey?: string;
	}>;
	name: string;
	siteId: string;
}

interface putTaxonomyCategoriesTaxonomyCategoryPermissions {
	actionIds: string[];
	roleName: string;
}

interface putTaxonomyVocabulariesTaxonomyVocabularyPermissions {
	actionIds: string[];
	roleName: string;
}

export class HeadlessAdminTaxonomyApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-admin-taxonomy/v1.0';
	}

	/**
	 * It allows deleting a vocabulary.
	 *
	 * @param vocabularyId the vocabulary id
	 */

	async deleteTaxonomyVocabulary(vocabularyId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-vocabularies/${vocabularyId}`
		);
	}

	/**
	 * It allows getting a category by vocabulary.
	 *
	 * @param name the name of the category
	 * @param vocabularyId the parent vocabulary id
	 */

	async getTaxonomyCategoryByVocabularyId(vocabularyId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-vocabularies/${vocabularyId}/taxonomy-categories`
		);
	}

	/**
	 * It allows getting a vocabulary by site.
	 *
	 * @param siteId the id of the site in which the vocabulary will be created
	 */

	async getTaxonomyVocabularyBySiteId(siteId: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/taxonomy-vocabularies`
		);
	}

	/**
	 * It allows creating a vocabulary inside a site.
	 *
	 * @param siteId the id of the site in which the vocabulary will be created
	 * @param name the name of the vocabulary
	 * @param [assetTypes] the asset types to which the vocabulary can be used
	 */

	async postSiteTaxonomyVocabulary({
		assetLibraries,
		assetTypes,
		externalReferenceCode,
		multiValued = true,
		name,
		siteId,
		system,
		visibilityType,
	}: postSiteTaxonomyVocabularyProps): Promise<TTaxonomyVocabulary> {
		const taxonomyVocabulary = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/taxonomy-vocabularies`,
			{
				data: {
					assetLibraries,
					assetTypes,
					externalReferenceCode,
					multiValued,
					name,
					system,
					visibilityType,
				},
			}
		);

		// A system vocabulary cannot be deleted while its feature flag is
		// enabled, so it is not auto-tracked. Callers must clean it up
		// explicitly (for example, by disabling the flag first).

		if (!system && this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: taxonomyVocabulary.id,
				type: 'taxonomyVocabulary',
			});
		}

		return taxonomyVocabulary;
	}

	/**
	 * It allows creating a subcategory inside a category.
	 *
	 * @param name the name of the subcategory
	 * @param vocabularyId the parent vocabulary id
	 */

	async postTaxonomyCategoryTaxonomyCategory({
		name,
		name_i18n,
		parentTaxonomyCategoryId,
	}: postTaxonomyCategoryTaxonomyCategory): Promise<{id: number}> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-categories/${parentTaxonomyCategoryId}/taxonomy-categories`,
			{data: {name, name_i18n}}
		);
	}

	/**
	 * It allows creating a category inside a vocabulary.
	 *
	 * @param name the name of the category
	 * @param vocabularyId the parent vocabulary id
	 */

	async postTaxonomyVocabularyTaxonomyCategory({
		name,
		name_i18n,
		system,
		vocabularyId,
	}: postTaxonomyVocabularyTaxonomyCategoryProps): Promise<{
		externalReferenceCode: string;
		id: number;
	}> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-vocabularies/${vocabularyId}/taxonomy-categories`,
			{data: {name, name_i18n, system}}
		);
	}

	/**
	 * It allows partially update a category name
	 *
	 * @param name the new name of the category
	 * @param id the category id
	 */

	async patchTaxonomyCategory({
		friendlyUrlPath,
		id,
		name,
	}: patchTaxonomyCategoryProps): Promise<{id: number}> {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-categories/${id}`,
			{friendlyUrlPath, name}
		);
	}

	/**
	 * It allows creating a tag inside a site.
	 *
	 * @param name the name of the tag
	 * @param siteId the id of the site in which the tag will be created
	 * @param assetLibraries the spaces the tag is scoped to (CMS only); omit to
	 * make the tag available in all spaces
	 */

	async postSiteKeyword({
		assetLibraries,
		name,
		siteId,
	}: postSiteKeywordProps): Promise<{
		externalReferenceCode: string;
		id: number;
	}> {
		const keyword = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/keywords`,
			{data: {assetLibraries, name}}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: keyword.id,
				type: 'keyword',
			});
		}

		return keyword;
	}

	/**
	 * It allows creating a tag inside an asset library
	 *
	 * @param name the name of the tag
	 * @param assetLibraryId the id of the asset library in which the tag will be created
	 */

	async postAssetLibraryKeyword({
		depotEntryId,
		name,
	}: postAssetLibraryKeywordProps): Promise<{id: number}> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${depotEntryId}/keywords`,
			{data: {name}}
		);
	}

	/**
	 * It allows deleting a tag.
	 *
	 * @param id the id of the tag
	 */

	async deleteKeyword({id}: {id: number}) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/keywords/${id}`
		);
	}

	/**
	 * It allows to add permission to a taxonomy category.
	 *
	 * @param id the id of the tag
	 * @param actionIds the actionIds of the user
	 * @param roleName the roleName of the user
	 */

	async putTaxonomyCategoriesTaxonomyCategoryPermissions(
		id: number,
		{actionIds, roleName}: putTaxonomyCategoriesTaxonomyCategoryPermissions
	) {
		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-categories/${id}/permissions`,
			{
				data: [
					{
						actionIds,
						roleName,
					},
				],
			}
		);
	}

	/**
	 * It allows to add permission to a taxonomy vocabulary.
	 *
	 * @param id the id of the tag
	 * @param actionIds the actionIds of the user
	 * @param roleName the roleName of the user
	 */

	async putTaxonomyVocabulariesTaxonomyVocabularyPermissions(
		id: number,
		{
			actionIds,
			roleName,
		}: putTaxonomyVocabulariesTaxonomyVocabularyPermissions
	) {
		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-vocabularies/${id}/permissions`,
			{
				data: [
					{
						actionIds,
						roleName,
					},
				],
			}
		);
	}
}
