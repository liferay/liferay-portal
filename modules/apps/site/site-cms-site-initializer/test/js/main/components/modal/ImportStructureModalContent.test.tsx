/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import ImportStructureModalContent from '../../../../../src/main/resources/META-INF/resources/js/main/components/modal/ImportStructureModalContent';
import ApiHelper from '../../../../../src/main/resources/META-INF/resources/js/services/ApiHelper';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/services/ApiHelper'
);

const mockPostFormData = ApiHelper.postFormData as jest.MockedFunction<
	typeof ApiHelper.postFormData
>;

const mockCloseModal = jest.fn();
const mockLoadData = jest.fn();

const DEFAULT_PROPS = {
	closeModal: mockCloseModal,
	importURL: '/o/site-cms-site-initializer/import',
	loadData: mockLoadData,
	objectFolderExternalReferenceCode: 'content-folder',
};

const renderComponent = (props = DEFAULT_PROPS) => {
	return render(<ImportStructureModalContent {...props} />);
};

const file = new File(['{"key": "value"}'], 'test.json', {
	type: 'application/json',
});

describe('ImportStructureModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders the modal header and warning message', () => {
		renderComponent();

		expect(
			screen.getByText('import-and-override-structure')
		).toBeInTheDocument();
		expect(
			screen.getByText('import-and-override-structure-warning-message')
		).toBeInTheDocument();
	});

	it('renders the json file field', () => {
		renderComponent();

		expect(screen.getByLabelText('json-file')).toBeInTheDocument();
	});

	it('renders the cancel and import-and-override buttons', () => {
		renderComponent();

		expect(screen.getByText('cancel')).toBeInTheDocument();

		const importButton = screen.getByText('import-and-override');

		expect(importButton).toBeInTheDocument();
		expect(importButton).toBeDisabled();
	});

	it('enables the import-and-override button when a json file is selected', async () => {
		const {container} = renderComponent();

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		fireEvent.change(input, {
			target: {files: [file]},
		});

		await waitFor(() => {
			expect(screen.getByText('import-and-override')).toBeEnabled();
		});
	});

	it('calls postFormData when import-and-override button is clicked', async () => {
		const {container} = renderComponent();

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		fireEvent.change(input, {
			target: {files: [file]},
		});

		await waitFor(() => {
			expect(screen.getByText('import-and-override')).toBeEnabled();
		});

		mockPostFormData.mockResolvedValue({success: true});

		fireEvent.click(screen.getByText('import-and-override'));

		await waitFor(() => {
			expect(mockPostFormData).toHaveBeenCalledTimes(1);
		});

		expect(mockCloseModal).toHaveBeenCalledTimes(1);
		expect(mockLoadData).toHaveBeenCalledTimes(1);
	});

	it('shows error message when postFormData returns an error', async () => {
		const {container} = renderComponent();

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		fireEvent.change(input, {
			target: {files: [file]},
		});

		const importButton = screen.getByText('import-and-override');

		await waitFor(() => {
			expect(importButton).toBeEnabled();
		});

		mockPostFormData.mockResolvedValue({
			errorMessage: 'Error importing structure',
			success: false,
		});

		fireEvent.click(importButton);

		await waitFor(() => {
			expect(
				screen.getByText('Error importing structure')
			).toBeInTheDocument();

			expect(importButton).toBeDisabled();
		});
	});
});
