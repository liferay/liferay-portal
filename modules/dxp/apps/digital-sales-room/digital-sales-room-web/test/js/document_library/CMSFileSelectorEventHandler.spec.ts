/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import CMSFileSelectorEventHandler from '../../../src/main/resources/META-INF/resources/js/document_library/CMSFileSelectorEventHandler';

const mockOpenCMSFileSelectorModal = jest.fn();
const mockDisplayErrorToast = jest.fn();
const mockDisplaySuccessToast = jest.fn();

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	openCMSFileSelectorModal: (...args: unknown[]) =>
		mockOpenCMSFileSelectorModal(...args),
}));

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/utils/toastUtil',
	() => ({
		displayErrorToast: (...args: unknown[]) =>
			mockDisplayErrorToast(...args),
		displaySuccessToast: (...args: unknown[]) =>
			mockDisplaySuccessToast(...args),
	})
);

const flushPromises = () => new Promise((resolve) => setTimeout(resolve, 0));

describe('CMSFileSelectorEventHandler', () => {
	const mockDetach = jest.fn();
	const mockFetch = jest.fn();
	const mockOn = jest.fn();

	beforeEach(() => {
		jest.clearAllMocks();

		(globalThis as any).Liferay = {
			...globalThis.Liferay,
			ThemeDisplay: {
				...(globalThis.Liferay?.ThemeDisplay || {}),
				getSiteGroupId: () => 12345,
			},
			Util: {
				...(globalThis.Liferay?.Util || {}),
				fetch: mockFetch,
			},
			detach: mockDetach,
			on: mockOn,
		};

		delete (window as any).location;

		(window as any).location = {href: 'http://localhost/current-page'};
	});

	it('detaches previous listener before registering a new one', () => {
		CMSFileSelectorEventHandler();

		expect(mockDetach).toHaveBeenCalledWith(
			'openCMSFileSelector',
			expect.any(Function)
		);
		expect(mockOn).toHaveBeenCalledWith(
			'openCMSFileSelector',
			expect.any(Function)
		);

		const detachOrder = mockDetach.mock.invocationCallOrder[0];
		const onOrder = mockOn.mock.invocationCallOrder[0];

		expect(detachOrder).toBeLessThan(onOrder);
	});

	it('detaches event listener on dispose', () => {
		const {dispose} = CMSFileSelectorEventHandler();

		dispose();

		expect(mockDetach).toHaveBeenCalledTimes(2);
		expect(mockDetach).toHaveBeenLastCalledWith(
			'openCMSFileSelector',
			expect.any(Function)
		);
	});

	it('opens CMS file selector modal with correct params', () => {
		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({
			data: {
				folderId: 100,
				groupId: 200,
				redirect: 'http://localhost/redirect',
			},
		});

		expect(mockOpenCMSFileSelectorModal).toHaveBeenCalledWith({
			allowDragAndDrop: false,
			config: {
				multiSelect: true,
			},
			folderMemoryKey: 'cms-file-selector-document-library:200',
			groupId: 200,
			onSelect: expect.any(Function),
		});
	});

	it('does nothing when onSelect receives empty items', () => {
		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({data: {groupId: 200}});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([]);

		expect(mockFetch).not.toHaveBeenCalled();

		onSelect(null);

		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('uploads to document folder when folderId is provided', async () => {
		const mockBlob = new Blob(['test']);

		mockFetch
			.mockResolvedValueOnce({blob: () => Promise.resolve(mockBlob)})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({id: 1}),
				ok: true,
			});

		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({
			data: {
				folderId: 100,
				groupId: 200,
				redirect: 'http://localhost/redirect',
			},
		});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file.pdf'},
						name: 'file.pdf',
					},
				},
				title: 'File Title',
			},
		]);

		await flushPromises();

		expect(mockFetch).toHaveBeenCalledWith('http://localhost/file.pdf');

		expect(mockFetch).toHaveBeenCalledWith(
			'/o/headless-delivery/v1.0/document-folders/100/documents',
			expect.objectContaining({
				body: expect.any(FormData),
				method: 'POST',
			})
		);
	});

	it('uploads to site root when folderId is 0', async () => {
		const mockBlob = new Blob(['test']);

		mockFetch
			.mockResolvedValueOnce({blob: () => Promise.resolve(mockBlob)})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({id: 1}),
				ok: true,
			});

		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({
			data: {
				groupId: 200,
				redirect: 'http://localhost/redirect',
			},
		});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file.pdf'},
						name: 'file.pdf',
					},
				},
			},
		]);

		await flushPromises();

		expect(mockFetch).toHaveBeenCalledWith(
			'/o/headless-delivery/v1.0/sites/12345/documents',
			expect.objectContaining({
				body: expect.any(FormData),
				method: 'POST',
			})
		);
	});

	it('shows success toast and redirects after successful upload', async () => {
		const mockBlob = new Blob(['test']);

		mockFetch
			.mockResolvedValueOnce({blob: () => Promise.resolve(mockBlob)})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({id: 1}),
				ok: true,
			});

		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({
			data: {
				folderId: 100,
				groupId: 200,
				redirect: 'http://localhost/redirect',
			},
		});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file.pdf'},
						name: 'file.pdf',
					},
				},
			},
		]);

		await flushPromises();

		expect(mockDisplaySuccessToast).toHaveBeenCalled();
		expect(window.location.href).toBe('http://localhost/redirect');
	});

	it('shows error toast when upload fails', async () => {
		const mockBlob = new Blob(['test']);

		mockFetch
			.mockResolvedValueOnce({blob: () => Promise.resolve(mockBlob)})
			.mockResolvedValueOnce({
				ok: false,
			});

		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({
			data: {
				folderId: 100,
				groupId: 200,
				redirect: 'http://localhost/redirect',
			},
		});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file.pdf'},
						name: 'file.pdf',
					},
				},
			},
		]);

		await flushPromises();

		expect(mockDisplayErrorToast).toHaveBeenCalled();
		expect(mockDisplaySuccessToast).not.toHaveBeenCalled();
	});

	it('skips items without file link', async () => {
		const mockBlob = new Blob(['test']);

		mockFetch
			.mockResolvedValueOnce({blob: () => Promise.resolve(mockBlob)})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({id: 1}),
				ok: true,
			});

		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({
			data: {
				folderId: 100,
				groupId: 200,
				redirect: 'http://localhost/redirect',
			},
		});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([
			{title: 'No Link Item'},
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file.pdf'},
						name: 'file.pdf',
					},
				},
				title: 'Has Link Item',
			},
		]);

		await flushPromises();

		expect(mockFetch).toHaveBeenCalledTimes(2);
		expect(mockFetch).toHaveBeenCalledWith('http://localhost/file.pdf');
	});

	it('uploads multiple files in parallel', async () => {
		const mockBlob = new Blob(['test']);

		mockFetch.mockImplementation((url: string) => {
			if (url.includes('localhost')) {
				return Promise.resolve({
					blob: () => Promise.resolve(mockBlob),
				});
			}

			return Promise.resolve({
				json: () => Promise.resolve({id: 1}),
				ok: true,
			});
		});

		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({
			data: {
				folderId: 100,
				groupId: 200,
				redirect: 'http://localhost/redirect',
			},
		});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file1.pdf'},
						name: 'file1.pdf',
					},
				},
			},
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file2.pdf'},
						name: 'file2.pdf',
					},
				},
			},
		]);

		await flushPromises();

		expect(mockFetch).toHaveBeenCalledTimes(4);
		expect(mockDisplaySuccessToast).toHaveBeenCalled();
	});

	it('uses current page URL as default redirect', async () => {
		const mockBlob = new Blob(['test']);

		mockFetch
			.mockResolvedValueOnce({blob: () => Promise.resolve(mockBlob)})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({id: 1}),
				ok: true,
			});

		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({data: {groupId: 200}});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file.pdf'},
						name: 'file.pdf',
					},
				},
			},
		]);

		await flushPromises();

		expect(window.location.href).toBe('http://localhost/current-page');
	});

	it('fires the fileEntrySaved event on file upload', async () => {
		const mockBlob = new Blob(['test']);

		mockFetch
			.mockResolvedValueOnce({blob: () => Promise.resolve(mockBlob)})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({fileName: 'file.pdf', id: 1}),
				ok: true,
			});

		const liferayFireSpy = jest.spyOn(Liferay, 'fire');

		CMSFileSelectorEventHandler();

		const eventHandler = mockOn.mock.calls[0][1];

		eventHandler({
			data: {
				folderId: 100,
				groupId: Liferay.ThemeDisplay.getScopeGroupId(),
				redirect: 'http://localhost/redirect',
			},
		});

		const {onSelect} = mockOpenCMSFileSelectorModal.mock.calls[0][0];

		onSelect([
			{
				embedded: {
					file: {
						link: {href: 'http://localhost/file.pdf'},
						name: 'file.pdf',
					},
				},
			},
		]);

		await flushPromises();

		expect(liferayFireSpy).toHaveBeenCalledWith(
			'fileEntrySaved',
			expect.objectContaining({
				fileEntryId: 1,
				fileName: 'file.pdf',
				groupId: Liferay.ThemeDisplay.getScopeGroupId(),
			})
		);
	});
});
