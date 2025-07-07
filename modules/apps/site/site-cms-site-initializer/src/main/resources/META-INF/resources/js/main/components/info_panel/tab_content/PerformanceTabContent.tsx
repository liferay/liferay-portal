/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, { useEffect, useState } from 'react';

import { Metrics } from '../../Metrics';
import { getEmptyState } from '../EmptyState';

export type Metric = {
	comparison: number;
	title: string;
	total: number;
};

export type EmptyStateData = {
	analyticsSettingsPortletURL: string;
	siteEditDepotEntryDepotAdminPortletURL: string;
	connectedToAnalyticsCloud: boolean;
	connectedToSpace: boolean;
	siteSyncedToAnalyticsCloud: boolean;
	isAdmin: boolean;
};

const defaultSelectedMetric = 'Impressions';

const metricsMock: Metric[] = [
	// {
	// 	comparison: 0,
	// 	title: 'Impressions',
	// 	total: 11,
	// },
	// {
	// 	comparison: -12.3,
	// 	title: 'Views',
	// 	total: 25321,
	// },
	// {
	// 	comparison: 32.1,
	// 	title: 'Downloads',
	// 	total: 220153310,
	// },
];

async function fetchComponentData(): Promise<Metric[]> {
	return metricsMock;
}

async function fetchEmptyStateData(contentPerformanceDataFetchURL: string): Promise<EmptyStateData> {
	
	//Endpoint
	
	// const response = await fetch(contentPerformanceDataFetchURL, {
	// 	method: 'GET',
	// });

	// return await response.json();

	//Mock

	return {
		analyticsSettingsPortletURL: '/mock-analytics',
		siteEditDepotEntryDepotAdminPortletURL: '/mock-depot',
		connectedToAnalyticsCloud: true,
		connectedToSpace: true,
		siteSyncedToAnalyticsCloud: true,
		isAdmin: true,
	};
}

type Props = {
	contentPerformanceDataFetchURL: string;
};

const PerformanceTabContent: React.FC<Props> = ({
	contentPerformanceDataFetchURL,
}) => {
	const [metrics, setMetrics] = useState<Metric[]>([]);
	const [emptyStateData, setEmptyStateData] = useState<EmptyStateData | null>(
		null
	);
	const [selectedMetric, setSelectedMetric] = useState<string>(
		defaultSelectedMetric
	);

	useEffect(() => {
		const fetchData = async () => {
			try {
				const metricsData = await fetchComponentData();
				setMetrics(metricsData);

				if (!metricsData.length) {
					const emptyData = await fetchEmptyStateData(
						contentPerformanceDataFetchURL
					);

					setEmptyStateData(emptyData);
				}
			} catch (err) {
				console.error(err);
			}
		};

		fetchData();
	}, [contentPerformanceDataFetchURL]);

	if (!metrics.length && emptyStateData) {
		return getEmptyState(emptyStateData);
	}

	return (
		<Metrics
			metrics={metrics}
			selectedMetric={selectedMetric}
			setSelectedMetric={setSelectedMetric}
		/>
	);
};

export default PerformanceTabContent;