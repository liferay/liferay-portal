/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	BarChart,
	ChartState,
	TrendIndicator,
} from '@liferay/frontend-js-charts-web';
import React from 'react';

import {SampleContainer} from './SampleContainer';

const BAR_DATA = [
	{label: 'Search', value: 4200},
	{label: 'Direct', value: 3100},
	{label: 'Social', value: 2600},
	{label: 'Email', value: 1500},
	{label: 'Referral', value: 900},
];

export function BarChartSamples() {
	return (
		<>
			<SampleContainer label="Vertical, categorical, list legend">
				<BarChart
					className="mx-auto"
					data={BAR_DATA}
					legend="list"
					scheme="categorical"
					title="Sessions by channel"
				/>
			</SampleContainer>

			<SampleContainer label="Horizontal, table legend">
				<BarChart
					className="mx-auto"
					data={BAR_DATA}
					legend="table"
					orientation="horizontal"
					title="Sessions by channel"
				/>
			</SampleContainer>

			<SampleContainer label="Inline (track and rounded)">
				<BarChart
					className="mx-auto"
					data={BAR_DATA}
					orientation="horizontal"
					rounded
					size="inline"
					title="Sessions by channel"
					track
				/>
			</SampleContainer>

			<SampleContainer label="Trend indicator">
				<div className="d-flex flex-wrap justify-content-center">
					<TrendIndicator
						className="mr-4"
						direction="up"
						label="versus last week"
						value={22.5}
					/>

					<TrendIndicator
						className="mr-4"
						direction="down"
						label="versus last week"
						value={8.1}
					/>

					<TrendIndicator
						direction="neutral"
						label="versus last week"
						value={0}
					/>
				</div>
			</SampleContainer>

			<SampleContainer label="Wrapped in ChartState">
				<ChartState empty={!BAR_DATA.length}>
					<BarChart
						className="mx-auto"
						data={BAR_DATA}
						title="Sessions by channel"
					/>
				</ChartState>
			</SampleContainer>
		</>
	);
}
