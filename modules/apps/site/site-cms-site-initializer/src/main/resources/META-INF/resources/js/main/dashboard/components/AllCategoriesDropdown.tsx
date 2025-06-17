/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useState} from 'react';

import ApiHelper from '../../../services/ApiHelper';
import {ViewDashboardContext} from '../ViewDashboardContext';
import {buildQueryString} from '../utils/buildQueryString';
import {FilterDropdown} from './FilterDropdown';
import {IAllFiltersDropdown} from './InventoryAnalysisCard';

const initialCategory = {
	label: Liferay.Language.get('all-categories'),
	value: 'all',
};

export type CategoryData = {
	assetLibraries: {id: number}[];
	id: number;
	name: string;
	numberOfTaxonomyCategories: number;
	parentTaxonomyVocabulary?: {
		id: number;
		name: string;
	};
};

type Category = {
	hasChildren?: boolean;
	label: string;
	parent?: {
		label: string;
		value: string;
	};
	value: string;
};

enum Context {
	Categories = 'categories',
	Vocabularies = 'vocabularies',
}

const AllCategoriesDropdown: React.FC<IAllFiltersDropdown> = ({
	item,
	onSelectItem,
}) => {
	const {
		filters: {space},
	} = useContext(ViewDashboardContext);

	const [categories, setCategories] = useState([initialCategory]);
	const [vocabularies, setVocabularies] = useState([initialCategory]);
	const [parentCategory, setParentCategory] = useState<Category | null>(null);
	const [loading, setLoading] = useState(false);
	const [dropdownActive, setDropdownActive] = useState(false);
	const [context, setContext] = useState<Context>(Context.Vocabularies);

	const fetchData = async (url: string) => {
		setLoading(true);

		const {data, error} = await ApiHelper.get<{items: CategoryData[]}>(url);

		setLoading(false);

		if (error) {
			console.error(error);

			return null;
		}

		return data;
	};

	const fetchVocabularies = async ({keywords} = {keywords: ''}) => {
		const queryParams = buildQueryString({
			search: keywords,
		});

		const data = await fetchData(
			`/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies${queryParams}`
		);

		const filteredData = data?.items
			.filter(({assetLibraries, numberOfTaxonomyCategories}) => {
				if (numberOfTaxonomyCategories === 0) {
					return false;
				}

				if (space.value === 'all') {
					return true;
				}

				// TODO - Fix it:
				// Decreasing 1 on id due a bug on response

				return assetLibraries.some(
					({id}) => String(id - 1) === space.value
				);
			})
			.map(({id, name, numberOfTaxonomyCategories}) => {
				const category: Category = {
					hasChildren: numberOfTaxonomyCategories > 0,
					label: name,
					value: id.toString(),
				};

				return category;
			});

		return filteredData ?? [];
	};

	const fetchCategories = async (
		item: Category,
		{keywords} = {keywords: ''}
	) => {
		const queryParams = buildQueryString({
			search: keywords,
		});

		const isSubcategory = !!item.parent;

		const endpoint = isSubcategory
			? `/o/headless-admin-taxonomy/v1.0/taxonomy-categories/${item.value}/taxonomy-categories${queryParams}`
			: `/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${item.value}/taxonomy-categories${queryParams}`;

		const data = await fetchData(endpoint);

		const categories = data?.items.map(
			({id, name, numberOfTaxonomyCategories}) => {
				const category: Category = {
					label: name,
					value: id.toString(),
				};

				if (numberOfTaxonomyCategories > 0) {
					category.hasChildren = true;
					category.parent = {
						label: item.label,
						value: item.value,
					};
				}

				return category;
			}
		);

		if (!isSubcategory && data?.items[0]?.parentTaxonomyVocabulary) {
			const {id, name} = data.items[0].parentTaxonomyVocabulary;

			setParentCategory({
				label: name,
				value: id.toString(),
			});
		}

		return categories ?? [];
	};

	return (
		<FilterDropdown
			active={dropdownActive}
			cancelLabel={parentCategory?.label}
			filterByValue="categories"
			icon="categories"
			items={categories}
			loading={loading}
			onActiveChange={() => {
				setDropdownActive(false);
				setParentCategory(null);
			}}
			onCancel={() => {
				setCategories(vocabularies);
				setParentCategory(null);
				setContext(Context.Vocabularies);
			}}
			onSearch={async (keywords) => {
				if (context === Context.Vocabularies) {
					const data = await fetchVocabularies({keywords});

					const categories = !keywords
						? [initialCategory, ...data]
						: data;

					setCategories(categories);
					setVocabularies(categories);
				}
				else if (context === Context.Categories) {
					const categories = await fetchCategories(
						parentCategory || item,
						{keywords}
					);

					setCategories(categories);
				}
			}}
			onSelectItem={async (item: Category) => {
				if (item.hasChildren) {
					const categories = await fetchCategories(item);

					setCategories(categories);
					setContext(Context.Categories);
				}
				else {
					onSelectItem(item);
					setParentCategory(null);
					setDropdownActive(false);
					setContext(Context.Vocabularies);
				}
			}}
			onTrigger={async () => {
				const data = await fetchVocabularies();

				const categories = [initialCategory, ...data];

				setCategories(categories);
				setVocabularies(categories);

				setDropdownActive(true);
				setContext(Context.Vocabularies);
			}}
			selectedItem={item}
			title={Liferay.Language.get('filter-by-category')}
		/>
	);
};

export {AllCategoriesDropdown};
