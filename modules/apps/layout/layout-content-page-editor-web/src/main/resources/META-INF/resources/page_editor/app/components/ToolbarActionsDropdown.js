/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import React from 'react';

import {useSelectMultipleItems} from '../contexts/ControlsContext';
import {useSelector} from '../contexts/StoreContext';
import {onDiscardDraft} from './DiscardDraftButton';
import {useDisabledRedo, useDisabledUndo} from './undo/Undo';
import {useHistoryItems, useOnHistoryItemClick} from './undo/UndoHistory';
import UndoOverlay from './undo/UndoOverlay';
import useUndoRedoActions from './undo/useUndoRedoActions';
import useDisabledDiscardDraft from './useDisabledDiscardDraft';
import useOnToggleSidebars from './useOnToggleSidebars';

export default function ToolbarActionsDropdown({discardDraftFormRef}) {
	const disabledDiscardDraft = useDisabledDiscardDraft();
	const disabledRedo = useDisabledRedo();
	const disabledUndo = useDisabledUndo();
	const historyItems = useHistoryItems();
	const {loadingHistory, onHistoryItemClick} = useOnHistoryItemClick();
	const {onRedo, onUndo} = useUndoRedoActions();
	const onToggleSidebars = useOnToggleSidebars();
	const selectMultipleItems = useSelectMultipleItems();
	const sidebarHidden = useSelector((state) => state.sidebar.hidden);
	const undoHistory = useSelector((state) => state.undoHistory);

	const selectItems = selectMultipleItems;

	return (
		<>
			{loadingHistory && <UndoOverlay />}
			<ClayDropDownWithItems
				hasLeftSymbols
				items={[
					{
						disabled: disabledUndo,
						label: Liferay.Language.get('undo'),
						onClick: () => onUndo({selectItems}),
						symbolLeft: 'undo',
					},
					{
						disabled: disabledRedo,
						label: Liferay.Language.get('redo'),
						onClick: () => onRedo({selectItems}),
						symbolLeft: 'redo',
					},
					{
						disabled: !historyItems.length,
						items: historyItems.length
							? [
									...historyItems,
									{type: 'divider'},
									{
										disabled: !undoHistory.length,
										label: Liferay.Language.get('undo-all'),
										onClick: onHistoryItemClick,
									},
								]
							: null,
						label: Liferay.Language.get('draft-history'),
						symbolLeft: 'time',
						type: 'contextual',
					},
					{type: 'divider'},
					{
						label: sidebarHidden
							? Liferay.Language.get('show-sidebars')
							: Liferay.Language.get('hide-sidebars'),
						onClick: onToggleSidebars,
						symbolLeft: 'view',
					},
					{type: 'divider'},
					{
						disabled: disabledDiscardDraft,
						label: Liferay.Language.get('discard-draft'),
						onClick: (event) =>
							onDiscardDraft(event, discardDraftFormRef.current),
					},
				]}
				trigger={
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('actions')}
						displayType="secondary"
						size="sm"
						symbol="ellipsis-v"
						title={Liferay.Language.get('actions')}
					/>
				}
			/>
		</>
	);
}
