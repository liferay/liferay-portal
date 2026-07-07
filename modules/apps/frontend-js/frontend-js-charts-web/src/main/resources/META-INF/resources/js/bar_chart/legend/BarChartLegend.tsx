/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo} from 'react';

import ChartLegend from '../../chart_legend/ChartLegend';

import type {
	ChartLegendColumn,
	ChartLegendItem,
} from '../../chart_legend/types';
import type {
	BarChartLegend as BarChartLegendType,
	BarChartLegendValue,
	BarDatum,
} from '../types';

interface Props {
	activeIndex: number | null;
	colorFor: (index: number) => string;
	data: BarDatum[];
	layout: BarChartLegendType;
	legendValue: BarChartLegendValue;
	onActivate: (index: number) => void;
	onDeactivate: (index: number) => void;
	onSelect: (index: number) => void;
	titleId: string;
	total: number;
}

function formatShare(value: number, total: number): string {
	return `${(total === 0 ? 0 : (value / total) * 100).toFixed(1)}%`;
}

function getListValue(
	legendValue: BarChartLegendValue,
	value: number,
	total: number
): string | undefined {
	if (legendValue === 'name') {
		return undefined;
	}

	if (legendValue === 'value') {
		return value.toLocaleString();
	}

	return formatShare(value, total);
}

export default function BarChartLegend({
	activeIndex,
	colorFor,
	data,
	layout,
	legendValue,
	onActivate,
	onDeactivate,
	onSelect,
	titleId,
	total,
}: Props) {
	const items = useMemo<ChartLegendItem[]>(
		() =>
			data.map((datum, index) => ({
				active: activeIndex === index,
				id: index,
				label: datum.label,
				listValue: getListValue(legendValue, datum.value, total),
				sortValue: datum.value,
				visual: (
					<span
						className="charts-legend__swatch"
						style={{background: colorFor(index)}}
					/>
				),
			})),
		[activeIndex, colorFor, data, legendValue, total]
	);

	const columns = useMemo<ChartLegendColumn[]>(
		() => [
			{
				label: Liferay.Language.get('value'),
				render: (item) => data[item.id].value.toLocaleString(),
			},
			{
				label: Liferay.Language.get('share'),
				render: (item) => formatShare(data[item.id].value, total),
			},
		],
		[data, total]
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
