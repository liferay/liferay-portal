/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useSWR from 'swr';

import analyticsOAuth2 from '../../../services/oauth/Analytics';

const useAnalyticsViewsMetrics = () => {
	const {data: analyticsViewsResponse = [], ...swr} = useSWR<
		AnalyticsViews[]
	>('administrator-dashboard/metrics/analytics', () =>
		Promise.all([
			analyticsOAuth2.getPages(
				new URLSearchParams({
					rangeKey: '90',
					sortMetric: 'viewsMetric',
					sortOrder: 'desc',
				})
			),
			analyticsOAuth2.getPages(
				new URLSearchParams({
					keywords: '/p/',
					rangeKey: '90',
					sortMetric: 'visitorsMetric',
					sortOrder: 'desc',
				})
			),
		])
	);

	const [viewsMetricResult, visitorsMetricResult] = analyticsViewsResponse;

	const viewsMetrics =
		visitorsMetricResult?.results?.map((item) => ({
			title: item.title.split('-')[0].trim(),
			views: item.metrics.viewsMetric.value,
			visitor: item.metrics.visitorsMetric.value,
		})) || [];

	viewsMetrics.length = 5;

	return {
		...swr,

		visitorsMetric:
			viewsMetricResult?.results
				?.map(
					({
						metrics: {
							viewsMetric: {value},
						},
					}) => value
				)
				.reduce(
					(previousValue, currentValue) =>
						previousValue + currentValue,
					0
				) || 0,
	};
};

export default useAnalyticsViewsMetrics;
