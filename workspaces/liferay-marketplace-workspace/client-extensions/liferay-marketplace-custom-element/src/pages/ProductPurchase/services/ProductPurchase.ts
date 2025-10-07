/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Analytics} from '../../../core/Analytics';
import {OrderTypes} from '../../../enums/Order';
import {Liferay} from '../../../liferay/liferay';
import CommerceSelectAccount from '../../../services/rest/CommerceSelectAccount';
import HeadlessCommerceDeliveryCart from '../../../services/rest/HeadlessCommerceDeliveryCart';

export default class ProductPurchase {
	protected orderTypeExternalReferenceCode?: OrderTypes;
	protected HeadlessCommerceDeliveryCart = HeadlessCommerceDeliveryCart;

	constructor(
		protected readonly account: Account,
		protected readonly product: DeliveryProduct
	) {}

	protected getCart() {
		return {
			accountId: this.account?.id,
			cartItems: this.getCartItems(),
			currencyCode: Liferay.CommerceContext.currency.currencyCode,
			orderTypeExternalReferenceCode: this.orderTypeExternalReferenceCode,
		} as Cart;
	}

	public async getNextStepsLink(cart: Cart) {
		return `/next-steps?orderId=${cart.id}`;
	}

	protected getCartItems(skuId = this.product.skus[0]?.id) {
		return [
			{
				price: {
					currency: Liferay.CommerceContext.currency.currencyCode,
					discount: 0,
				},
				productId: this.product.productId,
				quantity: 1,
				settings: {
					maxQuantity: 1,
				},
				skuId,
			},
		];
	}

	protected analyticsTrack() {
		Analytics.track('ORDER_CREATION', {
			accountId: this.account.id,
			orderTypeExternalReferenceCode: this.orderTypeExternalReferenceCode,
			productName: this.product.name,
		});
	}

	public async createOrder(cart?: Cart, _options?: unknown): Promise<Cart> {
		const body = {
			...this.getCart(),
			...cart,
		};

		const newCart = await (cart?.id
			? HeadlessCommerceDeliveryCart.updateCart(cart.id, body)
			: HeadlessCommerceDeliveryCart.createCart(
					Liferay.CommerceContext.commerceChannelId,
					body
				));

		await Promise.all([
			CommerceSelectAccount.selectAccount(this.account.id),
			HeadlessCommerceDeliveryCart.checkoutCart(newCart.id),
		]);

		this.analyticsTrack();

		return newCart;
	}
}
