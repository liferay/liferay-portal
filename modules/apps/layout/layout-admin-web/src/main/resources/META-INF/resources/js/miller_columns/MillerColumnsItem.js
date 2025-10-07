/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {PageTemplateModal} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';
import {useDrag, useDrop} from 'react-dnd';
import {getEmptyImage} from 'react-dnd-html5-backend';

import ACTIONS from '../actions';
import {ACCEPTING_TYPES} from './constants/acceptingTypes';
import {DROP_POSITIONS} from './constants/dropPositions';
import {LayoutColumnsContext} from './contexts/LayoutColumnsContext';
import {useKeyboardMovement} from './hooks/useKeyboardMovement';
import {useKeyboardNavigation} from './hooks/useKeyboardNavigation';
import {isValidMovement} from './utils/isValidMovement';

const ITEM_HOVER_BORDER_LIMIT = 20;

const ITEM_HOVER_TIMEOUT = 500;

const ITEM_STATES_COLORS = {
	'conversion-draft': 'info',
	'draft': 'secondary',
	'empty': 'warning',
	'pending': 'info',
};

const getDropPosition = (ref, monitor) => {
	if (!ref.current) {
		return;
	}

	const clientOffset = monitor.getClientOffset();
	const dropItemBoundingRect = ref.current.getBoundingClientRect();
	const hoverTopLimit = ITEM_HOVER_BORDER_LIMIT;
	const hoverBottomLimit =
		dropItemBoundingRect.height - ITEM_HOVER_BORDER_LIMIT;
	const hoverClientY = clientOffset.y - dropItemBoundingRect.top;

	let dropPosition = DROP_POSITIONS.middle;

	if (hoverClientY < hoverTopLimit) {
		dropPosition = DROP_POSITIONS.top;
	}
	else if (hoverClientY > hoverBottomLimit) {
		dropPosition = DROP_POSITIONS.bottom;
	}

	return dropPosition;
};

function addSeparators(items) {
	if (items.length < 2) {
		return items;
	}

	const separatedItems = [items[0]];

	for (let i = 1; i < items.length; i++) {
		const item = items[i];

		if (item.type === 'group' && item.separator) {
			separatedItems.push({type: 'divider'});
		}

		separatedItems.push(item);
	}

	return separatedItems.map((item) => {
		if (item.type === 'group') {
			return {
				...item,
				items: addSeparators(item.items),
			};
		}

		return item;
	});
}

function filterEmptyGroups(items) {
	return items
		.filter(
			(item) =>
				item.type !== 'group' ||
				(Array.isArray(item.items) && item.items.length)
		)
		.map((item) =>
			item.type === 'group'
				? {...item, items: filterEmptyGroups(item.items)}
				: item
		);
}

const noop = () => {};

