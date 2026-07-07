/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo} from 'react';

import {WORLD_MAP_DATA, WORLD_MAP_VIEW_BOX} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';
import {getMatchedDataIndices} from '../utils/getMatchedDataIndices';
import MapChartCountryFill from './MapChartCountryFill';
import MapChartCountryFocus from './MapChartCountryFocus';
import MapChartCountryOutline from './MapChartCountryOutline';
import MapChartMarker from './MapChartMarker';

const WORLD_MAP_ENTRIES = Object.entries(WORLD_MAP_DATA);
const MARKER_RADIUS = 6;
const MARKER_OVERLAY_RADIUS = 7.5;
const MARKER_FOCUS_RING_OUTER_RADIUS = 10.5;
const MARKER_FOCUS_RING_INNER_RADIUS = 8.5;
const STAGGER_DELAY_MS = 20;

function getStaggerDelayMs(dataIndex: number, validIndices: number[]): number {
	return validIndices.indexOf(dataIndex) * STAGGER_DELAY_MS;
}

function getValidFocusIndex(
	focusIndex: number | null,
	validIndices: number[]
): number | null {
	if (focusIndex === null || !validIndices.includes(focusIndex)) {
		return null;
	}

	return focusIndex;
}

interface MapChartPlotProps {
	activeIndex: number | null;
	baseId: string;
	colors: string[];
	data: MapDatum[];
	focusIndex: number | null;
	focusableIndex: number | null;
	itemRefFactory: (
		index: number
	) => (element: SVGGraphicsElement | null) => void;
	onBlur: () => void;
	onFocus: (index: number) => void;
	onHover: (index: number) => void;
	onHoverEnd: () => void;
	onKeyDown: (event: React.KeyboardEvent, index: number) => void;
	titleId: string;
	variant: 'choropleth' | 'markers';
}

export default function MapChartPlot({
	activeIndex,
	baseId,
	colors,
	data,
	focusIndex,
	focusableIndex,
	itemRefFactory,
	onBlur,
	onFocus,
	onHover,
	onHoverEnd,
	onKeyDown,
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

	const outlineElementsByCountry = useMemo(
		() =>
			new Map<string, React.ReactElement>(
				WORLD_MAP_ENTRIES.map(([countryCode, country]) => [
					countryCode,
					<MapChartCountryOutline
						countryCode={countryCode}
						countryPath={country.d}
						key={countryCode}
					/>,
				])
			),
		[]
	);

	const focusClipId = `${baseId}-country-focus-clip`;

	const validFocusIndex = getValidFocusIndex(focusIndex, validIndices);

	const focusedCountryPath =
		variant === 'choropleth' && validFocusIndex !== null
			? WORLD_MAP_DATA[data[validFocusIndex].country].d
			: null;

	const activeMarkerIndex =
		variant === 'markers' &&
		activeIndex !== null &&
		validIndices.includes(activeIndex)
			? activeIndex
			: null;

	const activeMarker =
		activeMarkerIndex !== null
			? {
					centroid:
						WORLD_MAP_DATA[data[activeMarkerIndex].country]
							.centroid,
					color: colors[activeMarkerIndex],
					focused: focusIndex === activeMarkerIndex,
				}
			: null;

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
							delayMs={getStaggerDelayMs(dataIndex, validIndices)}
							index={dataIndex}
							isActive={activeIndex === dataIndex}
							isFocusable={focusableIndex === dataIndex}
							key={countryCode}
							onBlur={onBlur}
							onFocus={onFocus}
							onHover={onHover}
							onHoverEnd={onHoverEnd}
							onKeyDown={onKeyDown}
							ref={itemRefFactory(dataIndex)}
						/>
					);
				}

				const outlineElement =
					outlineElementsByCountry.get(countryCode);

				if (outlineElement === undefined) {
					return null;
				}

				return outlineElement;
			})}

			<MapChartCountryFocus
				clipId={focusClipId}
				countryPath={focusedCountryPath}
			/>

			{variant === 'markers'
				? validIndices.map((index) => (
						<MapChartMarker
							color={colors[index]}
							datum={data[index]}
							delayMs={getStaggerDelayMs(index, validIndices)}
							index={index}
							isActive={index === activeIndex}
							isFocusable={focusableIndex === index}
							key={index}
							onBlur={onBlur}
							onFocus={onFocus}
							onHover={onHover}
							onHoverEnd={onHoverEnd}
							onKeyDown={onKeyDown}
							radius={MARKER_RADIUS}
							ref={itemRefFactory(index)}
						/>
					))
				: null}

			{activeMarker ? (
				<g aria-hidden="true" pointerEvents="none">
					<circle
						className="chart-map-marker-focus-ring-outer"
						cx={focusedMarkerCentroid[0]}
						cy={focusedMarkerCentroid[1]}
						r={MARKER_FOCUS_RING_OUTER_RADIUS}
					/>

					<circle
						className="chart-map-marker-focus-ring-inner"
						cx={focusedMarkerCentroid[0]}
						cy={focusedMarkerCentroid[1]}
						r={MARKER_FOCUS_RING_INNER_RADIUS}
					/>
				</g>
			) : null}
		</svg>
	);
}
