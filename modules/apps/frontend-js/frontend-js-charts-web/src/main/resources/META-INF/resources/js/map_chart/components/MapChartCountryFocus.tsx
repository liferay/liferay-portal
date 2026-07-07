/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface MapChartCountryFocusProps {
	clipId: string;
	countryPath: string | null;
}

export default function MapChartCountryFocus({
	clipId,
	countryPath,
}: MapChartCountryFocusProps) {
	if (!countryPath) {
		return null;
	}

	return (
		<>
			<defs>
				<clipPath id={clipId}>
					<path d={countryPath} />
				</clipPath>
			</defs>

			<g
				aria-hidden="true"
				clipPath={`url(#${clipId})`}
				pointerEvents="none"
			>
				<path
					className="chart-map-country-focus-halo"
					d={countryPath}
				/>

				<path
					className="chart-map-country-focus-ring"
					d={countryPath}
				/>
			</g>
		</>
	);
}
