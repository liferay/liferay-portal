/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {LineMarkerShape} from './plot/markers';

export interface LineSeries {

	/** Overrides the cycled hue (categorical scheme) or blue shade. */
	color?: string;

	/** Overrides the cycled `stroke-dasharray`. */
	dasharray?: string;

	/** Optional accessible description for the series as a whole. */
	description?: string;

	label: string;

	/** Overrides the cycled marker shape. */
	marker?: LineMarkerShape;

	/** One value per category; `null` breaks the line (a data gap). */
	values: Array<number | null>;
}

/**
 * Color scheme.
 *
 * - `blue` (default): every series uses a shade of `--primary`; series stay
 *   distinguishable through their marker shape and dash pattern (works in
 *   monochrome print too).
 * - `categorical`: each series gets a distinct hue from the Clay chart palette
 *   via `getCategoricalColors(count)`.
 */
export type LineChartScheme = 'blue' | 'categorical';

/**
 * Legend layout.
 *
 * - `list` (default): a compact marker/name row per series.
 * - `table`: a semantic detail `<table>` with rank, marker, series name, total
 *   and average.
 * - `none`: no legend, useful when the title already names the sole metric.
 */
export type LineChartLegend = 'list' | 'none' | 'table';

/**
 * Point tooltip placement.
 *
 * - `popover` (default): a value chip anchored to the active point with a small
 *   downward arrow; it repositions as focus/hover moves between points.
 * - `corner`: the value chip is pinned to the top-left of the canvas (preferred
 *   when points are dense).
 * - `none`: no tooltip. Data stays accessible through the point `aria-label`s.
 */
export type LineChartPointTooltip = 'corner' | 'none' | 'popover';

export interface LineChartProps {

	/** Enable line and marker reveal animations (default `true`). */
	animated?: boolean;

	/** Category labels along the x-axis; also the series value order. */
	categories: string[];

	/** Optional class name for the root `<figure>`. */
	className?: string;

	/** Optional accessible long description for the chart. */
	description?: string;

	/** Height of the SVG viewport. Default `320`. */
	height?: number;

	/** Legend layout. Default `list`. */
	legend?: LineChartLegend;

	/** Point tooltip placement. Default `popover`. */
	pointTooltip?: LineChartPointTooltip;

	/** Color scheme. Default `blue`. */
	scheme?: LineChartScheme;

	/** One entry per line. */
	series: LineSeries[];

	/** Accessible name for the chart as a whole. */
	title: string;

	/** Width of the SVG viewport. Default `640`. */
	width?: number;

	/** Formats y-axis tick labels (and tooltip values). Default `String`. */
	yFormat?: (value: number) => string;

	/** Approximate y-axis tick count. Default `5`. */
	yTicks?: number;
}
