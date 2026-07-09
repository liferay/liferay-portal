/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {
	useCallback,
	useEffect,
	useId,
	useMemo,
	useRef,
	useState,
} from 'react';

import {useChartKeyboardNav} from '../hooks/useChartKeyboardNav';
import {getCategoricalColors} from '../palette';
import {CHART_FAMILY_CLAY_PALETTE} from '../tokens';
import BarChartLegend from './legend/BarChartLegend';
import BarChartPlot from './plot/BarChartPlot';
import BarChartStackedPlot from './plot/BarChartStackedPlot';
import {getBarChartGeometry, getStackedBarChartGeometry} from './plot/geometry';

import '../../css/BarChart.scss';

import type {BarChartProps} from './types';

const DEFAULT_WIDTH = 480;

export default function BarChart({
	alignment = 'start',
	animated = true,
	className,
	data,
	description,
	height = 280,
	legend = 'none',
	legendSwatchBorder = true,
	legendValue = 'percent',
	orientation = 'vertical',
	rounded = false,
	scheme = 'blue',
	size = 'default',
	stacked = false,
	title,
	track = false,
	width,
}: BarChartProps) {
	const reactId = useId();
	const titleId = `${reactId}-title`;
	const descId = `${reactId}-desc`;

	const [focusIndex, setFocusIndex] = useState<number | null>(null);
	const [hoverIndex, setHoverIndex] = useState<number | null>(null);
	const activeIndex = focusIndex ?? hoverIndex;
	const barRefs = useRef<Array<SVGGraphicsElement | null>>([]);
	const rootRef = useRef<HTMLElement>(null);

	// A stacked meter with no explicit `width` fills its container. We measure
	// the root with a ResizeObserver and re-lay the segments in real pixels so
	// the 8px thickness, 2px gaps and pill caps stay constant at any width (the
	// viewBox tracks the measured width 1:1). Only this case observes; a fixed
	// `width` scales uniformly and needs no measure.

	const reflow = stacked && width === undefined;
	const [measuredWidth, setMeasuredWidth] = useState<number | null>(null);

	useEffect(() => {
		if (!reflow) {
			setMeasuredWidth(null);

			return;
		}

		const element = rootRef.current;

		if (!element || typeof ResizeObserver === 'undefined') {
			return;
		}

		const resizeObserver = new ResizeObserver((entries) => {
			for (const entry of entries) {
				const nextWidth = entry.contentRect.width;

				if (nextWidth > 0) {
					setMeasuredWidth(nextWidth);
				}
			}
		});

		resizeObserver.observe(element);

		return () => resizeObserver.disconnect();
	}, [reflow]);

	// A given `width` caps the canvas; omitting it falls back to the design
	// default (non-stacked) or the measured container width (reflowing meter).

	const cappedWidth = width ?? DEFAULT_WIDTH;
	const effectiveWidth = reflow
		? measuredWidth ?? DEFAULT_WIDTH
		: cappedWidth;

	const total = useMemo(
		() => data.reduce((acc, d) => acc + Math.max(0, d.value), 0),
		[data]
	);

	// Stacked segments must always read as distinct hues, so the mode forces
	// the categorical palette (and its active styling) regardless of `scheme`.

	const effectiveScheme = stacked ? 'categorical' : scheme;

	const palette = useMemo(
		() =>
			effectiveScheme === 'categorical'
				? getCategoricalColors(data.length)
				: null,
		[effectiveScheme, data.length]
	);

	const colorFor = useCallback(
		(index: number): string =>
			palette
				? palette[index] ?? CHART_FAMILY_CLAY_PALETTE.blue
				: CHART_FAMILY_CLAY_PALETTE.blue,
		[palette]
	);

	const geometry = useMemo(
		() =>
			getBarChartGeometry({
				data,
				height,
				orientation,
				rounded,
				size,
				width: cappedWidth,
			}),
		[data, height, orientation, rounded, size, cappedWidth]
	);

	const stackedGeometry = useMemo(
		() =>
			getStackedBarChartGeometry({
				data,
				height,
				rounded,
				size,
				width: effectiveWidth,
			}),
		[data, height, rounded, size, effectiveWidth]
	);

	const summaryText = useMemo(() => {
		if (legend === 'table') {
			return description ?? '';
		}

		return (
			description ??
			data
				.map((d) => d.description ?? `${d.label}: ${d.value}`)
				.join('. ')
		);
	}, [data, description, legend]);

	const focusBar = useCallback((index: number) => {
		barRefs.current[index]?.focus();
	}, []);

	const focusableIndexes = useMemo(
		() => Array.from({length: data.length}, (_, index) => index),
		[data.length]
	);

	const onKeyDown = useChartKeyboardNav(focusableIndexes, focusBar);

	const deactivate = useCallback(
		(index: number) =>
			setHoverIndex((current) => (current === index ? null : current)),
		[]
	);

	const setBarRef = useCallback(
		(index: number, element: SVGGraphicsElement | null) => {
			barRefs.current[index] = element;
		},
		[]
	);

	return (
		<figure
			aria-describedby={descId}
			aria-labelledby={titleId}
			className={classNames(
				'charts-bar-chart',
				`charts-bar-chart--${orientation}`,
				`charts-bar-chart--${effectiveScheme}`,
				`charts-bar-chart--legend-${legend}`,
				`charts-bar-chart--size-${size}`,
				`charts-bar-chart--align-${alignment}`,
				{
					'charts-bar-chart--motion': animated,
					'charts-bar-chart--no-swatch-border': !legendSwatchBorder,
					'charts-bar-chart--rounded': rounded,
					'charts-bar-chart--stacked': stacked,
					'charts-bar-chart--track': track,
				},
				className
			)}
			ref={rootRef}
			style={reflow ? undefined : {maxWidth: cappedWidth}}
		>
			<figcaption className="charts-bar-chart__title" id={titleId}>
				{title}
			</figcaption>

			<p className="sr-only" id={descId}>
				{summaryText}
			</p>

			{stacked ? (
				<BarChartStackedPlot
					data={data}
					focusIndex={focusIndex}
					geometry={stackedGeometry}
					height={height}
					hoverIndex={hoverIndex}
					onFocus={setFocusIndex}
					onHover={setHoverIndex}
					onKeyDown={onKeyDown}
					onLeave={deactivate}
					palette={palette}
					setBarRef={setBarRef}
					width={effectiveWidth}
				/>
			) : (
				<BarChartPlot
					data={data}
					focusIndex={focusIndex}
					geometry={geometry}
					height={height}
					hoverIndex={hoverIndex}
					onFocus={setFocusIndex}
					onHover={setHoverIndex}
					onKeyDown={onKeyDown}
					onLeave={deactivate}
					palette={palette}
					setBarRef={setBarRef}
					showAxis={size !== 'inline'}
					track={track}
					width={cappedWidth}
				/>
			)}

			<BarChartLegend
				activeIndex={activeIndex}
				colorFor={colorFor}
				data={data}
				layout={legend}
				legendValue={legendValue}
				onActivate={setHoverIndex}
				onDeactivate={deactivate}
				onSelect={focusBar}
				titleId={titleId}
				total={total}
			/>
		</figure>
	);
}
