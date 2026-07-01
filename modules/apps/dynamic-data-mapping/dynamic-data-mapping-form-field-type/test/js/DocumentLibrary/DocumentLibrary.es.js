/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import {PageProvider} from 'data-engine-js-components-web';
import {openSelectionModal} from 'frontend-js-components-web';
import React from 'react';

import DocumentLibrary from '../../../src/main/resources/META-INF/resources/js/DocumentLibrary/DocumentLibrary.es';

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openSelectionModal: jest.fn(),
}));

const globalLanguageDirection = Liferay.Language.direction;

const spritemap = 'icons.svg';

const fileEntryDeleteURL = 'http://test.local/delete-file-entry';
const guestUploadURL = 'http://test.local/upload-file-entry';

const defaultDocumentLibraryConfig = {
	name: 'uploadField',
	spritemap,
};

const valueWithFileEntry = (id) =>
	JSON.stringify({
		fileEntryId: id,
		groupId: 1,
		title: `file-${id}`,
		uuid: `uuid-${id}`,
	});

const formDataValue = (formData, key) => {
	if (!formData || typeof formData.entries !== 'function') {
		return null;
	}

	for (const [entryKey, entryValue] of formData.entries()) {
		if (entryKey === key || entryKey.endsWith(key)) {
			return entryValue;
		}
	}

	return null;
};

const DocumentLibraryWithProvider = (props) => (
	<PageProvider value={{editingLanguageId: 'en_US'}}>
		<DocumentLibrary {...props} />
	</PageProvider>
);

