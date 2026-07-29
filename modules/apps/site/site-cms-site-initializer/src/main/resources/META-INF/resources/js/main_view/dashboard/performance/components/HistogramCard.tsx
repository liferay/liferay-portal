/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {formatDate} from '@liferay/analytics-reports-js-components-web';
import {ChartState, LineChart} from '@liferay/frontend-js-charts-web';
import React, {useContext, useEffect, useMemo, useState} from 'react';

import {BaseCard} from '../../common/BaseCard';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {Histogram, MetricType} from '../types';

export function HistogramCard({
	metricType,
	title,
}: {
	metricType: MetricType;
	title: string;
}) {
	const {range, space} = useContext(PerformanceContext);

	const [histogram, setHistogram] = useState<Histogram>();
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	const depotEntryIds = useMemo(
		() => (space.value === 'all' ? undefined : [space.value]),
		[space.value]
	);

	useEffect(() => {
		async function fetchData() {
			setLoading(true);

			const {data, error} = await PerformanceService.getHistogramMetric({
				depotEntryIds,
				rangeKey: range.rangeKey,
				selectedMetric: metricType,
			});

			const histograms = data?.histograms ?? [];

			setHistogram(
				histograms.find(({metricName}) => metricName === metricType) ??
					histograms[0]
			);
			setError(error);
			setLoading(false);
		}

		fetchData();
	}, [depotEntryIds, metricType, range.rangeKey]);

	const metrics = histogram?.metrics ?? [];

	const empty = !metrics.length;

	return (
		<BaseCard
			className="d-flex flex-column h-100"
			contentClassName="flex-grow-1"
			title={title}
			uppercaseTitle={false}
		>
			<ChartState error={error} loading={loading}>
				<LineChart
					categories={metrics.map(({valueKey}) =>
						formatDate(new Date(valueKey), range.rangeKey)
					)}
					className="w-100"
					description={
						empty
							? Liferay.Language.get('there-is-no-data')
							: undefined
					}
					legendValue="name"
					series={[
						{
							color: 'var(--gray-400)',
							dasharray: '5 5',
							label: Liferay.Language.get('previous-period'),
							values: metrics.map(
								({previousValue}) => previousValue
							),
						},
						{
							label: Liferay.Language.get('current-period'),
							values: metrics.map(({value}) => value),
						},
					]}
					title=""
					yFormat={empty ? () => '' : undefined}
				/>
			</ChartState>
		</BaseCard>
	);
}
