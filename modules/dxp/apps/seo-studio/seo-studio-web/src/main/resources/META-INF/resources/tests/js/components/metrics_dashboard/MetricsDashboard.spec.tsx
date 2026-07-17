/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import MetricsDashboard from '../../../../js/components/metrics_dashboard/MetricsDashboard';
import {Metrics} from '../../../../js/components/metrics_dashboard/types';

const METRICS: Metrics = {
	affectedPagesCount: 8,
	averageInsightsPerAffectedPage: 2.5,
	categoryBreakdown: {contentStructure: 3, images: 12, metadata: 5},
	criticalInsights: 4,
	impactMix: {
		contentStructure: {1: 1, 2: 1, 3: 1},
		images: {1: 3, 2: 4, 3: 5},
		metadata: {1: 2, 3: 3},
	},
	totalInsights: 20,
};

type DashboardProps = React.ComponentProps<typeof MetricsDashboard>;

function renderDashboard(props: Partial<DashboardProps> = {}) {
	return render(
		<MetricsDashboard metrics={METRICS} scope="on-page" {...props} />
	);
}

describe('MetricsDashboard', () => {
	it('renders the KPI values from the metrics', () => {
		renderDashboard();

		expect(screen.getAllByText('20').length).toBeGreaterThan(0);
		expect(screen.getByText('4')).toBeInTheDocument();
		expect(screen.getByText('8')).toBeInTheDocument();
		expect(screen.getByText('2.5')).toBeInTheDocument();
	});

	it('renders the KPI labels', () => {
		renderDashboard();

		expect(screen.getByText('x-insights')).toBeInTheDocument();
		expect(screen.getByText('pages-affected')).toBeInTheDocument();
		expect(
			screen.getByText('average-insights-per-page')
		).toBeInTheDocument();
		expect(screen.getByText('high-impact-insights')).toBeInTheDocument();
	});

	it('renders the category breakdown and impact mix panels', () => {
		renderDashboard();

		expect(screen.getByText('insights-by-category')).toBeInTheDocument();
		expect(screen.getByText('impact-mix-per-category')).toBeInTheDocument();
	});

	it('renders no-data placeholders when there are no metrics', () => {
		renderDashboard({metrics: null});

		expect(screen.getByText('x-insights')).toBeInTheDocument();
		expect(screen.getByText('insights-by-category')).toBeInTheDocument();

		expect(screen.getAllByText('no-data-available')).toHaveLength(4);
		expect(screen.getAllByText('no-data-available-yet')).toHaveLength(2);
	});
});
