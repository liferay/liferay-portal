/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ChartLegendList from './ChartLegendList';
import ChartLegendTable from './ChartLegendTable';

import '../../css/ChartLegend.scss';

import type {
	ChartLegendColumn,
	ChartLegendItem,
	ChartLegendLayout,
} from './types';

interface Props {

	/** Column descriptors for the `table` layout (header + cell renderer). */
	columns: ChartLegendColumn[];
	items: ChartLegendItem[];
	labelColumnLabel?: string;
	layout: ChartLegendLayout;
	onActivate: (id: number) => void;
	onDeactivate: (id: number) => void;
	onSelect: (id: number) => void;

	/**
	 * Where the legend sits relative to the chart. A `list` at the `bottom`
	 * stacks in a single column and shows the table columns per row; the
	 * `table` layout ignores it. Default `end`.
	 */
	position?: 'bottom' | 'end';

	/** Draw the divider lines under the `table` layout header and rows. Default `true`. */
	tableDividers?: boolean;

	titleId: string;
}

export default function ChartLegend({
	columns,
	items,
	labelColumnLabel,
	layout,
	onActivate,
	onDeactivate,
	onSelect,
	position = 'end',
	tableDividers,
	titleId,
}: Props) {
	if (layout === 'list') {
		const stacked = position === 'bottom';

		return (
			<ChartLegendList
				columns={stacked ? columns : undefined}
				items={items}
				onActivate={onActivate}
				onDeactivate={onDeactivate}
				onSelect={onSelect}
				stacked={stacked}
			/>
		);
	}

	if (layout === 'table') {
		return (
			<ChartLegendTable
				columns={columns}
				dividers={tableDividers}
				items={items}
				labelColumnLabel={labelColumnLabel}
				onActivate={onActivate}
				onDeactivate={onDeactivate}
				onSelect={onSelect}
				titleId={titleId}
			/>
		);
	}

	return null;
}
