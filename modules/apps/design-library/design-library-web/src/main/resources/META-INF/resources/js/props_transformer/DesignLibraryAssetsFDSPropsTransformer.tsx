/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBulkActionItem,
	IFrontendDataSetProps,
} from '@liferay/frontend-data-set-web';
import React from 'react';

import {TableCellContentType} from '../constants';
import getCreationMenuItems from '../getCreationMenuItems';
import {ActionItem, DesignAsset, DesignLibraryResourceType} from '../types';
import confirmAndDeleteEntriesAction from './actions/confirmAndDeleteEntriesAction';
import getDesignAssetsConfirmationMessage from './actions/getDesignAssetsConfirmationMessage';
import {
	AuthorRenderer,
	FromNowDateTimeRenderer,
	LinkRenderer,
	ResourceTypeRenderer,
} from './cell_renderers';
import findResourceType from './findResourceType';

export default function DesignLibraryAssetsFDSPropsTransformer({
	additionalProps,
	bulkActions,
	...props
}: IFrontendDataSetProps & {
	additionalProps?: {resourceTypes?: DesignLibraryResourceType[]};
	bulkActions?: Array<IBulkActionItem>;
}): IFrontendDataSetProps {
	const resourceTypes: DesignLibraryResourceType[] =
		additionalProps?.resourceTypes || [];

	const primaryItems = getCreationMenuItems(resourceTypes);

	return {
		...props,
		bulkActions: bulkActions?.map((bulkAction): IBulkActionItem => {
			if (bulkAction.data?.id !== 'delete') {
				return bulkAction;
			}

			return {
				...bulkAction,
				isDisabled: ({allItemsSelectedActive, selectedItems}) =>
					allItemsSelectedActive ||
					!selectedItems?.length ||
					selectedItems.some((item) => !item.actions?.delete),
			};
		}),
		creationMenu: primaryItems.length ? {primaryItems} : undefined,
		customRenderers: {
			tableCell: [
				{
					component: (rendererProps: any) => {
						const resourceType = findResourceType(
							resourceTypes,
							rendererProps?.itemData
						);

						return (
							<LinkRenderer
								{...rendererProps}
								options={{
									actionId: resourceType
										? resourceType.defaultActionId
										: 'edit',
								}}
								stickerClassName="design-library-fds-sticker"
								stickerStyle={
									{
										'--design-library-sticker-color': `var(--${
											resourceType
												? resourceType.color
												: 'secondary'
										})`,
									} as React.CSSProperties
								}
								symbol={
									resourceType
										? resourceType.symbol
										: 'documents-and-media'
								}
							/>
						);
					},
					name: TableCellContentType.DESIGN_LIBRARY_LINK,
					type: 'internal',
				},
				{
					component: AuthorRenderer,
					name: TableCellContentType.AUTHOR,
					type: 'internal',
				},
				{
					component: (rendererProps: any) => (
						<ResourceTypeRenderer
							label={
								findResourceType(
									resourceTypes,
									rendererProps?.itemData
								)?.label
							}
						/>
					),
					name: TableCellContentType.RESOURCE_TYPE,
					type: 'internal',
				},
				{
					component: FromNowDateTimeRenderer,
					name: TableCellContentType.FROM_NOW_DATE_TIME,
					type: 'internal',
				},
			],
		},
		hideManagementBarInEmptyState: true,
		onBulkActionItemClick: ({
			action,
			loadData,
			selectedData,
		}: {
			action: ActionItem;
			loadData: () => void;
			selectedData: {items: Array<DesignAsset>};
		}) => {
			if (action.data.id === 'delete') {
				confirmAndDeleteEntriesAction({
					confirmationMessage: getDesignAssetsConfirmationMessage(
						selectedData.items
					),
					items: selectedData.items,
					loadData,
				});
			}
		},
		views: [
			{
				contentRenderer: 'table',
				default: true,
				label: Liferay.Language.get('table'),
				name: 'table',
				schema: {
					fields: [
						{
							actionId: 'edit',
							contentRenderer:
								TableCellContentType.DESIGN_LIBRARY_LINK,
							fieldName: 'embedded.name',
							label: Liferay.Language.get('title'),
							localizeLabel: true,
						},
						{
							contentRenderer: TableCellContentType.AUTHOR,
							fieldName: 'embedded.creator.name',
							label: Liferay.Language.get('author'),
							localizeLabel: true,
							truncate: true,
						},
						{
							contentRenderer: TableCellContentType.RESOURCE_TYPE,
							fieldName: 'type',
							label: Liferay.Language.get('type'),
							localizeLabel: true,
							truncate: true,
						},
						{
							contentRenderer:
								TableCellContentType.FROM_NOW_DATE_TIME,
							fieldName: 'dateModified',
							label: Liferay.Language.get('modified'),
							localizeLabel: true,
							sortable: true,
						},
					],
				},
				thumbnail: 'table',
			},
		],
	};
}
