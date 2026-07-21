/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo} from 'react';

import ChartLegend from '../../chart_legend/ChartLegend';
import {toPercent} from '../../percent';
import {MapDatum} from '../types/MapDatum';
import {getCountryLabel} from '../utils/getCountryLabel';
import MapChartLegendScale from './MapChartLegendScale';

import type {
	ChartLegendColumn,
	ChartLegendItem,
} from '../../chart_legend/types';

interface MapChartLegendProps {
	activeIndex: number | null;
	bucketCount: number;
	colors: string[];
	data: MapDatum[];
	legend: 'list' | 'none' | 'scale' | 'table';
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	scheme: 'blue' | 'categorical';
	tableDividers?: boolean;
	titleId: string;
	total: number;
}

export default function MapChartLegend({
	activeIndex,
	bucketCount,
	colors,
	data,
	legend,
	onFocus,
	onHover,
	onHoverEnd,
	scheme,
	tableDividers,
	titleId,
	total,
}: MapChartLegendProps) {
	const items = useMemo<ChartLegendItem[]>(
		() =>
			data
				.map((datum, dataIndex) => ({
					active: activeIndex === dataIndex,
					id: dataIndex,
					label: getCountryLabel(datum),
					listValue: datum.value,
					sortValue: datum.value,
					visual: (
						<span
							className="charts-legend__swatch"
							style={{background: colors[dataIndex]}}
						/>
					),
				}))
				.sort((a, b) => b.sortValue - a.sortValue),
		[activeIndex, colors, data]
	);

	const columns = useMemo<ChartLegendColumn[]>(
		() => [
			{
				label: Liferay.Language.get('value'),
				render: (item) => data[item.id].value,
			},
			{
				label: Liferay.Language.get('share'),
				render: (item) => `${toPercent(data[item.id].value, total)}%`,
			},
		],
		[data, total]
	);

	if (legend === 'scale') {
		return (
			<MapChartLegendScale bucketCount={bucketCount} scheme={scheme} />
		);
	}

	if (legend === 'list' || legend === 'table') {
		return (
			<ChartLegend
				columns={columns}
				items={items}
				labelColumnLabel={Liferay.Language.get('country')}
				layout={legend}
				onActivate={onHover}
				onDeactivate={() => onHoverEnd()}
				onSelect={onFocus}
				tableDividers={tableDividers}
				titleId={titleId}
			/>
		);
	}

	return null;
}
