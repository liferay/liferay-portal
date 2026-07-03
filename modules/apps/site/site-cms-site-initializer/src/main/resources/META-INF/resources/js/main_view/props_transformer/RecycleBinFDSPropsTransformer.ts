/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBulkActionItem,
	IInternalRenderer,
} from '@liferay/frontend-data-set-web';
import {getCMSItemSelectorGroupedFilters} from '@liferay/frontend-js-item-selector-web';
import {sub} from 'frontend-js-web';

import {
	FDS_EVENT_UPDATE_DISPLAY,
	OBJECT_ENTRY_FOLDER_CLASS_NAME,
} from '../../common/utils/constants';
import {openGenericFDSDeleteConfirmationModal} from '../../common/utils/genericOpenModalUtil';
import {getFormattedLabel} from '../../common/utils/getFormattedText';
import {getScopeExternalReferenceCode} from '../../common/utils/getScopeExternalReferenceCode';
import {displayDeleteSuccessToast} from '../../common/utils/toastUtil';
import deleteAssetEntriesBulkAction from './actions/deleteAssetEntriesBulkAction';
import restoreAssetEntriesBulkAction from './actions/restoreAssetEntriesBulkAction';
import restoreItemAction from './actions/restoreItemAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SimpleActionLinkRenderer from './cell_renderers/SimpleActionLinkRenderer';
import SpaceRendererWithCache from './cell_renderers/SpaceRendererWithCache';
import transformFDSBulkActions from './utils/transformFDSBulkActions';

export default function RecycleBinFDSPropsTransformer({
	additionalProps,
	bulkActions = [],
	itemsActions = [],
	...otherProps
}: {
	additionalProps: {additionalAPIURLParameters?: string};
	apiURL: string;
	bulkActions: Array<IBulkActionItem>;
	id: string;
	itemsActions?: any[];
	otherProps: any;
}) {
	const {additionalAPIURLParameters, ...remainingAdditionalProps} =
		additionalProps || {};

	return {
		...otherProps,
		additionalAPIURLParameters,
		additionalProps: remainingAdditionalProps,
		bulkActions: transformFDSBulkActions(bulkActions),
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: (props: any) =>
						SimpleActionLinkRenderer({
							...props,
							systemIconLabel: Liferay.Language.get(
								'system-default-structure'
							),
						}),
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: ({itemData}) =>
						SpaceRendererWithCache({
							scopeKey: itemData.embedded.scopeKey,
							spaceExternalReferenceCode:
								getScopeExternalReferenceCode(itemData),
						}),
					name: 'spaceTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		groupedFilters: getCMSItemSelectorGroupedFilters('scopeGroupId'),
		hideManagementBarInEmptyState: true,
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'actionLink') {
				return {
					...action,
					isVisible: (item: any) =>
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
			itemData,
			loadData,
		}: {
			action: {data: {id: string}};
			itemData: ItemData;
			loadData: () => {};
		}) {
			const title =
				itemData.embedded.title ||
				Liferay.Language.get('untitled-asset');

			if (action.data.id === 'delete') {
				const formattedItemLabel = getFormattedLabel(title);
				const confirmationText = sub(
					Liferay.Language.get(
						'you-are-about-to-permanently-delete-x-this-action-cannot-be-undone'
					),
					formattedItemLabel
				);

				const displaySuccessToast = () => {
					return displayDeleteSuccessToast(title);
				};

				openGenericFDSDeleteConfirmationModal(
					confirmationText,
					itemData.actions?.delete?.method,
					itemData.actions?.delete?.href,
					title,
					loadData,
					displaySuccessToast
				);
			}

			if (action.data.id === 'restore') {
				await restoreItemAction(
					title,
					loadData,
					itemData.actions?.restore.method,
					itemData.actions?.restore.href
				);
			}
		},
		onBulkActionItemClick: async ({
			action,
			selectedData,
		}: {
			action: any;
			selectedData: any;
		}) => {
			if (action?.data?.id === 'delete') {
				deleteAssetEntriesBulkAction({
					apiURL: otherProps.apiURL,
					dataSetId: otherProps.id,
					selectedData,
				});
			}
			else if (action?.data?.id === 'restore') {
				if (selectedData?.items?.length === 1) {
					const item = selectedData.items[0];
					const title =
						item.embedded?.title ||
						Liferay.Language.get('untitled-asset');

					await restoreItemAction(
						title,
						() =>
							Liferay.fire(FDS_EVENT_UPDATE_DISPLAY, {
								id: otherProps.id,
							}),
						item.actions?.restore?.method,
						item.actions?.restore?.href
					);
				}
				else {
					restoreAssetEntriesBulkAction({
						apiURL: otherProps.apiURL,
						dataSetId: otherProps.id,
						selectedData,
					});
				}
			}
		},
	};
}
