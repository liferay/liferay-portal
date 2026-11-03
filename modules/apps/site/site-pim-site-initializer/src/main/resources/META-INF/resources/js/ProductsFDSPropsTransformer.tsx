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
	deleteItemAction,
	executeBulkDeleteAction,
	getFormattedLabel,
	getScopeExternalReferenceCode,
	openAssetUsageListModal,
	styleDeleteAction,
	transformFDSBulkActions,
} from '@liferay/site-cms-site-initializer';
import {sub} from 'frontend-js-web';
import React from 'react';

import ProductTitleRenderer from './cell_renderers/ProductTitleRenderer';

export default function propsTransformer({
	additionalProps = {},
	bulkActions = [],
	creationMenu,
	itemsActions,
	...props
}: {
	additionalProps?: {brokenLinksCheckerEnabled?: boolean};
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
		async onActionDropdownItemClick({
			action,
			event,
			itemData,
			loadData,
		}: {
			action: any;
			event: Event;
			itemData: any;
			loadData: () => {};
		}) {
			if (action?.data?.id !== 'delete') {
				return;
			}

			event?.preventDefault();

			const confirmationMessage = sub(
				Liferay.Language.get('delete-asset-confirmation-body'),
				getFormattedLabel(
					itemData.title ||
						itemData.embedded?.title ||
						Liferay.Language.get('untitled-asset')
				)
			);

			if (additionalProps.brokenLinksCheckerEnabled) {
				openAssetUsageListModal({
					itemsData: [itemData],
					onDelete: async () => {
						await deleteItemAction(
							confirmationMessage,
							itemData,
							loadData
						);
					},
					selectAll: false,
				});
			}
			else {
				await deleteItemAction(confirmationMessage, itemData, loadData);
			}
		},
		onBulkActionItemClick: ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			if (action?.data?.id !== 'delete') {
				return;
			}

			if (additionalProps.brokenLinksCheckerEnabled) {
				openAssetUsageListModal({
					apiURL: props.apiURL,
					itemsData: selectedData.items,
					onDelete: () => {
						executeBulkDeleteAction(
							props.apiURL || '',
							props.id || '',
							selectedData
						);
					},
					onSkip: () => {
						deleteAssetEntriesBulkAction({
							apiURL: props.apiURL,
							dataSetId: props.id,
							selectedData,
						});
					},
					selectAll: selectedData.selectAll,
				});
			}
			else {
				deleteAssetEntriesBulkAction({
					apiURL: props.apiURL,
					dataSetId: props.id,
					selectedData,
				});
			}
		},
	};
}
