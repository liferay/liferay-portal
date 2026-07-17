/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {
	Bar,
	BarChart,
	CartesianGrid,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from 'recharts';

import ChartEmptyState from './ChartEmptyState';
import {
	SEVERITY_LEGEND_ORDER,
	SEVERITY_STACK_ORDER,
	getCategoryLabel,
	getSeverityColor,
	getSeverityLabel,
} from './labels';

function ImpactTooltip({active, payload}: {active?: boolean; payload?: any[]}) {
	if (!active || !payload || !payload.length) {
		return null;
	}

	return (
		<div className="seo-studio-metrics-dashboard-impact-tooltip">
			<div className="seo-studio-metrics-dashboard-impact-tooltip-title">
				{payload[0].payload.name}
			</div>

			<div className="seo-studio-metrics-dashboard-impact-tooltip-rows">
				{SEVERITY_LEGEND_ORDER.map((severity) => (
					<div
						className="seo-studio-metrics-dashboard-impact-tooltip-row"
						key={severity}
					>
						<span
							className="seo-studio-metrics-dashboard-legend-dot"
							style={{
								backgroundColor: getSeverityColor(severity),
							}}
						/>

						<span className="seo-studio-metrics-dashboard-impact-tooltip-row-label">
							{getSeverityLabel(severity)}
						</span>

						<span>{payload[0].payload[severity] ?? 0}</span>
					</div>
				))}
			</div>
		</div>
	);
}

export default function ImpactMixChart({
	impactMix,
}: {
	impactMix: Record<string, Record<string, number>>;
}) {
	const data = Object.entries(impactMix)
		.map(([category, severities]) => {
			const row: Record<string, number | string> = {
				name: getCategoryLabel(category),
			};

			SEVERITY_STACK_ORDER.forEach((severity) => {
				row[severity] = severities[severity] ?? 0;
			});

			return row;
		})
		.sort((a, b) => (a.name as string).localeCompare(b.name as string));

	if (!data.length) {
		return <ChartEmptyState />;
	}

	return (
		<div className="seo-studio-metrics-dashboard-impact">
			<p className="seo-studio-metrics-dashboard-impact-caption text-secondary">
				{Liferay.Language.get('high-medium-low-distribution')}
			</p>

			<ResponsiveContainer height={260} width="100%">
				<BarChart
					data={data}
					margin={{bottom: 0, left: -16, right: 8, top: 8}}
				>
					<CartesianGrid strokeDasharray="3 3" vertical={false} />

					<XAxis
						axisLine={false}
						dataKey="name"
						tick={{fontSize: 11}}
						tickFormatter={(value) => value.toUpperCase()}
						tickLine={false}
					/>

					<YAxis
						allowDecimals={false}
						axisLine={false}
						tick={{fontSize: 11}}
						tickLine={false}
						width={32}
					/>

					<Tooltip
						content={<ImpactTooltip />}
						cursor={{fill: 'rgba(0, 0, 0, 0.04)'}}
					/>

					{SEVERITY_STACK_ORDER.map((severity) => (
						<Bar
							dataKey={severity}
							fill={getSeverityColor(severity)}
							key={severity}
							name={getSeverityLabel(severity)}
							stackId="impact"
						/>
					))}
				</BarChart>
			</ResponsiveContainer>

			<ul className="seo-studio-metrics-dashboard-impact-legend">
				{SEVERITY_LEGEND_ORDER.map((severity) => (
					<li
						className="seo-studio-metrics-dashboard-impact-legend-item"
						key={severity}
					>
						<span
							className="seo-studio-metrics-dashboard-legend-dot"
							style={{
								backgroundColor: getSeverityColor(severity),
							}}
						/>

						{getSeverityLabel(severity)}
					</li>
				))}
			</ul>
		</div>
	);
}
