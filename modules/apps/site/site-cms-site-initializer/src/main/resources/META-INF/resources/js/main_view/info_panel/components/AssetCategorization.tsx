/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {ComponentProps, useCallback, useEffect, useState} from 'react';

import CategorizationSuggestionService from '../../../common/services/CategorizationSuggestionService';
import CategoryService from '../../../common/services/CategoryService';
import {
	IAssetObjectEntry,
	ITaxonomyCategoryBrief,
} from '../../../common/types/AssetType';
import ObjectEntryService from '../services/ObjectEntryService';
import AssetCategorizationSections from './AssetCategorizationSections';
import {
	AUTO_CATEGORIZE_AGENT,
	COMMIT_EVENT,
	CategorizationCommitPayload,
	CategorizationCommitSuggestion,
	GENERATE_TAGS_AGENT,
} from './categorizationAgentEvents';

type Categorization = Pick<
	IAssetObjectEntry,
	'keywords' | 'taxonomyCategoryBriefs' | 'systemProperties'
>;

export type CategorizationInputSize = ComponentProps<
	typeof ClayInput
>['sizing'];

export default function AssetCategorization({
	assetLibraryId,
	categoriesErrorMessage,
	categorization,
	cmsGroupId,
	getContent,
	getObjectEntryURL,
	hasUpdatePermission,
	inputSize,
	onUpdateCategorization,
}: {
	assetLibraryId: number | string;
	categoriesErrorMessage?: string;
	categorization: Categorization;
	cmsGroupId: number | string;
	getContent?: (
		objectDefinitionExternalReferenceCode?: string
	) => Promise<string>;
	getObjectEntryURL: string;
	hasUpdatePermission: boolean;
	inputSize?: CategorizationInputSize;
	onUpdateCategorization?: (data: IAssetObjectEntry) => void;
	updateObjectEntryURL?: string;
}) {
	const [objectEntry, setObjectEntry] = useState<IAssetObjectEntry>(
		categorization as IAssetObjectEntry
	);

	const updateObjectEntry = async ({
		keywords,
		taxonomyCategoryIds,
	}: Partial<IAssetObjectEntry>): Promise<void> => {
		const error: string | null = null;
		let newObjectEntry = {
			...objectEntry,
			keywords: keywords || objectEntry.keywords,
		};

		if (taxonomyCategoryIds) {
			if (
				objectEntry.taxonomyCategoryBriefs.length >
				taxonomyCategoryIds.length
			) {
				newObjectEntry = {
					...newObjectEntry,
					taxonomyCategoryBriefs:
						objectEntry.taxonomyCategoryBriefs.filter(
							({taxonomyCategoryId: id}) =>
								taxonomyCategoryIds.includes(id)
						),
				};
			}
			else {
				const addedCategoryId: number =
					taxonomyCategoryIds[taxonomyCategoryIds.length - 1];

				const {data: newCategory} =
					await CategoryService.getCategoryById(addedCategoryId);

				if (newCategory) {
					newObjectEntry = {
						...newObjectEntry,
						taxonomyCategoryBriefs: [
							...objectEntry.taxonomyCategoryBriefs,
							{
								embeddedTaxonomyCategory: newCategory,
								taxonomyCategoryId: Number(newCategory.id),
							},
						] as ITaxonomyCategoryBrief[],
					};
				}
			}
		}

		onUpdateCategorization?.(newObjectEntry);

		if (newObjectEntry) {
			setObjectEntry(newObjectEntry);
		}
		else if (error) {
			if (keywords?.length) {
				console.error('Failed to update asset tags.', error);
			}
			else {
				console.error(error);
			}
		}
	};

	useEffect(() => {
		(async () => {
			const {data, error} =
				await ObjectEntryService.getObjectEntry(getObjectEntryURL);

			if (data) {
				let newObjectEntry = data;

				setObjectEntry((objectEntry) => {
					if (objectEntry) {
						const {keywords, taxonomyCategoryBriefs} = objectEntry;

						newObjectEntry = {
							...data,
							keywords: getUnique([
								...data.keywords,
								...keywords,
							]) as string[],
							taxonomyCategoryBriefs: getUnique([
								...data.taxonomyCategoryBriefs,
								...taxonomyCategoryBriefs,
							]) as ITaxonomyCategoryBrief[],
						};
					}

					return newObjectEntry;
				});

				onUpdateCategorization?.(newObjectEntry);
			}
			else if (error) {
				console.error(error);
			}
		})();
	}, [getObjectEntryURL, onUpdateCategorization]);

	const addCategorySuggestions = useCallback(
		async (suggestions: CategorizationCommitSuggestion[]) => {
			const currentIds = objectEntry.taxonomyCategoryBriefs.map(
				({taxonomyCategoryId}) => taxonomyCategoryId
			);

			const briefs =
				await CategorizationSuggestionService.resolveNewCategoryBriefs(
					suggestions,
					currentIds
				);

			if (!briefs.length) {
				return;
			}

			const newObjectEntry = {
				...objectEntry,
				taxonomyCategoryBriefs: [
					...objectEntry.taxonomyCategoryBriefs,
					...briefs,
				],
			};

			onUpdateCategorization?.(newObjectEntry);

			setObjectEntry(newObjectEntry);

			openToast({
				message: sub(
					Liferay.Language.get(
						'x-categories-have-been-successfully-added-to-the-selected-content'
					),
					`${briefs.length}`
				),
				type: 'success',
			});
		},
		[objectEntry, onUpdateCategorization]
	);

	const addTagSuggestions = useCallback(
		async (suggestions: CategorizationCommitSuggestion[]) => {
			const scopeId =
				(objectEntry as IAssetObjectEntry).scopeId ||
				assetLibraryId ||
				cmsGroupId;

			const names = await CategorizationSuggestionService.createTagNames(
				suggestions,
				{assetLibraryId: scopeId, cmsGroupId}
			);

			const newObjectEntry = {
				...objectEntry,
				keywords: [
					...new Set([...(objectEntry.keywords || []), ...names]),
				],
			};

			onUpdateCategorization?.(newObjectEntry);

			setObjectEntry(newObjectEntry);

			openToast({
				message: sub(
					Liferay.Language.get(
						'x-tags-have-been-successfully-added-to-the-selected-content'
					),
					`${names.length}`
				),
				type: 'success',
			});
		},
		[assetLibraryId, cmsGroupId, objectEntry, onUpdateCategorization]
	);

	useEffect(() => {
		const handleCommit = ({
			agent,
			suggestions,
		}: CategorizationCommitPayload) => {
			if (agent === AUTO_CATEGORIZE_AGENT) {
				addCategorySuggestions(suggestions);
			}
			else if (agent === GENERATE_TAGS_AGENT) {
				addTagSuggestions(suggestions);
			}
		};

		Liferay.on(COMMIT_EVENT, handleCommit);

		return () => {
			Liferay.detach(COMMIT_EVENT, handleCommit);
		};
	}, [addCategorySuggestions, addTagSuggestions]);

	if (!objectEntry) {
		return null;
	}

	return (
		<AssetCategorizationSections
			assetLibraryId={assetLibraryId}
			cmsGroupId={cmsGroupId}
			errorMessage={categoriesErrorMessage}
			getContent={getContent}
			hasUpdatePermission={hasUpdatePermission}
			inputSize={inputSize}
			objectEntry={objectEntry}
			updateObjectEntry={updateObjectEntry}
		/>
	);
}

function getUnique(
	categorization:
		| IAssetObjectEntry['keywords']
		| IAssetObjectEntry['taxonomyCategoryBriefs']
) {
	if (typeof categorization[0] === 'string') {
		return [...new Set(categorization as string[])];
	}
	else {
		return [
			...new Map(

				// @ts-ignore

				categorization.map((item) => [item.taxonomyCategoryId, item])
			).values(),
		];
	}
}
