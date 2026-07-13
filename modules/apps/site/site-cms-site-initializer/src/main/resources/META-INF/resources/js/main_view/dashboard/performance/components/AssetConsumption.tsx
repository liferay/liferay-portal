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
import {BarChart} from '@liferay/frontend-js-charts-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import {BaseCard} from '../../common/BaseCard';
import PickerTrigger from '../../common/PickerTrigger';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {AssetConsumption as AssetConsumptionData} from '../types';

type GroupBy = 'category' | 'structure' | 'tag' | 'vocabulary';

type ViewType = 'chart' | 'table';

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
	const {range, space} = useContext(PerformanceContext);

	const [assetConsumption, setAssetConsumption] =
		useState<AssetConsumptionData>();
	const [groupBy, setGroupBy] = useState<GroupBy>('structure');
	const [loading, setLoading] = useState(true);
	const [page, setPage] = useState(1);
	const [pageSize, setPageSize] = useState(20);
	const [viewType, setViewType] = useState<ViewType>('table');

	const depotEntryIds = useMemo(
		() => (space.value === 'all' ? undefined : [space.value]),
		[space.value]
	);

	useEffect(() => {
		async function fetchData() {
			setLoading(true);

			const {data, error} = await PerformanceService.getAssetConsumption({
				depotEntryIds,
				groupBy,
				page,
				pageSize,
				rangeKey: range.rangeKey,
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
	}, [depotEntryIds, groupBy, page, pageSize, range.rangeKey]);

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

		if (viewType === 'chart') {
			return (
				<BarChart
					data={items.map(({count, title}) => ({
						label:
							title ||
							sub(Liferay.Language.get('no-x'), groupByLabel),
						value: count,
					}))}
					legend="none"
					orientation="horizontal"
					rounded
					size="inline"
					title={Liferay.Language.get('asset-consumption')}
					track
				/>
			);
		}

		return (
			<>
				<Table columnsVisibility={false} hover={false}>
					<Head
						items={[
							{
								align: 'left' as const,
								id: 'title',
								name: groupByLabel,
								width: 'calc(100% - 340px)',
							},
							{
								align: 'right' as const,
								id: 'views',
								name: Liferay.Language.get('views'),
								width: '200px',
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
						{({count, title}) => (
							<Row>
								<Cell>
									<Text size={3} weight="semi-bold">
										{title ||
											sub(
												Liferay.Language.get('no-x'),
												groupByLabel
											)}
									</Text>
								</Cell>

								<Cell align="right">{toThousands(count)}</Cell>

								<Cell align="right">
									{totalCount
										? ((count / totalCount) * 100).toFixed(
												2
											)
										: '0.00'}
									%
								</Cell>
							</Row>
						)}
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
			<div className="align-items-baseline d-flex mb-3">
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

			{renderBody()}
		</BaseCard>
	);
}
