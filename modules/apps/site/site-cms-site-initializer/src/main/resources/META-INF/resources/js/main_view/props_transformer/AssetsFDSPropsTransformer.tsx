/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IView} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

import {START_TASK} from '../../common/utils/events';
import formatActionURL from '../../common/utils/formatActionURL';
import {ISearchAssetObjectEntry} from '../../structure_builder/types/AssetType';
import {defaultPermissionsBulkAction} from '../default_permission/BulkDefaultPermissionModalContent';
import DefaultPermissionModalContent from '../default_permission/DefaultPermissionModalContent';
import AssetTypeInfoPanel from '../info_panel/AssetTypeInfoPanelContent';
import FilePreviewerModalContent from '../modal/FilePreviewerModalContent';
import createAssetAction from './actions/createAssetAction';
import createFolderAction from './actions/createFolderAction';
import deleteAssetEntriesBulkAction from './actions/deleteAssetEntriesBulkAction';
import deleteItemAction from './actions/deleteItemAction';
import multipleFilesUploadAction from './actions/multipleFilesUploadAction';
import shareAction from './actions/shareAction';
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
	cmsGroupId?: number;
	collaboratorURLs: Record<string, string>;
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
	creationMenu: any;
	itemsActions?: any[];
	otherProps: any;
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
			loadData,
		}: {
			action: any;
			event: Event;
			itemData: ItemData;
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
							classExternalReferenceCode:
								itemData.embedded.externalReferenceCode,
							className: itemData.entryClassName,
							closeModal,
						}),
					size: 'full-screen',
				});
			}
			else if (action?.data?.id === 'delete') {
				await deleteItemAction(itemData, loadData);
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
			else if (action?.data?.id === 'view-content') {
				event?.preventDefault();

				openModal({
					size: 'full-screen',
					title: itemData.embedded.title,
					url: formatActionURL(itemData, action.href),
				});
			}
			else if (action?.data?.id === 'view-file') {
				openModal({
					containerProps: {
						className: '',
					},
					contentComponent: () =>
						FilePreviewerModalContent({
							file: itemData.embedded.file,
							headerName: itemData.embedded.title,
						}),
					size: 'full-screen',
				});
			}
		},
		onBulkActionItemClick: ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			if (action?.data?.id === 'default-permissions') {
				defaultPermissionsBulkAction({
					className: OBJECT_ENTRY_FOLDER_CLASS_NAME,
					defaultPermissionAdditionalProps:
						additionalProps.defaultPermissionAdditionalProps || {},
					selectedData,
				});
			}
			else if (action?.data?.id === 'delete') {
				deleteAssetEntriesBulkAction({
					actionId: action.data.id,
					selectedData,
				});
			}
			else if (action?.data?.id === 'download') {
				Liferay.fire(START_TASK, {
					actionId: action.data.id,
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
