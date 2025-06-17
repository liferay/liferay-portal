/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {MarketplaceView} from '@liferay/marketplace-js-components-web';

import MarketplaceViews from '../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplaceViews';

const mockUseMarketplaceContext = {
	marketplaceRest: {
		checkoutCart: jest.fn(() => Promise.resolve()),
		createCart: jest.fn(() => Promise.resolve({id: 'test-cart-id'})),
		fetchMarketplace: jest.fn(() =>
			Promise.resolve({blob: () => new Blob(['test'])})
		),
		getPlacedOrder: jest.fn(() =>
			Promise.resolve({
				id: 'test-cart-id',
				placedOrderItems: [
					{
						virtualItemURLs: ['/o/marketplace/file.lpkg'],
						virtualItems: [{url: '/o/marketplace/file.lpkg'}],
					},
				],
			})
		),
	},
	modal: {onOpenChange: jest.fn()},
	permissions: {
		installFreeApps: true,
		manageFragmentsEntries: true,
		purchaseAndInstallPaidApps: true,
		viewApps: true,
	},
	product: {
		attachments: [{src: 'http://example.com/test.zip'}],
		name: 'Test Product',
	},
	setProduct: jest.fn(),
	setView: jest.fn(),
	view: MarketplaceView.PRODUCTS,
};

jest.mock('@liferay/marketplace-js-components-web', () => {
	const actualModule = jest.requireActual(
		'@liferay/marketplace-js-components-web'
	);

	return {
		...actualModule,
		Marketplace: {
			Products: ({children}) => (
				<div data-testid="mock-marketplace-products">
					{children({
						attachments: [{src: 'http://example.com/test.zip'}],
						name: 'Test Product',
					})}
				</div>
			),
			Storefront: ({onClickBack, primaryButton}) => (
				<div data-testid="mock-marketplace-storefront">
					{onClickBack && (
						<button onClick={onClickBack} role="button">
							back-to-list
						</button>
					)}

					{primaryButton}
				</div>
			),
		},
		MarketplaceProduct: jest.fn().mockImplementation((product) => ({
			...product,
			hasPermissionToInstall: jest.fn().mockReturnValue(true),
		})),
		useMarketplaceContext: jest.fn(() => mockUseMarketplaceContext),
	};
});

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/import/importZipFile',
	() =>
		jest.fn(({handleResponse}) => {
			handleResponse({
				importResults: {'some-fragment-id': 'some-fragment-name'},
			});

			return Promise.resolve();
		})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/marketplace/InstallFragmentModal',
	() => ({
		InstallFragmentModalBody: () => (
			<div data-testid="mock-install-fragment-modal-body">
				Install Fragment Modal Body
			</div>
		),
	})
);

const mockProps = {
	fragmentPortletNamespace: 'testNamespace',
	fragmentsImportURL: '/testImportURL',
	hideBackButton: false,
};

const getComponent = (props = mockProps) => <MarketplaceViews {...props} />;

const renderComponent = (props = mockProps) => render(getComponent(props));

