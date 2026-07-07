/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {WORLD_MAP_DATA, WORLD_MAP_VIEW_BOX} from '../geography/mapChartData';
import {MapDatum} from '../types/MapDatum';

const PATH_POINT_PATTERN = /[ML]\s*(-?\d+(?:\.\d+)?)[,\s]+(-?\d+(?:\.\d+)?)/g;
const PADDING_RATIO = 0.1;
const MIN_PADDING = 20;
const MIN_WIDTH = 200;
const MIN_HEIGHT = 100;

export interface DataBoundingBox {
	height: number;
	minX: number;
	minY: number;
	width: number;
}

interface RawExtent {
	maxX: number;
	maxY: number;
	minX: number;
	minY: number;
}

function extractPathPoints(d: string): number[][] {
	const points: number[][] = [];

	for (const match of d.matchAll(PATH_POINT_PATTERN)) {
		points.push([Number(match[1]), Number(match[2])]);
	}

	return points;
}

function getMatchedCountryPaths(data: MapDatum[]): string[] {
	return data
		.map((datum) => WORLD_MAP_DATA[datum.country])
		.filter((country): country is {centroid: [number, number]; d: string} =>
			Boolean(country)
		)
		.map((country) => country.d);
}

function expandExtent(extent: RawExtent, points: number[][]): RawExtent {
	return points.reduce(
		(accumulator, [x, y]) => ({
			maxX: Math.max(accumulator.maxX, x),
			maxY: Math.max(accumulator.maxY, y),
			minX: Math.min(accumulator.minX, x),
			minY: Math.min(accumulator.minY, y),
		}),
		extent
	);
}

function isFiniteExtent(extent: RawExtent): boolean {
	return (
		Number.isFinite(extent.minX) &&
		Number.isFinite(extent.minY) &&
		Number.isFinite(extent.maxX) &&
		Number.isFinite(extent.maxY)
	);
}

function padExtent(extent: RawExtent): RawExtent {
	const paddingX = Math.max(
		(extent.maxX - extent.minX) * PADDING_RATIO,
		MIN_PADDING
	);
	const paddingY = Math.max(
		(extent.maxY - extent.minY) * PADDING_RATIO,
		MIN_PADDING
	);

	return {
		maxX: extent.maxX + paddingX,
		maxY: extent.maxY + paddingY,
		minX: extent.minX - paddingX,
		minY: extent.minY - paddingY,
	};
}

function toBoundingBox(extent: RawExtent): DataBoundingBox {
	return {
		height: extent.maxY - extent.minY,
		minX: extent.minX,
		minY: extent.minY,
		width: extent.maxX - extent.minX,
	};
}

function enforceMinSize(box: DataBoundingBox): DataBoundingBox {
	const width = Math.max(box.width, MIN_WIDTH);
	const height = Math.max(box.height, MIN_HEIGHT);

	return {
		height,
		minX: Math.max(0, box.minX - (width - box.width) / 2),
		minY: Math.max(0, box.minY - (height - box.height) / 2),
		width,
	};
}

function getWorldBoundingBox(): DataBoundingBox {
	const [minX, minY, width, height] =
		WORLD_MAP_VIEW_BOX.split(' ').map(Number);

	return {height, minX, minY, width};
}

export function computeDataBoundingBox(data: MapDatum[]): DataBoundingBox {
	const paths = getMatchedCountryPaths(data);

	if (!paths.length) {
		return getWorldBoundingBox();
	}

	const emptyExtent: RawExtent = {
		maxX: -Infinity,
		maxY: -Infinity,
		minX: Infinity,
		minY: Infinity,
	};

	const extent = paths.reduce(
		(accumulator, d) => expandExtent(accumulator, extractPathPoints(d)),
		emptyExtent
	);

	if (!isFiniteExtent(extent)) {
		return getWorldBoundingBox();
	}

	return enforceMinSize(toBoundingBox(padExtent(extent)));
}
