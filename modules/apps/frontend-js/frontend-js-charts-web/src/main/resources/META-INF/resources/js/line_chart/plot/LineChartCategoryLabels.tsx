/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface Props {
	categories: string[];
	categoryX: number[];
	height: number;
}

export default function LineChartCategoryLabels({
	categories,
	categoryX,
	height,
}: Props) {
	return (
		<>
			{categories.map((category, index) => (
				<text
					className="charts-line-chart__category-label"
					key={`${category}-${index}`}
					textAnchor="middle"
					x={categoryX[index]}
					y={height - 12}
				>
					{category}
				</text>
			))}
		</>
	);
}
