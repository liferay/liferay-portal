/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataApiHelpers} from '../helpers/ApiHelpers';

/**
 * Resolves an asset category id from its vocabulary and category names, within
 * a given site's taxonomy.
 */
export async function getTaxonomyCategoryId(
	apiHelpers: DataApiHelpers,
	siteId: string,
	vocabularyName: string,
	categoryName: string
): Promise<number> {
	const {items: vocabularies} =
		await apiHelpers.headlessAdminTaxonomy.getTaxonomyVocabularyBySiteId(
			siteId
		);

	const vocabulary = vocabularies.find(
		(item) => item.name === vocabularyName
	);

	const {items: categories} =
		await apiHelpers.headlessAdminTaxonomy.getTaxonomyCategoryByVocabularyId(
			vocabulary.id
		);

	return categories.find((item) => item.name === categoryName).id;
}
