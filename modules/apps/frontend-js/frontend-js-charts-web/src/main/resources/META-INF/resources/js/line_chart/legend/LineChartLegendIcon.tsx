/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {renderMarker} from '../plot/markers';

import type {LineMarkerShape} from '../plot/markers';

interface Props {
	color: string;
	dasharray: string;
	marker: LineMarkerShape;
}

/**
 * A miniature line sample for the legend: a short dashed stroke with the series
 * marker centered on it, both tinted with the series color. This shows the two
 * traits that distinguish a series in `blue` mode (dash pattern and shape).
 */
export default function LineChartLegendIcon({color, dasharray, marker}: Props) {
	return (
		<svg
			aria-hidden="true"
			className="charts-line-chart__legend-icon"
			height={12}
			style={{'--charts-line-color': color} as React.CSSProperties}
			viewBox="0 0 24 12"
			width={24}
		>
			<line
				className="charts-line-chart__legend-icon-line-halo"
				style={{strokeDasharray: dasharray}}
				x1={2}
				x2={22}
				y1={6}
				y2={6}
			/>

			<line
				className="charts-line-chart__legend-icon-line"
				style={{strokeDasharray: dasharray}}
				x1={2}
				x2={22}
				y1={6}
				y2={6}
			/>

			<g
				className="charts-line-chart__legend-icon-marker"
				transform="translate(12 6)"
			>
				{renderMarker(marker, 3)}
			</g>
		</svg>
	);
}
