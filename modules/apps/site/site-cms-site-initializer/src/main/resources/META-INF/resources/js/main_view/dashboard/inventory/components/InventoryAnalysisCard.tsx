/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker, Text} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import {buildQueryString} from '@liferay/analytics-reports-js-components-web';
import React, {useContext, useEffect, useState} from 'react';

import ApiHelper from '../../../../common/services/ApiHelper';
import {BaseCard} from '../../common/BaseCard';
import PickerTrigger from '../../common/PickerTrigger';
import {AllCategoriesDropdown} from '../../common/filters/AllCategoriesDropdown';
import {AllStructureTypesDropdown} from '../../common/filters/AllStructureTypesDropdown';
import {AllTagsDropdown} from '../../common/filters/AllTagsDropdown';
import {AllVocabulariesDropdown} from '../../common/filters/AllVocabulariesDropdown';
import {Item} from '../../common/filters/FilterDropdown';
import {initialFilters as baseFilters} from '../../common/filters/filters';
import {InventoryContext} from '../InventoryContext';
import usePagination from '../utils/usePagination';
import {GroupByDropdown} from './GroupByDropdown';
import PaginatedTable from './PaginatedTable';

export type InventoryAnalysisDataType = {
	inventoryAnalysisItems: {count: number; key: string; title: string}[];
	inventoryAnalysisItemsCount: number;
	page: number;
	pageSize: number;
	totalCount: number;
};

const initialFilters = {
	...baseFilters,
	structureType: {
		label: Liferay.Language.get('category'),
		value: 'category',
	},
};

async function fetchStructureData({
	filters,
	language,
	page,
	pageSize,
	space,
}: {
	filters: {
		category: Item;
		structure: Item;
		structureType: Item;
		tag: Item;
		vocabulary: Item;
	};
	language: Item;
	page: number;
	pageSize: number;
	space: Item;
}) {
	const queryParams = buildQueryString(
		{
			categoryId: filters.category?.value,
			depotEntryId: space?.value,
			groupBy: filters.structureType?.value,
			languageId: language?.value,
			page: page.toString(),
			pageSize: pageSize.toString(),
			structureId: filters.structure?.value,
			tagId: filters.tag?.value,
			vocabularyId: filters.vocabulary?.value,
		},
		{
			shouldIgnoreParam: (value) => value === 'all',
		}
	);

	const endpoint = `/o/analytics-cms-rest/v1.0/inventory-analysis${queryParams}`;

	const {data, error} =
		await ApiHelper.get<InventoryAnalysisDataType>(endpoint);

	if (error) {
		console.error(error);
	}

	if (data) {
		return data;
	}

	return null;
}

type DropdownItem = {
	icon: string;
	name: string;
	value: 'chart' | 'table';
};

const dropdownItems: DropdownItem[] = [
	{
		icon: 'polls',
		name: Liferay.Language.get('chart[noun]'),
		value: 'chart',
	},
	{
		icon: 'table',
		name: Liferay.Language.get('table'),
		value: 'table',
	},
];

