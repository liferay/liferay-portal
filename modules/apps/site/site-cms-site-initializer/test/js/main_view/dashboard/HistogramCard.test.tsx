/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {PerformanceContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceContext';
import PerformanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceService';
import {HistogramCard} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/components/HistogramCard';

const renderComponent = () =>
	render(
		<PerformanceContextProvider>
			<HistogramCard metricType="viewsMetric" title="Views" />
		</PerformanceContextProvider>
	);

describe('HistogramCard', () => {
	const {ResizeObserver} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserver;
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the current and previous period lines from the fetched histogram', async () => {
		jest.spyOn(PerformanceService, 'getHistogramMetric').mockResolvedValue({
			data: {
				histograms: [
					{
						metricName: 'viewsMetric',
						metrics: [
							{
								previousValue: 10,
								previousValueKey: '2026-07-14T00:00',
								value: 20,
								valueKey: '2026-07-21T00:00',
							},
							{
								previousValue: 15,
								previousValueKey: '2026-07-15T00:00',
								value: 25,
								valueKey: '2026-07-22T00:00',
							},
						],
						total: 45,
						totalValue: 45,
					},
				],
			},
			error: null,
		});

		renderComponent();

		expect(await screen.findByText('current-period')).toBeInTheDocument();
		expect(screen.getByText('previous-period')).toBeInTheDocument();
	});

	it('renders a hollow chart when there are no histograms', async () => {
		jest.spyOn(PerformanceService, 'getHistogramMetric').mockResolvedValue({
			data: {histograms: []},
			error: null,
		});

		const {container} = renderComponent();

		expect(await screen.findByText('current-period')).toBeInTheDocument();
		expect(screen.getByText('previous-period')).toBeInTheDocument();
		expect(screen.getByText('there-is-no-data')).toBeInTheDocument();

		// The gridlines stay, with no line, no date and no value to read.

		expect(
			container.querySelectorAll('.charts-line-chart__gridline').length
		).toBeGreaterThan(0);
		expect(
			container.querySelectorAll('.charts-line-chart__line')
		).toHaveLength(0);
		expect(
			container.querySelectorAll('.charts-line-chart__category-label')
		).toHaveLength(0);

		for (const tick of container.querySelectorAll(
			'.charts-line-chart__tick-label'
		)) {
			expect(tick).toHaveTextContent('');
		}
	});
});
