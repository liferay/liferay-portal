/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * One-time dev tool. Not part of the module's build/test pipeline.
 *
 * Decodes the vendored `countries-110m.json` TopoJSON topology into a flat,
 * pre-projected SVG dataset so the MapChart component never has to decode
 * TopoJSON or run a projection in the browser. Re-run this script with
 * `yarn buildMapChartData` whenever the vendored topology needs refreshing,
 * then commit the regenerated `mapChartData.ts`.
 *
 * Data provenance: the topology is `visionscarto-world-atlas` (BSD-3-Clause,
 * by Philippe Rivière), a fork of mbostock's `world-atlas` that corrects
 * disputed-territory boundaries — notably separating Western Sahara from
 * Morocco and giving Kosovo its own geometry. Both are pre-built from
 * Natural Earth data (public domain). The topology is vendored at
 * `countries-110m.json` rather than fetched at build-time, so regenerating
 * is reproducible without network access. To refresh the vendored copy,
 * download it again from
 * `https://unpkg.com/visionscarto-world-atlas@1.0.0/world/110m.json`.
 *
 * Projection: equirectangular (`x = lon`, `y = -lat`, both linearly scaled
 * to the viewBox). This is a small non-interactive chart, so the added
 * distortion versus Mercator near the poles is an acceptable trade for
 * avoiding a `log(tan(...))` transform.
 *
 * Centroid: the average of every point in the geometry's largest ring
 * (by point count), which stands in for the exterior ring. A plain point
 * average is cheaper than a proper polygon-area-weighted centroid and is
 * accurate enough for placing a label or marker on a small rendered map.
 *
 * Numeric-to-alpha-2 crosswalk: the ISO 3166-1 numeric codes used as
 * `objects.countries.geometries[i].id` in the topology, mapped to their
 * ISO 3166-1 alpha-2 codes. This table is a throwaway of this script; it
 * must not be imported by, or duplicated into, any runtime module. A
 * handful of geometries (disputed territories such as Kosovo, Somaliland,
 * and Northern Cyprus) carry no numeric id at all in this topology, so
 * they fall back to their `properties.name` as the output key instead of
 * being dropped.
 */

/* eslint-env node */

'use strict';

const fs = require('fs');
const path = require('path');

const VIEW_BOX_WIDTH = 558;
const VIEW_BOX_HEIGHT = 282;

