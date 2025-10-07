/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {LinkOrButton} from '@clayui/shared';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {Ref, useContext, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import {ACTION_ITEM_TARGETS} from '../../utils/actionItems/constants';
import {triggerAction} from '../../utils/actionItems/triggerAction';
import {ICreationActionItem} from '../../utils/types';
import {useWindowSize} from '../../utils/useWindowSize';

const MEDIUM_BREAKPOINT = 768;

const DropdownTrigger = React.forwardRef(
	(
		{inEmptyState, ...otherProps}: {inEmptyState: boolean},
		ref: Ref<HTMLButtonElement>
	) => {
		const {width} = useWindowSize();

		if (inEmptyState || width > MEDIUM_BREAKPOINT) {
			return (
				<ClayButton
					{...otherProps}
					className={!inEmptyState ? 'nav-btn px-3' : undefined}
					data-testid="fdsCreationActionButton"
					displayType={inEmptyState ? 'secondary' : 'primary'}
					ref={ref}
				>
					<span className="inline-item-before">
						{Liferay.Language.get('new')}
					</span>

					<span className="d-inline-flex inline-item-after">
						<ClayIcon symbol="caret-bottom" />
					</span>
				</ClayButton>
			);
		}
		else {
			return (
				<ClayButtonWithIcon
					{...otherProps}
					aria-label={Liferay.Language.get('new')}
					className="nav-btn nav-btn-monospaced"
					data-testid="fdsCreationActionButton"
					ref={ref}
					symbol="plus"
					title={Liferay.Language.get('new')}
				/>
			);
		}
	}
);

const DropDown = ({
	inEmptyState,
	primaryItems,
}: {
	inEmptyState: boolean;
	primaryItems: Array<ICreationActionItem>;
}) => {
	const frontendDataSetContext = useContext(FrontendDataSetContext);

	const {loadData} = frontendDataSetContext;

	const [active, setActive] = useState(false);

	return (
		<ClayDropDown
			active={active}
			onActiveChange={setActive}
			trigger={<DropdownTrigger inEmptyState={inEmptyState} />}
		>
			<ClayDropDown.ItemList>
				{primaryItems.map((item, i) => (
					<ClayDropDown.Item
						key={i}
						onClick={(event) => {
							event.preventDefault();

							setActive(false);

							item.onClick?.({
								loadData,
							});

							if (item.href || item.target) {
								triggerAction(item, frontendDataSetContext);
							}
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

	const {loadData} = frontendDataSetContext;

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

			firstItem.onClick?.({
				loadData,
			});

			if (firstItem.href || firstItem.target) {
				triggerAction(firstItem, frontendDataSetContext);
			}
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
		<LinkOrButton {...sharedProps} className="btn btn-secondary">
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
			className={classNames('btn btn-primary nav-btn', {
				['nav-btn-monospaced']: isMobile,
				['px-3']: !isMobile,
			})}
			title={firstItem.label}
		>
			{isMobile ? (
				<ClayIcon symbol="plus" />
			) : (
				Liferay.Language.get('new')
			)}

			{newTabIcon}
		</LinkOrButton>
	);
}

function CreationMenu({
	inEmptyState,
	primaryItems,
}: {
	inEmptyState: boolean;
	primaryItems: Array<ICreationActionItem>;
}) {
	if (primaryItems?.length === 0) {
		return null;
	}

	return (
		<ul
			className={classNames('navbar-nav', {
				'd-inline-flex': inEmptyState,
			})}
		>
			<li className="nav-item">
				{primaryItems.length > 1 ? (
					<DropDown
						inEmptyState={inEmptyState}
						primaryItems={primaryItems}
					/>
				) : (
					<CreationButton
						firstItem={primaryItems[0]}
						inEmptyState={inEmptyState}
					/>
				)}
			</li>
		</ul>
	);
}

export default CreationMenu;
