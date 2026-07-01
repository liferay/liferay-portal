/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import PieChartLegend from '../../../src/main/resources/META-INF/resources/js/pie_chart/components/PieChartLegend';

const DATA = [
	{label: 'Alpha', value: 1},
	{label: 'Beta', value: 3},
];

const DEFAULT_PROPS = {
	activeIndex: null,
	colors: ['#000', '#fff'],
	data: DATA,
	onFocus: jest.fn(),
	onHover: jest.fn(),
	onHoverEnd: jest.fn(),
	titleId: 'title-id',
	total: 4,
};

describe('PieChartLegend', () => {
	it('renders a list legend when legend is list', () => {
		const {container} = render(
			<PieChartLegend {...DEFAULT_PROPS} legend="list" />
		);

		expect(
			container.querySelector('ul.chart-pie-legend')
		).toBeInTheDocument();
		expect(screen.queryByRole('table')).not.toBeInTheDocument();
	});

	it('renders a table legend when legend is table', () => {
		const {container} = render(
			<PieChartLegend {...DEFAULT_PROPS} legend="table" />
		);

		expect(screen.getByRole('table')).toBeInTheDocument();
		expect(
			container.querySelector('ul.chart-pie-legend')
		).not.toBeInTheDocument();
	});

	it('renders nothing when legend is none', () => {
		const {container} = render(
			<PieChartLegend {...DEFAULT_PROPS} legend="none" />
		);

		expect(container).toBeEmptyDOMElement();
	});

	it('applies is-active to the active list legend item', () => {
		const {container} = render(
			<PieChartLegend {...DEFAULT_PROPS} activeIndex={1} legend="list" />
		);

		const items = container.querySelectorAll('.chart-pie-legend-item');

		expect(items[0]).not.toHaveClass('is-active');
		expect(items[1]).toHaveClass('is-active');
	});

	it('applies is-active to the active table legend row', () => {
		render(
			<PieChartLegend {...DEFAULT_PROPS} activeIndex={1} legend="table" />
		);

		const activeRow = screen.getByText('Beta').closest('tr');
		const inactiveRow = screen.getByText('Alpha').closest('tr');

		expect(activeRow).toHaveClass('is-active');
		expect(inactiveRow).not.toHaveClass('is-active');
	});
});
