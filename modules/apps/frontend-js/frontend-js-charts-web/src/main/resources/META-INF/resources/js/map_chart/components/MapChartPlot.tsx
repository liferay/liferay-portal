/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo} from 'react';

import {WORLD_MAP_DATA, WORLD_MAP_VIEW_BOX} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';
import {getMatchedDataIndices} from '../utils/getMatchedDataIndices';
import MapChartCountryFill from './MapChartCountryFill';
import MapChartCountryOutline from './MapChartCountryOutline';
import MapChartMarker from './MapChartMarker';

const WORLD_MAP_ENTRIES = Object.entries(WORLD_MAP_DATA);
const MARKER_RADIUS = 5;

interface MapChartPlotProps {
	colors: string[];
	data: MapDatum[];
	titleId: string;
	variant: 'choropleth' | 'markers';
}

export default function MapChartPlot({
	colors,
	data,
	titleId,
	variant,
}: MapChartPlotProps) {
	const validIndices = useMemo(() => getMatchedDataIndices(data), [data]);

	const dataIndexByCountry = useMemo(
		() =>
			new Map(
				validIndices.map(
					(index) => [data[index].country, index] as const
				)
			),
		[data, validIndices]
	);

	return (
		<svg
			aria-labelledby={titleId}
			className="chart-map-svg"
			preserveAspectRatio="xMidYMid meet"
			viewBox={WORLD_MAP_VIEW_BOX}
		>
			{WORLD_MAP_ENTRIES.map(([countryCode, country]) => {
				const dataIndex = dataIndexByCountry.get(countryCode);

				if (variant === 'choropleth' && dataIndex !== undefined) {
					return (
						<MapChartCountryFill
							color={colors[dataIndex]}
							countryCode={countryCode}
							countryPath={country.d}
							datum={data[dataIndex]}
							key={countryCode}
						/>
					);
				}

				return (
					<MapChartCountryOutline
						countryCode={countryCode}
						countryPath={country.d}
						key={countryCode}
					/>
				);
			})}

			{variant === 'markers'
				? validIndices.map((index) => (
						<MapChartMarker
							color={colors[index]}
							datum={data[index]}
							index={index}
							key={index}
							radius={MARKER_RADIUS}
						/>
					))
				: null}
		</svg>
	);
}