const NUMERIC_TO_ALPHA2 = {
	'004': 'AF',
	'008': 'AL',
	'010': 'AQ',
	'012': 'DZ',
	'016': 'AS',
	'020': 'AD',
	'024': 'AO',
	'028': 'AG',
	'031': 'AZ',
	'032': 'AR',
	'036': 'AU',
	'040': 'AT',
	'044': 'BS',
	'048': 'BH',
	'050': 'BD',
	'051': 'AM',
	'052': 'BB',
	'056': 'BE',
	'060': 'BM',
	'064': 'BT',
	'068': 'BO',
	'070': 'BA',
	'072': 'BW',
	'074': 'BV',
	'076': 'BR',
	'084': 'BZ',
	'086': 'IO',
	'090': 'SB',
	'092': 'VG',
	'096': 'BN',
	'100': 'BG',
	'104': 'MM',
	'108': 'BI',
	'112': 'BY',
	'116': 'KH',
	'120': 'CM',
	'124': 'CA',
	'132': 'CV',
	'136': 'KY',
	'140': 'CF',
	'144': 'LK',
	'148': 'TD',
	'152': 'CL',
	'156': 'CN',
	'158': 'TW',
	'162': 'CX',
	'166': 'CC',
	'170': 'CO',
	'174': 'KM',
	'175': 'YT',
	'178': 'CG',
	'180': 'CD',
	'184': 'CK',
	'188': 'CR',
	'191': 'HR',
	'192': 'CU',
	'196': 'CY',
	'203': 'CZ',
	'204': 'BJ',
	'208': 'DK',
	'212': 'DM',
	'214': 'DO',
	'218': 'EC',
	'222': 'SV',
	'226': 'GQ',
	'231': 'ET',
	'232': 'ER',
	'233': 'EE',
	'234': 'FO',
	'238': 'FK',
	'239': 'GS',
	'242': 'FJ',
	'246': 'FI',
	'248': 'AX',
	'250': 'FR',
	'254': 'GF',
	'258': 'PF',
	'260': 'TF',
	'262': 'DJ',
	'266': 'GA',
	'268': 'GE',
	'270': 'GM',
	'275': 'PS',
	'276': 'DE',
	'288': 'GH',
	'292': 'GI',
	'296': 'KI',
	'300': 'GR',
	'304': 'GL',
	'308': 'GD',
	'312': 'GP',
	'316': 'GU',
	'320': 'GT',
	'324': 'GN',
	'328': 'GY',
	'332': 'HT',
	'334': 'HM',
	'336': 'VA',
	'340': 'HN',
	'344': 'HK',
	'348': 'HU',
	'352': 'IS',
	'356': 'IN',
	'360': 'ID',
	'364': 'IR',
	'368': 'IQ',
	'372': 'IE',
	'376': 'IL',
	'380': 'IT',
	'384': 'CI',
	'388': 'JM',
	'392': 'JP',
	'398': 'KZ',
	'400': 'JO',
	'404': 'KE',
	'408': 'KP',
	'410': 'KR',
	'414': 'KW',
	'417': 'KG',
	'418': 'LA',
	'422': 'LB',
	'426': 'LS',
	'428': 'LV',
	'430': 'LR',
	'434': 'LY',
	'438': 'LI',
	'440': 'LT',
	'442': 'LU',
	'446': 'MO',
	'450': 'MG',
	'454': 'MW',
	'458': 'MY',
	'462': 'MV',
	'466': 'ML',
	'470': 'MT',
	'474': 'MQ',
	'478': 'MR',
	'480': 'MU',
	'484': 'MX',
	'492': 'MC',
	'496': 'MN',
	'498': 'MD',
	'499': 'ME',
	'500': 'MS',
	'504': 'MA',
	'508': 'MZ',
	'512': 'OM',
	'516': 'NA',
	'520': 'NR',
	'524': 'NP',
	'528': 'NL',
	'531': 'CW',
	'533': 'AW',
	'534': 'SX',
	'535': 'BQ',
	'540': 'NC',
	'548': 'VU',
	'554': 'NZ',
	'558': 'NI',
	'562': 'NE',
	'566': 'NG',
	'570': 'NU',
	'574': 'NF',
	'578': 'NO',
	'580': 'MP',
	'581': 'UM',
	'583': 'FM',
	'584': 'MH',
	'585': 'PW',
	'586': 'PK',
	'591': 'PA',
	'598': 'PG',
	'600': 'PY',
	'604': 'PE',
	'608': 'PH',
	'612': 'PN',
	'616': 'PL',
	'620': 'PT',
	'624': 'GW',
	'626': 'TL',
	'630': 'PR',
	'634': 'QA',
	'638': 'RE',
	'642': 'RO',
	'643': 'RU',
	'646': 'RW',
	'652': 'BL',
	'654': 'SH',
	'659': 'KN',
	'660': 'AI',
	'662': 'LC',
	'663': 'MF',
	'666': 'PM',
	'670': 'VC',
	'674': 'SM',
	'678': 'ST',
	'682': 'SA',
	'686': 'SN',
	'688': 'RS',
	'690': 'SC',
	'694': 'SL',
	'702': 'SG',
	'703': 'SK',
	'704': 'VN',
	'705': 'SI',
	'706': 'SO',
	'710': 'ZA',
	'716': 'ZW',
	'724': 'ES',
	'728': 'SS',
	'729': 'SD',
	'732': 'EH',
	'740': 'SR',
	'744': 'SJ',
	'748': 'SZ',
	'752': 'SE',
	'756': 'CH',
	'760': 'SY',
	'762': 'TJ',
	'764': 'TH',
	'768': 'TG',
	'772': 'TK',
	'776': 'TO',
	'780': 'TT',
	'784': 'AE',
	'788': 'TN',
	'792': 'TR',
	'795': 'TM',
	'796': 'TC',
	'798': 'TV',
	'800': 'UG',
	'804': 'UA',
	'807': 'MK',
	'818': 'EG',
	'826': 'GB',
	'831': 'GG',
	'832': 'JE',
	'833': 'IM',
	'834': 'TZ',
	'840': 'US',
	'850': 'VI',
	'854': 'BF',
	'858': 'UY',
	'860': 'UZ',
	'862': 'VE',
	'876': 'WF',
	'882': 'WS',
	'887': 'YE',
	'894': 'ZM',
};

