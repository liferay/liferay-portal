/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import ChartSummary from '../../src/main/resources/META-INF/resources/js/chart_summary/ChartSummary';

const ITEMS = [
	{label: 'Alpha', value: 1},
	{label: 'Beta', value: 3},
];

const DEFAULT_PROPS = {
	id: 'summary-id',
	items: ITEMS,
	total: 4,
};

describe('ChartSummary', () => {
	it('renders the per-item summary text for each item', () => {
		render(<ChartSummary {...DEFAULT_PROPS} />);

		expect(screen.getByText(/Alpha: 1 \(25\.0%\)\./)).toBeInTheDocument();
		expect(screen.getByText(/Beta: 3 \(75\.0%\)\./)).toBeInTheDocument();
	});

	it('prefixes each item with its position when showPosition is set', () => {
		render(<ChartSummary {...DEFAULT_PROPS} showPosition />);

		expect(
			screen.getByText(/1 of 2, Alpha: 1 \(25\.0%\)\./)
		).toBeInTheDocument();
		expect(
			screen.getByText(/2 of 2, Beta: 3 \(75\.0%\)\./)
		).toBeInTheDocument();
	});

	it('includes the description prefix when provided', () => {
		render(
			<ChartSummary {...DEFAULT_PROPS} description="Sales by region" />
		);

		expect(screen.getByText(/^Sales by region/)).toBeInTheDocument();
	});

	it('omits the description prefix when not provided', () => {
		const {container} = render(<ChartSummary {...DEFAULT_PROPS} />);

		expect(container.textContent).not.toMatch(/^\s*undefined/);
		expect(container.textContent?.trim().startsWith('Alpha')).toBe(true);
	});

	it('uses the given id and the shared class for aria-describedby wiring', () => {
		const {container} = render(<ChartSummary {...DEFAULT_PROPS} />);

		const summary = container.querySelector('#summary-id');

		expect(summary).toHaveClass('charts-summary', 'sr-only');
	});
});
