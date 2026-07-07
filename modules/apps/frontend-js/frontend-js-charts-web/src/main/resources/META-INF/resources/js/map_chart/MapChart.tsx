/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useId, useMemo} from 'react';

import MapChartPlot from './components/MapChartPlot';
import MapChartSummary from './components/MapChartSummary';
import {MapChartProps} from './types/MapChartProps';

import '../../css/MapChart.scss';

export default function MapChart({data, title}: MapChartProps) {
	const baseId = useId();
	const titleId = `${baseId}-title`;
	const summaryId = `${baseId}-summary`;

	const total = useMemo(
		() => data.reduce((sum, datum) => sum + Math.max(0, datum.value), 0),
		[data]
	);

	return (
		<figure
			aria-describedby={summaryId}
			aria-labelledby={titleId}
			className="chart-map"
		>
			<figcaption className="chart-map-caption" id={titleId}>
				{title}
			</figcaption>

			<MapChartSummary data={data} id={summaryId} total={total} />

			<MapChartPlot data={data} titleId={titleId} />
		</figure>
	);
}
