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
	isMarketplaceButtonVisited: false,
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
					set: jest.fn(),
				},
			},
		};
	});

	afterEach(() => {
		delete global.Liferay;
	});

	it('renders ClayButtonWithIcon when not visited', () => {
		renderComponent();

		expect(screen.getByRole('button')).toBeInTheDocument();
	});

	it('renders MarketplaceModal when visited', () => {
		renderComponent({...mockProps, isMarketplaceButtonVisited: true});

		expect(
			screen.getByTestId('mock-marketplace-modal')
		).toBeInTheDocument();
	});

	it('opens MarketplacePresentationModal on click', async () => {
		renderComponent();

		userEvent.click(screen.getByRole('button'));

		await waitFor(() => {
			expect(openModalComponent).toHaveBeenCalledWith({
				ModalComponent: MarketplacePresentationModal,
				modalComponentProps: {
					body: mockProps.body,
					fragmentPortletNamespace:
						mockProps.fragmentPortletNamespace,
					fragmentsImportURL: mockProps.fragmentsImportURL,
					heading: mockProps.heading,
					portletNamespace: mockProps.portletNamespace,
				},
			});
		});
	});

	it('sets visited state to true on click', async () => {
		const {rerender} = renderComponent();

		userEvent.click(screen.getByRole('button'));

		rerender(
			getComponent({...mockProps, isMarketplaceButtonVisited: true})
		);

		await waitFor(() => {
			expect(
				screen.getByTestId('mock-marketplace-modal')
			).toBeInTheDocument();
		});
	});

	it('sets session storage on click', async () => {
		renderComponent();

		userEvent.click(screen.getByRole('button'));

		await waitFor(() => {
			expect(Liferay.Util.Session.set).toHaveBeenCalledWith(
				`${mockProps.portletNamespace}isMarketplaceButtonVisited`,
				true
			);
		});
	});

	it('adds notification class when not visited', () => {
		const {container} = renderComponent();

		expect(container.querySelector('.notification')).toBeInTheDocument();
	});

	it('does not add notification class when visited', () => {
		const {container} = renderComponent({
			...mockProps,
			isMarketplaceButtonVisited: true,
		});

		expect(
			container.querySelector('.notification')
		).not.toBeInTheDocument();
	});
});
