/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {InventoryAnalysisDataType} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/InventoryAnalysisCard';
import PaginatedTable from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/PaginatedTable';

const mockData: InventoryAnalysisDataType = {
	inventoryAnalysisItems: [
		{
			count: 100,
			key: 'title-1',
			title: 'title 1',
		},
		{
			count: 300,
			key: 'title-2',
			title: 'title 2',
		},
		{
			count: 50,
			key: 'title-3',
			title: 'title 3',
		},
		{
			count: 200,
			key: 'title-4',
			title: 'title 4',
		},
		{
			count: 150,
			key: 'title-5',
			title: 'title 5',
		},
		{
			count: 400,
			key: 'title-6',
			title: 'title 6',
		},
		{
			count: 250,
			key: 'title-7',
			title: 'title 7',
		},
		{
			count: 350,
			key: 'title-8',
			title: 'title 8',
		},
		{
			count: 500,
			key: 'title-9',
			title: 'title 9',
		},
		{
			count: 600,
			key: 'title-10',
			title: 'title 10',
		},
		{
			count: 700,
			key: 'title-11',
			title: 'title 11',
		},
		{
			count: 100,
			key: 'title-12',
			title: 'title 12',
		},
		{
			count: 300,
			key: 'title-13',
			title: 'title 13',
		},
		{
			count: 50,
			key: 'title-14',
			title: 'title 14',
		},
		{
			count: 200,
			key: 'title-15',
			title: 'title 15',
		},
		{
			count: 150,
			key: 'title-16',
			title: 'title 16',
		},
		{
			count: 400,
			key: 'title-17',
			title: 'title 17',
		},
		{
			count: 250,
			key: 'title-18',
			title: 'title 18',
		},
		{
			count: 350,
			key: 'title-19',
			title: 'title 19',
		},
		{
			count: 500,
			key: 'title-20',
			title: 'title 20',
		},
		{
			count: 600,
			key: 'title-21',
			title: 'title 21',
		},
		{
			count: 700,
			key: 'title-22',
			title: 'title 22',
		},
	],
	page: 1,
	pageSize: 20,
	totalCount: 4050,
};

const deltas = [{label: 20}, {label: 40}, {label: 60}];

const WrappedComponent = ({
	currentStructureTypeLabel,
	inventoryAnalysisData,
}: {
	currentStructureTypeLabel: string;
	inventoryAnalysisData: InventoryAnalysisDataType;
	viewType: 'chart' | 'table';
}) => (
	<PaginatedTable
		currentStructureTypeLabel={currentStructureTypeLabel}
		deltas={deltas}
		handleDeltaChange={() => {}}
		handlePageChange={() => {}}
		inventoryAnalysisData={inventoryAnalysisData}
		pagination={{page: 1, pageSize: 20}}
		viewType="chart"
	/>
);

