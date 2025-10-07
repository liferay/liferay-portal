/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayDropdown from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import {buildQueryString} from '@liferay/analytics-reports-js-components-web';
import React, {useContext, useEffect, useRef, useState} from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import {ViewDashboardContext} from '../ViewDashboardContext';
import usePagination from '../utils/usePagination';
import {AllCategoriesDropdown} from './AllCategoriesDropdown';
import {AllStructureTypesDropdown} from './AllStructureTypesDropdown';
import {AllTagsDropdown} from './AllTagsDropdown';
import {AllVocabulariesDropdown} from './AllVocabulariesDropdown';
import {BaseCard} from './BaseCard';
import {Item} from './FilterDropdown';
import {GroupByDropdown} from './GroupByDropdown';
import PaginatedTable from './PaginatedTable';

export interface IAllFiltersDropdown extends React.HTMLAttributes<HTMLElement> {
	item: Item;
	onSelectItem: (item: Item) => void;
}

export type InventoryAnalysisDataType = {
	inventoryAnalysisItems: {count: number; key: string; title: string}[];
	page: number;
	pageSize: number;
	totalCount: number;
};

export const initialFilters = {
	category: {
		label: Liferay.Language.get('all-categories'),
		value: 'all',
	},
	structure: {
		label: Liferay.Language.get('all-content-structures'),
		value: 'all',
	},
	structureType: {
		label: Liferay.Language.get('category'),
		value: 'category',
	},
	tag: {
		label: Liferay.Language.get('all-tags'),
		value: 'all',
	},
	vocabulary: {
		label: Liferay.Language.get('all-vocabularies'),
		value: 'all',
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

export function filterBySpaces(
	assetLibraries: {id: number}[],
	depotEntryId: string
) {
	return assetLibraries.some(({id}) => {

		// Returns true if id belongs to all spaces (-1).

		if (id === -1) {
			return true;
		}

		// Decreasing -1 due a bug where response is increasing +1 in the id.
		// Returns true if match id with id from space.

		return String(id - 1) === depotEntryId;
	});
}

type DropdownItem = {
	icon: string;
	name: string;
	value: 'chart' | 'table';
};

const dropdownItems: DropdownItem[] = [
	{
		icon: 'polls',
		name: Liferay.Language.get('chart'),
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
		filters: {language, space},
	} = useContext(ViewDashboardContext);

	const [filters, setFilters] = useState<{
		category: Item;
		structure: Item;
		structureType: Item;
		tag: Item;
		vocabulary: Item;
	}>(initialFilters);

	const [inventoryAnalysisData, setInventoryAnalysisData] =
		useState<InventoryAnalysisDataType>();

	const [dropdownActive, setDropdownActive] = useState(false);

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

	const triggerRef = useRef<HTMLButtonElement | null>(null);

	return (
		<div className="cms-dashboard__inventory-analysis mb-3">
			<BaseCard
				Preferences={
					<ClayDropdown
						active={dropdownActive}
						closeOnClickOutside={true}
						onActiveChange={setDropdownActive}
						trigger={
							<ClayButton
								aria-label={selectedItem.name}
								borderless={true}
								displayType="secondary"
								onClick={() => {
									setDropdownActive(!dropdownActive);
								}}
								ref={(node: HTMLButtonElement) => {
									triggerRef.current = node;
								}}
								size="sm"
							>
								{selectedItem.icon && (
									<ClayIcon
										className="mr-2"
										symbol={selectedItem.icon}
									/>
								)}

								<Text weight="semi-bold">
									{selectedItem.name}
								</Text>

								<ClayIcon
									className="mx-2"
									symbol="caret-bottom"
								/>
							</ClayButton>
						}
					>
						{dropdownItems.map((item) => (
							<ClayDropdown.Item
								active={item.value === selectedItem.value}
								key={item.value}
								onClick={() => {
									setSelectedItem(item);
									setDropdownActive(false);
									triggerRef?.current?.focus();
								}}
							>
								<div className="align-items-center d-flex">
									{item.value === selectedItem.value ? (
										<ClayIcon
											className="mr-2"
											symbol="check"
										/>
									) : (
										<ClayIcon className="mr-2" symbol="" />
									)}

									{item.icon && (
										<ClayIcon
											className="mr-2"
											symbol={item.icon}
										/>
									)}

									{item.name}
								</div>
							</ClayDropdown.Item>
						))}
					</ClayDropdown>
				}
				description={Liferay.Language.get(
					'this-report-provides-a-breakdown-of-total-assets-by-categorization,-content-structure-type,-or-space'
				)}
				title={Liferay.Language.get('inventory-analysis')}
			>
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
					<>
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

						<PaginatedTable
							currentStructureTypeLabel={
								filters.structureType.label
							}
							deltas={pagination.deltas}
							handleDeltaChange={handleDeltaChange}
							handlePageChange={handlePageChange}
							inventoryAnalysisData={inventoryAnalysisData}
							pagination={pagination}
							viewType={selectedItem.value}
						/>
					</>
				)}
			</BaseCard>
		</div>
	);
}
