/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {LinkOrButton} from '@clayui/shared';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import React, {useContext, useMemo} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../FrontendDataSetContext';
import formatActionURL from '../utils/actionItems/formatActionURL';
import isLink from '../utils/isLink';
import {IActionsDropdown, IItemsActions} from '../utils/types';

interface IDropdownItem {
	action: IItemsActions;
	className?: string;
	closeMenu: Function;
	onClick: Function;
	setLoading: Function;
	url: string | undefined;
}

function DropdownItem({
	action,
	className,
	closeMenu,
	onClick,
	url,
}: IDropdownItem) {
	const {icon, label, target} = action;

	return (
		<ClayDropDown.Item
			className={className}
			disabled={action.disabled}
			href={isLink(target, null) ? url : undefined}
			onClick={(event) =>
				onClick({
					action,
					closeMenu,
					event,
				})
			}
		>
			{icon && (
				<span className="dropdown-item-indicator-start">
					<ClayIcon symbol={icon} />
				</span>
			)}

			{label}
		</ClayDropDown.Item>
	);
}

function ActionsDropdown({
	actions,
	itemData,
	itemId,
	loading,
	menuActive,
	onClick,
	onMenuActiveChange,
	setLoading,
}: IActionsDropdown) {
	const {
		applyItemInlineUpdates,
		inlineEditingSettings,
		itemsChanges,
		toggleItemInlineEdit,
		uniformActionsDisplay,
	}: IFrontendDataSetContext = useContext(FrontendDataSetContext);

	const inlineEditingAvailable =
		inlineEditingSettings && itemData.actions?.update;

	const inlineEditingAlwaysOn =
		inlineEditingAvailable && inlineEditingSettings.alwaysOn;

	const isMounted = useIsMounted();

	let parsedItemId: number;

	if (typeof itemId === 'string') {
		parsedItemId = parseInt(itemId, 10);
	}
	else {
		parsedItemId = itemId;
	}

	const editModeActive = !!itemsChanges![parsedItemId];

	const hasIcons = useMemo(() => {
		const checkHasIcon = (currentActions: IItemsActions[]): boolean => {
			for (const action of currentActions) {
				if (action.icon) {
					return true;
				}

				if (action.type === 'group' && action.items?.length) {
					if (checkHasIcon(action.items)) {
						return true;
					}
				}
			}

			return false;
		};

		return checkHasIcon(actions);
	}, [actions]);

	const itemChanges =
		editModeActive && Object.keys(itemsChanges![parsedItemId]).length
			? itemsChanges![parsedItemId]
			: null;

	const inlineEditingActions = (
		<div className="d-flex">
			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('edit')}
				className="mr-1"
				disabled={inlineEditingAlwaysOn && !itemChanges}
				displayType="secondary"
				onClick={() => toggleItemInlineEdit!(parsedItemId)}
				size="xs"
				symbol="times-small"
			/>

			{loading ? (
				<ClayLoadingIndicator className="mb-2 mt-2" />
			) : (
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('save')}
					disabled={!itemChanges}
					monospaced
					onClick={() => {
						setLoading(true);

						applyItemInlineUpdates!(parsedItemId).finally(() => {
							if (isMounted()) {
								setLoading(false);
							}
						});
					}}
					size="xs"
					symbol="check"
				/>
			)}
		</div>
	);

	if (!inlineEditingAlwaysOn && editModeActive) {
		return inlineEditingActions;
	}

	if (!actions.length) {
		return null;
	}

	if (
		!inlineEditingAlwaysOn &&
		!uniformActionsDisplay &&
		actions.length === 1
	) {
		const [action] = actions;

		if (loading) {
			return <ClayLoadingIndicator className="mb-2 mt-2" />;
		}

		return (
			<LinkOrButton
				aria-label={action.label}
				className={classNames(
					'btn btn-secondary btn-sm',
					action.className
				)}
				disabled={action.disabled}
				href={
					isLink(
						action.target,
						action.onClick ? action.onClick : null
					)
						? formatActionURL(action.href, itemData, action.target)
						: null
				}
				monospaced={Boolean(action.icon)}
				onClick={(event: any) => {
					event.stopPropagation();

					onClick({
						action,
						event,
					});
				}}
				title={action.label}
			>
				{action.icon ? <ClayIcon symbol={action.icon} /> : action.label}
			</LinkOrButton>
		);
	}

	if (loading && !inlineEditingAlwaysOn) {
		return <ClayLoadingIndicator className="mb-2 mt-2" />;
	}

	const renderItems = (items: IItemsActions[]) =>
		items.map(({items: nestedItems = [], separator, type, ...item}, i) => {
			if (type === 'group') {
				if (!nestedItems.length) {
					return;
				}

				return (
					<ClayDropDown.Group {...item} key={i}>
						{separator && i !== 0 && <ClayDropDown.Divider />}

						{renderItems(nestedItems)}
					</ClayDropDown.Group>
				);
			}

			return (
				<DropdownItem
					action={item}
					className={item.className}
					closeMenu={() =>
						onMenuActiveChange && onMenuActiveChange(false)
					}
					key={i}
					onClick={onClick}
					setLoading={setLoading}
					url={
						item.href &&
						formatActionURL(item.href, itemData, item.target)
					}
				/>
			);
		});

	return (
		<div className="d-flex">
			{inlineEditingAlwaysOn && inlineEditingActions}

			<ClayDropDown
				active={menuActive}
				hasLeftSymbols={hasIcons}
				onActiveChange={() =>
					onMenuActiveChange && onMenuActiveChange(!menuActive)
				}
				onClick={(event) => event.stopPropagation()}
				trigger={
					<ClayButton
						className="component-action dropdown-toggle"
						disabled={loading}
						displayType="unstyled"
					>
						<ClayIcon symbol="ellipsis-v" />

						<span className="sr-only">
							{Liferay.Language.get('actions')}
						</span>
					</ClayButton>
				}
			>
				<ClayDropDown.ItemList>
					{renderItems(actions)}
				</ClayDropDown.ItemList>
			</ClayDropDown>
		</div>
	);
}

export default ActionsDropdown;
