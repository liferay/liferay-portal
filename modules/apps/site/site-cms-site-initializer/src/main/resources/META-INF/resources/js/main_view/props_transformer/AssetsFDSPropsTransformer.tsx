/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IView} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

import {openAssetUsageListModal} from '../../common/components/asset_usage/utils';
import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import formatActionURL from '../../common/utils/formatActionURL';
import CategoriesAndTagsModalContent from '../categorization/modal/CategoriesAndTagsModalContent';
import {defaultPermissionsBulkAction} from '../default_permission/BulkDefaultPermissionModalContent';
import {permissionsBulkAction} from '../default_permission/BulkPermissionModalContent';
import DefaultPermissionModalContent from '../default_permission/DefaultPermissionModalContent';
import AssetTypeInfoPanel from '../info_panel/AssetTypeInfoPanelContent';
import AssetNavigationModalContent from '../modal/asset_navigation_view/AssetNavigationModalContent';
import createAssetAction from './actions/createAssetAction';
import createFolderAction from './actions/createFolderAction';
import deleteAssetEntriesBulkAction, {
	executeBulkDeleteAction,
} from './actions/deleteAssetEntriesBulkAction';
import deleteItemAction from './actions/deleteItemAction';
import multipleFilesUploadAction from './actions/multipleFilesUploadAction';
import shareAction from './actions/shareAction';
import {triggerAssetBulkAction} from './actions/triggerAssetBulkAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import NameRenderer from './cell_renderers/NameRenderer';
import SimpleActionLinkRenderer from './cell_renderers/SimpleActionLinkRenderer';
import SpaceRenderer from './cell_renderers/SpaceRenderer';
import TypeRenderer from './cell_renderers/TypeRenderer';
import addOnClickToCreationMenuItems from './utils/addOnClickToCreationMenuItems';
import transformViewsItemsProps from './utils/transformViewsItemProps';

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

const ACTIONS = {
	createAsset: createAssetAction,
	createFolder: createFolderAction,
	uploadMultipleFiles: multipleFilesUploadAction,
};

export type AdditionalProps = {
	autocompleteURL: string;
	baseFolderViewURL: string;
	brokenLinksCheckerEnabled: boolean;
	cmsGroupId?: number;
	collaboratorURLs: Record<string, string>;
	contentViewURL: string;
	defaultPermissionAdditionalProps?: any;
	fileMimeTypeCssClasses: Record<string, string>;
	fileMimeTypeIcons: Record<string, string>;
	objectDefinitionCssClasses: Record<string, string>;
	objectDefinitionIcons: Record<string, string>;
	redirect: string;
};

