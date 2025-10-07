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

const AllVocabulariesDropdown: React.FC<IAllFiltersDropdown> = ({
	className,
	item,
	onSelectItem,
}) => {
	const {
		constants: {cmsGroupId},
		filters: {space},
	} = useContext(ViewDashboardContext);

	const [vocabularies, setVocabularies] = useState<Item[]>([
		initialFilters.vocabulary,
	]);

	const [dropdownActive, setDropdownActive] = useState(false);
	const [loading, setLoading] = useState(false);

	const fetchVocabularies = async (search: string = '') => {
		const queryParams = buildQueryString({
			search,
		});

		const endpoint = `/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/taxonomy-vocabularies${queryParams}`;

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
			filterByValue="vocabularies"
			icon="vocabulary"
			items={vocabularies}
			loading={loading}
			onActiveChange={() => setDropdownActive(!dropdownActive)}
			onSearch={async (value) => {
				setLoading(true);

				const vocabularies = await fetchVocabularies(value);

				setVocabularies(
					value
						? vocabularies
						: [initialFilters.vocabulary, ...vocabularies]
				);

				setLoading(false);
			}}
			onSelectItem={(item) => {
				onSelectItem(item);

				setDropdownActive(false);
			}}
			onTrigger={async () => {
				setLoading(true);

				const vocabularies = await fetchVocabularies();

				setVocabularies([initialFilters.vocabulary, ...vocabularies]);

				setLoading(false);
			}}
			selectedItem={item}
			title={Liferay.Language.get('filter-by-vocabulary')}
		/>
	);
};

export {AllVocabulariesDropdown};
