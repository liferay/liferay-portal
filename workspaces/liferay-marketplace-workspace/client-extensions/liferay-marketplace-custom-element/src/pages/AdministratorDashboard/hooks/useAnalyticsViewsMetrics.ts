/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useSWR from 'swr';

import analyticsOAuth2 from '../../../services/oauth/Analytics';

const useAnalyticsViewsMetrics = () => {
	const {data: viewsMetricResult, ...swr} = useSWR(
		'administrator-dashboard/analytics',
		() =>
			analyticsOAuth2.getPages(
				new URLSearchParams({
					rangeKey: '90',
					sortMetric: 'viewsMetric',
					sortOrder: 'desc',
				})
			)
	);

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
