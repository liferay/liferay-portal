/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useCallback, useId, useMemo, useRef, useState} from 'react';

import ChartTooltip from '../chart_tooltip/ChartTooltip';
import {useElementWidth} from '../hooks/useElementWidth';
import {getCategoricalColors} from '../palette';
import LineChartLegend from './legend/LineChartLegend';
import LineChartPlot from './plot/LineChartPlot';
import {getLineChartGeometry} from './plot/geometry';
import {dashPatternFor, markerShapeFor} from './plot/markers';

import '../../css/LineChart.scss';

import type {ResolvedSeriesStyle} from './plot/LineChartPlot';
import type {LineChartProps} from './types';

interface ActivePoint {
	categoryIndex: number;
	seriesIndex: number;
}

/**
 * Shades of blue for the `blue` scheme. Series stay a recognizable blue family
 * while their marker shape and dash pattern carry the real distinction (so the
 * chart survives monochrome printing). Each shade keeps the Clay token value as
 * a fallback so the chart stays blue on themes that do not define the variable.
 */
const BLUE_SHADES = [
	'var(--chart-color-2, light-dark(#006eff, #66abff))',
	'var(--chart-blue-d2, light-dark(#005fcc, #94c4ff))',
	'var(--chart-blue-l2, light-dark(#66abff, #006be6))',
	'var(--chart-blue-d1, light-dark(#006be6, #70b1ff))',
	'var(--chart-blue-l3, light-dark(#97c5ff, #0056b8))',
];

const DEFAULT_WIDTH = 640;

