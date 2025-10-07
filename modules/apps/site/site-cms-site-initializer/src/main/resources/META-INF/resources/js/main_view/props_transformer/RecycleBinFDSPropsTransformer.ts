/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';

import {openGenericFDSDeleteConfirmationModal} from '../../common/utils/genericOpenModalUtil';
import {getFormattedLabel} from '../../common/utils/getFormattedText';
import {displayDeleteSuccessToast} from '../../common/utils/toastUtil';
import deleteAssetEntriesBulkAction from './actions/deleteAssetEntriesBulkAction';
import restoreItemAction from './actions/restoreItemAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SimpleActionLinkRenderer from './cell_renderers/SimpleActionLinkRenderer';
import SpaceRenderer from './cell_renderers/SpaceRenderer';

const OBJECT_ENTRY_FOLDER_CLASSNAME =
	'com.liferay.object.model.ObjectEntryFolder';

export default function RecycleBinFDSPropsTransformer({
	itemsActions = [],
	...otherProps
}: {
	apiURL: string;
	itemsActions?: any[];
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: AuthorRenderer,
					name: 'authorTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: SimpleActionLinkRenderer,
					name: 'simpleActionLinkTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: SpaceRenderer,
					name: 'spaceTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'actionLink') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.entryClassName !==
								OBJECT_ENTRY_FOLDER_CLASSNAME
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
			if (action.data.id === 'delete') {
				const formattedItemLabel = getFormattedLabel(
					itemData.embedded.title
				);
				const confirmationText = sub(
					Liferay.Language.get(
						'you-are-about-to-permanently-delete-x-this-action-cannot-be-undone'
					),
					formattedItemLabel
				);

				const displaySuccessToast = () => {
					return displayDeleteSuccessToast(itemData.embedded.title);
				};

				openGenericFDSDeleteConfirmationModal(
					confirmationText,
					itemData.actions?.delete?.method,
					itemData.actions?.delete?.href,
					itemData.embedded.title,
					loadData,
					displaySuccessToast
				);
			}

			if (action.data.id === 'restore') {
				await restoreItemAction(
					itemData.embedded.title,
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
					selectedData,
				});
			}
		},
	};
}
