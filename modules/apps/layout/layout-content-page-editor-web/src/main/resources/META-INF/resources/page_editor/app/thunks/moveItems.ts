/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../types/State';
import {FragmentEntryLink} from '../actions/addFragmentEntryLinks';
import moveItemsAction from '../actions/moveItems';
import updateNetwork from '../actions/updateNetwork';
import FragmentService from '../services/FragmentService';
import LayoutService from '../services/LayoutService';
import {getUnmappedFragmentEntryLinks} from '../utils/getUnmappedFragments';
import sortItemIds from '../utils/sortItemIds';
import {clearPageContents} from '../utils/usePageContents';
import filterSelectedItems from './filterSelectedItems';

type Props = {
	itemIds: string[];
	parentItemIds: string[];
	positions: number[];
	unmappedFragmentEntryLinks?: FragmentEntryLink[];
};

export default function moveItems({
	itemIds,
	parentItemIds,
	positions,
	unmappedFragmentEntryLinks,
}: Props) {
	return (
		dispatch: (
			action: ReturnType<typeof updateNetwork | typeof moveItemsAction>
		) => void,
		getState: () => State
	) => {
		const {
			fragmentEntryLinks,
			languageId,
			layoutData,
			segmentsExperienceId,
		} = getState();

		const sortedItemIds = sortItemIds(
			filterSelectedItems(itemIds, layoutData.items),
			layoutData
		);

		return LayoutService.moveItems({
			itemIds: sortedItemIds,
			onNetworkStatus: dispatch,
			parentItemIds,
			positions,
			segmentsExperienceId,
		}).then(async (updatedLayoutData) => {

			// Take unmappedFragmentEntryLinks from param
			// for undo, otherwise calculate them now

			const unmappedFragments =
				unmappedFragmentEntryLinks ||
				getUnmappedFragmentEntryLinks({
					fragmentEntryLinks,
					layoutData,
					movedIds: itemIds,
					targetId: parentItemIds[0],
				});

			const updatedFragmentEntryLinks = [];

			for (const {
				editableValues,
				fragmentEntryLinkId,
			} of unmappedFragments) {
				const {fragmentEntryLink} =
					await FragmentService.updateEditableValues({
						editableValues,
						fragmentEntryLinkId,
						languageId,
						onNetworkStatus: dispatch,
						segmentsExperienceId,
					});

				updatedFragmentEntryLinks.push(fragmentEntryLink);
			}

			clearPageContents();

			return dispatch(
				moveItemsAction({
					fragmentEntryLinks: updatedFragmentEntryLinks,
					itemIds: sortedItemIds,
					layoutData: updatedLayoutData,
				})
			);
		});
	};
}
