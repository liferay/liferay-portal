/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {
	MarketplaceRest,
	useMarketplaceConfiguration,
} from '@liferay/marketplace-js-components-web';
import userEvent from '@testing-library/user-event';

import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import MarketplaceSearchResults from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/MarketplaceSearchResults';

global.Liferay = {
	Language: {get: (key) => key},
	ThemeDisplay: {getPathThemeImages: jest.fn()},
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
		MarketplaceContext: {
			Provider: ({children, value}) => (
				<actual.MarketplaceContext.Provider value={value}>
					{children}
				</actual.MarketplaceContext.Provider>
			),
		},
		MarketplaceRest: mockMarketplaceRest,
		useMarketplaceConfiguration: jest.fn(),
	};
});

const mockOpenChange = jest.fn();

jest.mock('@liferay/layout-js-components-web', () => {
	const {MarketplaceContext} = jest.requireActual(
		'@liferay/marketplace-js-components-web'
	);

	return {
		...jest.requireActual('@liferay/layout-js-components-web'),
		MarketplaceModal: jest.fn(
			({children, onOpenChange = mockOpenChange}) => (
				<MarketplaceContext.Provider
					value={{
						modal: {onOpenChange},
						setProduct: jest.fn(),
						setView: jest.fn(),
					}}
				>
					<div data-testid="marketplace-modal-children">
						{children}
					</div>
				</MarketplaceContext.Provider>
			)
		),
	};
});

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			fragmentPortletNamespace: 'mockNamespace',
			fragmentsImportURL: '/mockImportURL',
			portletNamespace: 'testPortlet',
		},
	})
);

const mockMarketplaceConfiguration = {
	authorized: true,
	data: {},
	loading: false,
};

const getProduct = (id) => ({
	catalogName: `Catalog ${id}`,
	id,
	name: `Product ${id}`,
	urlImage: `urlImage${id}`,
});

const mockProducts = {
	items: [getProduct(1), getProduct(2)],
	lastPage: 2,
	page: 1,
};

const components = ({searchValue = 'test'}) => (
	<StoreAPIContextProvider
		dispatch={() => {}}
		getState={() => ({
			permissions: {
				INSTALL_FREE_BUNDLED_APPS_MARKETPLACE: true,
				PURCHASE_AND_INSTALL_PAID_APPS_MARKETPLACE: true,
			},
		})}
	>
		<MarketplaceSearchResults searchValue={searchValue} />
	</StoreAPIContextProvider>
);

function renderMarketplaceSearchResults({
	searchValue = 'test',
	viewMarketplace = true,
} = {}) {
	return render(components({searchValue, viewMarketplace}));
}

