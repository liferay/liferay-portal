/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';

import {
	DRAG_OVER_POSITIONS,
	DragOverPosition,
} from '../../config/constants/dragOverPositions';
import {Item} from './Item';
import {KeyboardDragAndDropContextProvider} from './KeyboardDragAndDropContext';

interface ItemListProps {
	items: Item[];
	listId: string;
	updateLists: (listId: string, items: Item[]) => void;
}

export function ItemList({
	items: initialItems,
	listId,
	updateLists,
}: ItemListProps) {
	const [items, setItems] = useState(initialItems);

	const onDropItem = (
		itemId: string,
		nextIndex: number,
		dragOverPosition?: DragOverPosition
	) => {
		const index = items.findIndex(({id}) => id === itemId);
		const item = items[index];
		const nextItems = [...items];

		let updatedNextIndex = nextIndex;

		if (dragOverPosition === DRAG_OVER_POSITIONS.bottom) {
			updatedNextIndex =
				updatedNextIndex < nextItems.length
					? updatedNextIndex + 1
					: updatedNextIndex;
		}

		if (updatedNextIndex > index) {
			updatedNextIndex =
				updatedNextIndex > 0 ? updatedNextIndex - 1 : updatedNextIndex;
		}

		nextItems.splice(index, 1);
		nextItems.splice(updatedNextIndex, 0, item);

		setItems(nextItems);
		updateLists(listId, nextItems);
	};

	return (
		<KeyboardDragAndDropContextProvider itemList={items}>
			{items.map((item, index) => (
				<Item
					index={index}
					item={item}
					key={item.id}
					onDropItem={onDropItem}
				/>
			))}
		</KeyboardDragAndDropContextProvider>
	);
}
