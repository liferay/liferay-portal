/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ChartState, LineChart} from '@liferay/frontend-js-charts-web';
import React from 'react';

import {SampleContainer} from './SampleContainer';

const CATEGORIES = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];

const SERIES = [
	{label: 'Search', values: [1200, 1400, 1350, 1600, 1800, 2100]},
	{label: 'Direct', values: [900, 1100, 1050, 1200, 1150, 1300]},
	{label: 'Social', values: [400, 600, 800, 750, 900, 1250]},
];

const GAPPED_SERIES = [
	{label: 'Sensor A', values: [12, 18, null, 9, 14, 20]},
	{label: 'Sensor B', values: [8, null, null, 11, 16, 13]},
];

export function LineChartSamples() {
	return (
		<>
			<SampleContainer label="Blue scheme, popover tooltip, list legend">
				<LineChart
					categories={CATEGORIES}
					className="mx-auto"
					series={SERIES}
					title="Sessions by channel"
				/>
			</SampleContainer>

			<SampleContainer label="Blue scheme, table legend (series told apart by marker)">
				<LineChart
					categories={CATEGORIES}
					className="mx-auto"
					legend="table"
					series={SERIES}
					title="Sessions by channel"
				/>
			</SampleContainer>

			<SampleContainer label="Categorical scheme, table legend">
				<LineChart
					categories={CATEGORIES}
					className="mx-auto"
					legend="table"
					scheme="categorical"
					series={SERIES}
					title="Sessions by channel"
				/>
			</SampleContainer>

			<SampleContainer label="Corner tooltip, formatted axis">
				<LineChart
					categories={CATEGORIES}
					className="mx-auto"
					pointTooltip="corner"
					scheme="categorical"
					series={SERIES}
					title="Sessions by channel"
					yFormat={(value) => `${value / 1000}k`}
				/>
			</SampleContainer>

			<SampleContainer label="Names-only legend, borderless icon, centered">
				<LineChart
					alignment="center"
					categories={CATEGORIES}
					legend="list"
					legendSwatchBorder={false}
					legendValue="name"
					scheme="categorical"
					series={SERIES}
					title="Sessions by channel"
				/>
			</SampleContainer>

			<SampleContainer label="Data gaps (null values), no legend">
				<LineChart
					categories={CATEGORIES}
					className="mx-auto"
					legend="none"
					series={GAPPED_SERIES}
					title="Sensor readings"
				/>
			</SampleContainer>

			<SampleContainer label="Wrapped in ChartState">
				<ChartState empty={!SERIES.length}>
					<LineChart
						categories={CATEGORIES}
						className="mx-auto"
						series={SERIES}
						title="Sessions by channel"
					/>
				</ChartState>
			</SampleContainer>
		</>
	);
}
