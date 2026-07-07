/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import MapChart from '../../src/main/resources/META-INF/resources/js/map_chart/MapChart';
import {WORLD_MAP_DATA} from '../../src/main/resources/META-INF/resources/js/map_chart/geography/mapChartData';
import {MapDatum} from '../../src/main/resources/META-INF/resources/js/map_chart/types/MapDatum';
import {getBlueSchemeColor} from '../../src/main/resources/META-INF/resources/js/map_chart/utils/blueSchemeColors';
import {getCategoricalSchemeColor} from '../../src/main/resources/META-INF/resources/js/map_chart/utils/categoricalSchemeColors';
import {
	computeQuantileBuckets,
	getEffectiveBucketCount,
} from '../../src/main/resources/META-INF/resources/js/map_chart/utils/computeQuantileBuckets';

const DATA: MapDatum[] = [
	{country: 'CN', label: 'China', value: 14210},
	{country: 'US', label: 'United States', value: 12450},
	{country: 'IN', label: 'India', value: 9870},
];

describe('MapChart', () => {
	it('renders one marker per datum', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		expect(
			container.querySelectorAll('circle.chart-map-marker')
		).toHaveLength(DATA.length);
	});

	it('labels each marker with its display name and value', () => {
		render(<MapChart data={DATA} title="Population" />);

		expect(
			screen.getByRole('img', {name: 'China: 14210'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('img', {name: 'United States: 12450'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('img', {name: 'India: 9870'})
		).toBeInTheDocument();
	});

	it('omits a marker for a datum whose country is absent from the baked map', () => {
		const dataWithUnmatchedCountry: MapDatum[] = [
			...DATA,
			{country: 'ZZ', label: 'Unmapped', value: 1},
		];

		render(<MapChart data={dataWithUnmatchedCountry} title="Population" />);

		expect(screen.getAllByRole('img')).toHaveLength(DATA.length);
	});

	it('renders every baked country as its own separate land path', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		const paths = container.querySelectorAll('path.chart-map-land');

		expect(paths.length).toBe(Object.keys(WORLD_MAP_DATA).length);

		const countryCodes = new Set(
			Array.from(paths).map((path) => path.getAttribute('data-country'))
		);

		expect(countryCodes.size).toBe(paths.length);
	});

	it('gives the sr-only summary a full data readout', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		const summary = container.querySelector('.chart-map-summary');

		expect(summary).toHaveTextContent('China: 14210');
		expect(summary).toHaveTextContent('United States: 12450');
		expect(summary).toHaveTextContent('India: 9870');
	});

	it('colors each marker with its blue scheme quantile bucket', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		const bucketCount = getEffectiveBucketCount(DATA, 5);
		const buckets = computeQuantileBuckets(DATA, bucketCount);

		DATA.forEach((datum, index) => {
			const marker = container.querySelector(
				`circle[aria-label="${datum.label}: ${datum.value}"]`
			) as SVGCircleElement;

			expect(marker.style.getPropertyValue('--marker-fill')).toBe(
				getBlueSchemeColor(bucketCount, buckets[index])
			);
		});
	});

	it('colors each marker with its categorical scheme quantile bucket', () => {
		const {container} = render(
			<MapChart data={DATA} scheme="categorical" title="Population" />
		);

		const bucketCount = getEffectiveBucketCount(DATA, 5);
		const buckets = computeQuantileBuckets(DATA, bucketCount);

		DATA.forEach((datum, index) => {
			const marker = container.querySelector(
				`circle[aria-label="${datum.label}: ${datum.value}"]`
			) as SVGCircleElement;

			expect(marker.style.getPropertyValue('--marker-fill')).toBe(
				getCategoricalSchemeColor(bucketCount, buckets[index])
			);
		});
	});

	it('colors the highest-value marker with the darkest blue ramp step even when steps exceeds the distinct value count', () => {
		const {container} = render(
			<MapChart data={DATA} steps={6} title="Population" />
		);

		const bucketCount = getEffectiveBucketCount(DATA, 6);

		expect(bucketCount).toBe(3);

		const marker = container.querySelector(
			'circle[aria-label="China: 14210"]'
		) as SVGCircleElement;

		expect(marker.style.getPropertyValue('--marker-fill')).toBe(
			getBlueSchemeColor(bucketCount, 2)
		);
	});

	it('gives the svg an accessible name derived from the title', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		expect(container.querySelector('svg')).toHaveAccessibleName(
			'Population'
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		await checkAccessibility({bestPractices: true, context: container});
	});
});

describe('MapChart choropleth variant', () => {
	it('marks each data country with is-data and leaves the rest as base land', () => {
		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		const dataCountries = container.querySelectorAll(
			'path.chart-map-land.is-data'
		);

		expect(dataCountries).toHaveLength(DATA.length);

		const allCountries = container.querySelectorAll('path.chart-map-land');

		expect(allCountries.length).toBe(Object.keys(WORLD_MAP_DATA).length);
	});

	it('labels each data country with its display name and value', () => {
		render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		expect(
			screen.getByRole('img', {name: 'China: 14210'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('img', {name: 'United States: 12450'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('img', {name: 'India: 9870'})
		).toBeInTheDocument();
	});

	it('fills each data country with its blue scheme quantile bucket color', () => {
		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		const bucketCount = getEffectiveBucketCount(DATA, 5);
		const buckets = computeQuantileBuckets(DATA, bucketCount);

		DATA.forEach((datum, index) => {
			const path = container.querySelector(
				`path[data-country="${datum.country}"]`
			) as SVGPathElement;

			expect(path.style.getPropertyValue('--country-fill')).toBe(
				getBlueSchemeColor(bucketCount, buckets[index])
			);
		});
	});

	it('fills each data country with its categorical scheme quantile bucket color', () => {
		const {container} = render(
			<MapChart
				data={DATA}
				scheme="categorical"
				title="Population"
				variant="choropleth"
			/>
		);

		const bucketCount = getEffectiveBucketCount(DATA, 5);
		const buckets = computeQuantileBuckets(DATA, bucketCount);

		DATA.forEach((datum, index) => {
			const path = container.querySelector(
				`path[data-country="${datum.country}"]`
			) as SVGPathElement;

			expect(path.style.getPropertyValue('--country-fill')).toBe(
				getCategoricalSchemeColor(bucketCount, buckets[index])
			);
		});
	});

	it('does not mark non-data countries as data countries', () => {
		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		const nonDataCountries = container.querySelectorAll(
			'path.chart-map-land:not(.is-data)'
		);

		expect(nonDataCountries.length).toBe(
			Object.keys(WORLD_MAP_DATA).length - DATA.length
		);

		nonDataCountries.forEach((path) => {
			expect(path).not.toHaveAttribute('aria-label');
		});
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
