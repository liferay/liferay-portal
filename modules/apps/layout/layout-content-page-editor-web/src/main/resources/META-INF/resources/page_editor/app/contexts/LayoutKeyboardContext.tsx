/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useMemo, useState} from 'react';

import {LayoutData, LayoutDataItem} from '../../types/layout_data/LayoutData';
import {
	LAYOUT_DATA_ITEM_TYPES,
	LayoutDataItemType,
} from '../config/constants/layoutDataItemTypes';
import {ViewportSize} from '../config/constants/viewportSizes';
import {isItemHidden} from '../utils/isItemHidden';
import {isLayoutDataItemDeleted} from '../utils/isLayoutDataItemDeleted';
import {useSelector} from './StoreContext';

type LayoutKeyboardState = {
	itemList: string[];
	setTargetId: React.Dispatch<React.SetStateAction<string | null>>;
	targetId: string | null;
};

const LayoutKeyboardContext = React.createContext<LayoutKeyboardState>({
	itemList: [],
	setTargetId: () => {},
	targetId: null,
});

function LayoutKeyboardContextProvider({
	children,
}: {
	children: React.ReactNode;
}) {
	const layoutData = useSelector((state) => state.layoutData);
	const viewportSize = useSelector((state) => state.selectedViewportSize);

	const itemList = useMemo(() => {
		const list: string[] = [];

		visit(layoutData.rootItems.main, layoutData, list, viewportSize);

		return list;
	}, [layoutData, viewportSize]);

	const [targetId, setTargetId] = useState<string | null>(null);
	const [targetIndex, setTargetIndex] = useState<number | null>(null);

	// Store target index

	useEffect(() => {
		if (targetId) {
			setTargetIndex(itemList.indexOf(targetId));
		}
	}, [itemList, setTargetId, targetId]);

	// When removing or hiding an item, target the next/previous

	useEffect(() => {
		if (targetId && targetIndex && !itemList.includes(targetId)) {
			const nextIndex =
				targetIndex < itemList.length ? targetIndex : targetIndex - 1;

			const nextId = itemList[nextIndex];

			setTargetId(nextId || null);
			setTargetIndex(null);
		}
	}, [itemList, setTargetId, targetId, targetIndex]);

	return (
		<LayoutKeyboardContext.Provider
			value={{itemList, setTargetId, targetId}}
		>
			{children}
		</LayoutKeyboardContext.Provider>
	);
}

function visit(
	itemId: string,
	layoutData: LayoutData,
	list: string[],
	viewportSize: ViewportSize
) {
	const {items} = layoutData;

	const item = items[itemId];

	if (
		isSelectable(item) &&
		!isLayoutDataItemDeleted(layoutData, itemId) &&
		!isItemHidden(layoutData, itemId, viewportSize, {recursive: true})
	) {
		list.push(itemId);
	}

	if (!item.children.length) {
		return;
	}

	for (const childId of item.children) {
		visit(childId, layoutData, list, viewportSize);
	}
}

function isSelectable(item: LayoutDataItem) {
	if (item.type === LAYOUT_DATA_ITEM_TYPES.root && !item.children.length) {
		return true;
	}

	const selectableTypes: LayoutDataItemType[] = [
		LAYOUT_DATA_ITEM_TYPES.column,
		LAYOUT_DATA_ITEM_TYPES.collection,
		LAYOUT_DATA_ITEM_TYPES.container,
		LAYOUT_DATA_ITEM_TYPES.form,
		LAYOUT_DATA_ITEM_TYPES.fragment,
		LAYOUT_DATA_ITEM_TYPES.formStep,
		LAYOUT_DATA_ITEM_TYPES.formStepContainer,
		LAYOUT_DATA_ITEM_TYPES.fragmentDropZone,
		LAYOUT_DATA_ITEM_TYPES.row,
	];

	return selectableTypes.includes(item.type);
}

export {LayoutKeyboardContext, LayoutKeyboardContextProvider};