describe('Field DocumentLibrary', () => {

	// eslint-disable-next-line no-console
	const originalWarn = console.warn;

	afterAll(() => {

		// eslint-disable-next-line no-console
		console.warn = originalWarn;

		Liferay.Language.direction = globalLanguageDirection;
	});

	afterEach(cleanup);

	beforeAll(() => {

		// eslint-disable-next-line no-console
		console.warn = (...args) => {
			if (/DataProvider: Trying/.test(args[0])) {
				return;
			}
			originalWarn.call(console, ...args);
		};

		Liferay.Language.direction = {
			en_US: 'rtl',
		};
	});

	beforeEach(() => {
		jest.useFakeTimers();
		fetch.mockResponseOnce(JSON.stringify({}));

		Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);
	});

	it('disables guest upload field if maximumSubmissionLimitReached property is true', () => {
		const mockIsSignedIn = jest.fn();

		Liferay.ThemeDisplay.isSignedIn = mockIsSignedIn;

		render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				allowGuestUsers={true}
				maximumSubmissionLimitReached={true}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const guestUploadFieldInput = document.getElementById(
			'uploadFieldinputFileGuestUpload'
		);

		expect(guestUploadFieldInput.disabled).toBeTruthy();

		const guestUploadFieldInputLabel =
			document.querySelector('.select-button');

		expect(guestUploadFieldInputLabel.classList).toContain('disabled');
	});

	it('does not have aria-invalid attribute on first render when it is required', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				required={true}
			/>
		);

		const button = container.querySelector('button[aria-required="true"]');

		expect(button.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				required={true}
				value='{"id":"123"}'
			/>
		);

		const button = container.querySelector('button[aria-required="true"]');

		expect(button.hasAttribute('aria-invalid')).toBe(false);
	});

	it('falls back to Liferay.Language.get for the select label when the strings prop is not provided', () => {
		Liferay.ThemeDisplay.isSignedIn = jest.fn(() => true);

		const {getByText} = render(
			<DocumentLibraryWithProvider {...defaultDocumentLibraryConfig} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(getByText('select')).toBeInTheDocument();
	});

	it('has a helptext', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				tip="Type something"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a label', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				label="label"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a placeholder', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				placeholder="Placeholder"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a spritemap', () => {
		const {container} = render(
			<DocumentLibraryWithProvider {...defaultDocumentLibraryConfig} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has a value', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				value='{"id":"123"}'
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('has an id', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				id="ID"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('hide guest upload field if allowGuestUsers property is disabled', () => {
		const mockIsSignedIn = jest.fn();

		Liferay.ThemeDisplay.isSignedIn = mockIsSignedIn;

		render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				allowGuestUsers={false}
				value='{"id":"123"}'
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const guestUploadFieldInput = document.getElementById(
			'uploadFieldinputFileGuestUpload'
		);

		expect(guestUploadFieldInput).toBe(null);
	});

	it('is not readOnly', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				readOnly={false}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is not required', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				required={false}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('is readOnly', () => {
		render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				readOnly={true}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const uploadFieldInput = document.getElementById(
			'uploadFieldinputFile'
		);

		expect(uploadFieldInput.disabled).toBeTruthy();

		const uploadFieldInputSelectButton =
			document.querySelector('.select-button');

		expect(uploadFieldInputSelectButton.disabled).toBeTruthy();
	});

	it('is readOnly when allowed for guest users', () => {
		const mockIsSignedIn = jest.fn();

		Liferay.ThemeDisplay.isSignedIn = mockIsSignedIn;

		render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				allowGuestUsers={true}
				readOnly={true}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const guestUploadFieldInput = document.getElementById(
			'uploadFieldinputFileGuestUpload'
		);

		expect(guestUploadFieldInput.disabled).toBeTruthy();

		const guestUploadFieldInputLabel =
			document.querySelector('.select-button');

		expect(guestUploadFieldInputLabel.classList).toContain('disabled');
	});

	it('renders Label if showLabel is true', () => {
		const {container} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				label="text"
				showLabel
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
	});

	it('renders the select label from the strings prop when provided', () => {
		Liferay.ThemeDisplay.isSignedIn = jest.fn(() => true);

		const {getByText} = render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				strings={{selectLabel: 'Selecionar'}}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(getByText('Selecionar')).toBeInTheDocument();
	});

	it('shows guest upload field if allowGuestUsers property is enabled', () => {
		const mockIsSignedIn = jest.fn();

		Liferay.ThemeDisplay.isSignedIn = mockIsSignedIn;

		render(
			<DocumentLibraryWithProvider
				{...defaultDocumentLibraryConfig}
				allowGuestUsers={true}
				value='{"id":"123"}'
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		const guestUploadFieldInput = document.getElementById(
			'uploadFieldinputFileGuestUpload'
		);

		expect(guestUploadFieldInput).not.toBe(null);
	});

	describe('file deletion lifecycle', () => {
		const originalSendBeacon = navigator.sendBeacon;
		const originalXMLHttpRequest = global.XMLHttpRequest;

		let beaconCalls;
		let xhrInstances;
		let eventBus;

		const renderField = (props = {}) =>
			render(
				<DocumentLibraryWithProvider
					{...defaultDocumentLibraryConfig}
					fileEntryDeleteURL={fileEntryDeleteURL}
					guestUploadURL={guestUploadURL}
					onBlur={jest.fn()}
					onChange={jest.fn()}
					onFocus={jest.fn()}
					{...props}
				/>
			);

		const triggerBeforeUnload = () => {
			act(() => {
				window.dispatchEvent(new Event('beforeunload'));
			});
		};

		const triggerBeforeNavigate = () => {
			act(() => {
				Liferay.fire('beforeNavigate');
			});
		};

		const fireGlobal = (name) => {
			act(() => {
				(eventBus.get(name) || [])
					.slice()
					.forEach((handler) => handler());
			});
		};

		const completeUpload = (newFileEntryId) => {
			const uploadXhr = xhrInstances.find(
				(instance) => instance.url === guestUploadURL
			);

			expect(uploadXhr).toBeDefined();

			uploadXhr.responseText = JSON.stringify({
				file: valueParsed(newFileEntryId),
				success: true,
			});

			uploadXhr.readyState = 4;

			const readyStateChangeHandler =
				uploadXhr.addEventListener.mock.calls.find(
					([name]) => name === 'readystatechange'
				)[1];

			act(() => readyStateChangeHandler({}));
		};

		const valueParsed = (id) => JSON.parse(valueWithFileEntry(id));

		const deleteCalls = () => {
			const xhrDeletes = xhrInstances
				.filter((instance) => instance.url === fileEntryDeleteURL)
				.map((instance) =>
					Number(
						formDataValue(
							instance.send.mock.calls[0]?.[0],
							'oldFileEntryId'
						)
					)
				);

			const beaconDeletes = beaconCalls
				.filter((call) => call.url === fileEntryDeleteURL)
				.map((call) =>
					Number(formDataValue(call.formData, 'oldFileEntryId'))
				);

			return [...xhrDeletes, ...beaconDeletes];
		};

		const expectDeleted = (...ids) =>
			expect(deleteCalls().sort()).toEqual([...ids].sort());

		const expectNoDeletes = () => expect(deleteCalls()).toEqual([]);

		const clickClear = () => {
			const clearButton = document.querySelector(
				'[aria-label="unselect-file"]'
			);

			expect(clearButton).not.toBe(null);

			act(() => {
				fireEvent.click(clearButton);
			});
		};

		const triggerGuestUpload = () => {
			const fileInput = document.getElementById(
				'uploadFieldinputFileGuestUpload'
			);

			expect(fileInput).not.toBe(null);

			act(() => {
				fireEvent.change(fileInput, {
					target: {
						files: [
							new File(['hello'], 'hello.txt', {
								type: 'text/plain',
							}),
						],
					},
				});
			});
		};

		const selectViaModal = (newFileEntryId) => {
			openSelectionModal.mockImplementation(({onSelect}) => {
				act(() =>
					onSelect({value: valueWithFileEntry(newFileEntryId)})
				);
			});

			const selectButton = document.getElementById('uploadField');

			expect(selectButton).not.toBe(null);

			act(() => {
				fireEvent.click(selectButton);
			});
		};

		beforeEach(() => {
			openSelectionModal.mockReset();

			beaconCalls = [];
			xhrInstances = [];

			navigator.sendBeacon = jest.fn((url, formData) => {
				beaconCalls.push({formData, url});

				return true;
			});

			global.XMLHttpRequest = jest.fn(() => {
				const instance = {
					addEventListener: jest.fn(),
					open: jest.fn(function (method, url) {
						this.url = url;
					}),
					readyState: 0,
					responseText: '',
					send: jest.fn(),
					upload: {addEventListener: jest.fn()},
					url: '',
				};

				xhrInstances.push(instance);

				return instance;
			});

			eventBus = new Map();

			Liferay.on.mockImplementation((name, handler) => {
				if (!eventBus.has(name)) {
					eventBus.set(name, []);
				}

				eventBus.get(name).push(handler);
			});

			Liferay.detach.mockImplementation((name, handler) => {
				if (handler) {
					const handlers = eventBus.get(name) || [];
					const index = handlers.indexOf(handler);

					if (index >= 0) {
						handlers.splice(index, 1);
					}
				}
				else {
					eventBus.delete(name);
				}
			});

			Liferay.fire.mockImplementation((name) => {
				(eventBus.get(name) || [])
					.slice()
					.forEach((handler) => handler());
			});

			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => true);
		});

		afterEach(() => {
			global.XMLHttpRequest = originalXMLHttpRequest;
			navigator.sendBeacon = originalSendBeacon;
		});

		it('cleans up intermediate replacements on unload after multiple replaces in edit mode', () => {
			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);

			renderField({
				allowGuestUsers: true,
				value: valueWithFileEntry(42),
			});

			triggerGuestUpload();
			completeUpload(88);
			triggerGuestUpload();
			completeUpload(99);
			triggerBeforeUnload();

			expectDeleted(88, 99);
		});

		it('deletes both the previous and current upload on new-entry abandon after replace', () => {
			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);

			renderField({allowGuestUsers: true, value: '{}'});

			triggerGuestUpload();
			completeUpload(88);
			triggerGuestUpload();
			completeUpload(99);
			triggerBeforeUnload();

			expectDeleted(88, 99);
		});

		it('deletes only the replacement on unload when replacing in edit mode', () => {
			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);

			renderField({
				allowGuestUsers: true,
				value: valueWithFileEntry(42),
			});

			triggerGuestUpload();
			completeUpload(99);
			triggerBeforeUnload();

			expectDeleted(99);
		});

		it('deletes the new entry session upload on abandon', () => {
			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);

			renderField({allowGuestUsers: true, value: '{}'});

			triggerGuestUpload();
			completeUpload(99);
			triggerBeforeUnload();

			expectDeleted(99);
		});

		it('deletes the original on Save after Clear', () => {
			renderField({value: valueWithFileEntry(42)});

			clickClear();
			fireGlobal('paginationControlsSubmitButtonClicked');

			expectDeleted(42);
		});

		it('deletes the previous on Save after Replace', () => {
			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);

			renderField({
				allowGuestUsers: true,
				value: valueWithFileEntry(42),
			});

			triggerGuestUpload();
			completeUpload(99);
			fireGlobal('paginationControlsSubmitButtonClicked');

			expectDeleted(42);
		});

		it('does not delete on unload when editing an existing entry without changes', () => {
			renderField({value: valueWithFileEntry(42)});

			triggerBeforeUnload();

			expectNoDeletes();
		});

		it('cleans up the orphan on SPA navigation away from a replaced entry', () => {
			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);

			renderField({
				allowGuestUsers: true,
				value: valueWithFileEntry(42),
			});

			triggerGuestUpload();
			completeUpload(99);
			triggerBeforeNavigate();

			expectDeleted(99);
		});

		it('does not delete on unload when read-only (LPP-63922)', () => {
			renderField({readOnly: true, value: valueWithFileEntry(42)});

			triggerBeforeUnload();

			expectNoDeletes();
		});

		it('does not delete the previous on Save when replaced via the document selector', () => {
			renderField({value: valueWithFileEntry(42)});

			selectViaModal(99);
			fireGlobal('paginationControlsSubmitButtonClicked');

			expectNoDeletes();
		});

		it('does not delete the uploaded file when the guest clicks the field before submitting (LPP-64664)', () => {
			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);

			renderField({allowGuestUsers: true, value: '{}'});

			triggerGuestUpload();

			completeUpload(99);

			act(() => {
				fireEvent.click(document.getElementById('uploadField'));
			});

			fireGlobal('paginationControlsSubmitButtonClicked');

			expectNoDeletes();
		});

		it('does not delete when Clear is followed by Cancel (LPP-63917)', () => {
			renderField({value: valueWithFileEntry(42)});

			clickClear();
			triggerBeforeUnload();

			expectNoDeletes();
		});

		it('preserves the original on Cancel after Replace and cleans the orphan on unload', () => {
			Liferay.ThemeDisplay.isSignedIn = jest.fn(() => false);

			renderField({
				allowGuestUsers: true,
				value: valueWithFileEntry(42),
			});

			triggerGuestUpload();
			completeUpload(99);
			triggerBeforeUnload();

			expectDeleted(99);
		});
	});
});
