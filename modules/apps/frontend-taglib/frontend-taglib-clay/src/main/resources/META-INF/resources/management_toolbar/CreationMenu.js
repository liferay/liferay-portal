/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBadge from '@clayui/badge';
import ClayDropDown from '@clayui/drop-down';
import classNames from 'classnames';
import {sub, unescapeHTML} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import getDataAttributes from '../get_data_attributes';
import LinkOrButton from './LinkOrButton';

import './CreationMenu.scss';

const Item = ({item, onClick}) => {
	return (
		<ClayDropDown.Item
			href={item.href}
			onClick={(event) => {
				onClick(event, {item});
			}}
			symbolLeft={item.icon}
			{...getDataAttributes(item.data)}
		>
			{item.deprecated ? (
				<span className="creation-menu-item-deprecated">
					{unescapeHTML(item.label)}

					<ClayBadge
						className="text-uppercase"
						displayType="warning"
						label={Liferay.Language.get('deprecated')}
						translucent
					/>
				</span>
			) : (
				unescapeHTML(item.label)
			)}
		</ClayDropDown.Item>
	);
};

const ItemList = ({
	onItemClick,
	primaryItems,
	secondaryItems,
	visibleItemsCount,
}) => {
	let currentItemCount = 0;

	return (
		<ClayDropDown.ItemList
			className={classNames({
				'dropdown-menu-indicator-start': primaryItems.some(
					(item) => item.icon
				),
			})}
			data-qa-id="dropdownMenu"
		>
			{primaryItems?.map((item, index) => {
				currentItemCount++;

				if (currentItemCount > visibleItemsCount) {
					return false;
				}

				return <Item item={item} key={index} onClick={onItemClick} />;
			})}

			{secondaryItems?.map((secondaryItemsGroup, index) => (
				<ClayDropDown.Group
					header={secondaryItemsGroup.label}
					key={index}
				>
					{secondaryItemsGroup.items.map((item, index) => {
						currentItemCount++;

						if (currentItemCount > visibleItemsCount) {
							return false;
						}

						return (
							<Item
								item={item}
								key={index}
								onClick={onItemClick}
							/>
						);
					})}

					{secondaryItemsGroup.separator && <ClayDropDown.Divider />}
				</ClayDropDown.Group>
			))}
		</ClayDropDown.ItemList>
	);
};

const CreationMenu = ({
	closeOnClick = false,
	maxPrimaryItems,
	maxSecondaryItems,
	maxTotalItems = 15,
	onCreateButtonClick,
	onCreationMenuItemClick,
	onShowMoreButtonClick,
	primaryItems,
	secondaryItems,
	viewMoreURL,
}) => {
	const [active, setActive] = useState(false);

	const secondaryItemsCountRef = useRef(
		secondaryItems?.reduce((acc, cur) => {
			return acc + cur.items.length;
		}, 0) ?? 0
	);

	const totalItemsCountRef = useRef(
		primaryItems.length + secondaryItemsCountRef.current
	);

	const firstItemRef = useRef(
		primaryItems?.[0] ||
			secondaryItems?.[0].items?.[0] ||
			secondaryItems?.[0]
	);

	const getPlusIconLabel = () => {
		const item =
			totalItemsCountRef.current === 1 ? firstItemRef.current : null;

		return item?.label || Liferay.Language.get('new');
	};

	const getVisibleItemsCount = () => {
		const primaryItemsCount = primaryItems.length;
		const secondaryItemsCount = secondaryItemsCountRef.current;

		const defaultMaxPrimaryItems = maxPrimaryItems
			? primaryItemsCount > maxPrimaryItems
				? maxPrimaryItems
				: primaryItemsCount
			: primaryItemsCount > 8
				? 8
				: primaryItemsCount;

		const tempDefaultMaxSecondaryItems = maxSecondaryItems
			? secondaryItemsCount > maxSecondaryItems
				? maxSecondaryItems
				: secondaryItemsCount
			: secondaryItemsCount > 7
				? 7
				: secondaryItemsCount;

		const defaultMaxSecondaryItems =
			tempDefaultMaxSecondaryItems >
			maxTotalItems - defaultMaxPrimaryItems
				? maxTotalItems - defaultMaxPrimaryItems
				: tempDefaultMaxSecondaryItems;

		return secondaryItemsCount === 0
			? primaryItemsCount > maxTotalItems
				? maxTotalItems
				: primaryItemsCount
			: primaryItemsCount > defaultMaxPrimaryItems
				? secondaryItemsCount > defaultMaxSecondaryItems
					? defaultMaxPrimaryItems + defaultMaxSecondaryItems
					: defaultMaxPrimaryItems + secondaryItemsCount
				: secondaryItemsCount > defaultMaxSecondaryItems
					? primaryItemsCount + defaultMaxSecondaryItems
					: primaryItemsCount + secondaryItemsCount;
	};

	const [visibleItemsCount, setVisibleItemsCount] = useState(
		getVisibleItemsCount()
	);

	return (
		<>
			{totalItemsCountRef.current > 1 ? (
				<ClayDropDown
					active={active}
					className="creation-menu"
					closeOnClick={closeOnClick}
					menuElementAttrs={{className: 'creation-menu-dropdown'}}
					onActiveChange={setActive}
					trigger={
						<LinkOrButton
							aria-label={getPlusIconLabel()}
							className="nav-btn"
							data-qa-id="creationMenuNewButton"
							symbol="plus"
							title={getPlusIconLabel()}
							wideViewportTitleVisible={false}
						>
							<span className="d-md-block d-none pl-3 pr-3">
								{getPlusIconLabel()}
							</span>
						</LinkOrButton>
					}
				>
					{visibleItemsCount < totalItemsCountRef.current ? (
						<>
							<div className="inline-scroller">
								<ItemList
									onItemClick={onCreationMenuItemClick}
									primaryItems={primaryItems}
									secondaryItems={secondaryItems}
									visibleItemsCount={visibleItemsCount}
								/>
							</div>

							<div className="dropdown-caption">
								{sub(
									Liferay.Language.get(
										'showing-x-of-x-elements'
									),
									visibleItemsCount,
									totalItemsCountRef.current
								)}
							</div>

							<div className="dropdown-section">
								<LinkOrButton
									button={{block: true}}
									displayType="secondary"
									href={viewMoreURL}
									onClick={() => {
										if (onShowMoreButtonClick) {
											onShowMoreButtonClick();

											return;
										}

										setVisibleItemsCount(
											totalItemsCountRef.current
										);
									}}
								>
									{Liferay.Language.get('more')}
								</LinkOrButton>
							</div>
						</>
					) : (
						<ItemList
							onItemClick={onCreationMenuItemClick}
							primaryItems={primaryItems}
							secondaryItems={secondaryItems}
							visibleItemsCount={totalItemsCountRef.current}
						/>
					)}
				</ClayDropDown>
			) : (
				<>
					<LinkOrButton
						aria-label={getPlusIconLabel()}
						button={true}
						className="nav-btn"
						data-qa-id="creationMenuNewButton"
						displayType="primary"
						href={firstItemRef.current.href}
						onClick={(event) => {
							onCreateButtonClick(event, {
								item: firstItemRef.current,
							});
						}}
						symbol="plus"
						title={getPlusIconLabel()}
						wide
						wideViewportTitleVisible={false}
					>
						{Liferay.Language.get('new')}
					</LinkOrButton>
				</>
			)}
		</>
	);
};

export default CreationMenu;
