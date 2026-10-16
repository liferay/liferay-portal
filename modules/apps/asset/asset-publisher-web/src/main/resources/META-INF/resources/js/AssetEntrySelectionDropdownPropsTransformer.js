/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {getAssetEntriesItemSelectorProps} from 'asset-taglib';

export default function propsTransformer({
	additionalProps,
	portletNamespace,
	...props
}) {
	const {assetEntryTypes = [], currentURL, groupIds} = additionalProps;

	return {
		...props,
		onClick(event) {
			event.preventDefault();

			openItemSelectorModal({
				...getAssetEntriesItemSelectorProps({
					assetEntryTypes,
					groupIds,
					portletNamespace,
				}),
				items: [],
				onItemsChange(selectedAssetEntries) {
					if (!selectedAssetEntries || !selectedAssetEntries.length) {
						return;
					}

					Liferay.Util.postForm(document[`${portletNamespace}fm`], {
						data: {
							assetEntryIds: selectedAssetEntries
								.map(
									(selectedAssetEntry) =>
										selectedAssetEntry.assetEntryId
								)
								.join(','),
							assetEntryType: selectedAssetEntries[0].className,
							cmd: 'add-selection',
							redirect: currentURL,
						},
					});
				},
			});
		},
	};
}
