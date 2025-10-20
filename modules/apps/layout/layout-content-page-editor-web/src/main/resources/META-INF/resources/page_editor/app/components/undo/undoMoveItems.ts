/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../../types/State';
import {LayoutDataItem} from '../../../types/layout_data/LayoutData';
import {FragmentEntryLink} from '../../actions/addFragmentEntryLinks';
import moveItemsAction from '../../actions/moveItems';
import moveItems from '../../thunks/moveItems';

function undoAction({
	action,
}: {
	action: ReturnType<typeof moveItemsAction> & {
		parentItemIds: LayoutDataItem['itemId'][];
		positions: number[];
		unmappedFragmentEntryLinks: FragmentEntryLink[];
	};
}) {
	const {itemIds, parentItemIds, positions, unmappedFragmentEntryLinks} =
		action;

	return moveItems({
		itemIds,
		parentItemIds,
		positions,
		unmappedFragmentEntryLinks,
	});
}

function getDerivedStateForUndo({
	action,
	state,
}: {
	action: ReturnType<typeof moveItemsAction>;
	state: State;
}) {
	const {itemIds} = action;
	const {fragmentEntryLinks, layoutData} = state;

	const positions = [];
	const parentItemIds = [];

	for (const itemId of itemIds) {
		const item = layoutData.items[itemId];
		const parent = layoutData.items[item.parentId];

		parentItemIds.push(parent.itemId);
		positions.push(parent.children.indexOf(itemId));
	}

	const unmappedFragmentEntryLinks = [];

	for (const {fragmentEntryLinkId} of action.fragmentEntryLinks) {
		unmappedFragmentEntryLinks.push(
			fragmentEntryLinks[fragmentEntryLinkId]
		);
	}

	return {
		itemIds,
		parentItemIds,
		positions,
		unmappedFragmentEntryLinks,
	};
}

export {getDerivedStateForUndo, undoAction};
