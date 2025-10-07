/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {RangeSelectors} from '../../../js/components/RangeSelectorsDropdown';
import {Histogram} from '../../../js/components/cms/asset-metrics/AssetMetrics';
import {AssetMetricsTableView} from '../../../js/components/cms/asset-metrics/AssetMetricsTableView';
import {MetricName, MetricType} from '../../../js/types/global';

const mockedChartData = {
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
		{
			metricName: MetricName.Downloads,
			metrics: [
				{
					previousValue: 171,
					previousValueKey: '2025-07-19T17:00',
					value: 37,
					valueKey: '2025-07-20T17:00',
				},
				{
					previousValue: 29,
					previousValueKey: '2025-07-19T18:00',
					value: 965,
					valueKey: '2025-07-20T18:00',
				},
				{
					previousValue: 24,
					previousValueKey: '2025-07-19T19:00',
					value: 500,
					valueKey: '2025-07-20T19:00',
				},
			],
			total: 1231,
			totalValue: 3000,
		},
		{
			metricName: MetricName.Views,
			metrics: [
				{
					previousValue: 103,
					previousValueKey: '2025-07-19T17:00',
					value: 48,
					valueKey: '2025-07-20T17:00',
				},
				{
					previousValue: 25,
					previousValueKey: '2025-07-19T18:00',
					value: 566,
					valueKey: '2025-07-20T18:00',
				},
				{
					previousValue: 382,
					previousValueKey: '2025-07-19T19:00',
					value: 684,
					valueKey: '2025-07-20T19:00',
				},
			],
			total: 1231,
			totalValue: 3000,
		},
	],
};

interface IWrapperComponent {
	metricName: MetricName;
	metricType: MetricType;
	title: string;
}

const WrapperComponent: React.FC<IWrapperComponent> = ({
	metricName,
	metricType,
	title,
}) => {
	const histogram = mockedChartData.histograms.find(
		({metricName: currentMetricName}) => currentMetricName === metricName
	);

	return (
		<AssetMetricsTableView
			histogram={histogram as Histogram}
			metricName={metricName}
			metricType={metricType}
			rangeSelector={RangeSelectors.Last30Days}
			title={title}
		/>
	);
};

describe('TableView with different metrics', () => {
	it('renders table correctly with metric "Views" and displays data', async () => {
		render(
			<WrapperComponent
				metricName={MetricName.Views}
				metricType={MetricType.Views}
				title="views"
			/>
		);

		expect(screen.getByText('views')).toBeInTheDocument();

		const rows = screen.getAllByRole('row');
		expect(rows.length).toBeGreaterThan(1);

		const firstDataRow = rows[1] as HTMLTableRowElement;

		const dateCellText = firstDataRow.cells[0].textContent || '';
		expect(dateCellText).toMatch(
			/(\d{2}\/\d{2}\/\d{4})|(\d{4}-\d{2}-\d{2})|([A-Za-z]{3} \d{1,2})/
		);

		const valueCellText = firstDataRow.cells[1].textContent || '';
		const previousCellText = firstDataRow.cells[2].textContent || '';

		expect(valueCellText).toMatch(/[\d,.]+/);
		expect(previousCellText).toMatch(/[\d,.]+/);
	});

	it('renders table correctly with metric "Impressions" and displays data', () => {
		render(
			<WrapperComponent
				metricName={MetricName.Impressions}
				metricType={MetricType.Impressions}
				title="impressions"
			/>
		);

		expect(screen.getByText(/impressions/i)).toBeInTheDocument();

		const rows = screen.getAllByRole('row');
		expect(rows.length).toBeGreaterThan(1);

		const firstDataRow = rows[1] as HTMLTableRowElement;

		const dateCellText = firstDataRow.cells[0].textContent || '';
		expect(dateCellText).toMatch(
			/(\d{2}\/\d{2}\/\d{4})|(\d{4}-\d{2}-\d{2})|([A-Za-z]{3} \d{1,2})/
		);

		const valueCellText = firstDataRow.cells[1].textContent || '';
		const previousCellText = firstDataRow.cells[2].textContent || '';

		expect(valueCellText).toMatch(/[\d,.]+/);
		expect(previousCellText).toMatch(/[\d,.]+/);
	});

	it('renders table correctly with metric "Downloads" and displays data', () => {
		render(
			<WrapperComponent
				metricName={MetricName.Downloads}
				metricType={MetricType.Downloads}
				title="downloads"
			/>
		);

		expect(screen.getByText(/downloads/i)).toBeInTheDocument();

		const rows = screen.getAllByRole('row');
		expect(rows.length).toBeGreaterThan(1);

		const firstDataRow = rows[1] as HTMLTableRowElement;

		const dateCellText = firstDataRow.cells[0].textContent || '';
		expect(dateCellText).toMatch(
			/(\d{2}\/\d{2}\/\d{4})|(\d{4}-\d{2}-\d{2})|([A-Za-z]{3} \d{1,2})/
		);

		const valueCellText = firstDataRow.cells[1].textContent || '';
		const previousCellText = firstDataRow.cells[2].textContent || '';

		expect(valueCellText).toMatch(/[\d,.]+/);
		expect(previousCellText).toMatch(/[\d,.]+/);
	});

	it('renders pagination component', () => {
		render(
			<WrapperComponent
				metricName={MetricName.Views}
				metricType={MetricType.Views}
				title="views"
			/>
		);

		const paginationButton = screen.getByLabelText('Go to page, 1');

		expect(paginationButton).toBeInTheDocument();
	});

	it('changes number of items displayed when selecting items per page', async () => {
		render(
			<WrapperComponent
				metricName={MetricName.Views}
				metricType={MetricType.Views}
				title="views"
			/>
		);

		const itemsPerPageButton = screen.getByRole('button', {
			name: /items per page/i,
		});
		expect(itemsPerPageButton).toBeInTheDocument();

		await userEvent.click(itemsPerPageButton);

		const menu = await screen.findByRole('menu');

		const option20 = within(menu).getByText(/20 items/i, {
			selector: 'button',
		});
		expect(option20).toBeInTheDocument();

		await userEvent.click(option20);

		const rows = screen.getAllByRole('row');
		expect(rows.length).toBeLessThanOrEqual(21);
	});
});
