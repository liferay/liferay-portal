/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DesignLibraryAssetsFDSPropsTransformer from '../../../src/main/resources/META-INF/resources/js/props_transformer/DesignLibraryAssetsFDSPropsTransformer';

const mockOpenModal = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openModal: (...args: any[]) => mockOpenModal(...args),
	openToast: jest.fn(),
}));

const BASE_PROPS = {
	bulkActions: [
		{data: {id: 'delete'}, icon: 'trash', label: 'delete'},
		{data: {id: 'export'}, icon: 'export', label: 'export'},
	],
	id: 'fds-design-library-resources',
	items: [],
} as any;

const DELETABLE_ITEM = {
	actions: {
		delete: {
			href: '/o/headless-admin-site/v1.0/design-libraries/library-erc/style-books/style-book-erc',
			method: 'DELETE',
		},
	},
	embedded: {externalReferenceCode: 'style-book-erc', name: 'Style Book'},
};

const READ_ONLY_ITEM = {
	actions: {},
	embedded: {externalReferenceCode: 'read-only-erc', name: 'Read Only'},
};

function getBulkAction(id: string) {
	const {bulkActions} = DesignLibraryAssetsFDSPropsTransformer(BASE_PROPS);

	return (bulkActions as Array<any>).find(
		(bulkAction) => bulkAction.data.id === id
	);
}

describe('DesignLibraryAssetsFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('builds the creation menu from the design asset creation items', () => {
		const {creationMenu} = DesignLibraryAssetsFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				addStyleBookEntryURL: '/style-book',
				canAddStyleBook: true,
			},
		});

		expect(creationMenu?.primaryItems.map((item) => item.label)).toEqual([
			'new-style-book',
		]);
	});

	it('adds an empty creation menu when creation is not allowed', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer(BASE_PROPS).creationMenu
				?.primaryItems
		).toEqual([]);
	});

	it('exposes the table view', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer(BASE_PROPS).views?.map(
				(view) => view.name
			)
		).toEqual(['table']);
	});

	it('enables the delete bulk action when every selected asset can be deleted', () => {
		expect(
			getBulkAction('delete').isDisabled({
				allItemsSelectedActive: false,
				selectedItems: [DELETABLE_ITEM, DELETABLE_ITEM],
			})
		).toBe(false);
	});

	it('disables the delete bulk action when a selected asset cannot be deleted', () => {
		expect(
			getBulkAction('delete').isDisabled({
				allItemsSelectedActive: false,
				selectedItems: [DELETABLE_ITEM, READ_ONLY_ITEM],
			})
		).toBe(true);
	});

	it('disables the delete bulk action when nothing is selected', () => {
		expect(
			getBulkAction('delete').isDisabled({
				allItemsSelectedActive: false,
				selectedItems: [],
			})
		).toBe(true);
	});

	it('disables the delete bulk action when the cross page selection is active', () => {
		expect(
			getBulkAction('delete').isDisabled({
				allItemsSelectedActive: true,
				selectedItems: [DELETABLE_ITEM],
			})
		).toBe(true);
	});

	it('leaves the other bulk actions untouched', () => {
		expect(getBulkAction('export').isDisabled).toBeUndefined();
	});

	it('confirms the deletion of the selected design assets', () => {
		DesignLibraryAssetsFDSPropsTransformer(
			BASE_PROPS
		).onBulkActionItemClick({
			action: {data: {id: 'delete'}},
			loadData: jest.fn(),
			selectedData: {items: [DELETABLE_ITEM, DELETABLE_ITEM]},
		});

		expect(mockOpenModal).toHaveBeenCalledTimes(1);
	});

	it('ignores bulk actions other than delete', () => {
		DesignLibraryAssetsFDSPropsTransformer(
			BASE_PROPS
		).onBulkActionItemClick({
			action: {data: {id: 'export'}},
			loadData: jest.fn(),
			selectedData: {items: [DELETABLE_ITEM]},
		});

		expect(mockOpenModal).not.toHaveBeenCalled();
	});
});
