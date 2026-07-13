/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {PerformanceContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceContext';
import PerformanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceService';
import {AudienceAndDistribution} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/components/AudienceAndDistribution';

const renderComponent = () =>
	render(
		<PerformanceContextProvider>
			<AudienceAndDistribution />
		</PerformanceContextProvider>
	);

describe('AudienceAndDistribution', () => {
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

	it('renders the categorization slices from the fetched metric', async () => {
		jest.spyOn(PerformanceService, 'getMetric').mockImplementation(
			async ({groupBy}) => ({
				data: {
					metricType: 'viewsMetric',
					metrics:
						groupBy === 'location'
							? [{previousValue: 130, value: 162, valueKey: 'US'}]
							: [
									{
										previousValue: 70000,
										value: 83300,
										valueKey: 'Sales',
									},
									{
										previousValue: 15000,
										value: 17500,
										valueKey: 'Business',
									},
								],
				},
				error: null,
			})
		);

		renderComponent();

		expect(await screen.findByText('Sales')).toBeInTheDocument();
		expect(screen.getByText('Business')).toBeInTheDocument();
	});

	it('renders the empty state when the metrics are empty', async () => {
		jest.spyOn(PerformanceService, 'getMetric').mockResolvedValue({
			data: {metricType: 'viewsMetric', metrics: []},
			error: null,
		});

		renderComponent();

		expect((await screen.findAllByText('no-data-available')).length).toBe(
			2
		);
	});
});
