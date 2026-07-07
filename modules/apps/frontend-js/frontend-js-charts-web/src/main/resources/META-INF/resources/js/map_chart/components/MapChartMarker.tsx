/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {forwardRef} from 'react';

import {WORLD_MAP_DATA} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';

interface MapChartMarkerProps {
	color: string;
	datum: MapDatum;
	index: number;
	radius: number;
}

const MapChartMarker = forwardRef<SVGCircleElement, MapChartMarkerProps>(
	function MapChartMarker({color, datum, radius}, ref) {
		const {centroid} = WORLD_MAP_DATA[datum.country];

		return (
			<circle
				aria-label={`${datum.label}: ${datum.value}`}
				className="chart-map-marker"
				cx={centroid[0]}
				cy={centroid[1]}
				r={radius}
				ref={ref}
				role="img"
				style={{'--marker-fill': color} as React.CSSProperties}
				tabIndex={0}
			/>
		);
	}
);

export default MapChartMarker;
