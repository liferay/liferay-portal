/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../src/main/resources/META-INF/resources/js/ProductRelationshipsFDSPropsTransformer';
import ProductRelationshipSelectorTitleRenderer from '../../src/main/resources/META-INF/resources/js/cell_renderers/ProductRelationshipSelectorTitleRenderer';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	StatusLabel: jest.fn(),
	addOnClickToCreationMenuItems: (items) =>
		items.map((item) => ({...item, onClick() {}})),
	confirmAndDeleteEntryAction: (...args) =>
		mockConfirmAndDeleteEntryAction(...args),
}));

jest.mock('frontend-js-web', () => ({
	sub: (template) => template,
}));

const DELETE_HREF =
	'/o/headless-pim/v1.0/scopes/39226/links?className=com.liferay.object.model.ObjectDefinition%23P4R4&externalReferenceCode=SHIRT-BLUE&type=variant';

const mockConfirmAndDeleteEntryAction = jest.fn();

Liferay.Util = {...Liferay.Util, escapeHTML: (value) => value};

jest.mock(
	'../../src/main/resources/META-INF/resources/js/openProductRelationshipSelectorModal',
	() => ({
		__esModule: true,
		default: jest.fn(),
	})
);

describe('ProductRelationshipsFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
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

	it('registers the product title cell renderer', () => {
		const result = propsTransformer({});

		const [renderer] = result.customRenderers.tableCell;

		expect(renderer.component).toBe(
			ProductRelationshipSelectorTitleRenderer
		);
		expect(renderer.name).toBe('titleTableCellRenderer');
	});

	it('forces hideManagementBarInEmptyState to true and preserves the other props', () => {
		const result = propsTransformer({
			hideManagementBarInEmptyState: false,
			id: 'productRelationships',
		});

		expect(result.hideManagementBarInEmptyState).toBe(true);
		expect(result.id).toBe('productRelationships');
	});

	it('confirms a removal through the modal rather than the browser dialog', () => {
		const event = {preventDefault: jest.fn()};

		propsTransformer({}).onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			event,
			itemData: {
				actions: {
					delete: {
						href: DELETE_HREF,
						method: 'DELETE',
					},
				},
				title: 'Blue Shirt',
			},
			loadData: jest.fn(),
		});

		expect(event.preventDefault).toHaveBeenCalled();
		expect(mockConfirmAndDeleteEntryAction).toHaveBeenCalledWith(
			expect.objectContaining({
				deleteAction: {
					href: DELETE_HREF,
					method: 'DELETE',
				},
			})
		);
	});

	it('leaves the other item actions to the data set', () => {
		const event = {preventDefault: jest.fn()};

		propsTransformer({}).onActionDropdownItemClick({
			action: {data: {id: 'view'}},
			event,
			itemData: {actions: {delete: {href: '', method: ''}}, title: ''},
			loadData: jest.fn(),
		});

		expect(event.preventDefault).not.toHaveBeenCalled();
		expect(mockConfirmAndDeleteEntryAction).not.toHaveBeenCalled();
	});

	it('omits items actions when none are given', () => {
		const result = propsTransformer({id: 'productRelationships'});

		expect(result.itemsActions).toBeUndefined();
	});
});
