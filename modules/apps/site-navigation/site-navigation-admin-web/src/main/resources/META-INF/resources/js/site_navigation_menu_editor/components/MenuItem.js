/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayCard from '@clayui/card';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {fetch, objectToFormData, sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useMemo} from 'react';

import {NESTING_MARGIN} from '../constants/nestingMargin';
import {SIDEBAR_PANEL_IDS} from '../constants/sidebarPanelIds';
import {useConstants} from '../contexts/ConstantsContext';
import {useItems, useSetItems} from '../contexts/ItemsContext';
import {useDragLayer, useSetDragLayer} from '../contexts/KeyboardDndContext';
import {
	useSelectedMenuItemId,
	useSetSelectedMenuItemId,
} from '../contexts/SelectedMenuItemIdContext';
import {useSetSidebarPanelId} from '../contexts/SidebarPanelIdContext';
import getFlatItems from '../utils/getFlatItems';
import getItemPath from '../utils/getItemPath';
import {useDragItem, useDropTarget} from '../utils/useDragAndDrop';
import useKeyboardNavigation from '../utils/useKeyboardNavigation';
import {AddItemDropDown} from './AddItemDropdown';
import MenuItemOptions from './MenuItemOptions';

export function MenuItem({item, onMenuItemRemoved, sidebarPanelRef}) {
	const setItems = useSetItems();
	const setSelectedMenuItemId = useSetSelectedMenuItemId();
	const setSidebarPanelId = useSetSidebarPanelId();
	const {editSiteNavigationMenuItemParentURL, portletNamespace, spritemap} =
		useConstants();

	const items = useItems();
	const {
		parentSiteNavigationMenuItemId,
		siteNavigationMenuItemId,
		title,
		type,
	} = item;
	const itemPath = getItemPath(siteNavigationMenuItemId, items);
	const selected = useSelectedMenuItemId() === siteNavigationMenuItemId;

	const order = useMemo(
		() =>
			items
				.filter(
					(siteNavigationMenuItem) =>
						siteNavigationMenuItem.parentSiteNavigationMenuItemId ===
						item.parentSiteNavigationMenuItemId
				)
				.findIndex(
					(siteNavigationMenuItem) =>
						siteNavigationMenuItem.siteNavigationMenuItemId ===
						item.siteNavigationMenuItemId
				),
		[items, item]
	);

	const updateMenuItemParent = (itemId, parentId, order) => {
		updateMenuItem({
			editSiteNavigationMenuItemParentURL,
			itemId,
			order,
			parentId,
			portletNamespace,
		})
			.then(({siteNavigationMenuItems}) => {
				const newItems = getFlatItems(siteNavigationMenuItems);

				setItems(newItems);
			})
			.catch(({error}) => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});

				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}
			});
	};

	const keyboardDragLayer = useDragLayer();
	const setKeyboardDragLayer = useSetDragLayer();
	const {handlerRef, isDragging} = useDragItem(item, updateMenuItemParent);

	const {isOver, isOverFirstItem, nestingLevel, targetRef} =
		useDropTarget(item);

	const isKeyboardDragging = useMemo(
		() =>
			keyboardDragLayer?.siteNavigationMenuItemId
				? getItemPath(siteNavigationMenuItemId, items).includes(
						keyboardDragLayer.siteNavigationMenuItemId
					)
				: false,
		[
			items,
			keyboardDragLayer?.siteNavigationMenuItemId,
			siteNavigationMenuItemId,
		]
	);

	const itemStyle = {
		'--nesting-level': itemPath.length,
		'--nesting-margin': NESTING_MARGIN,
		'--over-nesting-level': nestingLevel,
	};

	const parentItemId =
		itemPath.length > 1 ? itemPath[itemPath.length - 2] : '0';

	const {element, isTarget, onBlur, onFocus, onKeyDown, setElement} =
		useKeyboardNavigation();

	const onDragHandlerKeyDown = (event) => {
		if (event.key === 'Enter') {
			event.preventDefault();
			event.stopPropagation();

			if (isKeyboardDragging) {
				let nextOrder = keyboardDragLayer.order;

				if (
					parentSiteNavigationMenuItemId ===
						keyboardDragLayer.parentSiteNavigationMenuItemId &&
					keyboardDragLayer.order > order
				) {
					nextOrder -= 1;
				}

				updateMenuItem({
					editSiteNavigationMenuItemParentURL,
					itemId: keyboardDragLayer.siteNavigationMenuItemId,
					order: nextOrder,
					parentId: keyboardDragLayer.parentSiteNavigationMenuItemId,
					portletNamespace,
				}).then(({siteNavigationMenuItems}) => {
					setKeyboardDragLayer(items, null);
					setItems(getFlatItems(siteNavigationMenuItems));
				});
			}
			else {
				setKeyboardDragLayer(items, {
					eventKey: event.key,
					menuItemTitle: title,
					menuItemType: type,
					order,
					parentSiteNavigationMenuItemId:
						item.parentSiteNavigationMenuItemId,
					siteNavigationMenuItemId,
				});
			}
		}

		if (event.key === 'Escape') {
			setKeyboardDragLayer(items, null);
		}

		if (!isKeyboardDragging) {
			return;
		}

		event.stopPropagation();

		const eventKey = event.key;

		if (eventKey === 'ArrowDown' || eventKey === 'ArrowUp') {
			const getNextPosition =
				eventKey === 'ArrowDown' ? getDownPosition : getUpPosition;

			const findNextPosition = (previousResult) => {
				const nextResult = getNextPosition({
					items,
					order: previousResult.order,
					parentSiteNavigationMenuItemId:
						previousResult.parentSiteNavigationMenuItemId,
				});

				if (!nextResult) {
					return nextResult;
				}

				const resultPath = getItemPath(
					nextResult.parentSiteNavigationMenuItemId,
					items
				);

				if (resultPath.includes(siteNavigationMenuItemId)) {
					return findNextPosition(nextResult);
				}

				return nextResult;
			};

			const result = findNextPosition({
				order: keyboardDragLayer ? keyboardDragLayer.order : order,
				parentSiteNavigationMenuItemId: keyboardDragLayer
					? keyboardDragLayer.parentSiteNavigationMenuItemId
					: item.parentSiteNavigationMenuItemId,
			});

			if (!result) {
				return;
			}

			event.preventDefault();

			setKeyboardDragLayer(items, {
				eventKey,
				menuItemTitle: title,
				menuItemType: type,
				order: result.order,
				parentSiteNavigationMenuItemId:
					result.parentSiteNavigationMenuItemId,
				siteNavigationMenuItemId,
			});
		}
	};

	return (
		<>
			<div
				aria-label={
					item.icon
						? `${sub(
								Liferay.Language.get(
									'open-x-configuration-panel'
								),
								`${title} (${type})`
							)}. ${Liferay.Language.get(
								'this-item-does-not-have-a-display-page'
							)}`
						: sub(
								Liferay.Language.get(
									'open-x-configuration-panel'
								),
								`${title} (${type})`
							)
				}
				aria-level={itemPath.length}
				className={classNames(
					'focusable-menu-item site_navigation_menu_editor_MenuItem',
					{
						'active': selected,
						'dragging': isDragging || isKeyboardDragging,
						'is-over': isOver,
						'is-over-top': isOverFirstItem,
					}
				)}
				data-item-id={item.siteNavigationMenuItemId}
				data-nesting-level={nestingLevel}
				data-parent-item-id={parentItemId}
				data-tooltip-align="top-left"
				onBlur={onBlur}
				onClick={(event) => {
					if (!isKeyboardDragging && event.nativeEvent.pointerType) {
						setSelectedMenuItemId(siteNavigationMenuItemId);
						setSidebarPanelId(SIDEBAR_PANEL_IDS.menuItemSettings);
					}
				}}
				onFocus={onFocus}
				onKeyDown={(event) => {
					if (
						(event.key === ' ' || event.key === 'Enter') &&
						!isKeyboardDragging &&
						event.target === element
					) {
						setSelectedMenuItemId(siteNavigationMenuItemId);
						setSidebarPanelId(SIDEBAR_PANEL_IDS.menuItemSettings);

						sidebarPanelRef.current.focus();
					}

					onKeyDown(event);
				}}
				ref={(ref) => {
					targetRef(ref);
					setElement(ref);
				}}
				role="menuitem"
				style={itemStyle}
				tabIndex={isTarget ? '0' : '-1'}
				title={sub(
					Liferay.Language.get('open-x-configuration-panel'),
					title
				)}
			>
				<ClayCard className="mb-4">
					<ClayCard.Body className="px-0">
						<div ref={handlerRef}>
							<ClayCard.Row>
								<ClayLayout.ContentCol gutters>
									<ClayButtonWithIcon
										displayType="unstyled"
										monospaced={false}
										onBlur={() =>
											setKeyboardDragLayer(null)
										}
										onKeyDown={onDragHandlerKeyDown}
										size="sm"
										symbol="drag"
										tabIndex={isTarget ? '0' : '-1'}
										title={sub(
											Liferay.Language.get('move-x'),
											title
										)}
									/>
								</ClayLayout.ContentCol>

								{item.displayIcon && (
									<ClayLayout.ContentCol gutters>
										<ClayIcon
											className="lfr-portal-tooltip mr-3"
											spritemap={spritemap}
											style={{
												height: '1.5rem',
												width: '1.5rem',
											}}
											symbol={item.displayIcon}
										/>
									</ClayLayout.ContentCol>
								)}

								<ClayLayout.ContentCol expand>
									<ClayCard.Description
										displayType="title"
										title={null}
										truncate={false}
									>
										{title}

										{item.icon && (
											<ClayIcon
												className="lfr-portal-tooltip ml-2 text-warning"
												data-title={item.iconLabel}
												symbol={item.icon}
											/>
										)}
									</ClayCard.Description>

									<div className="d-flex">
										<ClayLabel
											className="mt-1"
											displayType="secondary"
										>
											{type}
										</ClayLabel>

										{item.dynamic && (
											<ClayLabel
												className="mt-1"
												displayType="info"
											>
												{Liferay.Language.get(
													'dynamic'
												)}
											</ClayLabel>
										)}
									</div>
								</ClayLayout.ContentCol>

								<div
									onClick={(event) => event.stopPropagation()}
								>
									<AddItemDropDown
										className="position-absolute site_navigation_menu_editor_MenuItem-add-button-dropdown top-button"
										order={order}
										parentSiteNavigationMenuItemId={
											item.parentSiteNavigationMenuItemId
										}
										trigger={
											<ClayButtonWithIcon
												className="site_navigation_menu_editor_MenuItem-add-button"
												displayType="primary"
												onClick={(event) => {
													event.preventDefault();
													event.stopPropagation();
												}}
												size="xs"
												symbol="plus"
												tabIndex={isTarget ? '0' : '-1'}
												title={sub(
													Liferay.Language.get(
														'add-item-before-x'
													),
													title
												)}
											/>
										}
									/>

									<AddItemDropDown
										className="bottom-button position-absolute site_navigation_menu_editor_MenuItem-add-button-dropdown"
										order={order + 1}
										parentSiteNavigationMenuItemId={
											item.parentSiteNavigationMenuItemId
										}
										trigger={
											<ClayButtonWithIcon
												className="site_navigation_menu_editor_MenuItem-add-button"
												displayType="primary"
												onClick={(event) => {
													event.preventDefault();
													event.stopPropagation();
												}}
												size="xs"
												symbol="plus"
												tabIndex={isTarget ? '0' : '-1'}
												title={sub(
													Liferay.Language.get(
														'add-item-after-x'
													),
													title
												)}
											/>
										}
									/>
								</div>

								<ClayLayout.ContentCol
									gutters
									onClick={(event) => event.stopPropagation()}
								>
									<MenuItemOptions
										isTarget={isTarget}
										label={title}
										numberOfChildren={item.children.length}
										onMenuItemRemoved={onMenuItemRemoved}
										siteNavigationMenuItemId={
											siteNavigationMenuItemId
										}
									/>
								</ClayLayout.ContentCol>
							</ClayCard.Row>
						</div>
					</ClayCard.Body>
				</ClayCard>
			</div>
		</>
	);
}