describe('[CMS Dashboard] Components: PaginatedTable', () => {
	it('renders its data correctly in charts view', async () => {
		render(
			<WrappedComponent
				currentStructureTypeLabel="Category"
				inventoryAnalysisData={mockData}
				viewType="chart"
			/>
		);

		const table = screen.getByRole('table');
		expect(table).toBeInTheDocument();

		const tableRows = table.querySelectorAll('tr');

		expect(tableRows.length).toBe(23);
	});

	it('renders its data correctly in table view', async () => {
		render(
			<WrappedComponent
				currentStructureTypeLabel="Category"
				inventoryAnalysisData={mockData}
				viewType="table"
			/>
		);

		const table = screen.getByRole('table');
		expect(table).toBeInTheDocument();

		const tableRows = table.querySelectorAll('tr');
		expect(tableRows.length).toBe(23);
	});

	it('displays the default delta options in the items per page dropdown', async () => {
		render(
			<WrappedComponent
				currentStructureTypeLabel="Category"
				inventoryAnalysisData={mockData}
				viewType="chart"
			/>
		);

		const itemsPerPageDropdown = screen.getByRole('combobox', {
			name: 'Items Per Page',
		});

		await itemsPerPageDropdown.click();

		const expectedOptions = ['20', '40', '60'];
		const dropdownOptions = screen.getAllByRole('option');

		expect(dropdownOptions).toHaveLength(expectedOptions.length);

		dropdownOptions.forEach((option, index) => {
			expect(option).toHaveTextContent(expectedOptions[index]);
		});
	});

	it('paginates items correctly when navigating between pages', async () => {
		const handlePageChange = jest.fn();
		const handleDeltaChange = jest.fn();

		const {rerender} = render(
			<PaginatedTable
				currentStructureTypeLabel="Category"
				deltas={deltas}
				handleDeltaChange={handleDeltaChange}
				handlePageChange={handlePageChange}
				inventoryAnalysisData={{
					...mockData,
					inventoryAnalysisItems:
						mockData.inventoryAnalysisItems.slice(0, 19),
					totalCount: mockData.inventoryAnalysisItems.length,
				}}
				pagination={{page: 1, pageSize: 20}}
				viewType="chart"
			/>
		);

		let tableRows = screen.getAllByRole('row');

		expect(tableRows.length).toBe(20);

		const nextPageButton = screen.getByRole('button', {
			name: 'Go to the next page, 2',
		});

		fireEvent.click(nextPageButton);

		expect(handlePageChange).toHaveBeenCalledWith(2);

		rerender(
			<PaginatedTable
				currentStructureTypeLabel="Category"
				deltas={deltas}
				handleDeltaChange={handleDeltaChange}
				handlePageChange={handlePageChange}
				inventoryAnalysisData={{
					...mockData,
					inventoryAnalysisItems:
						mockData.inventoryAnalysisItems.slice(19, 21),
					totalCount: mockData.inventoryAnalysisItems.length,
				}}
				pagination={{page: 2, pageSize: 20}}
				viewType="chart"
			/>
		);

		tableRows = screen.getAllByRole('row');
		expect(tableRows.length).toBe(3);
	});

	it('paginates items according to selected delta', async () => {
		render(
			<WrappedComponent
				currentStructureTypeLabel="Category"
				inventoryAnalysisData={mockData}
				viewType="chart"
			/>
		);

		const itemsPerPageDropdown = screen.getByRole('combobox', {
			name: 'Items Per Page',
		});

		await itemsPerPageDropdown.click();

		const option20Items = screen.getByRole('option', {name: '20 items'});
		await option20Items.click();

		const table = screen.getByRole('table');
		const tableRows = table.querySelectorAll('tr');

		expect(tableRows.length).toBe(23);
	});

	it('displays the total count', async () => {
		render(
			<WrappedComponent
				currentStructureTypeLabel="Category"
				inventoryAnalysisData={mockData}
				viewType="chart"
			/>
		);

		const totalItems = screen.getByText(/Showing \d+ to \d+ of \d+/);

		expect(totalItems).toHaveTextContent('Showing 1 to 20 of 4050');
	});

	it('displays the correct item range per page', async () => {
		render(
			<WrappedComponent
				currentStructureTypeLabel="Category"
				inventoryAnalysisData={mockData}
				viewType="chart"
			/>
		);

		const nextPageButton = screen.getByRole('button', {
			name: 'Go to the next page, 2',
		});

		await nextPageButton.click();

		const paginationResults = screen.getByText(/Showing \d+ to \d+ of \d+/);

		expect(paginationResults).toHaveTextContent('Showing 1 to 20 of 4050');
	});

	it('displays the name, count, and assets percentage for each item', async () => {
		render(
			<WrappedComponent
				currentStructureTypeLabel="Category"
				inventoryAnalysisData={mockData}
				viewType="chart"
			/>
		);

		const tableRows = screen.getAllByRole('row');

		tableRows.slice(1).forEach((row, index) => {
			const cells = row.querySelectorAll('td');

			expect(cells[0]).toHaveTextContent(
				mockData.inventoryAnalysisItems[index].title
			);

			expect(cells[1]).toHaveTextContent(
				mockData.inventoryAnalysisItems[index].count.toString()
			);

			const percentage = (
				(mockData.inventoryAnalysisItems[index].count /
					mockData.totalCount) *
				100
			).toFixed(2);

			expect(cells[2]).toHaveTextContent(`${percentage}%`);
		});
	});
});
