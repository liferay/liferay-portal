/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import DropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import React, {useContext, useEffect, useState} from 'react';

import {TSort} from '../../utils/types';
import ViewsContext from '../../views/ViewsContext';

// @ts-ignore

import {VIEWS_ACTION_TYPES} from '../../views/viewsReducer';

function SortDropdown() {
	const [{sorts}, viewsDispatch]: [{sorts: TSort[]}, Function] =
		useContext(ViewsContext);

	const activeSort = sorts?.find((sort: TSort) => sort.active);

	const [active, setActive] = useState(false);
	const [selectedDirection, setSelectedDirection] = useState<
		TSort['direction']
	>(activeSort?.direction ?? 'asc');
	const [selectedKey, setSelectedKey] = useState<TSort['key']>(
		activeSort?.key
	);

	useEffect(() => {
		const activeSort = sorts?.find((sort: TSort) => sort.active);

		if (activeSort) {
			setSelectedDirection(activeSort.direction);
			setSelectedKey(activeSort.key);
		}
	}, [sorts]);

	return (
		<DropDown
			active={active}
			closeOnClick
			hasLeftSymbols
			onActiveChange={setActive}
			trigger={
				<Button
					aria-expanded={active}
					borderless
					className="nav-link"
					displayType="secondary"
					size="sm"
				>
					<span className="inline-item inline-item-before">
						<ClayIcon
							symbol={
								selectedDirection === 'asc'
									? 'order-list-up'
									: 'order-list-down'
							}
						/>
					</span>

					{Liferay.Language.get('order[sort]')}

					<ClayIcon
						className="inline-item inline-item-after"
						symbol="caret-bottom"
					/>
				</Button>
			}
		>
			<DropDown.ItemList items={sorts}>
				{

					// @ts-ignore

					(sort: TSort) =>
						sort.label ? (
							<DropDown.Item
								key={sort.key}
								onClick={() => {
									setSelectedKey(sort.key);

									viewsDispatch({
										type: VIEWS_ACTION_TYPES.UPDATE_SORTING,
										value: sorts.map((sortItem) =>
											sort.key === sortItem.key
												? {...sortItem, active: true}
												: {...sortItem, active: false}
										),
									});
								}}
								symbolLeft={
									selectedKey === sort.key ? 'check' : ''
								}
							>
								{sort.label}
							</DropDown.Item>
						) : (
							<></>
						)
				}
			</DropDown.ItemList>

			<DropDown.Divider />

			<DropDown.ItemList>
				<DropDown.Item
					key="asc"
					onClick={() => {
						setSelectedDirection('asc');

						viewsDispatch({
							type: VIEWS_ACTION_TYPES.UPDATE_SORTING,
							value: sorts.map((sortItem) => ({
								...sortItem,
								direction: 'asc',
							})),
						});
					}}
					symbolLeft={selectedDirection === 'asc' ? 'check' : ''}
				>
					{Liferay.Language.get('ascending')}
				</DropDown.Item>

				<DropDown.Item
					key="desc"
					onClick={() => {
						setSelectedDirection('desc');

						viewsDispatch({
							type: VIEWS_ACTION_TYPES.UPDATE_SORTING,
							value: sorts.map((sortItem) => ({
								...sortItem,
								direction: 'desc',
							})),
						});
					}}
					symbolLeft={selectedDirection === 'desc' ? 'check' : ''}
				>
					{Liferay.Language.get('descending')}
				</DropDown.Item>
			</DropDown.ItemList>
		</DropDown>
	);
}

export default SortDropdown;