function decodeArc(rawArc, transform) {
	const [scaleX, scaleY] = transform.scale;
	const [translateX, translateY] = transform.translate;

	let x = 0;
	let y = 0;

	return rawArc.map(([deltaX, deltaY]) => {
		x += deltaX;
		y += deltaY;

		return [x * scaleX + translateX, y * scaleY + translateY];
	});
}

function resolveArc(arcIndex, decodedArcs) {
	const isReversed = arcIndex < 0;
	const normalizedIndex = isReversed ? ~arcIndex : arcIndex;
	const arc = decodedArcs[normalizedIndex];

	return isReversed ? [...arc].reverse() : arc;
}

function stitchRing(arcIndexes, decodedArcs) {
	const ring = [];

	for (const arcIndex of arcIndexes) {
		const arcPoints = resolveArc(arcIndex, decodedArcs);
		const startIndex = ring.length ? 1 : 0;

		ring.push(...arcPoints.slice(startIndex));
	}

	return ring;
}

const ANTIMERIDIAN_CROSSING_THRESHOLD = 180;

/**
 * The equirectangular projection maps longitude linearly to x, so a ring
 * that crosses the antimeridian (e.g. Russia, Fiji, Antarctica) has two
 * consecutive points that jump from one edge of the map to the other. Left
 * unhandled, that jump draws as a straight line spanning the full map
 * width. Splitting the ring at every such jump keeps each piece on its own
 * side of the seam; a seam vertex interpolated at ±180 is injected at each
 * crossing, and the wrap-around piece (the run spanning ring[0]) is merged,
 * so every piece is bounded by seam vertices and closes along the map edge
 * rather than a diagonal chord back to its start.
 */
function interpolateSeamLatitude(previousPoint, point, seamLongitude) {
	const [previousLon, previousLat] = previousPoint;
	const [lon, lat] = point;

	const unwrappedLon = lon + (lon < previousLon ? 360 : -360);
	const fraction =
		(seamLongitude - previousLon) / (unwrappedLon - previousLon);

	return previousLat + fraction * (lat - previousLat);
}

function splitRingAtAntimeridian(ring) {
	const segments = [];

	let currentSegment = [ring[0]];

	for (let i = 1; i < ring.length; i++) {
		const previousPoint = currentSegment[currentSegment.length - 1];
		const [previousLon] = previousPoint;
		const point = ring[i];
		const [lon] = point;

		if (Math.abs(lon - previousLon) > ANTIMERIDIAN_CROSSING_THRESHOLD) {
			const exitSeam = previousLon > 0 ? 180 : -180;
			const seamLatitude = interpolateSeamLatitude(
				previousPoint,
				point,
				exitSeam
			);

			currentSegment.push([exitSeam, seamLatitude]);
			segments.push(currentSegment);
			currentSegment = [[-exitSeam, seamLatitude]];
		}

		currentSegment.push(point);
	}

	segments.push(currentSegment);

	if (segments.length > 1) {
		const firstSegment = segments.shift();
		const lastSegment = segments.pop();

		segments.unshift([...lastSegment, ...firstSegment.slice(1)]);
	}

	return segments.filter((segment) => segment.length > 1);
}

function collectRings(geometry, decodedArcs) {
	if (geometry.type === 'Polygon') {
		return geometry.arcs.flatMap((ring) =>
			splitRingAtAntimeridian(stitchRing(ring, decodedArcs))
		);
	}

	if (geometry.type === 'MultiPolygon') {
		return geometry.arcs.flatMap((polygon) =>
			polygon.flatMap((ring) =>
				splitRingAtAntimeridian(stitchRing(ring, decodedArcs))
			)
		);
	}

	throw new Error(`Unsupported geometry type: ${geometry.type}`);
}

function projectPoint([lon, lat]) {
	const x = ((lon + 180) / 360) * VIEW_BOX_WIDTH;
	const y = ((90 - lat) / 180) * VIEW_BOX_HEIGHT;

	return [x, y];
}

function buildPathFromRings(projectedRings) {
	return projectedRings
		.map((ring) => {
			const [firstPoint, ...restPoints] = ring;
			const moveTo = `M${firstPoint[0]},${firstPoint[1]}`;
			const lineTos = restPoints.map(([x, y]) => `L${x},${y}`);

			return [moveTo, ...lineTos, 'Z'].join(' ');
		})
		.join(' ');
}

