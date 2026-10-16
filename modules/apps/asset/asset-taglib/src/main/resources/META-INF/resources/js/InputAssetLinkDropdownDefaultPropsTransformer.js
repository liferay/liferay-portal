/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';

import getAssetEntriesItemSelectorProps from './getAssetEntriesItemSelectorProps';

export default function propsTransformer({
	additionalProps,
	portletNamespace,
	...props
}) {
	const {
		assetEntryTypes = [],
		groupIds,
		refererClassNameId,
		refererClassPK,
		removeIcon,
	} = additionalProps;

	return {
		...props,
		onClick(event) {
			event.preventDefault();

			const searchContainer = Liferay.SearchContainer.get(
				`${portletNamespace}assetLinksSearchContainer`
			);

			const searchContainerData = searchContainer.getData();

			const existingEntryIds = searchContainerData
				? searchContainerData.split(',')
				: [];

			const excludedAssetEntry =
				refererClassNameId > 0 && refererClassPK > 0
					? {classNameId: refererClassNameId, classPK: refererClassPK}
					: null;

			openItemSelectorModal({
				...getAssetEntriesItemSelectorProps({
					assetEntryTypes,
					excludedAssetEntry,
					groupIds,
					portletNamespace,
				}),
				items: existingEntryIds.map((entryId) => ({
					assetEntryId: Number(entryId),
				})),
				onItemsChange(selectedAssetEntries) {
					if (!selectedAssetEntries) {
						return;
					}

					selectedAssetEntries.forEach((selectedAssetEntry) => {
						const entryId = selectedAssetEntry.assetEntryId;

						if (existingEntryIds.indexOf(String(entryId)) !== -1) {
							return;
						}

						searchContainer.addRow(
							[
								`<div class="list-group-title">${Liferay.Util.escapeHTML(
									selectedAssetEntry.title
								)}</div><p class="list-group-subtitle">${Liferay.Util.escapeHTML(
									selectedAssetEntry.assetType
								)}</p><p class="list-group-subtitle">${Liferay.Language.get(
									'scope'
								)}: ${Liferay.Util.escapeHTML(
									selectedAssetEntry.groupDescriptiveName
								)}</p>`,
								`<button aria-label="${Liferay.Language.get(
									'remove'
								)}" class="btn btn-monospaced btn-outline-borderless btn-outline-secondary float-right lfr-portal-tooltip modify-link" data-rowId="${entryId}" title="${Liferay.Language.get(
									'remove'
								)}" type="button">${removeIcon}</button>`,
							],
							entryId
						);

						searchContainer.updateDataStore();

						existingEntryIds.push(String(entryId));
					});
				},
			});
		},
	};
}