export default function AssetsFDSPropsTransformer({
	additionalProps,
	creationMenu,
	itemsActions = [],
	views,
	...otherProps
}: {
	additionalProps: AdditionalProps;
	apiURL?: string;
	creationMenu: any;
	itemsActions?: any[];
	views: IView[];
}) {
	return {
		...otherProps,
		creationMenu: {
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
				} as IInternalRenderer,
				{
					component: NameRenderer,
					name: 'nameTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({actions, itemData, options, value}) =>
						SimpleActionLinkRenderer({
							actions,
							additionalProps,
							itemData,
							options,
							value,
						}),
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: SpaceRenderer,
					name: 'spaceTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: TypeRenderer,
					name: 'typeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		infoPanelComponent: (items: {items: ISearchAssetObjectEntry[]}) => (
			<AssetTypeInfoPanel
				additionalProps={additionalProps as any}
				{...items}
			/>
		),
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'default-permissions') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.entryClassName ===
								OBJECT_ENTRY_FOLDER_CLASS_NAME
						),
				};
			}
			else if (action?.data?.id === 'download') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(item?.embedded?.file?.link?.href),
				};
			}
			else if (action?.data?.id === 'actionLink') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.entryClassName !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME
						),
				};
			}
			else if (
				action?.data?.id === 'export-for-translation' ||
				action?.data?.id === 'import-translation' ||
				action?.data?.id === 'translate' ||
				action?.data?.id === 'view-content'
			) {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.entryClassName !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME &&
								!item?.embedded?.file
						),
				};
			}
			else if (action?.data?.id === 'view-file') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(item?.embedded?.file) &&
						Boolean(
							item?.entryClassName !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME
						),
				};
			}

			return action;
		}),
		async onActionDropdownItemClick({
			action,
			event,
			itemData,
			items,
			loadData,
		}: {
			action: any;
			event: Event;
			itemData: ItemData;
			items: any;
			loadData: () => {};
		}) {
			if (action?.data?.id === 'default-permissions') {
				openModal({
					containerProps: {
						className: '',
					},
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						DefaultPermissionModalContent({
							...(additionalProps.defaultPermissionAdditionalProps ||
								{}),
							apiURL: otherProps.apiURL,
							classExternalReferenceCode:
								itemData.embedded.externalReferenceCode,
							className: itemData.entryClassName,
							closeModal,
						}),
					size: 'full-screen',
				});
			}
			else if (action?.data?.id === 'delete') {
				if (additionalProps.brokenLinksCheckerEnabled) {
					openAssetUsageListModal({
						itemsData: [itemData],
						onDelete: async () => {
							await deleteItemAction(itemData, loadData);
						},
						selectAll: false,
					});
				}
				else {
					await deleteItemAction(itemData, loadData);
				}
			}
			else if (
				action?.data?.id === 'export-for-translation' ||
				action?.data?.id === 'import-translation'
			) {
				event?.preventDefault();

				openModal({
					size: 'full-screen',
					title: action.label,
					url: formatActionURL(itemData, action.href),
				});
			}
			else if (action?.data?.id === 'share') {
				const {autocompleteURL, collaboratorURLs} = additionalProps;

				shareAction({
					autocompleteURL,
					collaboratorURL: collaboratorURLs[itemData.entryClassName],
					creator: itemData.embedded.creator,
					itemId: itemData.embedded.id,
					title: itemData.embedded?.title,
				});
			}
			else if (
				action?.data?.id === 'view-content' ||
				action?.data?.id === 'view-file'
			) {
				event?.preventDefault();

				const filteredItems = items.filter(
					(item: any) =>
						item?.entryClassName !== OBJECT_ENTRY_FOLDER_CLASS_NAME
				);

				const currentItemPos = filteredItems.findIndex(
					(item: any) => item.embedded.id === itemData.embedded.id
				);

				openModal({
					containerProps: {
						className: '',
					},
					contentComponent: () =>
						AssetNavigationModalContent({
							additionalProps,
							contentViewURL: additionalProps.contentViewURL,
							currentIndex: currentItemPos,
							items: filteredItems,
						}),
					size: 'full-screen',
				});
			}
		},
		onBulkActionItemClick: async ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			if (action?.data?.id === 'categoriesAndTags') {
				openModal({
					center: true,
					containerProps: {
						className: 'modal-height-lg',
					},
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						CategoriesAndTagsModalContent({
							apiURL: otherProps.apiURL,
							closeModal,
							cmsGroupId: additionalProps.cmsGroupId as number,
							selectedData,
						}),
					size: 'md',
				});
			}
			else if (action?.data?.id === 'default-permissions') {
				defaultPermissionsBulkAction({
					apiURL: otherProps.apiURL,
					className: OBJECT_ENTRY_FOLDER_CLASS_NAME,
					defaultPermissionAdditionalProps:
						additionalProps.defaultPermissionAdditionalProps || {},
					selectedData,
				});
			}
			else if (action?.data?.id === 'delete') {
				if (additionalProps.brokenLinksCheckerEnabled) {
					openAssetUsageListModal({
						apiURL: otherProps.apiURL,
						itemsData: selectedData.items,
						onDelete: async () => {
							executeBulkDeleteAction(
								otherProps.apiURL as string,
								selectedData
							);
						},

						// Callback triggered after the request returns all assets
						// with no usages, will skip the asset usage list modal.
						// Instead, the default delete asset entries bulk action modal
						// will be displayed.

						onSkip: async () => {
							deleteAssetEntriesBulkAction({
								apiURL: otherProps.apiURL,
								selectedData,
							});
						},
						selectAll: selectedData.selectAll,
					});
				}
				else {
					deleteAssetEntriesBulkAction({
						apiURL: otherProps.apiURL,
						selectedData,
					});
				}
			}
			else if (action?.data?.id === 'download') {
				triggerAssetBulkAction({
					apiURL: otherProps.apiURL,
					selectedData,
					type: 'DownloadBulkAction',
				});
			}
			else if (action?.data?.id === 'permissions') {
				permissionsBulkAction({
					apiURL: otherProps.apiURL,
					className: OBJECT_ENTRY_FOLDER_CLASS_NAME,
					defaultPermissionAdditionalProps:
						additionalProps.defaultPermissionAdditionalProps || {},
					selectedData,
				});
			}
		},
		views: transformViewsItemsProps({
			fileMimeTypeCssClasses: additionalProps.fileMimeTypeCssClasses,
			fileMimeTypeIcons: additionalProps.fileMimeTypeIcons,
			objectDefinitionCssClasses:
				additionalProps.objectDefinitionCssClasses,
			objectDefinitionIcons: additionalProps.objectDefinitionIcons,
			views,
		}),
	};
}
