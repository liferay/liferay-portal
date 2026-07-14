/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Body, Cell, Head, Option, Picker, Row, Table, Text} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {toThousands} from '@liferay/analytics-reports-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import {BaseCard} from '../../common/BaseCard';
import PickerTrigger from '../../common/PickerTrigger';
import {AllCategoriesDropdown} from '../../common/filters/AllCategoriesDropdown';
import {AllStructureTypesDropdown} from '../../common/filters/AllStructureTypesDropdown';
import {AllTagsDropdown} from '../../common/filters/AllTagsDropdown';
import {AllVocabulariesDropdown} from '../../common/filters/AllVocabulariesDropdown';
import {Item} from '../../common/filters/FilterDropdown';
import {initialFilters} from '../../common/filters/filters';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {AssetConsumption as AssetConsumptionData} from '../types';

type GroupBy = 'category' | 'structure' | 'tag' | 'vocabulary';

type ViewType = 'chart' | 'table';

function toFilterParam(value: string) {
	return value === 'all' ? undefined : value;
}

const DELTAS = [20, 40, 60].map((label) => ({label}));

const GROUP_BY_OPTIONS: {label: string; value: GroupBy}[] = [
	{label: Liferay.Language.get('category'), value: 'category'},
	{label: Liferay.Language.get('vocabulary'), value: 'vocabulary'},
	{label: Liferay.Language.get('tag'), value: 'tag'},
	{label: Liferay.Language.get('content-structure'), value: 'structure'},
];

const VIEW_OPTIONS: {icon: string; label: string; value: ViewType}[] = [
	{icon: 'polls', label: Liferay.Language.get('chart[noun]'), value: 'chart'},
	{icon: 'table', label: Liferay.Language.get('table'), value: 'table'},
];

