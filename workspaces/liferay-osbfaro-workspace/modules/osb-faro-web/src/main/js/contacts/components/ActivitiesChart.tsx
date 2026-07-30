import moment from 'moment';
import React, {useRef, useState} from 'react';
import {
	ANIMATION_DURATION,
	AXIS,
	ChartTooltipRow,
	getAxisTickText,
	getYAxisWidth,
	RechartsTooltip,
} from 'shared/util/recharts';
import {
	Bar,
	CartesianGrid,
	Cell,
	ComposedChart,
	Line,
	ReferenceLine,
	ResponsiveContainer,
	Tooltip,
	TooltipProps,
	XAxis,
	YAxis,
} from 'recharts';
import {CHART_COLOR_NAMES} from 'shared/util/charts';
import {
	ChartView,
	DEFAULT_CHART_VIEW,
} from 'shared/components/ChartViewSelector';
import {createDateKeysIMap} from 'shared/util/intervals';
import {
	formatXAxisDate,
	getBarColor,
	getDateTitle,
	getIntervals,
} from 'shared/util/charts';
import {get} from 'lodash';
import {Interval, RangeSelectors} from 'shared/types';

const {stark: CHART_BLUE} = CHART_COLOR_NAMES;

interface IChartProps<T> extends React.HTMLAttributes<HTMLElement> {
	alwaysShowSelectedTooltip: boolean;
	chartView?: ChartView;
	hasSelectedPoint?: boolean;
	height?: number;
	hideGrid?: boolean;
	history: Array<T>;
	interval: Interval;
	LDPEnabled?: boolean;
	onAfterInit?: () => void;
	onPointSelect: (index: number | null) => void;
	rangeSelectors: RangeSelectors;
	selectedPoint?: number;
	tooltipRenderRows?: (data: T) => ChartTooltipRow[];
}

interface IActivitiesHistoryProps<initDateType = number> {
	intervalInitDate: initDateType;
	totalEvents: number;
	totalSessions?: number;
	uniqueVisitors?: number;
}

const ActivitiesChart: React.FC<
	IChartProps<IActivitiesHistoryProps<number>>
