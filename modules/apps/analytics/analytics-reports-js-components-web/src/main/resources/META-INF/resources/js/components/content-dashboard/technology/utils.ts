/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MetricType} from '../../../types/global';
import {getPercentage} from '../../../utils/math';
import {metricNameByType} from '../../../utils/metrics';
import {ChartData} from '../stacked-bar/StackedBarChart';
import {Data} from './Technology';

const getSafePercentage = (value: number, total: number) =>
	total > 0 ? getPercentage((value / total) * 100) : 0;

export function formatData(data: Data, metricType: MetricType): ChartData {
	const curMetricName = metricNameByType[metricType];

	const selectedMetric = data.deviceMetrics.find(
		({metricName}) => metricName === curMetricName
	);

	const total =
		selectedMetric?.metrics.reduce((sum, {value}) => sum + value, 0) ?? 0;

	let chartData =
		selectedMetric?.metrics
			.map(({value, valueKey}) => ({
				label: valueKey,
				percentage: getSafePercentage(value, total),
				value,
			}))
			.sort((a, b) => b.percentage - a.percentage) ?? [];

	if (chartData.length > 4) {
		const others = chartData.slice(3).reduce(
			(acc, item) => ({
				label: Liferay.Language.get('others'),
				percentage: getSafePercentage(acc.value + item.value, total),
				value: acc.value + item.value,
			}),
			{label: Liferay.Language.get('others'), percentage: 0, value: 0}
		);

		chartData = [...chartData.slice(0, 3), others];
	}

	return {
		data: chartData,
		total,
	};
}
