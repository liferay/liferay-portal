/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback, useContext, useEffect, useState} from 'react';

import ApiHelper from '../../../services/ApiHelper';
import {ViewDashboardContext} from '../ViewDashboardContext';
import {buildQueryString} from '../utils/buildQueryString';
import {FilterDropdown, Item} from './FilterDropdown';
import {
	IAllFiltersDropdown,
	initialStructureType,
} from './InventoryAnalysisCard';

export interface IStructureProps {
	items: {count: number; key: string; title: string}[];
	totalCount: number;
}

interface IGroupByDropdown extends IAllFiltersDropdown {
	setStructureTypeData: (value: IStructureProps) => void;
}

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
		label: Liferay.Language.get('structure-label'),
		value: 'structure',
	},
];

const GroupByDropdown: React.FC<IGroupByDropdown> = ({
	className,
	item,
	onSelectItem,
	setStructureTypeData,
}) => {
	const {
		filters: {language, space},
	} = useContext(ViewDashboardContext);

	const [dropdownActive, setDropdownActive] = useState(false);

	const fetchStructureData = useCallback(
		async (groupByValue: string) => {
			const queryParams = buildQueryString({
				groupBy: groupByValue,
				languageId: language.value,
				spaceId: space.value,
			});

			const endpoint = `/o/analytics-cms-rest/v1.0/inventory-analysis${queryParams}`;

			const {data, error} =
				await ApiHelper.get<IStructureProps>(endpoint);

			if (data) {
				setStructureTypeData({...data});
			}

			if (error) {
				console.error(error);
			}
		},
		[space.value, language.value, setStructureTypeData]
	);

	useEffect(() => {
		fetchStructureData(item.value);
	}, [fetchStructureData, item.value]);

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
				fetchStructureData(item.value || initialStructureType.value);
			}}
			selectedItem={item}
			showLabelInSmallViewport
		/>
	);
};

export {GroupByDropdown};
