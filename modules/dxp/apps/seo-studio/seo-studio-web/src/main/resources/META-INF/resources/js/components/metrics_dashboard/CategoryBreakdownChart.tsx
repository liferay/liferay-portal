/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {Cell, Pie, PieChart, Tooltip} from 'recharts';

import ChartEmptyState from './ChartEmptyState';
import {getCategoryColor, getCategoryLabel} from './labels';

function CategoryTooltip({
	active,
	payload,
}: {
	active?: boolean;
	payload?: any[];
}) {
	if (!active || !payload || !payload.length) {
		return null;
	}

	const {name, percent, value} = payload[0].payload;

	return (
		<div className="seo-studio-metrics-dashboard-category-tooltip">
			<div className="seo-studio-metrics-dashboard-category-tooltip-title">
				{name}
			</div>

			<div className="seo-studio-metrics-dashboard-category-tooltip-values">
				<span>{value}</span>

				<span>{percent}%</span>
			</div>
		</div>
	);
}

export default function CategoryBreakdownChart({
	categoryBreakdown,
}: {
	categoryBreakdown: Record<string, number>;
}) {
	const total = Object.values(categoryBreakdown).reduce(
		(sum, value) => sum + value,
		0
	);

	const data = Object.entries(categoryBreakdown)
		.map(([category, value]) => ({
			color: getCategoryColor(category),
			name: getCategoryLabel(category),
			percent: total ? Math.round((value / total) * 100) : 0,
			value,
		}))
		.sort((a, b) => b.value - a.value);

	if (!data.length) {
		return <ChartEmptyState />;
	}

	return (
		<div className="seo-studio-metrics-dashboard-category">
			<div className="seo-studio-metrics-dashboard-category-chart">
				<PieChart height={220} width={220}>
					<Pie
						data={data}
						dataKey="value"
						innerRadius={70}
						nameKey="name"
						outerRadius={100}
						stroke="none"
					>
						{data.map((entry) => (
							<Cell fill={entry.color} key={entry.name} />
						))}
					</Pie>

					<Tooltip
						allowEscapeViewBox={{x: true, y: true}}
						content={<CategoryTooltip />}
						wrapperStyle={{zIndex: 2}}
					/>
				</PieChart>

				<div className="seo-studio-metrics-dashboard-category-center">
					{total} {Liferay.Language.get('insights')}
				</div>
			</div>

			<ul className="seo-studio-metrics-dashboard-category-legend">
				{data.map((entry) => (
					<li
						className="seo-studio-metrics-dashboard-category-legend-item"
						key={entry.name}
					>
						<span
							className="seo-studio-metrics-dashboard-legend-dot"
							style={{backgroundColor: entry.color}}
						/>

						<span className="seo-studio-metrics-dashboard-category-legend-name">
							{entry.name}
						</span>

						<span className="seo-studio-metrics-dashboard-category-legend-count">
							{entry.value}
						</span>

						<span className="seo-studio-metrics-dashboard-category-legend-percent">
							{entry.percent}%
						</span>
					</li>
				))}
			</ul>
		</div>
	);
}
