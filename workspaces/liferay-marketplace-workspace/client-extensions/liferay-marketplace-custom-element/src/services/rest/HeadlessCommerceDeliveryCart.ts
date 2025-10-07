/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '../../liferay/liferay';
import fetcher from '../fetcher';

export default class HeadlessCommerceDeliveryCart {
	static async createCart(channelId: number | string, cart: Partial<Cart>) {
		return fetcher.post(
			`/o/headless-commerce-delivery-cart/v1.0/channels/${channelId}/carts`,
			cart
		);
	}

	static async checkoutCart(cartId: number) {
		return fetcher.post(
			`/o/headless-commerce-delivery-cart/v1.0/carts/${cartId}/checkout`
		);
	}

	static async deleteCart(id: number | string) {
		return fetcher.delete(
			`/o/headless-commerce-delivery-cart/v1.0/carts/${id}`
		);
	}

	static async getAccountCarts(
		accountId: number | string,
		channelId: string
	) {
		return fetcher<APIResponse<Cart>>(
			`o/headless-commerce-delivery-cart/v1.0/channels/${channelId}/account/${accountId}/carts`
		);
	}

	static async getCart(id: number) {
		return fetcher<Cart>(
			`/o/headless-commerce-delivery-cart/v1.0/carts/${id}`
		);
	}

	static async getCartItems(cartId: number) {
		return fetcher<APIResponse<CartItem>>(
			`o/headless-commerce-delivery-cart/v1.0/carts/${cartId}/items`
		);
	}

	static async getPaymentMethodURL(cartId: number, callbackURL: string) {
		const response = await Liferay.Util.fetch(
			`/o/headless-commerce-delivery-cart/v1.0/carts/${cartId}/payment-url?callbackURL=${callbackURL}`
		);

		return response.text();
	}

	static async updateCart(id: number | string, data: Partial<Cart>) {
		return fetcher.patch(
			`/o/headless-commerce-delivery-cart/v1.0/carts/${id}`,
			data
		);
	}
}
