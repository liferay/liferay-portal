/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import {buildQueryString} from '@liferay/analytics-reports-js-components-web';
import React, {useEffect, useState} from 'react';

import ApiHelper from '../../../../common/services/ApiHelper';
import getLocalizedValue from '../../../../common/utils/getLocalizedValue';
import PickerTrigger from '../PickerTrigger';
import {Item} from './FilterDropdown';
import {IAllFiltersDropdown, initialFilters} from './filters';

interface IAllStructureTypesDropdown extends IAllFiltersDropdown {
	ercContentStructures: string;
	ercFileTypes: string;
}

const AllStructureTypesDropdown: React.FC<IAllStructureTypesDropdown> = ({
	className,
	ercContentStructures,
	ercFileTypes,
	item,
	onSelectItem,
}) => {
	const [structures, setStructures] = useState<Item[]>([
		initialFilters.structure,
	]);

	useEffect(() => {
		const fetchStructures = async () => {
			const queryParams = buildQueryString({
				filter: `(objectFolderExternalReferenceCode eq '${ercContentStructures}' or objectFolderExternalReferenceCode eq '${ercFileTypes}')`,
			});

			const {data, error} = await ApiHelper.get<{
				items: {id: string; label: Record<string, string>}[];
			}>(`/o/object-admin/v1.0/object-definitions${queryParams}`);

			if (error) {
				console.error(error);

				return;
			}

			if (data) {
				setStructures([
					initialFilters.structure,
					...data.items.map(({id, label}) => ({
						label:
							getLocalizedValue(label) ||
							getLocalizedValue(label, 'en_US'),
						value: String(id),
					})),
				]);
			}
		};

		fetchStructures();
	}, [ercContentStructures, ercFileTypes]);

	return (
		<Picker
			aria-label={Liferay.Language.get(
				'filter-by-content-structure-type'
			)}
			as={PickerTrigger}
			borderless
			filterKey="label"
			items={structures}
			messages={{
				noResultsFound: Liferay.Language.get('no-results-were-found'),
				searchPlaceholder: Liferay.Language.get('search'),
			}}
			onSelectionChange={(key) => {
				const selectedStructure = structures.find(
					({value}) => value === String(key)
				);

				if (selectedStructure) {
					onSelectItem(selectedStructure);
				}
			}}
			searchable
			selectedKey={item.value}
			triggerClassName={className}
			triggerIcon="edit-layout"
		>
			{(structure: Item) => (
				<Option key={structure.value}>{structure.label}</Option>
			)}
		</Picker>
	);
};

export {AllStructureTypesDropdown};
