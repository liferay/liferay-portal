/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DesignLibraryAdminFDSPropsTransformer from '../../../src/main/resources/META-INF/resources/js/props_transformer/DesignLibraryAdminFDSPropsTransformer';

const mockOpenModal = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openModal: (...args: any[]) => mockOpenModal(...args),
	openToast: jest.fn(),
}));

const BASE_PROPS = {
	additionalProps: {},
	bulkActions: [
		{data: {id: 'delete'}, icon: 'trash', label: 'delete'},
		{data: {id: 'export'}, icon: 'export', label: 'export'},
	],
	id: 'fds-design-libraries',
	items: [],
} as any;

const DELETABLE_ITEM = {
	actions: {delete: {href: '/design-libraries/1', method: 'DELETE'}},
	name: 'Deletable',
};

const READ_ONLY_ITEM = {actions: {}, name: 'Read Only'};

function getBulkAction(id: string) {
	const {bulkActions} = DesignLibraryAdminFDSPropsTransformer(BASE_PROPS);

	return (bulkActions as Array<any>).find(
		(bulkAction) => bulkAction.data.id === id
	);
}

describe('DesignLibraryAdminFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('enables the delete bulk action when every selected item can be deleted', () => {
		expect(
			getBulkAction('delete').isDisabled({
				allItemsSelectedActive: false,
				selectedItems: [DELETABLE_ITEM, DELETABLE_ITEM],
			})
		).toBe(false);
	});

	it('disables the delete bulk action when a selected item cannot be deleted', () => {
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

	it('confirms the deletion of the selected design libraries', () => {
		DesignLibraryAdminFDSPropsTransformer(BASE_PROPS).onBulkActionItemClick(
			{
				action: {data: {id: 'delete'}},
				loadData: jest.fn(),
				selectedData: {items: [DELETABLE_ITEM, DELETABLE_ITEM]},
			}
		);

		expect(mockOpenModal).toHaveBeenCalledTimes(1);
	});

	it('ignores bulk actions other than delete', () => {
		DesignLibraryAdminFDSPropsTransformer(BASE_PROPS).onBulkActionItemClick(
			{
				action: {data: {id: 'export'}},
				loadData: jest.fn(),
				selectedData: {items: [DELETABLE_ITEM]},
			}
		);

		expect(mockOpenModal).not.toHaveBeenCalled();
	});

	it('confirms the deletion of a single design library from the row actions', () => {
		DesignLibraryAdminFDSPropsTransformer(
			BASE_PROPS
		).onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			event: {preventDefault: jest.fn()},
			itemData: DELETABLE_ITEM,
		});

		expect(mockOpenModal).toHaveBeenCalledTimes(1);
	});
});
