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
