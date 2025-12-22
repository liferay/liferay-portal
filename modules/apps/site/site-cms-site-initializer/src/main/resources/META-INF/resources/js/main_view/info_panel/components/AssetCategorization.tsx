/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import React, {ComponentProps, useEffect, useState} from 'react';

import CategoryService from '../../../common/services/CategoryService';
import {
	IAssetObjectEntry,
	ITaxonomyCategoryBrief,
} from '../../../common/types/AssetType';
import ObjectEntryService from '../services/ObjectEntryService';
import AssetCategories from './AssetCategories';
import AssetTags from './AssetTags';

type Categorization = Pick<
	IAssetObjectEntry,
	'keywords' | 'taxonomyCategoryBriefs' | 'systemProperties'
>;

export type CategorizationInputSize = ComponentProps<
	typeof ClayInput
>['sizing'];

export default function AssetCategorization({
	assetLibraryId,
	categorization,
	cmsGroupId,
	getObjectEntryURL,
	hasUpdatePermission,
	inputSize,
	onUpdateCategorization,
}: {
	assetLibraryId: number | string;
	categorization: Categorization;
	cmsGroupId: number | string;
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

	if (!objectEntry) {
		return null;
	}

	return (
		<>
			<AssetCategories
				cmsGroupId={cmsGroupId}
				hasUpdatePermission={hasUpdatePermission}
				inputSize={inputSize}
				objectEntry={objectEntry}
				updateObjectEntry={updateObjectEntry}
			/>

			<AssetTags
				assetLibraryId={assetLibraryId}
				cmsGroupId={cmsGroupId}
				hasUpdatePermission={hasUpdatePermission}
				inputSize={inputSize}
				key={objectEntry.keywords?.join(',') || 'tags'}
				objectEntry={objectEntry}
				updateObjectEntry={updateObjectEntry}
			/>
		</>
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
