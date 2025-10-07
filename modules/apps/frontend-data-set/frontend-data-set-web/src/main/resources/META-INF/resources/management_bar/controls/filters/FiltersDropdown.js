/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import React, {useContext, useEffect, useState} from 'react';

import ViewsContext from '../../../views/ViewsContext';
import Filter from './Filter';

const FiltersDropdown = () => {
	const [{filters: initialFilters}] = useContext(ViewsContext);

	const [active, setActive] = useState(false);
	const [activeFilter, setActiveFilter] = useState(null);
	const [filters, setFilters] = useState(initialFilters);
	const [query, setQuery] = useState('');

	const onSearch = (query) => {
		setQuery(query);

		setFilters(
			query
				? initialFilters.filter(({label}) =>
						label.toLowerCase().match(query.toLowerCase())
					) || []
				: initialFilters
		);
	};

	useEffect(() => {
		setFilters(initialFilters);

		setActiveFilter((currentActiveFilter) => {
			if (!currentActiveFilter) {
				return null;
			}

			return (
				initialFilters.find(
					(filter) => filter.id === currentActiveFilter.id
				) || null
			);
		});
	}, [initialFilters, setActiveFilter, setFilters]);

	return (
		<ClayDropDown
			active={active}
			className="filters-dropdown"
			onActiveChange={setActive}
			trigger={
				<Button
					aria-expanded={active}
					borderless
					className="filters-dropdown-button nav-link"
					displayType="secondary"
					size="sm"
				>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="filter" />
					</span>

					<span className="navbar-text-truncate">
						{Liferay.Language.get('filter')}
					</span>

					<ClayIcon
						className="inline-item inline-item-after"
						symbol="caret-bottom"
					/>
				</Button>
			}
		>
			{activeFilter ? (
				<>
					<li className="dropdown-subheader">
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('back')}
							className="btn-filter-navigation"
							displayType="unstyled"
							onClick={() => {
								setActiveFilter(null);

								setFilters(initialFilters);
							}}
							size="sm"
							symbol="angle-left"
						/>

						{activeFilter.label}
					</li>

					<Filter
						{...activeFilter}
						onClose={() => setActive(false)}
					/>
				</>
			) : (
				<ClayDropDown.Group header={Liferay.Language.get('filters')}>
					<ClayDropDown.Search
						aria-label={Liferay.Language.get('search')}
						onChange={onSearch}
						role="none"
						value={query}
					/>

					<ClayDropDown.Divider className="m-0" />

					{filters.length ? (
						<ClayDropDown.ItemList>
							{filters
								.filter(
									(filter) =>
										!filter.clientExtensionResolutionError
								)
								.map((filter) => (
									<ClayDropDown.Item
										key={filter.id}
										onClick={() => {
											setActiveFilter(filter);
										}}
									>
										{filter.label}
									</ClayDropDown.Item>
								))}
						</ClayDropDown.ItemList>
					) : (
						<ClayDropDown.Caption>
							{Liferay.Language.get('no-filters-were-found')}
						</ClayDropDown.Caption>
					)}
				</ClayDropDown.Group>
			)}
		</ClayDropDown>
	);
};

export default FiltersDropdown;
