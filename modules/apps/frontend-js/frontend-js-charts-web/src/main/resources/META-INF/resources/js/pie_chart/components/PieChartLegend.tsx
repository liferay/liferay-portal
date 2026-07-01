/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {PieDatum} from '../types/PieDatum';
import PieChartLegendList from './PieChartLegendList';
import PieChartLegendTable from './PieChartLegendTable';

interface PieChartLegendProps {
	activeIndex: number | null;
	colors: string[];
	data: PieDatum[];
	legend: 'list' | 'none' | 'table';
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	titleId: string;
	total: number;
}

export default function PieChartLegend({
	legend,
	titleId,
	...props
}: PieChartLegendProps) {
	if (legend === 'list') {
		return <PieChartLegendList {...props} />;
	}

	if (legend === 'table') {
		return <PieChartLegendTable {...props} titleId={titleId} />;
	}

	return null;
}
