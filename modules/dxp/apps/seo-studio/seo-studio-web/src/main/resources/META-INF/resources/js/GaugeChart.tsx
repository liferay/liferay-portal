/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {Cell, Label, Pie, PieChart} from 'recharts';

type ScoreCategory = 'good' | 'needsImprovement' | 'poor';

const FILL_COLORS: Record<ScoreCategory, string> = {
	good: '#50D2A0',
	needsImprovement: '#FFB46E',
	poor: '#FF5F5F',
};

const GAUGE_BACKGROUND = '#e7e7ed';

const RATINGS: Record<ScoreCategory, string> = {
	good: Liferay.Language.get('good'),
	needsImprovement: Liferay.Language.get('needs-improvement'),
	poor: Liferay.Language.get('poor'),
};

const TEXT_COLORS: Record<ScoreCategory, string> = {
	good: '#287d3c',
	needsImprovement: '#b95000',
	poor: '#da1414',
};

function getScoreCategory(score: number): ScoreCategory {
	if (score >= 90) {
		return 'good';
	}

	if (score >= 50) {
		return 'needsImprovement';
	}

	return 'poor';
}

interface Props {
	label: string;
	score: number;
}

export default function GaugeChart({label, score}: Props) {
	const category = getScoreCategory(score);

	// Recharts skips a Pie slice with value 0, which hides the gauge ring
	// entirely; substitute a sliver so the background ring still renders.

	const data = [
		{value: score === 0 ? 0.1 : score},
		{value: Math.max(100 - score, 0)},
	];

	return (
		<div className="pagespeed-gauge">
			<PieChart height={120} width={120}>
				<Pie
					cx="50%"
					cy="50%"
					data={data}
					dataKey="value"
					endAngle={-270}
					innerRadius={46}
					outerRadius={54}
					paddingAngle={0}
					startAngle={90}
					stroke="none"
				>
					<Cell fill={FILL_COLORS[category]} />

					<Cell fill={GAUGE_BACKGROUND} />

					<Label
						position="center"
						style={{
							fill: TEXT_COLORS[category],
							fontSize: '24px',
							fontWeight: 600,
						}}
						value={score}
					/>
				</Pie>
			</PieChart>

			<div className="pagespeed-gauge-info">
				<span className="pagespeed-gauge-label">{label}</span>

				<span
					className="pagespeed-gauge-rating"
					style={{color: TEXT_COLORS[category]}}
				>
					{RATINGS[category]}
				</span>
			</div>
		</div>
	);
}
