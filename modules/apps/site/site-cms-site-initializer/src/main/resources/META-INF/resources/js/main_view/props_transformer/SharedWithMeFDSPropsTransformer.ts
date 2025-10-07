/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';

import AssetNavigationModalContent from '../modal/asset_navigation_view/AssetNavigationModalContent';
import {AdditionalProps} from './AssetsFDSPropsTransformer';
import shareAction from './actions/shareAction';
import AuthorRenderer from './cell_renderers/AuthorRenderer';
import SharedItemRenderer from './cell_renderers/SharedItemRenderer';

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

export default function SharedWithMeFDSPropsTransformer({
	additionalProps,
	itemsActions = [],
	...otherProps
}: {
	additionalProps: AdditionalProps;
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
					component: SharedItemRenderer,
					name: 'sharedItemTableCellRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
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
								item?.actionIds?.includes('UPDATE')
						),
				};
			}
			else if (action?.data?.id === 'download') {
				return {
					...action,
					isVisible: (item: any) => Boolean(item?.file?.link?.href),
				};
			}
			else if (action?.data?.id === 'edit-folder') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(item?.actionIds?.includes('UPDATE')),
				};
			}
			else if (action?.data?.id === 'share') {
				return {
					...action,
					isVisible: (item: any) => Boolean(item?.shareable),
				};
			}
			else if (action?.data?.id === 'view-content') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.className !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME && !item?.file
						),
				};
			}
			else if (action?.data?.id === 'view-file') {
				return {
					...action,
					isVisible: (item: any) =>
						Boolean(
							item?.className !==
								OBJECT_ENTRY_FOLDER_CLASS_NAME && item?.file
						),
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
					collaboratorURL: collaboratorURLs[itemData.className],
					creator: itemData.creator,
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

				const transformedItems = filteredItems.map((item: any) => ({
					...item,
					embedded: {
						file: item.file ?? undefined,
						id: item.classPK,
						title: item.title,
					},
				}));

				openModal({
					containerProps: {
						className: '',
					},
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
	};
}
