/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from './ApiHelpers';

interface postSiteTaxonomyVocabularyProps {
	assetTypes?: AssetType[];
	name: string;
	siteId: string;
	visibilityType?: string;
}

export interface postTaxonomyCategoryTaxonomyCategory {
	name: string;
	name_i18n?: {['ES-es']: string};
	parentTaxonomyCategoryId: number;
}

export interface postTaxonomyVocabularyProps {
	assetLibraries?: AssetLibrary[];
	assetTypes?: AssetType[];
	name: string;
	name_i18n?: {['ES-es']: string};
	visibilityType?: string;
}

export interface postTaxonomyVocabularyTaxonomyCategoryProps {
	name: string;
	name_i18n?: {['ES-es']: string};
	vocabularyId: number;
}

interface patchTaxonomyCategoryProps {
	id: number;
	name: string;
}

interface postAssetLibraryKeywordProps {
	depotEntryId: string;
	name: string;
}

interface postSiteKeywordProps {
	name: string;
	siteId: string;
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
		assetTypes,
		name,
		siteId,
		visibilityType,
	}: postSiteTaxonomyVocabularyProps): Promise<{id: number}> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/taxonomy-vocabularies`,
			{data: {assetTypes, name, visibilityType}}
		);
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
	 * It allows creating a vocabulary.
	 *
	 * @param name the name of the vocabulary
	 * @param assetLibraries the asset libraries where the vocabulary will be available
	 */

	async postTaxonomyVocabulary({
		assetLibraries,
		name,
		name_i18n,
		visibilityType,
	}: postTaxonomyVocabularyProps): Promise<{id: number}> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-vocabularies`,
			{data: {assetLibraries, name, name_i18n, visibilityType}}
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
		vocabularyId,
	}: postTaxonomyVocabularyTaxonomyCategoryProps): Promise<{id: number}> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-vocabularies/${vocabularyId}/taxonomy-categories`,
			{data: {name, name_i18n}}
		);
	}

	/**
	 * It allows partially update a category name
	 *
	 * @param name the new name of the category
	 * @param id the category id
	 */

	async patchTaxonomyCategory({
		id,
		name,
	}: patchTaxonomyCategoryProps): Promise<{id: number}> {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/taxonomy-categories/${id}`,
			{name}
		);
	}

	/**
	 * It allows creating a tag inside a site.
	 *
	 * @param name the name of the tag
	 * @param siteId the id of the site in which the tag will be created
	 */

	async postSiteKeyword({
		name,
		siteId,
	}: postSiteKeywordProps): Promise<{id: number}> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/keywords`,
			{data: {name}}
		);
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
}
