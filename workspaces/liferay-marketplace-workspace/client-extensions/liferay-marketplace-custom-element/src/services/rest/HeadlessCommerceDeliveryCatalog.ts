/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetcher from '../fetcher';

export default class HeadlessCommerceDeliveryCatalog {
	static async getProduct(
		channelId: number | string,
		productId: number | string,
		searchParams = new URLSearchParams()
	) {
		return fetcher<DeliveryProduct>(
			`o/headless-commerce-delivery-catalog/v1.0/channels/${channelId}/products/${productId}?${searchParams.toString()}`
		);
	}

	static async getProductsPage(
		channelId: number | string,
		searchParams = new URLSearchParams()
	) {
		return fetcher<APIResponse<DeliveryProduct>>(
			`o/headless-commerce-delivery-catalog/v1.0/channels/${channelId}/products?${searchParams.toString()}`
		);
	}

	static async getProductsByChannelId(
		channelId: number,
		searchParams = new URLSearchParams()
	) {
		return fetcher<APIResponse<Product>>(
			`o/headless-commerce-delivery-catalog/v1.0/channels/${channelId}/products?${searchParams.toString()}`
		);
	}

	static async getChannels(
		searchParams: URLSearchParams = new URLSearchParams()
	) {
		return fetcher<APIResponse<Channel>>(
			`o/headless-commerce-delivery-catalog/v1.0/channels?${searchParams.toString()}`
		);
	}

	static async getSkuInfo(
		channelId: number | string,
		productId: number,
		skuId: number,
		searchParams = new URLSearchParams()
	) {
		return fetcher<APIResponse<Channel>>(
			`o/headless-commerce-delivery-catalog/v1.0/channels/${channelId}/products/${productId}/skus/${skuId}?${searchParams.toString()}`
		);
	}
}
