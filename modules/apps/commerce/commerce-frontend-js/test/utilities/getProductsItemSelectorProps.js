/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ServiceProvider from '../../src/main/resources/META-INF/resources/ServiceProvider/index';
import getProductsItemSelectorProps from '../../src/main/resources/META-INF/resources/utilities/getProductsItemSelectorProps';

const AdminCatalogAPIServiceProvider = ServiceProvider.AdminCatalogAPI('v1');

describe('getProductsItemSelectorProps', () => {
	it('lists the products of the admin catalog API', () => {
		const options = {
			channelId: 42,
			excludedProductIds: [1, 2],
			excludedProductTypes: ['grouped'],
		};

		const {apiURL} = getProductsItemSelectorProps({
			...options,
			portletNamespace: 'ns',
		});

		expect(apiURL).toBe(
			AdminCatalogAPIServiceProvider.getProductsURL(options)
		);
	});

	it('selects products by ID', () => {
		const {fdsProps, locator, multiSelect, title} =
			getProductsItemSelectorProps({portletNamespace: 'ns'});

		expect(fdsProps.configInURLBehavior).toBe('OFF');
		expect(fdsProps.id).toBe('nsproductsItemSelector');
		expect(
			fdsProps.views[0].schema.fields.map((field) => field.fieldName)
		).toEqual([
			'thumbnail',
			['name', 'LANG'],
			'skuFormatted',
			'catalog.name',
			'workflowStatusInfo',
		]);
		expect(locator.id).toBe('id');
		expect(locator.value).toBe('id');
		expect(multiSelect).toBe(true);
		expect(title).toBe('select-x');
	});

	it('supports a single selection with a custom title', () => {
		const {multiSelect, title} = getProductsItemSelectorProps({
			multiSelect: false,
			portletNamespace: 'ns',
			title: 'Add Products',
		});

		expect(multiSelect).toBe(false);
		expect(title).toBe('Add Products');
	});
});
