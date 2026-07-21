/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo} from 'react';

import ChartLegend from '../../chart_legend/ChartLegend';
import {toPercent} from '../../percent';
import {PieDatum} from '../types/PieDatum';

import type {
	ChartLegendColumn,
	ChartLegendItem,
	ChartLegendLayout,
} from '../../chart_legend/types';
import type {PieChartLegendValue} from '../PieChart';

interface PieChartLegendProps {
	activeIndex: number | null;
	colors: string[];
	data: PieDatum[];
	legend: ChartLegendLayout;
	legendTableDividers?: boolean;
	legendValue: PieChartLegendValue;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	titleId: string;
	total: number;
}

function getListValue(
	legendValue: PieChartLegendValue,
	value: number,
	total: number
): string | undefined {
	if (legendValue === 'name') {
		return undefined;
	}

	if (legendValue === 'value') {
		return value.toLocaleString();
	}

	return `${toPercent(value, total)}%`;
}

export default function PieChartLegend({
	activeIndex,
	colors,
	data,
	legend,
	legendTableDividers,
	legendValue,
	onFocus,
	onHover,
	onHoverEnd,
	titleId,
	total,
}: PieChartLegendProps) {
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
						style={{background: colors[index]}}
					/>
				),
			})),
		[activeIndex, colors, data, legendValue, total]
	);

	const columns = useMemo<ChartLegendColumn[]>(
		() => [
			{
				label: Liferay.Language.get('value'),
				render: (item) => data[item.id].value.toLocaleString(),
			},
			{
				label: Liferay.Language.get('share'),
				render: (item) => `${toPercent(data[item.id].value, total)}%`,
			},
		],
		[data, total]
	);

	return (
		<ChartLegend
			columns={columns}
			items={items}
			layout={legend}
			onActivate={onHover}
			onDeactivate={() => onHoverEnd()}
			onSelect={onFocus}
			tableDividers={legendTableDividers}
			titleId={titleId}
		/>
	);
}
