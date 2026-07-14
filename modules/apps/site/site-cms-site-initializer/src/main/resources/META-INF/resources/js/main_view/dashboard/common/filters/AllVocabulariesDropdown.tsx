/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {useEffect, useMemo, useState} from 'react';

import VocabularyService from '../../../../common/services/VocabularyService';
import PickerTrigger from '../PickerTrigger';
import {Item} from './FilterDropdown';
import {IAllFiltersDropdown, filterBySpaces, initialFilters} from './filters';

type Vocabulary = {assetLibraries: {id: number}[]; id: string; name: string};

interface IAllVocabulariesDropdown extends IAllFiltersDropdown {
	cmsGroupId: string;
	depotEntryId: string;
}

const AllVocabulariesDropdown: React.FC<IAllVocabulariesDropdown> = ({
	className,
	cmsGroupId,
	depotEntryId,
	item,
	onSelectItem,
}) => {
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
						depotEntryId === 'all' ||
						filterBySpaces(assetLibraries, depotEntryId)
				)
				.map(({id, name}) => ({
					label: name,
					value: String(id),
				})),
		],
		[rawVocabularies, depotEntryId]
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
