/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayTabs from '@clayui/tabs';
import {ChartState} from '@liferay/frontend-js-charts-web';
import React, {useState} from 'react';

const SAMPLE_DATA = [
	{label: 'Jan', value: 12},
	{label: 'Feb', value: 18},
	{label: 'Mar', value: 9},
];

// One tab per chart. The chart components arrive in their own tickets
// (LPD-95991/95993/95994/95996); until then their tabs are placeholders. The
// first tab covers the shared state wrapper, which is chart agnostic.

const CHART_TABS = ['Bar Chart', 'Line Chart', 'Pie Chart', 'Map Chart'];

function SampleContainer({children, label}) {
	return (
		<div className="mt-4">
			<h2>{label}</h2>

			{children}
		</div>
	);
}

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

function StateWrapperSamples() {
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

export function App() {
	const [activeIndex, setActiveIndex] = useState(0);

	const tabs = ['State wrapper', ...CHART_TABS];

	return (
		<>
			<ClayTabs
				activation="manual"
				active={activeIndex}
				onActiveChange={setActiveIndex}
			>
				{tabs.map((label, index) => (
					<ClayTabs.Item
						innerProps={{'aria-controls': `tabpanel-${index}`}}
						key={label}
					>
						{label}
					</ClayTabs.Item>
				))}
			</ClayTabs>

			<ClayTabs.Content activeIndex={activeIndex} fade>
				<ClayTabs.TabPane id="tabpanel-0">
					<StateWrapperSamples />
				</ClayTabs.TabPane>

				{CHART_TABS.map((label, index) => (
					<ClayTabs.TabPane id={`tabpanel-${index + 1}`} key={label}>
						<p className="mt-4 text-secondary">
							{`The ${label} component is not implemented yet.`}
						</p>
					</ClayTabs.TabPane>
				))}
			</ClayTabs.Content>
		</>
	);
}
