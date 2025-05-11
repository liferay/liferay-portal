/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MarketplaceView} from '@liferay/marketplace-js-components-web';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {sub} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import MarketplaceModal from '../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplaceModal';
import MarketplaceViews from '../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplaceViews';

const mockUseMarketplaceContext = {
	modal: {onOpenChange: jest.fn()},
	product: null,
	setView: jest.fn(),
	view: MarketplaceView.PRODUCTS,
};

const mockBaseResourceURL = '/mock/marketplace/rest';

jest.mock('@liferay/marketplace-js-components-web', () => {
	const actualModule = jest.requireActual(
		'@liferay/marketplace-js-components-web'
	);

	return {
		...actualModule,
		Marketplace: {
			Modal: jest.fn(
				({children, noConnectionMessage, title, trigger}) => (
					<div data-testid="mock-marketplace-modal">
						{trigger}

						{title}

						{noConnectionMessage}

						{children}
					</div>
				)
			),
		},
		MarketplaceContextProvider: ({
			baseResourceURL,
			children,
			permissions,
			settings,
		}) => (
			<div data-testid="mock-marketplace-context-provider">
				{JSON.stringify({baseResourceURL, permissions, settings})}

				{children}
			</div>
		),
		MarketplaceRest: {
			getBaseResourceURL: jest.fn(() => mockBaseResourceURL),
		},
		useMarketplaceContext: jest.fn(() => mockUseMarketplaceContext),
	};
});

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplaceViews',
	() => ({
		__esModule: true,
		default: jest.fn(({fragmentPortletNamespace, fragmentsImportURL}) => (
			<div data-testid="mock-marketplace-views">
				{fragmentPortletNamespace}

				{fragmentsImportURL}
			</div>
		)),
	})
);

const mockProps = {
	fragmentPortletNamespace: 'testNamespace',
	fragmentsImportURL: '/testImportURL',
	hideBackButton: true,
	permissions: {
		installFreeApps: true,
		purchaseAndInstallPaidApps: true,
		viewApps: true,
	},
	portletNamespace: 'testPortlet',
	trigger: <button data-testid="custom-trigger">Custom Trigger</button>,
};

const renderComponent = (props = mockProps) =>
	render(<MarketplaceModal {...props} />);

describe('MarketplaceModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders Marketplace.Modal with correct props', () => {
		renderComponent();

		expect(
			screen.getByTestId('mock-marketplace-modal')
		).toBeInTheDocument();
		expect(
			require('@liferay/marketplace-js-components-web').Marketplace.Modal
		).toHaveBeenCalledWith(
			expect.objectContaining({
				noConnectionMessage: Liferay.Language.get(
					'please-go-to-instance-settings-to-enable-the-connection'
				),
			}),
			expect.anything()
		);
	});

	it('renders MarketplaceViews with correct props', () => {
		renderComponent();

		expect(
			screen.getByTestId('mock-marketplace-views')
		).toBeInTheDocument();
		expect(MarketplaceViews).toHaveBeenCalledWith(
			expect.objectContaining({
				fragmentPortletNamespace: mockProps.fragmentPortletNamespace,
				fragmentsImportURL: mockProps.fragmentsImportURL,
				hideBackButton: mockProps.hideBackButton,
			}),
			expect.anything()
		);
	});

	it('renders custom trigger if provided', () => {
		renderComponent();

		expect(screen.getByTestId('custom-trigger')).toBeInTheDocument();
	});

	it('renders default trigger if custom trigger is not provided', () => {
		renderComponent({...mockProps, trigger: undefined});

		expect(
			screen.getByRole('button', {
				name: Liferay.Language.get('open-marketplace-explorer'),
			})
		).toBeInTheDocument();
	});

	it('calls onOpenChange when trigger is clicked', async () => {
		const {getByTestId} = renderComponent();
		const triggerButton = getByTestId('custom-trigger');

		userEvent.click(triggerButton);

		await waitFor(() => {
			expect(
				mockUseMarketplaceContext.modal.onOpenChange
			).toHaveBeenCalledWith(true);
		});
	});

	it('calls setView when view is PURCHASE', async () => {
		require('@liferay/marketplace-js-components-web').useMarketplaceContext.mockReturnValue(
			{...mockUseMarketplaceContext, view: MarketplaceView.PURCHASE}
		);

		const {getByTestId} = renderComponent();
		const triggerButton = getByTestId('custom-trigger');

		userEvent.click(triggerButton);

		await waitFor(() => {
			expect(mockUseMarketplaceContext.setView).toHaveBeenCalledWith(
				MarketplaceView.PRODUCTS
			);
		});
	});

	it('sets title when view is PURCHASE and product is available', () => {
		require('@liferay/marketplace-js-components-web').useMarketplaceContext.mockReturnValue(
			{
				...mockUseMarketplaceContext,
				product: {name: 'Test Product'},
				view: MarketplaceView.PURCHASE,
			}
		);

		renderComponent();

		expect(
			require('@liferay/marketplace-js-components-web').Marketplace.Modal
		).toHaveBeenCalledWith(
			expect.objectContaining({
				title: sub(
					Liferay.Language.get('installing-x'),
					'Test Product'
				),
			}),
			expect.anything()
		);
	});

	it('sets title to undefined when view is not PURCHASE', () => {
		require('@liferay/marketplace-js-components-web').useMarketplaceContext.mockReturnValue(
			{...mockUseMarketplaceContext, product: {name: 'Test Product'}}
		);

		renderComponent();

		const marketplaceModalProps =
			require('@liferay/marketplace-js-components-web').Marketplace.Modal
				.mock.calls[0][0];

		expect(marketplaceModalProps).toEqual(
			expect.not.objectContaining({
				title: expect.anything(),
			})
		);
	});

	it('renders null when trigger is null', () => {
		renderComponent({...mockProps, trigger: null});

		expect(screen.queryByTestId('custom-trigger')).not.toBeInTheDocument();
		expect(
			screen.queryByRole('button', {
				name: Liferay.Language.get('open-marketplace-explorer'),
			})
		).not.toBeInTheDocument();
	});

	it('calls onOpenChange when openOnRender is true', () => {
		renderComponent({...mockProps, openOnRender: true});

		expect(
			mockUseMarketplaceContext.modal.onOpenChange
		).toHaveBeenCalledWith(true);
	});

	it('permissions are passed correctly to MarketplaceContextProvider', () => {
		renderComponent();

		expect(
			screen.getByTestId('mock-marketplace-context-provider')
		).toHaveTextContent(
			JSON.stringify({
				baseResourceURL: mockBaseResourceURL,
				permissions: mockProps.permissions,
				settings: {productFilter: 'fragments'},
			})
		);
	});

	it('calls getBaseResourceURL with the correct portletNamespace', () => {
		renderComponent();

		expect(
			require('@liferay/marketplace-js-components-web').MarketplaceRest
				.getBaseResourceURL
		).toHaveBeenCalledWith(mockProps.portletNamespace);
	});
});
