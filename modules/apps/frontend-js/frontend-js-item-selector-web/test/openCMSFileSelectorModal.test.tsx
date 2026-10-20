/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';

import {
	getLastBreadcrumbFolders,
	setLastBreadcrumbFolders,
} from '../src/main/resources/META-INF/resources/item_selector/lastBreadcrumbFoldersMemory';
import openCMSFileSelectorModal from '../src/main/resources/META-INF/resources/item_selector/openCMSFileSelectorModal';

const FOLDER_MEMORY_KEY = 'test-field';

const fileItem = {
	embedded: {id: 1, title: 'File'},
	id: 1,
};

function folderResponse(status: {code: number} = {code: 0}) {
	return Promise.resolve({
		json: () => Promise.resolve({id: 555, status, title: 'Folder'}),
		ok: true,
	});
}

function jsonResponse(items: object[]) {
	return Promise.resolve({
		json: () =>
			Promise.resolve({
				items,
				lastPage: 1,
				page: 1,
				totalCount: items.length,
			}),
		ok: true,
	});
}

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn(),
}));

const mockedFetch = fetch as jest.Mock;

function getDecodedSearchURLs() {
	return mockedFetch.mock.calls
		.map(([url]) =>
			decodeURIComponent(url?.toString?.() ?? '').replace(/\+/g, ' ')
		)
		.filter((url) => url.includes('/o/search/v1.0/search'));
}

