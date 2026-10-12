/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ACTIONS,
	AuthorRenderer,
	SpaceRendererWithCache,
	StatusLabel,
	addOnClickToCreationMenuItems,
	deleteAssetEntriesBulkAction,
	getScopeExternalReferenceCode,
	styleDeleteAction,
	transformFDSBulkActions,
} from '@liferay/site-cms-site-initializer';
import React from 'react';

import ProductTitleRenderer from './cell_renderers/ProductTitleRenderer';

export default function propsTransformer({
	bulkActions = [],
	creationMenu,
	itemsActions,
	...props
}: {
	apiURL?: string;
	bulkActions?: any[];
	creationMenu: any;
	id?: string;
	itemsActions?: any[];
}) {
	return {
		...props,
		bulkActions: transformFDSBulkActions(bulkActions),
		creationMenu: creationMenu && {
			...creationMenu,
			primaryItems: addOnClickToCreationMenuItems(
				creationMenu.primaryItems,
				ACTIONS
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				},
				{
					component: ProductTitleRenderer,
					name: 'titleTableCellRenderer',
					type: 'internal',
				},
				{
					component: ({itemData}: {itemData: any}) => (
						<SpaceRendererWithCache
							scopeKey={itemData.embedded.scopeKey}
							spaceExternalReferenceCode={getScopeExternalReferenceCode(
								itemData
							)}
						/>
					),
					name: 'spaceTableCellRenderer',
					type: 'internal',
				},
				{
					component: ({itemData, value}: any) => (
						<StatusLabel
							expirationDate={
								itemData?.embedded?.expirationDate ?? undefined
							}
							label={value?.label}
						/>
					),
					name: 'statusTableCellRenderer',
					type: 'internal',
				},
			],
		},
		hideManagementBarInEmptyState: true,
		itemsActions: itemsActions?.map(styleDeleteAction),
		onBulkActionItemClick: ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			if (action?.data?.id === 'delete') {
				deleteAssetEntriesBulkAction({
					apiURL: props.apiURL,
					dataSetId: props.id,
					selectedData,
				});
			}
		},
	};
}
