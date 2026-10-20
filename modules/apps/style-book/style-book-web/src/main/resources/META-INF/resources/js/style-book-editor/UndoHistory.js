/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import {
	ReactPortal,
	useEventListener,
	useIsMounted,
} from '@liferay/frontend-js-react-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {UNDO_TYPES} from './constants/undoTypes';
import {
	useMultipleUndo,
	useRedoHistory,
	useUndoHistory,
} from './contexts/StyleBookEditorContext';

export default function UndoHistory() {
	const isMounted = useIsMounted();
	const multipleUndo = useMultipleUndo();
	const redoHistory = useRedoHistory();
	const undoHistory = useUndoHistory();

	const [active, setActive] = useState(false);
	const [loading, setLoading] = useState(false);

	const onHistoryItemClick = (event, numberOfActions, type) => {
		event.preventDefault();

		setLoading(true);

		multipleUndo({
			numberOfActions,
			type,
		}).then(() => {
			if (isMounted()) {
				setLoading(false);
			}
		});
	};

	return (
		<>
			<ClayDropDown
				active={active}
				alignmentPosition={Align.BottomRight}
				className="ml-2 mr-2"
				menuElementAttrs={{
					className: 'style-book-editor__undo-history',
					containerProps: {
						className: 'cadmin',
					},
				}}
				onActiveChange={setActive}
				trigger={
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('history[record]')}
						aria-pressed={active}
						disabled={!undoHistory.length && !redoHistory.length}
						displayType="secondary"
						size="sm"
						symbol="time"
						title={Liferay.Language.get('history[record]')}
					/>
				}
			>
				<ClayDropDown.ItemList>
					<History
						actions={redoHistory}
						onHistoryItemClick={onHistoryItemClick}
						type={UNDO_TYPES.redo}
					/>

					<History
						actions={undoHistory}
						onHistoryItemClick={onHistoryItemClick}
						type={UNDO_TYPES.undo}
					/>

					<ClayDropDown.Divider />

					<ClayDropDown.Item
						disabled={!undoHistory.length}
						onClick={(event) => {
							onHistoryItemClick(
								event,
								undoHistory.length,
								UNDO_TYPES.undo
							);
						}}
					>
						{Liferay.Language.get('undo-all')}
					</ClayDropDown.Item>
				</ClayDropDown.ItemList>
			</ClayDropDown>

			{loading && (
				<ReactPortal className="cadmin">
					<Overlay />
				</ReactPortal>
			)}
		</>
	);
}

const Overlay = () => {
	useEventListener(
		'keydown',
		(event) => {
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		},
		true,
		window
	);

	return (
		<div
			className="style-book-editor__undo-history__overlay"
			onClickCapture={(event) => {
				event.preventDefault();
				event.stopPropagation();
			}}
		></div>
	);
};
const History = ({actions = [], type, onHistoryItemClick}) => {
	const isSelectedAction = (index) => type === UNDO_TYPES.undo && index === 0;

	const actionList =
		type === UNDO_TYPES.undo ? actions : [...actions].reverse();

	return actionList.map((action, index) => (
		<ClayDropDown.Item
			disabled={isSelectedAction(index)}
			key={index}
			onClick={(event) => {
				const numberOfActions =
					type === UNDO_TYPES.undo
						? index
						: actionList.length - index;

				onHistoryItemClick(event, numberOfActions, type);
			}}
			symbolRight={isSelectedAction(index) ? 'check' : ''}
		>
			{sub(Liferay.Language.get('update-x'), action.label)}
		</ClayDropDown.Item>
	));
};
