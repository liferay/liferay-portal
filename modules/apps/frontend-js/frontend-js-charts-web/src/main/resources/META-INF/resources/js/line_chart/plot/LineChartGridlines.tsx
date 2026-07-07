/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import type {LineChartTick} from './geometry';

interface Props {
	format: (value: number) => string;
	plot: {width: number; x: number};
	ticks: LineChartTick[];
}

export default function LineChartGridlines({format, plot, ticks}: Props) {
	return (
		<>
			{ticks.map((tick) => (
				<g className="charts-line-chart__tick" key={tick.value}>
					<line
						className="charts-line-chart__gridline"
						x1={plot.x}
						x2={plot.x + plot.width}
						y1={tick.y}
						y2={tick.y}
					/>

					<text
						className="charts-line-chart__tick-label"
						textAnchor="end"
						x={plot.x - 8}
						y={tick.y + 4}
					>
						{format(tick.value)}
					</text>
				</g>
			))}
		</>
	);
}
