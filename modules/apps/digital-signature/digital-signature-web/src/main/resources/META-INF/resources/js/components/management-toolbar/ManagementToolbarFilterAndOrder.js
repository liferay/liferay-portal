/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {ManagementToolbar} from 'frontend-js-components-web';
import React, {useContext, useState} from 'react';

import DropDown, {CheckboxGroup, ItemsGroup, RadioGroup} from './DropDown';
import SearchContext from './SearchContext';

const getSortable = (columns, sort = '', defaultSort) => {
	if (sort.length) {
		const [column, order] = sort.split(':');

		return {asc: order === 'asc', column};
	}
	else if (columns.length) {
		const {asc = defaultSort === 'asc', key: column} =
			columns.find(({asc}) => asc !== undefined) || columns[0];

		return {asc, column};
	}

	return {};
};

export default function ManagementToolbarFilterAndOrder({
	columns = [],
	disabled,
	filters = [],
}) {
	const [{filters: appliedFilters = {}, defaultSort, sort}, dispatch] =
		useContext(SearchContext);
	const [localFilters, setLocalFilters] = useState(appliedFilters);
	const [isDropDownActive, setDropDownActive] = useState(false);

	const sortableColumns = columns.filter(({sortable}) => sortable);

	const {asc, column} = getSortable(sortableColumns, sort, defaultSort);
	const [sortColumn, setSortColumn] = useState(column);

	const filterItems = filters.map(
		({defaultText, items, key, multiple, name}) => {
			const props = {
				checked: localFilters[key],
				items,
				label: name,
			};

			if (multiple) {
				return (
					<CheckboxGroup
						{...props}
						key={key}
						onAdd={(value) => {
							setLocalFilters((prevFilters) => {
								const values = prevFilters[key] || [];

								return {
									...prevFilters,
									[key]: values.concat(value),
								};
							});
						}}
						onRemove={(value) => {
							setLocalFilters((prevFilters) => ({
								...prevFilters,
								[key]: prevFilters[key].filter(
									(currentValue) => currentValue !== value
								),
							}));
						}}
					/>
				);
			}
			else {
				return (
					<RadioGroup
						{...props}
						items={[
							{
								label:
									defaultText || Liferay.Language.get('any'),
							},
							...props.items,
						]}
						key={key}
						onChange={(value) => {
							setLocalFilters((prevFilters) => ({
								...prevFilters,
								[key]: value,
							}));
						}}
					/>
				);
			}
		}
	);

	const enableDoneButton = !!filterItems.length;

	const onSortButtonClick = (asc, newColumn) => {
		dispatch({
			sort: `${newColumn}:${asc ? 'asc' : 'desc'}`,
			type: 'SORT',
		});
	};

	const orderByItems = () => {
		if (!sortableColumns.length) {
			return [];
		}

		const props = {
			checked: sortColumn,
			items: sortableColumns.map(({key, value}) => ({
				label: value,
				value: key,
			})),
			label: Liferay.Language.get('order-by'),
		};

		let item = <RadioGroup {...props} onChange={setSortColumn} />;

		if (!enableDoneButton) {
			item = (
				<ItemsGroup
					{...props}
					onClick={(newColumn) => {
						setSortColumn(newColumn);
						onSortButtonClick(asc, newColumn);
						setDropDownActive(false);
					}}
				/>
			);
		}

		return [item];
	};

	const dropDownItems = [...filterItems, ...orderByItems()];

	const onDropDownActiveChange = (active) => {
		setDropDownActive(active);
		setLocalFilters(appliedFilters);
		setSortColumn(column);
	};

	const onDoneButtonClick = () => {
		dispatch({
			filters: localFilters,
			sort: `${sortColumn}:${asc ? 'asc' : 'desc'}`,
			type: 'UPDATE_FILTERS_AND_SORT',
		});

		setDropDownActive(false);
	};

	return (
		<>
			{!!dropDownItems.length && (
				<ManagementToolbar.ItemList>
					<ManagementToolbar.Item>
						<DropDown
							active={isDropDownActive}
							footerContent={
								enableDoneButton && (
									<ClayButton
										block
										onClick={onDoneButtonClick}
									>
										{Liferay.Language.get('done')}
									</ClayButton>
								)
							}
							onActiveChange={onDropDownActiveChange}
							trigger={
								<ClayButton
									className="nav-link"
									disabled={disabled}
									displayType="unstyled"
									id="filter-and-order"
								>
									<span className="navbar-breakpoint-down-d-none">
										{Liferay.Language.get(
											'filter-and-order'
										)}

										<ClayIcon
											className="inline-item inline-item-after"
											symbol="caret-bottom"
										/>
									</span>

									<span className="navbar-breakpoint-d-none">
										<ClayIcon
											className="inline-item inline-item-after"
											symbol="filter"
										/>
									</span>
								</ClayButton>
							}
						>
							{dropDownItems.map((item, index) => (
								<div key={index}>{item}</div>
							))}
						</DropDown>
					</ManagementToolbar.Item>

					<ManagementToolbar.Item>
						<ClayButtonWithIcon
							className={classNames(
								'nav-link',
								'nav-link-monospaced',
								{
									'order-arrow-down-active': !asc,
									'order-arrow-up-active': asc,
								}
							)}
							disabled={disabled}
							displayType="unstyled"
							onClick={() => onSortButtonClick(!asc, column)}
							symbol="order-arrow"
							title={Liferay.Language.get(
								'reverse-sort-direction'
							)}
						/>
					</ManagementToolbar.Item>
				</ManagementToolbar.ItemList>
			)}
		</>
	);
}
