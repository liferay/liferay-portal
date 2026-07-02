/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import PieChartSummary from '../../../src/main/resources/META-INF/resources/js/pie_chart/components/PieChartSummary';

const DATA = [
	{label: 'Alpha', value: 1},
	{label: 'Beta', value: 3},
];

const DEFAULT_PROPS = {
	data: DATA,
	description: undefined,
	id: 'summary-id',
	total: 4,
};

describe('PieChartSummary', () => {
	it('renders the per-item summary text for each datum', () => {
		render(<PieChartSummary {...DEFAULT_PROPS} />);

		expect(
			screen.getByText(/1 of 2, Alpha: 1 \(25\.0%\)\./)
		).toBeInTheDocument();
		expect(
			screen.getByText(/2 of 2, Beta: 3 \(75\.0%\)\./)
		).toBeInTheDocument();
	});

	it('includes the description prefix when provided', () => {
		render(
			<PieChartSummary {...DEFAULT_PROPS} description="Sales by region" />
		);

		expect(screen.getByText(/^Sales by region/)).toBeInTheDocument();
	});

	it('omits the description prefix when not provided', () => {
		const {container} = render(<PieChartSummary {...DEFAULT_PROPS} />);

		expect(container.textContent).not.toMatch(/^\s*undefined/);
		expect(container.textContent?.trim().startsWith('1 of 2')).toBe(true);
	});

	it('uses the correct id and className for aria-describedby wiring', () => {
		const {container} = render(<PieChartSummary {...DEFAULT_PROPS} />);

		const summary = container.querySelector('#summary-id');

		expect(summary).toHaveClass('chart-pie-summary', 'sr-only');
	});
});
