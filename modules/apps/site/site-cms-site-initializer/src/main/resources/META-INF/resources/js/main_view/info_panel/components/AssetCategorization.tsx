/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import React, {ComponentProps, useEffect, useState} from 'react';

import CategoryService from '../../../common/services/CategoryService';
import {IAssetObjectEntry} from '../../../common/types/AssetType';
import ObjectEntryService, {
	EntryCategorizationDTO,
} from '../services/ObjectEntryService';
import AssetCategories from './AssetCategories';
import AssetTags from './AssetTags';

type Categorization = Pick<
	IAssetObjectEntry,
	'keywords' | 'taxonomyCategoryBriefs'
>;

export type CategorizationInputSize = ComponentProps<
	typeof ClayInput
>['sizing'];

export default function AssetCategorization({
	categorization,
	cmsGroupId,
	getObjectEntryURL,
	inputSize,
	onUpdateCategorization,
	updateObjectEntryURL,
}: {
	categorization?: Categorization;
	cmsGroupId: number | string;
	getObjectEntryURL: string;
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
	}: EntryCategorizationDTO): Promise<void> => {
		let newObjectEntry: IAssetObjectEntry | null = null;
		let error: string | null = null;

		if (updateObjectEntryURL && !categorization) {
			({data: newObjectEntry, error} =
				await ObjectEntryService.patchObjectEntry(
					{
						keywords: keywords || objectEntry?.keywords!,
						...(taxonomyCategoryIds ? {taxonomyCategoryIds} : {}),
					},
					updateObjectEntryURL
				));
		}
		else {
			newObjectEntry = {
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
							],
						};
					}
				}
			}

			onUpdateCategorization?.(newObjectEntry);
		}

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
							keywords: [...data.keywords, ...keywords],
							taxonomyCategoryBriefs: [
								...data.taxonomyCategoryBriefs,
								...taxonomyCategoryBriefs,
							],
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

	if (!objectEntry) {
		return null;
	}

	return (
		<>
			<AssetCategories
				cmsGroupId={cmsGroupId}
				inputSize={inputSize}
				objectEntry={objectEntry}
				updateObjectEntry={updateObjectEntry}
			/>

			<AssetTags
				cmsGroupId={cmsGroupId}
				inputSize={inputSize}
				key={objectEntry.keywords?.join(',') || 'tags'}
				objectEntry={objectEntry}
				updateObjectEntry={updateObjectEntry}
			/>
		</>
	);
}
