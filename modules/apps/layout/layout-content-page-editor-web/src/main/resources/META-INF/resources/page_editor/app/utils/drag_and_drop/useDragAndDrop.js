/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {throttle} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useReducer,
	useRef,
	useState,
} from 'react';
import {useDrag, useDrop} from 'react-dnd';
import {getEmptyImage} from 'react-dnd-html5-backend';

import {LAYOUT_DATA_ITEM_TYPES} from '../../config/constants/layoutDataItemTypes';
import {config} from '../../config/index';
import {useIsDisabledCollectionItem} from '../../contexts/CollectionItemContext';
import {useActiveItemIds, useSelectItem} from '../../contexts/ControlsContext';
import {useSelectorRef} from '../../contexts/StoreContext';
import {useGetWidgets} from '../../contexts/WidgetsContext';
import {isMultistepForm} from '../../utils/isMultistepForm';
import {openFormConversionModal} from '../../utils/openFormConversionModal';
import {getFormParent} from '../getFormParent';
import {isMovementValid} from '../isMovementValid';
import isStepper from '../isStepper';
import toMovementItem from '../toMovementItem';
import {DRAG_DROP_TARGET_TYPE} from './constants/dragDropTargetType';
import defaultComputeHover from './defaultComputeHover';
import getDropData from './getDropData';

export const initialDragDrop = {
	canDrag: true,

	dispatch: null,

	fragmentEntryLinksRef: {
		current: {},
	},

	layoutDataRef: {
		current: {
			items: [],
		},
	},

	setCanDrag: () => {},

	state: {

		/**
		 * Item that is being dragged
		 */
		dragSource: null,

		/**
		 * Target item where the item is being dragged true.
		 * If elevate is true, dropTarget is the sibling
		 * of drop item, otherwise is it's parent.
		 */
		dropTarget: null,

		/**
		 * If true, dropTarget is the sibling of dragSource
		 * and targetPosition determines the item index.
		 */
		elevate: false,

		/**
		 * Vertical position relative to dropTarget
		 * (bottom, middle, top)
		 */
		targetPositionWithMiddle: null,

		/**
		 * Vertical position relative to dropTarget
		 * (bottom, top)
		 */
		targetPositionWithoutMiddle: null,

		/**
		 * Source of the Drag and Drop status
		 * (where the drag and drop status have been generated)
		 */
		type: DRAG_DROP_TARGET_TYPE.INITIAL,
	},

	targetRefs: new Map(),
};

const DragAndDropContext = React.createContext(initialDragDrop);

export function useDropTargetData() {
	const {dropTarget, targetPositionWithMiddle} =
		useContext(DragAndDropContext).state;

	return {
		item: dropTarget,
		position: targetPositionWithMiddle,
	};
}

export function useSetCanDrag() {
	return useContext(DragAndDropContext).setCanDrag;
}

export function NotDraggableArea({children}) {
	return (
		<div
			draggable
			onDragStart={(event) => {
				event.preventDefault();
				event.stopPropagation();
			}}
		>
			{children}
		</div>
	);
}

export function useDragItem(source, onDragEnd, onBegin = () => {}) {
	const {canDrag, dispatch, fragmentEntryLinksRef, layoutDataRef, state} =
		useContext(DragAndDropContext);

	const activeItemIds = useActiveItemIds();

	const sourceRef = useRef(null);

	const getWidgets = useGetWidgets();

	const item = {
		...source,
		id: source.itemId,
		namespace: config.portletNamespace,
	};

	if (!item.origin) {
		delete item.origin;
	}

	// Calculate sources array, take items from activeItemIds if it's multiSelection

	let sources = [item];

	if (activeItemIds.length > 1) {
		sources = [
			...activeItemIds
				.filter((id) => layoutDataRef.current.items[id])
				.map((id) =>
					toMovementItem(
						id,
						layoutDataRef.current,
						fragmentEntryLinksRef.current
					)
				),
		];
	}

	const [{isDraggingSource}, handlerRef, previewRef] = useDrag({
		begin() {
			onBegin();
		},

		canDrag,

		collect: (monitor) => ({
			isDraggingSource: monitor.isDragging(),
		}),

		end() {
			computeDrop({
				dispatch,
				fragmentEntryLinksRef,
				getWidgets,
				layoutDataRef,
				onDragEnd,
				sources,
				state,
			});
		},

		item,
	});

	useEffect(() => {
		previewRef(getEmptyImage(), {captureDraggingState: true});
	}, [previewRef]);

	return {
		handlerRef,
		isDraggingSource,
		sourceRef,
	};
}

export function useDragSymbol(
	{fieldTypes, fragmentEntryType, icon, isWidget, label, portletId, type},
	onDragEnd
) {
	const selectItem = useSelectItem();

	const {handlerRef, isDraggingSource, sourceRef} = useDragItem(
		{
			fieldTypes,
			fragmentEntryType,
			icon,
			isSymbol: true,
			isWidget,
			itemId: label,
			name: label,
			portletId,
			type,
		},
		onDragEnd,
		() => selectItem(null)
	);

	const symbolRef = (element) => {
		sourceRef.current = element;
		handlerRef(element);
	};

	return {
		isDraggingSource,
		sourceRef: symbolRef,
	};
}

