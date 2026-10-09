/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';
import {fetch} from 'frontend-js-web';

import {openCMSModal} from '../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import RecycleBinFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/RecycleBinFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	replaceTokens: jest.fn(),
}));

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	getCMSItemSelectorGroupedFilters: jest.fn(() => []),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: jest.fn()})
);

const mockFetch = fetch as jest.Mock;

const ITEM_DATA = {
	actions: {
		delete: {
			href: '/o/cms/basic-documents/1',
			method: 'DELETE',
		},
	},
	embedded: {
		externalReferenceCode: 'FILE_ERC',
		id: 1,
		title: 'File',
	},
	entryClassName: 'com.liferay.object.model.ObjectDefinition#1',
	title: 'File',
};

function getTransformerProps(brokenLinksCheckerEnabled: boolean) {
	return RecycleBinFDSPropsTransformer({
		additionalProps: {brokenLinksCheckerEnabled},
		apiURL: '/o/search/v1.0/search',
		id: 'com.liferay.site.cms.site.initializer-recycleBinSection',
		itemsActions: [{data: {id: 'delete'}}],
	} as any);
}

function mockBulkActionItemPreviewResponse(usages: number) {
	mockFetch.mockResolvedValueOnce({
		json: async () => ({
			items: [
				{
					attributes: {
						deletionType: 'PERMANENT_DELETION',
						type: 'ASSET',
						usages,
					},
					classPK: 1,
					name: 'File',
				},
			],
			totalCount: 1,
		}),
		ok: true,
		status: 200,
	} as Response);
}

describe('RecycleBinFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('marks the delete item action with the danger class', () => {
		const [deleteAction, restoreAction] = RecycleBinFDSPropsTransformer({
			additionalProps: {},
			itemsActions: [{data: {id: 'delete'}}, {data: {id: 'restore'}}],
		} as any).itemsActions;

		expect(deleteAction.className).toBe('text-danger');
		expect(restoreAction.className).toBeUndefined();
	});

	it('warns before permanently deleting an item used in other assets', async () => {
		mockBulkActionItemPreviewResponse(1);

		await getTransformerProps(true).onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			itemData: ITEM_DATA,
			loadData: jest.fn(),
		} as any);

		await waitFor(() => expect(openCMSModal).toHaveBeenCalledTimes(1));

		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining('/o/bulk/v1.0/bulk-action-item/preview'),
			expect.anything()
		);
		expect(openCMSModal).toHaveBeenCalledWith(
			expect.objectContaining({
				contentComponent: expect.any(Function),
				status: 'danger',
			})
		);
	});

	it('asks for confirmation when the item is not used in other assets', async () => {
		mockBulkActionItemPreviewResponse(0);

		await getTransformerProps(true).onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			itemData: ITEM_DATA,
			loadData: jest.fn(),
		} as any);

		await waitFor(() => expect(openCMSModal).toHaveBeenCalledTimes(1));

		expect(openCMSModal).toHaveBeenCalledWith(
			expect.objectContaining({
				bodyHTML:
					'you-are-about-to-permanently-delete-x-this-action-cannot-be-undone',
			})
		);
	});

	it('skips the usage check when the broken links checker is disabled', async () => {
		await getTransformerProps(false).onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			itemData: ITEM_DATA,
			loadData: jest.fn(),
		} as any);

		expect(mockFetch).not.toHaveBeenCalled();
		expect(openCMSModal).toHaveBeenCalledWith(
			expect.objectContaining({
				bodyHTML:
					'you-are-about-to-permanently-delete-x-this-action-cannot-be-undone',
			})
		);
	});

	it('warns before permanently deleting selected items used in other assets', async () => {
		mockBulkActionItemPreviewResponse(1);

		await getTransformerProps(true).onBulkActionItemClick({
			action: {data: {id: 'delete'}},
			selectedData: {items: [ITEM_DATA], selectAll: false},
		});

		await waitFor(() => expect(openCMSModal).toHaveBeenCalledTimes(1));

		expect(openCMSModal).toHaveBeenCalledWith(
			expect.objectContaining({
				contentComponent: expect.any(Function),
				status: 'danger',
			})
		);
	});
});
