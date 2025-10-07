/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import CreationModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/CreationModalContent';

const mockOnSubmit = jest.fn();
const mockNavigate = jest.fn();

jest.mock('frontend-js-web', () => ({
	...((jest.requireActual('frontend-js-web') ?? {}) as any),
	navigate: (url: string) => mockNavigate(url),
}));

const defaultProps = {
	action: 'createFolder' as const,
	assetLibraries: [
		{groupId: 123, name: 'Space 1'},
		{groupId: 456, name: 'Space 2'},
	],
	closeModal: () => {},
	onSubmit: mockOnSubmit,
	title: 'Create Folder',
};

describe('CreationModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	test('renders the modal title in the header', () => {
		render(<CreationModalContent {...defaultProps} />);
		expect(screen.getByText('Create Folder')).toBeInTheDocument();
	});

	test('shows the name field only when action is "createFolder"', () => {
		const {rerender} = render(<CreationModalContent {...defaultProps} />);
		expect(screen.getByLabelText(/name/i)).toBeInTheDocument();

		rerender(
			<CreationModalContent {...defaultProps} action="createAsset" />
		);
		expect(screen.queryByLabelText('name')).not.toBeInTheDocument();
	});

	test('shows the space picker only when there are multiple asset libraries', () => {
		const {rerender} = render(<CreationModalContent {...defaultProps} />);
		expect(screen.getByLabelText(/space/i)).toBeInTheDocument();

		rerender(
			<CreationModalContent
				{...defaultProps}
				assetLibraries={[{groupId: 123, name: 'Only One Space'}]}
			/>
		);
		expect(screen.queryByLabelText(/space/i)).not.toBeInTheDocument();
	});

	test('calls onSubmit when form is submitted and there is no redirect', async () => {
		render(<CreationModalContent {...defaultProps} />);

		await userEvent.type(screen.getByLabelText(/name/i), 'Folder Name');
		await userEvent.click(screen.getByText('select-a-space'));
		await userEvent.click(screen.getByText('Space 1'));
		await userEvent.click(screen.getByText('save'));

		expect(mockOnSubmit).toHaveBeenCalledWith(
			expect.objectContaining({
				groupId: '123',
				name: 'Folder Name',
			}),
			expect.objectContaining({
				setErrors: expect.any(Function),
				setFieldError: expect.any(Function),
			})
		);
	});

	test('navigates with correct parameters when form is submitted and redirect is provided', async () => {
		const originalURL = window.URL;

		try {
			window.URL = class extends URL {
				constructor(path: string, base = 'http://localhost:8080') {
					super(path, base);
				}
			} as typeof URL;

			render(
				<CreationModalContent
					{...defaultProps}
					redirect="/target-page"
				/>
			);

			await userEvent.type(screen.getByLabelText(/name/i), 'Folder Name');
			await userEvent.click(screen.getByText('select-a-space'));
			await userEvent.click(screen.getByText('Space 1'));
			await userEvent.click(screen.getByText('save'));

			expect(mockNavigate).toHaveBeenCalledWith(
				expect.stringContaining('/target-page?')
			);
			expect(mockNavigate).toHaveBeenCalledWith(
				expect.stringContaining('name=Folder+Name')
			);
			expect(mockNavigate).toHaveBeenCalledWith(
				expect.stringContaining('groupId=123')
			);
		}
		finally {
			window.URL = originalURL;
		}
	});
});
