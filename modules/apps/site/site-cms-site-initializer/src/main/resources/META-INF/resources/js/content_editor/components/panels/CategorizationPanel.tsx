/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback} from 'react';

import {IAssetObjectEntry} from '../../../common/types/AssetType';
import AssetCategorization from '../../../main_view/info_panel/components/AssetCategorization';
import {
	CategorizationFields,
	UpdateCategorizationProps,
} from '../ContentEditorSidePanel';

export default function CategorizationPanel({
	categorizationFields,
	contentAPIURL,
	groupId,
	onUpdateCategorization,
}: {
	categorizationFields: CategorizationFields;
	contentAPIURL: string;
	groupId: number | string;
	onUpdateCategorization: (props: UpdateCategorizationProps) => void;
}) {
	const {assetCategoryIds, assetTagNames} = categorizationFields;

	const updateCategorization = useCallback(
		({keywords = [], taxonomyCategoryBriefs = []}: IAssetObjectEntry) => {
			const fields: CategorizationFields = {
				assetCategoryIds: {
					serverValue: taxonomyCategoryBriefs
						.map(({taxonomyCategoryId: id}) => id)
						.join(','),
					value: taxonomyCategoryBriefs,
				},
				assetTagNames: {
					serverValue: keywords.join(','),
					value: keywords,
				},
			};

			(Object.entries(fields) as UpdateCategorizationProps[]).forEach(
				onUpdateCategorization
			);
		},
		[onUpdateCategorization]
	);

	return (
		<div className="px-3">
			<AssetCategorization
				categorization={{
					keywords: assetTagNames.value,
					taxonomyCategoryBriefs: assetCategoryIds.value,
				}}
				cmsGroupId={groupId}
				getObjectEntryURL={contentAPIURL}
				inputSize="sm"
				onUpdateCategorization={updateCategorization}
			/>
		</div>
	);
}
