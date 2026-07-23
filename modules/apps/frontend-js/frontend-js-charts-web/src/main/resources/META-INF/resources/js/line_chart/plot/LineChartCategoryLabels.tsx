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

const AVERAGE_CHAR_WIDTH = 6.5;
const LABEL_GAP = 16;

export default function LineChartCategoryLabels({
	categories,
	categoryX,
	height,
}: Props) {
	const spacing =
		categoryX.length > 1 ? categoryX[1] - categoryX[0] : Infinity;

	const labelWidth =
		categories.reduce(
			(widest, category) => Math.max(widest, category.length),
			0
		) *
			AVERAGE_CHAR_WIDTH +
		LABEL_GAP;

	const step = Math.max(1, Math.ceil(labelWidth / spacing));

	return (
		<>
			{categories.map((category, index) =>
				index % step === 0 ? (
					<text
						className="charts-line-chart__category-label"
						key={`${category}-${index}`}
						textAnchor="middle"
						x={categoryX[index]}
						y={height - 12}
					>
						{category}
					</text>
				) : null
			)}
		</>
	);
}
