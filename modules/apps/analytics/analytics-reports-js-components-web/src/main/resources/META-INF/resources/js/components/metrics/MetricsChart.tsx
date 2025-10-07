/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useRef, useState} from 'react';
import {
	Bar,
	CartesianGrid,
	ComposedChart,
	ResponsiveContainer,
	Text,
	TextProps,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts';

import {RangeSelectors} from '../RangeSelectorsDropdown';
import AccessibleTick, {IAccessibleTickProps} from './AccessibleTick';
import EmptyChart, {IChartEmptyStateProps} from './EmptyChart';
import MetricsChartLegend, {
	IMetricsChartLegendProps,
} from './MetricsChartLegend';
import {FormattedData, calculateTickIntervals, formatXAxisDate} from './utils';

export enum DataKey {
	AxisX = 'x',
	AxisY = 'y',
}

export function getAccessibleAxisX({
	activeTabIndex,
	changeTooltipProps,
	formatter,
	intervals,
	onTickBlur,
	rangeSelector,
}: {
	activeTabIndex: boolean;
	changeTooltipProps: (props: IAccessibleTickProps) => void;
	formatter: (value: number) => string | number;
	intervals: (null | number)[];
	onTickBlur: () => void;
	rangeSelector: RangeSelectors;
}) {
	return (tickProps: IAccessibleTickProps & {textAnchor: any}) => {
		const {index, textAnchor, x, y} = tickProps;

		const tickIntervals = calculateTickIntervals(
			intervals as number[],
			rangeSelector
		);

		const shouldRenderText = tickIntervals.includes(
			intervals[index] as number
		);

		return (
			<>
				<AccessibleTick
					{...tickProps}
					activeTabIndex={activeTabIndex}
					onTickBlur={onTickBlur}
					showTooltip={({index}) => {
						changeTooltipProps({
							index,
							visible: true,
							x: 0,
							y: 0,
						});
					}}
				/>

				{shouldRenderText && (
					<Text
						style={{
							fill: '#6B6C7E',
							fontSize: '0.75rem',
						}}
						textAnchor={textAnchor}
						x={x}
						y={y}
					>
						{formatter(intervals[index] as number)}
					</Text>
				)}
			</>
		);
	};
}

interface IMetricsChartProps extends React.HTMLAttributes<HTMLElement> {
	MetricsChartTooltip: React.JSXElementConstructor<any>;
	activeTabIndex: boolean;
	emptyChartProps: IChartEmptyStateProps;
	formattedData: FormattedData;
	legendAlign?: string;
	legendItems: IMetricsChartLegendProps['legendItems'];
	onChartBlur: () => void;
	onChartFocus: () => void;
	onDatakeyChange: (dataKey: string | null) => void;
	rangeSelector: RangeSelectors;
	tooltipTitle: string;
	xAxisDataKey: string;
}

function getAxisTickY(formatter?: (value: number) => string | number) {
	return ({
		payload: {offset, value},
		textAnchor,
		x,
		y,
	}: {
		payload: {
			offset: number;
			value: number;
		};
		textAnchor: TextProps['textAnchor'];
		x: number;
		y: number;
	}) => (
		<Text
			style={{
				fill: '#6B6C7E',
				fontSize: '0.75rem',
			}}
			textAnchor={textAnchor}
			x={x}
			y={y + offset}
		>
			{formatter ? formatter(value) : value}
		</Text>
	);
}

const MetricsChart: React.FC<IMetricsChartProps> = ({
	MetricsChartTooltip,
	activeTabIndex,
	children,
	emptyChartProps,
	formattedData,
	legendAlign = 'text-left',
	legendItems,
	onChartBlur,
	onChartFocus,
	onDatakeyChange,
	rangeSelector,
	tooltipTitle,
	xAxisDataKey,
}) => {
	const metricChartRef = useRef<HTMLDivElement>(null);

	const [tooltipProps, setTooltipProps] = useState<IAccessibleTickProps>({
		index: 0,
		payload: {},
		visible: false,
		x: 0,
		y: 0,
	});

	const handleChangeTooltip = (tooltipProps: IAccessibleTickProps) => {
		setTooltipProps((prevState) => ({...prevState, ...tooltipProps}));
	};

	return (
		<div
			className="metrics-chart tab-focus"
			onKeyDown={(event) => {
				if (event.key === 'Enter') {
					metricChartRef.current?.blur();

					if (emptyChartProps.show) {
						const firstLegend =
							metricChartRef.current?.querySelector(
								'.metrics-chart__legend li:first-child'
							) as HTMLElement;

						firstLegend.focus();
					}
					else {
						handleChangeTooltip({
							index: 0,
							visible: true,
							x: 0,
							y: 0,
						});

						const firstTick =
							metricChartRef.current?.querySelectorAll(
								'.accessibility-tick-line'
							)?.[0] as HTMLElement;

						if (firstTick) {
							firstTick.classList.add('active');

							firstTick.focus();
						}
					}

					onChartFocus();
				}
			}}
			ref={metricChartRef}
			tabIndex={0}
		>
			<EmptyChart {...emptyChartProps}>
				<ResponsiveContainer height={275}>
					<ComposedChart
						data={formattedData.combinedData}
						onMouseLeave={() => {
							handleChangeTooltip({
								index: 0,
								visible: false,
								x: 0,
								y: 0,
							});
						}}
						onMouseMove={(event) => {
							handleChangeTooltip({
								index: event?.activeTooltipIndex ?? 0,
								visible: true,
								x: event?.activeCoordinate?.x ?? 0,
								y: event?.activeCoordinate?.y ?? 0,
							});
						}}
					>
						<CartesianGrid
							stroke="#E7E7ED"
							strokeDasharray="3 3"
							vertical={false}
						/>

						<XAxis
							axisLine={{
								stroke: '#E7E7ED',
							}}
							dataKey={xAxisDataKey}

							// eslint-disable-next-line react-compiler/react-compiler
							tick={getAccessibleAxisX({
								activeTabIndex,
								changeTooltipProps: handleChangeTooltip,
								formatter: (value) =>
									formatXAxisDate(value, rangeSelector),
								intervals: formattedData.intervals,
								onTickBlur: () => {
									metricChartRef.current?.focus();

									onChartBlur();
								},
								rangeSelector,
							})}
							tickLine={false}
							tickMargin={14}
						/>

						<YAxis
							axisLine={{
								stroke: '#E7E7ED',
							}}
							stroke="#E7E7ED"
							tick={getAxisTickY(
								formattedData.data[DataKey.AxisY]?.format
							)}
							tickLine={false}
							width={40}
						/>

						{/* Hack to display the grid when there are no data to display */}

						{!formattedData.combinedData.length && (
							<Bar dataKey={DataKey.AxisY} stackId="a" />
						)}

						<Tooltip
							content={() => {
								if (tooltipProps.visible) {
									return (
										<MetricsChartTooltip
											{...tooltipProps}
											formattedData={formattedData}
											rangeSeletor={rangeSelector}
											title={tooltipTitle}
										/>
									);
								}

								return null;
							}}
							cursor={!!formattedData.intervals.length}
							wrapperStyle={{
								visibility: tooltipProps.visible
									? 'visible'
									: 'hidden',
							}}
						/>

						{children}
					</ComposedChart>
				</ResponsiveContainer>

				<MetricsChartLegend
					activeTabIndex={activeTabIndex}
					align={legendAlign}
					legendItems={legendItems}
					onDatakeyChange={onDatakeyChange}
				/>
			</EmptyChart>
		</div>
	);
};

export default MetricsChart;