MenuItem.propTypes = {
	item: PropTypes.shape({
		children: PropTypes.array.isRequired,
		icon: PropTypes.string,
		iconLabel: PropTypes.string,
		siteNavigationMenuItemId: PropTypes.string.isRequired,
		title: PropTypes.string.isRequired,
		type: PropTypes.string.isRequired,
	}),
};

function updateMenuItem({
	editSiteNavigationMenuItemParentURL,
	itemId,
	order,
	parentId,
	portletNamespace,
}) {
	return fetch(editSiteNavigationMenuItemParentURL, {
		body: objectToFormData({
			[`${portletNamespace}siteNavigationMenuItemId`]: itemId,
			[`${portletNamespace}parentSiteNavigationMenuItemId`]: parentId,
			[`${portletNamespace}order`]: order,
		}),
		method: 'POST',
	})
		.then((response) => response.json())
		.catch(({error}) => {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});

			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}
		});
}

export function getDownPosition({
	items,
	order,
	parentSiteNavigationMenuItemId,
}) {
	const flatItems = getFlatItems(items);

	const parentItem = flatItems.find(
		(item) =>
			item.siteNavigationMenuItemId === parentSiteNavigationMenuItemId
	);

	const siblingsItems = flatItems.filter(
		(item) =>
			item.parentSiteNavigationMenuItemId ===
			parentSiteNavigationMenuItemId
	);

	const siblingItem = siblingsItems[order];

	// If there aren't any sibling, the menu is placed as the sibling of the parent.

	if (!siblingItem) {

		// If there aren't any sibling and the parentSiteNavigationMenuItemId is 0,
		// there is no movement possible.

		if (!parentItem) {
			return null;
		}

		const parentSiblings = flatItems.filter(
			(item) =>
				item.parentSiteNavigationMenuItemId ===
				parentItem.parentSiteNavigationMenuItemId
		);

		const parentOrder = parentSiblings.findIndex(
			(item) =>
				item.siteNavigationMenuItemId ===
				parentItem.siteNavigationMenuItemId
		);

		return {
			order: parentOrder + 1,
			parentSiteNavigationMenuItemId:
				parentItem.parentSiteNavigationMenuItemId,
		};
	}

	// If there aren't any sibling, the menu is placed as its child.

	return {
		order: 0,
		parentSiteNavigationMenuItemId: siblingItem.siteNavigationMenuItemId,
	};
}

