/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {
	useCallback,
	useContext,
	useEffect,
	useReducer,
	useRef,
	useState,
} from 'react';

import {LayoutData} from '../../types/layout_data/LayoutData';
import {ItemActivationOrigin} from '../config/constants/itemActivationOrigins';
import {ITEM_TYPES, ItemType} from '../config/constants/itemTypes';
import {LAYOUT_DATA_ITEM_TYPES} from '../config/constants/layoutDataItemTypes';
import {
	MULTI_SELECT_TYPES,
	MultiSelectType,
} from '../config/constants/multiSelectTypes';
import {useSelectorRef} from './StoreContext';

type RangeLimitIds = {end?: string | null; start?: string};

type ControlsState = {
	activationOrigin?: ItemActivationOrigin | null;
	activeItemIds?: string[];
	activeItemType?: ItemType | null;
	highlightedItems?: string[];
	hoveredItemId?: string | null;
	hoveredItemType?: ItemType | null;
	rangeLimitIds?: RangeLimitIds;
};

type ControlsAction = {
	activeItemIds?: string[];
	itemId?: string | null;
	itemIds?: string[];
	itemType?: ItemType | null;
	layoutData?: LayoutData | null;
	multiSelect?: MultiSelectType | null;
	origin?: ItemActivationOrigin | null;
	parentId?: string | null;
	type:
		| typeof HIGHLIGHT_ITEMS
		| typeof HOVER_ITEM
		| typeof MULTI_SELECT
		| typeof SELECT_ITEM;
};

const ACTIVE_INITIAL_STATE: ControlsState = {
	activationOrigin: null,
	activeItemIds: [],
	activeItemType: null,
	rangeLimitIds: {},
};

const HIGHLIGHT_INITIAL_STATE: ControlsState = {
	highlightedItems: [],
};

const HOVER_INITIAL_STATE: ControlsState = {
	hoveredItemId: null,
};

const HIGHLIGHT_ITEMS = 'HIGHLIGHT_ITEM';
const HOVER_ITEM = 'HOVER_ITEM';
const MULTI_SELECT = 'MULTI_SELECT';
const SELECT_ITEM = 'SELECT_ITEM';

const ActiveStateContext = React.createContext(ACTIVE_INITIAL_STATE);
const ActiveDispatchContext = React.createContext<
	React.Dispatch<ControlsAction>
>(() => {});

const HighlightDispatchContext = React.createContext<
	React.Dispatch<ControlsAction>
>(() => {});
const HighlightStateContext = React.createContext(HIGHLIGHT_INITIAL_STATE);

const HoverStateContext = React.createContext(HOVER_INITIAL_STATE);
const HoverDispatchContext = React.createContext<
	React.Dispatch<ControlsAction>
>(() => {});

const MultiSelectStateContext = React.createContext<MultiSelectType | null>(
	null
);

const MultiSelectStateRefContext = React.createContext<
	React.MutableRefObject<MultiSelectType | null>
>({current: null});

const MultiSelectDispatchContext = React.createContext<
	(multiSelect?: MultiSelectType | null) => void
>(() => {});

/**
 * This method includes a new item in the active items. If this item is already
 * belongs to the active items, it is removed.
 */

function getActiveItemIds(activeItemIds: string[], itemId: string) {
	return activeItemIds.includes(itemId)
		? activeItemIds.filter((activeItemId) => activeItemId !== itemId)
		: [...activeItemIds, itemId];
}

/**
 * This method gets all elements within a selection range
 *
 * First it looks for the item at the start of the range and enable a flag to mark
 * all the elements iterated as included until the end of the range is found.
 */

