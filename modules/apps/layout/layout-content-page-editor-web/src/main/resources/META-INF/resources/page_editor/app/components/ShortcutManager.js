/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isCtrlOrMeta} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useRef, useState} from 'react';

import {ITEM_ACTIVATION_ORIGINS} from '../config/constants/itemActivationOrigins';
import {ITEM_TYPES} from '../config/constants/itemTypes';
import {
	BACKSPACE_KEY_CODE,
	C_KEY_CODE,
	D_KEY_CODE,
	H_KEY_CODE,
	PERIOD_KEY_CODE,
	R_KEY_CODE,
	S_KEY_CODE,
	V_KEY_CODE,
	X_KEY_CODE,
	Z_KEY_CODE,
} from '../config/constants/keyboardCodes';
import {LAYOUT_DATA_ITEM_TYPES} from '../config/constants/layoutDataItemTypes';
import {useClipboard, useSetClipboard} from '../contexts/ClipboardContext';
import {
	useActiveItemIds,
	useActiveItemType,
	useSelectItem,
	useSelectMultipleItems,
} from '../contexts/ControlsContext';
import {
	useOpenShortcutModal,
	useSetEditedNodeId,
	useSetOpenShortcutModal,
} from '../contexts/ShortcutContext';
import {useDispatch, useSelector} from '../contexts/StoreContext';
import {useGetWidgets} from '../contexts/WidgetsContext';
import selectCanManageFragmentEntries from '../selectors/selectCanManageFragmentEntries';
import selectCanUpdatePageStructure from '../selectors/selectCanUpdatePageStructure';
import deleteItem from '../thunks/deleteItem';
import duplicateItem from '../thunks/duplicateItem';
import pasteItems from '../thunks/pasteItems';
import switchSidebarPanel from '../thunks/switchSidebarPanel';
import canBeDuplicated from '../utils/canBeDuplicated';
import canBeHidden from '../utils/canBeHidden';
import canBeRemoved from '../utils/canBeRemoved';
import canBeRenamed from '../utils/canBeRenamed';
import canBeSaved from '../utils/canBeSaved';
import isCuttable from '../utils/isCuttable';
import {isMovementValid} from '../utils/isMovementValid';
import toMovementItem from '../utils/toMovementItem';
import updateItemStyle from '../utils/updateItemStyle';
import SaveFragmentCompositionModal from './SaveFragmentCompositionModal';
import ShortcutModal from './ShortcutModal';
import useUndoRedoActions from './undo/useUndoRedoActions';

