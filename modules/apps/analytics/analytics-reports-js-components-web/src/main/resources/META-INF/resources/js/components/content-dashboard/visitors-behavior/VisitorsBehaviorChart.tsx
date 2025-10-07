/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useMemo, useState} from 'react';
import {Line} from 'recharts';

import {Context} from '../../../Context';
import {Colors} from '../../../types/global';
import {formatDate} from '../../../utils/date';
import {assetContent, metricNameByType} from '../../../utils/metrics';
import {CircleDot, PublishedVersionDot} from '../../metrics/Dots';
import MetricsChart, {DataKey} from '../../metrics/MetricsChart';
import {IMetricsChartLegendProps} from '../../metrics/MetricsChartLegend';
import {getFillOpacity} from '../../metrics/utils';
import {Data, PublishedVersionData} from './VisitorsBehavior';
import VisitorsBehaviorChartTooltip from './VisitorsBehaviorChartTooltip';
import {VisitorsBehaviorDataKey, formatVisitorsBehaviorData} from './utils';

interface IVisitorsBehaviorChartProps {
	data: Data;
	publishedVersionData: PublishedVersionData | null;
}

const VisitorsBehaviorChart: React.FC<IVisitorsBehaviorChartProps> = ({
	data,
	publishedVersionData,
}) => {
	const {filters} = useContext(Context);

	const [activeTabIndex, setActiveTabIndex] = useState(false);

	const [activeLegendItem, setActiveLegendItem] = useState<
		VisitorsBehaviorDataKey | DataKey | null
	>(null);

	const metricName = metricNameByType[filters.metric];

	const formattedData = useMemo(
		() =>
			formatVisitorsBehaviorData({
				data,
				metricName,
				publishedVersionData,
				...assetContent[metricName],
			}),
		[data, metricName, publishedVersionData]
	);

	const metricsChartData = formattedData.data[VisitorsBehaviorDataKey.Metric];
	const publishedVersionsChartData =
		formattedData.data[VisitorsBehaviorDataKey.PublishedVersionData];

	const legendItems: IMetricsChartLegendProps['legendItems'] = [
		{
			Dot: CircleDot,
			dataKey: VisitorsBehaviorDataKey.Metric,
			dotColor: metricsChartData?.color ?? 'none',
			title: metricsChartData.title,
			total: metricsChartData.total,
		},
		{
			Dot: PublishedVersionDot,
			dataKey: VisitorsBehaviorDataKey.PublishedVersionData,
			dotColor: publishedVersionsChartData?.color ?? 'none',
			textColor: 'secondary',
			title: publishedVersionsChartData.title,
			total: publishedVersionsChartData.total,
		},
	];

	return (
		<>
			<MetricsChart
				MetricsChartTooltip={VisitorsBehaviorChartTooltip}
				activeTabIndex={activeTabIndex}
				emptyChartProps={{
					description: Liferay.Language.get(
						'check-back-later-to-see-if-your-data-sources-are-populated-with-data'
					),
					link: {
						title: Liferay.Language.get(
							'learn-more-about-visitors-behavior'
						),
						url: 'https://learn.liferay.com/w/dxp/content-authoring-and-management/content-dashboard/content-dashboard-interface',
					},
					show: !formattedData.combinedData.length,
					title: Liferay.Language.get(
						'there-is-no-data-for-visitors-behavior'
					),
				}}
				formattedData={formattedData}
				legendItems={legendItems}
				onChartBlur={() => setActiveTabIndex(false)}
				onChartFocus={() => setActiveTabIndex(true)}
				onDatakeyChange={(dataKey) =>
					setActiveLegendItem(dataKey as VisitorsBehaviorDataKey)
				}
				rangeSelector={filters.rangeSelector.rangeKey}
				tooltipTitle={Liferay.Language.get('visitors-behavior')}
				xAxisDataKey={VisitorsBehaviorDataKey.Metric}
			>
				<Line
					activeDot={
						<CircleDot stroke={metricsChartData.color ?? 'none'} />
					}
					animationDuration={100}
					dataKey={VisitorsBehaviorDataKey.Metric}
					dot={
						<CircleDot stroke={metricsChartData.color ?? 'none'} />
					}
					fill={Colors.Blue}
					fillOpacity={getFillOpacity(
						VisitorsBehaviorDataKey.Metric,
						activeLegendItem
					)}
					legendType="plainline"
					stroke={metricsChartData.color ?? 'none'}
					strokeOpacity={getFillOpacity(
						VisitorsBehaviorDataKey.Metric,
						activeLegendItem
					)}
					strokeWidth={2}
					type="linear"
				/>

				<Line
					activeDot={<PublishedVersionDot stroke={Colors.Black} />}
					animationDuration={100}
					dataKey={VisitorsBehaviorDataKey.PublishedVersionData}
					dot={<PublishedVersionDot stroke={Colors.Black} />}
					stroke={Colors.Black}
					strokeOpacity={getFillOpacity(
						VisitorsBehaviorDataKey.PublishedVersionData,
						activeLegendItem
					)}
					strokeWidth={2}
					type="monotone"
				/>
			</MetricsChart>

			{/* Used on playwright to test data */}

			<div
				data-qa-chart-data={JSON.stringify(
					formattedData.combinedData.map(
						(dataKey) => dataKey[VisitorsBehaviorDataKey.Metric]
					)
				)}
				data-qa-tooltip-formatted-date={JSON.stringify(
					formatDate(
						new Date(
							formattedData.combinedData[0]?.[
								VisitorsBehaviorDataKey.AxisX
							] ?? 0
						),
						filters.rangeSelector.rangeKey
					)
				)}
				data-testid="visitors-behavior-chart-data"
			/>
		</>
	);
};

export default VisitorsBehaviorChart;
