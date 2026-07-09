/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

const CHAR_WIDTH = 6.5;
const CHIP_HEIGHT = 22;
const CHIP_PADDING_X = 10;
const POINT_GAP = 12;

interface Props {
	color: string;
	plot: {height: number; width: number; x: number; y: number};
	point: {x: number; y: number};
	text: string;
}

function clamp(value: number, min: number, max: number): number {
	return Math.min(Math.max(value, min), max);
}

/**
 * The point-anchored popover: a value chip that sits above the active point
 * (flipping below near the top edge) with a small arrow. It stays in SVG
 * because it is positioned in the plot's coordinate space. The `corner`
 * tooltip is instead the shared HTML `ChartTooltip`, rendered by `LineChart`.
 */
export default function LineChartTooltip({color, plot, point, text}: Props) {
	const chipWidth = text.length * CHAR_WIDTH + CHIP_PADDING_X * 2;

	const centerX = clamp(
		point.x,
		plot.x + chipWidth / 2,
		plot.x + plot.width - chipWidth / 2
	);

	// Sit above the point by default; flip below when there is no room above, so
	// a point near the top edge is not clipped by the SVG viewport.

	const below = point.y - POINT_GAP - CHIP_HEIGHT < plot.y;
	const centerY = below
		? point.y + POINT_GAP + CHIP_HEIGHT / 2
		: point.y - POINT_GAP - CHIP_HEIGHT / 2;

	const arrowX = clamp(
		point.x - centerX,
		-chipWidth / 2 + 8,
		chipWidth / 2 - 8
	);
	const arrowBaseY = below ? -CHIP_HEIGHT / 2 : CHIP_HEIGHT / 2;
	const arrowTipY = below ? -CHIP_HEIGHT / 2 - 5 : CHIP_HEIGHT / 2 + 5;

	return (
		<g
			className="charts-line-chart__tooltip charts-line-chart__tooltip--popover"
			style={{'--charts-line-color': color} as React.CSSProperties}
			transform={`translate(${centerX} ${centerY})`}
		>
			<rect
				className="charts-line-chart__tooltip-box"
				height={CHIP_HEIGHT}
				rx={4}
				width={chipWidth}
				x={-chipWidth / 2}
				y={-CHIP_HEIGHT / 2}
			/>

			<polygon
				className="charts-line-chart__tooltip-arrow"
				points={`${arrowX - 5},${arrowBaseY} ${arrowX + 5},${arrowBaseY} ${arrowX},${arrowTipY}`}
			/>

			<text
				className="charts-line-chart__tooltip-text"
				textAnchor="middle"
				x={0}
				y={4}
			>
				{text}
			</text>
		</g>
	);
}
