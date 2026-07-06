/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

import {
	WORLD_MAP_DATA,
	WORLD_MAP_VIEW_BOX,
} from '../../../src/main/resources/META-INF/resources/js/map_chart/geography/mapChartData';

const GEOGRAPHY_DIRECTORY = path.join(
	__dirname,
	'../../../src/main/resources/META-INF/resources/js/map_chart/geography'
);

const NW_AFRICA_ALPHA2_CODES = ['ML', 'DZ', 'MR'];

const ANTIMERIDIAN_ALPHA2_CODES = ['RU', 'FJ', 'US'];

const MAX_WELL_FORMED_SEGMENT_RATIO = 0.2;

interface CountryGeometry {
	id?: string;
	properties?: {name?: string};
}

interface CountriesTopology {
	objects: {
		countries: {
			geometries: CountryGeometry[];
		};
	};
}

function readCountriesTopology(): CountriesTopology {
	const topologyPath = path.join(GEOGRAPHY_DIRECTORY, 'countries-110m.json');

	return JSON.parse(fs.readFileSync(topologyPath, 'utf8'));
}

function parseViewBox(viewBox: string): {height: number; width: number} {
	const [, , width, height] = viewBox.split(' ').map(Number);

	return {height, width};
}

function parseSubpathPoints(subpath: string): Array<[number, number]> {
	const pointPattern = /[ML](-?[\d.]+),(-?[\d.]+)/g;
	const points: Array<[number, number]> = [];

	let match = pointPattern.exec(subpath);

	while (match !== null) {
		points.push([Number(match[1]), Number(match[2])]);

		match = pointPattern.exec(subpath);
	}

	return points;
}

function computeBounds(d: string): {maxY: number; minY: number} {
	const ys = d
		.split('Z')
		.flatMap((subpath) => parseSubpathPoints(subpath.trim()))
		.map(([, y]) => y);

	return {maxY: Math.max(...ys), minY: Math.min(...ys)};
}

function findLongestSegment(d: string): number {
	const subpaths = d
		.split('Z')
		.map((subpath) => subpath.trim())
		.filter(Boolean);

	let longestSegment = 0;

	for (const subpath of subpaths) {
		const points = parseSubpathPoints(subpath);

		for (let index = 1; index < points.length; index++) {
			const [previousX, previousY] = points[index - 1];
			const [x, y] = points[index];

			const segmentLength = Math.hypot(x - previousX, y - previousY);

			longestSegment = Math.max(longestSegment, segmentLength);
		}

		if (points.length > 2) {
			const [firstX, firstY] = points[0];
			const [lastX, lastY] = points[points.length - 1];

			longestSegment = Math.max(
				longestSegment,
				Math.hypot(lastX - firstX, lastY - firstY)
			);
		}
	}

	return longestSegment;
}

describe('buildMapChartData', () => {
	it('bakes every source country feature into the output', () => {
		const topology = readCountriesTopology();

		expect(Object.keys(WORLD_MAP_DATA).length).toBe(
			topology.objects.countries.geometries.length
		);
	});

	it.each(NW_AFRICA_ALPHA2_CODES)(
		'produces a well-formed path for %s with no spurious diagonal',
		(alpha2) => {
			const country = WORLD_MAP_DATA[alpha2];
			const {width} = parseViewBox(WORLD_MAP_VIEW_BOX);

			expect(country).toBeDefined();
			expect(country.d.startsWith('M')).toBe(true);
			expect(country.d.trimEnd().endsWith('Z')).toBe(true);

			const longestSegment = findLongestSegment(country.d);

			expect(longestSegment).toBeLessThan(
				width * MAX_WELL_FORMED_SEGMENT_RATIO
			);
		}
	);

	it.each(ANTIMERIDIAN_ALPHA2_CODES)(
		'closes antimeridian-crossing %s along the seam with no full-width chord',
		(alpha2) => {
			const country = WORLD_MAP_DATA[alpha2];
			const {width} = parseViewBox(WORLD_MAP_VIEW_BOX);

			expect(country).toBeDefined();

			const longestSegment = findLongestSegment(country.d);

			expect(longestSegment).toBeLessThan(
				width * MAX_WELL_FORMED_SEGMENT_RATIO
			);
		}
	);

	it('bakes a resolved name for an alpha-2-keyed country', () => {
		expect(WORLD_MAP_DATA.AE.name).toBe('country.united-arab-emirates');
	});

	it('omits name for a name-keyed country with no numeric id', () => {
		expect(WORLD_MAP_DATA.Kosovo.name).toBeUndefined();
	});

	it('separates Western Sahara from Morocco at their shared border', () => {
		const morocco = WORLD_MAP_DATA['MA'];
		const westernSahara = WORLD_MAP_DATA['EH'];

		expect(morocco).toBeDefined();
		expect(westernSahara).toBeDefined();

		const moroccoBounds = computeBounds(morocco.d);
		const westernSaharaBounds = computeBounds(westernSahara.d);

		expect(
			westernSaharaBounds.maxY - westernSaharaBounds.minY
		).toBeGreaterThan(8);

		expect(moroccoBounds.maxY).toBeLessThanOrEqual(
			westernSaharaBounds.minY + 1
		);
	});
});