export function InventoryAnalysisCard() {
	const {
		constants,
		filters: {language, space},
	} = useContext(InventoryContext);

	const [filters, setFilters] = useState<{
		category: Item;
		structure: Item;
		structureType: Item;
		tag: Item;
		vocabulary: Item;
	}>(initialFilters);

	const [inventoryAnalysisData, setInventoryAnalysisData] =
		useState<InventoryAnalysisDataType>();

	const [selectedItem, setSelectedItem] = useState<DropdownItem>(
		dropdownItems[0]
	);

	const {handleDeltaChange, handlePageChange, pagination} = usePagination();

	useEffect(() => {
		setFilters(initialFilters);
	}, [space?.value]);

	useEffect(() => {
		async function fetchData() {
			const data = await fetchStructureData({
				filters,
				language,
				page: pagination.page,
				pageSize: pagination.pageSize,
				space,
			});

			if (data) {
				setInventoryAnalysisData(data);
			}
		}

		fetchData();
	}, [filters, language, pagination, space]);

	return (
		<div className="cms-dashboard__inventory-analysis mb-3">
			<BaseCard
				Preferences={
					<Picker
						aria-label={selectedItem.name}
						as={PickerTrigger}
						items={dropdownItems}
						onSelectionChange={(key) => {
							const item = dropdownItems.find(
								({value}) => value === key
							);

							if (item) {
								setSelectedItem(item);
							}
						}}
						selectedKey={selectedItem.value}
						triggerIcon={selectedItem.icon}
					>
						{(item: DropdownItem) => (
							<Option key={item.value} textValue={item.name}>
								<ClayIcon className="mr-2" symbol={item.icon} />

								{item.name}
							</Option>
						)}
					</Picker>
				}
				ariaLevel={3}
				description={Liferay.Language.get(
					'this-report-provides-a-breakdown-of-total-assets-by-categorization,-content-structure-type,-or-space'
				)}
				role="heading"
				title={Liferay.Language.get('inventory-analysis')}
			>
				<div className="align-items-lg-center d-flex flex-column flex-lg-row">
					<div className="align-items-center d-flex mb-2 mb-md-0 mr-md-4">
						<span className="mr-2">
							<Text size={3} weight="semi-bold">
								{Liferay.Language.get('group-by')}
							</Text>
						</span>

						<GroupByDropdown
							item={filters.structureType}
							onSelectItem={(structureType) =>
								setFilters({...filters, structureType})
							}
						/>
					</div>

					<div className="d-flex flex-md-row flex-row flex-xs-column">
						<div className="align-items-center d-flex mb-2 mb-lg-0 mr-lg-3">
							<span className="align-self-lg-auto align-self-start mr-2">
								<Text size={3} weight="semi-bold">
									{Liferay.Language.get('filter-by')}
								</Text>
							</span>
						</div>

						<div className="d-flex flex-wrap">
							<div className="mb-2 mb-lg-0 mr-2">
								<AllStructureTypesDropdown
									ercContentStructures={
										constants.ercContentStructures
									}
									ercFileTypes={constants.ercFileTypes}
									item={filters.structure}
									onSelectItem={(structure) =>
										setFilters({
											...filters,
											structure,
										})
									}
								/>
							</div>

							<div className="mb-2 mb-lg-0 mr-2">
								<AllVocabulariesDropdown
									cmsGroupId={constants.cmsGroupId}
									depotEntryId={space.value}
									item={filters.vocabulary}
									onSelectItem={(vocabulary) => {
										setFilters({
											...filters,
											vocabulary,
										});
									}}
								/>
							</div>

							<div className="mb-2 mb-lg-0 mr-2">
								<AllCategoriesDropdown
									cmsGroupId={constants.cmsGroupId}
									depotEntryId={space.value}
									item={filters.category}
									onSelectItem={(category) => {
										setFilters({
											...filters,
											category,
										});
									}}
								/>
							</div>

							<div className="mb-2 mb-lg-0">
								<AllTagsDropdown
									cmsGroupId={constants.cmsGroupId}
									depotEntryId={space.value}
									item={filters.tag}
									onSelectItem={(tag) =>
										setFilters({
											...filters,
											tag,
										})
									}
								/>
							</div>
						</div>
					</div>
				</div>

				{!inventoryAnalysisData ||
				inventoryAnalysisData.totalCount === 0 ? (
					<ClayEmptyState
						className="cms-dashboard__empty-state"
						description={Liferay.Language.get(
							'there-are-no-assets-created-in-the-spaces'
						)}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state.svg`}
						imgSrcReducedMotion={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state.svg`}
						title={Liferay.Language.get('no-assets-yet')}
					/>
				) : (
					<PaginatedTable
						currentStructureTypeLabel={filters.structureType.label}
						deltas={pagination.deltas}
						handleDeltaChange={handleDeltaChange}
						handlePageChange={handlePageChange}
						inventoryAnalysisData={inventoryAnalysisData}
						pagination={pagination}
						viewType={selectedItem.value}
					/>
				)}
			</BaseCard>
		</div>
	);
}
