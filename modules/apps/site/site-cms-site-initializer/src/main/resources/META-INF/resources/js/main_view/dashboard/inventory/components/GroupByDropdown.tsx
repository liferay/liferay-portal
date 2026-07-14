/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React from 'react';

import PickerTrigger from '../../common/PickerTrigger';
import {Item} from '../../common/filters/FilterDropdown';
import {IAllFiltersDropdown} from '../../common/filters/filters';

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
}) => (
	<Picker
		aria-label={Liferay.Language.get('group-by')}
		as={PickerTrigger}
		borderless
		items={defaultStructureTypes}
		onSelectionChange={(key) => {
			const selectedStructureType = defaultStructureTypes.find(
				({value}) => value === key
			);

			if (selectedStructureType) {
				onSelectItem(selectedStructureType);
			}
		}}
		selectedKey={item.value}
		triggerClassName={className}
	>
		{(structureType: Item) => (
			<Option key={structureType.value}>{structureType.label}</Option>
		)}
	</Picker>
);

export {GroupByDropdown};
