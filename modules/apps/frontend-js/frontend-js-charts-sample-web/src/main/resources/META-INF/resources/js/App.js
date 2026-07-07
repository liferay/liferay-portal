/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayTabs from '@clayui/tabs';
import React, {useState} from 'react';

import {BarChartSamples} from './BarChartSamples';
import {LineChartSamples} from './LineChartSamples';
import {PieChartSamples} from './PieChartSamples';
import {StateWrapperSamples} from './StateWrapperSamples';

// One tab per chart. Each chart's samples live in their own file and are
// dropped in below; the remaining chart (LPD-95996) is a placeholder until it
// lands.

const TABS = [
	{Samples: StateWrapperSamples, label: 'State wrapper'},
	{Samples: BarChartSamples, label: 'Bar Chart'},
	{Samples: LineChartSamples, label: 'Line Chart'},
	{Samples: PieChartSamples, label: 'Pie Chart'},
	{label: 'Map Chart'},
];

export function App() {
	const [activeIndex, setActiveIndex] = useState(0);

	return (
		<>
			<ClayTabs
				activation="manual"
				active={activeIndex}
				onActiveChange={setActiveIndex}
			>
				{TABS.map((tab, index) => (
					<ClayTabs.Item
						innerProps={{'aria-controls': `tabpanel-${index}`}}
						key={tab.label}
					>
						{tab.label}
					</ClayTabs.Item>
				))}
			</ClayTabs>

			<ClayTabs.Content activeIndex={activeIndex} fade>
				{TABS.map((tab, index) => {
					const {Samples} = tab;

					return (
						<ClayTabs.TabPane
							id={`tabpanel-${index}`}
							key={tab.label}
						>
							{Samples ? (
								<Samples />
							) : (
								<p className="mt-4 text-secondary">
									{`The ${tab.label} component is not implemented yet.`}
								</p>
							)}
						</ClayTabs.TabPane>
					);
				})}
			</ClayTabs.Content>
		</>
	);
}
