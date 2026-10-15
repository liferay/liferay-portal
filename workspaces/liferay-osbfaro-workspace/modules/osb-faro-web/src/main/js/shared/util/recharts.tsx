import ChartTooltip, {
	Alignments,
	Weights,
} from 'shared/components/chart-tooltip';
import React, {FC} from 'react';
import {Column} from 'shared/components/chart-tooltip/types';
import {Text} from 'recharts';

const AXIS_LABEL_OFFSET = 20;
const TEXT_PADDING = 4;
const Y_AXIS_WIDTH = 30;

export const ANIMATION_DURATION = {
	bar: 800,
	line: 1000,
};

export const AXIS = {
	borderStroke: '#E7E7ED',
	font: '14px -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol"',
	gridStroke: '#E7E7ED',
	textColor: '#6B6C7E',
};

export const BAR_COLORS = {
	blue: {
		default: '#4B9BFF',
		hover: '#318DFF',
		notSelected: '#97C5FF',
		selected: '#0071FD',
	},
	orange: {
		default: '#FFB46E',
		hover: '#FFA754',
		notSelected: '#FFCEA1',
		selected: '#FF8C21',
	},
};

export interface ChartTooltipRow {
	className?: string;
	label: string | (() => React.ReactNode);
	value?: string;
}

export const getTextWidth: (text: any, font?: string) => number = (
	text,
	font = AXIS.font
) => {
	const canvas =
		(getTextWidth as any).canvas ||
		((getTextWidth as any).canvas = document.createElement('canvas'));
	const context = canvas.getContext('2d');
	context.font = font;
	const metrics = context.measureText(text);

	return Math.ceil(metrics.width) + TEXT_PADDING;
};

type AxisTickProps = {
	payload: {offset: number; value: number | string};
	textAnchor?: 'inherit' | 'end' | 'start' | 'middle';
	x: number;
	y: number;
};

export const getAxisTickText =
	(
		axis = 'x',
		formatter: (val: number | string) => string | number = (val) => val
	) =>
	({payload: {offset, value}, textAnchor, x, y}: AxisTickProps) => (
		<Text
			style={{
				fill: AXIS.textColor,
				font: AXIS.font,
				fontSize: '0.75rem',
			}}
			textAnchor={textAnchor}
			x={x}
			y={axis === 'y' ? y + offset : y}
		>
			{formatter(value)}
		</Text>
	);

interface IRechartsTooltipProps extends React.HTMLAttributes<HTMLElement> {
	dateTitle?: string;
	rows: {
		className?: string;
		label?: string | (() => React.ReactNode);
		value?: string;
	}[];
}

export const RechartsTooltip: FC<IRechartsTooltipProps> = ({
	dateTitle,
	rows,
	title,
}) => (
	<div className="bb-tooltip-container" style={{position: 'static'}}>
		<ChartTooltip
			header={[
				{
					columns: [
						{
							label: title ?? '',
							weight: Weights.Semibold,
							width: 150,
						},
						{
							align: Alignments.Right,
							label: dateTitle ?? '',
							weight: Weights.Semibold,
							width: 55,
						},
					],
				},
			]}
			rows={rows.map(({className, label, value}) => ({
				columns: [
					{
						className,
						label,
						weight: 'normal',
					},
					{
						align: 'right',
						label: value,
						weight: 'semibold',
					},
				] as Column[],
			}))}
		/>
	</div>
);

type YAxisLabelViewBox = {
	viewBox: {height: number; width: number; x: number; y: number};
};

export const getYAxisLabel =
	(label: string | number, position = 'left', yAxisWidth = Y_AXIS_WIDTH) =>
	({viewBox: {height, width, x, y}}: YAxisLabelViewBox) => {
		const verticalSign = height >= 0 ? 1 : -1;

		const verticalEnd = verticalSign > 0 ? 'end' : 'start';

		const textAnchor = position === 'right' ? 'end' : 'start';

		return (
			<Text
				fill={AXIS.textColor}
				textAnchor={textAnchor}
				verticalAnchor={verticalEnd}
				x={
					position === 'right'
						? x + yAxisWidth
						: x + width - yAxisWidth
				}
				y={y - verticalSign * AXIS_LABEL_OFFSET}
			>
				{label}
			</Text>
		);
	};

export const getYAxisWidth = (
	data: Record<string, any>[],
	dataKey: string,
	minWidth = Y_AXIS_WIDTH
) =>
	data.reduce((acc, tick) => {
		const tickLabel = tick[dataKey];

		const textWidth = getTextWidth(tickLabel);

		return textWidth > acc ? textWidth : acc;
	}, minWidth);
