/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import useAnalyticsQuery from '../../../common/hooks/useAnalyticsQuery';
import {IEngagementChartItem} from '../../../common/utils/types';
import AnalyticsFrame from './AnalyticsFrame';
import EngagementChart from './EngagementChart';
import Loader from './Loader';

function TimelineEngagementChart() {
	const [data, setData] = useState<IEngagementChartItem[]>([]);
	const [element, setElement] = useState<HTMLElement | null>(null);

	const {isLoading, response} = useAnalyticsQuery({
		element,
		query: {
			paths: [
				{key: 'siteSessions', path: '/sessions-site-histogram-metric'},
			],
		},
		variables: {
			devices: 'Any',
			emailAddresses: [],
			interval: 'D',
			location: 'Any',
			rangeEnd: null,
			rangeKey: 7,
			rangeStart: null,
		},
	});

	useEffect(() => {
		if (response) {
			const histogramMetrics =
				response.siteSessions?.histogram?.histogramMetrics ?? [];

			setData(
				histogramMetrics.map((histogramMetric: any) => ({
					date: histogramMetric.key,
					numberOfVisits: histogramMetric.value ?? 0,
					timeSpent: 0,
				}))
			);
		}
	}, [response]);

	return (
		<AnalyticsFrame
			icon="analytics"
			title={Liferay.Language.get('engagement-timeline')}
		>
			<div ref={setElement}>
				{isLoading ? (
					<Loader />
				) : !data?.length ? (
					<p className="mt-3 text-center text-muted">
						{Liferay.Language.get('no-data-available')}
					</p>
				) : (
					<EngagementChart data={data} />
				)}
			</div>
		</AnalyticsFrame>
	);
}

export default TimelineEngagementChart;
