/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import MultipleFileUploader from '../../../../src/main/resources/META-INF/resources/js/main/components/MultipleFileUploader';

jest.mock('frontend-js-web', () => ({
	sub: (str: string, arg: string) => str.replace('x', arg),
}));

global.Liferay.Util.formatStorage = jest.fn((size: number) => {
	return `${size / 1024} KB`;
});

const mockCloseModal = jest.fn();

const DEFAULT_PROPS = {
	assetLibraries: [
		{groupId: 123, name: 'Library A'},
		{groupId: 456, name: 'Library B'},
	],
	onModalClose: mockCloseModal,
	onUploadComplete: jest.fn(),
	parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
};

const createFile = (name: string, size: number, type = 'image/png') => {
	return new File(['a'.repeat(size)], name, {type});
};

describe('MultipleFileUploader', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the component', () => {
		const {getByRole, getByText} = render(
			<MultipleFileUploader {...DEFAULT_PROPS} />
		);

		expect(getByRole('button', {name: 'select-files'})).toBeInTheDocument();
		expect(getByText('space')).toBeInTheDocument();
		expect(getByRole('combobox')).toBeInTheDocument();
	});

	it('adds dropped files to the list and shows footer', async () => {
		const {container, findByText, getByRole, getByText} = render(
			<MultipleFileUploader {...DEFAULT_PROPS} />
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file = createFile('image1.png', 2048);

		fireEvent.change(input, {
			target: {files: [file]},
		});

		expect(await findByText('image1.png')).toBeInTheDocument();
		expect(getByText('2 KB')).toBeInTheDocument();

		const uploadButton = getByRole('button', {
			name: 'upload-(1)',
		});
		expect(uploadButton).toBeInTheDocument();
		expect(getByRole('button', {name: 'cancel'})).toBeInTheDocument();
	});

	it('prevents to add duplicated files', async () => {
		const {container, findByText, getByRole} = render(
			<MultipleFileUploader {...DEFAULT_PROPS} />
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file1 = createFile('image1.png', 1024);
		const file2 = createFile('image1.png', 2048);
		const file3 = createFile('image2.png', 2048);

		fireEvent.change(input, {
			target: {files: [file1]},
		});

		expect(await findByText('image1.png')).toBeInTheDocument();

		fireEvent.change(input, {
			target: {files: [file2, file3]},
		});

		expect(await findByText('image2.png')).toBeInTheDocument();

		const uploadButton = getByRole('button', {
			name: 'upload-(2)',
		});
		expect(uploadButton).toBeInTheDocument();
	});

	it('removes a file when clicking the remove button', async () => {
		const {container} = render(<MultipleFileUploader {...DEFAULT_PROPS} />);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file = createFile('to-remove.png', 1024);

		fireEvent.change(input, {
			target: {files: [file]},
		});

		expect(await screen.findByText('to-remove.png')).toBeInTheDocument();

		const removeBtn = screen.getByLabelText('remove-file');
		fireEvent.click(removeBtn);

		await waitFor(() => {
			expect(screen.queryByText('to-remove.png')).not.toBeInTheDocument();
		});
	});

	it('calls closeModal when Cancel button is clicked', async () => {
		const {container} = render(<MultipleFileUploader {...DEFAULT_PROPS} />);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file = createFile('example.png', 2048);

		fireEvent.change(input, {
			target: {files: [file]},
		});

		expect(await screen.findByText('example.png')).toBeInTheDocument();

		const cancelButton = screen.getByRole('button', {
			name: /cancel/i,
		});
		fireEvent.click(cancelButton);

		expect(mockCloseModal).toHaveBeenCalled();
	});
});
