/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React, {useEffect, useMemo, useState} from 'react';

import TagService from '../../../../common/services/TagService';
import PickerTrigger from '../PickerTrigger';
import {Item} from './FilterDropdown';
import {IAllFiltersDropdown, filterBySpaces, initialFilters} from './filters';

type Keyword = {assetLibraries: {id: number}[]; id: string; name: string};

interface IAllTagsDropdown extends IAllFiltersDropdown {
	cmsGroupId: string;
	depotEntryId: string;
}

const AllTagsDropdown: React.FC<IAllTagsDropdown> = ({
	className,
	cmsGroupId,
	depotEntryId,
	item,
	onSelectItem,
}) => {
	const [keywords, setKeywords] = useState<Keyword[]>([]);

	useEffect(() => {
		const fetchTags = async () => {
			const {data, error} = await TagService.getTags(cmsGroupId);

			if (error) {
				console.error(error);

				return;
			}

			if (data) {
				setKeywords(data.items);
			}
		};

		fetchTags();
	}, [cmsGroupId]);

	const tags: Item[] = useMemo(
		() => [
			initialFilters.tag,
			...keywords
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
		[keywords, depotEntryId]
	);

	return (
		<Picker
			aria-label={Liferay.Language.get('filter-by-tag')}
			as={PickerTrigger}
			borderless
			filterKey="label"
			items={tags}
			messages={{
				noResultsFound: Liferay.Language.get('no-results-were-found'),
				searchPlaceholder: Liferay.Language.get('search'),
			}}
			onSelectionChange={(key) => {
				const selectedTag = tags.find(
					({value}) => value === String(key)
				);

				if (selectedTag) {
					onSelectItem(selectedTag);
				}
			}}
			searchable
			selectedKey={item.value}
			triggerClassName={className}
			triggerIcon="tag"
		>
			{(tag: Item) => <Option key={tag.value}>{tag.label}</Option>}
		</Picker>
	);
};

export {AllTagsDropdown};
