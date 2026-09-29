/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import MultipleFileUploader from '../../src/main/resources/META-INF/resources/multiple_file_uploader/MultipleFileUploader';
import {UploadBatchesCallback} from '../../src/main/resources/META-INF/resources/multiple_file_uploader/types';

jest.mock('frontend-js-web', () => ({
	formatStorage: (str: string) => `${str} MB`,
	sub: (str: string, arg: string) => str.replace('x', arg),
}));

const mockCloseModal = jest.fn();
const mockUploadComplete = jest.fn();
const mockUploadRequest = jest.fn().mockResolvedValue({error: false});

const DEFAULT_PROPS = {
	onModalClose: mockCloseModal,
	onUploadComplete: mockUploadComplete,
	scopeSelectorElement: <p>test scope selector</p>,
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
		expect(getByText('test scope selector')).toBeInTheDocument();
	});

	it('adds dropped files to the list and shows footer', async () => {
		const user = userEvent.setup();

		const {container, findByText, getByRole, getByText} = render(
			<MultipleFileUploader {...DEFAULT_PROPS} />
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file = createFile('image1.png', 2048);

		await user.upload(input, file);

		expect(await findByText('image1.png')).toBeInTheDocument();
		expect(getByText('2 KB')).toBeInTheDocument();

		const uploadButton = getByRole('button', {
			name: 'upload-(1)',
		});
		expect(uploadButton).toBeInTheDocument();
		expect(getByRole('button', {name: 'cancel'})).toBeInTheDocument();
	});

	it('prevents to add duplicated files', async () => {
		const user = userEvent.setup();

		const {container, findByText, getByRole} = render(
			<MultipleFileUploader {...DEFAULT_PROPS} />
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file1 = createFile('image1.png', 1024);
		const file2 = createFile('image1.png', 2048);
		const file3 = createFile('image2.png', 2048);

		await user.upload(input, file1);

		expect(await findByText('image1.png')).toBeInTheDocument();

		await user.upload(input, [file2, file3]);

		expect(await findByText('image2.png')).toBeInTheDocument();

		const uploadButton = getByRole('button', {name: 'upload-(2)'});
		expect(uploadButton).toBeInTheDocument();
	});

	it('removes a file when clicking the remove button', async () => {
		const user = userEvent.setup();

		const {container} = render(<MultipleFileUploader {...DEFAULT_PROPS} />);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file = createFile('to-remove.png', 1024);

		await user.upload(input, file);

		expect(await screen.findByText('to-remove.png')).toBeInTheDocument();

		const removeBtn = screen.getByRole('button', {name: 'remove-file'});
		await user.click(removeBtn);

		await waitFor(() => {
			expect(screen.queryByText('to-remove.png')).not.toBeInTheDocument();
		});
	});

	it('calls closeModal when Cancel button is clicked', async () => {
		const user = userEvent.setup();

		const {container} = render(<MultipleFileUploader {...DEFAULT_PROPS} />);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file = createFile('example.png', 2048);

		await user.upload(input, file);

		expect(await screen.findByText('example.png')).toBeInTheDocument();

		const cancelButton = screen.getByRole('button', {
			name: /cancel/i,
		});
		await user.click(cancelButton);

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

	it('runs formValidation while submitting the form and stop when not valid', async () => {
		const user = userEvent.setup();
		const mockFormValidation = jest.fn().mockResolvedValue(false);

		const {container} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				formValidation={mockFormValidation}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.png', 1024);

		await user.upload(input, file1);

		expect(await screen.findByText('upload1.png')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		await user.click(uploadButton);

		expect(mockFormValidation).toHaveBeenCalledTimes(1);
		expect(mockUploadRequest).not.toHaveBeenCalled();
		expect(mockUploadComplete).not.toHaveBeenCalled();
	});

	it('runs formValidation while submitting the form and continue when valid', async () => {
		const user = userEvent.setup();
		const mockFormValidation = jest.fn().mockResolvedValue(true);
		const mockUploadRequestSuccess = jest.fn().mockResolvedValue({});

		const {container} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				formValidation={mockFormValidation}
				uploadRequest={mockUploadRequestSuccess}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.png', 1024);

		await user.upload(input, file1);

		expect(await screen.findByText('upload1.png')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		await user.click(uploadButton);

		expect(mockFormValidation).toHaveBeenCalledTimes(1);
		expect(mockUploadRequestSuccess).toHaveBeenCalledTimes(1);
		expect(mockUploadComplete).toHaveBeenCalledTimes(1);
		expect(mockUploadComplete).toHaveBeenCalledWith({
			failedFiles: [],
			successFiles: ['upload1.png'],
		});
	});

	it('submits the files and calls onUploadComplete', async () => {
		const user = userEvent.setup();
		const mockUploadRequestSuccess = jest.fn().mockResolvedValue({});

		const {container} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				uploadRequest={mockUploadRequestSuccess}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.png', 1024);
		const file2 = createFile('upload2.png', 2048);

		await user.upload(input, [file1, file2]);

		expect(await screen.findByText('upload2.png')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		await user.click(uploadButton);

		expect(mockUploadRequestSuccess).toHaveBeenCalledTimes(2);
		expect(mockUploadComplete).toHaveBeenCalledWith({
			failedFiles: [],
			successFiles: ['upload1.png', 'upload2.png'],
		});
	});

	it('shows files that failed to upload', async () => {
		const user = userEvent.setup();

		const mockUploadRequestFail = jest
			.fn()
			.mockResolvedValue({error: 'failed to upload'});

		const {container, getByText} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				uploadRequest={mockUploadRequestFail}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.png', 1024);
		const file2 = createFile('upload2.png', 2048);

		await user.upload(input, [file1, file2]);

		expect(await screen.findByText('upload2.png')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		await user.click(uploadButton);

		expect(mockUploadRequestFail).toHaveBeenCalledTimes(2);
		expect(getByText('2-files-could-not-be-uploaded')).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'upload-another-file'})
		).toBeInTheDocument();
	});

	it('shows the error of a request that rejects', async () => {
		const user = userEvent.setup();

		const mockUploadRequestReject = jest
			.fn()
			.mockRejectedValue(new Error('network down'));

		const {container} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				uploadRequest={mockUploadRequestReject}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		await user.upload(input, [createFile('upload1.png', 1024)]);

		expect(await screen.findByText('upload1.png')).toBeInTheDocument();

		await user.click(screen.getByRole('button', {name: /upload/i}));

		expect(
			await screen.findByText('1-files-could-not-be-uploaded')
		).toBeInTheDocument();
		expect(screen.getByText('network down')).toBeInTheDocument();
		expect(mockUploadComplete).toHaveBeenCalledWith({
			failedFiles: ['upload1.png'],
			successFiles: [],
		});
	});

	it('shows the error of a request that throws synchronously', async () => {
		const user = userEvent.setup();

		const mockUploadRequestThrow = jest.fn().mockImplementation(() => {
			throw new Error('no group');
		});

		const {container} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				uploadRequest={mockUploadRequestThrow}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		await user.upload(input, [createFile('upload1.png', 1024)]);

		expect(await screen.findByText('upload1.png')).toBeInTheDocument();

		await user.click(screen.getByRole('button', {name: /upload/i}));

		expect(
			await screen.findByText('1-files-could-not-be-uploaded')
		).toBeInTheDocument();
		expect(screen.getByText('no group')).toBeInTheDocument();
		expect(mockUploadComplete).toHaveBeenCalledWith({
			failedFiles: ['upload1.png'],
			successFiles: [],
		});
	});

	it('can show multiple errors per request', async () => {
		const user = userEvent.setup();

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
				uploadRequest={mockUploadRequestFail}
			/>
		);

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;
		const file1 = createFile('upload1.zip', 1024);

		await user.upload(input, [file1]);

		expect(await screen.findByText('upload1.zip')).toBeInTheDocument();

		const uploadButton = screen.getByRole('button', {name: /upload/i});
		await user.click(uploadButton);

		expect(mockUploadRequestFail).toHaveBeenCalledTimes(1);
		expect(getByText('2-files-could-not-be-uploaded')).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'upload-another-file'})
		).toBeInTheDocument();
	});

	it('can show custom button label and messages', async () => {
		const user = userEvent.setup();

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

		await user.upload(input, file);

		expect(await findByText('image1.png')).toBeInTheDocument();

		expect(getByRole('button', {name: 'Import'})).toBeInTheDocument();

		expect(getByText('files-to-import')).toBeInTheDocument();
	});

	it('filters initial files to upload by extension', async () => {
		const file1 = createFile('image1.png', 1024);
		const file2 = createFile('document1.pdf', 2048);

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

		const {findByText, getByRole, getByText} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				filesToUpload={[file1Data, file2Data]}
				validExtensions=".png"
			/>
		);

		expect(await findByText('image1.png')).toBeInTheDocument();

		expect(
			getByText('please-enter-a-file-with-a-valid-e.pngtension-x')
		).toBeInTheDocument();

		expect(getByRole('button', {name: 'upload-(1)'})).toBeInTheDocument();
	});

	it('filters initial files to upload by max size', async () => {
		const file1 = createFile('image1.png', 1024);
		const file2 = createFile('image2.png', 3072);

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

		const {findByText, getByRole, getByText} = render(
			<MultipleFileUploader
				{...DEFAULT_PROPS}
				filesToUpload={[file1Data, file2Data]}
				maxFileSize={2000}
				validExtensions=".png"
			/>
		);

		expect(await findByText('image1.png')).toBeInTheDocument();

		expect(
			getByText(
				'please-enter-a-file-with-a-valid-file-size-no-larger-than-2000 MB'
			)
		).toBeInTheDocument();

		expect(getByRole('button', {name: 'upload-(1)'})).toBeInTheDocument();
	});

	describe('batch upload', () => {
		it('uploads all files in parallel by default', async () => {
			const started: string[] = [];
			let resolveFirst!: () => void;

			const mockBatchRequest = jest.fn().mockImplementation(
				({fileData}: {fileData: {name: string}}) =>
					new Promise<{error: boolean}>((resolve) => {
						started.push(fileData.name);

						if (fileData.name === 'A.pdf') {
							resolveFirst = () => resolve({error: false});
						}
						else {
							resolve({error: false});
						}
					})
			);

			const {container} = render(
				<MultipleFileUploader
					{...DEFAULT_PROPS}
					uploadRequest={mockBatchRequest}
				/>
			);

			const input =
				container.querySelector<HTMLInputElement>(
					'input[type="file"]'
				)!;

			fireEvent.change(input, {
				target: {
					files: [
						createFile('A.pdf', 1024),
						createFile('A.txt', 1024),
					],
				},
			});

			expect(await screen.findByText('A.pdf')).toBeInTheDocument();

			fireEvent.click(screen.getByRole('button', {name: /upload/i}));

			await waitFor(() => expect(started).toContain('A.pdf'));

			expect(started).toContain('A.txt');

			resolveFirst();

			await waitFor(() => expect(mockUploadComplete).toHaveBeenCalled());
		});

		it('uploads files sequentially when uploadBatches returns one file per batch', async () => {
			const callOrder: string[] = [];
			let resolveFirstBatch!: () => void;

			const mockBatchRequest = jest.fn().mockImplementation(
				({fileData}: {fileData: {name: string}}) =>
					new Promise<{error: boolean}>((resolve) => {
						callOrder.push(fileData.name);

						if (fileData.name === 'A.pdf') {
							resolveFirstBatch = () => resolve({error: false});
						}
						else {
							resolve({error: false});
						}
					})
			);

			const sequentialUploadBatches: UploadBatchesCallback = (files) =>
				files.map((file) => [file]);

			const {container} = render(
				<MultipleFileUploader
					{...DEFAULT_PROPS}
					uploadBatches={sequentialUploadBatches}
					uploadRequest={mockBatchRequest}
				/>
			);

			const input =
				container.querySelector<HTMLInputElement>(
					'input[type="file"]'
				)!;

			fireEvent.change(input, {
				target: {
					files: [
						createFile('A.pdf', 1024),
						createFile('B.pdf', 1024),
					],
				},
			});

			expect(await screen.findByText('A.pdf')).toBeInTheDocument();

			fireEvent.click(screen.getByRole('button', {name: /upload/i}));

			await waitFor(() => expect(callOrder).toContain('A.pdf'));

			expect(callOrder).not.toContain('B.pdf');

			resolveFirstBatch();

			await waitFor(() => expect(callOrder).toContain('B.pdf'));

			await waitFor(() => expect(mockUploadComplete).toHaveBeenCalled());

			expect(mockUploadComplete).toHaveBeenCalledTimes(1);
			expect(mockUploadComplete).toHaveBeenCalledWith({
				failedFiles: [],
				successFiles: ['A.pdf', 'B.pdf'],
			});
		});

		it('reports a failure that follows a success once every batch finishes', async () => {
			const user = userEvent.setup();

			const mockBatchRequest = jest
				.fn()
				.mockImplementation(({fileData}: {fileData: {name: string}}) =>
					Promise.resolve(
						fileData.name === 'B.pdf'
							? {error: 'failed to upload'}
							: {}
					)
				);

			const sequentialUploadBatches: UploadBatchesCallback = (files) =>
				files.map((file) => [file]);

			const {container} = render(
				<MultipleFileUploader
					{...DEFAULT_PROPS}
					uploadBatches={sequentialUploadBatches}
					uploadRequest={mockBatchRequest}
				/>
			);

			const input =
				container.querySelector<HTMLInputElement>(
					'input[type="file"]'
				)!;

			await user.upload(input, [
				createFile('A.pdf', 1024),
				createFile('B.pdf', 1024),
			]);

			expect(await screen.findByText('B.pdf')).toBeInTheDocument();

			await user.click(screen.getByRole('button', {name: /upload/i}));

			expect(
				await screen.findByText('1-files-could-not-be-uploaded')
			).toBeInTheDocument();
			expect(screen.getByText('B.pdf')).toBeInTheDocument();
			expect(screen.getByText('failed to upload')).toBeInTheDocument();

			expect(mockUploadComplete).toHaveBeenCalledTimes(1);
			expect(mockUploadComplete).toHaveBeenCalledWith({
				failedFiles: ['B.pdf'],
				successFiles: ['A.pdf'],
			});
		});
	});
});