export function AssetConsumption() {
	const {constants, range, space} = useContext(PerformanceContext);

	const [assetConsumption, setAssetConsumption] =
		useState<AssetConsumptionData>();
	const [filters, setFilters] = useState<{
		category: Item;
		structure: Item;
		tag: Item;
		vocabulary: Item;
	}>(initialFilters);
	const [groupBy, setGroupBy] = useState<GroupBy>('structure');
	const [loading, setLoading] = useState(true);
	const [page, setPage] = useState(1);
	const [pageSize, setPageSize] = useState(20);
	const [viewType, setViewType] = useState<ViewType>('chart');

	const depotEntryIds = useMemo(
		() => (space.value === 'all' ? undefined : [space.value]),
		[space.value]
	);

	useEffect(() => {
		setFilters(initialFilters);
		setPage(1);
	}, [space.value]);

	useEffect(() => {
		async function fetchData() {
			setLoading(true);

			const {data, error} = await PerformanceService.getAssetConsumption({
				categoryId: toFilterParam(filters.category.value),
				depotEntryIds,
				groupBy,
				page,
				pageSize,
				rangeKey: range.rangeKey,
				structureId: toFilterParam(filters.structure.value),
				tagId: toFilterParam(filters.tag.value),
				vocabularyId: toFilterParam(filters.vocabulary.value),
			});

			if (data) {
				setAssetConsumption(data);
			}

			if (error) {
				console.error(error);
			}

			setLoading(false);
		}

		fetchData();
	}, [depotEntryIds, filters, groupBy, page, pageSize, range.rangeKey]);

	const groupByLabel =
		GROUP_BY_OPTIONS.find(({value}) => value === groupBy)?.label ?? '';
	const items = assetConsumption?.performanceAssetConsumptionItems ?? [];
	const totalCount = assetConsumption?.totalCount ?? 0;
	const viewOption = VIEW_OPTIONS.find(({value}) => value === viewType);

	function renderBody() {
		if (loading) {
			return (
				<div
					className="align-items-center d-flex justify-content-center"
					style={{minHeight: '200px'}}
				>
					<ClayLoadingIndicator size="md" />
				</div>
			);
		}

		if (!items.length) {
			return (
				<ClayEmptyState
					description={Liferay.Language.get(
						'there-are-no-assets-created-in-the-space'
					)}
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state.svg`}
					title={Liferay.Language.get('no-assets-yet')}
				/>
			);
		}

		const isChart = viewType === 'chart';
		const cellClassName = isChart ? 'border-0' : '';

		return (
			<>
				<Table
					borderless={isChart}
					columnsVisibility={false}
					hover={false}
					striped={false}
				>
					<Head
						items={[
							{
								align: 'left' as const,
								id: 'title',
								name: groupByLabel,
								width: isChart ? '200px' : 'calc(100% - 340px)',
							},
							{
								align: isChart
									? ('left' as const)
									: ('right' as const),
								id: 'views',
								name: Liferay.Language.get('views'),
								width: isChart ? 'calc(100% - 340px)' : '200px',
							},
							{
								align: 'right' as const,
								id: 'percentage',
								name: Liferay.Language.get(
									'percentage-of-views'
								),
								width: '140px',
							},
						]}
					>
						{(column) => (
							<Cell
								align={column.align}
								key={column.id}
								width={column.width}
							>
								{column.name}
							</Cell>
						)}
					</Head>

					<Body items={items}>
						{({count, title}) => {
							const percentage = totalCount
								? (count / totalCount) * 100
								: 0;

							return (
								<Row>
									<Cell className={cellClassName}>
										<Text size={3} weight="semi-bold">
											{title ||
												sub(
													Liferay.Language.get(
														'no-x'
													),
													groupByLabel
												)}
										</Text>
									</Cell>

									<Cell
										align={isChart ? undefined : 'right'}
										className={cellClassName}
									>
										{isChart ? (
											<div className="cms-dashboard__volume-chart">
												<div
													className="cms-dashboard__volume-chart__bar"
													style={{
														width: `${percentage}%`,
													}}
												/>

												<div className="cms-dashboard__volume-chart__value">
													<Text
														size={3}
														weight="semi-bold"
													>
														{toThousands(count)}
													</Text>
												</div>
											</div>
										) : (
											toThousands(count)
										)}
									</Cell>

									<Cell
										align="right"
										className={cellClassName}
									>
										<Text size={3} weight="semi-bold">
											{percentage.toFixed(2)}%
										</Text>
									</Cell>
								</Row>
							);
						}}
					</Body>
				</Table>

				<ClayPaginationBarWithBasicItems
					active={page}
					activeDelta={pageSize}
					className="mt-3"
					deltas={DELTAS}
					ellipsisBuffer={3}
					onActiveChange={setPage}
					onDeltaChange={(delta) => {
						setPage(1);
						setPageSize(delta);
					}}
					totalItems={
						assetConsumption?.performanceAssetConsumptionItemsCount ??
						0
					}
				/>
			</>
		);
	}

	return (
		<BaseCard
			Preferences={
				<Picker
					aria-label={viewOption?.label}
					as={PickerTrigger}
					items={VIEW_OPTIONS}
					onSelectionChange={(key) => setViewType(key as ViewType)}
					selectedKey={viewType}
					triggerIcon={viewOption?.icon}
				>
					{({icon, label, value}) => (
						<Option key={value} textValue={label}>
							<ClayIcon className="mr-2" symbol={icon} />

							{label}
						</Option>
					)}
				</Picker>
			}
			description={Liferay.Language.get(
				'total-number-of-assets-grouped-by-category-vocabulary-tags-structure-type-or-space'
			)}
			title={Liferay.Language.get('asset-consumption')}
			uppercaseTitle={false}
		>
			<div className="align-items-lg-center d-flex flex-column flex-lg-row mb-3">
				<div className="align-items-center d-flex mb-2 mb-lg-0 mr-lg-4">
					<span className="mr-2">
						<Text size={3} weight="semi-bold">
							{Liferay.Language.get('group-by')}
						</Text>
					</span>

					<Picker
						aria-label={Liferay.Language.get('group-by')}
						as={PickerTrigger}
						borderless
						items={GROUP_BY_OPTIONS}
						onSelectionChange={(key) => {
							setGroupBy(key as GroupBy);
							setPage(1);
						}}
						selectedKey={groupBy}
					>
						{({label, value}) => (
							<Option key={value} textValue={label}>
								{label}
							</Option>
						)}
					</Picker>
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
								onSelectItem={(structure) => {
									setFilters({...filters, structure});
									setPage(1);
								}}
							/>
						</div>

						<div className="mb-2 mb-lg-0 mr-2">
							<AllVocabulariesDropdown
								cmsGroupId={constants.cmsGroupId}
								depotEntryId={space.value}
								item={filters.vocabulary}
								onSelectItem={(vocabulary) => {
									setFilters({...filters, vocabulary});
									setPage(1);
								}}
							/>
						</div>

						<div className="mb-2 mb-lg-0 mr-2">
							<AllCategoriesDropdown
								cmsGroupId={constants.cmsGroupId}
								depotEntryId={space.value}
								item={filters.category}
								onSelectItem={(category) => {
									setFilters({...filters, category});
									setPage(1);
								}}
							/>
						</div>

						<div className="mb-2 mb-lg-0">
							<AllTagsDropdown
								cmsGroupId={constants.cmsGroupId}
								depotEntryId={space.value}
								item={filters.tag}
								onSelectItem={(tag) => {
									setFilters({...filters, tag});
									setPage(1);
								}}
							/>
						</div>
					</div>
				</div>
			</div>

			{renderBody()}
		</BaseCard>
	);
}
