/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';

import {MetricsCard} from './MetricsCard';

type Metric = {
	comparison: number;
	title: string;
	total: number;
};

const metrics: Metric[] = [
	{
		comparison: 0,
		title: 'Impressions',
		total: 11,
	},
	{
		comparison: -12.3,
		title: 'Views',
		total: 25321,
	},
	{
		comparison: 32.1,
		title: 'Downloads',
		total: 220153310,
	},
];

export function Metrics() {
	const [selectedCard, setSelectedCard] = useState<string>('Impressions');

	return (
		<div className="d-flex flex-row justify-content-between">
			{metrics.map((metric) => {
				return (
					<MetricsCard
						active={selectedCard === metric.title}
						comparison={metric.comparison}
						key={metric.title}
						setSelectedCard={setSelectedCard}
						title={metric.title}
						total={metric.total}
					/>
				);
			})}
		</div>
	);
}
