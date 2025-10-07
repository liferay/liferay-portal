/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';

import {openAssetUsageListModal} from '../../common/components/asset_usage/utils';
import formatActionURL from '../../common/utils/formatActionURL';
import AssetNavigationModalContent from '../modal/asset_navigation_view/AssetNavigationModalContent';
import {AdditionalProps} from './AssetsFDSPropsTransformer';
import deleteItemAction from './actions/deleteItemAction';
import shareAction from './actions/shareAction';
import AssetRenderer from './cell_renderers/AssetRenderer';

export default function HomeRecentAssetsFDSPropsTransformer({
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
			if (action?.data?.id === 'download') {
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
			if (action?.data?.id === 'delete') {
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

			if (
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
			else if (
				action?.data?.id === 'view-content' ||
				action?.data?.id === 'view-file'
			) {
				event?.preventDefault();

				const currentItemPos = items.findIndex(
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
					itemId: itemData.embedded.id,
					title: itemData.embedded?.title,
				});
			}
		},
	};
}
