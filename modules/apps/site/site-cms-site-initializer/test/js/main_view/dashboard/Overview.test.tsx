/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {TrendClassification} from '@liferay/analytics-reports-js-components-web';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {PerformanceContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceContext';
import PerformanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceService';
import {Overview} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/components/Overview';
import {OverviewMetrics} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/types';

const trend = {classification: TrendClassification.Positive, percentage: 22.5};

const mockedMetrics: OverviewMetrics = {
	downloadsMetric: {
		metricType: 'downloadsMetric',
		previousValue: 0,
		trend,
		value: 5900,
	},
	impressionsMetric: {
		metricType: 'impressionsMetric',
		previousValue: 0,
		trend,
		value: 31900,
	},
	readsMetric: {
		metricType: 'readsMetric',
		previousValue: 0,
		trend,
		value: 5260,
	},
	viewsMetric: {
		metricType: 'viewsMetric',
		previousValue: 0,
		trend,
		value: 18100,
	},
};

const renderComponent = () =>
	render(
		<PerformanceContextProvider>
			<Overview />
		</PerformanceContextProvider>
	);

describe('Overview', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the four metric cards with their fetched values', async () => {
		jest.spyOn(PerformanceService, 'getOverviewMetrics').mockResolvedValue({
			data: mockedMetrics,
			error: null,
		});

		renderComponent();

		expect(await screen.findByText('31.9K')).toBeInTheDocument();
		expect(screen.getByText('impressions')).toBeInTheDocument();
		expect(screen.getByText('views')).toBeInTheDocument();
		expect(screen.getByText('downloads')).toBeInTheDocument();
		expect(screen.getByText('reads-metric')).toBeInTheDocument();
	});
});
