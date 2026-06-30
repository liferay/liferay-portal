/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useRef, useState} from 'react';
import {
	DragObjectWithType,
	DropTargetMonitor,
	useDrag,
	useDrop,
} from 'react-dnd';
import {getEmptyImage} from 'react-dnd-html5-backend';

import './useDragAndDrop.scss';
import {DropPosition} from './useDragAndDrop';

const HOVER_BORDER_LIMIT = 60;

const ITEM_TYPE = 'item';

export default function usePointerDragAndDrop<
	T extends {
		id: string;
	},
>({
	dragHandlerRef,
	dropItemRef,
	hoverLimit = HOVER_BORDER_LIMIT,
	items,
	onDrop,
	targetItem,
}: {
	dragHandlerRef: React.RefObject<HTMLElement>;
	dropItemRef: React.RefObject<HTMLElement>;
	hoverLimit?: number;
	items: T[];
	onDrop?: (items: T[]) => void;
	targetItem: T;
}) {
	const dropIndexRef = useRef<number>(0);
	const [dropPosition, setDropPosition] = useState<DropPosition>(null);

	const [{isOver}, drop] = useDrop<
		T & DragObjectWithType,
		void,
		{draggingItem: T; isOver: boolean}
	>({
		accept: ITEM_TYPE,
		canDrop: (draggedItem) => {
			const draggedItemIndex = items.findIndex(
				({id}) => id === draggedItem.id
			);

			return draggedItemIndex !== dropIndexRef.current;
		},
		collect: (monitor) => ({
			draggingItem: monitor.getItem(),
			isOver: monitor.isOver({shallow: true}),
		}),
		drop(droppedItem, monitor) {
			if (!monitor.canDrop()) {
				return;
			}

			const newItems = items.filter(({id}) => id !== droppedItem.id);

			newItems.splice(dropIndexRef.current, 0, droppedItem);

			onDrop?.(newItems);
		},
		hover(draggedItem, monitor) {
			if (!dropItemRef.current) {
				return;
			}

			let dropPosition: DropPosition = null;

			if (targetItem.id !== draggedItem.id) {
				dropPosition = getDropPosition(
					dropItemRef,
					monitor,
					hoverLimit
				);
			}

			setDropPosition(dropPosition);

			const targetIndex = items
				.filter(({id}) => id !== draggedItem.id)
				.findIndex(({id}) => id === targetItem.id);

			dropIndexRef.current = Math.max(
				0,
				targetIndex + (dropPosition === 'bottom' ? 1 : 0)
			);
		},
	});

	const [{isDragging}, drag, previewRef] = useDrag<
		DragObjectWithType,
		void,
		{isDragging: boolean}
	>({
		collect: (monitor) => ({
			isDragging: monitor.isDragging(),
		}),
		item: {...targetItem, type: ITEM_TYPE},
	});

	useEffect(() => {
		drag(dragHandlerRef);
	}, [drag, dragHandlerRef]);

	useEffect(() => {
		drop(dropItemRef);
	}, [drop, dropItemRef]);

	useEffect(() => {
		previewRef(getEmptyImage());
	}, [previewRef]);

	return {
		isPointerDragging: isDragging,
		isPointerDropBottomPosition: isOver && dropPosition === 'bottom',
		isPointerDropTopPosition: isOver && dropPosition === 'top',
	};
}

function getDropPosition(
	ref: React.RefObject<HTMLElement>,
	monitor: DropTargetMonitor,
	hoverLimit: number
) {
	if (!ref.current) {
		return null;
	}

	const clientOffset = monitor.getClientOffset()!;
	const dropItemBoundingRect = ref.current.getBoundingClientRect();
	const hoverBottomLimit = Math.max(
		dropItemBoundingRect.height / 2,
		dropItemBoundingRect.height - hoverLimit
	);
	const hoverClientY = clientOffset.y - dropItemBoundingRect.top;

	return hoverClientY > hoverBottomLimit ? 'bottom' : 'top';
}