export default function ShortcutManager() {
	const activeItemIds = useActiveItemIds();
	const activeItemType = useActiveItemType();
	const selectItem = useSelectItem();
	const selectMultipleItems = useSelectMultipleItems();

	const clipboard = useClipboard();
	const setClipboard = useSetClipboard();

	const dispatch = useDispatch();
	const getWidgets = useGetWidgets();
	const openShortcutModal = useOpenShortcutModal();

	const setEditedNodeId = useSetEditedNodeId();
	const setOpenShortcutModal = useSetOpenShortcutModal();

	const {onRedo, onUndo} = useUndoRedoActions();

	const canManageFragments = useSelector(selectCanManageFragmentEntries);
	const canUpdatePageStructure = useSelector(selectCanUpdatePageStructure);
	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const layoutData = useSelector((state) => state.layoutData);
	const sidebarHidden = useSelector((state) => state.sidebar.hidden);

	const masterLayoutData = useSelector(
		(state) => state.masterLayout?.masterLayoutData
	);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const [openSaveModal, setOpenSaveModal] = useState(false);

	const getParentItemId = () => {
		const rootItem = layoutData.items[layoutData.rootItems.main];

		return !activeItemIds?.length ? rootItem.itemId : activeItemIds[0];
	};

	const selectItems = selectMultipleItems;

	const multiSelection = activeItemIds.length > 1;

	const activeLayoutDataItem =
		activeItemType === ITEM_TYPES.layoutDataItem
			? layoutData.items[activeItemIds[0]]
			: null;

	const keymapRef = useRef(null);

	keymapRef.current = {
		copy: {
			action: () => setClipboard(activeItemIds),
			canBeExecuted: () =>
				!isEditingEditableField() &&
				!isTextSelected() &&
				canUpdatePageStructure &&
				activeItemIds.every(
					(activeItemId) =>
						!!layoutData.items[activeItemId] &&
						canBeDuplicated(
							fragmentEntryLinks,
							layoutData.items[activeItemId],
							layoutData,
							getWidgets
						)
				),
			isKeyCombination: (event) =>
				isCtrlOrMeta(event) && event.code === C_KEY_CODE,
		},
		cut: {
			action: () => {
				setClipboard(activeItemIds);

				dispatch(
					deleteItem({
						itemIds: activeItemIds,
						selectItems,
					})
				);
			},
			canBeExecuted: (event) =>
				!isEditingEditableField() &&
				!isTextSelected() &&
				canUpdatePageStructure &&
				!isInteractiveElement(event.target) &&
				activeItemIds.every((id) =>
					isCuttable(id, fragmentEntryLinks, layoutData)
				),
			isKeyCombination: (event) =>
				isCtrlOrMeta(event) && event.code === X_KEY_CODE,
		},
		duplicate: {
			action: () =>
				dispatch(
					duplicateItem({
						itemIds: activeItemIds,
						selectItems,
					})
				),
			canBeExecuted: () =>
				canUpdatePageStructure &&
				!!activeItemIds.length &&
				activeItemIds.every(
					(activeItemId) =>
						!!layoutData.items[activeItemId] &&
						canBeDuplicated(
							fragmentEntryLinks,
							layoutData.items[activeItemId],
							layoutData,
							getWidgets
						)
				),

			isKeyCombination: (event) =>
				isCtrlOrMeta(event) &&
				event.altKey &&
				event.code === D_KEY_CODE,
		},
		hideShow: {
			action: () =>
				updateItemStyle({
					dispatch,
					itemIds: activeItemIds,
					selectedViewportSize,
					styleName: 'display',
					styleValue:
						layoutData.items[activeItemIds[0]].config.styles
							.display === 'none'
							? 'block'
							: 'none',
				}),
			canBeExecuted: () =>
				canUpdatePageStructure &&
				!!activeItemIds.length &&
				activeItemIds.every(
					(activeItemId) =>
						!!layoutData.items[activeItemId] &&
						canBeHidden({
							fragmentEntryLinks,
							item: layoutData.items[activeItemId],
							layoutData,
							masterLayoutData,
							selectedViewportSize,
						})
				),

			isKeyCombination: (event) =>
				isCtrlOrMeta(event) &&
				event.altKey &&
				event.code === H_KEY_CODE,
		},
		hideSidebar: {
			action: () =>
				dispatch(switchSidebarPanel({hidden: !sidebarHidden})),
			canBeExecuted: (event) =>
				!isInteractiveElement(event.target) &&
				!isWithinIframe() &&
				!isEditingEditableField(),

			isKeyCombination: (event) =>
				isCtrlOrMeta(event) &&
				event.shiftKey &&
				event.code === PERIOD_KEY_CODE,
		},
		openShortcutModal: {
			action: () => setOpenShortcutModal(true),
			canBeExecuted: (event) =>
				!isInteractiveElement(event.target) &&
				!isWithinIframe() &&
				!isEditingEditableField(),
			isKeyCombination: (event) => event.shiftKey && event.key === '?',
		},
		paste: {
			action: () =>
				dispatch(
					pasteItems({
						clipboard,
						parentItemId: getParentItemId(),
						selectItems,
					})
				),
			canBeExecuted: () =>
				!isEditingEditableField() &&
				!isInteractiveElement(document.activeElement) &&
				canUpdatePageStructure &&
				isOnlyOneParentSelected(activeItemIds) &&
				clipboard.length &&
				isMovementValid({
					fragmentEntryLinks,
					getWidgets,
					layoutData,
					sources: clipboard.map((id) =>
						toMovementItem(id, layoutData, fragmentEntryLinks)
					),
					targetId: getParentItemId(),
				}),
			isKeyCombination: (event) =>
				isCtrlOrMeta(event) && event.code === V_KEY_CODE,
		},
		remove: {
			action: () =>
				dispatch(
					deleteItem({
						itemIds: activeItemIds,
						selectItems,
					})
				),
			canBeExecuted: (event) =>
				canUpdatePageStructure &&
				!!activeItemIds.length &&
				activeItemIds.every(
					(activeItemId) =>
						!!layoutData.items[activeItemId] &&
						canBeRemoved(
							layoutData.items[activeItemId],
							layoutData
						) &&
						!isInteractiveElement(event.target)
				),
			isKeyCombination: (event) => event.code === BACKSPACE_KEY_CODE,
		},
		rename: {
			action: () => setEditedNodeId(activeItemIds[0]),
			canBeExecuted: () =>
				!multiSelection &&
				canUpdatePageStructure &&
				!!layoutData.items[activeItemIds[0]] &&
				canBeRenamed(layoutData.items[activeItemIds[0]]),
			isKeyCombination: (event) =>
				isCtrlOrMeta(event) &&
				event.altKey &&
				event.code === R_KEY_CODE,
		},
		save: {
			action: () => setOpenSaveModal(true),
			canBeExecuted: () =>
				canManageFragments &&
				!multiSelection &&
				canUpdatePageStructure &&
				!!layoutData.items[activeItemIds[0]] &&
				canBeSaved(layoutData.items[activeItemIds[0]], layoutData),
			isKeyCombination: (event) =>
				isCtrlOrMeta(event) && event.code === S_KEY_CODE,
		},
		selectParent: {
			action: () => {
				const selectableParent = getSelectableParent(
					layoutData,
					activeLayoutDataItem
				);

				if (selectableParent) {
					selectItem(selectableParent.itemId, {
						itemType: ITEM_TYPES.layoutDataItem,
						origin: ITEM_ACTIVATION_ORIGINS.layout,
					});
				}
			},
			canBeExecuted: (event) =>
				!multiSelection &&
				!isInteractiveElement(event.target) &&
				activeLayoutDataItem,
			isKeyCombination: (event) =>
				event.shiftKey && event.key === 'Enter',
		},
		undo: {
			action: (event) => {
				if (event.shiftKey) {
					onRedo({selectItems});
				}
				else {
					onUndo({selectItems});
				}
			},
			canBeExecuted: (event) =>
				(isEditableField(event.target) ||
					!isInteractiveElement(event.target)) &&
				!isWithinIframe() &&
				!isEditingEditableField(),
			isKeyCombination: (event) =>
				isCtrlOrMeta(event) &&
				event.code === Z_KEY_CODE &&
				!event.altKey,
		},
	};

	useEffect(() => {
		const onKeyDown = (event) => {
			const shortcut = Object.values(keymapRef.current).find(
				(shortcut) =>
					shortcut.isKeyCombination(event) &&
					shortcut.canBeExecuted(event)
			);

			if (shortcut) {
				event.stopPropagation();
				event.preventDefault();

				shortcut.action(event);
			}
		};

		window.addEventListener('keydown', onKeyDown, true);

		return () => {
			window.removeEventListener('keydown', onKeyDown, true);
		};
	}, []);

	return (
		<>
			{openSaveModal && (
				<SaveFragmentCompositionModal
					onCloseModal={() => setOpenSaveModal(false)}
				/>
			)}

			{openShortcutModal && (
				<ShortcutModal
					onCloseModal={() => setOpenShortcutModal(false)}
				/>
			)}
		</>
	);
}