export default function LineChart({
	alignment = 'start',
	animated = true,
	categories,
	className,
	description,
	height = 320,
	legend = 'list',
	legendSwatchBorder = true,
	legendValue = 'value',
	pointTooltip = 'popover',
	scheme = 'blue',
	series,
	title,
	width,
	yFormat,
	yTicks = 5,
}: LineChartProps) {
	const reactId = useId();
	const titleId = `${reactId}-title`;
	const descId = `${reactId}-desc`;

	const rootRef = useRef<HTMLElement>(null);

	const measuredWidth = useElementWidth(rootRef);

	const effectiveWidth = width ?? (measuredWidth || DEFAULT_WIDTH);

	const [focus, setFocus] = useState<ActivePoint | null>(null);
	const [hover, setHover] = useState<ActivePoint | null>(null);
	const active = focus ?? hover;

	const activeValue =
		active !== null
			? series[active.seriesIndex].values[active.categoryIndex]
			: null;

	const pointRefs = useRef<Array<Array<SVGCircleElement | null>>>([]);

	const format = useMemo(
		() => yFormat ?? ((value: number) => String(value)),
		[yFormat]
	);

	const geometry = useMemo(
		() =>
			getLineChartGeometry({
				categories,
				height,
				series,
				width: effectiveWidth,
				yTicks,
			}),
		[categories, height, series, effectiveWidth, yTicks]
	);

	const palette = useMemo(
		() =>
			scheme === 'categorical'
				? getCategoricalColors(series.length)
				: null,
		[scheme, series.length]
	);

	const styles = useMemo<ResolvedSeriesStyle[]>(
		() =>
			series.map((line, index) => ({
				color:
					line.color ??
					(palette
						? palette[index]
						: BLUE_SHADES[index % BLUE_SHADES.length]),
				dasharray: line.dasharray ?? dashPatternFor(index),
				marker: line.marker ?? markerShapeFor(index),
			})),
		[series, palette]
	);

	const summaryText = useMemo(() => {
		if (description) {
			return description;
		}

		return series.map((line) => line.description ?? line.label).join('. ');
	}, [description, series]);

	// Precompute the non-null point indexes per series once per geometry, so the
	// keyboard, legend and tabbable lookups stay O(1) instead of re-reducing on
	// every focus and hover change.

	const allFiniteIndexes = useMemo(
		() =>
			geometry.series.map((seriesLayout) =>
				seriesLayout.points.reduce<number[]>(
					(accumulator, point, index) => {
						if (point) {
							accumulator.push(index);
						}

						return accumulator;
					},
					[]
				)
			),
		[geometry]
	);

	const finiteIndexes = useCallback(
		(seriesIndex: number): number[] => allFiniteIndexes[seriesIndex] ?? [],
		[allFiniteIndexes]
	);

	const tabbable = useMemo<ActivePoint | null>(() => {
		if (active) {
			return active;
		}

		for (let seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
			const indexes = finiteIndexes(seriesIndex);

			if (indexes.length) {
				return {categoryIndex: indexes[0], seriesIndex};
			}
		}

		return null;
	}, [active, finiteIndexes, series.length]);

	const setPointRef = useCallback(
		(
			seriesIndex: number,
			categoryIndex: number,
			element: SVGCircleElement | null
		) => {
			(pointRefs.current[seriesIndex] ??= [])[categoryIndex] = element;
		},
		[]
	);

	const focusPoint = useCallback(
		(seriesIndex: number, categoryIndex: number) => {
			pointRefs.current[seriesIndex]?.[categoryIndex]?.focus();
		},
		[]
	);

	const onFocusPoint = useCallback(
		(seriesIndex: number, categoryIndex: number) =>
			setFocus({categoryIndex, seriesIndex}),
		[]
	);

	const onBlurPoint = useCallback(
		(seriesIndex: number, categoryIndex: number) =>
			setFocus((current) =>
				current?.seriesIndex === seriesIndex &&
				current?.categoryIndex === categoryIndex
					? null
					: current
			),
		[]
	);

	const onHoverPoint = useCallback(
		(seriesIndex: number, categoryIndex: number) =>
			setHover({categoryIndex, seriesIndex}),
		[]
	);

	const onLeavePoint = useCallback(
		(seriesIndex: number, categoryIndex: number) =>
			setHover((current) =>
				current?.seriesIndex === seriesIndex &&
				current?.categoryIndex === categoryIndex
					? null
					: current
			),
		[]
	);

	const nearestFinite = useCallback(
		(seriesIndex: number, categoryIndex: number): number | null => {
			const indexes = finiteIndexes(seriesIndex);

			if (!indexes.length) {
				return null;
			}

			return indexes.reduce((best, index) =>
				Math.abs(index - categoryIndex) < Math.abs(best - categoryIndex)
					? index
					: best
			);
		},
		[finiteIndexes]
	);

	const onKeyDownPoint = useCallback(
		(
			seriesIndex: number,
			categoryIndex: number,
			event: React.KeyboardEvent
		) => {
			const indexes = finiteIndexes(seriesIndex);
			const position = indexes.indexOf(categoryIndex);

			const moveSeries = (direction: 1 | -1) => {
				for (
					let next = seriesIndex + direction;
					next >= 0 && next < series.length;
					next += direction
				) {
					const target = nearestFinite(next, categoryIndex);

					if (target !== null) {
						focusPoint(next, target);

						return true;
					}
				}

				return false;
			};

			let handled = true;

			switch (event.key) {
				case 'ArrowRight':
					focusPoint(
						seriesIndex,
						indexes[Math.min(position + 1, indexes.length - 1)]
					);
					break;
				case 'ArrowLeft':
					focusPoint(seriesIndex, indexes[Math.max(position - 1, 0)]);
					break;
				case 'ArrowDown':
					moveSeries(1);
					break;
				case 'ArrowUp':
					moveSeries(-1);
					break;
				case 'Home':
					focusPoint(seriesIndex, indexes[0]);
					break;
				case 'End':
					focusPoint(seriesIndex, indexes[indexes.length - 1]);
					break;
				default:
					handled = false;
			}

			if (handled) {
				event.preventDefault();
			}
		},
		[finiteIndexes, focusPoint, nearestFinite, series.length]
	);

	return (
		<figure
			aria-describedby={descId}
			aria-labelledby={titleId}
			className={classNames(
				'charts-line-chart',
				`charts-line-chart--${scheme}`,
				`charts-line-chart--legend-${legend}`,
				`charts-line-chart--tooltip-${pointTooltip}`,
				`charts-line-chart--align-${alignment}`,
				{
					'charts-line-chart--motion': animated,
					'charts-line-chart--no-swatch-border': !legendSwatchBorder,
				},
				className
			)}
			ref={rootRef}
			style={{maxWidth: width}}
		>
			<figcaption className="charts-line-chart__title" id={titleId}>
				{title}
			</figcaption>

			<p className="sr-only" id={descId}>
				{summaryText}
			</p>

			<div className="charts-line-chart__plot">
				<LineChartPlot
					active={active}
					categories={categories}
					focus={focus}
					format={format}
					geometry={geometry}
					height={height}
					onBlurPoint={onBlurPoint}
					onFocusPoint={onFocusPoint}
					onHoverPoint={onHoverPoint}
					onKeyDownPoint={onKeyDownPoint}
					onLeavePoint={onLeavePoint}
					pointTooltip={pointTooltip}
					series={series}
					setPointRef={setPointRef}
					styles={styles}
					tabbable={tabbable}
					width={effectiveWidth}
				/>

				{pointTooltip === 'corner' &&
					active !== null &&
					activeValue !== null && (
						<ChartTooltip
							label={series[active.seriesIndex].label}
							value={format(activeValue)}
						/>
					)}
			</div>

			<LineChartLegend
				activeSeriesIndex={active?.seriesIndex ?? null}
				format={format}
				layout={legend}
				legendValue={legendValue}
				onActivate={(seriesIndex) => {
					const target = nearestFinite(seriesIndex, 0);

					setHover(
						target === null
							? null
							: {categoryIndex: target, seriesIndex}
					);
				}}
				onDeactivate={(seriesIndex) =>
					setHover((current) =>
						current?.seriesIndex === seriesIndex ? null : current
					)
				}
				onSelect={(seriesIndex) => {
					const target = nearestFinite(seriesIndex, 0);

					if (target !== null) {
						focusPoint(seriesIndex, target);
					}
				}}
				series={series}
				styles={styles}
				titleId={titleId}
			/>
		</figure>
	);
}
