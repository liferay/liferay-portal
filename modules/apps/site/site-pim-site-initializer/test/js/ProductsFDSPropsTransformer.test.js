/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../src/main/resources/META-INF/resources/js/ProductsFDSPropsTransformer';

const mockDeleteAssetEntriesBulkAction = jest.fn();
const mockDeleteItemAction = jest.fn();

jest.mock('@liferay/site-cms-site-initializer', () => ({
	ACTIONS: {},
	AuthorRenderer: () => null,
	SpaceRendererWithCache: () => null,
	StatusLabel: () => null,
	addOnClickToCreationMenuItems: (primaryItems) => primaryItems,
	deleteAssetEntriesBulkAction: (...args) =>
		mockDeleteAssetEntriesBulkAction(...args),
	deleteItemAction: (...args) => mockDeleteItemAction(...args),
	getFormattedLabel: (label) => `<strong>${label}</strong>`,
	getScopeExternalReferenceCode: () => '',
	styleDeleteAction: (action) =>
		action?.data?.id === 'delete'
			? {...action, className: 'text-danger'}
			: action,
	transformFDSBulkActions: (bulkActions) =>
		bulkActions.map((bulkAction) => ({...bulkAction, transformed: true})),
}));

jest.mock('frontend-js-web', () => ({
	sub: (template, value) => `${template}:${value}`,
}));

describe('ProductsFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('forces hideManagementBarInEmptyState to true and preserves the other props', () => {
		const result = propsTransformer({
			apiURL: '/o/search/v1.0/search',
			hideManagementBarInEmptyState: false,
			id: 'products',
		});

		expect(result.apiURL).toBe('/o/search/v1.0/search');
		expect(result.hideManagementBarInEmptyState).toBe(true);
		expect(result.id).toBe('products');
	});

	it('marks the delete action as text-danger and leaves the other actions unchanged', () => {
		const result = propsTransformer({
			itemsActions: [
				{data: {id: 'edit'}, icon: 'pencil'},
				{data: {id: 'delete'}, icon: 'trash'},
			],
		});

		expect(result.itemsActions[0].className).toBeUndefined();
		expect(result.itemsActions[1].className).toBe('text-danger');
	});

	it('gates the bulk actions behind the shared permission transform', () => {
		const result = propsTransformer({
			bulkActions: [{data: {id: 'delete'}, icon: 'trash'}],
		});

		expect(result.bulkActions).toEqual([
			{data: {id: 'delete'}, icon: 'trash', transformed: true},
		]);
	});

	it('deletes the selected products when the delete bulk action is clicked', () => {
		const result = propsTransformer({
			apiURL: '/o/search/v1.0/search',
			id: 'products',
		});

		const selectedData = {items: [{embedded: {id: 1}}], selectAll: false};

		result.onBulkActionItemClick({
			action: {data: {id: 'delete'}},
			selectedData,
		});

		expect(mockDeleteAssetEntriesBulkAction).toHaveBeenCalledWith({
			apiURL: '/o/search/v1.0/search',
			dataSetId: 'products',
			selectedData,
		});
	});

	it('deletes the product through the CMS delete action when the delete item action is clicked', async () => {
		const event = {preventDefault: jest.fn()};
		const itemData = {
			actions: {delete: {href: '/delete', method: 'delete'}},
			embedded: {id: 1, title: 'Shirt'},
		};
		const loadData = jest.fn();

		await propsTransformer({}).onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			event,
			itemData,
			loadData,
		});

		expect(event.preventDefault).toHaveBeenCalled();
		expect(mockDeleteItemAction).toHaveBeenCalledWith(
			'delete-asset-confirmation-body:<strong>Shirt</strong>',
			itemData,
			loadData
		);
	});

	it('ignores bulk actions other than delete', () => {
		const result = propsTransformer({
			apiURL: '/o/search/v1.0/search',
			id: 'products',
		});

		result.onBulkActionItemClick({
			action: {data: {id: 'expire'}},
			selectedData: {items: [], selectAll: false},
		});

		expect(mockDeleteAssetEntriesBulkAction).not.toHaveBeenCalled();
	});
});
