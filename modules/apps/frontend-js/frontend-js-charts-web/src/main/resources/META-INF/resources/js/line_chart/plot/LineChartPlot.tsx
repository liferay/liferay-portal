/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import LineChartAxis from './LineChartAxis';
import LineChartCategoryLabels from './LineChartCategoryLabels';
import LineChartGridlines from './LineChartGridlines';
import LineChartSeries from './LineChartSeries';
import LineChartTooltip from './LineChartTooltip';

import type {LineChartPointTooltip, LineSeries} from '../types';
import type {LineChartGeometry} from './geometry';
import type {LineMarkerShape} from './markers';

export interface ResolvedSeriesStyle {
	color: string;
	dasharray: string;
	marker: LineMarkerShape;
}

interface ActivePoint {
	categoryIndex: number;
	seriesIndex: number;
}

interface Props {
	active: ActivePoint | null;
	categories: string[];
	focus: ActivePoint | null;
	format: (value: number) => string;
	geometry: LineChartGeometry;
	height: number;
	onBlurPoint: (seriesIndex: number, categoryIndex: number) => void;
	onFocusPoint: (seriesIndex: number, categoryIndex: number) => void;
	onHoverPoint: (seriesIndex: number, categoryIndex: number) => void;
	onKeyDownPoint: (
		seriesIndex: number,
		categoryIndex: number,
		event: React.KeyboardEvent
	) => void;
	onLeavePoint: (seriesIndex: number, categoryIndex: number) => void;
	pointTooltip: LineChartPointTooltip;
	series: LineSeries[];
	setPointRef: (
		seriesIndex: number,
		categoryIndex: number,
		element: SVGCircleElement | null
	) => void;
	styles: ResolvedSeriesStyle[];
	tabbable: ActivePoint | null;
	width: number;
}

export default function LineChartPlot({
	active,
	categories,
	focus,
	format,
	geometry,
	height,
	onBlurPoint,
	onFocusPoint,
	onHoverPoint,
	onKeyDownPoint,
	onLeavePoint,
	pointTooltip,
	series,
	setPointRef,
	styles,
	tabbable,
	width,
}: Props) {
	const activePoint =
		active &&
		geometry.series[active.seriesIndex]?.points[active.categoryIndex]
			? geometry.series[active.seriesIndex].points[active.categoryIndex]
			: null;

	return (
		<svg
			focusable="false"
			preserveAspectRatio="xMidYMid meet"
			viewBox={`0 0 ${width} ${height}`}
			width="100%"
		>
			<LineChartGridlines
				format={format}
				plot={geometry.plot}
				ticks={geometry.ticks}
			/>

			<LineChartAxis axis={geometry.axis} />

			<LineChartCategoryLabels
				categories={categories}
				categoryX={geometry.categoryX}
				height={height}
			/>

			{series.map((line, index) => (
				<LineChartSeries
					active={active?.seriesIndex === index}
					activeCategoryIndex={
						active?.seriesIndex === index
							? active.categoryIndex
							: null
					}
					categories={categories}
					color={styles[index].color}
					dasharray={styles[index].dasharray}
					focusedCategoryIndex={
						focus?.seriesIndex === index
							? focus.categoryIndex
							: null
					}
					format={format}
					key={`${line.label}-${index}`}
					layout={geometry.series[index]}
					marker={styles[index].marker}
					onBlurPoint={onBlurPoint}
					onFocusPoint={onFocusPoint}
					onHoverPoint={onHoverPoint}
					onKeyDownPoint={onKeyDownPoint}
					onLeavePoint={onLeavePoint}
					seriesIndex={index}
					seriesLabel={line.label}
					setPointRef={setPointRef}
					tabbable={tabbable}
				/>
			))}

			{pointTooltip !== 'none' && active && activePoint && (
				<LineChartTooltip
					color={styles[active.seriesIndex].color}
					mode={pointTooltip}
					plot={geometry.plot}
					point={{x: activePoint.x, y: activePoint.y}}
					text={`${series[active.seriesIndex].label}: ${format(
						activePoint.value
					)}`}
				/>
			)}
		</svg>
	);
}
