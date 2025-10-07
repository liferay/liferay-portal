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
	contentAPIURL,
	groupId,
	onUpdateCategorization,
}: {
	contentAPIURL: string;
	groupId: number | string;
	onUpdateCategorization: (props: UpdateCategorizationProps) => void;
}) {
	const updateCategorization = useCallback(
		({keywords = [], taxonomyCategoryBriefs = []}: IAssetObjectEntry) => {
			const fields: {
				name: keyof CategorizationFields;
				value: string;
			}[] = [
				{
					name: 'assetCategoryIds',
					value: taxonomyCategoryBriefs
						.map(({taxonomyCategoryId: id}) => id)
						.join(','),
				},
				{
					name: 'assetTagNames',
					value: keywords.join(','),
				},
			];

			fields.forEach(onUpdateCategorization);
		},
		[onUpdateCategorization]
	);

	return (
		<div className="px-3">
			<AssetCategorization
				cmsGroupId={groupId}
				getObjectEntryURL={contentAPIURL}
				inputSize="sm"
				onUpdateCategorization={updateCategorization}
				updateObjectEntryURL={contentAPIURL}
			/>
		</div>
	);
}
