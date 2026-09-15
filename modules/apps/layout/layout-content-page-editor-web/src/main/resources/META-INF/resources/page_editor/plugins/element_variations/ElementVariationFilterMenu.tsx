/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import DropDown from '@clayui/drop-down';
import {ClayCheckbox, ClayToggle} from '@clayui/form';
import {useId} from 'frontend-js-components-web';
import React, {useState} from 'react';

import {
	FILTER_TYPES,
	Filter,
	FilterType,
	getFilterLabel,
	getFilterOptions,
} from './elementVariationFilters';

interface Props {
	audiences: Array<{label: string; value: string}>;
	className?: string;
	defaultFilterType?: FilterType | null;
	filters: Filter[];
	onActiveChange?: (active: boolean) => void;
	onAddFilter: (filter: Filter) => void;
	trigger: React.ReactElement;
}

export default function ElementVariationFilterMenu({
	audiences,
	className,
	defaultFilterType = null,
	filters,
	onActiveChange,
	onAddFilter,
	trigger,
}: Props) {
	const excludeId = useId();

	const [active, setActive] = useState(false);
	const [exclude, setExclude] = useState(false);
	const [filterType, setFilterType] = useState(defaultFilterType);
	const [search, setSearch] = useState('');
	const [values, setValues] = useState<string[]>([]);

	const openFilterType = (nextFilterType: FilterType) => {
		const filter = filters.find(({type}) => type === nextFilterType);

		setExclude(filter?.exclude ?? false);
		setFilterType(nextFilterType);
		setValues(filter?.values ?? []);
	};

	const filterTypes = FILTER_TYPES.filter((type) =>
		getFilterLabel(type).toLowerCase().includes(search.toLowerCase())
	);

	return (
		<DropDown
			active={active}
			className={className}
			closeOnClick={false}
			onActiveChange={(nextActive) => {
				if (nextActive) {
					setSearch('');

					if (defaultFilterType) {
						openFilterType(defaultFilterType);
					}
					else {
						setFilterType(null);
					}
				}

				setActive(nextActive);

				onActiveChange?.(nextActive);
			}}
			renderMenuOnClick
			trigger={trigger}
		>
			{!active ? null : filterType ? (
				<>
					<div className="align-items-center d-flex dropdown-header">
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('back')}
							className="component-action mr-2"
							displayType={null}
							onClick={() => setFilterType(null)}
							size="sm"
							symbol="angle-left"
						/>

						<span>{getFilterLabel(filterType)}</span>
					</div>

					<div className="dropdown-divider" />

					<div className="align-items-center d-flex dropdown-section justify-content-between">
						<label className="mb-0" htmlFor={excludeId}>
							{Liferay.Language.get('exclude')}
						</label>

						<ClayToggle
							id={excludeId}
							onToggle={setExclude}
							toggled={exclude}
						/>
					</div>

					<div className="dropdown-divider" />

					<DropDown.ItemList role="presentation">
						{getFilterOptions(filterType, audiences).map(
							(option) => (
								<DropDown.Section
									key={option.value}
									role="none"
								>
									<ClayCheckbox
										checked={values.includes(option.value)}
										label={option.label}
										onChange={() =>
											setValues((previousValues) =>
												previousValues.includes(
													option.value
												)
													? previousValues.filter(
															(value) =>
																value !==
																option.value
														)
													: [
															...previousValues,
															option.value,
														]
											)
										}
									/>
								</DropDown.Section>
							)
						)}
					</DropDown.ItemList>

					<div className="dropdown-footer">
						<ClayButton
							disabled={!values.length}
							displayType="primary"
							onClick={() => {
								onAddFilter({
									exclude,
									type: filterType,
									values,
								});

								setActive(false);
							}}
							size="sm"
						>
							{Liferay.Language.get('add-filter')}
						</ClayButton>
					</div>
				</>
			) : (
				<>
					<div className="dropdown-header">
						{Liferay.Language.get('filter')}
					</div>

					<DropDown.Search
						aria-label={Liferay.Language.get('search')}
						onChange={setSearch}
						value={search}
					/>

					<DropDown.ItemList>
						<DropDown.Group
							header={Liferay.Language.get('filter-by')}
						>
							{filterTypes.map((type) => (
								<DropDown.Item
									key={type}
									onClick={() => openFilterType(type)}
								>
									{getFilterLabel(type)}
								</DropDown.Item>
							))}
						</DropDown.Group>
					</DropDown.ItemList>
				</>
			)}
		</DropDown>
	);
}
