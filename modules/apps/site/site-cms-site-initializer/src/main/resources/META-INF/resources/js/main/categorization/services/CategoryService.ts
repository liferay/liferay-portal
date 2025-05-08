/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {HEADERS_ALL_LANGUAGES} from '../../../services/ApiHelper';

const createCategory = async (
	categoryByVocabularyIdAPIURL: string,
	category: TaxonomyCategory
) => {
	const response = await fetch(categoryByVocabularyIdAPIURL, {
		body: JSON.stringify(category),
		headers: HEADERS_ALL_LANGUAGES,
		method: 'POST',
	});

	if (response.ok) {
		return await response.json();
	}
	else {
		throw new Error(
			`POST request failed to create a new Category under 'vocabularyId = ${category.taxonomyVocabularyId}' using the following provided data: ${JSON.stringify(category)}`
		);
	}
};

const getCategory = async (
	categoryByCategoryIdAPIURL: string,
	categoryId: number
) => {
	const response = await fetch(categoryByCategoryIdAPIURL, {
		headers: HEADERS_ALL_LANGUAGES,
		method: 'GET',
	});

	if (response.ok) {
		return await response.json();
	}
	else {
		throw new Error(
			`GET request failed to fetch a Category with 'categoryId = ${categoryId}'`
		);
	}
};

const updateCategory = async (
	categoryByCategoryIdAPIURL: string,
	category: TaxonomyCategory
) => {
	const response = await fetch(categoryByCategoryIdAPIURL, {
		body: JSON.stringify(category),
		headers: HEADERS_ALL_LANGUAGES,
		method: 'PATCH',
	});

	if (response.ok) {
		return await response.json();
	}
	else {
		throw new Error(
			`PATCH request failed to update a Category with 'categoryId = ${category.id}' using the following provided data: ${JSON.stringify(category)}`
		);
	}
};

export default {createCategory, getCategory, updateCategory};
