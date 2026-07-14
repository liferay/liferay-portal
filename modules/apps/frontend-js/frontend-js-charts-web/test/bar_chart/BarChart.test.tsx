/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, within} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import {BarChart} from '../../src/main/resources/META-INF/resources/js';

const DATA = [
	{label: 'Jan', value: 12},
	{label: 'Feb', value: 18},
	{label: 'Mar', value: 9},
];

describe('BarChart', () => {
	it('moves focus to the next bar with the arrow keys', () => {
		render(<BarChart data={DATA} title="Monthly visits" />);

		const bars = screen.getAllByRole('img');

		bars[0].focus();

		fireEvent.keyDown(bars[0], {key: 'ArrowRight'});

		expect(bars[1]).toHaveFocus();
	});

	it('renders one accessible bar per datum', () => {
		render(<BarChart data={DATA} title="Monthly visits" />);

		const bars = screen.getAllByRole('img');

		expect(bars).toHaveLength(DATA.length);
		expect(bars[0]).toHaveAttribute('aria-label', 'Jan: 12');
		expect(bars[1]).toHaveAttribute('aria-label', 'Feb: 18');
	});

	it('exposes the title as the chart accessible name', () => {
		render(<BarChart data={DATA} title="Monthly visits" />);

		expect(screen.getByRole('figure')).toHaveAccessibleName(
			'Monthly visits'
		);
	});

	it('applies the orientation and size modifiers', () => {
		render(
			<BarChart
				data={DATA}
				orientation="horizontal"
				size="inline"
				title="Monthly visits"
			/>
		);

		const figure = screen.getByRole('figure');

		expect(figure).toHaveClass('charts-bar-chart--horizontal');
		expect(figure).toHaveClass('charts-bar-chart--size-inline');
	});

	it('omits the motion modifier when animated is false', () => {
		render(
			<BarChart animated={false} data={DATA} title="Monthly visits" />
		);

		expect(screen.getByRole('figure')).not.toHaveClass(
			'charts-bar-chart--motion'
		);
	});

	it('assigns a per-bar fill in the categorical scheme', () => {
		render(
			<BarChart data={DATA} scheme="categorical" title="Monthly visits" />
		);

		expect(screen.getAllByRole('img')[0]).toHaveStyle({
			'--charts-bar-fill': 'var(--chart-color-1)',
		});
	});

	it('renders a semantic detail table for legend="table"', () => {
		render(<BarChart data={DATA} legend="table" title="Monthly visits" />);

		expect(screen.getAllByRole('columnheader')).toHaveLength(5);
		expect(screen.getByRole('table')).toBeInTheDocument();

		// One row header (scope="row") per datum.

		expect(
			screen.getByRole('rowheader', {name: 'Jan'})
		).toBeInTheDocument();
	});

	it('lays the stacked meter out as one accessible segment per datum', () => {
		render(<BarChart data={DATA} stacked title="Monthly visits" />);

		const segments = screen.getAllByRole('img');

		expect(segments).toHaveLength(DATA.length);
		expect(segments[0]).toHaveAttribute('aria-label', 'Jan: 12');
	});

	it('forces the categorical scheme and drops the axis when stacked', () => {
		const {container} = render(
			<BarChart
				data={DATA}
				scheme="blue"
				stacked
				title="Monthly visits"
			/>
		);

		const figure = screen.getByRole('figure');

		expect(figure).toHaveClass('charts-bar-chart--stacked');
		expect(figure).toHaveClass('charts-bar-chart--categorical');
		expect(
			container.querySelector('.charts-bar-chart__axis')
		).not.toBeInTheDocument();
	});

	it('shows the datum share in the list legend by default', () => {
		render(<BarChart data={DATA} legend="list" title="Monthly visits" />);

		// 12 of 39 total.

		expect(screen.getByText('30.8%')).toBeInTheDocument();
	});

	it('shows the raw value in the list legend for legendValue="value"', () => {
		render(
			<BarChart
				data={DATA}
				legend="list"
				legendValue="value"
				title="Monthly visits"
			/>
		);

		const legend = screen.getByRole('list', {hidden: true});

		expect(within(legend).getByText('12')).toBeInTheDocument();
		expect(within(legend).queryByText('30.8%')).not.toBeInTheDocument();
	});

	it('shows no trailing metric in the list legend for legendValue="name"', () => {
		render(
			<BarChart
				data={DATA}
				legend="list"
				legendValue="name"
				title="Monthly visits"
			/>
		);

		const legend = screen.getByRole('list', {hidden: true});

		expect(within(legend).getByText('Jan')).toBeInTheDocument();
		expect(within(legend).queryByText('12')).not.toBeInTheDocument();
		expect(within(legend).queryByText('30.8%')).not.toBeInTheDocument();
	});

	it('applies the alignment and swatch-border modifiers', () => {
		render(
			<BarChart
				alignment="center"
				data={DATA}
				legend="list"
				legendSwatchBorder={false}
				title="Monthly visits"
			/>
		);

		const figure = screen.getByRole('figure');

		expect(figure).toHaveClass('charts-bar-chart--align-center');
		expect(figure).toHaveClass('charts-bar-chart--no-swatch-border');
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<BarChart data={DATA} legend="list" title="Monthly visits" />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('has no accessibility violations when stacked', async () => {
		const {container} = render(
			<BarChart
				data={DATA}
				legend="list"
				stacked
				title="Monthly visits"
			/>
		);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('wraps a horizontal label onto two lines when the band has room', () => {
		const data = [
			{label: 'Knowledge Base', value: 12},
			{label: 'Feb', value: 18},
			{label: 'Mar', value: 9},
			{label: 'Apr', value: 21},
			{label: 'May', value: 7},
		];

		const {container} = render(
			<BarChart data={data} orientation="horizontal" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;
		const tspans = label.querySelectorAll('tspan');

		expect(tspans).toHaveLength(2);
		expect(
			Array.from(tspans)
				.map((tspan) => tspan.textContent)
				.join(' ')
		).toBe('Knowledge Base');
		expect(label.textContent).not.toContain('…');
	});

	it('truncates a horizontal label to one line with an ellipsis when bands are crowded', () => {
		const data = Array.from({length: 20}, (_, index) => ({
			label:
				index === 0
					? 'A Very Long Label That Cannot Fit The Gutter'
					: `Item ${index}`,
			value: index + 1,
		}));

		const {container} = render(
			<BarChart data={data} orientation="horizontal" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;
		const tspans = label.querySelectorAll('tspan');

		expect(tspans).toHaveLength(1);
		expect(label.textContent?.endsWith('…')).toBe(true);
	});

	it('truncates a single word longer than the gutter with an ellipsis', () => {
		const data = [
			{label: 'Supercalifragilistic', value: 12},
			{label: 'Feb', value: 18},
		];

		const {container} = render(
			<BarChart data={data} orientation="horizontal" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;

		expect(label.textContent?.endsWith('…')).toBe(true);
	});

	it('truncates a non-last wrapped line whose word alone overflows the gutter', () => {
		const data = [
			{label: 'Administration Console Settings', value: 12},
			{label: 'Feb', value: 18},
			{label: 'Mar', value: 9},
			{label: 'Apr', value: 21},
			{label: 'May', value: 7},
		];

		const {container} = render(
			<BarChart data={data} orientation="horizontal" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;
		const tspans = label.querySelectorAll('tspan');

		expect(tspans[0].textContent?.endsWith('…')).toBe(true);
		expect(tspans[0].textContent).not.toBe('Administration');
	});

	it('does not double-ellipsize when the last wrapped line is an already-truncated overflowing word', () => {
		const data = [
			{label: 'Go Supercalifragilistic Dog Cat', value: 12},
			{label: 'Feb', value: 18},
			{label: 'Mar', value: 9},
			{label: 'Apr', value: 21},
		];

		const {container} = render(
			<BarChart data={data} orientation="horizontal" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;
		const tspans = label.querySelectorAll('tspan');

		expect(tspans).toHaveLength(2);
		expect(tspans[1].textContent?.endsWith('…')).toBe(true);
		expect(tspans[1].textContent).not.toContain('……');
	});

	it('hides the truncated label text from assistive technology', async () => {
		const data = [
			{label: 'A Very Long Label That Cannot Fit The Gutter', value: 12},
			{label: 'Feb', value: 18},
			{label: 'Mar', value: 9},
		];

		const {container} = render(
			<BarChart data={data} orientation="horizontal" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;

		expect(label).toHaveAttribute('aria-hidden', 'true');
		expect(label.querySelector('title')?.textContent).toBe(
			'A Very Long Label That Cannot Fit The Gutter'
		);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('renders a single empty tspan for a horizontal label with no text', () => {
		const data = [
			{label: '', value: 12},
			{label: 'Feb', value: 18},
			{label: 'Mar', value: 9},
		];

		const {container} = render(
			<BarChart data={data} orientation="horizontal" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;
		const tspans = label.querySelectorAll('tspan');

		expect(tspans).toHaveLength(1);
		expect(tspans[0].textContent).toBe('');
	});

	it('renders a single tspan for a horizontal label with only whitespace', () => {
		const data = [
			{label: '   ', value: 12},
			{label: 'Feb', value: 18},
			{label: 'Mar', value: 9},
		];

		const {container} = render(
			<BarChart data={data} orientation="horizontal" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;
		const tspans = label.querySelectorAll('tspan');

		expect(tspans).toHaveLength(1);
		expect(tspans[0].textContent).toBe('   ');
	});

	it('keeps a single label line for vertical orientation', () => {
		const {container} = render(
			<BarChart data={DATA} orientation="vertical" title="Visits" />
		);

		const label = container.querySelector(
			'.charts-bar-chart__label'
		) as SVGTextElement;

		expect(label.querySelectorAll('tspan')).toHaveLength(1);
	});
});