function computeCentroid(projectedRings) {
	const largestRing = projectedRings.reduce((largest, ring) =>
		ring.length > largest.length ? ring : largest
	);

	const pointCount = largestRing.length;
	const [sumX, sumY] = largestRing.reduce(
		([accumulatedX, accumulatedY], [x, y]) => [
			accumulatedX + x,
			accumulatedY + y,
		],
		[0, 0]
	);

	return [sumX / pointCount, sumY / pointCount];
}

function buildCountryData(geometry, decodedArcs) {
	const rings = collectRings(geometry, decodedArcs);
	const projectedRings = rings.map((ring) => ring.map(projectPoint));

	return {
		centroid: computeCentroid(projectedRings),
		d: buildPathFromRings(projectedRings),
	};
}

/**
 * Most geometries carry an ISO 3166-1 numeric `id`, resolved to its alpha-2
 * code. A handful of disputed territories (Kosovo, Somaliland, Northern
 * Cyprus) carry no numeric id at all in this topology, so they fall back
 * to their `properties.name`. Every source feature must map to some key,
 * or it silently disappears from the baked dataset.
 */
function resolveKey(geometry) {
	const alpha2 = NUMERIC_TO_ALPHA2[geometry.id];

	if (alpha2) {
		return alpha2;
	}

	return geometry.properties?.name || null;
}

/**
 * Cross-references Liferay's canonical country list to map each alpha-2
 * code to its `country.<slug>` translation key, emitted into the generated
 * data as a literal client-side translation lookup keyed by that slug, so
 * the frontend build extracts every key into the module bundle and resolves
 * it client-side. Only alpha-2-keyed entries in the baked dataset get a
 * `name`; the 3 name-keyed disputed territories have no entry here and none
 * is generated for them.
 *
 * `countryNameKeys.json` is vendored from `apps/address/address-impl`'s
 * `countries.json` (each entry's `a2` mapped to `country.${name}`), the
 * same way `countries-110m.json` is vendored above, rather than read from
 * that module at build time. Re-sync it by hand if that module's country
 * slugs ever change.
 */
function loadAlpha2ToNameKey() {
	const nameKeysPath = path.join(__dirname, 'countryNameKeys.json');

	return JSON.parse(fs.readFileSync(nameKeysPath, 'utf8'));
}

function buildMapChartData(topology, alpha2ToNameKey) {
	const decodedArcs = topology.arcs.map((rawArc) =>
		decodeArc(rawArc, topology.transform)
	);

	const mapChartData = {};

	for (const geometry of topology.objects.countries.geometries) {
		const key = resolveKey(geometry);

		if (!key) {

			// eslint-disable-next-line no-console
			console.warn(
				`Skipping country with no resolvable key: ${geometry.id}`
			);
			continue;
		}

		const countryData = buildCountryData(geometry, decodedArcs);
		const alpha2 = NUMERIC_TO_ALPHA2[geometry.id];
		const nameKey = alpha2 && alpha2ToNameKey[alpha2];

		if (nameKey) {
			countryData.nameKey = nameKey;
		}

		mapChartData[key] = countryData;
	}

	return sortByKey(mapChartData);
}

function sortByKey(unsortedObject) {
	const sortedObject = {};

	for (const key of Object.keys(unsortedObject).sort()) {
		sortedObject[key] = unsortedObject[key];
	}

	return sortedObject;
}

function formatMapChartDataModule(mapChartData) {
	const header = [
		`export const WORLD_MAP_VIEW_BOX = '0 0 ${VIEW_BOX_WIDTH} ${VIEW_BOX_HEIGHT}';`,
		'',
	].join('\n');

	const serializedData = JSON.stringify(mapChartData, null, '\t').replace(
		/"nameKey": "([^"]+)"/g,
		'"name": Liferay.Language.get(\'$1\')'
	);

	const body = `export const WORLD_MAP_DATA: Record<string, {centroid: [number, number]; d: string; name?: string}> = ${serializedData};\n`;

	return `${header}${body}`;
}

function main() {
	const topologyPath = path.join(__dirname, 'countries-110m.json');
	const outputPath = path.join(__dirname, 'mapChartData.ts');

	const topology = JSON.parse(fs.readFileSync(topologyPath, 'utf8'));
	const alpha2ToNameKey = loadAlpha2ToNameKey();
	const mapChartData = buildMapChartData(topology, alpha2ToNameKey);

	fs.writeFileSync(outputPath, formatMapChartDataModule(mapChartData));

	// eslint-disable-next-line no-console
	console.log(
		`Built ${Object.keys(mapChartData).length} countries to ${outputPath}`
	);
}

main();
