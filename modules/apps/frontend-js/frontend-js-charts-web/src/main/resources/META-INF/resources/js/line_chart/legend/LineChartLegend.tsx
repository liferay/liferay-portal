/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo} from 'react';

import ChartLegend from '../../chart_legend/ChartLegend';
import LineChartLegendIcon from './LineChartLegendIcon';

import type {
	ChartLegendColumn,
	ChartLegendItem,
	ChartLegendLayout,
} from '../../chart_legend/types';
import type {ResolvedSeriesStyle} from '../plot/LineChartPlot';
import type {LineSeries} from '../types';

interface Props {
	activeSeriesIndex: number | null;
	format: (value: number) => string;
	layout: ChartLegendLayout;
	onActivate: (seriesIndex: number) => void;
	onDeactivate: (seriesIndex: number) => void;
	onSelect: (seriesIndex: number) => void;
	series: LineSeries[];
	styles: ResolvedSeriesStyle[];
	titleId: string;
}

export default function LineChartLegend({
	activeSeriesIndex,
	format,
	layout,
	onActivate,
	onDeactivate,
	onSelect,
	series,
	styles,
	titleId,
}: Props) {

	// Per-series metrics, keyed on the data alone so hovering (which only
	// changes the active index) does not recompute totals and averages.

	const metrics = useMemo(
		() =>
			series.map((line) => {
				const lineValues = line.values.filter(
					(value): value is number =>
						value !== null && Number.isFinite(value)
				);
				const total = lineValues.reduce((sum, value) => sum + value, 0);

				return {
					average: lineValues.length ? total / lineValues.length : 0,
					latest: lineValues.length
						? lineValues[lineValues.length - 1]
						: undefined,
					total,
				};
			}),
		[series]
	);

	const items = useMemo<ChartLegendItem[]>(
		() =>
			series.map((line, index) => {
				const {latest, total} = metrics[index];

				return {
					active: activeSeriesIndex === index,
					id: index,
					label: line.label,
					listValue:
						latest === undefined ? undefined : format(latest),
					sortValue: total,
					visual: (
						<LineChartLegendIcon
							color={styles[index].color}
							dasharray={styles[index].dasharray}
							marker={styles[index].marker}
						/>
					),
				};
			}),
		[activeSeriesIndex, format, metrics, series, styles]
	);

	const columns = useMemo<ChartLegendColumn[]>(
		() => [
			{
				label: Liferay.Language.get('total'),
				render: (item) => format(metrics[item.id].total),
			},
			{
				label: Liferay.Language.get('average'),
				render: (item) =>
					format(Math.round(metrics[item.id].average * 10) / 10),
			},
		],
		[format, metrics]
	);

	return (
		<ChartLegend
			columns={columns}
			items={items}
			layout={layout}
			onActivate={onActivate}
			onDeactivate={onDeactivate}
			onSelect={onSelect}
			titleId={titleId}
		/>
	);
}
