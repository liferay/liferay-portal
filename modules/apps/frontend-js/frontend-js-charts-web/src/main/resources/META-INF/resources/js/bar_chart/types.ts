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
 * Horizontal placement of the chart canvas and the legend within the figure
 * when a `width` cap leaves free space. `start` (default) keeps everything
 * flush-left like the other charts; `center` / `end` shift the block together.
 */
export type BarChartAlignment = 'center' | 'end' | 'start';

/**
 * What the `legend="list"` rows show next to each label.
 *
 * - `percent` (default): the datum's share of the total, e.g. `42.3%`.
 * - `value`: the raw value, e.g. `68`.
 * - `name`: nothing extra — just the swatch and label.
 *
 * No effect on `legend="table"`, which always breaks value and share into their
 * own columns.
 */
export type BarChartLegendValue = 'name' | 'percent' | 'value';

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

	/**
	 * Horizontal placement of the chart canvas and legend within the figure.
	 * Default `start`.
	 */
	alignment?: BarChartAlignment;

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

	/**
	 * Draw the 1px border around each legend color swatch (list and table).
	 * Default `true`. Set `false` for borderless swatches.
	 */
	legendSwatchBorder?: boolean;

	/** What the `legend="list"` rows show next to each label. Default `percent`. */
	legendValue?: BarChartLegendValue;

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

	/**
	 * Lay every datum out end-to-end in a single horizontal row, each segment
	 * sized to its share of the total (a segmented meter, not a bar per band).
	 * Implies a horizontal, single-line layout and always colors segments from
	 * the categorical palette so they read as distinct. Pairs naturally with
	 * `size="inline"` and `rounded`. On hover / focus a segment surfaces a dark
	 * tooltip with its label and value (or its `description`). Off by default.
	 */
	stacked?: boolean;

	/** Accessible name for the chart as a whole. */
	title: string;

	/** Show a light-gray track behind each bar spanning the full plot. */
	track?: boolean;

	/** Width of the SVG viewport. */
	width?: number;
}
