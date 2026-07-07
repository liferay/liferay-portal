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
	mode: 'corner' | 'popover';
	plot: {height: number; width: number; x: number; y: number};
	point: {x: number; y: number};
	text: string;
}

function clamp(value: number, min: number, max: number): number {
	return Math.min(Math.max(value, min), max);
}

export default function LineChartTooltip({
	color,
	mode,
	plot,
	point,
	text,
}: Props) {
	const chipWidth = text.length * CHAR_WIDTH + CHIP_PADDING_X * 2;

	if (mode === 'corner') {
		return (
			<g
				className="charts-line-chart__tooltip charts-line-chart__tooltip--corner"
				style={{'--charts-line-color': color} as React.CSSProperties}
				transform={`translate(${plot.x + 4} ${plot.y + 4})`}
			>
				<rect
					className="charts-line-chart__tooltip-box"
					height={CHIP_HEIGHT}
					rx={4}
					width={chipWidth}
					x={0}
					y={0}
				/>

				<text
					className="charts-line-chart__tooltip-text"
					x={CHIP_PADDING_X}
					y={CHIP_HEIGHT / 2 + 4}
				>
					{text}
				</text>
			</g>
		);
	}

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