describe('MarketplaceSearchResults', () => {
	let mockMarketplaceInstance;

	beforeEach(() => {
		useMarketplaceConfiguration.mockReturnValue(
			mockMarketplaceConfiguration
		);
		mockMarketplaceInstance = new MarketplaceRest();
		mockMarketplaceInstance.getProducts.mockResolvedValue(mockProducts);
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders "see marketplace results" button when not showing marketplace results', () => {
		renderMarketplaceSearchResults();

		expect(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		).toBeInTheDocument();
	});

	it('does not render "see marketplace results" if not connected to marketplace', () => {
		mockMarketplaceConfiguration.authorized = false;

		renderMarketplaceSearchResults();

		expect(
			screen.queryByRole('button', {name: 'see-marketplace-results'})
		).not.toBeInTheDocument();

		mockMarketplaceConfiguration.authorized = true;
	});

	it('fetches and displays marketplace results when button is clicked', async () => {
		const expectProduct = (index) => {
			expect(screen.getByText(`Product ${index}`)).toBeInTheDocument();
			expect(screen.getByText(`Catalog ${index}`)).toBeInTheDocument();

			const imageElements = screen.getAllByRole('img');
			const urlImage = imageElements.find(
				(image) => image.getAttribute('src') === `urlImage${index}`
			);

			expect(urlImage).toBeInTheDocument();
		};

		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			expect(mockMarketplaceInstance.getProducts).toHaveBeenCalled();
			expect(
				screen.getByText('showing-results-from-marketplace')
			).toBeInTheDocument();
			expect(screen.getByText('showing-2-results')).toBeInTheDocument();
			expect(screen.getByTitle(`Product 1-details`)).toBeInTheDocument();
			expect(screen.getByTitle(`Product 2-details`)).toBeInTheDocument();

			expectProduct(1);
			expectProduct(2);
		});
	});

	it('hides marketplace search results when searchValue changes', async () => {
		const {rerender} = renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			expect(
				screen.getByText('showing-results-from-marketplace')
			).toBeInTheDocument();
		});

		rerender(components({searchValue: 'test2'}));

		expect(
			screen.queryByText('showing-results-from-marketplace')
		).not.toBeInTheDocument();
	});

	it('displays empty state when no results are found', async () => {
		const emptyProducts = {items: [], lastPage: 1, page: 1};

		mockMarketplaceInstance.getProducts.mockResolvedValueOnce(
			emptyProducts
		);

		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			expect(screen.getAllByText('no-results-found')).toHaveLength(2);
		});
	});

	it('displays loading indicator while fetching results', async () => {
		const {container} = renderMarketplaceSearchResults({loading: true});

		userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			expect(
				container.getElementsByClassName('loading-animation').length
			).toBe(1);
		});
	});

	it('handles "load more results" functionality', async () => {
		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		expect(mockMarketplaceInstance.getProducts).toHaveBeenCalledTimes(1);

		await expect(screen.getAllByRole('menuitem').length).toBe(2);

		expect(
			screen.getByRole('menuitem', {name: 'Product 1 Catalog 1'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('menuitem', {name: 'Product 2 Catalog 2'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {
				name: 'load-more-results',
			})
		).toBeInTheDocument();

		mockMarketplaceInstance.getProducts.mockResolvedValue({
			items: [getProduct(3), getProduct(4)],
			lastPage: 2,
			page: 2,
		});

		await userEvent.click(
			screen.getByRole('button', {
				name: 'load-more-results',
			})
		);

		await waitFor(() => {
			expect(
				screen.queryByRole('button', {name: 'load-more-results'})
			).not.toBeInTheDocument();
			expect(screen.getAllByRole('menuitem').length).toBe(4);
			expect(
				screen.getByRole('menuitem', {name: 'Product 1 Catalog 1'})
			).toBeInTheDocument();
			expect(
				screen.getByRole('menuitem', {name: 'Product 2 Catalog 2'})
			).toBeInTheDocument();
			expect(
				screen.getByRole('menuitem', {name: 'Product 3 Catalog 3'})
			).toBeInTheDocument();
			expect(
				screen.getByRole('menuitem', {name: 'Product 4 Catalog 4'})
			).toBeInTheDocument();
			expect(mockMarketplaceInstance.getProducts).toHaveBeenCalledTimes(
				2
			);
		});
	});

	it('focuses the first item only on initial load and handles keyboard navigation', async () => {
		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		expect(screen.getAllByRole('menubar').length).toBe(1);
		expect(screen.getAllByRole('menuitem').length).toBe(2);

		expect(
			screen.getByRole('menuitem', {name: 'Product 1 Catalog 1'})
		).toHaveFocus();

		await userEvent.keyboard('{ArrowDown}');

		expect(
			screen.getByRole('menuitem', {name: 'Product 2 Catalog 2'})
		).toHaveFocus();

		mockMarketplaceInstance.getProducts.mockResolvedValue({
			items: [getProduct(3), getProduct(4)],
			lastPage: 2,
			page: 2,
		});

		await userEvent.click(
			screen.getByRole('button', {name: 'load-more-results'})
		);

		expect(screen.getAllByRole('menuitem').length).toBe(4);

		expect(
			screen.getByRole('menuitem', {name: 'Product 1 Catalog 1'})
		).not.toHaveFocus();

		screen.getByRole('menuitem', {name: 'Product 2 Catalog 2'}).focus();
		expect(
			screen.getByRole('menuitem', {name: 'Product 2 Catalog 2'})
		).toHaveFocus();

		await userEvent.keyboard('{ArrowDown}');

		expect(
			screen.getByRole('menuitem', {name: 'Product 3 Catalog 3'})
		).toHaveFocus();

		await userEvent.keyboard('{ArrowDown}');

		expect(
			screen.getByRole('menuitem', {name: 'Product 4 Catalog 4'})
		).toHaveFocus();

		await userEvent.keyboard('{ArrowUp}');

		expect(
			screen.getByRole('menuitem', {name: 'Product 3 Catalog 3'})
		).toHaveFocus();
	});

	it('triggers modal on enter key press', async () => {
		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			userEvent.keyboard('{Enter}');

			expect(mockOpenChange).toHaveBeenCalledWith(true);
		});
	});

	it('triggers modal on space key press', async () => {
		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			userEvent.keyboard('{Space}');

			expect(mockOpenChange).toHaveBeenCalledWith(true);
		});
	});

	it('triggers modal on space key press linux', async () => {
		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			userEvent.keyboard(' ');

			expect(mockOpenChange).toHaveBeenCalledWith(true);
		});
	});

	it('displays error message in console when API call fails', async () => {
		mockMarketplaceInstance.getProducts.mockRejectedValue(
			new Error('API Error!')
		);

		console.error = jest.fn();

		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		await waitFor(() => {
			expect(console.error).toHaveBeenCalledWith(
				'Failed to fetch products:',
				expect.any(Error)
			);
		});

		console.error.mockRestore();
	});

	it('renders MarketplaceModal with specific children', async () => {
		renderMarketplaceSearchResults();

		await userEvent.click(
			screen.getByRole('button', {name: 'see-marketplace-results'})
		);

		screen.getAllByRole('menuitem');

		let firstMenuItem = null;

		await waitFor(() => {
			[firstMenuItem] = screen.getAllByRole('menuitem');
		});

		await userEvent.click(firstMenuItem);

		expect(
			screen.getByTestId('marketplace-modal-children')
		).toBeInTheDocument();

		expect(
			require('@liferay/layout-js-components-web').MarketplaceModal
		).toHaveBeenCalledWith(
			expect.objectContaining({
				children: expect.anything(),
				fragmentPortletNamespace: 'mockNamespace',
				fragmentsImportURL: '/mockImportURL',
				hideBackButton: true,
				permissions: {
					installFreeApps: true,
					purchaseAndInstallPaidApps: true,
				},
				portletNamespace: 'testPortlet',
				trigger: null,
			}),
			expect.anything()
		);
	});
});