export function getItemsWithinRange({
	itemIds,
	layoutDataItems,
	rangeLimitIds,
}: {
	itemIds: string[];
	layoutDataItems: LayoutData['items'];
	rangeLimitIds: RangeLimitIds;
}) {
	let activateSelection = false;
	const selectedItems: string[] = [];

	const findItemsWithinRange = ({
		itemIds,
		layoutDataItems,
		rangeLimitIds,
	}: {
		itemIds: string[];
		layoutDataItems: LayoutData['items'];
		rangeLimitIds: RangeLimitIds;
	}) => {
		for (const childId of itemIds) {
			const item = layoutDataItems[childId];

			const isLimitId =
				rangeLimitIds.start === childId ||
				rangeLimitIds.end === childId;

			if (isLimitId) {
				activateSelection = !activateSelection;
			}

			if (
				(isLimitId || activateSelection) &&
				item.type !== LAYOUT_DATA_ITEM_TYPES.formStep &&
				item.type !== LAYOUT_DATA_ITEM_TYPES.column &&
				item.type !== LAYOUT_DATA_ITEM_TYPES.collectionItem &&
				item.type !== LAYOUT_DATA_ITEM_TYPES.fragmentDropZone
			) {
				selectedItems.push(childId);
			}

			findItemsWithinRange({
				itemIds: item.children,
				layoutDataItems,
				rangeLimitIds,
			});
		}
	};

	findItemsWithinRange({
		itemIds,
		layoutDataItems,
		rangeLimitIds,
	});

	return selectedItems;
}

const reducer = (
	state: ControlsState,
	action: ControlsAction
): ControlsState => {
	const {
		activeItemIds,
		itemId,
		itemIds,
		itemType,
		layoutData,
		multiSelect,
		origin,
		parentId,
		type,
	} = action;

	let nextState = state;

	if (type === HIGHLIGHT_ITEMS) {
		nextState = {highlightedItems: itemIds};
	}
	else if (type === HOVER_ITEM && itemId !== nextState.hoveredItemId) {
		nextState = {
			...nextState,
			activationOrigin: origin,
			hoveredItemId: itemId,
			hoveredItemType: itemType,
		};
	}
	else if (type === SELECT_ITEM) {
		const currentActiveItemIds = state.activeItemIds ?? [];

		let rangeLimitIds: RangeLimitIds = {};
		let nextActiveItemIds = itemId ? [itemId] : [];
		let nextItemType = itemType;

		if (state.activeItemType === ITEM_TYPES.editable) {
			nextActiveItemIds = itemId ? [itemId] : [];
		}
		else if (!itemId) {
			nextActiveItemIds = [];
		}
		else if (multiSelect === MULTI_SELECT_TYPES.simple) {
			if (itemType === ITEM_TYPES.editable) {
				if (!parentId || currentActiveItemIds.includes(parentId)) {
					return state;
				}

				nextActiveItemIds = getActiveItemIds(
					currentActiveItemIds,
					parentId
				);

				nextItemType = ITEM_TYPES.layoutDataItem;
			}
			else {
				nextActiveItemIds = getActiveItemIds(
					currentActiveItemIds,
					itemId
				);
			}
		}
		else if (multiSelect === MULTI_SELECT_TYPES.range) {
			let initialActiveItemIds = currentActiveItemIds;

			// The last active item id is taken when the first item in the
			// range is selected.

			let startLimitId = [...currentActiveItemIds].pop();

			if (
				itemType === ITEM_TYPES.editable &&
				currentActiveItemIds.length
			) {
				nextItemType = ITEM_TYPES.layoutDataItem;
			}

			const currentRangeLimitIds = state.rangeLimitIds ?? {};

			if (currentRangeLimitIds.end) {

				// If a range selection has just been made, and another range
				// selection is made immediately after, the first item id of
				// the range is kept and the activeItemIds from the last range
				// selection are removed.

				startLimitId = currentRangeLimitIds.start || startLimitId;

				initialActiveItemIds = currentActiveItemIds.slice(
					0,
					Math.min(
						startLimitId === undefined
							? -1
							: currentActiveItemIds.indexOf(startLimitId),
						currentActiveItemIds.indexOf(currentRangeLimitIds.end)
					)
				);
			}

			rangeLimitIds = {end: parentId || itemId, start: startLimitId};

			if (!currentActiveItemIds.length) {
				nextActiveItemIds = [itemId];
			}
			else if (
				!rangeLimitIds.start ||
				rangeLimitIds.end === rangeLimitIds.start
			) {

				// If the start and end of the range are the same id, only
				// this item is selected

				nextActiveItemIds = [parentId || itemId];
			}
			else if (layoutData) {
				const root = layoutData.items[layoutData.rootItems.main];

				nextActiveItemIds = getItemsWithinRange({
					itemIds: root.children,
					layoutDataItems: layoutData.items,
					rangeLimitIds,
				});

				nextActiveItemIds = [
					...new Set([...initialActiveItemIds, ...nextActiveItemIds]),
				];
			}
		}

		nextState = {
			...nextState,
			activationOrigin: origin,
			activeItemIds: nextActiveItemIds,
			activeItemType: nextItemType,
			rangeLimitIds,
		};
	}
	else if (type === MULTI_SELECT) {
		nextState = {
			...state,
			activeItemIds: activeItemIds || state.activeItemIds,
		};
	}

	return nextState;
};

