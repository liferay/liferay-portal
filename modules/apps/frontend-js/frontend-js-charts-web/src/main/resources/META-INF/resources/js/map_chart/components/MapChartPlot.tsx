/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo} from 'react';

import {CHART_FAMILY_CLAY_PALETTE} from '../../tokens';
import {WORLD_MAP_DATA, WORLD_MAP_VIEW_BOX} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';
import {getMatchedDataIndices} from '../utils/getMatchedDataIndices';
import MapChartMarker from './MapChartMarker';

const WORLD_MAP_ENTRIES = Object.entries(WORLD_MAP_DATA);
const MARKER_COLOR = CHART_FAMILY_CLAY_PALETTE.blue;
const MARKER_RADIUS = 5;

interface MapChartPlotProps {
	data: MapDatum[];
	titleId: string;
}

export default function MapChartPlot({data, titleId}: MapChartPlotProps) {
	const validIndices = useMemo(() => getMatchedDataIndices(data), [data]);

	return (
		<svg
			aria-labelledby={titleId}
			className="chart-map-svg"
			preserveAspectRatio="xMidYMid meet"
			viewBox={WORLD_MAP_VIEW_BOX}
		>
			{WORLD_MAP_ENTRIES.map(([countryCode, country]) => (
				<path
					className="chart-map-land"
					d={country.d}
					data-country={countryCode}
					key={countryCode}
				/>
			))}

			{validIndices.map((index) => (
				<MapChartMarker
					color={MARKER_COLOR}
					datum={data[index]}
					index={index}
					key={data[index].country}
					radius={MARKER_RADIUS}
				/>
			))}
		</svg>
	);
}
