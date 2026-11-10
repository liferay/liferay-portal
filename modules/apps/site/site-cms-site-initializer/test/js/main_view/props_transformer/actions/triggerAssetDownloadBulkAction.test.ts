/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

import {triggerAssetDownloadBulkAction} from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/triggerAssetDownloadBulkAction';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	fetch: jest.fn(),
	sub: jest.fn((str: string) => str),
}));

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/utils/downloadBlob',
	() => ({downloadBlob: jest.fn()})
);

const OBJECT_ENTRY_CLASS_NAME = 'com.liferay.object.model.ObjectEntry';

const fileItem = (id: number) => ({
	embedded: {file: {}},
	entryClassName: OBJECT_ENTRY_CLASS_NAME,
	id,
});

const folderItem = (id: number) => ({id});

const contentAssetItem = (id: number) => ({
	entryClassName: OBJECT_ENTRY_CLASS_NAME,
	id,
});

const ERROR_MESSAGE =
	'unable-to-process-the-bulk-download.-please-check-your-selection-and-try-again';

const WARNING_MESSAGE =
	'you-have-selected-both-content-and-file-assets.-only-file-assets-can-be-downloaded.-content-assets-will-be-skipped';

describe('triggerAssetDownloadBulkAction', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(fetch as jest.Mock).mockResolvedValue({ok: true} as Response);
	});

	it('downloads a folder-only selection without aborting', async () => {
		await triggerAssetDownloadBulkAction({
			selectedData: {items: [folderItem(1)], selectAll: false},
			type: 'DownloadBulkAction',
		} as any);

		expect(fetch).toHaveBeenCalled();
		expect(openToast).not.toHaveBeenCalledWith(
			expect.objectContaining({message: ERROR_MESSAGE})
		);
	});

	it('blocks a selection with only non-downloadable content assets', async () => {
		await triggerAssetDownloadBulkAction({
			selectedData: {items: [contentAssetItem(1)], selectAll: false},
			type: 'DownloadBulkAction',
		} as any);

		expect(fetch).not.toHaveBeenCalled();
		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({message: ERROR_MESSAGE, type: 'danger'})
		);
	});

	it('warns but still downloads when a folder is mixed with a content asset', async () => {
		await triggerAssetDownloadBulkAction({
			selectedData: {
				items: [folderItem(1), contentAssetItem(2)],
				selectAll: false,
			},
			type: 'DownloadBulkAction',
		} as any);

		expect(fetch).toHaveBeenCalled();
		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: WARNING_MESSAGE,
				type: 'warning',
			})
		);
	});

	it('downloads an all-files selection without warning', async () => {
		await triggerAssetDownloadBulkAction({
			selectedData: {items: [fileItem(1), fileItem(2)], selectAll: false},
			type: 'DownloadBulkAction',
		} as any);

		expect(fetch).toHaveBeenCalled();
		expect(openToast).not.toHaveBeenCalledWith(
			expect.objectContaining({message: WARNING_MESSAGE})
		);
	});
});
