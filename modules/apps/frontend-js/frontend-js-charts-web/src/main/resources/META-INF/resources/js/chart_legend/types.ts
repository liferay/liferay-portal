/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {ReactNode} from 'react';

/**
 * Legend layout shared by every chart.
 *
 * - `list`: a compact visual/label row per entry.
 * - `table`: a semantic detail table with rank, visual, label and numeric
 *   columns, sorted by `sortValue`.
 * - `none`: no legend.
 */
export type ChartLegendLayout = 'list' | 'none' | 'table';

/** One legend entry. The chart owns the visual and its values. */
export interface ChartLegendItem {
	active: boolean;

	/** Stable id passed back to the activate/deactivate/select handlers. */
	id: number;

	label: string;

	/** Optional value shown next to the label in the `list` layout. */
	listValue?: ReactNode;

	/** Ranking metric for the `table` layout (sorted descending). */
	sortValue: number;

	/** The row's color swatch or marker icon. */
	visual: ReactNode;
}

/**
 * A `table` column: its header label and how it renders each row's cell from
 * the entry. Pairing the header with its cell renderer keeps them in one place
 * instead of coupling two positional arrays.
 */
export interface ChartLegendColumn {
	label: string;
	render: (item: ChartLegendItem) => ReactNode;
}
