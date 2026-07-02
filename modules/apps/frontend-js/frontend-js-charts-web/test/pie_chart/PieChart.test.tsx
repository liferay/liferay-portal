/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import PieChart from '../../src/main/resources/META-INF/resources/js/pie_chart/PieChart';
import {PieDatum} from '../../src/main/resources/META-INF/resources/js/pie_chart/types/PieDatum';

const DATA: PieDatum[] = [
	{label: 'Alpha', value: 30},
	{label: 'Beta', value: 50},
	{label: 'Gamma', value: 20},
];

describe('PieChart', () => {
	it('renders one slice path per datum', () => {
		const {container} = render(<PieChart data={DATA} title="Sales" />);

		expect(container.querySelectorAll('.chart-pie-slice')).toHaveLength(
			DATA.length
		);
	});

	it('renders the center label for a ring', () => {
		const {container} = render(<PieChart data={DATA} title="Sales" />);

		expect(
			container.querySelector('.chart-pie-center-label')
		).toBeInTheDocument();
	});

	it('hides the center label for a solid pie', () => {
		const {container} = render(
			<PieChart data={DATA} innerRadius={0} title="Sales" />
		);

		expect(
			container.querySelector('.chart-pie-center-label')
		).not.toBeInTheDocument();
	});

	it('sizes the viewBox from a preset', () => {
		const {container} = render(
			<PieChart data={DATA} size="xs" title="Sales" />
		);

		expect(container.querySelector('svg')).toHaveAttribute(
			'viewBox',
			'0 0 160 160'
		);
	});

	it('sizes the viewBox from a numeric pixel value', () => {
		const {container} = render(
			<PieChart data={DATA} size={300} title="Sales" />
		);

		expect(container.querySelector('svg')).toHaveAttribute(
			'viewBox',
			'0 0 300 300'
		);
	});

	it('caps the figure width to the xs preset', () => {
		const {container} = render(
			<PieChart data={DATA} size="xs" title="Sales" />
		);

		expect(container.querySelector('.chart-pie')).toHaveStyle({
			maxWidth: '160px',
		});
	});

	it('caps the figure width to the lg preset', () => {
		const {container} = render(
			<PieChart data={DATA} size="lg" title="Sales" />
		);

		expect(container.querySelector('.chart-pie')).toHaveStyle({
			maxWidth: '360px',
		});
	});

	it('caps the figure width to a numeric pixel value', () => {
		const {container} = render(
			<PieChart data={DATA} size={300} title="Sales" />
		);

		expect(container.querySelector('.chart-pie')).toHaveStyle({
			maxWidth: '300px',
		});
	});

	it('labels each slice with its value and percentage', () => {
		render(<PieChart data={DATA} title="Sales" />);

		expect(
			screen.getByRole('img', {name: 'Alpha: 30 (30%)'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('img', {name: 'Beta: 50 (50%)'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('img', {name: 'Gamma: 20 (20%)'})
		).toBeInTheDocument();
	});

	it('adds the reveal class when animated by default', () => {
		const {container} = render(<PieChart data={DATA} title="Sales" />);

		expect(container.querySelector('.chart-pie')).toHaveClass(
			'chart-pie-revealed'
		);
	});

	it('omits the reveal class when animation is disabled', () => {
		const {container} = render(
			<PieChart animationDisabled data={DATA} title="Sales" />
		);

		expect(container.querySelector('.chart-pie')).not.toHaveClass(
			'chart-pie-revealed'
		);
	});

	it('moves focus to the next slice on ArrowRight', () => {
		render(<PieChart data={DATA} title="Sales" />);

		const slices = screen.getAllByRole('img');

		slices[0].focus();
		fireKeyDown(slices[0], 'ArrowRight');

		expect(slices[1]).toHaveFocus();
	});

	it('moves focus to the first slice on Home', () => {
		render(<PieChart data={DATA} title="Sales" />);

		const slices = screen.getAllByRole('img');

		slices[2].focus();
		fireKeyDown(slices[2], 'Home');

		expect(slices[0]).toHaveFocus();
	});

	it('moves focus to the last slice on End', () => {
		render(<PieChart data={DATA} title="Sales" />);

		const slices = screen.getAllByRole('img');

		slices[0].focus();
		fireKeyDown(slices[0], 'End');

		expect(slices[slices.length - 1]).toHaveFocus();
	});

	it('renders a list legend with one item per datum', () => {
		const {container} = render(
			<PieChart data={DATA} legend="list" title="Sales" />
		);

		const list = container.querySelector('ul.chart-pie-legend');

		expect(list).toBeInTheDocument();
		expect(
			container.querySelectorAll('.chart-pie-legend-item')
		).toHaveLength(DATA.length);
	});

	it('ranks the table legend rows by value descending', () => {
		render(<PieChart data={DATA} legend="table" title="Sales" />);

		const table = screen.getByRole('table');
		const rowHeaders = within(table).getAllByRole('rowheader');

		expect(rowHeaders.map((cell) => cell.textContent)).toEqual([
			'Beta',
			'Alpha',
			'Gamma',
		]);
	});

	it('marks the highest value table row as first ranked', () => {
		render(<PieChart data={DATA} legend="table" title="Sales" />);

		const table = screen.getByRole('table');
		const firstBodyRow = within(table).getAllByRole('row')[1];

		expect(within(firstBodyRow).getByRole('rowheader')).toHaveTextContent(
			'Beta'
		);
	});

	it('renders neither a list nor a table legend when none', () => {
		const {container} = render(
			<PieChart data={DATA} legend="none" title="Sales" />
		);

		expect(
			container.querySelector('.chart-pie-legend')
		).not.toBeInTheDocument();
		expect(screen.queryByRole('table')).not.toBeInTheDocument();
	});

	it('keeps the screen reader summary for the list legend', () => {
		const {container} = render(
			<PieChart data={DATA} legend="list" title="Sales" />
		);

		expect(
			container.querySelector('.chart-pie-summary')
		).toBeInTheDocument();
	});

	it('suppresses the screen reader summary for the table legend', () => {
		const {container} = render(
			<PieChart data={DATA} legend="table" title="Sales" />
		);

		expect(
			container.querySelector('.chart-pie-summary')
		).not.toBeInTheDocument();
	});

	it('activates the matching slice when a legend item is hovered', async () => {
		const {container} = render(
			<PieChart data={DATA} legend="list" title="Sales" />
		);

		const items = container.querySelectorAll('.chart-pie-legend-item');

		await userEvent.hover(items[1]);

		expect(container.querySelectorAll('.chart-pie-slice')[1]).toHaveClass(
			'is-hover'
		);
	});

	it('focuses the matching slice when a table legend row is clicked', async () => {
		render(<PieChart data={DATA} legend="table" title="Sales" />);

		const table = screen.getByRole('table');
		const gammaRow = within(table)
			.getByText('Gamma')
			.closest('tr') as HTMLElement;

		await userEvent.click(gammaRow);

		expect(
			screen.getByRole('img', {name: 'Gamma: 20 (20%)'})
		).toHaveFocus();
	});

	it('shows the focus ring when a slice receives keyboard focus', async () => {
		const {container} = render(<PieChart data={DATA} title="Sales" />);

		const slices = screen.getAllByRole('img');

		await userEvent.tab();

		expect(slices[0]).toHaveFocus();
		expect(
			container.querySelector('.chart-pie-focus-ring')
		).toBeInTheDocument();
	});

	it('does not show the focus ring on hover alone', async () => {
		const {container} = render(<PieChart data={DATA} title="Sales" />);

		const slices = screen.getAllByRole('img');

		await userEvent.hover(slices[1]);

		expect(slices[1]).not.toHaveFocus();
		expect(
			container.querySelector('.chart-pie-focus-ring')
		).not.toBeInTheDocument();
	});
});

function fireKeyDown(element: Element, key: string) {
	element.dispatchEvent(new KeyboardEvent('keydown', {bubbles: true, key}));
}
