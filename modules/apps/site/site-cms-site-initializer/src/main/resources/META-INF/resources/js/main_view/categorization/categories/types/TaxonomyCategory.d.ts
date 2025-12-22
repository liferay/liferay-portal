/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface TaxonomyCategory {
	actions?: Actions;
	availableLanguages?: string[];
	dateCreated?: string;
	dateModified?: string;
	description?: string;
	description_i18n?: {
		[key: string]: string;
	};
	externalReferenceCode?: string;
	id?: string;
	name: string;
	name_i18n: {
		[key: string]: string;
	};
	numberOfTaxonomyCategories?: number;
	parentTaxonomyCategory?: {
		externalReferenceCode?: string;
		id?: number;
		name?: string;
		name_i18n?: {
			[key: string]: string;
		};
	};
	parentTaxonomyVocabulary?: {
		externalReferenceCode?: string;
		id?: number;
		name?: string;
		name_i18n?: {
			[key: string]: string;
		};
	};
	siteExternalReferenceCode?: string;
	siteId?: number;
	taxonomyCategoryProperties?: TaxonomyCategoryProperty[];
	taxonomyCategoryUsageCount?: number;
	taxonomyVocabularyId?: number;
	viewableBy?: 'Anyone' | 'Members' | 'Owner';
}

interface TaxonomyCategoryProperty {
	externalReferenceCode?: string;
	key: string;
	value: string;
}
