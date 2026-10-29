/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ServiceProvider from '../../../../src/main/resources/META-INF/resources/ServiceProvider/index';

const AdminCatalogAPIServiceProvider = ServiceProvider.AdminCatalogAPI('v1');

const STATUS_FILTER = 'statusCode/any(x:(x eq 0))';

function getProductsURL(options) {
	return new URL(AdminCatalogAPIServiceProvider.getProductsURL(options));
}

describe('getProductsURL', () => {
	it('lists the approved products with their catalog', () => {
		const url = getProductsURL();

		expect(url.pathname).toBe(
			'/o/headless-commerce-admin-catalog/v1.0/products'
		);
		expect(url.searchParams.get('filter')).toBe(STATUS_FILTER);
		expect(url.searchParams.get('nestedFields')).toBe('catalog');
	});

	it('filters by channel', () => {
		const url = getProductsURL({channelId: 42});

		expect(url.searchParams.get('filter')).toBe(
			`${STATUS_FILTER} and (productChannelFilter eq false or channelId/any(x:(x eq 42)))`
		);
	});

	it('excludes products and product types', () => {
		const url = getProductsURL({
			excludedProductIds: [1, 2],
			excludedProductTypes: ['grouped'],
		});

		expect(url.searchParams.get('filter')).toBe(
			`${STATUS_FILTER} and not (productId in (1,2)) and productType ne 'grouped'`
		);
	});
});