export function useDropClear() {
	const {dispatch} = useContext(DragAndDropContext);

	const [, dropClearRef] = useDrop({
		accept: Object.values(LAYOUT_DATA_ITEM_TYPES),
		hover() {
			dispatch(initialDragDrop.state);
		},
	});

	return dropClearRef;
}

export function useDropTarget(targetItem, computeHover = defaultComputeHover) {
	const isDisabledCollectionItem = useIsDisabledCollectionItem();

	const {dispatch, layoutDataRef, state, targetRefs} =
		useContext(DragAndDropContext);

	const targetRef = useRef(null);

	const isOverTarget =
		state.dropTarget &&
		targetItem &&
		state.dropTarget.itemId === targetItem.itemId;

	const coordsRef = useRef({x: 0, y: 0});

	const [, setDropTargetRef] = useDrop({
		accept: Object.values(LAYOUT_DATA_ITEM_TYPES),
		hover(source, monitor) {
			if (source.origin !== targetItem.origin) {
				return;
			}

			const monitorCoords = monitor.getSourceClientOffset();

			if (
				coordsRef.current.x === monitorCoords.x &&
				coordsRef.current.y === monitorCoords.y
			) {
				return;
			}

			coordsRef.current = monitorCoords;

			computeHover({
				dispatch,
				layoutDataRef,
				monitor,
				sourceItem: source,
				state,
				targetItem,
				targetRefs,
			});
		},
	});

	useEffect(() => {
		const itemId = targetItem.itemId;

		if (isDisabledCollectionItem) {
			return;
		}

		targetRefs.set(itemId, targetRef);

		return () => {
			targetRefs.delete(itemId);
		};
	}, [
		isDisabledCollectionItem,
		layoutDataRef,
		targetItem,
		targetRef,
		targetRefs,
	]);

	const setTargetRef = useCallback(
		(element) => {
			setDropTargetRef(element);

			targetRef.current = element;
		},
		[setDropTargetRef]
	);

	return {
		isOverTarget,
		sourceItem: state.dragSource,
		targetItemId: state.dropTarget?.itemId,
		targetPosition: state.targetPositionWithMiddle,
		targetRef: setTargetRef,
	};
}

export function DragAndDropContextProvider({children}) {
	const [canDrag, setCanDrag] = useState(true);

	const [state, reducerDispatch] = useReducer(
		(state, nextState) =>
			Object.keys(state).some((key) => state[key] !== nextState[key])
				? nextState
				: state,
		initialDragDrop.state
	);

	const targetRefs = useMemo(() => new Map(), []);

	const dispatch = useMemo(() => {
		return throttle(reducerDispatch, 100);
	}, [reducerDispatch]);

	const fragmentEntryLinksRef = useSelectorRef(
		(state) => state.fragmentEntryLinks
	);

	const layoutDataRef = useSelectorRef((state) => state.layoutData);

	const dragAndDropContext = useMemo(
		() => ({
			canDrag,
			dispatch,
			fragmentEntryLinksRef,
			layoutDataRef,
			setCanDrag,
			state,
			targetRefs,
		}),
		[
			canDrag,
			dispatch,
			fragmentEntryLinksRef,
			layoutDataRef,
			state,
			targetRefs,
			setCanDrag,
		]
	);

	return (
		<DragAndDropContext.Provider value={dragAndDropContext}>
			{children}
		</DragAndDropContext.Provider>
	);
}

function computeDrop({
	dispatch,
	fragmentEntryLinksRef,
	getWidgets,
	layoutDataRef,
	onDragEnd,
	sources,
	state,
}) {
	const {dragSource, dropTarget, targetPositionWithoutMiddle} = state;

	if (!dragSource) {
		return;
	}

	// Get the definitive target id where the drag source item drops

	const {position, targetId} = getDropData({
		isElevation: state.elevate,
		layoutDataRef,
		sourceItemId: dragSource.itemId,
		targetItemId: dropTarget.itemId,
		targetPosition: targetPositionWithoutMiddle,
	});

	if (
		!isMovementValid({
			fragmentEntryLinks: fragmentEntryLinksRef.current,
			getWidgets,
			layoutData: layoutDataRef.current,
			onInvalid: () => dispatch(initialDragDrop.state),
			sources,
			targetId,
			type: 'drop',
		})
	) {
		return;
	}

	if (dragSource && dropTarget) {
		if (isFormConversion(layoutDataRef.current, sources, targetId)) {
			openFormConversionModal({
				onContinue: async () => onDragEnd(targetId, position),
			});
		}
		else {
			onDragEnd(targetId, position);
		}
	}

	dispatch(initialDragDrop.state);
}

function isFormConversion(layoutData, sources, targetId) {
	const targetItem = layoutData.items[targetId];
	const formParent = getFormParent(targetItem, layoutData);

	if (
		formParent &&
		sources.some((item) => isStepper(item)) &&
		!isMultistepForm(formParent)
	) {
		return true;
	}

	return false;
}