> = ({
	LDPEnabled = false,
	alwaysShowSelectedTooltip = false,
	chartView = DEFAULT_CHART_VIEW,
	hasSelectedPoint,
	height = 340,
	hideGrid = false,
	history,
	interval,
	onPointSelect,
	rangeSelectors,
	selectedPoint,
	tooltipRenderRows,
}) => {
	const _tooltipRef = useRef<any>();

	const [hoverIndex, setHoverIndex] = useState(-1);
	const [mouseOutside, setMouseOutside] = useState(false);
	const [selectedTooltipX, setSelectedTooltipX] = useState<
		number | null | undefined
	>(null);

	const dateKeysIMap = createDateKeysIMap(
		interval,
		history,
		'intervalInitDate'
	);

	const renderTooltip = ({active, payload}: TooltipProps<number, string>) => {
		if ((active && !!payload?.length) || hasSelectedPoint) {
			const data: IActivitiesHistoryProps<number> & {
				totalSessions: number;
			} = get(
				payload,
				[0, 'payload'],
				selectedPoint !== undefined ? history[selectedPoint] : undefined
			);

			if (!data) {
				return null;
			}

			const {intervalInitDate, totalEvents, totalSessions} = data;

			const rows: ChartTooltipRow[] = tooltipRenderRows
				? tooltipRenderRows(data)
				: [
						{
							label: Liferay.Language.get('events'),
							value: totalEvents.toLocaleString(),
						},
						{
							label: Liferay.Language.get('sessions'),
							value: totalSessions.toLocaleString(),
						},
					];

			if (moment.utc(intervalInitDate).isSame(moment(), 'day')) {
				rows.push({
					className: 'text-info text-uppercase mt-4',
					label: Liferay.Language.get(
						'data-for-todays-events-may-vary-or-be-incomplete'
					),
				});
			}

			return (
				<RechartsTooltip
					dateTitle=""
					rows={rows}
					title={getDateTitle(
						dateKeysIMap.get(intervalInitDate),
						rangeSelectors.rangeKey,
						interval
					)}
				/>
			);
		}

		return null;
	};

	const intervals = getIntervals(
		rangeSelectors.rangeKey,
		history.map(({intervalInitDate}) => intervalInitDate),
		interval,
		dateKeysIMap
	);

	const showFixedTooltip = hasSelectedPoint && mouseOutside;

	const yAxisWidth = getYAxisWidth(history, 'totalEvents');

	return (
		<ResponsiveContainer height={height}>
			<ComposedChart
				accessibilityLayer
				data={history}
				onClick={(pointData) => {
					if (alwaysShowSelectedTooltip && pointData) {
						const tooltip = _tooltipRef.current;

						if (tooltip?.state?.boxWidth && tooltip.getTranslate) {
							setSelectedTooltipX(
								tooltip.getTranslate({
									key: 'x',
									tooltipDimension: tooltip.state.boxWidth,
									viewBoxDimension:
										tooltip.props.viewBox.width,
								})
							);
						}

						if (pointData.activeTooltipIndex !== undefined) {
							onPointSelect(pointData.activeTooltipIndex);
						}
					}
				}}
				onMouseLeave={() => setMouseOutside(true)}
				onMouseMove={() => setMouseOutside(false)}
			>
				{!hideGrid && (
					<CartesianGrid
						stroke={AXIS.gridStroke}
						strokeDasharray="3 3"
						vertical={false}
					/>
				)}

				<XAxis
					axisLine={{stroke: AXIS.borderStroke}}
					dataKey="intervalInitDate"
					domain={['dataMin', 'dataMax']}
					interval="preserveStart"
					padding={{left: 20, right: 20}}
					tick={getAxisTickText('x', (value) =>
						formatXAxisDate(
							value,
							rangeSelectors.rangeKey,
							interval,
							dateKeysIMap
						)
					)}
					tickLine={false}
					tickMargin={12}
					ticks={intervals.filter((v): v is number => v !== null)}
					type="number"
				/>

				<XAxis
					axisLine={{stroke: AXIS.borderStroke}}
					dataKey="intervalInitDate"
					orientation="top"
					stroke={AXIS.gridStroke}
					tick={false}
					tickLine={false}
					xAxisId="top"
				/>

				<YAxis
					allowDecimals={false}
					axisLine={{stroke: AXIS.borderStroke}}
					label={
						LDPEnabled
							? {
									dy: -20,
									position: 'top',
									style: {fill: AXIS.textColor},
									value: Liferay.Language.get('events'),
								}
							: undefined
					}
					name={Liferay.Language.get('events')}
					stroke={AXIS.gridStroke}
					tick={getAxisTickText('y')}
					tickCount={6}
					tickLine={false}
					type="number"
					width={yAxisWidth}
				/>

				<YAxis
					axisLine={{stroke: AXIS.borderStroke}}
					orientation="right"
					stroke={AXIS.gridStroke}
					tick={false}
					tickLine={false}
					type="number"
					width={1}
					yAxisId="right"
				/>

				{!hideGrid && (
					<Tooltip
						content={renderTooltip}
						cursor={{stroke: CHART_BLUE}}
						position={
							showFixedTooltip &&
							selectedTooltipX !== null &&
							selectedTooltipX !== undefined
								? {x: selectedTooltipX}
								: undefined
						}
						ref={_tooltipRef}
						wrapperStyle={
							showFixedTooltip
								? {visibility: 'visible'}
								: undefined
						}
					/>
				)}

				<ReferenceLine
					strokeWidth={1}
					x={
						selectedPoint !== undefined
							? history[selectedPoint]?.intervalInitDate
							: undefined
					}
				/>

				{chartView === 'line' ? (
					<Line
						activeDot={{r: 5}}
						animationDuration={ANIMATION_DURATION.line}
						dataKey="totalEvents"
						dot={false}
						stroke={CHART_BLUE}
						strokeWidth={2}
						type="linear"
					/>
				) : (
					<Bar
						animationDuration={ANIMATION_DURATION.bar}
						dataKey="totalEvents"
						fill={CHART_BLUE}
						onMouseEnter={(e, index) => setHoverIndex(index)}
						onMouseLeave={() => setHoverIndex(-1)}
					>
						{history.map((entry, index) => (
							<Cell
								fill={getBarColor(
									index,
									hoverIndex,
									selectedPoint
								)}
								key={`cell-${index}`}
							/>
						))}
					</Bar>
				)}
			</ComposedChart>
		</ResponsiveContainer>
	);
};

export default ActivitiesChart;
