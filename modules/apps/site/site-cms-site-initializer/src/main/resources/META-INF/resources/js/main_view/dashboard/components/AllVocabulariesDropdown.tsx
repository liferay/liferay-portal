/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import VocabularyService from '../../../common/services/VocabularyService';
import {DashboardsContext} from '../DashboardsContext';
import {Item} from './FilterDropdown';
import {
	IAllFiltersDropdown,
	filterBySpaces,
	initialFilters,
} from './InventoryAnalysisCard';
import PickerTrigger from './PickerTrigger';

type Vocabulary = {assetLibraries: {id: number}[]; id: string; name: string};

const AllVocabulariesDropdown: React.FC<IAllFiltersDropdown> = ({
	className,
	item,
	onSelectItem,
}) => {
	const {
		constants: {cmsGroupId},
		filters: {space},
	} = useContext(DashboardsContext);

	const [rawVocabularies, setRawVocabularies] = useState<Vocabulary[]>([]);

	useEffect(() => {
		const fetchVocabularies = async () => {
			const {data, error} =
				await VocabularyService.getVocabularies(cmsGroupId);

			if (error) {
				console.error(error);

				return;
			}

			if (data) {
				setRawVocabularies(data.items);
			}
		};

		fetchVocabularies();
	}, [cmsGroupId]);

	const vocabularies: Item[] = useMemo(
		() => [
			initialFilters.vocabulary,
			...rawVocabularies
				.filter(
					({assetLibraries}) =>
						space.value === 'all' ||
						filterBySpaces(assetLibraries, space.value)
				)
				.map(({id, name}) => ({
					label: name,
					value: String(id),
				})),
		],
		[rawVocabularies, space.value]
	);

	return (
		<Picker
			aria-label={Liferay.Language.get('filter-by-vocabulary')}
			as={PickerTrigger}
			borderless
			filterKey="label"
			items={vocabularies}
			messages={{
				noResultsFound: Liferay.Language.get('no-results-were-found'),
				searchPlaceholder: Liferay.Language.get('search'),
			}}
			onSelectionChange={(key) => {
				const selectedVocabulary = vocabularies.find(
					({value}) => value === String(key)
				);

				if (selectedVocabulary) {
					onSelectItem(selectedVocabulary);
				}
			}}
			searchable
			selectedKey={item.value}
			triggerClassName={className}
			triggerIcon="vocabulary"
		>
			{(vocabulary: Item) => (
				<Option key={vocabulary.value}>{vocabulary.label}</Option>
			)}
		</Picker>
	);
};

export {AllVocabulariesDropdown};
