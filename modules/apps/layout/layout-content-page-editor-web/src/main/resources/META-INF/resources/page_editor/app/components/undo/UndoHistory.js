/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import React, {useState} from 'react';
import {v4 as uuidv4} from 'uuid';

import {SELECT_SEGMENTS_EXPERIENCE} from '../../../plugins/experience/actions';
import {UNDO_TYPES} from '../../config/constants/undoTypes';
import {config} from '../../config/index';
import {useDispatch, useSelector} from '../../contexts/StoreContext';
import multipleUndo from '../../thunks/multipleUndo';
import getSegmentsExperienceName from '../../utils/getSegmentsExperienceName';
import UndoOverlay from './UndoOverlay';
import getActionLabel from './getActionLabel';

export function useOnHistoryItemClick() {
	const dispatch = useDispatch();
	const isMounted = useIsMounted();
	const [loadingHistory, setLoadingHistory] = useState(false);
	const store = useSelector((state) => state);
	const undoHistory = useSelector((state) => state.undoHistory || []);

	const onHistoryItemClick = (
		event,
		numberOfActions = undoHistory.length,
		type = UNDO_TYPES.undo
	) => {
		event.preventDefault();

		setLoadingHistory(true);

		dispatch(
			multipleUndo({
				numberOfActions,
				store,
				type,
			})
		).finally(() => {
			if (isMounted()) {
				setLoadingHistory(false);
			}
		});
	};

	return {loadingHistory, onHistoryItemClick};
}

export function useHistoryItems() {
	const redoHistory = useSelector((state) => state.redoHistory || []);
	const undoHistory = useSelector((state) => state.undoHistory || []);
	const store = useSelector((state) => state);
	const {onHistoryItemClick} = useOnHistoryItemClick();

	const actionsToItems = (items, type) => {
		const isSelectedAction = (index) =>
			type === UNDO_TYPES.undo && index === 0;

		return items.map((action, index) => ({
			disabled: isSelectedAction(index),
			experience:
				action.type !== SELECT_SEGMENTS_EXPERIENCE &&
				action.segmentsExperienceId !==
					config.defaultSegmentsExperienceId &&
				!config.singleSegmentsExperienceMode &&
				getSegmentsExperienceName(
					action.segmentsExperienceId,
					store.availableSegmentsExperiences
				),
			id: uuidv4(),
			label: getActionLabel(action, type, {
				availableSegmentsExperiences:
					store.availableSegmentsExperiences,
			}),
			onClick: (event) => {
				const numberOfActions =
					type === UNDO_TYPES.undo ? index : items.length - index;

				onHistoryItemClick(event, numberOfActions, type);
			},
			symbolRight: isSelectedAction(index) ? 'check' : '',
		}));
	};

	const redoItems = actionsToItems(
		[...redoHistory].reverse(),
		UNDO_TYPES.redo
	);
	const undoItems = actionsToItems(undoHistory, UNDO_TYPES.undo);

	return [...redoItems, ...undoItems];
}

export default function UndoHistory() {
	const redoHistory = useSelector((state) => state.redoHistory || []);
	const undoHistory = useSelector((state) => state.undoHistory || []);

	const [active, setActive] = useState(false);

	const {loadingHistory, onHistoryItemClick} = useOnHistoryItemClick();
	const historyItems = useHistoryItems();

	return (
		<>
			<ClayDropDown
				active={active}
				alignmentPosition={Align.BottomRight}
				className="ml-2"
				menuElementAttrs={{
					className: 'page-editor__undo-history',
					containerProps: {
						className: 'cadmin',
					},
				}}
				onActiveChange={setActive}
				trigger={
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('draft-history')}
						aria-pressed={active}
						disabled={!undoHistory.length && !redoHistory.length}
						displayType="secondary"
						size="sm"
						symbol="time"
						title={Liferay.Language.get('draft-history')}
					/>
				}
			>
				<ClayDropDown.ItemList>
					{historyItems.map((item) => {
						return (
							<ClayDropDown.Item
								disabled={item.disabled}
								key={item.id}
								onClick={item.onClick}
								symbolRight={item.symbolRight}
							>
								{item.label}

								{item.experience}
							</ClayDropDown.Item>
						);
					})}

					<ClayDropDown.Divider />

					<ClayDropDown.Item
						disabled={!undoHistory.length}
						onClick={onHistoryItemClick}
					>
						{Liferay.Language.get('undo-all')}
					</ClayDropDown.Item>
				</ClayDropDown.ItemList>
			</ClayDropDown>

			{loadingHistory && <UndoOverlay />}
		</>
	);
}