describe('openCMSFileSelectorModal', () => {
	let root: {unmount?: () => void} | undefined;

	function openModal(...args: Parameters<typeof openCMSFileSelectorModal>) {
		root = openCMSFileSelectorModal(...args) as {unmount?: () => void};

		return root;
	}

	beforeAll(() => {
		Object.assign(Liferay.ThemeDisplay, {
			isImpersonated: () => false,
		});
	});

	beforeEach(() => {
		setLastBreadcrumbFolders(FOLDER_MEMORY_KEY, null);

		mockedFetch.mockImplementation(() => jsonResponse([fileItem]));
	});

	afterEach(() => {
		root?.unmount?.();
		root = undefined;

		document.body.innerHTML = '';

		jest.clearAllMocks();
	});

	afterAll(() => {
		Object.assign(Liferay.ThemeDisplay, {
			isImpersonated: undefined,
		});
	});

	it('renders cards and table options in the view-toggle picker', async () => {
		const user = userEvent.setup();

		openModal({
			groupId: 123,
			onSelect: jest.fn(),
		});

		const dialog = await screen.findByRole('dialog');

		const viewToggle = await within(dialog).findByRole('combobox', {
			name: 'x-view-selected',
		});

		await user.click(viewToggle);

		const options = await screen.findAllByRole('option');

		expect(options).toHaveLength(2);

		expect(options[0]).toHaveTextContent('cards');

		expect(options[1]).toHaveTextContent('table');
	});

	it('opens without a group and lists the files', async () => {
		openModal({
			onSelect: jest.fn(),
		});

		await screen.findByRole('dialog');

		await waitFor(() => {
			expect(getDecodedSearchURLs().length).toBeGreaterThan(0);
		});

		expect(
			mockedFetch.mock.calls.every(
				([url]) => !url?.toString?.().includes('undefined')
			)
		).toBe(true);
	});

	it('fetches with a folder-inclusive root filter', async () => {
		openModal({
			groupId: 123,
			onSelect: jest.fn(),
		});

		await screen.findByRole('dialog');

		await waitFor(() => {
			expect(getDecodedSearchURLs().length).toBeGreaterThan(0);
		});

		const [firstURL] = getDecodedSearchURLs();

		expect(firstURL).toContain("(cmsSection eq 'files')");

		expect(firstURL).toContain('(rootDescendantNode eq false)');

		expect(firstURL).not.toContain(
			"objectDefinitionExternalReferenceCode ne 'L_OBJECT_ENTRY_FOLDER'"
		);

		expect(firstURL).not.toContain('folderId eq ');
	});

	it("widens the extension filter so folders pass through with objectDefinitionExternalReferenceCode eq 'L_OBJECT_ENTRY_FOLDER'", async () => {
		openModal({
			allowedExtensions: 'jpg,png',
			groupId: 123,
			onSelect: jest.fn(),
		});

		await screen.findByRole('dialog');

		await waitFor(() => {
			expect(getDecodedSearchURLs().length).toBeGreaterThan(0);
		});

		const [firstURL] = getDecodedSearchURLs();

		expect(firstURL).toContain("extension in ('jpg','png')");

		expect(firstURL).toContain(
			"objectDefinitionExternalReferenceCode eq 'L_OBJECT_ENTRY_FOLDER'"
		);
	});

	it('reopens at the folder browsed when the previous selection was confirmed', async () => {
		setLastBreadcrumbFolders(FOLDER_MEMORY_KEY, [
			{id: null, label: 'Files'},
			{id: 555, label: 'Folder', scopeId: 1},
		]);

		mockedFetch.mockImplementation((url) => {
			if (url.toString().includes('/object-entry-folders/555')) {
				return folderResponse();
			}

			return jsonResponse([fileItem]);
		});

		openModal({
			folderMemoryKey: FOLDER_MEMORY_KEY,
			groupId: 123,
			onSelect: jest.fn(),
		});

		await screen.findByRole('dialog');

		await waitFor(() => {
			expect(getDecodedSearchURLs().length).toBeGreaterThan(0);
		});

		expect(
			getDecodedSearchURLs().every((url) =>
				url.includes('folderId eq 555')
			)
		).toBe(true);
	});

	it('reopens at the root when the remembered folder was deleted', async () => {
		setLastBreadcrumbFolders(FOLDER_MEMORY_KEY, [
			{id: null, label: 'Files'},
			{id: 555, label: 'Folder', scopeId: 1},
		]);

		mockedFetch.mockImplementation((url) => {
			if (url.toString().includes('/object-entry-folders/555')) {
				return Promise.resolve({ok: false, status: 404});
			}

			return jsonResponse([fileItem]);
		});

		openModal({
			folderMemoryKey: FOLDER_MEMORY_KEY,
			groupId: 123,
			onSelect: jest.fn(),
		});

		await screen.findByRole('dialog');

		await waitFor(() => {
			expect(getLastBreadcrumbFolders(FOLDER_MEMORY_KEY)).toBeNull();
		});

		await waitFor(() => {
			expect(
				getDecodedSearchURLs().some(
					(url) => !url.includes('folderId eq ')
				)
			).toBe(true);
		});

		expect(
			mockedFetch.mock.calls.some(([url]) =>
				url?.toString?.().includes('/object-entry-folders/555')
			)
		).toBe(true);
	});

	it('keeps the remembered folder when the validation request fails transiently', async () => {
		setLastBreadcrumbFolders(FOLDER_MEMORY_KEY, [
			{id: null, label: 'Files'},
			{id: 555, label: 'Folder', scopeId: 1},
		]);

		mockedFetch.mockImplementation((url) => {
			if (url.toString().includes('/object-entry-folders/555')) {
				return Promise.resolve({ok: false, status: 500});
			}

			return jsonResponse([fileItem]);
		});

		openModal({
			folderMemoryKey: FOLDER_MEMORY_KEY,
			groupId: 123,
			onSelect: jest.fn(),
		});

		await screen.findByRole('dialog');

		await waitFor(() => {
			expect(getDecodedSearchURLs().length).toBeGreaterThan(0);
		});

		expect(getLastBreadcrumbFolders(FOLDER_MEMORY_KEY)).not.toBeNull();

		expect(
			getDecodedSearchURLs().every((url) =>
				url.includes('folderId eq 555')
			)
		).toBe(true);
	});

	it('treats a trashed remembered folder as deleted and reopens at the root', async () => {
		setLastBreadcrumbFolders(FOLDER_MEMORY_KEY, [
			{id: null, label: 'Files'},
			{id: 555, label: 'Folder', scopeId: 1},
		]);

		mockedFetch.mockImplementation((url) => {
			if (url.toString().includes('/object-entry-folders/555')) {
				return folderResponse({code: 8});
			}

			return jsonResponse([fileItem]);
		});

		openModal({
			folderMemoryKey: FOLDER_MEMORY_KEY,
			groupId: 123,
			onSelect: jest.fn(),
		});

		await screen.findByRole('dialog');

		await waitFor(() => {
			expect(getLastBreadcrumbFolders(FOLDER_MEMORY_KEY)).toBeNull();
		});
	});

	it('does not restore a folder remembered by a different field', async () => {
		setLastBreadcrumbFolders(FOLDER_MEMORY_KEY, [
			{id: null, label: 'Files'},
			{id: 555, label: 'Folder', scopeId: 1},
		]);

		openModal({
			folderMemoryKey: 'another-field',
			groupId: 123,
			onSelect: jest.fn(),
		});

		await screen.findByRole('dialog');

		await waitFor(() => {
			expect(getDecodedSearchURLs().length).toBeGreaterThan(0);
		});

		expect(
			getDecodedSearchURLs().every((url) => !url.includes('folderId eq '))
		).toBe(true);
	});
});
