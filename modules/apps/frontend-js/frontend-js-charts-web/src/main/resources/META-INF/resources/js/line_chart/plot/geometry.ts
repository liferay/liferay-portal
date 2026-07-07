/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {LineSeries} from '../types';

/** A single plotted data point (a non-null value). */
export interface LinePoint {
	categoryIndex: number;
	value: number;
	x: number;
	y: number;
}

export interface LineSeriesLayout {

	/**
	 * SVG `path` `d` strings for the line. A `null` value breaks the series into
	 * more than one segment, so each string is a contiguous run of points.
	 */
	paths: string[];

	/** One entry per category; `null` where the series has a data gap. */
	points: Array<LinePoint | null>;
}

export interface LineChartTick {
	value: number;
	y: number;
}

export interface LineChartGeometry {
	axis: {x1: number; x2: number; y1: number; y2: number};
	categoryX: number[];
	plot: {height: number; width: number; x: number; y: number};
	series: LineSeriesLayout[];
	ticks: LineChartTick[];
}

interface Options {
	categories: string[];
	height: number;
	series: LineSeries[];
	width: number;
	yTicks: number;
}

const PADDING = {bottom: 32, left: 48, right: 24, top: 16};

/**
 * "Nice" tick step from the {1, 2, 2.5, 5} x 10^k family, so axis labels land on
 * readable values (5, 10, 25, 50) rather than arbitrary fractions.
 */
export function niceTickStep(range: number, count: number): number {
	if (range <= 0 || count <= 0) {
		return 1;
	}

	const rough = range / count;
	const magnitude = Math.pow(10, Math.floor(Math.log10(rough)));
	const normalized = rough / magnitude;

	let nice;

	if (normalized < 1.5) {
		nice = 1;
	}
	else if (normalized < 3) {
		nice = 2;
	}
	else if (normalized < 4) {
		nice = 2.5;
	}
	else if (normalized < 7) {
		nice = 5;
	}
	else {
		nice = 10;
	}

	return nice * magnitude;
}

function isFiniteValue(value: number | null): value is number {
	return value !== null && Number.isFinite(value);
}

/**
 * Turns the chart props into per-series coordinates, split polyline paths, and
 * nice y-axis ticks, keeping the layout math out of the render tree.
 */
export function getLineChartGeometry({
	categories,
	height,
	series,
	width,
	yTicks,
}: Options): LineChartGeometry {
	const plotWidth = Math.max(0, width - PADDING.left - PADDING.right);
	const plotHeight = Math.max(0, height - PADDING.top - PADDING.bottom);

	const values = series.flatMap((line) => line.values).filter(isFiniteValue);

	const rawMin = values.length ? Math.min(...values) : 0;
	const rawMax = values.length ? Math.max(...values) : 1;

	const step = niceTickStep(Math.max(rawMax - rawMin, rawMax, 1), yTicks);

	const domainMin = rawMin >= 0 ? 0 : Math.floor(rawMin / step) * step;
	const domainMax = Math.max(
		domainMin + step,
		Math.ceil(rawMax / step) * step
	);
	const domainRange = domainMax - domainMin;

	const categoryX = categories.map((_, index) =>
		categories.length <= 1
			? PADDING.left + plotWidth / 2
			: PADDING.left + (plotWidth * index) / (categories.length - 1)
	);

	const yFor = (value: number) =>
		PADDING.top + plotHeight * (1 - (value - domainMin) / domainRange);

	const ticks: LineChartTick[] = [];

	for (let value = domainMin; value <= domainMax + step / 2; value += step) {
		ticks.push({value, y: yFor(value)});
	}

	const seriesLayouts = series.map((line): LineSeriesLayout => {
		const points = categories.map((_, index): LinePoint | null => {
			const value = line.values[index] ?? null;

			if (!isFiniteValue(value)) {
				return null;
			}

			return {
				categoryIndex: index,
				value,
				x: categoryX[index],
				y: yFor(value),
			};
		});

		const paths: string[] = [];

		let current: LinePoint[] = [];

		const flush = () => {
			if (current.length > 1) {
				paths.push(
					current
						.map(
							(point, index) =>
								`${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`
						)
						.join(' ')
				);
			}

			current = [];
		};

		for (const point of points) {
			if (point) {
				current.push(point);
			}
			else {
				flush();
			}
		}

		flush();

		return {paths, points};
	});

	return {
		axis: {
			x1: PADDING.left,
			x2: PADDING.left + plotWidth,
			y1: PADDING.top + plotHeight,
			y2: PADDING.top + plotHeight,
		},
		categoryX,
		plot: {
			height: plotHeight,
			width: plotWidth,
			x: PADDING.left,
			y: PADDING.top,
		},
		series: seriesLayouts,
		ticks,
	};
}
