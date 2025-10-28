/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import MarketplacePresentationModal from '../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplacePresentationModal';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplaceModal',
	() => ({
		__esModule: true,
		default: jest.fn(
			({
				children,
				fragmentPortletNamespace,
				fragmentsImportURL,
				trigger,
			}) => (
				<div data-testid="mock-marketplace-modal-local">
					{trigger}

					{children}

					{fragmentPortletNamespace}

					{fragmentsImportURL}
				</div>
			)
		),
	})
);

jest.mock('@liferay/marketplace-js-components-web', () => ({
	...jest.requireActual('@liferay/marketplace-js-components-web'),
	useMarketplaceContext: jest.fn(() => ({
		modal: {
			onOpenChange: jest.fn(),
		},
	})),
}));

const mockProps = {
	body: 'Test body',
	fragmentPortletNamespace: 'testNamespace',
	fragmentsImportURL: '/testImportURL',
	heading: 'Test Heading',
	onCloseModal: jest.fn(),
	portletNamespace: 'testPortlet',
};

const renderComponent = (props = mockProps) =>
	render(<MarketplacePresentationModal {...props} />);

describe('MarketplacePresentationModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders the modal with correct content', async () => {
		renderComponent();

		await waitFor(() => {
			expect(screen.getByText('Test Heading')).toBeInTheDocument();
			expect(screen.getByText('Test body')).toBeInTheDocument();
			expect(screen.getByText('cancel')).toBeInTheDocument();
			expect(screen.getByText('explore-marketplace')).toBeInTheDocument();
			expect(
				screen.getAllByRole('presentation', {hidden: true})[1]
			).toHaveAttribute(
				'src',
				`${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathContext()}/o/layout-js-components-web/images/marketplace.svg`
			);
		});
	});

	it('calls onCloseModal when cancel button is clicked', async () => {
		const {findByRole} = renderComponent();

		const cancelButton = await findByRole('button', {name: /cancel/i});

		expect(cancelButton).toBeInTheDocument();

		userEvent.click(cancelButton);

		await waitFor(() => {
			expect(mockProps.onCloseModal).toHaveBeenCalledTimes(1);
		});
	});

	it('renders MarketplaceModal with correct props', async () => {
		const {findByRole} = await renderComponent();

		const exploreMarketplaceButton = await findByRole('button', {
			name: /explore-marketplace/i,
		});

		expect(exploreMarketplaceButton).toBeInTheDocument();

		userEvent.click(exploreMarketplaceButton);

		await waitFor(() => {
			screen.getByTestId('mock-marketplace-modal-local');
		});

		expect(
			require('../../../src/main/resources/META-INF/resources/js/components/marketplace/MarketplaceModal')
				.default
		).toHaveBeenCalledWith(
			expect.objectContaining({
				fragmentPortletNamespace: mockProps.fragmentPortletNamespace,
				fragmentsImportURL: mockProps.fragmentsImportURL,
				openOnRender: true,
				portletNamespace: mockProps.portletNamespace,
				trigger: null,
			}),
			expect.anything()
		);
	});
});
