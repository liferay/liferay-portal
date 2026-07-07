/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ChartState, PieChart} from '@liferay/frontend-js-charts-web';
import React from 'react';

import {SampleContainer} from './SampleContainer';

const TRAFFIC_BY_SOURCE = [
	{label: 'Organic search', value: 4200},
	{label: 'Direct', value: 2600},
	{label: 'Referral', value: 1500},
	{label: 'Social', value: 900},
];

const REVENUE_BY_COUNTRY = [
	{label: 'United States', value: 5400},
	{label: 'Germany', value: 2300},
	{label: 'Brazil', value: 1800},
	{label: 'Japan', value: 1200},
];

const DEVICES = [
	{label: 'Desktop', value: 62},
	{label: 'Mobile', value: 31},
	{label: 'Tablet', value: 7},
];

export function PieChartSamples() {
	return (
		<>
			<SampleContainer label="Ring, list legend">
				<PieChart
					className="mx-auto"
					data={TRAFFIC_BY_SOURCE}
					legend="list"
					title="Traffic by source"
				/>
			</SampleContainer>

			<SampleContainer label="Solid pie (no inner radius)">
				<PieChart
					className="mx-auto"
					data={REVENUE_BY_COUNTRY}
					innerRadius={0}
					title="Revenue by country"
				/>
			</SampleContainer>

			<SampleContainer label="Size presets">
				<div className="d-flex flex-wrap justify-content-center">
					<PieChart data={DEVICES} size="xs" title="Devices (xs)" />

					<PieChart data={DEVICES} size="sm" title="Devices (sm)" />

					<PieChart data={DEVICES} size="lg" title="Devices (lg)" />
				</div>
			</SampleContainer>

			<SampleContainer label="Table legend">
				<PieChart
					className="mx-auto"
					data={DEVICES}
					legend="table"
					title="Devices"
				/>
			</SampleContainer>

			<SampleContainer label="List legend: raw value, borderless swatch">
				<PieChart
					className="mx-auto"
					data={TRAFFIC_BY_SOURCE}
					legend="list"
					legendSwatchBorder={false}
					legendValue="value"
					title="Traffic by source"
				/>
			</SampleContainer>

			<SampleContainer label="Wrapped in ChartState">
				<ChartState empty={!DEVICES.length}>
					<PieChart
						className="mx-auto"
						data={DEVICES}
						title="Devices"
					/>
				</ChartState>
			</SampleContainer>
		</>
	);
}
