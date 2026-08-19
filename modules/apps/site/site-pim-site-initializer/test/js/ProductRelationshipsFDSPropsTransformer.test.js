/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../src/main/resources/META-INF/resources/js/ProductRelationshipsFDSPropsTransformer';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	addOnClickToCreationMenuItems: (items) =>
		items.map((item) => ({...item, onClick() {}})),
}));

jest.mock(
	'../../src/main/resources/META-INF/resources/js/openProductRelationshipSelectorModal',
	() => ({
		__esModule: true,
		default: jest.fn(),
	})
);

describe('ProductRelationshipsFDSPropsTransformer', () => {
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

	it('wires an onClick onto the creation menu primary items', () => {
		const result = propsTransformer({
			creationMenu: {
				primaryItems: [{data: {action: 'createProductRelationship'}}],
			},
		});

		expect(result.creationMenu.primaryItems[0].onClick).toEqual(
			expect.any(Function)
		);
	});

	it('preserves the other props and omits items actions when none are given', () => {
		const result = propsTransformer({id: 'productRelationships'});

		expect(result.id).toBe('productRelationships');
		expect(result.itemsActions).toBeUndefined();
	});
});
