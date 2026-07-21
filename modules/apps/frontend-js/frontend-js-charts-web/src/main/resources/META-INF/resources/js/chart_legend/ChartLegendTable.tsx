/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useMemo} from 'react';

import type {ChartLegendColumn, ChartLegendItem} from './types';

interface Props {
	columns: ChartLegendColumn[];

	/** Draw the divider lines under the header and each row. Default `true`. */
	dividers?: boolean;

	items: ChartLegendItem[];
	labelColumnLabel?: string;
	onActivate: (id: number) => void;
	onDeactivate: (id: number) => void;
	onSelect: (id: number) => void;
	titleId: string;
}

export default function ChartLegendTable({
	columns,
	dividers = true,
	items,
	labelColumnLabel,
	onActivate,
	onDeactivate,
	onSelect,
	titleId,
}: Props) {
	const rows = useMemo(
		() =>
			[...items]
				.sort((a, b) => b.sortValue - a.sortValue)
				.map((item, index) => ({...item, rank: index + 1})),
		[items]
	);

	return (
		<table
			aria-labelledby={titleId}
			className={classNames('charts-legend-table', {
				'charts-legend-table--no-dividers': !dividers,
			})}
		>
			<thead>
				<tr>
					<th
						className="charts-legend-table__th charts-legend-table__th--rank"
						scope="col"
					>
						#
					</th>

					<th
						className="charts-legend-table__th charts-legend-table__th--visual"
						scope="col"
					>
						<span className="sr-only">
							{Liferay.Language.get('color')}
						</span>
					</th>

					<th
						className="charts-legend-table__th charts-legend-table__th--label"
						scope="col"
					>
						{labelColumnLabel ?? Liferay.Language.get('label')}
					</th>

					{columns.map((column) => (
						<th
							className="charts-legend-table__th charts-legend-table__th--number"
							key={column.label}
							scope="col"
						>
							{column.label}
						</th>
					))}
				</tr>
			</thead>

			<tbody>
				{rows.map((row) => (
					<tr
						className={classNames('charts-legend-table__row', {
							'is-active': row.active,
						})}
						key={row.id}
						onClick={() => onSelect(row.id)}
						onMouseEnter={() => onActivate(row.id)}
						onMouseLeave={() => onDeactivate(row.id)}
					>
						<td className="charts-legend-table__cell charts-legend-table__cell--rank">
							{row.rank}
						</td>

						<td className="charts-legend-table__cell charts-legend-table__cell--visual">
							{row.visual}
						</td>

						<th
							className="charts-legend-table__cell charts-legend-table__cell--label"
							scope="row"
						>
							{row.label}
						</th>

						{columns.map((column) => (
							<td
								className="charts-legend-table__cell charts-legend-table__cell--number"
								key={column.label}
							>
								{column.render(row)}
							</td>
						))}
					</tr>
				))}
			</tbody>
		</table>
	);
}
