/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useMemo, useState} from 'react';
import {Line} from 'recharts';

import {Context} from '../../../Context';
import {formatDate} from '../../../utils/date';
import {assetContent, metricNameByType} from '../../../utils/metrics';
import {CircleDot, DiamondDot, DotProps, SquareDot} from '../../metrics/Dots';
import MetricsChart, {DataKey} from '../../metrics/MetricsChart';
import {IMetricsChartLegendProps} from '../../metrics/MetricsChartLegend';
import {getFillOpacity} from '../../metrics/utils';
import {Data} from './InteractionsByPage';
import InteractionsByPageChartTooltip from './InteractionsByPageChartTooltip';
import {InteractionsByPageDataKey, formatInteractionsByPageData} from './utils';

interface IInteractionsByPageChartProps {
	data: Data;
}

export const Dot: {
	[key in InteractionsByPageDataKey]: React.JSXElementConstructor<any>;
} = {
	[InteractionsByPageDataKey.Page1]: CircleDot,
	[InteractionsByPageDataKey.Page2]: DiamondDot,
	[InteractionsByPageDataKey.Page3]: SquareDot,
	[InteractionsByPageDataKey.AxisX]: () => <></>,
	[InteractionsByPageDataKey.AxisY]: () => <></>,
};

const InteractionsByPageChart: React.FC<IInteractionsByPageChartProps> = ({
	data,
}) => {
	const {filters} = useContext(Context);

	const [activeTabIndex, setActiveTabIndex] = useState(false);

	const [activeLegendItem, setActiveLegendItem] = useState<
		InteractionsByPageDataKey | DataKey | null
	>(null);

	const metricName = metricNameByType[filters.metric];

	const formattedData = useMemo(
		() =>
			formatInteractionsByPageData({
				data,
				metricName,
				...assetContent[metricName],
			}),
		[data, metricName]
	);

	const legendItems = Object.keys(formattedData.data)
		.map((dataKey, index) => {
			if (dataKey === DataKey.AxisX || dataKey === DataKey.AxisY) {
				return false;
			}

			const data =
				formattedData.data[dataKey as InteractionsByPageDataKey];

			const SelectedDot: React.JSXElementConstructor<DotProps> =
				Dot[dataKey as InteractionsByPageDataKey];

			return {
				Dot: SelectedDot,
				block: true,
				dataKey,
				dotColor: data?.color ?? 'none',
				textColor: index !== 0 ? undefined : 'dark',
				title: data.title,
				total: data.total,
				url: data?.url,
			};
		})
		.filter(Boolean);

	return (
		<>
			<MetricsChart
				MetricsChartTooltip={InteractionsByPageChartTooltip}
				activeTabIndex={activeTabIndex}
				emptyChartProps={{
					description: Liferay.Language.get(
						'check-back-later-to-see-if-your-data-sources-are-populated-with-data'
					),
					link: {
						title: Liferay.Language.get(
							'learn-more-about-top-pages-asset-appears-on'
						),
						url: 'https://learn.liferay.com/w/dxp/content-authoring-and-management/content-dashboard/content-dashboard-interface',
					},
					show: !formattedData.combinedData.length,
					title: Liferay.Language.get(
						'there-is-no-data-for-top-pages-asset-appears-on'
					),
				}}
				formattedData={formattedData}
				legendItems={
					legendItems as IMetricsChartLegendProps['legendItems']
				}
				onChartBlur={() => setActiveTabIndex(false)}
				onChartFocus={() => setActiveTabIndex(true)}
				onDatakeyChange={(dataKey) =>
					setActiveLegendItem(dataKey as InteractionsByPageDataKey)
				}
				rangeSelector={filters.rangeSelector.rangeKey}
				tooltipTitle={
					assetContent[metricName].interactionsByPageTooltipTitle
				}
				xAxisDataKey={InteractionsByPageDataKey.Page1}
			>
				{Object.keys(formattedData.data).map((dataKey) => {
					if (
						dataKey === DataKey.AxisX ||
						dataKey === DataKey.AxisY
					) {
						return null;
					}

					const data =
						formattedData.data[
							dataKey as InteractionsByPageDataKey
						];

					const SelectedDot: React.JSXElementConstructor<DotProps> =
						Dot[dataKey as InteractionsByPageDataKey];

					return (
						<Line
							activeDot={
								<SelectedDot stroke={data?.color ?? 'none'} />
							}
							animationDuration={100}
							dataKey={dataKey}
							dot={<SelectedDot stroke={data?.color ?? 'none'} />}
							fill={data.color}
							fillOpacity={getFillOpacity(
								dataKey,
								activeLegendItem
							)}
							key={dataKey}
							legendType="plainline"
							stroke={data.color}
							strokeOpacity={getFillOpacity(
								dataKey,
								activeLegendItem
							)}
							strokeWidth={2}
							type="linear"
						/>
					);
				})}
			</MetricsChart>

			{/* Used on playwright to test data */}

			<div
				data-qa-page-1-chart-data={JSON.stringify(
					formattedData.combinedData.map(
						(dataKey: any) =>
							dataKey[InteractionsByPageDataKey.Page1]
					)
				)}
				data-qa-page-2-chart-data={JSON.stringify(
					formattedData.combinedData.map(
						(dataKey: any) =>
							dataKey[InteractionsByPageDataKey.Page2]
					)
				)}
				data-qa-page-3-chart-data={JSON.stringify(
					formattedData.combinedData.map(
						(dataKey: any) =>
							dataKey[InteractionsByPageDataKey.Page3]
					)
				)}
				data-qa-tooltip-formatted-date={JSON.stringify(
					formatDate(
						new Date(
							formattedData.combinedData[0]?.[
								InteractionsByPageDataKey.AxisX
							] ?? 0
						),
						filters.rangeSelector.rangeKey
					)
				)}
				data-testid="interactions-by-page-chart-data"
			/>
		</>
	);
};

export default InteractionsByPageChart;
