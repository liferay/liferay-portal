/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface MapChartCountryOutlineProps {
	countryCode: string;
	countryPath: string;
}

export default function MapChartCountryOutline({
	countryCode,
	countryPath,
}: MapChartCountryOutlineProps) {
	return (
		<path
			className="chart-map-land"
			d={countryPath}
			data-country={countryCode}
		/>
	);
}
