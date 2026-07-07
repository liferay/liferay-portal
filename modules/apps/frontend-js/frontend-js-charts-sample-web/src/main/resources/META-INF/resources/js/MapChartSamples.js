/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ChartState, MapChart} from '@liferay/frontend-js-charts-web';
import React from 'react';

import {SampleContainer} from './SampleContainer';

const WORLD_DATA = [
	{country: 'US', value: 5400},
	{country: 'BR', value: 2300},
	{country: 'GB', value: 1900},
	{country: 'DE', value: 1800},
	{country: 'FR', value: 1600},
	{country: 'RU', value: 1400},
	{country: 'CN', value: 4700},
	{country: 'IN', value: 3900},
	{country: 'JP', value: 2100},
	{country: 'AU', value: 900},
	{country: 'ZA', value: 700},
	{country: 'NG', value: 600},
	{country: 'EG', value: 500},
];

const EUROPE_DATA = [
	{country: 'GB', value: 1900},
	{country: 'DE', value: 1800},
	{country: 'FR', value: 1600},
	{country: 'ES', value: 1100},
	{country: 'IT', value: 1050},
	{country: 'PL', value: 700},
	{country: 'SE', value: 450},
];

const SPARSE_DATA = [
	{country: 'US', value: 800},
	{country: 'BR', value: 800},
	{country: 'JP', value: 200},
];

export function MapChartSamples() {
	return (
		<div className="row">
			<div className="col-12 col-lg-6">
				<SampleContainer label="Markers, blue scheme">
					<MapChart
						className="mx-auto"
						data={WORLD_DATA}
						legend="scale"
						scheme="blue"
						title="Sales by country"
						variant="markers"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="Choropleth, blue scheme">
					<MapChart
						className="mx-auto"
						data={WORLD_DATA}
						legend="scale"
						scheme="blue"
						title="Sales by country"
						variant="choropleth"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="Markers, categorical scheme">
					<MapChart
						className="mx-auto"
						data={WORLD_DATA}
						legend="scale"
						scheme="categorical"
						title="Sales by country"
						variant="markers"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="3 buckets">
					<MapChart
						className="mx-auto"
						data={WORLD_DATA}
						legend="scale"
						steps={3}
						title="Sales by country"
						variant="choropleth"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="List legend">
					<MapChart
						className="mx-auto"
						data={WORLD_DATA}
						legend="list"
						title="Sales by country"
						variant="choropleth"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="Table legend">
					<MapChart
						className="mx-auto"
						data={WORLD_DATA}
						legend="table"
						title="Sales by country"
						variant="choropleth"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="No legend">
					<MapChart
						className="mx-auto"
						data={WORLD_DATA}
						title="Sales by country"
						variant="choropleth"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="Fit to data">
					<MapChart
						className="mx-auto"
						data={EUROPE_DATA}
						fit="data"
						title="Offices in Europe"
						variant="markers"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="Sparse data">
					<MapChart
						className="mx-auto"
						data={SPARSE_DATA}
						title="Sales by country"
						variant="choropleth"
					/>
				</SampleContainer>
			</div>

			<div className="col-12 col-lg-6">
				<SampleContainer label="Wrapped in ChartState">
					<ChartState empty={!WORLD_DATA.length}>
						<MapChart
							className="mx-auto"
							data={WORLD_DATA}
							title="Sales by country"
							variant="choropleth"
						/>
					</ChartState>
				</SampleContainer>
			</div>
		</div>
	);
}