const MillerColumnsItem = ({
	createPageTemplateURL,
	getPageTemplateCollectionsURL,
	getItemActionsURL,
	isLayoutSetPrototype,
	item,
	items,
	namespace,
	onItemDrop = noop,
	getItemChildren = noop,
	rtl,
}) => {
	const {
		active,
		addChildLayoutURL,
		bulkActions = [],
		checked,
		description = '',
		draggable,
		hasChild,
		hasDuplicatedFriendlyURL = false,
		hasGuestViewPermission,
		id: itemId,
		quickActions = [],
		selectable,
		states = [],
		target,
		title,
		url,
		viewUrl,
	} = item;

	const ref = useRef();
	const timeoutRef = useRef();

	const onClose = () => {
		setOpenModal(false);
	};

	const [openModal, setOpenModal] = useState(false);

	const [dropPosition, setDropPosition] = useState();

	const [itemActionsActive, setItemActionsActive] = useState(false);

	const [dropdownActions, setDropdownActions] = useState([]);

	const loadPromiseRef = useRef();

	const [loadMessage, setLoadMessage] = useState('');

	const {layoutColumns, setLayoutColumns} = useContext(LayoutColumnsContext);

	function loadDropdownActions() {
		if (!loadPromiseRef.current) {
			let loadingMessageShown = false;
			let optionsLoaded = false;
			const url = new URL(getItemActionsURL);
			url.searchParams.append(`${namespace}plid`, itemId);

			setTimeout(() => {
				if (!optionsLoaded) {
					setLoadMessage(
						sub(Liferay.Language.get('loading-x-options'), title)
					);
					loadingMessageShown = true;
				}
			}, 500);

			loadPromiseRef.current = fetch(url, {
				method: 'GET',
			})
				.then((response) => response.json())
				.then(({actions}) => {
					optionsLoaded = true;
					if (loadingMessageShown) {
						setLoadMessage(
							sub(Liferay.Language.get('x-options-loaded'), title)
						);
					}

					const updateItem = (item) => {
						const newItem = {
							...item,
							onClick(event) {
								const action = item.data?.action;

								if (action === 'convertToPageTemplate') {
									setOpenModal(true);
								}
								else if (action) {
									event.preventDefault();

									ACTIONS[action]?.(item.data);
								}
							},
							symbolLeft: item.icon,
						};

						if (Array.isArray(item.items)) {
							newItem.items = item.items.map(updateItem);
						}

						return newItem;
					};

					const dropdownActions = actions.map((action) => {
						return {
							...action,
							items: action.items?.map(updateItem),
						};
					});

					setDropdownActions(
						addSeparators(filterEmptyGroups(dropdownActions))
					);
				});
		}
	}

	const normalizedQuickActions = useMemo(() => {
		return quickActions.filter(
			(action) => action.quickAction && action.url
		);
	}, [quickActions]);

	const [{isDragging: isDragSource}, drag, previewRef] = useDrag({
		canDrag: () => !isKeyboardMovementEnabled,
		collect: (monitor) => ({
			isDragging: !!monitor.isDragging(),
		}),
		end: ({initialColumns}, monitor) => {
			if (!monitor.didDrop()) {
				setLayoutColumns(initialColumns);
			}
		},
		isDragging: (monitor) => {
			const movedItems = monitor.getItem().items;

			return (
				(movedItems.some((item) => item.checked) && checked) ||
				movedItems.some((item) => item.id === itemId)
			);
		},
		item: {
			initialColumns: layoutColumns,
			items: checked
				? Array.from(items.values()).filter((item) => item.checked)
				: [items.get(itemId)],
			type: ACCEPTING_TYPES.ITEM,
		},
	});

	const [{isOver}, drop] = useDrop({
		accept: ACCEPTING_TYPES.ITEM,
		canDrop(source, monitor) {
			const dropPosition = getDropPosition(ref, monitor);

			return isValidMovement({
				dropPosition,
				items,
				sources: source.items,
				target: item,
			});
		},
		collect: (monitor) => ({
			isOver: !!monitor.isOver(),
		}),
		drop(source, monitor) {
			if (monitor.canDrop()) {
				onItemDrop(source.items, item, dropPosition);
			}
		},
		hover(source, monitor) {
			let dropPosition;

			if (isOver && monitor.canDrop()) {
				dropPosition = getDropPosition(ref, monitor);
			}

			setDropPosition(dropPosition);
		},
	});

	const {
		enableMovement,
		isEnabled: isKeyboardMovementEnabled,
		isSource: isKeyboardMovementSource,
		isTarget: isKeyboardMovementTarget,
		position: keyboardMovementPosition,
	} = useKeyboardMovement({
		element: ref.current,
		item,
		items,
	});

	const {isTarget: isNavigationTarget, onKeyDown} = useKeyboardNavigation({
		element: ref.current,
		getItemChildren,
		item,
		rtl,
	});

	const tabIndex = isNavigationTarget || -1;
	const targetPosition = dropPosition || keyboardMovementPosition;
	const isSource = isDragSource || isKeyboardMovementSource;
	const isTarget = isOver || isKeyboardMovementTarget;

	useEffect(() => {
		drag(drop(ref));
	}, [drag, drop]);

	useEffect(() => {
		previewRef(getEmptyImage(), {captureDraggingState: true});
	}, [previewRef]);

	useEffect(() => {
		if (
			!active &&
			isTarget &&
			targetPosition === DROP_POSITIONS.middle &&
			!timeoutRef.current
		) {
			timeoutRef.current = setTimeout(() => {
				if (isTarget) {
					getItemChildren(itemId);
				}
			}, ITEM_HOVER_TIMEOUT);
		}
		else if (
			!isTarget ||
			(targetPosition !== DROP_POSITIONS.middle && timeoutRef.current)
		) {
			clearTimeout(timeoutRef.current);
			timeoutRef.current = null;
		}
	}, [active, isTarget, itemId, getItemChildren, targetPosition]);

	const warningMessage = isLayoutSetPrototype
		? Liferay.Language.get(
				'there-is-a-page-with-the-same-friendly-url-in-a-site-using-this-site-template'
			)
		: Liferay.Language.get(
				'there-is-a-page-with-the-same-friendly-url-in-the-site-template'
			);

	const groupId = useId();

	const ariaProps = hasChild
		? {
				'aria-controls': `miller-columns-list-${itemId}`,
				'aria-expanded': active,
			}
		: {};

	return (
		<ClayLayout.ContentRow
			className={classNames('list-group-item-flex miller-columns-item', {
				'dragging': isSource,
				'drop-bottom':
					isTarget && targetPosition === DROP_POSITIONS.bottom,
				'drop-middle':
					isTarget && targetPosition === DROP_POSITIONS.middle,
				'drop-top': isTarget && targetPosition === DROP_POSITIONS.top,
				'miller-columns-item--active': active,
			})}
			containerElement="li"
			data-actions={bulkActions}
			onKeyDown={itemActionsActive ? null : onKeyDown}
			ref={ref}
			role="none"
			verticalAlign="center"
		>
			<a
				{...ariaProps}
				aria-current={active}
				aria-label={`${title} ${description}`.trim()}
				aria-owns={groupId}
				className="miller-columns-item-mask"
				href={url}
				role="menuitem"
				tabIndex={tabIndex}
			/>

			<span
				className="autofit-row autofit-row-center"
				id={groupId}
				role="menuitem"
			>
				{draggable && (
					<ClayLayout.ContentCol className="c-pl-0 miller-columns-item-drag-handler">
						<ClayButton
							aria-label={sub(Liferay.Language.get('move-x'), [
								title,
							])}
							className="drag-handler"
							displayType="unstyled"
							onClick={(event) => {
								if (event.detail === 0) {
									const sources = checked
										? Array.from(items.values()).filter(
												(item) => item.checked
											)
										: [item];

									enableMovement(sources);
								}
							}}
							onKeyDown={(event) => {
								if (
									![
										'ArrowDown',
										'ArrowLeft',
										'ArrowRight',
										'ArrowUp',
									].includes(event.key)
								) {
									event.stopPropagation();
								}
							}}
							tabIndex={tabIndex}
							title={sub(Liferay.Language.get('move-x'), [title])}
						>
							<ClayIcon symbol="drag" />
						</ClayButton>
					</ClayLayout.ContentCol>
				)}

				{selectable && (
					<ClayLayout.ContentCol data-qa-id="selectLayout">
						<ClayCheckbox
							aria-label={sub(
								Liferay.Language.get('select-x'),
								title
							)}
							className="c-mb-0"
							defaultChecked={checked}
							name={`${namespace}rowIds`}
							tabIndex={tabIndex}
							value={itemId}
						/>
					</ClayLayout.ContentCol>
				)}

				<ClayLayout.ContentCol className="c-pl-1" expand>
					<div
						className="align-items-center list-group-title text-truncate-inline"
						data-qa-id="layoutHref"
					>
						{viewUrl ? (
							<ClayLink
								aria-label={(() => {
									if (!hasGuestViewPermission) {
										return `${title}. ${Liferay.Language.get(
											'restricted-page'
										)}`;
									}

									if (
										Liferay.FeatureFlags['LPS-174417'] &&
										hasDuplicatedFriendlyURL
									) {
										return `${title}. ${warningMessage}`;
									}

									return title;
								})()}
								href={viewUrl}
								tabIndex={tabIndex}
								target={target}
							>
								{title}
							</ClayLink>
						) : (
							<span>{title}</span>
						)}

						{!hasGuestViewPermission && (
							<ClayIcon
								className="c-ml-2 c-mt-0 lfr-portal-tooltip miller-columns-item--restricted__icon text-4 text-secondary"
								data-title={Liferay.Language.get(
									'restricted-page'
								)}
								symbol="password-policies"
							/>
						)}

						{Liferay.FeatureFlags['LPS-174417'] &&
						hasDuplicatedFriendlyURL ? (
							<ClayIcon
								className="align-self-center c-ml-2 flex-shrink-0 icon-warning lfr-portal-tooltip text-warning"
								data-title={warningMessage}
								symbol="warning-full"
							/>
						) : null}
					</div>

					{description && (
						<div className="d-flex flex-wrap h5 list-group-subtitle small">
							<span className="mr-2">{description}</span>

							{states.map((state) => (
								<ClayLabel
									displayType={ITEM_STATES_COLORS[state.id]}
									key={state.id}
								>
									{state.label}
								</ClayLabel>
							))}
						</div>
					)}
				</ClayLayout.ContentCol>

				{addChildLayoutURL ? (
					<ClayLayout.ContentCol className="miller-columns-item-actions">
						<ClayLink
							aria-label={Liferay.Language.get('add-child-page')}
							borderless
							button={{monospaced: true}}
							displayType="secondary"
							href={addChildLayoutURL}
							tabIndex={tabIndex}
							title={Liferay.Language.get('add-child-page')}
						>
							<ClayIcon symbol="plus" />
						</ClayLink>
					</ClayLayout.ContentCol>
				) : null}

				{normalizedQuickActions.map((action) => (
					<ClayLayout.ContentCol
						className="miller-columns-item-quick-action"
						key={action.id}
					>
						<ClayLink
							borderless
							displayType="secondary"
							href={action.url}
							monospaced
							outline
						>
							<ClayIcon symbol={action.icon} />
						</ClayLink>
					</ClayLayout.ContentCol>
				))}

				{!!getItemActionsURL && itemId !== '0' ? (
					<ClayLayout.ContentCol className="miller-columns-item-actions">
						<ClayDropDownWithItems
							active={itemActionsActive}
							caption={
								!loadPromiseRef.current ? (
									<ClayLoadingIndicator />
								) : (
									''
								)
							}
							items={dropdownActions}
							onActiveChange={setItemActionsActive}
							onKeyDown={(event) => event.stopPropagation()}
							trigger={
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'open-page-options-menu'
									)}
									borderless
									className="btn-options"
									displayType="secondary"
									onClick={loadDropdownActions}
									size="sm"
									symbol="ellipsis-v"
									tabIndex={tabIndex}
									title={Liferay.Language.get(
										'open-page-options-menu'
									)}
								/>
							}
						/>

						<span aria-live="polite" className="sr-only">
							{loadMessage}
						</span>

						{openModal && (
							<PageTemplateModal
								createTemplateURL={createPageTemplateURL}
								getCollectionsURL={
									getPageTemplateCollectionsURL
								}
								layoutId={itemId}
								namespace={namespace}
								onClose={onClose}
							/>
						)}
					</ClayLayout.ContentCol>
				) : null}

				{hasChild && (
					<ClayLayout.ContentCol className="miller-columns-item-child-indicator text-secondary">
						<ClayIcon symbol={rtl ? 'caret-left' : 'caret-right'} />
					</ClayLayout.ContentCol>
				)}
			</span>
		</ClayLayout.ContentRow>
	);
};

export default MillerColumnsItem;
