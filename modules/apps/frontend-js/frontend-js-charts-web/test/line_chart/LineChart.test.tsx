/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
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
