/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CandidateCategory} from '../types';
import {getTaxonomyItems} from './getTaxonomyItems';

const MAX_CANDIDATE_CATEGORIES = 150;

const TAXONOMY_ENDPOINT = '/o/headless-admin-taxonomy/v1.0';

interface GetCandidateCategoriesParams {
	classNameId?: number;
	cmsGroupId: number | string;
	max?: number;
	scopeId: number;
}

interface TaxonomyCategoryItem {
	id: number | string;
	name: string;
	parentTaxonomyVocabulary?: {name?: string};
}

export async function getCandidateCategories({
	classNameId = -1,
	cmsGroupId,
	max = MAX_CANDIDATE_CATEGORIES,
	scopeId,
}: GetCandidateCategoriesParams): Promise<CandidateCategory[]> {
	if (!Number.isInteger(scopeId)) {
		return [];
	}

	const assetTypes = ["'0'"];

	if (classNameId >= 0) {
		assetTypes.push(`'${classNameId}'`);
	}

	const filters = [`assetTypes in (${assetTypes.join(', ')})`];

	let endpoint = `asset-libraries/${scopeId}`;

	if (scopeId < 0) {
		endpoint = `sites/${cmsGroupId}`;

		filters.push(`assetLibraries in ('${scopeId}')`);
	}

	const url = `${Liferay.ThemeDisplay.getPortalURL()}${TAXONOMY_ENDPOINT}/${endpoint}/taxonomy-categories?filter=${encodeURIComponent(
		filters.join(' and ')
	)}`;

	const items = await getTaxonomyItems<TaxonomyCategoryItem>(url, max);

	return items.map((item) => ({
		id: parseInt(`${item.id}`, 10),
		name: item.name,
		vocabulary: item.parentTaxonomyVocabulary?.name ?? '',
	}));
}
