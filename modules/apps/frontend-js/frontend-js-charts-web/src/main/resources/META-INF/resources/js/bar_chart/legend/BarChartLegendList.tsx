/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import type {BarDatum} from '../types';

interface Props {
	activeIndex: number | null;
	colorFor: (index: number) => string;
	data: BarDatum[];
	onActivate: (index: number) => void;
	onDeactivate: (index: number) => void;
	onSelect: (index: number) => void;
	total: number;
}

export default function BarChartLegendList({
	activeIndex,
	colorFor,
	data,
	onActivate,
	onDeactivate,
	onSelect,
	total,
}: Props) {
	return (
		<ul aria-hidden="true" className="charts-bar-chart__legend">
			{data.map((datum, index) => {
				const percent = total === 0 ? 0 : (datum.value / total) * 100;

				return (
					<li
						className={classNames('charts-bar-chart__legend-item', {
							'is-active': activeIndex === index,
						})}
						key={`${datum.label}-${index}`}
						onClick={() => onSelect(index)}
						onMouseEnter={() => onActivate(index)}
						onMouseLeave={() => onDeactivate(index)}
					>
						<span
							className="charts-bar-chart__legend-swatch"
							style={{background: colorFor(index)}}
						/>

						<span className="charts-bar-chart__legend-label">
							{datum.label}
						</span>

						<span className="charts-bar-chart__legend-value">
							{percent.toFixed(1)}%
						</span>
					</li>
				);
			})}
		</ul>
	);
}
