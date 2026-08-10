import ChartTooltip, {
	Alignments,
	Weights,
} from 'shared/components/chart-tooltip';
import moment from 'moment';
import React from 'react';
import Trend from 'shared/components/Trend';
import {Colors} from 'shared/util/charts';
import {getCustomDateFormat, getDate as getDateUtil} from 'shared/util/date';

export const Tooltip = ({dataPoint}: {dataPoint: any[]}) => {
	const control = dataPoint[0];
	const variant = dataPoint[1];

	const improvementLabel = (improvement: number | string | undefined) => {
		if (improvement) {
			const numericImprovement = Number(improvement);

			return (
				<Trend
					color={
						numericImprovement > 0
							? Colors.positive
							: Colors.negative
					}
					icon={numericImprovement > 0 ? 'caret-top' : 'caret-bottom'}
					label={String(improvement)}
				/>
			);
		}

		return <span>{'-'}</span>;
	};

	const header = [
		{
			columns: [
				{
					label: `${Liferay.Language.get('variants')} | ${moment
						.utc(getDateUtil(control.payload.key))
						.format(getCustomDateFormat())}`,
					weight: Weights.Semibold,
					width: 140,
				},
				{
					align: Alignments.Right,
					label: Liferay.Language.get('high'),
					weight: Weights.Semibold,
					width: 60,
				},
				{
					align: Alignments.Right,
					label: Liferay.Language.get('low'),
					weight: Weights.Semibold,
					width: 60,
				},
				{
					align: Alignments.Right,
					label: Liferay.Language.get('median'),
					weight: Weights.Semibold,
					width: 60,
				},
				{
					label: '',
					width: 60,
				},
			],
		},
	];

	const rows = [
		{
			columns: [
				{
					color: control.color,
					label: control.name,
					weight: Weights.Semibold,
				},
				{
					align: Alignments.Right,
					label: control.payload.tooltip.control.high,
				},
				{
					align: Alignments.Right,
					label: control.payload.tooltip.control.low,
				},
				{
					align: Alignments.Right,
					label: control.payload.tooltip.control.median,
				},
				{
					align: Alignments.Right,
					label: improvementLabel(
						control.payload.tooltip.control.improvement
					),
				},
			],
		},
		{
			columns: [
				{
					color: variant.color,
					label: variant.name,
					weight: Weights.Semibold,
				},
				{
					align: Alignments.Right,
					label: variant.payload.tooltip.variant.high,
				},
				{
					align: Alignments.Right,
					label: variant.payload.tooltip.variant.low,
				},
				{
					align: Alignments.Right,
					label: variant.payload.tooltip.variant.median,
				},
				{
					align: Alignments.Right,
					label: improvementLabel(
						variant.payload.tooltip.variant.improvement
					),
				},
			],
		},
	];

	return (
		<div className="bb-tooltip-container position-static">
			<ChartTooltip header={header} rows={rows as any} />
		</div>
	);
};
