/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getGlobalSiteId from '../../../../utils/getGlobalSiteId';
import {pushToApiHelpersData} from '../../../../utils/pushToApiHelpersData';

export default async function addGlobalCategories({
	apiHelpers,
	names,
	vocabularyName,
}: {
	apiHelpers: DataApiHelpers;
	names: string[];
	vocabularyName: 'Audience' | 'Stage' | 'Topic';
}) {
	const globalSiteId = await getGlobalSiteId(apiHelpers);

	const vocabulary =
		await apiHelpers.headlessAdminTaxonomy.getSiteTaxonomyVocabularyByExternalReferenceCode(
			globalSiteId,
			`L_${vocabularyName.toUpperCase()}`
		);

	const categories: {id: number; name: string}[] = [];

	for (const name of names) {
		const category =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{name, vocabularyId: vocabulary.id}
			);

		pushToApiHelpersData(apiHelpers, [category.id], 'taxonomyCategory');

		categories.push({id: category.id, name});
	}

	return categories;
}
