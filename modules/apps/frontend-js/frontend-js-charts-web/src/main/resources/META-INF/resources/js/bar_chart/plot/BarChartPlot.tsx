/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import BarChartBar from './BarChartBar';

import type {BarDatum} from '../types';
import type {BarChartGeometry} from './geometry';

interface Props {
	data: BarDatum[];
	focusIndex: number | null;
	geometry: BarChartGeometry;
	height: number;
	hoverIndex: number | null;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
	onLeave: (index: number) => void;
	palette: string[] | null;
	setBarRef: (index: number, element: SVGGraphicsElement | null) => void;
	showAxis: boolean;
	track: boolean;
	width: number;
}

export default function BarChartPlot({
	data,
	focusIndex,
	geometry,
	height,
	hoverIndex,
	onFocus,
	onHover,
	onKeyDown,
	onLeave,
	palette,
	setBarRef,
	showAxis,
	track,
	width,
}: Props) {
	return (
		<svg
			focusable="false"
			preserveAspectRatio="xMidYMid meet"
			viewBox={`0 0 ${width} ${height}`}
			width="100%"
		>
			{showAxis && (
				<line
					className="charts-bar-chart__axis"
					x1={geometry.axis.x1}
					x2={geometry.axis.x2}
					y1={geometry.axis.y1}
					y2={geometry.axis.y2}
				/>
			)}

			{data.map((datum, index) => (
				<BarChartBar
					active={focusIndex === index || hoverIndex === index}
					datum={datum}
					fill={palette ? palette[index] : null}
					index={index}
					key={`${datum.label}-${index}`}
					layout={geometry.bars[index]}
					onFocus={onFocus}
					onHover={onHover}
					onKeyDown={onKeyDown}
					onLeave={onLeave}
					setBarRef={setBarRef}
					track={track}
				/>
			))}
		</svg>
	);
}
