/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IView} from '@liferay/frontend-data-set-web';
import React from 'react';

import StatusLabel from '../../common/components/StatusLabel';
import {openAssetUsageListModal} from '../../common/components/asset_usage/utils';
import {AssetLibrary} from '../../common/types/AssetLibrary';
import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../common/utils/constants';
import {getScopeExternalReferenceCode} from '../../common/utils/getScopeExternalReferenceCode';
import {openCMSModal} from '../../common/utils/openCMSModal';
import EditAssetCategoriesModalContent from '../categorization/modal/EditAssetCategoriesModalContent';
import EditAssetTagsModalContent from '../categorization/modal/EditAssetTagsModalContent';
import {defaultPermissionsBulkAction} from '../default_permission/BulkDefaultPermissionModalContent';
import {permissionsBulkAction} from '../default_permission/BulkPermissionModalContent';
import DefaultPermissionModalContent from '../default_permission/DefaultPermissionModalContent';
import openResetAssetPermissionModal from '../default_permission/ResetPermissionModalContent';
import AssetTypeInfoPanel from '../info_panel/AssetTypeInfoPanelContent';
import ExportTranslationModalContent from '../modal/ExportTranslationModalContent';
import AssetNavigationModalContent from '../modal/asset_navigation_view/AssetNavigationModalContent';
import ACTIONS from './actions/creationMenuActions';
import deleteAssetEntriesBulkAction, {
	executeBulkDeleteAction,
} from './actions/deleteAssetEntriesBulkAction';
import deleteItemAction from './actions/deleteItemAction';
import executeResetPermissionBulkAction from './actions/executeResetPermissionBulkAction';
import openFolderItemSelectorAction from './actions/openFolderItemSelectorAction';
import shareAction from './actions/shareAction';
import {triggerAssetDownloadBulkAction} from './actions/triggerAssetDownloadBulkAction';
import AdditionalItemInfoRenderer from './cell_renderers/AdditionalItemInfoRenderer';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SimpleActionLinkRenderer from './cell_renderers/SimpleActionLinkRenderer';
import SpaceRendererWithCache from './cell_renderers/SpaceRendererWithCache';
import TypeRenderer from './cell_renderers/TypeRenderer';
import addOnClickToCreationMenuItems from './utils/addOnClickToCreationMenuItems';
import transformViewsItemsProps from './utils/transformViewsItemProps';
import GalleryView from './views/GalleryView';

