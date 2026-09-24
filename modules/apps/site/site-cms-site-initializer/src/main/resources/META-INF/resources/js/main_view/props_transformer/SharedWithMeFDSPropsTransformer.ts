/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../common/utils/constants';
import {openCMSModal} from '../../common/utils/openCMSModal';
import AssetNavigationModalContent from '../modal/asset_navigation_view/AssetNavigationModalContent';
import {AdditionalProps} from './AssetsFDSPropsTransformer';
import shareAction from './actions/shareAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SharedItemRenderer from './cell_renderers/SharedItemRenderer';
import VisibleRenderer from './cell_renderers/VisibleRenderer';
import {
	setSharedWithMeAdditionalProps,
	transformSharedItem,
} from './utils/openSharedItemViewModal';

export default function SharedWithMeFDSPropsTransformer({
	additionalProps,
	itemsActions = [],
	...otherProps
}: {
	additionalProps: AdditionalProps;
	itemsActions?: any[];
	otherProps: any;
}) {
	setSharedWithMeAdditionalProps(additionalProps);

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
					component: SharedItemRenderer,
					name: 'sharedItemTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
				{
					component: VisibleRenderer,
					name: 'visibleTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		itemsActions: itemsActions.map((action) => {
			if (action?.data?.id === 'actionLink') {
				return {
					...action,
					data: {
						...action.data,
						disableHeader: false,
						size: 'full-screen',
						title: 'View',
					},
					isVisible: () => false,
				};
			}
			else if (action?.data?.id === 'actionLinkEdit') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.className !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME &&
								item?.actionIds?.includes('UPDATE') &&
								item?.visible
						),
				};
			}
			else if (action?.data?.id === 'actionLinkFolder') {
				return {
					...action,
					isVisible: (item: any) => Boolean(item?.visible),
				};
			}
			else if (action?.data?.id === 'download') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(item?.file?.link?.href && item?.visible),
				};
			}
			else if (action?.data?.id === 'download-folder') {
				return {
					...action,
					isVisible: (item: any) => Boolean(item?.visible),
				};
			}
			else if (action?.data?.id === 'edit-folder') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.actionIds?.includes('UPDATE') && item?.visible
						),
				};
			}
			else if (action?.data?.id === 'share') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(item?.shareable && item?.visible),
				};
			}
			else if (action?.data?.id === 'view-content') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.className !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME &&
								!item?.file &&
								item?.visible
						),
					target: 'event',
				};
			}
			else if (action?.data?.id === 'view-file') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.className !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME &&
								item?.file &&
								item?.visible
						),
					target: 'event',
				};
			}

			return action;
		}),
		onActionDropdownItemClick: ({
			action,
			event,
			itemData,
			items,
		}: {
			action: any;
			event: Event;
			itemData: any;
			items: any;
		}) => {
			if (action?.data?.id === 'share') {
				const {autocompleteURL, collaboratorURLs} = additionalProps;

				shareAction({
					autocompleteURL,
					canManageCollaborators:
						itemData?.actionIds?.includes('UPDATE'),
					collaboratorURL: collaboratorURLs[itemData.className],
					creator: itemData.creator,
					entryClassName: itemData.className,
					itemId: itemData.classPK,
					title: itemData?.title,
				});
			}
			else if (
				action?.data?.id === 'view-content' ||
				action?.data?.id === 'view-file'
			) {
				event?.preventDefault();

				const filteredItems = items.filter(
					(item: any) =>
						item?.className !== OBJECT_ENTRY_FOLDER_CLASS_NAME
				);

				const currentItemPos = filteredItems.findIndex(
					(item: any) => item.id === itemData.id
				);

				const transformedItems = filteredItems.map(transformSharedItem);

				openCMSModal({
					contentComponent: () =>
						AssetNavigationModalContent({
							additionalProps,
							contentViewURL: additionalProps.contentViewURL,
							currentIndex: currentItemPos,
							items: transformedItems,
							showInfoPanel: false,
						}),
					size: 'full-screen',
				});
			}
		},
		searchAsYouType: true,
		searchSuggestionsEnabled: true,
	};
}
