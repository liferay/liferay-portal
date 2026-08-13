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

const STYLE_BOOK_RESOURCE_TYPE = {
	color: 'purple',
	defaultActionId: 'edit',
	entryClassName: 'com.liferay.style.book.model.StyleBookEntry',
	key: 'style-book',
	label: 'Style Book',
	symbol: 'book',
};

const FRAGMENT_RESOURCE_TYPE = {
	color: 'pink',
	defaultActionId: 'view',
	entryClassName: 'com.liferay.fragment.model.FragmentCollection',
	key: 'fragment',
	label: 'Fragment Set',
	symbol: 'cards2',
};

function creationItem(id: string, label: string) {
	return {
		id,
		label,
		module: `http://localhost/${id}`,
		moduleProps: {},
	};
}

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

	it('builds the creation menu from the contributed creation items', () => {
		const {creationMenu} = DesignLibraryAssetsFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				resourceTypes: [
					{
						...STYLE_BOOK_RESOURCE_TYPE,
						creationItems: [
							creationItem('add-style-book', 'new-style-book'),
						],
					},
				],
			},
		});

		expect(creationMenu?.primaryItems.map((item) => item.label)).toEqual([
			'new-style-book',
		]);
	});

	it('orders the creation menu by contributor', () => {
		const {creationMenu} = DesignLibraryAssetsFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				resourceTypes: [
					{
						...STYLE_BOOK_RESOURCE_TYPE,
						creationItems: [
							creationItem('add-style-book', 'new-style-book'),
						],
					},
					{
						...FRAGMENT_RESOURCE_TYPE,
						creationItems: [
							creationItem(
								'add-basic-fragment',
								'new-basic-fragment'
							),
							creationItem(
								'add-fragment-set',
								'new-fragment-set'
							),
						],
					},
				],
			},
		});

		expect(creationMenu?.primaryItems.map((item) => item.label)).toEqual([
			'new-style-book',
			'new-basic-fragment',
			'new-fragment-set',
		]);
	});

	it('omits the creation menu when no type contributes creation items', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer({
				...BASE_PROPS,
				additionalProps: {
					resourceTypes: [STYLE_BOOK_RESOURCE_TYPE],
				},
			}).creationMenu
		).toBeUndefined();
	});

	it('omits the creation menu when there are no resource types', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer(BASE_PROPS).creationMenu
		).toBeUndefined();
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