const ActiveProvider = ({
	children,
	initialState,
}: {
	children: React.ReactNode;
	initialState: ControlsState;
}) => {
	const [state, dispatch] = useReducer(reducer, initialState);

	return (
		<ActiveDispatchContext.Provider value={dispatch}>
			<ActiveStateContext.Provider value={state}>
				{children}
			</ActiveStateContext.Provider>
		</ActiveDispatchContext.Provider>
	);
};

const HighlightProvider = ({
	children,
	initialState,
}: {
	children: React.ReactNode;
	initialState: ControlsState;
}) => {
	const [state, dispatch] = useReducer(reducer, initialState);

	return (
		<HighlightDispatchContext.Provider value={dispatch}>
			<HighlightStateContext.Provider value={state}>
				{children}
			</HighlightStateContext.Provider>
		</HighlightDispatchContext.Provider>
	);
};

const HoverProvider = ({
	children,
	initialState,
}: {
	children: React.ReactNode;
	initialState: ControlsState;
}) => {
	const [state, dispatch] = useReducer(reducer, initialState);

	return (
		<HoverDispatchContext.Provider value={dispatch}>
			<HoverStateContext.Provider value={state}>
				{children}
			</HoverStateContext.Provider>
		</HoverDispatchContext.Provider>
	);
};

const MultiSelectProvider = ({children}: {children: React.ReactNode}) => {
	const [multiSelectType, setMultiSelectType] =
		useState<MultiSelectType | null>(null);
	const multiSelectionTypeRef = useRef(multiSelectType);

	useEffect(() => {
		multiSelectionTypeRef.current = multiSelectType;
	}, [multiSelectType]);

	const activateMultiSelect = useCallback(
		(multiSelect: MultiSelectType | null = null) => {
			if (!multiSelect) {
				multiSelectionTypeRef.current = null;
			}

			setMultiSelectType(multiSelect);
		},
		[]
	);

	return (
		<MultiSelectDispatchContext.Provider value={activateMultiSelect}>
			<MultiSelectStateRefContext.Provider value={multiSelectionTypeRef}>
				<MultiSelectStateContext.Provider value={multiSelectType}>
					{children}
				</MultiSelectStateContext.Provider>
			</MultiSelectStateRefContext.Provider>
		</MultiSelectDispatchContext.Provider>
	);
};

const ControlsProvider = ({
	activeInitialState = ACTIVE_INITIAL_STATE,
	highlightInitialState = HIGHLIGHT_INITIAL_STATE,
	hoverInitialState = HOVER_INITIAL_STATE,
	children,
}: {
	activeInitialState?: ControlsState;
	children: React.ReactNode;
	highlightInitialState?: ControlsState;
	hoverInitialState?: ControlsState;
}) => {
	return (
		<ActiveProvider initialState={activeInitialState}>
			<HoverProvider initialState={hoverInitialState}>
				<HighlightProvider initialState={highlightInitialState}>
					<MultiSelectProvider>{children}</MultiSelectProvider>
				</HighlightProvider>
			</HoverProvider>
		</ActiveProvider>
	);
};

const useActivationOrigin = () =>
	useContext(ActiveStateContext).activationOrigin ?? null;

const useActiveItemIds = () =>
	useContext(ActiveStateContext).activeItemIds ?? [];

const useActiveItemType = () =>
	useContext(ActiveStateContext).activeItemType ?? null;

