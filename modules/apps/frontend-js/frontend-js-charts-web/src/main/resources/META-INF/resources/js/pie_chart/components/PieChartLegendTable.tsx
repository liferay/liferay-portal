/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {PieDatum} from '../types/PieDatum';
import {toPercent} from '../utils/percent';

interface PieChartLegendTableProps {
	activeIndex: number | null;
	colors: string[];
	data: PieDatum[];
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	titleId: string;
	total: number;
}

export default function PieChartLegendTable({
	activeIndex,
	colors,
	data,
	onFocus,
	onHover,
	onHoverEnd,
	titleId,
	total,
}: PieChartLegendTableProps) {
	const rows = data
		.map((datum, dataIndex) => ({
			color: colors[dataIndex],
			dataIndex,
			datum,
		}))
		.sort((a, b) => b.datum.value - a.datum.value)
		.map((row, index) => ({...row, rank: index + 1}));

	return (
		<table aria-labelledby={titleId} className="chart-pie-legend-table">
			<thead>
				<tr>
					<th scope="col">#</th>

					<th scope="col">
						<span className="sr-only">
							{Liferay.Language.get('color')}
						</span>
					</th>

					<th scope="col">{Liferay.Language.get('label')}</th>

					<th scope="col">{Liferay.Language.get('value')}</th>

					<th scope="col">{Liferay.Language.get('share')}</th>
				</tr>
			</thead>

			<tbody>
				{rows.map((row) => {
					const rowClassName =
						activeIndex === row.dataIndex ? 'is-active' : undefined;

					return (
						<tr
							className={rowClassName}
							key={row.dataIndex}
							onClick={() => onFocus(row.dataIndex)}
							onMouseEnter={() => onHover(row.dataIndex)}
							onMouseLeave={onHoverEnd}
						>
							<td>{row.rank}</td>

							<td>
								<span
									className="chart-pie-legend-swatch"
									style={{background: row.color}}
								/>
							</td>

							<th scope="row">{row.datum.label}</th>

							<td>{row.datum.value}</td>

							<td>{toPercent(row.datum.value, total)}%</td>
						</tr>
					);
				})}
			</tbody>
		</table>
	);
}
