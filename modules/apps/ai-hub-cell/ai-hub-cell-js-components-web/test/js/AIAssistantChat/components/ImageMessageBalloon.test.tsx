/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import {fetch} from 'frontend-js-web';
import React from 'react';

import ImageMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/ImageMessageBalloon';

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

const IMAGE_ONE = 'data:image/png;base64,one';
const IMAGE_TWO = 'data:image/png;base64,two';

function response() {
	return {
		json: () => Promise.resolve({id: 1, title: 'AI-image.png'}),
		ok: true,
	};
}

function lastPostBody() {
	const postCall = mockFetch.mock.calls.find(
		([, init]) => (init as RequestInit)?.method === 'POST'
	);

	return JSON.parse((postCall?.[1] as RequestInit).body as string);
}

describe('ImageMessageBalloon', () => {
	let originalDataTransfer: typeof DataTransfer;

	beforeAll(() => {
		originalDataTransfer = (global as {DataTransfer?: typeof DataTransfer})
			.DataTransfer as typeof DataTransfer;

		(global as {DataTransfer?: unknown}).DataTransfer = class {
			items = {
				_files: [] as File[],
				add(file: File) {
					this._files.push(file);
				},
			};

			get files() {
				return this.items._files;
			}
		};
	});

	afterAll(() => {
		(global as {DataTransfer?: unknown}).DataTransfer =
			originalDataTransfer;
	});

	function appendFileUploadField() {
		const field = document.createElement('div');

		field.dataset.aiAssistantFieldId = '';
		field.innerHTML = '<input class="file-upload-input" type="file" />';

		document.body.appendChild(field);

		const fileInput = field.querySelector(
			'.file-upload-input'
		) as HTMLInputElement;

		Object.defineProperty(fileInput, 'files', {
			value: null,
			writable: true,
		});

		return fileInput;
	}

	beforeEach(() => {
		mockFetch.mockReset();
		mockFetch.mockResolvedValue(response() as never);
		(Liferay.Util.openToast as jest.Mock).mockClear();
	});

	afterEach(() => {
		document
			.querySelectorAll('[data-ai-assistant-field-id]')
			.forEach((element) => element.remove());
	});

	it('renders a single generated image', () => {
		render(<ImageMessageBalloon images={[IMAGE_ONE]} />);

		expect(screen.getByAltText('generated-image')).toHaveAttribute(
			'src',
			IMAGE_ONE
		);
	});

	it('saves the single image to the group basic-documents endpoint when the save button is clicked', async () => {
		render(
			<ImageMessageBalloon
				images={[IMAGE_ONE]}
				saveProps={{groupId: 123}}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'save-image'}));

		await waitFor(() =>
			expect(mockFetch).toHaveBeenCalledWith(
				'/o/cms/basic-documents/scopes/123',
				expect.objectContaining({method: 'POST'})
			)
		);

		expect(lastPostBody().file.fileBase64).toBe('one');
	});

	it('selects every image by default and saves only the images still selected', async () => {
		render(
			<ImageMessageBalloon
				images={[IMAGE_ONE, IMAGE_TWO]}
				saveProps={{groupId: 123}}
			/>
		);

		const checkboxes = screen.getAllByRole('checkbox', {
			name: 'generated-image',
		});

		expect(checkboxes).toHaveLength(2);
		expect(checkboxes[0]).toBeChecked();
		expect(checkboxes[1]).toBeChecked();

		fireEvent.click(checkboxes[1]);

		expect(checkboxes[1]).not.toBeChecked();

		fireEvent.click(screen.getByRole('button', {name: 'save-image'}));

		await waitFor(() =>
			expect(
				mockFetch.mock.calls.filter(
					([, init]) => (init as RequestInit)?.method === 'POST'
				)
			).toHaveLength(1)
		);

		expect(lastPostBody().file.fileBase64).toBe('one');
	});

	it('injects the generated image into the file-upload field matched by the selector', () => {
		const fileInput = appendFileUploadField();

		render(
			<ImageMessageBalloon
				images={[IMAGE_ONE]}
				saveProps={{
					fileUploadSelector: '[data-ai-assistant-field-id]',
					groupId: 123,
				}}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'save-image'}));

		expect(fileInput.files).toHaveLength(1);
		expect((fileInput.files as unknown as File[])[0].name).toMatch(
			/^AI-image-.*\.png$/
		);

		expect(
			mockFetch.mock.calls.some(
				([, init]) => (init as RequestInit)?.method === 'POST'
			)
		).toBe(false);
	});

	it('injects the first image into the field and saves the remaining images to Files', async () => {
		const fileInput = appendFileUploadField();

		render(
			<ImageMessageBalloon
				images={[IMAGE_ONE, IMAGE_TWO]}
				saveProps={{
					fileUploadSelector: '[data-ai-assistant-field-id]',
					groupId: 123,
				}}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'save-images'}));

		expect(fileInput.files).toHaveLength(1);

		await waitFor(() =>
			expect(
				mockFetch.mock.calls.filter(
					([, init]) => (init as RequestInit)?.method === 'POST'
				)
			).toHaveLength(1)
		);

		expect(lastPostBody().file.fileBase64).toBe('two');

		expect(Liferay.Util.openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'info'})
		);
	});

	it('falls back to the group save when the selector matches no field', async () => {
		render(
			<ImageMessageBalloon
				images={[IMAGE_ONE]}
				saveProps={{
					fileUploadSelector: '[data-ai-assistant-field-id]',
					groupId: 123,
				}}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'save-image'}));

		await waitFor(() =>
			expect(mockFetch).toHaveBeenCalledWith(
				'/o/cms/basic-documents/scopes/123',
				expect.objectContaining({method: 'POST'})
			)
		);
	});

	it('shows an error toast when the save request fails', async () => {
		mockFetch.mockResolvedValue({ok: false, statusText: 'Boom'} as never);

		render(
			<ImageMessageBalloon
				images={[IMAGE_ONE]}
				saveProps={{groupId: 123}}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'save-image'}));

		await waitFor(() =>
			expect(Liferay.Util.openToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			)
		);
	});

	it('disables the save button when no image is selected', () => {
		render(
			<ImageMessageBalloon
				images={[IMAGE_ONE, IMAGE_TWO]}
				saveProps={{groupId: 123}}
			/>
		);

		const checkboxes = screen.getAllByRole('checkbox', {
			name: 'generated-image',
		});

		fireEvent.click(checkboxes[0]);
		fireEvent.click(checkboxes[1]);

		expect(screen.getByRole('button', {name: 'save-image'})).toBeDisabled();
	});

	it('disables the save button and the image selection while saving', async () => {
		let release: () => void = () => {};

		const gate = new Promise<void>((resolve) => {
			release = resolve;
		});

		mockFetch.mockImplementation(
			() => gate.then(() => response()) as never
		);

		render(
			<ImageMessageBalloon
				images={[IMAGE_ONE, IMAGE_TWO]}
				saveProps={{groupId: 123}}
			/>
		);

		const saveButton = screen.getByRole('button', {name: 'save-images'});

		fireEvent.click(saveButton);

		await waitFor(() => expect(saveButton).toBeDisabled());

		expect(saveButton).toHaveTextContent('saving');

		screen
			.getAllByRole('checkbox', {name: 'generated-image'})
			.forEach((checkbox) => expect(checkbox).toBeDisabled());

		await act(async () => {
			release();
		});

		await waitFor(() => expect(saveButton).toBeEnabled());
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<ImageMessageBalloon images={[IMAGE_ONE, IMAGE_TWO]} />
		);

		await checkAccessibility({context: container});
	});
});
