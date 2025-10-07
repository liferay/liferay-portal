/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {TreeView as ClayTreeView} from '@clayui/core';
import {useEventListener} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {ITEM_ACTIVATION_ORIGINS} from '../../../../../app/config/constants/itemActivationOrigins';
import {ITEM_TYPES} from '../../../../../app/config/constants/itemTypes';
import {
	ARROW_DOWN_KEY_CODE,
	ARROW_LEFT_KEY_CODE,
	ARROW_RIGHT_KEY_CODE,
	ARROW_UP_KEY_CODE,
	ENTER_KEY_CODE,
	SPACE_KEY_CODE,
} from '../../../../../app/config/constants/keyboardCodes';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../app/config/constants/layoutDataItemTypes';
import {LAYOUT_TYPES} from '../../../../../app/config/constants/layoutTypes';
import {MULTI_SELECT_TYPES} from '../../../../../app/config/constants/multiSelectTypes';
import {config} from '../../../../../app/config/index';
import {
	useActiveItemIds,
	useHoverItem,
	useHoveredItemId,
	useMultiSelectType,
	useSelectItem,
} from '../../../../../app/contexts/ControlsContext';
import {useEditedNodeId} from '../../../../../app/contexts/ShortcutContext';
import {
	useDispatch,
	useSelector,
	useSelectorRef,
} from '../../../../../app/contexts/StoreContext';
import selectCanUpdateEditables from '../../../../../app/selectors/selectCanUpdateEditables';
import selectCanUpdateItemConfiguration from '../../../../../app/selectors/selectCanUpdateItemConfiguration';
import selectCanUpdatePageStructure from '../../../../../app/selectors/selectCanUpdatePageStructure';
import {DragAndDropContextProvider} from '../../../../../app/utils/drag_and_drop/useDragAndDrop';
import usePageContents from '../../../../../app/utils/usePageContents';
import StructureTreeNode from './StructureTreeNode';
import StructureTreeNodeActions from './StructureTreeNodeActions';
import VisibilityButton from './VisibilityButton';
import getTreeNodes from './getTreeNodes';

