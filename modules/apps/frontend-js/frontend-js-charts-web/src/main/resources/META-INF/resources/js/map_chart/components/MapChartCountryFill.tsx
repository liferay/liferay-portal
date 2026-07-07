/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {MapDatum} from '../types/MapDatum';

interface MapChartCountryFillProps {
	color: string;
	countryCode: string;
	countryPath: string;
	datum: MapDatum;
}

export default function MapChartCountryFill({
	color,
	countryCode,
	countryPath,
	datum,
}: MapChartCountryFillProps) {
	return (
		<path
			aria-label={`${datum.label}: ${datum.value}`}
			className="chart-map-land is-data"
			d={countryPath}
			data-country={countryCode}
			role="img"
			style={{'--country-fill': color} as React.CSSProperties}
			tabIndex={0}
		/>
	);
}
