/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {MapDatum} from '../types/MapDatum';
import {getCountryLabel} from '../utils/getCountryLabel';

interface MapChartTooltipProps {
	datum: MapDatum;
}

export default function MapChartTooltip({datum}: MapChartTooltipProps) {
	return (
		<div aria-hidden="true" className="chart-map-tooltip">
			<span className="chart-map-tooltip-label">
				{getCountryLabel(datum)}
			</span>

			<span className="chart-map-tooltip-value">{datum.value}</span>
		</div>
	);
}
