import moment from 'moment';
import React, {useState} from 'react';
import {ANIMATION_DURATION, AXIS, getAxisTickText} from 'shared/util/recharts';
import {
	Area,
	CartesianGrid,
	ComposedChart as ComposedChartRecharts,
	Legend,
	Line,
	ResponsiveContainer,
	Tooltip as TooltipRechart,
	XAxis,
	YAxis,
} from 'recharts';
import {CHART_COLOR_NAMES} from 'shared/util/charts';
import {CONTROL_COLOR} from '../util/constants';
import {getAxisMeasuresFromData} from 'shared/util/charts';
import {getDate, getDayMonthFormat} from 'shared/util/date';
import {getShortIntervals} from 'experiments/util/experiments';

const {stark: CHART_BLUE} = CHART_COLOR_NAMES;

interface IComposedChartProps {
	chartType?: 'line' | 'area';
	data: {
		controlLabel: string;
		data: Array<Record<string, any>>;
		format: (value: any) => string;
		intervals: Array<string | number>;
		variantLabel?: string;
	};
	Tooltip: React.ComponentType<any>;
}

export const ComposedChart = ({
	Tooltip,
	chartType = 'line',
	data: initialData,
}: IComposedChartProps) => {
	const {controlLabel, data, format, intervals, variantLabel} = initialData;

	const [hoverLegend, setHoverLegend] = useState<string | null>(null);

	const getStrokeOpacity = (key: string) =>
		hoverLegend === key || !hoverLegend ? 1 : 0.2;

	const getFillOpacity = (key: string) =>
		hoverLegend === key || !hoverLegend ? 0.1 : 0.2;

	let customIntervals = intervals;

	// Shorten intervals of tickX

	if (intervals.length >= 12) {
		customIntervals = getShortIntervals(intervals);
	}

	const groupedData = data
		.map((item: Record<string, any>) => [
			item['data_control'],
			item['data_variant'],
		])
		.flat();

	return (
		<ResponsiveContainer height={320}>
			<ComposedChartRecharts data={data} height={320}>
				<CartesianGrid
					stroke={AXIS.gridStroke}
					strokeDasharray="3 3"
					vertical={false}
				/>

				<XAxis
					axisLine={{stroke: AXIS.borderStroke}}
					dataKey="key"
					interval="preserveStart"
					tickFormatter={(date) =>
						moment.utc(getDate(date)).format(getDayMonthFormat())
					}
					tickLine={false}
					ticks={customIntervals}
					type="category"
				/>

				<XAxis
					axisLine={{stroke: AXIS.borderStroke}}
					dataKey="key"
					interval="preserveStart"
					orientation="top"
					stroke={AXIS.gridStroke}
					tick={false}
					tickLine={false}
					ticks={customIntervals}
					xAxisId="top"
				/>

				<YAxis
					axisLine={{stroke: AXIS.borderStroke}}
					stroke={AXIS.gridStroke}
					tick={getAxisTickText('y', (value) => format(value))}
					tickLine={false}
					ticks={getAxisMeasuresFromData(groupedData).intervals}
					type="number"
				/>

				<YAxis
					axisLine={{
						stroke: AXIS.borderStroke,
					}}
					orientation="right"
					stroke={AXIS.gridStroke}
					tick={false}
					tickLine={false}
					width={12}
					yAxisId="right"
				/>

				{chartType === 'line' && (
					<>
						<Line
							animationDuration={ANIMATION_DURATION.line}
							dataKey="data_control"
							dot={false}
							fill={CONTROL_COLOR}
							legendType="circle"
							name={controlLabel}
							stroke={CONTROL_COLOR}
							strokeOpacity={getStrokeOpacity('data_control')}
							strokeWidth="2"
						/>

						{variantLabel && (
							<Line
								animationDuration={ANIMATION_DURATION.line}
								dataKey="data_variant"
								dot={false}
								fill={CHART_BLUE}
								legendType="circle"
								name={variantLabel}
								stroke={CHART_BLUE}
								strokeOpacity={getStrokeOpacity('data_variant')}
								strokeWidth="2"
							/>
						)}
					</>
				)}

				{chartType === 'area' && (
					<>
						<Area
							animationDuration={ANIMATION_DURATION.line}
							dataKey="data_control"
							dot={false}
							fill={CONTROL_COLOR}
							fillOpacity={getFillOpacity('data_control')}
							legendType="circle"
							name={controlLabel}
							stroke={CONTROL_COLOR}
							strokeOpacity={getStrokeOpacity('data_control')}
							strokeWidth="2"
						/>

						{variantLabel && (
							<Area
								animationDuration={ANIMATION_DURATION.line}
								dataKey="data_variant"
								dot={false}
								fill={CHART_BLUE}
								fillOpacity={getFillOpacity('data_variant')}
								legendType="circle"
								name={variantLabel}
								stroke={CHART_BLUE}
								strokeOpacity={getStrokeOpacity('data_variant')}
								strokeWidth="2"
							/>
						)}
					</>
				)}

				<TooltipRechart
					content={({active, payload}) => {
						if (active && !!payload?.length) {
							return <Tooltip dataPoint={payload} />;
						}

						return null;
					}}
				/>

				<Legend
					align="right"
					iconSize={8}
					onMouseEnter={
						(({dataKey}: {dataKey: string}) =>
							setHoverLegend(dataKey)) as any
					}
					onMouseLeave={() => setHoverLegend(null)}
					verticalAlign="bottom"
					wrapperStyle={{
						fontSize: '14px',
						lineHeight: '20px',
					}}
				/>
			</ComposedChartRecharts>
		</ResponsiveContainer>
	);
};
