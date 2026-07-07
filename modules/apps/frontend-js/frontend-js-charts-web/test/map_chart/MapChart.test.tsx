/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
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

describe('MapChart interaction', () => {
	it('renders two concentric focus ring circles when a marker gains keyboard focus', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		const marker = screen.getByRole('img', {name: 'China: 14210'});

		expect(
			container.querySelector('.chart-map-marker-focus-ring-outer')
		).not.toBeInTheDocument();

		fireEvent.focus(marker);

		expect(
			container.querySelector('.chart-map-marker-focus-ring-outer')
		).toBeInTheDocument();
		expect(
			container.querySelector('.chart-map-marker-focus-ring-inner')
		).toBeInTheDocument();
	});

	it('renders an inset halo and ring silhouette clipped to the focused country', () => {
		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		const country = screen.getByRole('img', {name: 'China: 14210'});

		fireEvent.focus(country);

		const halo = container.querySelector(
			'.chart-map-country-focus-halo'
		) as SVGPathElement;
		const ring = container.querySelector(
			'.chart-map-country-focus-ring'
		) as SVGPathElement;

		expect(halo).toBeInTheDocument();
		expect(ring).toBeInTheDocument();
		expect(halo.getAttribute('d')).toBe(country.getAttribute('d'));
		expect(ring.getAttribute('d')).toBe(country.getAttribute('d'));

		const clippedGroup = halo.closest('g[clip-path]');

		expect(clippedGroup).not.toBeNull();
		expect(clippedGroup?.getAttribute('clip-path')).toMatch(/^url\(#.+\)$/);
	});

	it('marks a hovered marker as active without rendering a focus ring', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		const marker = screen.getByRole('img', {name: 'China: 14210'});

		fireEvent.pointerEnter(marker);

		expect(marker).toHaveClass('is-active');
		expect(
			container.querySelector('.chart-map-marker-focus-ring-outer')
		).not.toBeInTheDocument();
		expect(
			container.querySelector('.chart-map-marker-focus-ring-inner')
		).not.toBeInTheDocument();
	});

	it('paints an enlarged active overlay marker on hover and marks it focused on keyboard focus', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		const marker = screen.getByRole('img', {name: 'China: 14210'});

		expect(
			container.querySelector('.chart-map-marker-overlay')
		).not.toBeInTheDocument();

		fireEvent.pointerEnter(marker);

		const overlay = container.querySelector(
			'.chart-map-marker-overlay'
		) as SVGCircleElement;

		expect(overlay).toBeInTheDocument();
		expect(Number(overlay.getAttribute('r'))).toBeGreaterThan(6);
		expect(overlay).not.toHaveClass('is-focused');

		fireEvent.focus(marker);

		expect(
			container.querySelector('.chart-map-marker-overlay')
		).toHaveClass('is-focused');
	});

	it('marks a hovered country as active without rendering an inset ring', () => {
		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		const country = screen.getByRole('img', {name: 'China: 14210'});

		fireEvent.pointerEnter(country);

		expect(country).toHaveClass('is-active');
		expect(
			container.querySelector('.chart-map-country-focus-halo')
		).not.toBeInTheDocument();
		expect(
			container.querySelector('.chart-map-country-focus-ring')
		).not.toBeInTheDocument();
	});

	it('keeps marker entrance stagger contiguous despite unmatched countries between data rows', () => {
		const dataWithGaps: MapDatum[] = [
			{country: 'CN', label: 'China', value: 14210},
			{country: 'ZZ', label: 'Unmapped', value: 1},
			{country: 'US', label: 'United States', value: 12450},
			{country: 'YY', label: 'Unmapped 2', value: 1},
			{country: 'IN', label: 'India', value: 9870},
		];

		const {container} = render(
			<MapChart data={dataWithGaps} title="Population" />
		);

		const getMarkerDelay = (label: string) =>
			(
				container.querySelector(
					`circle[aria-label="${label}"]`
				) as SVGCircleElement
			).style.getPropertyValue('--marker-delay');

		expect(getMarkerDelay('China: 14210')).toBe('0ms');
		expect(getMarkerDelay('United States: 12450')).toBe('20ms');
		expect(getMarkerDelay('India: 9870')).toBe('40ms');
	});

	it('keeps country entrance stagger contiguous despite unmatched countries between data rows', () => {
		const dataWithGaps: MapDatum[] = [
			{country: 'CN', label: 'China', value: 14210},
			{country: 'ZZ', label: 'Unmapped', value: 1},
			{country: 'US', label: 'United States', value: 12450},
			{country: 'YY', label: 'Unmapped 2', value: 1},
			{country: 'IN', label: 'India', value: 9870},
		];

		const {container} = render(
			<MapChart
				data={dataWithGaps}
				title="Population"
				variant="choropleth"
			/>
		);

		const getCountryDelay = (countryCode: string) =>
			(
				container.querySelector(
					`path[data-country="${countryCode}"]`
				) as SVGPathElement
			).style.getPropertyValue('--country-delay');

		expect(getCountryDelay('CN')).toBe('0ms');
		expect(getCountryDelay('US')).toBe('20ms');
		expect(getCountryDelay('IN')).toBe('40ms');
	});

	it('only tabs to the currently focusable marker, keeping the rest out of tab order', () => {
		render(<MapChart data={DATA} title="Population" />);

		const focusedMarker = screen.getByRole('img', {name: 'China: 14210'});
		const otherMarker = screen.getByRole('img', {
			name: 'United States: 12450',
		});

		expect(focusedMarker).toHaveAttribute('tabindex', '0');
		expect(otherMarker).toHaveAttribute('tabindex', '-1');

		fireEvent.focus(focusedMarker);
		fireEvent.keyDown(focusedMarker, {key: 'ArrowRight'});

		expect(otherMarker).toHaveAttribute('tabindex', '0');
		expect(focusedMarker).toHaveAttribute('tabindex', '-1');
	});

	it('renders markers and data countries as targets of the body.c-prefers-reduced-motion animation override', () => {
		document.body.classList.add('c-prefers-reduced-motion');

		const {container} = render(<MapChart data={DATA} title="Population" />);

		expect(document.body).toHaveClass('c-prefers-reduced-motion');
		expect(
			container.querySelectorAll('circle.chart-map-marker')
		).toHaveLength(DATA.length);

		document.body.classList.remove('c-prefers-reduced-motion');
	});

	it('renders data countries as targets of the body.c-prefers-reduced-motion animation override', () => {
		document.body.classList.add('c-prefers-reduced-motion');

		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		expect(document.body).toHaveClass('c-prefers-reduced-motion');
		expect(
			container.querySelectorAll('path.chart-map-land.is-data')
		).toHaveLength(DATA.length);

		document.body.classList.remove('c-prefers-reduced-motion');
	});

	it('has no accessibility violations while a marker is focused', async () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		fireEvent.focus(screen.getByRole('img', {name: 'China: 14210'}));

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('has no accessibility violations while a country is focused', async () => {
		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		fireEvent.focus(screen.getByRole('img', {name: 'China: 14210'}));

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('renders no tooltip when nothing is hovered or focused', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		expect(
			container.querySelector('.chart-map-tooltip')
		).not.toBeInTheDocument();
	});

	it('renders the tooltip with the hovered datum label and value', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		fireEvent.pointerEnter(screen.getByRole('img', {name: 'China: 14210'}));

		const tooltip = container.querySelector('.chart-map-tooltip');

		expect(tooltip).toBeInTheDocument();
		expect(tooltip).toHaveTextContent('China');
		expect(tooltip).toHaveTextContent('14210');
	});

	it('renders the tooltip with the keyboard-focused datum label and value', () => {
		const {container} = render(
			<MapChart data={DATA} title="Population" variant="choropleth" />
		);

		fireEvent.focus(screen.getByRole('img', {name: 'India: 9870'}));

		const tooltip = container.querySelector('.chart-map-tooltip');

		expect(tooltip).toBeInTheDocument();
		expect(tooltip).toHaveTextContent('India');
		expect(tooltip).toHaveTextContent('9870');
	});

	it('keeps the tooltip free of the active datum bucket fill color', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		fireEvent.pointerEnter(screen.getByRole('img', {name: 'China: 14210'}));

		const tooltip = container.querySelector(
			'.chart-map-tooltip'
		) as HTMLElement;

		expect(tooltip.style.getPropertyValue('--marker-fill')).toBe('');
		expect(tooltip.style.backgroundColor).toBe('');
		expect(tooltip).not.toHaveAttribute('style');
	});

	it('marks the tooltip as aria-hidden', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		fireEvent.pointerEnter(screen.getByRole('img', {name: 'China: 14210'}));

		expect(container.querySelector('.chart-map-tooltip')).toHaveAttribute(
			'aria-hidden',
			'true'
		);
	});

	it('has no accessibility violations while the tooltip is visible', async () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		fireEvent.pointerEnter(screen.getByRole('img', {name: 'China: 14210'}));

		await checkAccessibility({bestPractices: true, context: container});
	});
});

