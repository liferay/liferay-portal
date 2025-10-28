/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import ClayStyleModal from '../../../src/main/resources/META-INF/resources/js/components/modals/CardStyleModal';

globalThis.Liferay = {
	Language: {get: (key) => key},
};

const mockProps = {
	body: 'This is the body of the new feature.',
	heading: 'New Feature Alert!',
	imageSrc: '/path/to/image.png',
	onCloseModal: jest.fn(),
	onPrimaryButtonClick: jest.fn(),
	primaryButtonIcon: 'check',
	primaryButtonLabel: 'Got it!',
	secondaryButtonLabel: 'Dismiss',
};

const renderComponent = (props = mockProps) =>
	render(<ClayStyleModal {...props} />);

describe('ClayStyleModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders the modal with provided content', async () => {
		renderComponent();
		await waitFor(() => {
			expect(screen.getByText('New Feature Alert!')).toBeInTheDocument();
			expect(
				screen.getByText('This is the body of the new feature.')
			).toBeInTheDocument();
			expect(
				screen.getAllByRole('presentation', {hidden: true})[1]
			).toHaveAttribute('src', '/path/to/image.png');
		});
	});

	it('renders the primary button with label and icon', async () => {
		renderComponent();
		await waitFor(() => {
			const primaryButton = screen.getByRole('button', {name: 'Got it!'});
			expect(primaryButton).toBeInTheDocument();
			expect(
				primaryButton.querySelector('svg use[href="#check"]')
			).toBeInTheDocument();
		});
	});

	it('renders the secondary button with label', async () => {
		renderComponent();
		await waitFor(() => {
			expect(
				screen.getByRole('button', {name: 'Dismiss'})
			).toBeInTheDocument();
		});
	});

	it('calls onClose prop when the close button is clicked', async () => {
		renderComponent();
		const closeButton = await waitFor(() => screen.getByLabelText('close'));
		await userEvent.click(closeButton);
		await waitFor(() => {
			expect(mockProps.onCloseModal).toHaveBeenCalledTimes(1);
		});
	});

	it('calls onClose prop when the secondary button is clicked', async () => {
		renderComponent();
		const secondaryButton = await waitFor(() =>
			screen.getByRole('button', {
				name: 'Dismiss',
			})
		);
		await userEvent.click(secondaryButton);
		await waitFor(() => {
			expect(mockProps.onCloseModal).toHaveBeenCalledTimes(1);
		});
	});

	it('calls onPrimaryButtonClick and onClose prop when the primary button is clicked', async () => {
		renderComponent();

		const primaryButton = await waitFor(() =>
			screen.getByRole('button', {name: 'Got it!'})
		);
		await userEvent.click(primaryButton);
		await waitFor(() => {
			expect(mockProps.onPrimaryButtonClick).toHaveBeenCalledTimes(1);
			expect(mockProps.onCloseModal).toHaveBeenCalledTimes(1);
		});
	});

	it('does not render primary button if not provided', async () => {
		renderComponent({...mockProps, primaryButtonLabel: undefined});
		await waitFor(() => {
			expect(
				screen.queryByRole('button', {name: 'Got it!'})
			).not.toBeInTheDocument();
		});
	});

	it('does not render secondary button if not provided', async () => {
		renderComponent({...mockProps, secondaryButtonLabel: undefined});
		await waitFor(() => {
			expect(
				screen.queryByRole('button', {name: 'Dismiss!'})
			).not.toBeInTheDocument();
		});
	});

	it('renders primary button without icon if not provided', async () => {
		renderComponent({...mockProps, primaryButtonIcon: undefined});
		await waitFor(() => {
			const primaryButton = screen.getByRole('button', {name: 'Got it!'});
			expect(
				primaryButton.querySelector(`svg[data-clay-symbol="check"]`)
			).toBeNull();
		});
	});
});
