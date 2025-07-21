/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useContext, useEffect, useState} from 'react';

import {TrendClassification} from '../../dashboard/utils/metrics';
import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../context';
import {MetricType, Metrics} from './performance/Metrics';
import {
	PerformanceTabContext,
	PerformanceTabProvider,
} from './performance/PerformanceTabContext';

import '../../../../css/infoPanel/PerformanceTab.scss';
import {CheckPermissions} from './performance/CheckPermissions';

export type Metric = {
	metricType: MetricType;
	trend: {
		percentage: number;
		trendClassification: TrendClassification;
	};
	value: number;
};

type MetricsApiResponse = {
	defaultMetric: Metric;
	selectedMetrics: Metric[];
};

async function fetchMetricsData(_assetId: number): Promise<MetricsApiResponse> {

	// TODO fetch from API

	return {
		defaultMetric: {
			metricType: MetricType.Views,
			trend: {
				percentage: 50,
				trendClassification: TrendClassification.Neutral,
			},
			value: 456000,
		},
		selectedMetrics: [
			{
				metricType: MetricType.Impressions,
				trend: {
					percentage: 2,
					trendClassification: TrendClassification.Positive,
				},
				value: 3000,
			},
			{
				metricType: MetricType.Views,
				trend: {
					percentage: 50,
					trendClassification: TrendClassification.Neutral,
				},
				value: 456000,
			},
			{
				metricType: MetricType.Downloads,
				trend: {
					percentage: -100,
					trendClassification: TrendClassification.Negative,
				},
				value: 15000,
			},
		],
	};
}

const PerformanceTabContent = () => {
	const {changeMetric, filters} = useContext(PerformanceTabContext);
	const [data, setData] = useState<MetricsApiResponse | null>(null);
	const [loading, setLoading] = useState(false);

	const selectedAsset = useContext<IAssetTypeInfoPanelContext>(
		AssetTypeInfoPanelContext
	);

	useEffect(() => {
		if (selectedAsset?.id) {
			const fetchData = async () => {
				try {
					setLoading(true);

					const data = await fetchMetricsData(
						selectedAsset?.objectEntries?.[0].embedded
							.scopeId as number
					);

					if (!filters.metric) {
						changeMetric(data.defaultMetric.metricType);
					}

					setData(data);
					setLoading(false);
				}
				catch (error) {
					console.error(error);

					setLoading(false);
				}
			};

			fetchData();
		}
	}, [changeMetric, filters.metric, selectedAsset]);

	if (loading) {
		return <ClayLoadingIndicator />;
	}

	if (!data) {
		return null;
	}

	return <Metrics {...data} />;
};

const PerformanceTab = () => (
	<CheckPermissions>
		<PerformanceTabProvider>
			<PerformanceTabContent />
		</PerformanceTabProvider>
	</CheckPermissions>
);

export default PerformanceTab;
