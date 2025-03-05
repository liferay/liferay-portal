/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {MarketplaceRest} from '@liferay/marketplace-js-components-web';

import MarketplaceSearchResults from '../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/MarketplaceSearchResults';

global.Liferay = {
	FeatureFlags: {'LPD-34938': true},
	Language: {get: (key) => key},
	ThemeDisplay: {getPathThemeImages: jest.fn()},
};

jest.mock('@liferay/marketplace-js-components-web', () => {
	const actual = jest.requireActual('@liferay/marketplace-js-components-web');
	const mockGetProducts = {
		getProducts: jest.fn(),
	};
	const mockMarketplaceRest = jest.fn(() => mockGetProducts);

	return {
		...actual,
		MarketplaceContext: {
			Provider: ({children, value}) => (
				<actual.MarketplaceContext.Provider value={value}>
					{children}
				</actual.MarketplaceContext.Provider>
			),
		},
		MarketplaceRest: mockMarketplaceRest,
	};
});

jest.mock('@liferay/layout-js-components-web', () => {
	const {MarketplaceContext} = jest.requireActual(
		'@liferay/marketplace-js-components-web'
	);

	return {
		...jest.requireActual('@liferay/layout-js-components-web'),
		MarketplaceModal: ({onOpenChange, trigger}) => (
			<MarketplaceContext.Provider
				value={{
					modal: {onOpenChange},
					setProduct: jest.fn(),
					setView: jest.fn(),
				}}
			>
				{trigger}
			</MarketplaceContext.Provider>
		),
	};
});

const mockProducts = {
	items: [
		{
			catalogName: 'Catalog 1',
			name: 'Product 1',
			urlImage: 'urlImage1',
		},
		{
			catalogName: 'Catalog 2',
			name: 'Product 2',
			urlImage: 'urlImage2',
		},
	],
	lastPage: 1,
	page: 1,
};

function renderMarketplaceSearchResults({authorized = true, loading = false}) {
	return render(
		<MarketplaceSearchResults
			baseResourceURL="mocked-base-url"
			marketplaceConfiguration={{
				authorized,
				loading,
			}}
			searchValue="test"
		/>
	);
}

describe('MarketplaceSearchResults', () => {
	let mockMarketplaceInstance;

	beforeEach(() => {
		mockMarketplaceInstance = new MarketplaceRest();
		mockMarketplaceInstance.getProducts.mockResolvedValue(mockProducts);
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('fetches and displays marketplace results', async () => {
		const {container} = renderMarketplaceSearchResults({});

		await waitFor(() => {
			expect(mockMarketplaceInstance.getProducts).toHaveBeenCalled();
			expect(
				screen.getByText('showing-results-from-marketplace')
			).toBeInTheDocument();
			expect(screen.getAllByTitle(`x-details`).length).toBe(2);

			const expectProduct = (index) => {
				expect(
					screen.getByText(`Product ${index}`)
				).toBeInTheDocument();
				expect(
					screen.getByText(`Catalog ${index}`)
				).toBeInTheDocument();
				const imageElements = screen.getAllByRole('img');
				const urlImage = imageElements.find(
					(image) => image.getAttribute('src') === `urlImage${index}`
				);
				expect(urlImage).toBeInTheDocument();
			};

			expectProduct(1);
			expectProduct(2);

			expect(
				container.getElementsByClassName('lexicon-icon-angle-right')
					.length
			).toBe(2);
		});
	});

	it('displays empty state when no results are found', async () => {
		const emptyProducts = {items: [], lastPage: 1, page: 1};
		mockMarketplaceInstance.getProducts.mockResolvedValueOnce(
			emptyProducts
		);

		renderMarketplaceSearchResults({});

		await waitFor(() => {
			expect(screen.getByText('no-results-found')).toBeInTheDocument();
		});
	});

	it('displays loading indicator while fetching results', async () => {
		const {container} = renderMarketplaceSearchResults({loading: true});

		await waitFor(() => {
			expect(
				container.getElementsByClassName('loading-animation').length
			).toBe(1);
		});
	});

	it('handles "load more results" functionality', async () => {
		mockProducts.lastPage = 2;

		renderMarketplaceSearchResults({});

		await waitFor(() => {
			fireEvent.click(
				screen.getByRole('button', {name: 'load-more-results'})
			);
			expect(mockMarketplaceInstance.getProducts).toHaveBeenCalledTimes(
				2
			);
		});
	});
});