export function getUpPosition({items, order, parentSiteNavigationMenuItemId}) {

	// The first menu cannot be moved upwards

	if (order === 0 && parentSiteNavigationMenuItemId === '0') {
		return null;
	}

	const flatItems = getFlatItems(items);

	const parentItem = flatItems.find(
		(item) =>
			item.siteNavigationMenuItemId === parentSiteNavigationMenuItemId
	);

	const siblingsItems = flatItems.filter(
		(item) =>
			item.parentSiteNavigationMenuItemId ===
			parentSiteNavigationMenuItemId
	);

	// When the menu is the first child, the menu is placed as the sibling of the parent.

	if (order === 0) {
		const parentSiblings = flatItems.filter(
			(item) =>
				item.parentSiteNavigationMenuItemId ===
				parentItem.parentSiteNavigationMenuItemId
		);

		const parentOrder = parentSiblings.findIndex(
			(item) =>
				item.siteNavigationMenuItemId ===
				parentItem.siteNavigationMenuItemId
		);

		return {
			order: parentOrder,
			parentSiteNavigationMenuItemId:
				parentItem.parentSiteNavigationMenuItemId,
		};
	}

	const siblingItem = siblingsItems[order - 1];

	const siblingItemChildren = flatItems.filter(
		(item) =>
			item.parentSiteNavigationMenuItemId ===
			siblingItem.siteNavigationMenuItemId
	);

	return {
		order: siblingItemChildren.length,
		parentSiteNavigationMenuItemId: siblingItem.siteNavigationMenuItemId,
	};
}
