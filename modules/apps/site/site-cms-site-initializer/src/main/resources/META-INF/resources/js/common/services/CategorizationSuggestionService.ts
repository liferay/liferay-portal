/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ITaxonomyCategoryBrief} from '../types/AssetType';
import CategoryService from './CategoryService';
import TagService from './TagService';

export interface CategorizationCommitSuggestion {
	id?: number;
	isNew?: boolean;
	name: string;
}

async function createTagNames(
	suggestions: CategorizationCommitSuggestion[],
	{
		assetLibraryId,
		cmsGroupId,
	}: {
		assetLibraryId: number | string | null | undefined;
		cmsGroupId: number | string;
	}
): Promise<string[]> {
	return (
		await Promise.all(
			suggestions.map(async (suggestion) => {
				if (suggestion.isNew) {
					const {data, status} = await TagService.createTag({
						assetLibraryId,
						cmsGroupId,
						name: suggestion.name,
					});

					if (data?.name) {
						return data.name;
					}

					return status === 'CONFLICT' ? suggestion.name : null;
				}

				return suggestion.name;
			})
		)
	).filter((name): name is string => Boolean(name));
}

async function resolveNewCategoryBriefs(
	suggestions: CategorizationCommitSuggestion[],
	currentCategoryIds: number[]
): Promise<ITaxonomyCategoryBrief[]> {
	const newSuggestions = suggestions.filter(
		(suggestion) =>
			typeof suggestion.id === 'number' &&
			!currentCategoryIds.includes(suggestion.id)
	);

	if (!newSuggestions.length) {
		return [];
	}

	return (
		await Promise.all(
			newSuggestions.map(async (suggestion) => {
				const {data} = await CategoryService.getCategoryById(
					suggestion.id as number
				);

				return data
					? {
							embeddedTaxonomyCategory: data,
							taxonomyCategoryId: Number(data.id),
						}
					: null;
			})
		)
	).filter(Boolean) as ITaxonomyCategoryBrief[];
}

export default {createTagNames, resolveNewCategoryBriefs};
