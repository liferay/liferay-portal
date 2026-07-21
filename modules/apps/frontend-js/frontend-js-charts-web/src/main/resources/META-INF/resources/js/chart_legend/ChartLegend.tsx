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
	tableDividers,
	titleId,
}: Props) {
	if (layout === 'list') {
		return (
			<ChartLegendList
				items={items}
				onActivate={onActivate}
				onDeactivate={onDeactivate}
				onSelect={onSelect}
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
