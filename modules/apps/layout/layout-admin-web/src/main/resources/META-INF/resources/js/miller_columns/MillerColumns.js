/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {usePrevious} from '@liferay/frontend-js-react-web';
import {DragPreview} from '@liferay/layout-js-components-web';
import {sub} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useRef,
	useState,
} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import MillerColumnsColumn from './MillerColumnsColumn';
import {DROP_POSITIONS} from './constants/dropPositions';
import {
	KeyboardMovementContext,
	KeyboardMovementProvider,
} from './contexts/KeyboardMovementContext';
import {KeyboardNavigationProvider} from './contexts/KeyboardNavigationContext';
import {LayoutColumnsContext} from './contexts/LayoutColumnsContext';

const getItemsMap = (columns, oldItems = new Map()) => {
	const map = new Map();

	let parentId;
	let parentIndex;
	let parentKey;

	columns.forEach((column, columnIndex) => {
		let childrenCount = 0;
		let newParentId;
		let newParentIndex;
		let newParentKey;

		column.forEach((item, itemIndex) => {
			childrenCount++;

			const oldItem = Array.from(oldItems.values()).find(
				(oldItem) => oldItem.id === item.id
			);

			map.set(item.key, {
				...item,
				checked: oldItem ? oldItem.checked : false,
				columnIndex,
				itemIndex,
				parentId,
				parentIndex,
				parentKey,
			});

			if (item.active && item.hasChild) {
				newParentId = item.id;
				newParentIndex = itemIndex;
				newParentKey = item.key;
			}
		});

		if (parentKey) {
			map.set(parentKey, {
				...map.get(parentKey),
				childrenCount,
			});
		}

		parentId = newParentId;
		parentIndex = newParentIndex;
		parentKey = newParentKey;
	});

	return map;
};

const noop = () => {};

