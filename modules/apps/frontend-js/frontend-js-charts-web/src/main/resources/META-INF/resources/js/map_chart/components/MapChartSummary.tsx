/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {MapDatum} from '../types/MapDatum';
import {getCountryLabel} from '../utils/getCountryLabel';
import {toPercent} from '../utils/percent';

interface MapChartSummaryProps {
	data: MapDatum[];
	id: string;
	total: number;
}

export default function MapChartSummary({
	data,
	id,
	total,
}: MapChartSummaryProps) {
	return (
		<p className="chart-map-summary sr-only" id={id}>
			{data
				.map(
					(datum) =>
						`${getCountryLabel(datum)}: ${datum.value} (${toPercent(
							datum.value,
							total
						)}%). `
				)
				.join('')}
		</p>
	);
}
