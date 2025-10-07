/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Body, Cell, Head, Row, Table, Text} from '@clayui/core';
import {WeightFont} from '@clayui/core/lib/typography/Heading';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {sub} from 'frontend-js-web';
import React, {useMemo} from 'react';

import {InventoryAnalysisDataType} from './InventoryAnalysisCard';

type TableData = {
	percentage: number;
	title: string;
	volume: JSX.Element;
};

const viewSpecs = {
	chart: {
		expandable: 'volume',
		textWeight: 'semi-bold',
		titleWidth: '200px',
		volumeWidth: 'calc(100% - 340px)',
	},
	table: {
		expandable: 'title',
		percentageWidth: '140px',
		textWeight: 'normal',
		titleWidth: 'calc(100% - 340px',
		volumeWidth: '200px',
	},
};

const VolumeChart = ({
	percentage,
	volume,
}: {
	percentage: number;
	volume: number;
}) => {
	return (
		<div className="cms-dashboard__inventory-analysis__bar-chart">
			<div
				className="cms-dashboard__inventory-analysis__bar-chart__bar"
				style={{width: `${percentage}%`}}
			/>

			<div className="cms-dashboard__inventory-analysis__bar-chart__value">
				<Text size={3} weight="semi-bold">
					{volume}
				</Text>
			</div>
		</div>
	);
};

const mapData = (
	data: InventoryAnalysisDataType,
	viewType: 'chart' | 'table'
): TableData[] => {
	return data.inventoryAnalysisItems.map(({count, title}) => {
		const percentage = (count / data.totalCount) * 100;

		title = title === 'Unknown' ? '' : title;

		return {
			percentage,
			title,
			volume:
				viewType === 'chart' ? (
					<VolumeChart percentage={percentage} volume={count} />
				) : (
					<Text size={3} weight="normal">
						{count}
					</Text>
				),
		};
	});
};

interface IPaginatedTable {
	currentStructureTypeLabel: string;
	deltas: {label: number}[];
	handleDeltaChange: (delta: number) => void;
	handlePageChange: (page: number) => void;
	inventoryAnalysisData: InventoryAnalysisDataType | undefined;
	pagination: {
		page: number;
		pageSize: number;
	};
	viewType: 'chart' | 'table';
}

const PaginatedTable: React.FC<IPaginatedTable> = ({
	currentStructureTypeLabel,
	deltas,
	handleDeltaChange,
	handlePageChange,
	inventoryAnalysisData,
	pagination,
	viewType,
}) => {
	const tableData = useMemo(() => {
		if (inventoryAnalysisData) {
			return mapData(inventoryAnalysisData, viewType);
		}

		return [];
	}, [inventoryAnalysisData, viewType]);

	return (
		<div>
			<Table
				borderless={viewType === 'chart'}
				columnsVisibility={false}
				hover={false}
				striped={false}
			>
				<Head
					items={[
						{
							align: 'left',
							id: 'title',
							name: currentStructureTypeLabel,
							width: viewSpecs[viewType].titleWidth,
						},
						{
							align: viewType === 'chart' ? 'left' : 'right',
							id: 'volume',
							name: Liferay.Language.get('assets-volume'),
							width: viewSpecs[viewType].volumeWidth,
						},
						{
							align: 'right',
							id: 'percentage',
							name: Liferay.Language.get('%-of-assets'),
							width: '140px',
						},
					]}
				>
					{(column) => (
						<Cell
							align={column.align as 'left' | 'right'}
							key={column.id}
							width={column.width}
						>
							{column.name}
						</Cell>
					)}
				</Head>

				<Body items={tableData}>
					{(row) => (
						<Row>
							<Cell
								className={
									viewType === 'chart' ? 'border-0' : ''
								}
								expanded={
									viewSpecs[viewType].expandable === 'volume'
								}
								width="10%"
							>
								<Text size={3} weight="semi-bold">
									{row['title'] ||
										sub(
											Liferay.Language.get('no-x'),
											currentStructureTypeLabel
										)}
								</Text>
							</Cell>

							<Cell
								align="right"
								className={
									viewType === 'chart' ? 'border-0' : ''
								}
								expanded={
									viewSpecs[viewType].expandable === 'volume'
								}
								width="80%"
							>
								{row['volume']}
							</Cell>

							<Cell
								align="right"
								className={
									viewType === 'chart' ? 'border-0' : ''
								}
								width="10%"
							>
								<Text
									size={3}
									weight={
										viewSpecs[viewType]
											.textWeight as WeightFont
									}
								>
									{`${row['percentage'].toFixed(2)}%`}
								</Text>
							</Cell>
						</Row>
					)}
				</Body>
			</Table>

			<ClayPaginationBarWithBasicItems
				active={pagination.page}
				activeDelta={pagination.pageSize}
				className="mt-3"
				deltas={deltas}
				ellipsisBuffer={3}
				ellipsisProps={{'aria-label': 'More', 'title': 'More'}}
				onActiveChange={handlePageChange}
				onDeltaChange={handleDeltaChange}
				totalItems={inventoryAnalysisData?.totalCount ?? 0}
			/>
		</div>
	);
};

export default PaginatedTable;
