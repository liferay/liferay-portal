/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactPortal} from '@liferay/frontend-js-react-web';
import React from 'react';
import {useDragLayer} from 'react-dnd';

import {ItemTypes} from './Column';
import {ITaskDragItem, TaskCard} from './Task';

import './TaskDragLayer.scss';

const TASK_CARD_DRAG_SCALE = 0.7;

export default function TaskDragLayer() {
	const {
		cardWidth,
		clientOffset,
		initialClientOffset,
		initialSourceClientOffset,
		isDragging,
		task,
	} = useDragLayer((monitor) => {
		const item: ITaskDragItem | null =
			monitor.getItemType() === ItemTypes.TASK ? monitor.getItem() : null;

		return {
			cardWidth: item?.cardWidth,
			clientOffset: monitor.getClientOffset(),
			initialClientOffset: monitor.getInitialClientOffset(),
			initialSourceClientOffset: monitor.getInitialSourceClientOffset(),
			isDragging: monitor.isDragging() && Boolean(item),
			task: item?.task,
		};
	});

	if (
		!clientOffset ||
		!initialClientOffset ||
		!initialSourceClientOffset ||
		!isDragging ||
		!task
	) {
		return null;
	}

	const translateX =
		clientOffset.x -
		TASK_CARD_DRAG_SCALE *
			(initialClientOffset.x - initialSourceClientOffset.x);
	const translateY =
		clientOffset.y -
		TASK_CARD_DRAG_SCALE *
			(initialClientOffset.y - initialSourceClientOffset.y);

	return (
		<ReactPortal aria-hidden className="lfr__kaban-task-drag-layer">
			<div
				style={{
					transform: `translate(${translateX}px, ${translateY}px) scale(${TASK_CARD_DRAG_SCALE})`,
					transformOrigin: 'top left',
					width: cardWidth ?? 'max-content',
				}}
			>
				<TaskCard task={task} />
			</div>
		</ReactPortal>
	);
}
