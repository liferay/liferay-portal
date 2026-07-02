/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {PieChartLegendBaseProps} from '../types/PieChartLegendBaseProps';
import {toPercent} from '../utils/percent';

type PieChartLegendListProps = PieChartLegendBaseProps;

export default function PieChartLegendList({
	activeIndex,
	colors,
	data,
	onFocus,
	onHover,
	onHoverEnd,
	total,
}: PieChartLegendListProps) {
	return (
		<ul aria-hidden="true" className="chart-pie-legend">
			{data.map((datum, index) => (
				<li
					className={classNames('chart-pie-legend-item', {
						'is-active': activeIndex === index,
					})}
					key={index}
					onClick={() => onFocus(index)}
					onMouseEnter={() => onHover(index)}
					onMouseLeave={onHoverEnd}
				>
					<span
						className="chart-pie-legend-swatch"
						style={{background: colors[index]}}
					/>

					<span className="chart-pie-legend-label">
						{datum.label}
					</span>

					<span className="chart-pie-legend-percent">
						{toPercent(datum.value, total)}%
					</span>
				</li>
			))}
		</ul>
	);
}