describe('MapChart legend', () => {
	it('renders no legend by default', () => {
		const {container} = render(<MapChart data={DATA} title="Population" />);

		expect(
			container.querySelector('.charts-legend')
		).not.toBeInTheDocument();
		expect(
			container.querySelector('.charts-legend-table')
		).not.toBeInTheDocument();
		expect(
			container.querySelector('.chart-map-legend-scale')
		).not.toBeInTheDocument();
	});

	it('renders one list item per datum', () => {
		const {container} = render(
			<MapChart data={DATA} legend="list" title="Population" />
		);

		expect(container.querySelectorAll('.charts-legend__item')).toHaveLength(
			DATA.length
		);
	});

	it('renders one table row per datum', () => {
		const {container} = render(
			<MapChart data={DATA} legend="table" title="Population" />
		);

		expect(
			container.querySelectorAll('.charts-legend-table__row')
		).toHaveLength(DATA.length);

		const headerRow = container.querySelector(
			'.charts-legend-table thead tr'
		);

		expect(headerRow).toHaveTextContent('country');

		const shareCell = container.querySelector(
			'.charts-legend-table__row:first-child .charts-legend-table__cell--number:last-child'
		);

		expect(shareCell).toHaveTextContent('38.9%');
	});

	it('sorts the list legend by value descending regardless of input order', () => {
		const unsortedData: MapDatum[] = [
			{country: 'FR', label: 'France', value: 100},
			{country: 'DE', label: 'Germany', value: 500},
			{country: 'BR', label: 'Brazil', value: 300},
		];

		const {container} = render(
			<MapChart data={unsortedData} legend="list" title="Population" />
		);

		const labels = Array.from(
			container.querySelectorAll('.charts-legend__label')
		).map((label) => label.textContent);

		expect(labels).toEqual(['Germany', 'Brazil', 'France']);
	});

	it('sorts the table legend by value descending with a matching 1..N rank regardless of input order', () => {
		const unsortedData: MapDatum[] = [
			{country: 'FR', label: 'France', value: 100},
			{country: 'DE', label: 'Germany', value: 500},
			{country: 'BR', label: 'Brazil', value: 300},
		];

		const {container} = render(
			<MapChart data={unsortedData} legend="table" title="Population" />
		);

		const labels = Array.from(
			container.querySelectorAll('.charts-legend-table__cell--label')
		).map((label) => label.textContent);

		expect(labels).toEqual(['Germany', 'Brazil', 'France']);

		const ranks = Array.from(
			container.querySelectorAll('.charts-legend-table__cell--rank')
		).map((rank) => rank.textContent);

		expect(ranks).toEqual(['1', '2', '3']);
	});

	it('renders a scale swatch per effective bucket, matching the map instead of the requested steps', () => {
		const {container} = render(
			<MapChart data={DATA} legend="scale" steps={6} title="Population" />
		);

		const bucketCount = getEffectiveBucketCount(DATA, 6);

		expect(bucketCount).toBe(3);

		const swatches = container.querySelectorAll(
			'.chart-map-legend-scale-swatch'
		);

		expect(swatches).toHaveLength(bucketCount);

		const markerFills = new Set(
			Array.from(
				container.querySelectorAll('circle.chart-map-marker')
			).map((marker) =>
				(marker as SVGCircleElement).style.getPropertyValue(
					'--marker-fill'
				)
			)
		);

		expect(markerFills.size).toBe(bucketCount);

		expect(screen.getByText('less')).toBeInTheDocument();
		expect(screen.getByText('more')).toBeInTheDocument();
	});

	it('marks the hovered marker as active in the list legend', () => {
		const {container} = render(
			<MapChart data={DATA} legend="list" title="Population" />
		);

		const marker = screen.getByRole('img', {name: 'China: 14210'});
		const legendItems = container.querySelectorAll('.charts-legend__item');

		expect(legendItems[0]).not.toHaveClass('is-active');

		fireEvent.pointerEnter(marker);

		expect(legendItems[0]).toHaveClass('is-active');
	});

	it('marks the keyboard-focused marker as active in the table legend', () => {
		const {container} = render(
			<MapChart data={DATA} legend="table" title="Population" />
		);

		const marker = screen.getByRole('img', {name: 'India: 9870'});
		const legendRows = container.querySelectorAll(
			'.charts-legend-table__row'
		);

		fireEvent.focus(marker);

		expect(legendRows[2]).toHaveClass('is-active');
	});

	it('focuses the corresponding marker when a list legend item is clicked', () => {
		render(<MapChart data={DATA} legend="list" title="Population" />);

		const legendItem = screen.getByText('United States').closest('li');

		fireEvent.click(legendItem as HTMLLIElement);

		expect(
			screen.getByRole('img', {name: 'United States: 12450'})
		).toHaveFocus();
	});

	it('has no accessibility violations with the list legend', async () => {
		const {container} = render(
			<MapChart data={DATA} legend="list" title="Population" />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('has no accessibility violations with the table legend', async () => {
		const {container} = render(
			<MapChart data={DATA} legend="table" title="Population" />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('has no accessibility violations with the scale legend', async () => {
		const {container} = render(
			<MapChart data={DATA} legend="scale" title="Population" />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
