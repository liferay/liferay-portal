/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayCard from '@clayui/card';
import ClayIcon from '@clayui/icon';
import {ContentCol} from '@clayui/layout';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import React, {RefObject} from 'react';

import {
	DRAG_OVER_POSITIONS,
	DragOverPosition,
} from '../../config/constants/dragOverPositions';
import {useKeyboardDragItem} from './KeyboardDragAndDropContext';
import {useMouseDragItem, useMouseDropTarget} from './MouseDragAndDropContext';

export interface Item {
	id: string;
	name: string;
}

interface ItemProps {
	index: number;
	item: Item;
	onDropItem: (
		itemId: Item['id'],
		index: number,
		dragOverPosition?: DragOverPosition
	) => void;
}

export function Item({index, item, onDropItem}: ItemProps) {
	const dragButtonDescriptionId = useId();
	const itemDescriptionId = useId();
	const {name} = item;

	const {handlerRef: mouseDragHandlerRef, isDragging: isMouseDragging} =
		useMouseDragItem(item);

	const {
		dragOverPosition: keyboardDragOverPosition,
		handlerRef: keyboardDragHandlerRef,
		isDragging: isKeyboardDragging,
		targetRef: keyboardDropTargetRef,
	} = useKeyboardDragItem(item, onDropItem);

	const {
		dragOverPosition: mouseDragOverPosition,
		targetRef: mouseDropTargetRef,
	} = useMouseDropTarget(item.id, index, onDropItem);

	const targetRef = (element: HTMLDivElement | null) => {
		keyboardDropTargetRef(element);
		mouseDropTargetRef(element);
	};

	return (
		<div className="c-pb-3" ref={targetRef} role="listitem">
			<div ref={mouseDragHandlerRef}>
				<ClayCard
					className={classNames('c-mb-0', {
						dragging: isMouseDragging || isKeyboardDragging,
						draggingOver:
							mouseDragOverPosition || keyboardDragOverPosition,
						draggingOverBottom:
							mouseDragOverPosition ===
								DRAG_OVER_POSITIONS.bottom ||
							keyboardDragOverPosition ===
								DRAG_OVER_POSITIONS.bottom,
						draggingOverTop:
							mouseDragOverPosition === DRAG_OVER_POSITIONS.top ||
							keyboardDragOverPosition ===
								DRAG_OVER_POSITIONS.top,
					})}
				>
					<ClayCard.Body className="px-0">
						<ClayCard.Row className="align-items-center">
							<ContentCol gutters>
								<ClayButton
									aria-labelledby={`${dragButtonDescriptionId} ${itemDescriptionId}`}
									className="reorder-list-drag-handle"
									data-item-id={item.id}
									displayType="unstyled"
									monospaced
									ref={
										keyboardDragHandlerRef as unknown as RefObject<HTMLButtonElement>
									}
									size="xs"
									tabIndex={-1}
								>
									<ClayIcon
										className="text-secondary"
										symbol="drag"
									/>

									<span
										className="sr-only"
										id={dragButtonDescriptionId}
									>
										{Liferay.Language.get('reorder')}
									</span>
								</ClayButton>
							</ContentCol>

							<ContentCol expand>
								<ClayCard.Description
									className="text-uppercase"
									displayType="title"
									id={itemDescriptionId}
									title={name}
								>
									{name}
								</ClayCard.Description>
							</ContentCol>
						</ClayCard.Row>
					</ClayCard.Body>
				</ClayCard>
			</div>
		</div>
	);
}
