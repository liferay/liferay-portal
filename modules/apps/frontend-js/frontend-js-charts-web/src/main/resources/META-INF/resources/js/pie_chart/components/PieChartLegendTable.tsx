/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {PieChartLegendBaseProps} from '../types/PieChartLegendBaseProps';
import {toPercent} from '../utils/percent';

interface PieChartLegendTableProps extends PieChartLegendBaseProps {
	titleId: string;
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
				{rows.map((row) => (
					<tr
						className={classNames({
							'is-active': activeIndex === row.dataIndex,
						})}
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

						<td>{row.datum.value.toLocaleString()}</td>

						<td>{toPercent(row.datum.value, total)}%</td>
					</tr>
				))}
			</tbody>
		</table>
	);
}
