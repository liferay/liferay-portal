/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useMemo, useRef} from 'react';

import {useElementWidth} from '../../hooks/useElementWidth';
import {MARKER_RADIUS} from '../constants';
import {WORLD_MAP_DATA, WORLD_MAP_VIEW_BOX} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';
import {computeDataBoundingBox} from '../utils/computeDataBoundingBox';
import {computeMarkerHitRadius} from '../utils/computeMarkerHitRadius';
import {computeMarkerRadius} from '../utils/computeMarkerRadius';
import {getViewBoxWidth} from '../utils/getViewBoxWidth';
import MapChartCountryFill from './MapChartCountryFill';
import MapChartCountryFocus from './MapChartCountryFocus';
import MapChartCountryOutline from './MapChartCountryOutline';
import MapChartMarker from './MapChartMarker';
import MapChartMarkerOverlay from './MapChartMarkerOverlay';

const WORLD_MAP_ENTRIES = Object.entries(WORLD_MAP_DATA);
const STAGGER_DELAY_MS = 20;
const WORLD_VIEW_BOX_WIDTH = getViewBoxWidth(WORLD_MAP_VIEW_BOX);

function getStaggerDelayMs(dataIndex: number, validIndexes: number[]): number {
	return validIndexes.indexOf(dataIndex) * STAGGER_DELAY_MS;
}

function getValidFocusIndex(
	focusIndex: number | null,
	validIndexes: number[]
): number | null {
	if (focusIndex === null || !validIndexes.includes(focusIndex)) {
		return null;
	}

	return focusIndex;
}

function getViewBox(fit: 'data' | 'world', data: MapDatum[]): string {
	if (fit === 'world') {
		return WORLD_MAP_VIEW_BOX;
	}

	const boundingBox = computeDataBoundingBox(data);

	return `${boundingBox.minX} ${boundingBox.minY} ${boundingBox.width} ${boundingBox.height}`;
}

function getMarkerRadius(fit: 'data' | 'world', viewBoxWidth: number): number {
	if (fit === 'world') {
		return MARKER_RADIUS;
	}

	return computeMarkerRadius(
		MARKER_RADIUS,
		viewBoxWidth,
		WORLD_VIEW_BOX_WIDTH
	);
}

interface MapChartPlotProps {
	activeIndex: number | null;
	baseId: string;
	colors: string[];
	data: MapDatum[];
	fit: 'data' | 'world';
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
	validIndexes: number[];
	variant: 'choropleth' | 'markers';
}

export default function MapChartPlot({
	activeIndex,
	baseId,
	colors,
	data,
	fit,
	focusIndex,
	focusableIndex,
	itemRefFactory,
	onBlur,
	onFocus,
	onHover,
	onHoverEnd,
	onKeyDown,
	titleId,
	validIndexes,
	variant,
}: MapChartPlotProps) {
	const svgRef = useRef<SVGSVGElement>(null);
	const renderedWidthPx = useElementWidth(svgRef);

	const viewBox = useMemo(() => getViewBox(fit, data), [fit, data]);

	const viewBoxWidth = useMemo(() => getViewBoxWidth(viewBox), [viewBox]);

	const markerRadius = useMemo(
		() => getMarkerRadius(fit, viewBoxWidth),
		[fit, viewBoxWidth]
	);

	const markerScale = markerRadius / MARKER_RADIUS;

	const markerHitRadius = useMemo(
		() =>
			computeMarkerHitRadius(markerRadius, renderedWidthPx, viewBoxWidth),
		[markerRadius, renderedWidthPx, viewBoxWidth]
	);

	const dataIndexByCountry = useMemo(
		() =>
			new Map(
				validIndexes.map(
					(index) => [data[index].country, index] as const
				)
			),
		[data, validIndexes]
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

	const validFocusIndex = getValidFocusIndex(focusIndex, validIndexes);

	const focusedCountryPath =
		variant === 'choropleth' && validFocusIndex !== null
			? WORLD_MAP_DATA[data[validFocusIndex].country].d
			: null;

	const activeMarkerIndex =
		variant === 'markers' &&
		activeIndex !== null &&
		validIndexes.includes(activeIndex)
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
			ref={svgRef}
			viewBox={viewBox}
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
							delayMs={getStaggerDelayMs(dataIndex, validIndexes)}
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
				? validIndexes.map((index) => (
						<MapChartMarker
							color={colors[index]}
							datum={data[index]}
							delayMs={getStaggerDelayMs(index, validIndexes)}
							hitRadius={markerHitRadius}
							index={index}
							isActive={index === activeIndex}
							isFocusable={focusableIndex === index}
							key={index}
							onBlur={onBlur}
							onFocus={onFocus}
							onHover={onHover}
							onHoverEnd={onHoverEnd}
							onKeyDown={onKeyDown}
							radius={markerRadius}
							ref={itemRefFactory(index)}
						/>
					))
				: null}

			{activeMarker ? (
				<MapChartMarkerOverlay
					centroid={activeMarker.centroid}
					color={activeMarker.color}
					focused={activeMarker.focused}
					markerScale={markerScale}
				/>
			) : null}
		</svg>
	);
}
