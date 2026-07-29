/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../src/main/resources/META-INF/resources/js/ProductsFDSPropsTransformer';

const mockDeleteAssetEntriesBulkAction = jest.fn();

jest.mock('@liferay/site-cms-site-initializer', () => ({
	ACTIONS: {},
	AuthorRenderer: () => null,
	SpaceRendererWithCache: () => null,
	addOnClickToCreationMenuItems: (primaryItems) => primaryItems,
	deleteAssetEntriesBulkAction: (...args) =>
		mockDeleteAssetEntriesBulkAction(...args),
	getScopeExternalReferenceCode: () => '',
	transformFDSBulkActions: (bulkActions) =>
		bulkActions.map((bulkAction) => ({...bulkAction, transformed: true})),
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
