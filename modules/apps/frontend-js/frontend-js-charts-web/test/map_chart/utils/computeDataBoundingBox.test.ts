/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	WORLD_MAP_DATA,
	WORLD_MAP_VIEW_BOX,
} from '../../../src/main/resources/META-INF/resources/js/map_chart/geography/mapChartData';
import {MapDatum} from '../../../src/main/resources/META-INF/resources/js/map_chart/types/MapDatum';
import {computeDataBoundingBox} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/computeDataBoundingBox';

function parseWorldViewBox(): {
	height: number;
	minX: number;
	minY: number;
	width: number;
} {
	const [minX, minY, width, height] =
		WORLD_MAP_VIEW_BOX.split(' ').map(Number);

	return {height, minX, minY, width};
}

function getRawExtent(alpha2Codes: string[]): {
	maxX: number;
	maxY: number;
	minX: number;
	minY: number;
} {
	const points = alpha2Codes.flatMap((alpha2Code) =>
		Array.from(
			WORLD_MAP_DATA[alpha2Code].d.matchAll(
				/[ML]\s*(-?\d+(?:\.\d+)?)[,\s]+(-?\d+(?:\.\d+)?)/g
			)
		).map((match) => [Number(match[1]), Number(match[2])])
	);

	return {
		maxX: Math.max(...points.map(([x]) => x)),
		maxY: Math.max(...points.map(([, y]) => y)),
		minX: Math.min(...points.map(([x]) => x)),
		minY: Math.min(...points.map(([, y]) => y)),
	};
}

const EUROPE_DATA: MapDatum[] = [
	{country: 'FR', label: 'France', value: 40},
	{country: 'DE', label: 'Germany', value: 10},
];

describe('computeDataBoundingBox', () => {
	it('produces a bounding box tighter than the full world viewBox', () => {
		const world = parseWorldViewBox();
		const cropped = computeDataBoundingBox(EUROPE_DATA);

		expect(cropped.width).toBeLessThan(world.width);
		expect(cropped.height).toBeLessThan(world.height);
	});

	it('applies padding beyond the raw geometry extent', () => {
		const raw = getRawExtent(['FR', 'DE']);
		const padded = computeDataBoundingBox(EUROPE_DATA);

		expect(padded.minX).toBeLessThan(raw.minX);
		expect(padded.minY).toBeLessThan(raw.minY);
		expect(padded.minX + padded.width).toBeGreaterThan(raw.maxX);
		expect(padded.minY + padded.height).toBeGreaterThan(raw.maxY);
	});

	it('falls back to the full world bounding box for empty data', () => {
		expect(computeDataBoundingBox([])).toEqual(parseWorldViewBox());
	});

	it('returns a non-zero-size bounding box for a single country', () => {
		const {height, width} = computeDataBoundingBox([
			{country: 'FR', label: 'France', value: 40},
		]);

		expect(width).toBeGreaterThan(0);
		expect(height).toBeGreaterThan(0);
	});

	it('skips unknown country codes without crashing', () => {
		expect(() =>
			computeDataBoundingBox([
				{country: 'XX', label: 'Unknown', value: 1},
			])
		).not.toThrow();

		expect(
			computeDataBoundingBox([
				{country: 'XX', label: 'Unknown', value: 1},
			])
		).toEqual(parseWorldViewBox());
	});

	it('ignores unknown country codes mixed with known ones', () => {
		const withUnknown = computeDataBoundingBox([
			...EUROPE_DATA,
			{country: 'XX', label: 'Unknown', value: 1},
		]);

		expect(withUnknown).toEqual(computeDataBoundingBox(EUROPE_DATA));
	});
});
