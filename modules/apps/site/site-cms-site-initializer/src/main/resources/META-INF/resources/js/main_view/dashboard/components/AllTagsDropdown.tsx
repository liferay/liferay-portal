/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {buildQueryString} from '@liferay/analytics-reports-js-components-web';
import React, {useContext, useState} from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import {ViewDashboardContext} from '../ViewDashboardContext';
import {FilterDropdown, Item} from './FilterDropdown';
import {
	IAllFiltersDropdown,
	filterBySpaces,
	initialFilters,
} from './InventoryAnalysisCard';

const AllTagsDropdown: React.FC<IAllFiltersDropdown> = ({
	className,
	item,
	onSelectItem,
}) => {
	const {
		filters: {space},
	} = useContext(ViewDashboardContext);

	const [tags, setTags] = useState<Item[]>([initialFilters.tag]);

	const [dropdownActive, setDropdownActive] = useState(false);
	const [loading, setLoading] = useState(false);

	const fetchTags = async (search: string = '') => {
		const queryParams = buildQueryString({
			search,
		});

		const endpoint = `/o/headless-admin-taxonomy/v1.0/keywords${queryParams}`;

		const {data, error} = await ApiHelper.get<{
			items: {assetLibraries: {id: number}[]; id: string; name: string}[];
		}>(endpoint);

		if (data) {
			return data.items
				.filter(({assetLibraries}) => {
					if (space.value === 'all') {
						return true;
					}

					return filterBySpaces(assetLibraries, space.value);
				})
				.map(({id, name}) => ({
					label: name,
					value: String(id),
				}));
		}

		if (error) {
			console.error(error);
		}

		return [];
	};

	return (
		<FilterDropdown
			active={dropdownActive}
			className={className}
			filterByValue="tags"
			icon="tag"
			items={tags}
			loading={loading}
			onActiveChange={() => setDropdownActive(!dropdownActive)}
			onSearch={async (value) => {
				setLoading(true);

				const tags = await fetchTags(value);

				setTags(value ? tags : [initialFilters.tag, ...tags]);

				setLoading(false);
			}}
			onSelectItem={(item) => {
				onSelectItem(item);

				setDropdownActive(false);
			}}
			onTrigger={async () => {
				setLoading(true);

				const tags = await fetchTags();

				setTags([initialFilters.tag, ...tags]);

				setLoading(false);
			}}
			selectedItem={item}
			title={Liferay.Language.get('filter-by-tag')}
		/>
	);
};

export {AllTagsDropdown};