function getSelectableParent(layoutData, item) {
	if (!item) {
		return null;
	}

	const parentItem = layoutData.items[item.parentId];

	if (!parentItem) {
		return null;
	}

	if (
		parentItem.type !== LAYOUT_DATA_ITEM_TYPES.column &&
		parentItem.type !== LAYOUT_DATA_ITEM_TYPES.collectionItem &&
		parentItem.type !== LAYOUT_DATA_ITEM_TYPES.fragmentDropZone &&
		parentItem.type !== LAYOUT_DATA_ITEM_TYPES.root
	) {
		return parentItem;
	}

	return getSelectableParent(parentItem);
}

function isEditableField(element) {
	return !!element.closest('.page-editor__editable');
}

function isEditingEditableField() {
	return !!document.activeElement.getAttribute('contenteditable');
}

function isInteractiveElement(element) {
	return (
		['INPUT', 'OPTION', 'SELECT', 'TEXTAREA'].includes(element.tagName) ||
		!!element.closest('.alloy-editor-container') ||
		!!element.closest('.cke_editable') ||
		!!element.closest('.dropdown-menu') ||
		!!element.closest('.page-editor__page-structure__item-configuration') ||
		!!element.closest('.page-editor__allowed-fragment__tree') ||
		!!element.classList.contains('ck-editor__editable')
	);
}

function isTextSelected() {
	return window.getSelection().type === 'Range';
}

function isOnlyOneParentSelected(activeItemIds) {
	if (activeItemIds?.length > 1) {
		openToast({
			message: Liferay.Language.get(
				'it-is-not-possible-to-paste-on-two-destinations-at-the-same-time'
			),
			type: 'danger',
		});

		return false;
	}

	return true;
}

function isWithinIframe() {
	return window.top !== window.self;
}
