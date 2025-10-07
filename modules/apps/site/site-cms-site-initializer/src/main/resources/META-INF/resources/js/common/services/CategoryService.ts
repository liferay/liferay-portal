/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from './ApiHelper';

const createCategory = async (
	categoryByVocabularyIdAPIURL: string,
	category: TaxonomyCategory
) => {
	return await ApiHelper.post<TaxonomyCategory>(
		categoryByVocabularyIdAPIURL,
		category
	);
};

const getCategory = async (categoryByCategoryIdAPIURL: string) => {
	return await ApiHelper.get<TaxonomyCategory>(categoryByCategoryIdAPIURL);
};

/**
 * Updates the TaxonomyCategory specified by the provided ID in the API URL.
 * Defaults to a 'PUT' request unless specified to 'PATCH'.
 *
 * @param categoryByCategoryIdAPIURL API URL with the ID of the category being updated
 * @param category the updated category data
 * @param updateMethod whether to partially update or replace the category. Defaults to 'PUT'.
 */
const updateCategory = async (
	categoryByCategoryIdAPIURL: string,
	category: TaxonomyCategory | Partial<TaxonomyCategory>
) => {
	return await ApiHelper.put<TaxonomyCategory>(
		categoryByCategoryIdAPIURL,
		category
	);
};

export default {createCategory, getCategory, updateCategory};
