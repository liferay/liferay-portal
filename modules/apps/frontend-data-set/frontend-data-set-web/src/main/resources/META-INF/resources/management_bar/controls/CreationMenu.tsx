/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown, {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {LinkOrButton} from '@clayui/shared';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {Ref, useContext, useState} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../../FrontendDataSetContext';
import {ACTION_ITEM_TARGETS} from '../../utils/actionItems/constants';
import hasNestedActions from '../../utils/actionItems/hasNestedActions';
import {triggerAction} from '../../utils/actionItems/triggerAction';
import {ICreationActionItem, TClayDropDownItem} from '../../utils/types';
import {useWindowSize} from '../../utils/useWindowSize';

const MEDIUM_BREAKPOINT = 768;

function getDropDownItems(
	items: Array<ICreationActionItem>,
	onItemClick: (item: ICreationActionItem) => void
): Array<TClayDropDownItem> {
	const hasIcons = items.some((item) => !!item.icon);

	return items.flatMap((item, index) => {
		const dropDownItem: TClayDropDownItem = {
			className: item.className,
			label: item.label,
		};

		if (hasIcons) {
			dropDownItem.symbolLeft = item.icon;
		}

		if (hasNestedActions(item)) {
			dropDownItem.items = getDropDownItems(item.items, onItemClick);
			dropDownItem.type = item.type;

			if (item.separator && index > 0) {
				return [{type: 'divider'}, dropDownItem];
			}
		}
		else {
			dropDownItem.onClick = (event: React.MouseEvent) => {
				event.preventDefault();

				onItemClick(item);
			};
		}

		return [dropDownItem];
	});
}

function getSingleItem(
	primaryItems: Array<ICreationActionItem>
): ICreationActionItem | null {
	if (primaryItems.length !== 1) {
		return null;
	}

	const [item] = primaryItems;

	if (!hasNestedActions(item)) {
		return item;
	}

	if (item.items.length === 1 && !hasNestedActions(item.items[0])) {
		return item.items[0];
	}

	return null;
}

function triggerCreationAction(
	item: ICreationActionItem,
	frontendDataSetContext: IFrontendDataSetContext
) {
	item.onClick?.({
		loadData: frontendDataSetContext.loadData,
	});

	if (item.href || item.target) {
		triggerAction(item, frontendDataSetContext);
	}
}

const DropdownTrigger = React.forwardRef(
	(
		{
			hideManagementBarInEmptyState = false,
			inEmptyState,
			...otherProps
		}: {hideManagementBarInEmptyState?: boolean; inEmptyState: boolean},
		ref: Ref<HTMLButtonElement>
	) => {
		const {width} = useWindowSize();

		if (inEmptyState || width > MEDIUM_BREAKPOINT) {
			return (
				<ClayButton
					{...otherProps}
					className={!inEmptyState ? 'nav-btn px-3' : undefined}
					data-testid="fdsCreationActionButton"
					displayType={
						inEmptyState && hideManagementBarInEmptyState
							? 'primary'
							: 'secondary'
					}
					ref={ref}
				>
					<span className="inline-item inline-item-before">
						{Liferay.Language.get('new')}
					</span>

					<ClayIcon
						className="inline-item inline-item-after"
						symbol="caret-bottom"
					/>
				</ClayButton>
			);
		}
		else {
			return (
				<ClayButton
					{...otherProps}
					aria-label={Liferay.Language.get('new')}
					className="nav-btn nav-btn-monospaced"
					data-testid="fdsCreationActionButton"
					displayType={
						inEmptyState && hideManagementBarInEmptyState
							? 'primary'
							: 'secondary'
					}
					ref={ref}
					title={Liferay.Language.get('new')}
				>
					<ClayIcon className="inline-item ml-2 mr-2" symbol="plus" />

					<ClayIcon
						className="inline-item mr-2"
						symbol="caret-bottom"
					/>
				</ClayButton>
			);
		}
	}
);

const DropDown = ({
	hideManagementBarInEmptyState = false,
	inEmptyState,
	primaryItems,
}: {
	hideManagementBarInEmptyState?: boolean;
	inEmptyState: boolean;
	primaryItems: Array<ICreationActionItem>;
}) => {
	const frontendDataSetContext = useContext(FrontendDataSetContext);

	const [active, setActive] = useState(false);

	const onItemClick = (item: ICreationActionItem) => {
		setActive(false);

		triggerCreationAction(item, frontendDataSetContext);
	};

	const trigger = (
		<DropdownTrigger
			hideManagementBarInEmptyState={hideManagementBarInEmptyState}
			inEmptyState={inEmptyState}
		/>
	);

	if (primaryItems.some(hasNestedActions)) {
		return (
			<ClayDropDownWithItems
				active={active}
				items={getDropDownItems(primaryItems, onItemClick)}
				onActiveChange={setActive}
				trigger={trigger}
			/>
		);
	}

	return (
		<ClayDropDown
			active={active}
			onActiveChange={setActive}
			trigger={trigger}
		>
			<ClayDropDown.ItemList>
				{primaryItems.map((item, i) => (
					<ClayDropDown.Item
						className={item.className}
						key={i}
						onClick={(event) => {
							event.preventDefault();

							onItemClick(item);
						}}
					>
						{item.icon && (
							<span className="pr-2">
								<ClayIcon symbol={item.icon} />
							</span>
						)}

						{item.label}
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
};

function CreationButton({
	firstItem,
	inEmptyState,
}: {
	firstItem: ICreationActionItem;
	inEmptyState: boolean;
}) {
	const frontendDataSetContext = useContext(FrontendDataSetContext);

	const opensInNewTab = firstItem.target === ACTION_ITEM_TARGETS.BLANK;

	const {width} = useWindowSize();

	const isMobile = width <= MEDIUM_BREAKPOINT;

	const sharedProps = {
		['data-testid']: 'fdsCreationActionButton',
		['data-tooltip-align']: 'top',
		href: opensInNewTab ? firstItem.href : undefined,
		onClick: () => {
			if (opensInNewTab) {
				return;
			}

			triggerCreationAction(firstItem, frontendDataSetContext);
		},
		target: opensInNewTab ? '_blank' : undefined,
	};

	let newTabIcon = null;

	if (opensInNewTab && !isMobile) {
		newTabIcon = (
			<span
				className={classNames(
					'inline-item-after',
					inEmptyState ? 'inline-item' : 'd-inline-flex'
				)}
			>
				<ClayIcon symbol="shortcut" />
			</span>
		);
	}

	return inEmptyState ? (
		<LinkOrButton
			{...sharedProps}
			className={classNames('btn btn-secondary', firstItem.className)}
		>
			{firstItem.label}

			{newTabIcon}
		</LinkOrButton>
	) : (
		<LinkOrButton
			{...sharedProps}
			aria-label={
				opensInNewTab
					? sub(
							Liferay.Language.get('x-opens-new-window'),
							firstItem.label
						)
					: firstItem.label
			}
			className={classNames(
				'btn btn-primary nav-btn',
				firstItem.className,
				{
					['nav-btn-monospaced']: isMobile,
					['px-3']: !isMobile,
				}
			)}
			title={firstItem.label}
		>
			{isMobile ? <ClayIcon symbol="plus" /> : firstItem.label}

			{newTabIcon}
		</LinkOrButton>
	);
}

function CreationMenu({
	hideManagementBarInEmptyState = false,
	inEmptyState,
	primaryItems,
}: {
	hideManagementBarInEmptyState?: boolean;
	inEmptyState: boolean;
	primaryItems: Array<ICreationActionItem>;
}) {
	if (primaryItems?.length === 0) {
		return null;
	}

	const singleItem = getSingleItem(primaryItems);

	return (
		<ul
			className={classNames('navbar-nav', {
				'd-inline-flex': inEmptyState,
			})}
		>
			<li className="nav-item">
				{singleItem ? (
					<CreationButton
						firstItem={singleItem}
						inEmptyState={inEmptyState}
					/>
				) : (
					<DropDown
						hideManagementBarInEmptyState={
							hideManagementBarInEmptyState
						}
						inEmptyState={inEmptyState}
						primaryItems={primaryItems}
					/>
				)}
			</li>
		</ul>
	);
}

export default CreationMenu;