const useHighlightedItemIds = () =>
	useContext(HighlightStateContext).highlightedItems ?? [];

const useHighlightItems = () => {
	const dispatch = useContext(HighlightDispatchContext);

	return useCallback(
		(itemIds: string[]) =>
			dispatch({
				itemIds,
				type: HIGHLIGHT_ITEMS,
			}),
		[dispatch]
	);
};

const useHoveredItemId = () =>
	useContext(HoverStateContext).hoveredItemId ?? null;

const useHoveredItemType = () =>
	useContext(HoverStateContext).hoveredItemType ?? null;

const useHoveringOrigin = () =>
	useContext(HoverStateContext).activationOrigin ?? null;

const useHoverItem = () => {
	const dispatch = useContext(HoverDispatchContext);

	return useCallback(
		(
			itemId: string | null,
			{
				itemType = ITEM_TYPES.layoutDataItem,
				origin = null,
			}: {
				itemType?: ItemType | null;
				origin?: ItemActivationOrigin | null;
			} = {
				itemType: ITEM_TYPES.layoutDataItem,
			}
		) =>
			dispatch({
				itemId,
				itemType,
				origin,
				type: HOVER_ITEM,
			}),
		[dispatch]
	);
};

const useIsActive = () => {
	const {activeItemIds} = useContext(ActiveStateContext);

	return useCallback(
		(itemId: string) => (activeItemIds ?? []).includes(itemId),
		[activeItemIds]
	);
};

const useIsHovered = () => {
	const hoveredItemId = useContext(HoverStateContext).hoveredItemId ?? null;

	return useCallback(
		(itemId: string) => hoveredItemId === itemId,
		[hoveredItemId]
	);
};

const useSelectItem = () => {
	const activeDispatch = useContext(ActiveDispatchContext);
	const highlightDispatch = useContext(HighlightDispatchContext);
	const {highlightedItems: highlightedItemIds} = useContext(
		HighlightStateContext
	);
	const layoutDataRef = useSelectorRef((state) => state.layoutData);
	const multiSelectTypeRef = useContext(MultiSelectStateRefContext);

	return useCallback(
		(
			itemId: string | null,
			{
				parentId = null,
				itemType = ITEM_TYPES.layoutDataItem,
				origin = null,
			}: {
				itemType?: ItemType | null;
				origin?: ItemActivationOrigin | null;
				parentId?: string | null;
			} = {
				itemType: ITEM_TYPES.layoutDataItem,
			}
		) => {
			activeDispatch({
				itemId,
				itemType,
				layoutData: layoutDataRef.current,
				multiSelect: multiSelectTypeRef.current,
				origin,
				parentId,
				type: SELECT_ITEM,
			});

			if (highlightedItemIds?.length) {
				highlightDispatch({
					itemIds: [],
					type: HIGHLIGHT_ITEMS,
				});
			}
		},
		[
			activeDispatch,
			highlightDispatch,
			highlightedItemIds,
			layoutDataRef,
			multiSelectTypeRef,
		]
	);
};

const useActivateMultiSelect = () => useContext(MultiSelectDispatchContext);

const useSelectMultipleItems = () => {
	const activeDispatch = useContext(ActiveDispatchContext);

	return useCallback(
		(
			itemIds: string[] | null,
			{origin = null}: {origin?: ItemActivationOrigin | null} = {}
		) => {
			activeDispatch({
				activeItemIds: itemIds || [],
				origin,
				type: MULTI_SELECT,
			});
		},
		[activeDispatch]
	);
};

const useMultiSelectType = () => useContext(MultiSelectStateContext);

const useMultiSelectTypeRef = () => useContext(MultiSelectStateRefContext);

export {
	ControlsProvider,
	reducer,
	useActivateMultiSelect,
	useActivationOrigin,
	useActiveItemIds,
	useActiveItemType,
	useHighlightedItemIds,
	useHighlightItems,
	useHoveredItemId,
	useHoveredItemType,
	useHoveringOrigin,
	useHoverItem,
	useIsActive,
	useIsHovered,
	useMultiSelectType,
	useMultiSelectTypeRef,
	useSelectItem,
	useSelectMultipleItems,
};
