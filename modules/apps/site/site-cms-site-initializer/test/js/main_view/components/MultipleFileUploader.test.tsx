/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import MultipleFileUploader from '../../../../src/main/resources/META-INF/resources/js/main_view/multiple_file_uploader/MultipleFileUploader';

jest.mock('frontend-js-web', () => ({
	sub: (str: string, arg: string) => str.replace('x', arg),
}));

const mockCloseModal = jest.fn();
const mockUploadComplete = jest.fn();
const mockUploadRequest = jest.fn().mockResolvedValue({error: false});

const DEFAULT_PROPS = {
	assetLibraries: [
		{externalReferenceCode: 'erc-1', groupId: 123, name: 'Library A'},
		{externalReferenceCode: 'erc-2', groupId: 456, name: 'Library B'},
	],
	onModalClose: mockCloseModal,
	onUploadComplete: mockUploadComplete,
	uploadRequest: mockUploadRequest,
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

	it('can accept files as props and shows footer', async () => {
		const file1 = createFile('image1.png', 1024);
		const file2 = createFile('image2.png', 2048);

		const file1Data = {
			file: file1,
			name: file1.name,
			size: file1.size,
		};

		const file2Data = {
			file: file2,
			name: file2.name,
			size: file2.size,
		};

		const {findByText, getByRole} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				filesToUpload={[file1Data, file2Data]}
			/>
		);

		expect(await findByText('image1.png')).toBeInTheDocument();
		expect(await findByText('image2.png')).toBeInTheDocument();

		expect(getByRole('button', {name: 'upload-(2)'})).toBeInTheDocument();
		expect(getByRole('button', {name: 'cancel'})).toBeInTheDocument();
	});

	it('checks the accessibility of the multiple file uploader', async () => {
		const {container} = render(<MultipleFileUploader {...DEFAULT_PROPS} />);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('space is required before submitting the form', async () => {
		const {container, getByText} = render(
			<MultipleFileUploader {...DEFAULT_PROPS} />
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.png', 1024);

		fireEvent.change(input, {target: {files: [file1]}});

		expect(await screen.findByText('upload1.png')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		fireEvent.click(uploadButton);

		await waitFor(() => {
			expect(getByText('this-field-is-required')).toBeVisible();

			expect(mockUploadRequest).not.toHaveBeenCalled();
		});
	});

	it('submits the files and calls onUploadComplete', async () => {
		const {container} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				assetLibraries={[
					{
						externalReferenceCode: 'erc-1',
						groupId: 2,
						name: 'Library A',
					},
				]}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.png', 1024);
		const file2 = createFile('upload2.png', 2048);

		fireEvent.change(input, {target: {files: [file1, file2]}});

		expect(await screen.findByText('upload2.png')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		fireEvent.click(uploadButton);

		await waitFor(() => {
			expect(mockUploadRequest).toHaveBeenCalledTimes(2);

			expect(mockUploadComplete).toHaveBeenCalledWith({
				assetLibrary: expect.objectContaining({
					externalReferenceCode: 'erc-1',
					groupId: 2,
					name: 'Library A',
				}),
				failedFiles: [],
				successFiles: ['upload1.png', 'upload2.png'],
			});
		});
	});

	it('shows files that failed to upload', async () => {
		const mockUploadRequestFail = jest
			.fn()
			.mockResolvedValue({error: true});

		const {container, getByText} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				assetLibraries={[
					{
						externalReferenceCode: 'erc-2',
						groupId: 2,
						name: 'Library A',
					},
				]}
				uploadRequest={mockUploadRequestFail}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.png', 1024);
		const file2 = createFile('upload2.png', 2048);

		fireEvent.change(input, {target: {files: [file1, file2]}});

		expect(await screen.findByText('upload2.png')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		fireEvent.click(uploadButton);

		await waitFor(() => {
			expect(mockUploadRequestFail).toHaveBeenCalledTimes(2);
			expect(
				getByText('2-files-could-not-be-uploaded')
			).toBeInTheDocument();
			expect(
				screen.getByRole('button', {name: 'upload-another-file'})
			).toBeInTheDocument();
		});
	});

	it('can show multiple errors per request', async () => {
		const mockUploadRequestFail = jest.fn().mockResolvedValue({
			errors: [
				{errorMessage: 'first error message', name: 'file1.xlf'},
				{errorMessage: 'second error message', name: 'file2.xlf'},
			],
			multipleErrors: true,
		});

		const {container, getByText} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				assetLibraries={[
					{
						externalReferenceCode: 'erc-2',
						groupId: 2,
						name: 'Library A',
					},
				]}
				uploadRequest={mockUploadRequestFail}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.zip', 1024);

		fireEvent.change(input, {target: {files: [file1]}});

		expect(await screen.findByText('upload1.zip')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		fireEvent.click(uploadButton);

		await waitFor(() => {
			expect(mockUploadRequestFail).toHaveBeenCalledTimes(1);
			expect(
				getByText('2-files-could-not-be-uploaded')
			).toBeInTheDocument();
			expect(
				screen.getByRole('button', {name: 'upload-another-file'})
			).toBeInTheDocument();
		});
	});

	it('can show custom button label and messages', async () => {
		const {container, findByText, getByRole, getByText} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				buttonLabel="Import"
				description="This is a custom description for import translation"
				messages={{filesToUpload: 'files-to-import'}}
			/>
		);

		expect(
			getByText('This is a custom description for import translation')
		).toBeInTheDocument();

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file = createFile('image1.png', 2048);

		fireEvent.change(input, {
			target: {files: [file]},
		});

		expect(await findByText('image1.png')).toBeInTheDocument();

		expect(
			getByRole('button', {
				name: 'Import',
			})
		).toBeInTheDocument();

		expect(getByText('files-to-import')).toBeInTheDocument();
	});
});
