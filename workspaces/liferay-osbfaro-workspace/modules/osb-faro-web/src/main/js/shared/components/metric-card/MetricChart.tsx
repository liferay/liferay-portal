import Checkbox from 'shared/components/Checkbox';
import ClayLink from '@clayui/link';
import ComposedChartWithEmptyState from 'shared/components/ComposedChartWithEmptyState';
import MetricStateRenderer from './MetricStateRenderer';
import MetricTooltip from './MetricTooltip';
import React, {useMemo, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	ANIMATION_DURATION,
	AXIS,
	getAxisTickText,
	getTextWidth,
	getYAxisLabel,
} from 'shared/util/recharts';
import {
	Bar,
	CartesianGrid,
	Cell,
	ComposedChart,
	Legend,
	Line,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts';
import {
	CHART_DATA_PREVIOUS,
	getActiveItem,
	getMetricData,
	getMetricName,
	METRIC_TOOLTIP_LABEL_MAP,
} from './util';
import {formatXAxisDate} from 'shared/util/charts';
import {ICommonMetricProps, useActions, useData} from './MetricBaseCard';
import {Interval, RangeSelectors} from 'shared/types';
import {useMetricQuery} from './hooks';
import {useRetentionPeriod} from 'shared/hooks/useRetentionPeriod';

export {CHART_DATA_PREVIOUS, METRIC_TOOLTIP_LABEL_MAP};

type TChartSeries = {
	color?: string;
	data: any;
	dataName?: string;
	id: string;
	name?: string;
	tooltipTitle?: string;
	type?: string;
};

interface IMetricChartProps extends Partial<ICommonMetricProps> {
	chartHeight?: number;
	compareToPrevious: boolean;
	data: any;

	// @deprecated. It is used only on CustomAsset

	onCompareToPreviousChange?: (compareToPrevious: boolean) => void;
}

export const MetricChart: React.FC<IMetricChartProps> = ({
	chartHeight: height = 350,
	compareToPrevious,
	data,
	interval,
	onCompareToPreviousChange,
	rangeSelectors,
}) => {
	const {changeCompareToPrevious} = useActions();

	const [hoveredLegendItem, setHoveredLegendItem] = useState<string | null>(
		null
	);
	const [hoverIndex, setHoverIndex] = useState<number>(-1);

	const {
		chartData,
		compositeContent,
		content: {name, title},
		dateKeysIMap,
		format,
		intervals,
		timeline,
	} = useMemo(
		() => getActiveItem(data, compareToPrevious),
		[compareToPrevious, data]
	);

	const dataIds: string[] = chartData.map((item: TChartSeries) => item.id);

	let yAxisWidth = 40;

	const combinedData = useMemo(
		() =>
			timeline.data.map((date: number, i: number) =>
				dataIds.reduce<Record<string, any>>(
					(acc, item, j) => {
						acc[item] = chartData[j].data[i];

						const textWidth = getTextWidth(
							format(chartData[j].data[i])
						);

						if (yAxisWidth < textWidth) {
							yAxisWidth = textWidth;
						}

						return acc;
					},
					{
						date,
						dateString: formatXAxisDate(
							date,
							rangeSelectors!.rangeKey,
							interval as Interval,
							dateKeysIMap
						),
					}
				)
			),
		[timeline, dataIds, chartData]
	);

	const barData = chartData.filter(
		(item: TChartSeries) => item.type === 'bar'
	);

	const lineData = chartData.filter((item: TChartSeries) => {
		if (!compareToPrevious && item.id === CHART_DATA_PREVIOUS) {
			return false;
		}

		return item.type !== 'bar';
	});

	const retentionPeriod = useRetentionPeriod();

	return (
		<>
			<ResponsiveContainer height={height}>
				<ComposedChart data={combinedData}>
					<CartesianGrid
						stroke={AXIS.gridStroke}
						strokeDasharray="3 3"
						vertical={false}
					/>

					<XAxis
						axisLine={{stroke: AXIS.borderStroke}}
						dataKey="date"
						interval="preserveStart"
						stroke={AXIS.gridStroke}
						tick={getAxisTickText('x', (int: number | string) =>
							formatXAxisDate(
								int,
								rangeSelectors!.rangeKey,
								interval as Interval,
								dateKeysIMap
							)
						)}
						tickLine={false}
						tickMargin={12}
						ticks={intervals}
					/>

					<XAxis
						axisLine={{stroke: AXIS.borderStroke}}
						dataKey="date"
						orientation="top"
						stroke={AXIS.gridStroke}
						tick={false}
						tickLine={false}
						xAxisId="top"
					/>

					<YAxis
						axisLine={{stroke: AXIS.borderStroke}}
						label={getYAxisLabel(
							METRIC_TOOLTIP_LABEL_MAP[name] || title,
							'left',
							yAxisWidth
						)}
						stroke={AXIS.gridStroke}
						tick={getAxisTickText('y', format)}
						tickLine={false}
						width={yAxisWidth}
					/>

					<YAxis
						axisLine={{stroke: AXIS.borderStroke}}
						orientation="right"
						stroke={AXIS.gridStroke}
						tick={false}
						tickLine={false}
						width={12}
						yAxisId="right"
					/>

					<Tooltip
						content={(props: any) => (
							<MetricTooltip
								compareToPrevious={compareToPrevious}
								data={data}
								interval={interval as Interval}
								rangeSelectors={
									rangeSelectors as RangeSelectors
								}
								retentionPeriod={retentionPeriod}
								{...props}
							/>
						)}
						cursor={!!intervals.length}
					/>

					<Legend
						align="right"
						formatter={(label: string, {dataKey}: any) => {
							if (
								compositeContent &&
								dataKey !== CHART_DATA_PREVIOUS
							) {
								const barItem = barData.find(
									({id}: TChartSeries) => id === dataKey
								);
								const dataName = barItem?.dataName;

								if (dataName && compositeContent[dataName]) {
									const {value} = compositeContent[dataName];

									return (
										<span className="legend-text-color">
											{label}
											<b className="ml-1">{value}</b>
										</span>
									);
								}
							}

							return label;
						}}
						iconSize={8}
						onMouseEnter={({dataKey}: any) =>
							setHoveredLegendItem(dataKey)
						}
						onMouseLeave={() => setHoveredLegendItem(null)}
						verticalAlign="bottom"
						wrapperStyle={{
							bottom: 'auto',
							color: AXIS.textColor,
							fontSize: '14px',
							lineHeight: '21px',
						}}
					/>

					{barData.map((item: TChartSeries) => (
						<Bar
							animationDuration={ANIMATION_DURATION.bar}
							dataKey={item.id}
							fill={item.color}
							fillOpacity={
								hoveredLegendItem === item.id ||
								!hoveredLegendItem
									? 1
									: 0.2
							}
							key={item.id}
							legendType="circle"
							name={item.name}
							onMouseEnter={(_e: unknown, index: number) =>
								setHoverIndex(index)
							}
							onMouseLeave={() => setHoverIndex(-1)}
							stackId="a"
						>
							{item.data.map((_entry: unknown, index: number) => (
								<Cell
									fill={item.color}
									key={`cell-${index}`}
									opacity={index === hoverIndex ? 0.75 : 1}
								/>
							))}
						</Bar>
					))}

					{lineData.map((item: TChartSeries) => (
						<Line
							animationDuration={ANIMATION_DURATION.line}
							dataKey={item.id}
							dot={false}
							fill={item.color}
							key={item.id}
							legendType="plainline"
							name={item.name}
							stroke={item.color}
							strokeDasharray={
								item.id === CHART_DATA_PREVIOUS
									? '5 5'
									: undefined
							}
							strokeOpacity={
								hoveredLegendItem === item.id ||
								!hoveredLegendItem
									? 1
									: 0.2
							}
							strokeWidth={2}
							type="linear"
						/>
					))}
				</ComposedChart>
			</ResponsiveContainer>

			<div className="analytics-metrics-chart-sub-content-wrapper">
				<Checkbox
					checked={compareToPrevious}
					label={Liferay.Language.get('compare-to-previous')}
					onChange={() => {
						changeCompareToPrevious(!compareToPrevious);

						onCompareToPreviousChange?.(!compareToPrevious);
					}}
				/>
			</div>

			<div
				data-qa-is-chart-populated={data.data[0].data.some(
					(value: number) => value
				)}
			/>
		</>
	);
};

interface IMetricChartWrapperProps {
	data: any;
	emptyDescription?: string | React.ReactElement;
	emptyTitle?: string;
	interval: Interval;
	metricName: string;
	rangeSelectors: RangeSelectors;
}

const MetricChartWrapper: React.FC<IMetricChartWrapperProps> = ({
	data,
	emptyDescription = (
		<>
			<span className="mr-1">
				{Liferay.Language.get(
					'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
				)}
			</span>

			<ClayLink
				href={URLConstants.SitesDashboardSitesActivities}
				key="DOCUMENTATION"
				target="_blank"
			>
				{Liferay.Language.get('learn-more-about-site-activity')}
			</ClayLink>
		</>
	),
	emptyTitle = Liferay.Language.get('there-is-no-data-for-site-activity'),
	interval,
	metricName,
	rangeSelectors,
}) => {
	const {chartDataMapFn, compareToPrevious, metrics, queries} = useData();

	const {compositeMetrics, name, title, tooltipTitle, type} = metrics.find(
		({name}: {name: string}) => name === metricName
	);

	const formattedData = useMemo(
		() =>
			getMetricData({
				chartDataMapFn,
				compositeMetrics,
				interval,
				name,
				rangeSelectors,
				result: data[queries.name],
				title,
				tooltipTitle,
				type,
			}),
		[data, interval, metricName, rangeSelectors]
	);

	const {intervals} = useMemo(
		() => getActiveItem(formattedData, compareToPrevious),
		[formattedData, compareToPrevious]
	);

	return (
		<div className="analytics-metrics-chart">
			<ComposedChartWithEmptyState
				emptyDescription={emptyDescription}
				emptyTitle={emptyTitle}
				showEmptyState={!intervals.length}
			>
				<MetricChart
					compareToPrevious={compareToPrevious}
					data={formattedData}
					interval={interval}
					rangeSelectors={rangeSelectors}
				/>
			</ComposedChartWithEmptyState>
		</div>
	);
};

const MetricChartRenderer: React.FC<ICommonMetricProps> = ({
	accountId,
	emptyDescription,
	emptyTitle,
	experienceId,
	filters,
	interval,
	rangeSelectors,
}) => {
	const {activeItemIndex, metrics, queries, variables} = useData();

	const metricName = getMetricName(activeItemIndex, metrics);

	const {data, error, loading} = useMetricQuery({
		accountId,
		experienceId,
		filters,
		interval,
		Query: queries.MetricQuery(metricName),
		rangeSelectors,
		variables,
	});

	return (
		<MetricStateRenderer error={error} loading={loading}>
			<MetricChartWrapper
				data={data}
				emptyDescription={
					emptyDescription as string | React.ReactElement | undefined
				}
				emptyTitle={emptyTitle}
				interval={interval}
				metricName={metricName}
				rangeSelectors={rangeSelectors}
			/>
		</MetricStateRenderer>
	);
};

export default MetricChartRenderer;
