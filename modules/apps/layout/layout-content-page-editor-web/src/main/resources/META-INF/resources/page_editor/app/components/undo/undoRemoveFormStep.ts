/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentEntryLink} from '../../../app/actions/addFragmentEntryLinks';
import removeFormStep from '../../../app/actions/removeFormStep';
import updateFormItemConfig from '../../../app/actions/updateFormItemConfig';
import {Dispatch} from '../../../app/contexts/StoreContext';
import {getFormParent} from '../../../app/utils/getFormParent';
import {getStepperChild} from '../../../app/utils/getStepperChild';
import {State} from '../../../types/State';
import {FormLayoutDataItem} from '../../../types/layout_data/FormLayoutDataItem';
import LayoutService from '../../services/LayoutService';

function undoAction({
	action,
	store,
}: {
	action: ReturnType<typeof removeFormStep> & {
		form: FormLayoutDataItem;
		stepperFragmentEntryLinkId: FragmentEntryLink['fragmentEntryLinkId'];
	};
	store: State;
}) {
	const {form, movedItemIds, removedItemIds, stepperFragmentEntryLinkId} =
		action;

	const nextMovedItems: Array<{itemId: string; parentId: string}> = [];

	for (const movedItem of movedItemIds) {
		const item = store.layoutData.items[movedItem.itemId];

		nextMovedItems.push({itemId: item.itemId, parentId: item.parentId});
	}

	return (dispatch: Dispatch) => {
		return LayoutService.undoUpdateFormConfig({
			addedItemIds: removedItemIds,
			itemConfig: form.config,
			itemId: form.itemId,
			movedItemIds,
			onNetworkStatus: dispatch,
			removedItemIds: [],
			segmentsExperienceId: store.segmentsExperienceId,
			stepperFragmentEntryLinkId,
		}).then(({fragmentEntryLinks, layoutData}) => {
			dispatch(
				updateFormItemConfig({
					addedItemIds: removedItemIds,
					fragmentEntryLinks,
					isMapping: false,
					itemIds: [form.itemId],
					layoutData,
					movedItemIds: nextMovedItems,
					removedFragmentEntryLinkIds: removedItemIds
						.map((itemId) => {
							const item = layoutData.items[itemId];

							if ('fragmentEntryLinkId' in item.config) {
								return item?.config?.fragmentEntryLinkId;
							}

							return '';
						})
						.filter(Boolean),
				})
			);
		});
	};
}

function getDerivedStateForUndo({
	action,
	state,
}: {
	action: ReturnType<typeof removeFormStep>;
	state: State;
}) {
	const {fragmentEntryLinks, layoutData} = state;

	const item = layoutData.items[action.itemId];

	const form = getFormParent(item, layoutData);

	if (!form) {
		return;
	}

	const stepper = getStepperChild(form, layoutData, fragmentEntryLinks);

	return {
		form,
		fragmentEntryLinks: state.fragmentEntryLinks,
		layoutData: state.layoutData,
		movedItemIds: action.movedItemIds,
		removedItemIds: action.removedItemIds,
		stepperFragmentEntryLinkId: stepper?.config?.fragmentEntryLinkId,
	};
}

export {getDerivedStateForUndo, undoAction};
