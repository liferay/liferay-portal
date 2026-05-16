/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const BASE_SETTINGS = {
	iconOnly: false,
	productConfiguration: {
		allowedOrderQuantities: [],
		maxOrderQuantity: 50,
		minOrderQuantity: 1,
		multipleOrderQuantity: 1,
	},
};

const BASE_CHANNEL = {
	currencyCode: 'USD',
	groupId: '42398',
	id: '42397',
};

export function mockCpInstance(overrides = {}) {
	return {
		availability: {stockQuantity: 100},
		backOrderAllowed: false,
		disabled: false,
		inCart: false,
		options: [],
		published: true,
		purchasable: true,
		quantity: 1,
		skuId: 42633,
		skuOptions: '[]',
		validQuantity: true,
		...overrides,
	};
}

export function mockProduct(overrides = {}) {
	return {
		accountId: 43879,
		cartId: '43882',
		channel: {...BASE_CHANNEL, ...(overrides.channel || {})},
		cpInstance: mockCpInstance(overrides.cpInstance),
		productId: 42182,
		settings: {
			...BASE_SETTINGS,
			...(overrides.settings || {}),
			productConfiguration: {
				...BASE_SETTINGS.productConfiguration,
				...((overrides.settings &&
					overrides.settings.productConfiguration) ||
					{}),
			},
		},
	};
}

/**
 * Single-SKU bundled product — purchasable directly. The AddToCart button must
 * render in the enabled state (the storefront product card shows quantity + add
 * button rather than redirecting to the product detail page).
 */
export function mockBundledProductSingleSku(overrides = {}) {
	return mockProduct({
		...overrides,
		cpInstance: mockCpInstance({
			purchasable: true,
			...(overrides.cpInstance || {}),
		}),
	});
}

/**
 * Multi-SKU bundled product — not directly purchasable; the caller (product
 * card) would render a "view all variants" link instead of AddToCart. In the
 * AddToCart unit-test world this maps to `purchasable: false`, which forces
 * the button into the disabled state.
 */
export function mockBundledProductMultiSku(overrides = {}) {
	return mockProduct({
		...overrides,
		cpInstance: mockCpInstance({
			purchasable: false,
			...(overrides.cpInstance || {}),
		}),
	});
}

/**
 * Multi-SKU product with selectable options. The AddToCart component still
 * receives a single resolved SKU; this fixture mirrors what the option-selector
 * upstream would hand off after one variant has been chosen.
 */
export function mockProductWithOptions(overrides = {}) {
	return mockProduct({
		...overrides,
		cpInstance: mockCpInstance({
			options: [{name: 'Color', value: 'Black'}],
			skuOptions: JSON.stringify([{key: 'Color', value: 'Black'}]),
			...(overrides.cpInstance || {}),
		}),
	});
}

export function mockMultiSkuProduct(overrides = {}) {
	return mockProduct({
		...overrides,
		cpInstance: mockCpInstance({
			published: true,
			purchasable: false,
			...(overrides.cpInstance || {}),
		}),
	});
}
