/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen, within} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import {LineChart} from '../../src/main/resources/META-INF/resources/js';

const CATEGORIES = ['Jan', 'Feb', 'Mar'];

const SERIES = [
	{label: 'Visits', values: [12, 18, 9]},
	{label: 'Signups', values: [4, null, 7]},
];

describe('LineChart', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;

	let resizeCallback: ResizeObserverCallback;

	beforeEach(() => {
		window.ResizeObserver = jest.fn().mockImplementation((callback) => {
			resizeCallback = callback;

			return {
				disconnect: jest.fn(),
				observe: jest.fn(),
				unobserve: jest.fn(),
			};
		});
	});

	afterEach(() => {
		window.ResizeObserver = ResizeObserverOriginal;
	});

	it('renders one accessible point per non-null value', () => {
		render(
			<LineChart
				categories={CATEGORIES}
				series={SERIES}
				title="Traffic over time"
			/>
		);

		// Three Visits points + two Signups points (Feb is a gap).

		expect(screen.getAllByRole('img')).toHaveLength(5);
		expect(screen.getByLabelText('Visits, Jan: 12')).toBeInTheDocument();
		expect(screen.getByLabelText('Signups, Mar: 7')).toBeInTheDocument();
	});

	it('omits the marker where a series has a null value', () => {
		render(
			<LineChart
				categories={CATEGORIES}
				series={SERIES}
				title="Traffic over time"
			/>
		);

		expect(screen.queryByLabelText(/Signups, Feb/)).not.toBeInTheDocument();
	});

	it('exposes the title as the chart accessible name', () => {
		render(
			<LineChart
				categories={CATEGORIES}
				series={SERIES}
				title="Traffic over time"
			/>
		);

		expect(screen.getByRole('figure')).toHaveAccessibleName(
			'Traffic over time'
		);
	});

	it('applies the scheme, legend and tooltip modifiers', () => {
		render(
			<LineChart
				categories={CATEGORIES}
				legend="table"
				pointTooltip="corner"
				scheme="categorical"
				series={SERIES}
				title="Traffic over time"
			/>
		);

		const figure = screen.getByRole('figure');

		expect(figure).toHaveClass('charts-line-chart--categorical');
		expect(figure).toHaveClass('charts-line-chart--legend-table');
		expect(figure).toHaveClass('charts-line-chart--tooltip-corner');
	});

	it('omits the motion modifier when animated is false', () => {
		render(
			<LineChart
				animated={false}
				categories={CATEGORIES}
				series={SERIES}
				title="Traffic over time"
			/>
		);

		expect(screen.getByRole('figure')).not.toHaveClass(
			'charts-line-chart--motion'
		);
	});

	it('renders a semantic detail table for legend="table"', () => {
		render(
			<LineChart
				categories={CATEGORIES}
				legend="table"
				series={SERIES}
				title="Traffic over time"
			/>
		);

		expect(screen.getAllByRole('columnheader')).toHaveLength(5);
		expect(screen.getByRole('table')).toBeInTheDocument();

		// One row header (scope="row") per series.

		expect(
			screen.getByRole('rowheader', {name: 'Visits'})
		).toBeInTheDocument();
	});

	it('shows each series latest value in the list legend by default', () => {
		render(
			<LineChart
				categories={CATEGORIES}
				legend="list"
				series={SERIES}
				title="Traffic over time"
			/>
		);

		const legend = screen.getByRole('list', {hidden: true});

		// Visits latest = 9.

		expect(within(legend).getByText('9')).toBeInTheDocument();
	});

	it('hides the list legend value for legendValue="name"', () => {
		render(
			<LineChart
				categories={CATEGORIES}
				legend="list"
				legendValue="name"
				series={SERIES}
				title="Traffic over time"
			/>
		);

		const legend = screen.getByRole('list', {hidden: true});

		expect(within(legend).getByText('Visits')).toBeInTheDocument();
		expect(within(legend).queryByText('9')).not.toBeInTheDocument();
	});

	it('applies the alignment and swatch-border modifiers', () => {
		render(
			<LineChart
				alignment="center"
				categories={CATEGORIES}
				legend="list"
				legendSwatchBorder={false}
				series={SERIES}
				title="Traffic over time"
			/>
		);

		const figure = screen.getByRole('figure');

		expect(figure).toHaveClass('charts-line-chart--align-center');
		expect(figure).toHaveClass('charts-line-chart--no-swatch-border');
	});

	it('renders every category label when they fit', () => {
		const {container} = render(
			<LineChart
				categories={CATEGORIES}
				series={SERIES}
				title="Traffic over time"
			/>
		);

		expect(
			container.querySelectorAll('.charts-line-chart__category-label')
		).toHaveLength(3);
	});

	it('culls category labels that would overlap', () => {
		const categories = Array.from(
			{length: 24},
			(_, index) => `Jul 22, ${index} AM`
		);

		const {container} = render(
			<LineChart
				categories={categories}
				series={[{label: 'Visits', values: categories.map(() => 0)}]}
				title="Traffic over time"
				width={640}
			/>
		);

		const labels = container.querySelectorAll(
			'.charts-line-chart__category-label'
		);

		expect(labels).toHaveLength(5);
		expect(labels[0]).toHaveTextContent('Jul 22, 0 AM');
		expect(labels[4]).toHaveTextContent('Jul 22, 20 AM');
	});

	it('lays the plot out at the measured container width when width is omitted', () => {
		const {container} = render(
			<LineChart
				categories={CATEGORIES}
				series={SERIES}
				title="Traffic over time"
			/>
		);

		act(() =>
			resizeCallback(
				[{contentRect: {width: 1000}} as ResizeObserverEntry],
				{} as ResizeObserver
			)
		);

		expect(container.querySelector('svg')).toHaveAttribute(
			'viewBox',
			'0 0 1000 320'
		);
	});

	it('lays the plot out at the given width regardless of the container', () => {
		const {container} = render(
			<LineChart
				categories={CATEGORIES}
				series={SERIES}
				title="Traffic over time"
				width={500}
			/>
		);

		act(() =>
			resizeCallback(
				[{contentRect: {width: 1000}} as ResizeObserverEntry],
				{} as ResizeObserver
			)
		);

		expect(container.querySelector('svg')).toHaveAttribute(
			'viewBox',
			'0 0 500 320'
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<LineChart
				categories={CATEGORIES}
				legend="list"
				series={SERIES}
				title="Traffic over time"
			/>
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
