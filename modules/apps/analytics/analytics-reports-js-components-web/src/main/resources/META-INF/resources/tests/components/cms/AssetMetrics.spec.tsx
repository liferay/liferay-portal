/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React, {useContext, useEffect} from 'react';

import {Context, ContextProvider} from '../../../js/Context';
import ApiHelper from '../../../js/apis/ApiHelper';
import {AssetMetrics} from '../../../js/components/cms/asset-metrics/AssetMetrics';
import {MetricName, MetricType} from '../../../js/types/global';

const Component = () => {
	const {changeMetricFilter, filters} = useContext(Context);

	useEffect(() => {
		if (filters.metric === MetricType.Undefined) {
			changeMetricFilter(MetricType.Impressions);
		}
	}, [changeMetricFilter, filters.metric]);

	return <AssetMetrics />;
};

const WrapperComponent = () => (
	<ContextProvider customState={{}}>
		<Component />
	</ContextProvider>
);

describe('AssetMetrics', () => {
	const {ResizeObserver} = window;

	beforeAll(() => {

		// @ts-ignore

		delete window.ResizeObserver;

		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	afterAll(() => {
		cleanup();

		window.ResizeObserver = ResizeObserver;

		jest.restoreAllMocks();
	});

	it('renders table view', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				histograms: [
					{
						metricName: MetricName.Impressions,
						metrics: [
							{
								previousValue: 8360000,
								previousValueKey: '2025-07-19T17:00',
								value: 9700000,
								valueKey: '2025-07-20T17:00',
							},
							{
								previousValue: 6880000,
								previousValueKey: '2025-07-19T18:00',
								value: 150000,
								valueKey: '2025-07-20T18:00',
							},
							{
								previousValue: 1160000,
								previousValueKey: '2025-07-19T19:00',
								value: 3180000,
								valueKey: '2025-07-20T19:00',
							},
						],
						total: 1231,
						totalValue: 3000,
					},
				],
			},
			error: null,
		});

		render(<WrapperComponent />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		fireEvent.click(screen.getByTestId('btn-change-view'));

		fireEvent.click(screen.getByText('table'));

		expect(screen.getByTestId('asset-metric-table')).toBeInTheDocument();
	});

	it('renders chart view', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				histograms: [
					{
						metricName: MetricName.Impressions,
						metrics: [
							{
								previousValue: 8360000,
								previousValueKey: '2025-07-19T17:00',
								value: 9700000,
								valueKey: '2025-07-20T17:00',
							},
							{
								previousValue: 6880000,
								previousValueKey: '2025-07-19T18:00',
								value: 150000,
								valueKey: '2025-07-20T18:00',
							},
							{
								previousValue: 1160000,
								previousValueKey: '2025-07-19T19:00',
								value: 3180000,
								valueKey: '2025-07-20T19:00',
							},
						],
						total: 1231,
						totalValue: 3000,
					},
				],
			},
			error: null,
		});

		render(<WrapperComponent />);

		expect(
			screen.getByText(
				'this-metric-calculates-the-total-number-of-times-an-asset-is-seen-by-visitors'
			)
		).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getByTestId('asset-metric-chart')).toBeInTheDocument();
	});

	it('renders chart view if there is no data', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				histograms: [
					{
						metricName: MetricName.Impressions,
						metrics: [],
						total: 0,
						totalValue: 0,
					},
				],
			},
			error: null,
		});

		render(<WrapperComponent />);

		expect(
			screen.getByText(
				'this-metric-calculates-the-total-number-of-times-an-asset-is-seen-by-visitors'
			)
		).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getByTestId('asset-metric-chart')).toBeInTheDocument();
	});

	it('renders empty state when select the table view and there is no data', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				histograms: [
					{
						metricName: MetricName.Impressions,
						metrics: [],
						total: 0,
						totalValue: 0,
					},
				],
			},
			error: null,
		});

		render(<WrapperComponent />);

		expect(
			screen.getByText(
				'this-metric-calculates-the-total-number-of-times-an-asset-is-seen-by-visitors'
			)
		).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		fireEvent.click(screen.getByTestId('btn-change-view'));

		fireEvent.click(screen.getByText('table'));

		expect(screen.getByText('no-data-available-yet')).toBeInTheDocument();

		expect(
			screen.getByText(
				'there-is-no-data-available-for-the-applied-filters-or-from-the-data-source'
			)
		).toBeInTheDocument();

		expect(
			screen.getByText('learn-more-about-asset-performance')
		).toBeInTheDocument();
	});
});
