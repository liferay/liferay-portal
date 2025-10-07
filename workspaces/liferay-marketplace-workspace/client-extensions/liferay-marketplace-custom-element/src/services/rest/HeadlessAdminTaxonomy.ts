/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '../../liferay/liferay';
import fetcher from '../fetcher';

export default class HeadlessAdminTaxonomy {
	static async getTaxonomyVocabularies() {
		return fetcher(
			`/o/headless-admin-taxonomy/v1.0/sites/${Liferay.ThemeDisplay.getCompanyGroupId()}/taxonomy-vocabularies`
		);
	}

	static async getSiteTaxonomyVocabulariesWithCategories() {
		const response = await fetcher.post<{
			data: {taxonomyVocabularies: APIResponse<TaxonomyVocabulary>};
		}>('/o/graphql', {
			query: `{
				taxonomyVocabularies: siteTaxonomyVocabularies(siteKey: "${Liferay.ThemeDisplay.getScopeGroupId()}") {
					items {
						id
						name
						taxonomyCategories {
							items {
								externalReferenceCode
								id
								name
							}
						}
					}
					lastPage
					page
					pageSize
					totalCount
				}
			}`,
		});

		return response.data.taxonomyVocabularies;
	}

	static async getTaxonomyCategories(
		vocabularyId: number,
		searchParams = new URLSearchParams()
	) {
		return fetcher(
			`/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${vocabularyId}/taxonomy-categories?${searchParams.toString()}`
		);
	}
}
