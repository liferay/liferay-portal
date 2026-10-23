/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {LinkOrButton} from '@clayui/shared';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useContext, useMemo} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../FrontendDataSetContext';
import formatActionURL from '../utils/actionItems/formatActionURL';
import isLink from '../utils/isLink';
import {
	EItemActionsType,
	IActionsDropdown,
	IItemsActions,
	TClayDropDownItem,
} from '../utils/types';

function isDefined(value: any) {
	return value !== null && value !== undefined;
}

function ActionsDropdown({
	accessibleName,
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

	const isMounted = useIsMounted();

	const items = useMemo(() => {
		const mapActionsToItems = (currentActions: IItemsActions[]): any[] => {
			const hasIconsInGroup = currentActions.some(
				(action) => !!action.icon
			);

			return currentActions.flatMap((action, index) => {
				const {
					className,
					disabled,
					icon,
					items: nestedItems,
					label,
					separator,
					type,
				} = action;

				const newItem: TClayDropDownItem = {};

				if (isDefined(className)) {
					newItem.className = className;
				}

				if (isDefined(disabled)) {
					newItem.disabled = disabled;
				}

				if (isDefined(action.href)) {
					newItem.href = formatActionURL(
						action.href,
						itemData,
						action.target
					);
				}

				if (nestedItems?.length) {
					newItem.items = mapActionsToItems(nestedItems);
				}

				if (isDefined(label)) {
					newItem.label = label;
				}

				newItem.onClick = (event: any) => {
					onClick({
						action,
						closeMenu: () =>
							onMenuActiveChange && onMenuActiveChange(false),
						event,
					});
				};

				if (hasIconsInGroup) {
					newItem.symbolLeft = icon;
				}

				if (isDefined(type)) {
					newItem.type = type;
				}

				if (separator && index > 0 && nestedItems?.length) {
					return [{type: 'divider'}, newItem];
				}

				return [newItem];
			});
		};

		return mapActionsToItems(actions);
	}, [actions, itemData, onClick, onMenuActiveChange]);

	const inlineEditingAvailable =
		inlineEditingSettings && itemData.actions?.update;

	const inlineEditingAlwaysOn =
		inlineEditingAvailable && inlineEditingSettings.alwaysOn;

	const parsedItemId: number =
		typeof itemId === 'string' ? parseInt(itemId, 10) : itemId;

	const editModeActive = !!itemsChanges?.[parsedItemId];

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
		actions.length === 1 &&
		(actions[0].type === undefined ||
			actions[0].type === EItemActionsType.ITEM ||
			((actions[0].type === EItemActionsType.CONTEXTUAL ||
				actions[0].type === EItemActionsType.GROUP) &&
				actions[0].items?.length === 1))
	) {
		let [action] = actions;

		if (
			action.type === EItemActionsType.CONTEXTUAL ||
			action.type === EItemActionsType.GROUP
		) {
			if (action?.items && action?.items[0]) {
				action = action.items[0];
			}
		}

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

	return (
		<div className="d-flex">
			{inlineEditingAlwaysOn && inlineEditingActions}

			<ClayDropDownWithItems
				active={menuActive}
				items={items}
				menuElementAttrs={{onClick: (event) => event.stopPropagation()}}
				onActiveChange={(active) =>
					onMenuActiveChange && onMenuActiveChange(active)
				}
				trigger={
					<ClayButton
						className="component-action dropdown-toggle"
						disabled={loading}
						displayType="unstyled"
						onClick={(event) => event.stopPropagation()}
					>
						<ClayIcon symbol="ellipsis-v" />

						<span className="sr-only">
							{sub(
								Liferay.Language.get('x-actions'),
								accessibleName
							)}
						</span>
					</ClayButton>
				}
			/>
		</div>
	);
}

export default ActionsDropdown;
