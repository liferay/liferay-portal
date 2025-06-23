/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import SearchResultsPanel from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/SearchResultsPanel';

global.Liferay = {
	Language: {get: (key) => key},
};

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/TabCollection',
	() => ({
		__esModule: true,
		default: ({collection}) => (
			<div data-testid={`tab-collection-${collection.id}`}>
				{collection.label}
			</div>
		),
	})
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/MarketplaceSearchResults',
	() => ({
		__esModule: true,
		default: () => <div data-testid="marketplace-search-results" />,
	})
);

const mockFilteredTabs = [
	{
		collections: [
			{id: '1', label: 'Collection 1'},
			{id: '2', label: 'Collection 2'},
		],
		label: 'Category 1',
	},
	{
		collections: [{id: '3', label: 'Collection 3'}],
		label: 'Category 2',
	},
];

function renderSearchResultsPanel({
	loading = false,
	filteredTabs = mockFilteredTabs,
	searchValue = 'test',
	viewMarketplace = true,
} = {}) {
	return render(
		<StoreAPIContextProvider
			dispatch={() => {}}
			getState={() => ({
				permissions: {
					VIEW_MARKETPLACE: viewMarketplace,
				},
			})}
		>
			<SearchResultsPanel
				filteredTabs={filteredTabs}
				loading={loading}
				searchValue={searchValue}
			/>
		</StoreAPIContextProvider>
	);
}

describe('SearchResultsPanel', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders loading indicator when loading is true', async () => {
		const {container} = renderSearchResultsPanel({loading: true});

		await waitFor(() => {
			expect(
				container.getElementsByClassName('loading-animation').length
			).toBe(1);
		});
	});

	it('renders filtered tabs when available', () => {
		renderSearchResultsPanel();
		mockFilteredTabs.forEach((tab) => {
			expect(screen.getByText(tab.label)).toBeInTheDocument();
			tab.collections.forEach((collection) => {
				expect(
					screen.getByTestId(`tab-collection-${collection.id}`)
				).toBeInTheDocument();
			});
		});
	});

	it('renders empty state when no filtered tabs are available', () => {
		renderSearchResultsPanel({filteredTabs: []});
		expect(screen.getByText('no-results-found')).toBeInTheDocument();
	});

	it('renders marketplace results if has permission to view marketplace', () => {
		renderSearchResultsPanel();

		expect(
			screen.getByTestId('marketplace-search-results')
		).toBeInTheDocument();
	});

	it('does not render marketplace results if no permission to view marketplace', () => {
		renderSearchResultsPanel({viewMarketplace: false});

		expect(
			screen.queryByTestId('marketplace-search-results')
		).not.toBeInTheDocument();
	});
});
