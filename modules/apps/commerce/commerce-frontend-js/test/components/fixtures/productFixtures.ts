/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface IProductOverrides {
	channel?: object;
	cpInstance?: object;
	settings?: {
		productConfiguration?: object;
		[key: string]: unknown;
	};
}

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

export function mockCpInstance(overrides: object = {}) {
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

export function mockProduct(overrides: IProductOverrides = {}) {
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

export function mockBundledProductSingleSku(overrides: IProductOverrides = {}) {
	return mockProduct({
		...overrides,
		cpInstance: mockCpInstance({
			purchasable: true,
			...(overrides.cpInstance || {}),
		}),
	});
}

export function mockBundledProductMultiSku(overrides: IProductOverrides = {}) {
	return mockProduct({
		...overrides,
		cpInstance: mockCpInstance({
			purchasable: false,
			...(overrides.cpInstance || {}),
		}),
	});
}