describe('MarketplaceViews', () => {
	let consoleErrorSpy;

	beforeEach(() => {
		jest.clearAllMocks();

		consoleErrorSpy = jest
			.spyOn(console, 'error')
			.mockImplementation(() => {});
	});

	afterEach(() => {
		consoleErrorSpy.mockRestore();
		jest.restoreAllMocks();
	});

	it('renders products view correctly', async () => {
		renderComponent();

		expect(
			screen.getByTestId('mock-marketplace-products')
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: 'install'})
		).toBeInTheDocument();
	});

	it('renders storefront view correctly', async () => {
		const mockContext = {
			...mockUseMarketplaceContext,
			view: MarketplaceView.STOREFRONT,
		};
		require('@liferay/marketplace-js-components-web').useMarketplaceContext.mockReturnValue(
			mockContext
		);

		renderComponent();

		expect(
			screen.getByTestId('mock-marketplace-storefront')
		).toBeInTheDocument();

		const installButton = screen.getByRole('button', {name: 'install'});
		expect(installButton).toBeInTheDocument();
		userEvent.click(installButton);

		await waitFor(() => {
			expect(
				require('../../../src/main/resources/META-INF/resources/js/components/import/importZipFile')
			).toHaveBeenCalled();
		});

		const backButton = screen.queryByRole('button', {name: 'back-to-list'});
		expect(backButton).toBeInTheDocument();

		await userEvent.click(backButton);
		expect(mockContext.setView).toHaveBeenCalledWith(
			MarketplaceView.PRODUCTS
		);
	});

	it('handles product installation', async () => {
		const mockReload = jest.fn();
		Object.defineProperty(window, 'location', {
			value: {reload: mockReload},
		});

		renderComponent();

		userEvent.click(screen.getByRole('button', {name: 'install'}));

		await waitFor(() => {
			expect(mockUseMarketplaceContext.setView).toHaveBeenCalledWith(
				MarketplaceView.PURCHASE
			);

			expect(mockUseMarketplaceContext.setProduct).toHaveBeenCalled();

			expect(
				mockUseMarketplaceContext.marketplaceRest.createCart
			).toHaveBeenCalled();

			expect(
				mockUseMarketplaceContext.marketplaceRest.checkoutCart
			).toHaveBeenCalled();

			expect(
				require('../../../src/main/resources/META-INF/resources/js/components/import/importZipFile')
			).toHaveBeenCalled();

			expect(
				require('frontend-js-components-web').openToast
			).toHaveBeenCalledWith(expect.objectContaining({type: 'success'}));

			expect(mockReload).toHaveBeenCalledTimes(1);
			expect(
				mockUseMarketplaceContext.modal.onOpenChange
			).toHaveBeenCalledWith(false);
		});
	});

	it('renders purchase view correctly', () => {
		require('@liferay/marketplace-js-components-web').useMarketplaceContext.mockReturnValue(
			{
				...mockUseMarketplaceContext,
				view: MarketplaceView.PURCHASE,
			}
		);

		renderComponent();

		expect(
			screen.getByTestId('mock-install-fragment-modal-body')
		).toBeInTheDocument();
	});

	it('handles installation error', async () => {
		const mockContext = {
			...mockUseMarketplaceContext,
			marketplaceRest: {
				checkoutCart: jest.fn(() => Promise.resolve()),
				createCart: jest.fn(() =>
					Promise.reject(new Error('Installation failed'))
				),
				fetchMarketplace: jest.fn(() =>
					Promise.resolve({blob: () => new Blob(['test'])})
				),
			},
		};
		require('@liferay/marketplace-js-components-web').useMarketplaceContext.mockReturnValue(
			mockContext
		);

		renderComponent();

		userEvent.click(screen.getByRole('button', {name: 'install'}));

		await waitFor(() => {
			expect(
				require('frontend-js-components-web').openToast
			).toHaveBeenCalledWith(expect.objectContaining({type: 'danger'}));

			expect(mockContext.modal.onOpenChange).toHaveBeenCalledWith(false);
		});
	});

	it('hideBackButton prop controls back button visibility', async () => {
		require('@liferay/marketplace-js-components-web').useMarketplaceContext.mockReturnValue(
			{
				...mockUseMarketplaceContext,
				view: MarketplaceView.STOREFRONT,
			}
		);

		renderComponent({...mockProps, hideBackButton: true});

		expect(
			screen.queryByRole('button', {name: 'back-to-list'})
		).not.toBeInTheDocument();
	});

	it('hides install button if user cannot install product', async () => {
		const {rerender} = renderComponent();

		expect(
			screen.getByRole('button', {name: 'install'})
		).toBeInTheDocument();

		require('@liferay/marketplace-js-components-web').MarketplaceProduct =
			jest.fn().mockImplementation((product) => ({
				...product,
				hasPermissionToInstall: jest.fn().mockReturnValue(false),
			}));

		rerender(getComponent());

		expect(
			screen.queryByRole('button', {name: 'install'})
		).not.toBeInTheDocument();

		require('@liferay/marketplace-js-components-web').MarketplaceProduct =
			jest.fn().mockImplementation((product) => ({
				...product,
				hasPermissionToInstall: jest.fn().mockReturnValue(false),
			}));

		const mockContext = {
			...mockUseMarketplaceContext,
			permissions: {
				...mockUseMarketplaceContext.permissions,
				manageFragmentsEntries: false,
			},
		};

		require('@liferay/marketplace-js-components-web').useMarketplaceContext.mockReturnValue(
			mockContext
		);

		rerender(getComponent());

		expect(
			screen.queryByRole('button', {name: 'install'})
		).not.toBeInTheDocument();
	});
});
