/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {useMarketplaceConfiguration} from '@liferay/marketplace-js-components-web';

import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import SearchResultsPanel from '../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/SearchResultsPanel';

global.Liferay = {
	FeatureFlags: {'LPD-34938': true},
	Language: {get: (key) => key},
};

jest.mock('@liferay/marketplace-js-components-web', () => {
	const actual = jest.requireActual('@liferay/marketplace-js-components-web');
	const mockGetProducts = {
		getProducts: jest.fn(),
	};
	const mockMarketplaceRest = jest.fn(() => mockGetProducts);
	mockMarketplaceRest.getBaseResourceURL = jest.fn(() => 'mocked-base-url');

	return {
		...actual,
		MarketplaceRest: mockMarketplaceRest,
		useMarketplaceConfiguration: jest.fn(),
	};
});

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/TabCollection',
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
	'../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/MarketplaceSearchResults',
	() => ({
		__esModule: true,
		default: ({searchValue}) => (
			<div data-testid="marketplace-search-results">
				Search: {searchValue}
			</div>
		),
	})
);

const mockMarketplaceConfiguration = {
	authorized: true,
};

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

const components = ({
	loading = false,
	viewMarketplace = true,
	filteredTabs = mockFilteredTabs,
	searchValue = 'test',
}) => (
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

function renderMarketplaceSearchResults({
	loading = false,
	viewMarketplace = true,
	filteredTabs = mockFilteredTabs,
	searchValue = 'test',
}) {
	return render(
		components({filteredTabs, loading, searchValue, viewMarketplace})
	);
}

describe('SearchResultsPanel', () => {
	beforeEach(() => {
		useMarketplaceConfiguration.mockReturnValue(
			mockMarketplaceConfiguration
		);
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders loading indicator when loading is true', async () => {
		const {container} = renderMarketplaceSearchResults({loading: true});

		await waitFor(() => {
			expect(
				container.getElementsByClassName('loading-animation').length
			).toBe(1);
		});
	});

	it('renders filtered tabs when available', () => {
		renderMarketplaceSearchResults({});
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
		renderMarketplaceSearchResults({filteredTabs: []});
		expect(screen.getByText('no-results-found')).toBeInTheDocument();
	});

	it('renders marketplace search results when button is clicked', async () => {
		renderMarketplaceSearchResults({});

		fireEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			expect(
				screen.getByTestId('marketplace-search-results')
			).toBeInTheDocument();
		});

		expect(
			screen.getByTestId('marketplace-search-results')
		).toHaveTextContent('Search: test');
	});

	it('hides marketplace search results when searchValue changes', async () => {
		const {rerender} = renderMarketplaceSearchResults({});

		fireEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			expect(
				screen.getByTestId('marketplace-search-results')
			).toBeInTheDocument();
		});

		rerender(components({searchValue: 'test2'}));

		expect(
			screen.queryByTestId('marketplace-search-results')
		).not.toBeInTheDocument();
	});

	it('renders "see marketplace results" button when not showing marketplace results', () => {
		renderMarketplaceSearchResults({});

		expect(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		).toBeInTheDocument();
	});

	it('does not render "see marketplace results" if Liferay FeatureFlag is false', () => {
		global.Liferay.FeatureFlags['LPD-34938'] = false;

		renderMarketplaceSearchResults({});

		expect(
			screen.queryByRole('button', {name: 'see-marketplace-results'})
		).not.toBeInTheDocument();

		global.Liferay.FeatureFlags['LPD-34938'] = true;
	});

	it('does not render "see marketplace results" if not connected to marketplace', () => {
		mockMarketplaceConfiguration.authorized = false;

		renderMarketplaceSearchResults({});

		expect(
			screen.queryByRole('button', {name: 'see-marketplace-results'})
		).not.toBeInTheDocument();

		mockMarketplaceConfiguration.authorized = true;
	});

	it('does not render "see marketplace results" if no permission to view marketplace', () => {
		renderMarketplaceSearchResults({viewMarketplace: false});

		expect(
			screen.queryByRole('button', {name: 'see-marketplace-results'})
		).not.toBeInTheDocument();
	});
});
