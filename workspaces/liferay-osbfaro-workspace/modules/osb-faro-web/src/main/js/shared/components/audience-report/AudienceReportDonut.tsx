// @ts-nocheck - TEMP

import ChartTooltip from 'shared/components/chart-tooltip';
import React, {useState} from 'react';
import {AXIS} from 'shared/util/recharts';
import {
	Cell,
	Label,
	Legend,
	Pie,
	PieChart,
	ResponsiveContainer,
	Sector,
	Text,
	Tooltip,
} from 'recharts';
import {Text as ClayText} from '@clayui/core';
import {Dataset} from './types';
import {get} from 'lodash';
import {formatPercentFromRatio, toFixedPoint} from 'shared/util/numbers';

const EMPTY_CHART_COLOR = '#E7E7ED';

interface IActiveShapeProps {
	cx: number;
	cy: number;
	endAngle: number;
	fill: number;
	innerRadius: number;
	outerRadius: number;
	startAngle: number;
}

const ActiveShape: React.FC<IActiveShapeProps> = ({
	cx,
	cy,
	endAngle,
	fill,
	innerRadius,
	outerRadius,
	startAngle,
}) => (
	<g>
		<Sector
			cx={cx}
			cy={cy}
			endAngle={endAngle}
			fill={fill}
			innerRadius={innerRadius}
			outerRadius={outerRadius + 4}
			startAngle={startAngle}
		/>
	</g>
);

interface IBarLabelProps {
	cx: number;
	cy: number;
	innerRadius: number;
	isEmpty: boolean;
	midAngle: number;
	outerRadius: number;
	percent: number;
}

const BarLabel: React.FC<IBarLabelProps> = ({
	cx,
	cy,
	innerRadius,
	isEmpty,
	midAngle,
	outerRadius,
	percent,
}) => {
	const RADIAN = Math.PI / 180;

	const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
	const x = cx + radius * Math.cos(-midAngle * RADIAN);
	const y = cy + radius * Math.sin(-midAngle * RADIAN);

	if (!isEmpty && percent) {
		return (
			<Text
				style={{
					fill: 'black',
					font: AXIS.font,
					fontSize: '1rem',
					fontWeight: 600,
				}}
				textAnchor="middle"
				x={x}
				y={y}
			>
				{formatPercentFromRatio(percent)}
			</Text>
		);
	}

	return null;
};

interface ITooltipContentProps {
	isEmpty: boolean;
	active: boolean;
	payload: {
		count: number;
		label: string;
	}[];
}

const TooltipContent: React.FC<ITooltipContentProps> = ({
	active,
	isEmpty,
	payload,
}) => {
	if (!isEmpty && active && !!payload.length) {
		const {count, label} = get(payload, [0, 'payload'], {});

		return (
			<div className="bb-tooltip-container" style={{position: 'static'}}>
				<ChartTooltip
					rows={[
						{
							columns: [
								{
									className: 'pt-0',
									label: () => (
										<span style={{whiteSpace: 'nowrap'}}>
											<strong>
												{`${toFixedPoint(count)}`}
											</strong>

											{` ${label}`}
										</span>
									),
								},
							],
						},
					]}
				/>
			</div>
		);
	}

	return null;
};

interface IAudienceReportDonutProps extends Dataset {
	height?: number;
}

const AudienceReportDonut: React.FC<IAudienceReportDonutProps> = ({
	data = [],
	empty: {message: emptyMessage, show: isEmpty = false},
	height = 360,
	total = 0,
}) => {
	const [hoverIndex, setHoverIndex] = useState<number>(-1);

	const handleSetHoverIndex = (e, index) => {
		!isEmpty && setHoverIndex(index);
	};

	const handleResetHoverIndex = () => {
		!isEmpty && setHoverIndex(-1);
	};

	return (
		<div className="audience-report-chart-donut">
			<ResponsiveContainer height={height}>
				<PieChart>
					<Tooltip
						content={(props: ITooltipContentProps) => (
							<TooltipContent {...props} />
						)}
					/>

					<Legend
						formatter={(_, {payload: {label}}) => {
							if (isEmpty) {
								return (
									<div className="text-center pl-4 pr-4">
										{emptyMessage}
									</div>
								);
							}

							return (
								<ClayText
									className="legend-item"
									color="secondary"
									size={3}
								>
									{label}
								</ClayText>
							);
						}}
						iconSize={isEmpty ? 0 : 12}
						layout="vertical"
						onBlur={() => {}}
						onMouseMove={handleSetHoverIndex}
						onMouseOut={handleResetHoverIndex}
						verticalAlign="bottom"
					/>

					<Pie
						activeIndex={hoverIndex}
						activeShape={(props: IActiveShapeProps) => (
							<ActiveShape {...props} />
						)}
						blendStroke
						cy={142}
						data={
							isEmpty
								? [
										{
											color: EMPTY_CHART_COLOR,
											count: 1,
											label: 'empty',
										},
									]
								: data
						}
						dataKey="count"
						endAngle={-270}
						innerRadius="50%"
						isAnimationActive={false}
						label={(props: IBarLabelProps) => (
							<BarLabel {...props} isEmpty={isEmpty} />
						)}
						labelLine={false}
						legendType="circle"
						onBlur={() => {}}
						onMouseMove={handleSetHoverIndex}
						onMouseOut={handleResetHoverIndex}
						outerRadius="90%"
						startAngle={90}
					>
						<Label position="center" value={toFixedPoint(total)} />

						{isEmpty ? (
							<Cell
								fill={EMPTY_CHART_COLOR}
								fillOpacity={1}
								key="cell-empty"
								strokeOpacity={1}
							/>
						) : (
							data.map(({color}, index) => (
								<Cell
									fill={color}
									fillOpacity={
										hoverIndex >= 0 && hoverIndex !== index
											? 0.2
											: 1
									}
									key={`cell-${index}`}
									strokeOpacity={
										hoverIndex >= 0 && hoverIndex !== index
											? 0
											: 1
									}
								/>
							))
						)}
					</Pie>
				</PieChart>
			</ResponsiveContainer>
		</div>
	);
};

export default AudienceReportDonut;
