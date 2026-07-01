/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface BarDatum {

	/** Optional descriptive text read by screen readers. Defaults to `${label}: ${value}`. */
	description?: string;
	label: string;
	value: number;
}

/**
 * Color scheme.
 *
 * - `blue` (default): every bar uses `--primary`.
 * - `categorical`: each bar gets a distinct hue from the Clay chart palette
 *   via `getCategoricalColors(count)`.
 */
export type BarChartScheme = 'blue' | 'categorical';

/**
 * Legend layout. BarChart already labels each bar inline, so a legend is
 * opt-in — default `none`.
 *
 * - `none` (default): no legend below the chart.
 * - `list`: a compact swatch/label/value grid; each item focuses its bar.
 * - `table`: a semantic detail `<table>` with rank, swatch, label, value and
 *   share of total.
 */
export type BarChartLegend = 'list' | 'none' | 'table';

export interface BarChartProps {

	/** Enable bar reveal animations (default `true`). */
	animated?: boolean;

	/** Optional class name for the root `<figure>`. */
	className?: string;

	data: BarDatum[];

	/** Optional accessible long description for the chart. */
	description?: string;

	/** Height of the SVG viewport. */
	height?: number;

	/** Legend layout. Default `none`. */
	legend?: BarChartLegend;

	/** Layout direction. `vertical` is the default (bars rise upward). */
	orientation?: 'horizontal' | 'vertical';

	/** Round the bar (and matching track) into a pill. */
	rounded?: boolean;

	/** Color scheme. Default `blue`. */
	scheme?: BarChartScheme;

	/**
	 * Bar thickness preset. `default` bars fill ~60% of their band; `inline`
	 * flattens every bar to 8px (the progress-bar row).
	 */
	size?: 'default' | 'inline';

	/** Accessible name for the chart as a whole. */
	title: string;

	/** Show a light-gray track behind each bar spanning the full plot. */
	track?: boolean;

	/** Width of the SVG viewport. */
	width?: number;
}
