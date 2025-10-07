/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useContext, useEffect} from 'react';

import {Context, ContextProvider} from '../../js/Context';
import {BaseOverviewMetrics} from '../../js/components/BaseOverviewMetrics';
import {MetricType} from '../../js/types/global';
import {TrendClassification} from '../../js/utils/metrics';

type Metric = {
	metricType: MetricType;
	trend: {
		classification: TrendClassification;
		percentage: number;
	};
	value: number;
};

const data: {
	defaultMetric: Metric;
	selectedMetrics: Metric[];
} = {
	defaultMetric: {
		metricType: MetricType.Impressions,
		trend: {
			classification: TrendClassification.Neutral,
			percentage: 10,
		},
		value: 1000,
	},
	selectedMetrics: [
		{
			metricType: MetricType.Impressions,
			trend: {
				classification: TrendClassification.Neutral,
				percentage: 10,
			},
			value: 1000,
		},
		{
			metricType: MetricType.Views,
			trend: {
				classification: TrendClassification.Negative,
				percentage: -50,
			},
			value: 2000,
		},

		{
			metricType: MetricType.Downloads,
			trend: {
				classification: TrendClassification.Positive,
				percentage: 100,
			},
			value: 3000,
		},
	],
};

const BaseOverviewWithData = () => {
	const {changeMetricFilter, filters} = useContext(Context);

	useEffect(() => {
		if (filters.metric === MetricType.Undefined) {
			changeMetricFilter(MetricType.Impressions);
		}
	}, [changeMetricFilter, filters.metric]);

	return <BaseOverviewMetrics data={data} small />;
};

const WrapperComponent = () => {
	return (
		<ContextProvider customState={{}}>
			<BaseOverviewWithData />
		</ContextProvider>
	);
};

describe('CMS Asset Type Info Panel Metrics Component', () => {
	it('renders all cards', () => {
		render(<WrapperComponent />);

		const impressions = screen.getByTestId('overview__impressions-metric');
		const views = screen.getByTestId('overview__views-metric');
		const downloads = screen.getByTestId('overview__downloads-metric');

		expect(impressions).toBeInTheDocument();
		expect(views).toBeInTheDocument();
		expect(downloads).toBeInTheDocument();

		const impressionsTitle = screen.getByTestId(
			'overview__impressions-title'
		);
		const viewsTitle = screen.getByTestId('overview__views-title');
		const downloadsTitle = screen.getByTestId('overview__downloads-title');

		expect(impressionsTitle.textContent).toEqual('IMPRESSIONS');
		expect(viewsTitle.textContent).toEqual('VIEWS');
		expect(downloadsTitle.textContent).toEqual('DOWNLOADS');

		const impressionsPercentageIcon = screen.getByTestId(
			'overview__impressions-percentage'
		);
		const viewsPercentageIcon = screen.getByTestId(
			'overview__views-percentage'
		);
		const downloadsPercentageIcon = screen.getByTestId(
			'overview__downloads-percentage'
		);

		expect(
			impressionsPercentageIcon.querySelector('.text-secondary')
		).toBeTruthy();
		expect(viewsPercentageIcon.querySelector('.text-danger')).toBeTruthy();
		expect(
			downloadsPercentageIcon.querySelector('.text-success')
		).toBeTruthy();

		const impressionsPercentageDescription = screen.getByTestId(
			'overview__impressions-percentage-description'
		);
		const viewsPercentageDescription = screen.getByTestId(
			'overview__views-percentage-description'
		);
		const downloadsPercentageDescription = screen.getByTestId(
			'overview__downloads-percentage-description'
		);

		expect(impressionsPercentageDescription.textContent).toEqual('10%');
		expect(
			impressionsPercentageDescription.querySelector('.text-secondary')
		).toBeTruthy();

		expect(viewsPercentageDescription.textContent).toEqual('50%');
		expect(
			viewsPercentageDescription.querySelector('.text-danger')
		).toBeTruthy();

		expect(downloadsPercentageDescription.textContent).toEqual('100%');
		expect(
			downloadsPercentageDescription.querySelector('.text-success')
		).toBeTruthy();
	});

	it('allows keyboard navigation and selection', async () => {
		render(<WrapperComponent />);

		const impressions = screen.getByTestId('overview__impressions-metric');
		const views = screen.getByTestId('overview__views-metric');
		const downloads = screen.getByTestId('overview__downloads-metric');

		expect(impressions).toHaveAttribute('aria-pressed', 'true');

		await userEvent.tab();
		expect(impressions).toHaveFocus();

		await userEvent.tab();
		expect(views).toHaveFocus();

		await userEvent.tab();
		expect(downloads).toHaveFocus();

		await userEvent.keyboard('{enter}');

		await waitFor(() => {
			expect(impressions).toHaveAttribute('aria-pressed', 'false');
			expect(views).toHaveAttribute('aria-pressed', 'false');
			expect(downloads).toHaveAttribute('aria-pressed', 'true');
		});

		await userEvent.tab({shift: true});

		expect(views).toHaveFocus();

		await userEvent.keyboard(' ');

		await waitFor(() => {
			expect(impressions).toHaveAttribute('aria-pressed', 'false');
			expect(views).toHaveAttribute('aria-pressed', 'true');
			expect(downloads).toHaveAttribute('aria-pressed', 'false');
		});
	});
});
