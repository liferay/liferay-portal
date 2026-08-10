/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IInternalRenderer,
	IItemsActions,
	replaceTokens,
} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import {openAssetUsageListModal} from '../../common/components/asset_usage/utils';
import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../common/utils/constants';
import {getFormattedLabel} from '../../common/utils/getFormattedText';
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

export type DashboardAssetListAdditionalProps = Pick<
	AdditionalProps,
	| 'assetLibraries'
	| 'autocompleteURL'
	| 'availableExportFileFormats'
	| 'availableLocales'
	| 'brokenLinksCheckerEnabled'
	| 'collaboratorURLs'
	| 'contentViewURL'
	| 'defaultPermissionAdditionalProps'
	| 'fileMimeTypeCssClasses'
	| 'fileMimeTypeIcons'
	| 'objectDefinitionCssClasses'
	| 'objectDefinitionIcons'
	| 'parentObjectEntryFolderExternalReferenceCode'
> &
	Partial<
		Pick<
			AdditionalProps,
			| 'additionalAPIURLParameters'
			| 'rootObjectEntryFolderExternalReferenceCode'
		>
	>;

export default function getDashboardAssetListFDSProps({
	additionalProps,
	itemsActions = [],
	renderSubtitle,
	...otherProps
}: {
	additionalProps: DashboardAssetListAdditionalProps;
	apiURL?: string;
	itemsActions?: IItemsActions[];
	renderSubtitle?: (itemData: ISearchAssetObjectEntry) => React.ReactNode;
	[key: string]: any;
}) {
	const {additionalAPIURLParameters, ...remainingAdditionalProps} =
		additionalProps || {};

	return {
		...otherProps,
		additionalAPIURLParameters,
		additionalProps: remainingAdditionalProps,
		customRenderers: {
			tableCell: [
				{
					component: ({actions, itemData, options, value}) => (
						<AssetRenderer
							actions={actions}
							additionalProps={additionalProps}
							itemData={itemData}
							onViewClick={(item) => {
								openCMSModal({
									contentComponent: () =>
										AssetNavigationModalContent({
											additionalProps,
											contentViewURL:
												additionalProps.contentViewURL,
											currentIndex: 0,
											items: [item],
										}),
									size: 'full-screen',
								});
							}}
							options={options}
							renderSubtitle={renderSubtitle}
							value={value}
						/>
					),
					name: 'assetRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
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
					'',
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
								itemData.embedded?.externalReferenceCode,
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
				const title =
					itemData.title ||
					itemData.embedded?.title ||
					Liferay.Language.get('untitled-asset');

				const confirmationMessage =
					itemData.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME
						? sub(
								Liferay.Language.get(
									'delete-folder-confirmation-body'
								),
								getFormattedLabel(title)
							)
						: sub(
								Liferay.Language.get(
									'delete-asset-confirmation-body'
								),
								getFormattedLabel(title)
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
					await deleteItemAction(
						confirmationMessage,
						itemData,
						loadData
					);
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
									additionalProps.availableLocales.find(
										(locale) =>
											locale.languageId === languageId
									)
								)
								.filter(Boolean),
							availableTargetLocales:
								additionalProps.availableLocales,
							closeModal,
							defaultSourceLanguageId:
								itemData.embedded?.defaultLanguageId,
							translationsAPIURL: `${itemData.actions.get.href}/translations`,
						}),
				});
			}
			else if (action?.data?.id === 'import-translation') {
				event?.preventDefault();

				if (!itemData.embedded) {
					return;
				}

				const formattedHref = replaceTokens(action.href, itemData);

				ACTIONS.importTranslation(itemData, formattedHref, loadData);
			}
			else if (action?.data?.id === 'reset-to-default-permissions') {
				if (!itemData.embedded) {
					return;
				}

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

				if (!itemData.embedded) {
					return;
				}

				const currentItemPos = items.findIndex(
					(item: any) => item.embedded?.id === itemData.embedded.id
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
				if (!itemData.embedded) {
					return;
				}

				const {autocompleteURL, collaboratorURLs} = additionalProps;

				shareAction({
					autocompleteURL,
					collaboratorURL: collaboratorURLs[itemData.entryClassName],
					creator: itemData.embedded.creator,
					entryClassName: itemData.entryClassName,
					itemId: itemData.embedded.id,
					title: itemData.embedded.title,
				});
			}
		},
	};
}
