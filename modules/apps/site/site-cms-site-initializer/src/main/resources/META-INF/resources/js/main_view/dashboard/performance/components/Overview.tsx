/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {toThousands} from '@liferay/analytics-reports-js-components-web';
import React, {useContext, useEffect, useState} from 'react';

import {SectionHeader} from '../../common/SectionHeader';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {MetricType, OverviewMetrics} from '../types';
import InteractiveCard, {MetricColor} from './InteractiveCard';

type MetricConfig = {
	color: MetricColor;
	icon: string;
	key: MetricType;
	title: string;
};

const METRICS: MetricConfig[] = [
	{
		color: 'pink',
		icon: 'low-vision',
		key: 'impressionsMetric',
		title: Liferay.Language.get('impressions'),
	},
	{
		color: 'purple',
		icon: 'view',
		key: 'viewsMetric',
		title: Liferay.Language.get('views'),
	},
	{
		color: 'green',
		icon: 'download',
		key: 'downloadsMetric',
		title: Liferay.Language.get('downloads'),
	},
	{
		color: 'orange',
		icon: 'book',
		key: 'readsMetric',
		title: Liferay.Language.get('reads-metric'),
	},
];

export function Overview() {
	const {range, space} = useContext(PerformanceContext);

	const [loading, setLoading] = useState(true);
	const [metrics, setMetrics] = useState<OverviewMetrics>();
	const [selected, setSelected] = useState<MetricType>(METRICS[0].key);

	useEffect(() => {
		async function getMetrics() {
			setLoading(true);

			const {data, error} = await PerformanceService.getOverviewMetrics({
				depotEntryIds:
					space.value === 'all' ? undefined : [space.value],
				rangeKey: range.rangeKey,
			});

			if (data) {
				setMetrics(data);
			}

			if (error) {
				console.error(error);
			}

			setLoading(false);
		}

		getMetrics();
	}, [range.rangeKey, space.value]);

	return (
		<>
			<ClayLayout.Row className="mb-3">
				<ClayLayout.Col size={12}>
					<SectionHeader
						description={Liferay.Language.get(
							'get-a-high-level-view-of-performance-trends-to-spot-changes-and-guide-decisions'
						)}
						icon="analytics"
						title={Liferay.Language.get('performance-overview')}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row className="mb-4">
				{METRICS.map(({color, icon, key, title}) => {
					const metric = metrics?.[key];

					return (
						<ClayLayout.Col
							className="mb-3"
							key={key}
							md={6}
							xl={3}
						>
							<InteractiveCard
								active={key === selected}
								color={color}
								icon={icon}
								loading={loading}
								onClick={() => setSelected(key)}
								title={title}
								trend={metric?.trend}
								value={metric && toThousands(metric.value)}
							/>
						</ClayLayout.Col>
					);
				})}
			</ClayLayout.Row>
		</>
	);
}
