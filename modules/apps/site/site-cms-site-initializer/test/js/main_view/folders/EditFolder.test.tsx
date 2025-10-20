/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import FolderService from '../../../../src/main/resources/META-INF/resources/js/common/services/FolderService';
import EditFolder from '../../../../src/main/resources/META-INF/resources/js/main_view/folders/EditFolder';
import {mockNavigate} from '../../__mocks__/frontend-js-web';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const mockFolder = {
	description: 'Folder description',
	id: 123,
	scopeKey: 'Default',
	title: 'My Folder',
};

describe('EditFolder', () => {
	let getFolderSpy: jest.SpyInstance;
	let updateFolderSpy: jest.SpyInstance;

	beforeEach(() => {
		getFolderSpy = jest
			.spyOn(FolderService, 'getFolder')
			.mockResolvedValue(mockFolder);

		updateFolderSpy = jest
			.spyOn(FolderService, 'updateFolder')
			.mockResolvedValue({data: mockFolder, error: null});

		mockNavigate.mockClear();
		(openToast as jest.Mock).mockClear();
	});

	afterEach(() => {
		getFolderSpy.mockRestore();
		updateFolderSpy.mockRestore();
	});

	const renderComponent = async (skipFirstRender = false) => {
		const renderResult = render(
			<EditFolder backURL="/back" folderId={String(mockFolder.id)} />
		);

		if (!skipFirstRender) {
			await waitFor(() =>
				expect(
					screen.getByRole('heading', {name: mockFolder.title})
				).toBeInTheDocument()
			);
		}

		return renderResult;
	};

	it('renders the loading state and then the form with fetched data', async () => {
		await renderComponent(true);

		expect(screen.getByRole('heading', {name: ''})).toBeInTheDocument();
		expect(
			screen
				.getByRole('heading', {name: ''})
				.querySelector('.loading-animation')
		).toBeInTheDocument();

		await waitFor(() => {
			expect(getFolderSpy).toHaveBeenCalledWith(String(mockFolder.id));
		});

		expect(
			await screen.findByRole('heading', {name: mockFolder.title})
		).toBeInTheDocument();

		const nameInput = await screen.findByLabelText(/name/i);
		expect(nameInput).toHaveValue(mockFolder.title);

		expect(screen.getByLabelText('description')).toHaveValue(
			mockFolder.description
		);

		const combobox = screen.getByRole('combobox', {name: /space/i});
		expect(combobox).toHaveValue(mockFolder.scopeKey);
		expect(combobox).toHaveAttribute('aria-readonly', 'true');
	});

	it('navigates back when the back button is clicked', async () => {
		await renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'back'}));

		expect(mockNavigate).toHaveBeenCalledWith('/back');
	});

	it('submits the form with updated values and shows a success message', async () => {
		await renderComponent();

		await waitFor(() =>
			expect(
				screen.getByRole('heading', {name: mockFolder.title})
			).toBeInTheDocument()
		);

		const nameInput = await screen.findByLabelText(/name/i);
		const descriptionInput = screen.getByLabelText('description');

		await userEvent.clear(nameInput);
		await userEvent.type(nameInput, 'Updated Folder Name');

		await userEvent.clear(descriptionInput);
		await userEvent.type(descriptionInput, 'Updated Description');

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => {
			expect(updateFolderSpy).toHaveBeenCalledWith({
				description: 'Updated Description',
				id: 123,
				title: 'Updated Folder Name',
			});
		});

		expect(mockNavigate).toHaveBeenCalledWith('/back');
		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: 'x-was-updated-successfully',
				type: 'success',
			})
		);
	});

	it('shows an error message if the update fails', async () => {
		updateFolderSpy.mockResolvedValue({error: 'Update failed!'});

		await renderComponent();

		await waitFor(() =>
			expect(
				screen.getByRole('heading', {name: mockFolder.title})
			).toBeInTheDocument()
		);

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => {
			expect(updateFolderSpy).toHaveBeenCalled();
		});

		expect(mockNavigate).not.toHaveBeenCalled();
		expect(openToast).toHaveBeenCalledWith({
			message: 'Update failed!',
			type: 'danger',
		});
	});

	it('shows a validation error if the name is empty', async () => {
		await renderComponent();

		await waitFor(() =>
			expect(
				screen.getByRole('heading', {name: mockFolder.title})
			).toBeInTheDocument()
		);

		const nameInput = await screen.findByLabelText(/name/i);

		await userEvent.clear(nameInput);

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(
			await screen.findByText('this-field-is-required')
		).toBeInTheDocument();
		expect(updateFolderSpy).not.toHaveBeenCalled();
		expect(mockNavigate).not.toHaveBeenCalled();
	});
});
