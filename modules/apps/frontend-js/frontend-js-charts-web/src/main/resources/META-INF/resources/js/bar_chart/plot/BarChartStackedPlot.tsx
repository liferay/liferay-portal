/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import BarChartStackedSegment from './BarChartStackedSegment';

import type {BarDatum, FocusableBarElement} from '../types';
import type {StackedBarChartGeometry} from './geometry';

interface Props {
	data: BarDatum[];
	focusIndex: number | null;
	geometry: StackedBarChartGeometry;
	height: number;
	hoverIndex: number | null;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
	onLeave: (index: number) => void;
	palette: string[] | null;
	setBarRef: (index: number, element: FocusableBarElement | null) => void;
	width: number;
}

export default function BarChartStackedPlot({
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
	width,
}: Props) {
	return (
		<svg
			focusable="false"
			preserveAspectRatio="xMidYMid meet"
			viewBox={`0 0 ${width} ${height}`}
			width="100%"
		>
			{data.map((datum, index) => (
				<BarChartStackedSegment
					active={focusIndex === index || hoverIndex === index}
					datum={datum}
					fill={palette ? palette[index] : null}
					index={index}
					key={`${datum.label}-${index}`}
					layout={geometry.segments[index]}
					onFocus={onFocus}
					onHover={onHover}
					onKeyDown={onKeyDown}
					onLeave={onLeave}
					setBarRef={setBarRef}
					width={width}
				/>
			))}
		</svg>
	);
}
