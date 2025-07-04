/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {Metrics} from '../../Metrics';

export type Metric = {
	comparison: number;
	title: string;
	total: number;
};

const defaultSelectedMetric = 'Impressions';

const metricsMock: Metric[] = [
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

async function fetchComponentData(): Promise<Metric[]> {
	return metricsMock;
}

const PerformanceTabContent = () => {
	const [metrics, setMetrics] = useState<Metric[]>([]);
	const [selectedMetric, setSelectedMetric] = useState<string>(
		defaultSelectedMetric
	);

	useEffect(() => {
		const fetchData = async () => {
			const data = await fetchComponentData();
			setMetrics(data);
		};

		fetchData();
	});

	return (
		metrics.length && (
			<Metrics
				metrics={metrics}
				selectedMetric={selectedMetric}
				setSelectedMetric={setSelectedMetric}
			/>
		)
	);
};

export default PerformanceTabContent;
