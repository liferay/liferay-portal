/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import BarChartLegendList from './BarChartLegendList';
import BarChartLegendTable from './BarChartLegendTable';

import type {BarChartLegend as BarChartLegendType, BarDatum} from '../types';

interface Props {
	activeIndex: number | null;
	colorFor: (index: number) => string;
	data: BarDatum[];
	layout: BarChartLegendType;
	onActivate: (index: number) => void;
	onDeactivate: (index: number) => void;
	onSelect: (index: number) => void;
	titleId: string;
	total: number;
}

export default function BarChartLegend({
	activeIndex,
	colorFor,
	data,
	layout,
	onActivate,
	onDeactivate,
	onSelect,
	titleId,
	total,
}: Props) {
	if (layout === 'list') {
		return (
			<BarChartLegendList
				activeIndex={activeIndex}
				colorFor={colorFor}
				data={data}
				onActivate={onActivate}
				onDeactivate={onDeactivate}
				onSelect={onSelect}
				total={total}
			/>
		);
	}

	if (layout === 'table') {
		return (
			<BarChartLegendTable
				activeIndex={activeIndex}
				colorFor={colorFor}
				data={data}
				onActivate={onActivate}
				onDeactivate={onDeactivate}
				onSelect={onSelect}
				titleId={titleId}
				total={total}
			/>
		);
	}

	return null;
}
