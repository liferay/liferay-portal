/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import {openAssetUsageListModal} from '../../common/components/asset_usage/utils';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../common/utils/constants';
import {openCMSModal} from '../../common/utils/openCMSModal';
import DefaultPermissionModalContent from '../default_permission/DefaultPermissionModalContent';
import openResetAssetPermissionModal from '../default_permission/ResetPermissionModalContent';
import ExportTranslationModalContent from '../modal/ExportTranslationModalContent';
import AssetNavigationModalContent from '../modal/asset_navigation_view/AssetNavigationModalContent';
import {AdditionalProps} from './AssetsFDSPropsTransformer';
import ACTIONS from './actions/creationMenuActions';
import deleteItemAction from './actions/deleteItemAction';
import openFolderItemSelectorAction from './actions/openFolderItemSelectorAction';
import shareAction from './actions/shareAction';
import AssetRenderer from './cell_renderers/AssetRenderer';

export default function HomeRecentAssetsFDSPropsTransformer({
	additionalProps,
	itemsActions = [],
	...otherProps
}: {
	additionalProps: AdditionalProps;
	apiURL?: string;
	itemsActions?: any[];
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: ({actions, itemData, options, value}) =>
						AssetRenderer({
							actions,
							additionalProps,
							itemData,
							options,
							value,
						}),
					name: 'assetRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		itemsActions: itemsActions.map((action) => {
			if (
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
			else if (
				action?.data?.id === 'export-for-translation' ||
				action?.data?.id === 'import-translation' ||
				action?.data?.id === 'view-content'
			) {
				return {
					...action,
					isVisible: (item: any) => Boolean(!item?.embedded?.file),
				};
			}
			else if (action?.data?.id === 'view-file') {
				return {
					...action,
					isVisible: (item: any) => Boolean(item?.embedded?.file),
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
			itemData: any;
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
			else if (
				action?.data?.id === 'view-content' ||
				action?.data?.id === 'view-file'
			) {
				event?.preventDefault();

				const currentItemPos = items.findIndex(
					(item: any) => item.embedded.id === itemData.embedded.id
				);

				openCMSModal({
					contentComponent: () =>
						AssetNavigationModalContent({
							additionalProps,
							contentViewURL: additionalProps.contentViewURL,
							currentIndex: currentItemPos,
							items,
						}),
					size: 'full-screen',
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
		},
	};
}
