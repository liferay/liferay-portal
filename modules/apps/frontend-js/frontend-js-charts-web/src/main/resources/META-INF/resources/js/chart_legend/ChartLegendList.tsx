/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import type {ChartLegendColumn, ChartLegendItem} from './types';

interface Props {

	/**
	 * When present, each row renders one cell per column (the same data the
	 * `table` layout shows) instead of the single `listValue`.
	 */
	columns?: ChartLegendColumn[];

	items: ChartLegendItem[];
	onActivate: (id: number) => void;
	onDeactivate: (id: number) => void;
	onSelect: (id: number) => void;

	/** Stack the items in a single column instead of the responsive grid. */
	stacked?: boolean;
}

export default function ChartLegendList({
	columns,
	items,
	onActivate,
	onDeactivate,
	onSelect,
	stacked = false,
}: Props) {
	return (
		<ul
			aria-hidden="true"
			className={classNames('charts-legend', {
				'charts-legend--stacked': stacked,
			})}
		>
			{items.map((item) => (
				<li
					className={classNames('charts-legend__item', {
						'is-active': item.active,
					})}
					key={item.id}
					onClick={() => onSelect(item.id)}
					onMouseEnter={() => onActivate(item.id)}
					onMouseLeave={() => onDeactivate(item.id)}
				>
					{item.visual}

					<span className="charts-legend__label">{item.label}</span>

					{columns?.length ? (
						columns.map((column) => (
							<span
								className="charts-legend__value"
								key={column.label}
							>
								{column.render(item)}
							</span>
						))
					) : item.listValue !== undefined ? (
						<span className="charts-legend__value">
							{item.listValue}
						</span>
					) : null}
				</li>
			))}
		</ul>
	);
}
