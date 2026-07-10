/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../src/main/resources/META-INF/resources/js/ProductsFDSPropsTransformer';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	ACTIONS: {},
	AuthorRenderer: () => null,
	SpaceRendererWithCache: () => null,
	addOnClickToCreationMenuItems: (primaryItems) => primaryItems,
	getScopeExternalReferenceCode: () => '',
}));

describe('ProductsFDSPropsTransformer', () => {
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
});
