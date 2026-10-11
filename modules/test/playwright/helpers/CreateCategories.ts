/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {ApiHelpers} from './ApiHelpers';
import type {postTaxonomyVocabularyTaxonomyCategoryProps} from './HeadlessAdminTaxonomyApiHelper';

export type TCategory = Omit<
	postTaxonomyVocabularyTaxonomyCategoryProps,
	'vocabularyId'
>;

export async function createCategories({
	apiHelpers,
	assetLibraries,
	assetTypes,
	categoryNames,
	multiValued,
	siteId,
	vocabularyName,
	vocabularyVisibility,
}: {
	apiHelpers: ApiHelpers;
	assetLibraries?: AssetLibrary[];
	assetTypes?: AssetType[];
	categoryNames: TCategory[];
	multiValued?: boolean;
	siteId?: string;
	vocabularyName: string;
	vocabularyVisibility?: boolean;
}): Promise<({externalReferenceCode: string; id: number} & TCategory)[]> {
	const {id: vocabularyId} =
		await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
			assetLibraries,
			assetTypes,
			multiValued,
			name: vocabularyName,
			siteId,
			visibilityType:
				vocabularyVisibility === true ? 'INTERNAL' : 'PUBLIC',
		});

	const categories = [];
	for (const {description, name, name_i18n} of categoryNames) {
		const {externalReferenceCode, id} =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					description,
					name,
					name_i18n,
					vocabularyId,
				}
			);

		categories.push({
			description,
			externalReferenceCode,
			id,
			name,
			name_i18n,
			vocabularyId,
		});
	}

	return categories;
}
