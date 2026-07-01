/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ChartState} from '@liferay/frontend-js-charts-web';
import React from 'react';

import {SampleContainer} from './SampleContainer';

const SAMPLE_DATA = [
	{label: 'Jan', value: 12},
	{label: 'Feb', value: 18},
	{label: 'Mar', value: 9},
];

function PlaceholderChart({crash, data = [], title}) {
	if (crash) {
		throw new Error('Simulated chart render failure');
	}

	return (
		<div className="border p-3 rounded">
			<strong>{title}</strong>

			<ul className="list-unstyled mb-0 mt-2">
				{data.map((datum) => (
					<li
						key={datum.label}
					>{`${datum.label}: ${datum.value}`}</li>
				))}
			</ul>
		</div>
	);
}

// The states the wrapper adds around any chart. The success path is the chart
// itself, shown by each chart component.

export function StateWrapperSamples() {
	return (
		<>
			<SampleContainer label="Loading">
				<ChartState loading>
					<PlaceholderChart
						data={SAMPLE_DATA}
						title="Monthly visits"
					/>
				</ChartState>
			</SampleContainer>

			<SampleContainer label="Fetch error">
				<ChartState error="Unable to load chart data">
					<PlaceholderChart
						data={SAMPLE_DATA}
						title="Monthly visits"
					/>
				</ChartState>
			</SampleContainer>

			<SampleContainer label="Empty">
				<ChartState empty>
					<PlaceholderChart data={[]} title="Monthly visits" />
				</ChartState>
			</SampleContainer>

			<SampleContainer label="Render error">
				<ChartState
					fallbackError={(error) =>
						console.error('Chart crashed:', error)
					}
				>
					<PlaceholderChart
						crash
						data={SAMPLE_DATA}
						title="Monthly visits"
					/>
				</ChartState>
			</SampleContainer>
		</>
	);
}
