/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useMemo} from 'react';

import type {BarDatum} from '../types';

interface Props {
	activeIndex: number | null;
	colorFor: (index: number) => string;
	data: BarDatum[];
	onActivate: (index: number) => void;
	onDeactivate: (index: number) => void;
	onSelect: (index: number) => void;
	titleId: string;
	total: number;
}

export default function BarChartLegendTable({
	activeIndex,
	colorFor,
	data,
	onActivate,
	onDeactivate,
	onSelect,
	titleId,
	total,
}: Props) {
	const rows = useMemo(
		() =>
			data
				.map((datum, index) => ({
					color: colorFor(index),
					dataIndex: index,
					datum,
					share: total === 0 ? 0 : datum.value / total,
				}))
				.sort((a, b) => b.datum.value - a.datum.value)
				.map((row, sortedIndex) => ({...row, rank: sortedIndex + 1})),
		[data, colorFor, total]
	);

	return (
		<table
			aria-labelledby={titleId}
			className="charts-bar-chart__legend-table"
		>
			<thead>
				<tr>
					<th
						className="charts-bar-chart__legend-table-th charts-bar-chart__legend-table-th--rank"
						scope="col"
					>
						#
					</th>

					<th
						className="charts-bar-chart__legend-table-th charts-bar-chart__legend-table-th--color"
						scope="col"
					>
						<span className="sr-only">
							{Liferay.Language.get('color')}
						</span>
					</th>

					<th
						className="charts-bar-chart__legend-table-th charts-bar-chart__legend-table-th--label"
						scope="col"
					>
						{Liferay.Language.get('label')}
					</th>

					<th
						className="charts-bar-chart__legend-table-th charts-bar-chart__legend-table-th--value"
						scope="col"
					>
						{Liferay.Language.get('value')}
					</th>

					<th
						className="charts-bar-chart__legend-table-th charts-bar-chart__legend-table-th--share"
						scope="col"
					>
						{Liferay.Language.get('share')}
					</th>
				</tr>
			</thead>

			<tbody>
				{rows.map((row) => (
					<tr
						className={classNames('charts-bar-chart__legend-row', {
							'is-active': activeIndex === row.dataIndex,
						})}
						key={`${row.datum.label}-${row.dataIndex}`}
						onClick={() => onSelect(row.dataIndex)}
						onMouseEnter={() => onActivate(row.dataIndex)}
						onMouseLeave={() => onDeactivate(row.dataIndex)}
					>
						<td className="charts-bar-chart__legend-cell charts-bar-chart__legend-cell--rank">
							{row.rank}
						</td>

						<td className="charts-bar-chart__legend-cell charts-bar-chart__legend-cell--color">
							<span
								className="charts-bar-chart__legend-swatch"
								style={{background: row.color}}
							/>
						</td>

						<th
							className="charts-bar-chart__legend-cell charts-bar-chart__legend-cell--label"
							scope="row"
						>
							{row.datum.label}
						</th>

						<td className="charts-bar-chart__legend-cell charts-bar-chart__legend-cell--value">
							{row.datum.value.toLocaleString()}
						</td>

						<td className="charts-bar-chart__legend-cell charts-bar-chart__legend-cell--share">
							{(row.share * 100).toFixed(1)}%
						</td>
					</tr>
				))}
			</tbody>
		</table>
	);
}