const MillerColumns = ({
	createPageTemplateURL,
	getPageTemplateCollectionsURL,
	getItemActionsURL,
	isLayoutSetPrototype,
	namespace,
	onColumnsChange = noop,
	getItemChildren,
	rtl,
	saveData = noop,
	searchContainer,
}) => {
	const ref = useRef();

	const {layoutColumns} = useContext(LayoutColumnsContext);

	const [items, setItems] = useState(() => getItemsMap(layoutColumns));

	// Transform items map into a columns-like array.

	const columns = useMemo(() => {
		const columns = [];

		// eslint-disable-next-line no-for-of-loops/no-for-of-loops
		for (const item of items.values()) {
			if (!columns[item.columnIndex]) {
				columns[item.columnIndex] = {
					items: [],
					parent: Array.from(items.values()).find(
						(_item) => _item.id === item.parentId
					),
				};
			}

			const column = columns[item.columnIndex];

			column.items.push(item);
		}

		// Add empty column in the end if last column has an active item

		const lastColumnActiveItem = columns[columns.length - 1].items.find(
			(item) => item.active
		);
		if (lastColumnActiveItem && !lastColumnActiveItem.hasChild) {
			columns.push({
				items: [],
				parent: lastColumnActiveItem,
			});
		}

		return columns;
	}, [items]);

	const previousColumnsValue = usePrevious(columns);
	const previousLayoutColumnsValue = usePrevious(layoutColumns);

	useEffect(() => {
		if (previousLayoutColumnsValue !== layoutColumns) {
			setItems(getItemsMap(layoutColumns, items));
		}
	}, [layoutColumns, items, previousLayoutColumnsValue]);

	useEffect(() => {
		if (previousColumnsValue !== columns) {
			onColumnsChange(columns);
		}
	}, [columns, onColumnsChange, previousColumnsValue]);

	useEffect(() => {
		if (ref.current) {
			ref.current.scrollLeft = ref.current.scrollWidth;
		}
	}, []);

	useEffect(() => {
		if (searchContainer) {
			searchContainer.on('rowToggled', (event) => {
				setItems((oldItems) => {
					const newItems = new Map(oldItems);

					newItems.forEach((item) => {
						const itemNode = event.elements.allElements._nodes.find(
							(node) => node.value === item.id
						);

						if (itemNode) {
							newItems.set(item.id, {
								...newItems.get(item.id),
								checked: itemNode.checked,
							});
						}
					});

					return newItems;
				});
			});
		}
	}, [searchContainer]);

	const getMovedItems = useCallback(
		(sources, newParentId, targetIndex) => {
			const targetColumnItems = Array.from(items.values()).filter(
				(item) => item.parentId === newParentId
			);

			// Return sources if target column has no items

			if (!targetColumnItems.length) {
				return sources.map((source, index) => ({
					plid: source.id,
					position: targetIndex + index,
				}));
			}

			// Calculate all items of the target column that are changing position

			const movedItems = new Map();

			let previousSources = 0;

			targetColumnItems.forEach((item, index) => {

				// Iterating on target position. Insert sources into movedItems

				if (index === targetIndex) {
					sources.forEach((source, sourceIndex) => {
						movedItems.set(source.id, {
							plid: source.id,
							position: index - previousSources + sourceIndex,
						});
					});

					previousSources = sources.length - previousSources;
				}

				// Iterating on a source. Don't insert it into movedItems but we
				// update previous sources counter

				if (sources.some((source) => source.id === item.id)) {
					previousSources =
						index < targetIndex
							? previousSources + 1
							: previousSources - 1;
				}

				// Insert item that is not a source into movedItems if it has a new
				// position

				else if (previousSources) {
					movedItems.set(item.id, {
						plid: item.id,
						position:
							index < targetIndex
								? index - previousSources
								: index + previousSources,
					});
				}
			});

			// Insert sources into movedItems in the case we are targeting last
			// position of column so the loop does not reach it

			sources.forEach((source, index) => {
				if (!movedItems.has(source.id)) {
					movedItems.set(source.id, {
						plid: source.id,
						position: targetIndex + index,
					});
				}
			});

			return Array.from(movedItems.values());
		},
		[items]
	);

	const onItemDrop = useCallback(
		(sources, target, position = DROP_POSITIONS.bottom) => {
			let targetId;
			let targetIndex;

			if (position === DROP_POSITIONS.middle) {
				targetId = target.id;

				targetIndex = Array.from(items.values()).filter(
					(item) => item.id === target.id
				).length;
			}
			else {
				targetId = target.parentId;

				targetIndex = target.itemIndex;

				if (position === DROP_POSITIONS.bottom) {
					targetIndex = target.itemIndex + 1;
				}
			}

			// Update checked items to keep them selected after updating items
			// with server response

			const newItems = new Map(items);

			sources.forEach((source) => {
				if (source.checked) {
					newItems.set(source.id, source);
				}
			});

			setItems(newItems);

			saveData(
				getMovedItems(sources, targetId, targetIndex),
				targetId,
				sources[0].url
			);
		},
		[getMovedItems, items, saveData]
	);

	const columnSizes = layoutColumns
		.filter((col) => col.length)
		.map((col) => col.length);

	const onKeyDown = (event) => {
		const activeElement = document.activeElement;
		const resizes = document.querySelectorAll('[id*="resize"]');

		// Modify the semantic order of the page by placing the resizers last

		if (
			event.key === 'Tab' &&
			event.shiftKey &&
			activeElement.id === 'resize-0'
		) {
			event.preventDefault();

			[...resizes].forEach((resize) => {
				resize.tabIndex = -1;
			});

			document
				.querySelector(
					'.miller-columns-item-mask[role="menuitem"][tabindex="0"]'
				)
				.parentElement.querySelector('.btn-options')
				?.focus();
		}

		if (
			event.key === 'Tab' &&
			!event.shiftKey &&
			activeElement.classList.contains('btn-options')
		) {
			event.preventDefault();

			[...resizes].forEach((resize) => {
				resize.tabIndex = 1;
			});
			document.getElementById('resize-0')?.focus();
		}
	};

	return (
		<KeyboardNavigationProvider columnSizes={columnSizes}>
			<KeyboardMovementProvider
				columnSizes={columnSizes}
				items={items}
				onMove={onItemDrop}
				rtl={rtl}
			>
				<DndProvider backend={HTML5Backend}>
					<div
						className="bg-white miller-columns-row"
						onKeyDown={onKeyDown}
						ref={ref}
						role="application"
					>
						{columns.map((column, index) => (
							<MillerColumnsColumn
								columnItems={column.items}
								columnsContainer={ref}
								createPageTemplateURL={createPageTemplateURL}
								getItemActionsURL={getItemActionsURL}
								getItemChildren={getItemChildren}
								getPageTemplateCollectionsURL={
									getPageTemplateCollectionsURL
								}
								index={index}
								isLayoutSetPrototype={isLayoutSetPrototype}
								items={items}
								key={index}
								namespace={namespace}
								onItemDrop={onItemDrop}
								rtl={rtl}
							/>
						))}

						<DragPreviewWrapper />
					</div>
				</DndProvider>
			</KeyboardMovementProvider>
		</KeyboardNavigationProvider>
	);
};

function DragPreviewWrapper() {
	const {sources, target} = useContext(KeyboardMovementContext);

	const getDragPreviewLabel = (item) => {
		const items = sources.length ? sources : item?.items;

		if (items) {
			if (items.length > 1) {
				return sub(Liferay.Language.get('x-elements'), items.length);
			}
			else {
				const [item] = items;

				return item.title;
			}
		}
	};

	const alignment = useMemo(() => {
		if (!target) {
			return null;
		}

		const {columnIndex, itemIndex, position} = target;

		const column = document.querySelectorAll('.miller-columns-col')[
			columnIndex
		];

		const item = column.querySelectorAll('.miller-columns-item')[itemIndex];

		return {
			element: item,
			position,
			scrollElement: document.querySelector('.miller-columns-row'),
		};
	}, [target]);

	return (
		<DragPreview
			alignment={alignment}
			focusElement={!!sources.length}
			getLabel={getDragPreviewLabel}
		/>
	);
}

export default MillerColumns;
