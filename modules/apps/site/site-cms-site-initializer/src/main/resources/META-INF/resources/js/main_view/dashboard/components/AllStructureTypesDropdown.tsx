/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {buildQueryString} from '@liferay/analytics-reports-js-components-web';
import React, {useContext, useState} from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import getLocalizedValue from '../../../common/utils/getLocalizedValue';
import {ViewDashboardContext} from '../ViewDashboardContext';
import {FilterDropdown, Item} from './FilterDropdown';
import {IAllFiltersDropdown, initialFilters} from './InventoryAnalysisCard';

const AllStructureTypesDropdown: React.FC<IAllFiltersDropdown> = ({
	className,
	item,
	onSelectItem,
}) => {
	const {constants} = useContext(ViewDashboardContext);

	const [structures, setStructures] = useState<Item[]>([
		initialFilters.structure,
	]);
	const [loading, setLoading] = useState(false);
	const [dropdownActive, setDropdownActive] = useState(false);

	const fetchStructures = async (search: string = '') => {
		const queryParams = buildQueryString({
			filter: `(objectFolderExternalReferenceCode eq '${constants.ercContentStructures}' or objectFolderExternalReferenceCode eq '${constants.ercFileTypes}')`,
			search,
		});

		const endpoint = `/o/object-admin/v1.0/object-definitions${queryParams}`;

		const {data, error} = await ApiHelper.get<{
			items: {id: string; label: Record<string, string>}[];
		}>(endpoint);

		if (data) {
			return data.items.map(({id, label}) => ({
				label:
					getLocalizedValue(label) ||
					getLocalizedValue(label, 'en_US'),
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
			filterByValue="structures"
			icon="edit-layout"
			items={structures}
			loading={loading}
			onActiveChange={() => setDropdownActive(!dropdownActive)}
			onSearch={async (value) => {
				setLoading(true);

				const structures = await fetchStructures(value);

				setStructures(
					value
						? structures
						: [initialFilters.structure, ...structures]
				);

				setLoading(false);
			}}
			onSelectItem={(item) => {
				onSelectItem(item);

				setDropdownActive(false);
			}}
			onTrigger={async () => {
				setLoading(true);

				const structures = await fetchStructures();

				setStructures([initialFilters.structure, ...structures]);

				setLoading(false);
			}}
			selectedItem={item}
			title={Liferay.Language.get('filter-by-content-structure-type')}
		/>
	);
};

export {AllStructureTypesDropdown};
