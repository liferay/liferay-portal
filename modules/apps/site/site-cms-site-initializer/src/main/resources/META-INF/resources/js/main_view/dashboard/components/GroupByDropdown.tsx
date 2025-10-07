/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';

import {FilterDropdown, Item} from './FilterDropdown';
import {IAllFiltersDropdown} from './InventoryAnalysisCard';

const defaultStructureTypes: Item[] = [
	{
		label: Liferay.Language.get('category'),
		value: 'category',
	},
	{
		label: Liferay.Language.get('vocabulary'),
		value: 'vocabulary',
	},
	{
		label: Liferay.Language.get('tag'),
		value: 'tag',
	},
	{
		label: Liferay.Language.get('content-structure-label'),
		value: 'structure',
	},
];

const GroupByDropdown: React.FC<IAllFiltersDropdown> = ({
	className,
	item,
	onSelectItem,
}) => {
	const [dropdownActive, setDropdownActive] = useState(false);

	return (
		<FilterDropdown
			active={dropdownActive}
			className={className}
			filterByValue="structureTypes"
			items={defaultStructureTypes}
			loading={false}
			onActiveChange={() => setDropdownActive(!dropdownActive)}
			onSelectItem={(item) => {
				onSelectItem(item);
				setDropdownActive(false);
			}}
			selectedItem={item}
			showLabelInSmallViewport
		/>
	);
};

export {GroupByDropdown};
