/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {PieDatum} from '../types/PieDatum';
import {SliceAngles} from '../types/SliceAngles';
import {computePrecedingTotals} from '../utils/computePrecedingTotals';
import {toPercent} from '../utils/percent';
import PieChartCenterLabel from './PieChartCenterLabel';
import PieChartSlice from './PieChartSlice';

interface PieChartGraphicProps {
	activeDatum?: PieDatum;
	activeIndex: number | null;
	activePercent?: number;
	colors: string[];
	data: PieDatum[];
	innerRadius: number;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
	onSliceBlur: () => void;
	pathFactory: (angles: SliceAngles) => string;
	pixelSize: number;
	sliceRefFactory: (
		index: number
	) => (element: SVGPathElement | null) => void;
	total: number;
}

export default function PieChartGraphic({
	activeDatum,
	activeIndex,
	activePercent,
	colors,
	data,
	innerRadius,
	onFocus,
	onHover,
	onHoverEnd,
	onKeyDown,
	onSliceBlur,
	pathFactory,
	pixelSize,
	sliceRefFactory,
	total,
}: PieChartGraphicProps) {
	const precedingTotals = computePrecedingTotals(data);

	return (
		<>
			<svg
				className="chart-pie-svg"
				focusable="false"
				preserveAspectRatio="xMidYMid meet"
				viewBox={`0 0 ${pixelSize} ${pixelSize}`}
			>
				{data.map((datum, index) => (
					<PieChartSlice
						color={colors[index]}
						datum={datum}
						index={index}
						isActive={activeIndex === index}
						key={index}
						onBlur={onSliceBlur}
						onFocus={onFocus}
						onHover={onHover}
						onHoverEnd={onHoverEnd}
						onKeyDown={onKeyDown}
						pathFactory={pathFactory}
						percent={toPercent(datum.value, total)}
						precedingTotal={precedingTotals[index]}
						sliceRef={sliceRefFactory(index)}
						total={total}
					/>
				))}
			</svg>

			{innerRadius > 0 ? (
				<PieChartCenterLabel
					activeDatum={activeDatum}
					activePercent={activePercent}
					total={total}
				/>
			) : null}
		</>
	);
}
