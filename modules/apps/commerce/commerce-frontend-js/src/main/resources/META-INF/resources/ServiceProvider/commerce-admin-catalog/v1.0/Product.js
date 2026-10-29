/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AJAX from '../../../utilities/AJAX/index';

const PRODUCTS_PATH = '/products';

const PRODUCT_SPECIFICATIONS_PATH = '/productSpecifications';

const STATUS_FILTER = 'statusCode/any(x:(x eq 0))';

const VERSION = 'v1.0';

function resolveProductsPath(basePath = '', productId = '') {
	return `${basePath}${VERSION}${PRODUCTS_PATH}/${productId}`;
}

function resolveProductsURL(
	basePath,
	{channelId, excludedProductIds = [], excludedProductTypes = []} = {}
) {
	const url = new URL(
		`${basePath}${VERSION}${PRODUCTS_PATH}`,
		window.location.origin
	);

	url.searchParams.set('nestedFields', 'catalog');

	const filters = [STATUS_FILTER];

	if (channelId) {
		filters.push(
			`(productChannelFilter eq false or channelId/any(x:(x eq ${channelId})))`
		);
	}

	if (excludedProductIds.length) {
		filters.push(`not (productId in (${excludedProductIds.join(',')}))`);
	}

	excludedProductTypes.forEach((excludedProductType) =>
		filters.push(`productType ne '${excludedProductType}'`)
	);

	url.searchParams.set('filter', filters.join(' and '));

	return url.toString();
}

function resolveProductSpecificationsPath(basePath = '', productId = '') {
	return `${basePath}${VERSION}${PRODUCTS_PATH}/${productId}${PRODUCT_SPECIFICATIONS_PATH}`;
}

export default function Product(basePath) {
	return {
		createProduct: (json) => AJAX.POST(resolveProductsPath(basePath), json),

		createProductSpecification: (productId, json) =>
			AJAX.POST(
				resolveProductSpecificationsPath(basePath, productId),
				json
			),

		getProductById: (productId) =>
			AJAX.GET(resolveProductsPath(basePath, productId)),

		getProductsURL: (options) => resolveProductsURL(basePath, options),

		updateProduct: (productId, json) =>
			AJAX.PATCH(resolveProductsPath(basePath, productId), json),
	};
}