export type AdditionalProps = {
	assetLibraries: AssetLibrary[];
	autocompleteURL: string;
	availableExportFileFormats: any[];
	availableTargetLocales: any[];
	baseFolderViewURL: string;
	brokenLinksCheckerEnabled: boolean;
	cmsGroupId?: number;
	collaboratorURLs: Record<string, string>;
	contentViewURL: string;
	defaultPermissionAdditionalProps?: any;
	fileMimeTypeCssClasses: Record<string, string>;
	fileMimeTypeIcons: Record<string, string>;
	galleryViewEnabled?: boolean;
	objectDefinitionCssClasses: Record<string, string>;
	objectDefinitionIcons: Record<string, string>;
	objectEntryFolderExternalReferenceCode: string;
	parentObjectEntryFolderExternalReferenceCode: string;
	redirect: string;
	rootObjectEntryFolderExternalReferenceCode: string;
	showAdditionalItemInfo?: boolean;
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
	id?: string;
	itemsActions?: any[];
	views: IView[];
}) {
	let mergedViews = views;

	if (additionalProps.galleryViewEnabled) {
		const galleryViewRenderer: IView = {
			component: (props: any) => GalleryView({...props, additionalProps}),
			default: true,
			label: Liferay.Language.get('gallery'),
			name: 'gallery',
			schema: {
				description: 'description',
				image: 'imageURL',
				link: '',
				sticker: '',
				symbol: '',
				title: 'embedded.title',
			},
			thumbnail: 'gallery',
		};

		const nonDefaultViews = views.map((view) => {
			return {
				...view,
				default: false,
			};
		});

		mergedViews = [...nonDefaultViews, galleryViewRenderer];
	}

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
					component: ({actions, itemData, options, value}) => {
						const simpleActionLink = (
							<SimpleActionLinkRenderer
								actions={actions}
								additionalProps={additionalProps}
								itemData={itemData}
								options={options}
								value={value}
							/>
						);

						if (!additionalProps.showAdditionalItemInfo) {
							return simpleActionLink;
						}

						return (
							<>
								{simpleActionLink}
								<AdditionalItemInfoRenderer
									itemData={itemData}
								/>
							</>
						);
					},
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) => (
						<SpaceRendererWithCache
							spaceExternalReferenceCode={getScopeExternalReferenceCode(
								itemData
							)}
						/>
					),
					name: 'spaceTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: TypeRenderer,
					name: 'typeTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({value}) => StatusLabel(value),
					name: 'statusTableCellRenderer',
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
			if (action?.data?.id === 'copy' || action?.data?.id === 'move') {
				return {
					...action,
					isVisible: () => true,
				};
			}
			else if (
				action?.data?.id === 'default-permissions' ||
				action?.data?.id === 'edit-and-propagate-default-permissions'
			) {
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
			if (action?.data?.id === 'copy' || action?.data?.id === 'move') {
				openFolderItemSelectorAction(
					action?.data?.id,
					additionalProps.assetLibraries,
					itemData,
					loadData,
					additionalProps.objectEntryFolderExternalReferenceCode,
					additionalProps.rootObjectEntryFolderExternalReferenceCode ||
						additionalProps.parentObjectEntryFolderExternalReferenceCode
				);
			}
			else if (
				action?.data?.id === 'default-permissions' ||
				action?.data?.id === 'edit-and-propagate-default-permissions'
			) {
				openCMSModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						DefaultPermissionModalContent({
							...(additionalProps.defaultPermissionAdditionalProps ||
								{}),
							allowPropagate:
								action.data.id ===
								'edit-and-propagate-default-permissions',
							apiURL: otherProps.apiURL,
							classExternalReferenceCode:
								itemData.embedded.externalReferenceCode,
							className: itemData.entryClassName,
							closeModal,
							section:
								additionalProps.rootObjectEntryFolderExternalReferenceCode ||
								additionalProps.parentObjectEntryFolderExternalReferenceCode,
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
			else if (action?.data?.id === 'export-for-translation') {
				event?.preventDefault();

				openCMSModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						ExportTranslationModalContent({
							availableExportFileFormats:
								additionalProps.availableExportFileFormats,
							availableSourceLocales: Object.keys(
								itemData.embedded?.title_i18n || {}
							)
								.map((languageId) =>
									additionalProps.availableTargetLocales.find(
										(locale) =>
											locale.languageId === languageId
									)
								)
								.filter(Boolean),
							availableTargetLocales:
								additionalProps.availableTargetLocales,
							closeModal,
							defaultSourceLanguageId:
								itemData.embedded?.defaultLanguageId,
							itemId: itemData.embedded.id,
						}),
				});
			}
			else if (action?.data?.id === 'import-translation') {
				ACTIONS.importTranslation(itemData, loadData);
			}
			else if (action?.data?.id === 'reset-to-default-permissions') {
				openResetAssetPermissionModal({
					className: itemData.entryClassName,
					classPK: itemData.embedded.id,
					loadData,
				});
			}
			else if (action?.data?.id === 'share') {
				const {autocompleteURL, collaboratorURLs} = additionalProps;

				shareAction({
					autocompleteURL,
					collaboratorURL: collaboratorURLs[itemData.entryClassName],
					creator: itemData.embedded.creator,
					entryClassName: itemData.entryClassName,
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

				openCMSModal({
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
			if (action?.data?.id === 'edit-categories') {
				openCMSModal({
					center: true,
					containerProps: {
						className: 'modal-height-lg',
					},
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						EditAssetCategoriesModalContent({
							apiURL: otherProps.apiURL,
							assetLibraries: additionalProps.assetLibraries,
							closeModal,
							cmsGroupId: additionalProps.cmsGroupId as number,
							selectedData,
						}),
					size: 'md',
				});
			}
			else if (action?.data?.id === 'edit-tags') {
				openCMSModal({
					center: true,
					containerProps: {
						className: 'modal-height-lg',
					},
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						EditAssetTagsModalContent({
							apiURL: otherProps?.apiURL,
							assetLibraries: additionalProps.assetLibraries,
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
					section:
						additionalProps.rootObjectEntryFolderExternalReferenceCode ||
						additionalProps.parentObjectEntryFolderExternalReferenceCode,
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
								otherProps.id || '',
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
								dataSetId: otherProps.id,
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
				triggerAssetDownloadBulkAction({
					apiURL: otherProps.apiURL,
					selectedData,
					type: 'DownloadBulkAction',
				});
			}
			else if (
				action?.data?.id === 'edit-default-permissions-by-role'
			) {
				defaultPermissionsBulkAction({
					apiURL: otherProps.apiURL,
					className: OBJECT_ENTRY_FOLDER_CLASS_NAME,
					defaultPermissionAdditionalProps:
						additionalProps.defaultPermissionAdditionalProps || {},
					section:
						additionalProps.rootObjectEntryFolderExternalReferenceCode ||
						additionalProps.parentObjectEntryFolderExternalReferenceCode,
					selectedData,
					singleRoleMode: true,
				});
			}
			else if (action?.data?.id === 'edit-permissions-by-role') {
				permissionsBulkAction({
					apiURL: otherProps.apiURL,
					className: OBJECT_ENTRY_FOLDER_CLASS_NAME,
					defaultPermissionAdditionalProps:
						additionalProps.defaultPermissionAdditionalProps || {},
					section:
						additionalProps.rootObjectEntryFolderExternalReferenceCode ||
						additionalProps.parentObjectEntryFolderExternalReferenceCode,
					selectedData,
					singleRoleMode: true,
				});
			}
			else if (action?.data?.id === 'permissions') {
				permissionsBulkAction({
					apiURL: otherProps.apiURL,
					className: OBJECT_ENTRY_FOLDER_CLASS_NAME,
					defaultPermissionAdditionalProps:
						additionalProps.defaultPermissionAdditionalProps || {},
					section:
						additionalProps.rootObjectEntryFolderExternalReferenceCode ||
						additionalProps.parentObjectEntryFolderExternalReferenceCode,
					selectedData,
				});
			}
			else if (action?.data?.id === 'reset-to-default-permissions') {
				openResetAssetPermissionModal({
					loadData: () => {
						executeResetPermissionBulkAction({
							apiURL: otherProps.apiURL,
							selectedData,
						});
					},
				});
			}
		},
		views: transformViewsItemsProps({
			fileMimeTypeCssClasses: additionalProps.fileMimeTypeCssClasses,
			fileMimeTypeIcons: additionalProps.fileMimeTypeIcons,
			objectDefinitionCssClasses:
				additionalProps.objectDefinitionCssClasses,
			objectDefinitionIcons: additionalProps.objectDefinitionIcons,
			views: mergedViews,
		}),
	};
}