export default function StructureTreeContent({expandedKeys, setExpandedKeys}) {
	const activeItemIds = useActiveItemIds();
	const canUpdateEditables = useSelector(selectCanUpdateEditables);
	const canUpdateItemConfiguration = useSelector(
		selectCanUpdateItemConfiguration
	);
	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const layoutData = useSelector((state) => state.layoutData);
	const multiSelectType = useMultiSelectType();
	const pageContents = usePageContents();
	const hoverItem = useHoverItem();
	const hoveredItemId = useHoveredItemId();
	const selectItem = useSelectItem();
	const treeRef = useRef(null);

	const mappingFields = useSelector((state) => state.mappingFields);
	const masterLayoutData = useSelector(
		(state) => state.masterLayout?.masterLayoutData
	);

	const restrictedItemIds = useSelector((state) => state.restrictedItemIds);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const layoutDataRef = useSelectorRef((store) => store.layoutData);

	const [dragAndDropHoveredItemId, setDragAndDropHoveredItemId] =
		useState(null);

	const isMasterPage = config.layoutType === LAYOUT_TYPES.master;

	const data = masterLayoutData || layoutData;

	const onHoverNode = useCallback((itemId) => {
		setDragAndDropHoveredItemId(itemId);
	}, []);

	const rootItem = data.items[data.rootItems.main];

	if (!rootItem) {
		if (process.env.NODE_ENV === 'development') {
			console.error(
				`Root item with id ${data.rootItems.main} not found in layout data.`
			);
		}
	}

	const initNodes = useMemo(() => {
		return getTreeNodes(rootItem, data.items, {
			canUpdateEditables,
			canUpdateItemConfiguration,
			fragmentEntryLinks,
			isMasterPage,
			layoutData,
			layoutDataRef,
			mappingFields,
			masterLayoutData,
			onHoverNode,
			pageContents,
			restrictedItemIds,
			selectedViewportSize,
		}).children;
	}, [
		rootItem,
		canUpdateEditables,
		canUpdateItemConfiguration,
		data.items,
		fragmentEntryLinks,
		isMasterPage,
		layoutData,
		layoutDataRef,
		mappingFields,
		masterLayoutData,
		pageContents,
		restrictedItemIds,
		onHoverNode,
		selectedViewportSize,
	]);

	const updateNodes = useCallback(({activeItemIds, hoveredItemId, nodes}) => {
		return nodes.map((item) => ({
			...item,
			active: activeItemIds.includes(item.id),
			children: item.children
				? updateNodes({
						activeItemIds,
						hoveredItemId,
						nodes: item.children,
					})
				: [],
			hovered: item.id === hoveredItemId,
		}));
	}, []);

	const nodes = useMemo(
		() =>
			updateNodes({
				activeItemIds,
				hoveredItemId,
				nodes: initNodes,
			}),
		[activeItemIds, updateNodes, hoveredItemId, initNodes]
	);

	const handleButtonsKeyDown = (event) => {
		if (
			[
				ARROW_DOWN_KEY_CODE,
				ARROW_LEFT_KEY_CODE,
				ARROW_RIGHT_KEY_CODE,
				ARROW_UP_KEY_CODE,
			].includes(event.nativeEvent.code)
		) {
			document.activeElement
				.closest('.page-editor__page-structure__clay-tree-node')
				?.focus();
		}
		else {
			event.stopPropagation();
		}
	};

	const ItemActions = ({item}) => {
		const activeItemIds = useActiveItemIds();
		const editedNodeId = useEditedNodeId();
		const dispatch = useDispatch();
		const hoveredItemId = useHoveredItemId();
		const isMultiSelect = activeItemIds.length > 1;
		const isSelected = activeItemIds.includes(item.id);
		const isHovered = item.id === hoveredItemId;
		const canUpdatePageStructure = useSelector(
			selectCanUpdatePageStructure
		);
		const showOptions =
			canUpdatePageStructure &&
			item.itemType !== ITEM_TYPES.editable &&
			item.type !== LAYOUT_DATA_ITEM_TYPES.dropZone &&
			item.type !== LAYOUT_DATA_ITEM_TYPES.formStepContainer &&
			item.activable &&
			!item.isMasterItem;

		if (editedNodeId) {
			return null;
		}

		return (
			<div
				className={classNames('autofit-row w-auto', {
					'page-editor__page-structure__tree-node__buttons--hidden':
						item.hidden || item.hiddenAncestor,
				})}
				onFocus={(event) => event.stopPropagation()}
				onKeyDown={handleButtonsKeyDown}
			>
				{(item.hideable || item.hidden) && (
					<VisibilityButton
						className="ml-0"
						disabled={
							item.isMasterItem ||
							item.hiddenAncestor ||
							isMultiSelect
						}
						dispatch={dispatch}
						node={item}
						visible={item.hidden || isHovered || isSelected}
					/>
				)}

				{showOptions && (
					<StructureTreeNodeActions
						disabled={isMultiSelect}
						item={item}
						visible={item.hidden || isHovered || isSelected}
					/>
				)}
			</div>
		);
	};

	useEffect(() => {
		if (dragAndDropHoveredItemId) {
			setExpandedKeys((previousExpandedKeys) => [
				...new Set([
					...previousExpandedKeys,
					...[dragAndDropHoveredItemId],
				]),
			]);
		}
	}, [dragAndDropHoveredItemId, setExpandedKeys]);

	const onKeyDown = (event, item) => {
		const {code} = event.nativeEvent;

		if (![ENTER_KEY_CODE, SPACE_KEY_CODE].includes(code)) {
			return;
		}

		if (item.activable) {
			selectItem(item.id, {
				itemType: item.itemType,
				origin: ITEM_ACTIVATION_ORIGINS.sidebar,
			});

			hoverItem(null, {
				origin: ITEM_ACTIVATION_ORIGINS.sidebar,
			});
		}
	};

	// Each time an item is focused on, if range multiselect is enabled,
	// the item is selected.

	useEventListener(
		'focusin',
		(event) => {
			if (multiSelectType === MULTI_SELECT_TYPES.range) {
				const itemId = event.target.querySelector(
					'.page-editor__page-structure__tree-node__mask'
				).dataset.itemId;

				const item = layoutData.items[itemId];

				if (item) {
					selectItem(itemId, {
						origin: ITEM_ACTIVATION_ORIGINS.sidebar,
					});
				}
			}
		},
		false,
		treeRef.current
	);

	return (
		<div
			className="overflow-auto page-editor__page-structure__structure-tree pt-4"
			ref={treeRef}
		>
			{!nodes.length && (
				<ClayAlert
					aria-live="polite"
					displayType="info"
					title={Liferay.Language.get('info')}
				>
					{Liferay.Language.get('there-is-no-content-on-this-page')}
				</ClayAlert>
			)}

			<DragAndDropContextProvider>
				<ClayTreeView
					displayType="light"
					expandDoubleClick={false}
					expandedKeys={new Set(expandedKeys)}
					items={nodes}
					onExpandedChange={(expandedNodes) => {
						setExpandedKeys(Array.from(expandedNodes));
					}}
					onItemsChange={() => {}}
					showExpanderOnHover={false}
				>
					{(item) => (
						<ClayTreeView.Item
							actions={<ItemActions item={item} />}
						>
							<ClayTreeView.ItemStack
								className={classNames(
									'page-editor__page-structure__clay-tree-node',
									{
										'page-editor__page-structure__clay-tree-node--active':
											item.active && item.activable,
										'page-editor__page-structure__clay-tree-node--hovered':
											item.hovered,
										'page-editor__page-structure__clay-tree-node--mapped':
											item.mapped,
										'page-editor__page-structure__clay-tree-node--master-item':
											item.isMasterItem,
									}
								)}
								data-qa-id={item.tooltipTitle}
								data-title={
									item.isMasterItem || !item.activable
										? ''
										: item.tooltipTitle
								}
								data-tooltip-align={
									item.isMasterItem || !item.activable
										? ''
										: 'right'
								}
								onKeyDown={(event) => onKeyDown(event, item)}
								onMouseLeave={(event) => {
									if (item.hovered) {
										event.stopPropagation();
										hoverItem(null, {
											origin: ITEM_ACTIVATION_ORIGINS.sidebar,
										});
									}
								}}
								onMouseOver={(event) => {
									event.stopPropagation();
									hoverItem(item.id, {
										origin: ITEM_ACTIVATION_ORIGINS.sidebar,
									});
								}}
							>
								<span className="sr-only">{item.name}</span>

								<StructureTreeNode node={item} />
							</ClayTreeView.ItemStack>

							<ClayTreeView.Group items={item.children}>
								{(item) => (
									<ClayTreeView.Item
										actions={<ItemActions item={item} />}
									>
										<span className="sr-only">
											{item.name}
										</span>

										<StructureTreeNode node={item} />
									</ClayTreeView.Item>
								)}
							</ClayTreeView.Group>
						</ClayTreeView.Item>
					)}
				</ClayTreeView>
			</DragAndDropContextProvider>
		</div>
	);
}
