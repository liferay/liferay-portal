/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo} from 'react';

import {PieDatum} from '../types/PieDatum';
import {SliceAngles} from '../types/SliceAngles';
import {computePrecedingTotals} from '../utils/computePrecedingTotals';
import {computeSliceAngles} from '../utils/computeSliceAngles';
import {toPercent} from '../utils/percent';
import PieChartCenterLabel from './PieChartCenterLabel';
import PieChartSlice from './PieChartSlice';

interface PieChartPlotProps {
	activeDatum?: PieDatum;
	activeIndex: number | null;
	activePercent?: string;
	baseId: string;
	colors: string[];
	data: PieDatum[];
	focusIndex: number | null;
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

function getSliceClipId(baseId: string, index: number): string {
	return `${baseId}-slice-clip-${index}`;
}

export default function PieChartPlot({
	activeDatum,
	activeIndex,
	activePercent,
	baseId,
	colors,
	data,
	focusIndex,
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
}: PieChartPlotProps) {
	const precedingTotals = useMemo(() => computePrecedingTotals(data), [data]);

	const slicePaths = useMemo(
		() =>
			total > 0
				? data.map((datum, index) =>
						pathFactory(
							computeSliceAngles({
								precedingTotal: precedingTotals[index],
								total,
								value: datum.value,
							})
						)
					)
				: [],
		[data, pathFactory, precedingTotals, total]
	);

	const focusedSlicePath =
		focusIndex !== null ? slicePaths[focusIndex] : undefined;

	return (
		<>
			<svg
				className="chart-pie-svg"
				focusable="false"
				preserveAspectRatio="xMidYMid meet"
				viewBox={`0 0 ${pixelSize} ${pixelSize}`}
			>
				<defs>
					{slicePaths.map((slicePath, index) => (
						<clipPath
							id={getSliceClipId(baseId, index)}
							key={index}
						>
							<path d={slicePath} />
						</clipPath>
					))}
				</defs>

				{slicePaths.length
					? data.map((datum, index) => (
							<PieChartSlice
								color={colors[index]}
								d={slicePaths[index]}
								datum={datum}
								index={index}
								isActive={activeIndex === index}
								key={index}
								onBlur={onSliceBlur}
								onFocus={onFocus}
								onHover={onHover}
								onHoverEnd={onHoverEnd}
								onKeyDown={onKeyDown}
								percent={toPercent(datum.value, total)}
								sliceRef={sliceRefFactory(index)}
							/>
						))
					: null}

				{focusIndex !== null && focusedSlicePath ? (
					<g
						aria-hidden="true"
						clipPath={`url(#${getSliceClipId(baseId, focusIndex)})`}
						pointerEvents="none"
					>
						<path
							className="chart-pie-focus-halo"
							d={focusedSlicePath}
						/>

						<path
							className="chart-pie-focus-ring"
							d={focusedSlicePath}
						/>
					</g>
				) : null}
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
