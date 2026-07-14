/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/FilterDropdown';
import {
	InventoryContext,
	State,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/inventory/InventoryContext';
import {InventoryAnalysisCard} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/inventory/components/InventoryAnalysisCard';

const mockContextValue: State = {
	changeLanguage: jest.fn(),
	changeSpace: jest.fn(),
	constants: {},
	filters: {
		language: {label: 'English', value: 'en_US'} as Item,
		space: {label: 'Global', value: '1'} as Item,
	},
};

const mockData = {
	data: {
		inventoryAnalysisItems: [
			{count: 5, key: 'articles', title: 'Articles'},
			{count: 2, key: 'blogs', title: 'Blog Posts'},
		],
		totalCount: 7,
	},
	error: null,
};

const mockApiHelperGet = (inventoryAnalysisData: any) =>
	jest
		.spyOn(ApiHelper, 'get')
		.mockImplementation((url: string) =>
			url.includes('inventory-analysis')
				? Promise.resolve(inventoryAnalysisData)
				: Promise.resolve({data: {items: []}, error: null})
		);

describe('[CMS Dashboard] InventoryAnalysisCard', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders inventory analysis data inside a PaginatedTable', async () => {
		mockApiHelperGet(mockData);

		render(
			<InventoryContext.Provider value={mockContextValue}>
				<InventoryAnalysisCard />
			</InventoryContext.Provider>
		);

		await waitFor(() =>
			expect(screen.getByText('Articles')).toBeInTheDocument()
		);

		expect(screen.getByText('Blog Posts')).toBeInTheDocument();

		const totals = screen.getAllByText(/7/);
		expect(totals.length).toBeGreaterThan(0);
	});

	it('allows user to switch preferences between chart and table', async () => {
		mockApiHelperGet(mockData);

		render(
			<InventoryContext.Provider value={mockContextValue}>
				<InventoryAnalysisCard />
			</InventoryContext.Provider>
		);

		await waitFor(() =>
			expect(screen.getByText('Articles')).toBeInTheDocument()
		);

		const preferencesButton = screen.getByRole('combobox', {
			name: 'chart[noun]',
		});

		fireEvent.click(preferencesButton);

		const tableOption = await screen.findByRole('option', {name: 'table'});
		fireEvent.click(tableOption);

		expect(
			screen.getByRole('combobox', {name: 'table'})
		).toBeInTheDocument();
	});

	it('renders filters (group by and filter by) when data is available', async () => {
		mockApiHelperGet(mockData);

		render(
			<InventoryContext.Provider value={mockContextValue}>
				<InventoryAnalysisCard />
			</InventoryContext.Provider>
		);

		await waitFor(() =>
			expect(screen.getByText('Articles')).toBeInTheDocument()
		);

		expect(screen.getByText('group-by')).toBeInTheDocument();
		expect(screen.getByText('filter-by')).toBeInTheDocument();
	});

	it('renders EmptyStateCard when no inventory analysis data is available', async () => {
		mockApiHelperGet({
			data: {
				inventoryAnalysisItems: [],
				totalCount: 0,
			},
			error: null,
		});

		render(
			<InventoryContext.Provider value={mockContextValue}>
				<InventoryAnalysisCard />
			</InventoryContext.Provider>
		);

		await waitFor(() =>
			expect(screen.getByText('no-assets-yet')).toBeInTheDocument()
		);

		expect(
			screen.getByText('there-are-no-assets-created-in-the-spaces')
		).toBeInTheDocument();

		const images = screen.getAllByRole('presentation');
		const emptyStateImage = images.find((image) =>
			image.getAttribute('src')?.includes('cms_empty_state.svg')
		);

		expect(emptyStateImage).toBeInTheDocument();
	});
});
