/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {PieChartLegendBaseProps} from '../types/PieChartLegendBaseProps';
import PieChartLegendList from './PieChartLegendList';
import PieChartLegendTable from './PieChartLegendTable';

interface PieChartLegendProps extends PieChartLegendBaseProps {
	legend: 'list' | 'none' | 'table';
	titleId: string;
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
