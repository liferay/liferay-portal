/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import MarketplaceButton from '../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplaceButton';
import MarketplacePresentationModal from '../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplacePresentationModal';
import openModalComponent from '../../../src/main/resources/META-INF/resources/js/components/modals/openModalComponent';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/modals/openModalComponent'
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplaceModal',
	() => ({
		__esModule: true,
		default: jest.fn(() => (
			<div data-testid="mock-marketplace-modal">
				Mock MarketplaceModal
			</div>
		)),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplacePresentationModal',
	() => ({
		__esModule: true,
		default: jest.fn(() => (
			<div data-testid="mock-marketplace-presentation-modal">
				Mock MarketplacePresentationModal
			</div>
		)),
	})
);

const mockProps = {
	body: 'Test Body',
	fragmentPortletNamespace: 'testNamespace',
	fragmentsImportURL: '/testImportURL',
	heading: 'Test Heading',
	permissions: {},
	portletNamespace: 'testPortlet',
};

function getComponent(props = mockProps) {
	return <MarketplaceButton {...props} />;
}

const renderComponent = (props = mockProps) => render(getComponent(props));

describe('MarketplaceButton', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		global.Liferay = {
			Language: {get: (key) => key},
			Util: {
				Session: {
					get: jest.fn(),
					set: jest.fn(),
				},
			},
		};
	});

	afterEach(() => {
		delete global.Liferay;
	});

	it('renders button after visited status checked and adds notification class when not visited', async () => {
		global.Liferay.Util.Session.get.mockResolvedValue('false');

		renderComponent();

		expect(
			screen.queryByRole('button', {name: /open-marketplace-explorer/i})
		).not.toBeInTheDocument();
		expect(
			screen.queryByTestId('mock-marketplace-modal')
		).not.toBeInTheDocument();

		await waitFor(() => {
			const marketplaceButton = screen.getByRole('button', {
				name: /open-marketplace-explorer/i,
			});
			expect(marketplaceButton).toBeInTheDocument();
			expect(marketplaceButton).toHaveClass(
				'marketplace-button--notification'
			);
			expect(
				screen.queryByTestId('mock-marketplace-modal')
			).not.toBeInTheDocument();
		});
	});

	it('render MarketplaceModal after visited status checked and does not add notification class when visited', async () => {
		global.Liferay.Util.Session.get.mockResolvedValue('true');

		renderComponent();

		expect(
			screen.queryByRole('button', {name: /open-marketplace-explorer/i})
		).not.toBeInTheDocument();
		expect(
			screen.queryByTestId('mock-marketplace-modal')
		).not.toBeInTheDocument();

		await waitFor(() => {
			expect(
				screen.getByTestId('mock-marketplace-modal')
			).toBeInTheDocument();
			expect(
				screen.queryByRole('button', {
					name: /open-marketplace-explorer/i,
				})
			).not.toBeInTheDocument();
		});
	});

	it('opens MarketplacePresentationModal on click when button not visited', async () => {
		global.Liferay.Util.Session.get.mockResolvedValue('false');

		renderComponent();

		expect(
			screen.queryByRole('button', {name: /open-marketplace-explorer/i})
		).not.toBeInTheDocument();
		expect(
			screen.queryByTestId('mock-marketplace-modal')
		).not.toBeInTheDocument();

		const button = await waitFor(() =>
			screen.getByRole('button', {name: /open-marketplace-explorer/i})
		);

		userEvent.click(button);

		await waitFor(() => {
			expect(openModalComponent).toHaveBeenCalledWith({
				ModalComponent: MarketplacePresentationModal,
				modalComponentProps: {
					body: mockProps.body,
					fragmentPortletNamespace:
						mockProps.fragmentPortletNamespace,
					fragmentsImportURL: mockProps.fragmentsImportURL,
					heading: mockProps.heading,
					permissions: mockProps.permissions,
					portletNamespace: mockProps.portletNamespace,
				},
			});
		});
	});

	it('sets visited state to true on click and re-renders modal', async () => {
		global.Liferay.Util.Session.get.mockResolvedValue('false');

		const {rerender} = renderComponent();

		expect(
			screen.queryByRole('button', {name: /open-marketplace-explorer/i})
		).not.toBeInTheDocument();
		expect(
			screen.queryByTestId('mock-marketplace-modal')
		).not.toBeInTheDocument();

		const button = await waitFor(() =>
			screen.getByRole('button', {name: /open-marketplace-explorer/i})
		);

		global.Liferay.Util.Session.set.mockImplementation(() => {
			global.Liferay.Util.Session.get.mockResolvedValue('true');
		});

		userEvent.click(button);

		rerender(getComponent({...mockProps}));

		await waitFor(() => {
			expect(
				screen.getByTestId('mock-marketplace-modal')
			).toBeInTheDocument();
		});
	});

	it('sets session storage on click', async () => {
		global.Liferay.Util.Session.get.mockResolvedValue('false');

		renderComponent();

		expect(
			screen.queryByRole('button', {name: /open-marketplace-explorer/i})
		).not.toBeInTheDocument();

		const button = await waitFor(() =>
			screen.getByRole('button', {name: /open-marketplace-explorer/i})
		);

		await userEvent.click(button);

		await waitFor(() => {
			expect(Liferay.Util.Session.set).toHaveBeenCalledWith(
				`${mockProps.portletNamespace}isMarketplaceButtonVisited`,
				'true'
			);
		});
	});
});
