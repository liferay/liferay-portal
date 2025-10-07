/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import React, {ComponentProps, useEffect, useState} from 'react';

import {IAssetObjectEntry} from '../../../common/types/AssetType';
import ObjectEntryService, {
	EntryCategorizationDTO,
} from '../services/ObjectEntryService';
import AssetCategories from './AssetCategories';
import AssetTags from './AssetTags';

export type CategorizationInputSize = ComponentProps<
	typeof ClayInput
>['sizing'];

export default function AssetCategorization({
	cmsGroupId,
	getObjectEntryURL,
	inputSize,
	onUpdateCategorization,
	updateObjectEntryURL,
}: {
	cmsGroupId: number | string;
	getObjectEntryURL: string;
	inputSize?: CategorizationInputSize;
	onUpdateCategorization?: (data: IAssetObjectEntry) => void;
	updateObjectEntryURL: string;
}) {
	const [objectEntry, setObjectEntry] = useState<IAssetObjectEntry | null>(
		null
	);

	const updateObjectEntry = async ({
		keywords,
		taxonomyCategoryIds,
	}: EntryCategorizationDTO): Promise<void> => {
		const {data, error} = await ObjectEntryService.patchObjectEntry(
			{
				keywords: keywords || objectEntry?.keywords!,
				...(taxonomyCategoryIds ? {taxonomyCategoryIds} : {}),
			},
			updateObjectEntryURL
		);

		if (data) {
			setObjectEntry(data);

			onUpdateCategorization?.(data);
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
				setObjectEntry(data);

				onUpdateCategorization?.(data);
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
				objectEntry={objectEntry}
				updateObjectEntry={updateObjectEntry}
			/>
		</>
	);
}
