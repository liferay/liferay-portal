/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useSelector} from '@xstate/store/react';
import {useCallback, useEffect} from 'react';

import {Liferay} from '../liferay/liferay';
import {cartStore} from '../pages/ProductPurchase/store';
import HeadlessCommerceDeliveryCart from '../services/rest/HeadlessCommerceDeliveryCart';

const channelId = Liferay.CommerceContext.commerceChannelId;

const useProductPurchaseCart = (
	accountId: number,
	product: DeliveryProduct,
	orderTypeExternalReferenceCode: string
) => {
	const {cart, cartItems} = useSelector(cartStore, (state) => state.context);

	const cartId = cart?.id;

	const setCart = useCallback(
		(cart: Cart) => cartStore.send({cart, type: 'setCart'}),
		[]
	);

	const setCartItems = useCallback(
		(cartItems: CartItem[]) =>
			cartStore.send({cartItems, type: 'setCartItems'}),
		[]
	);

	const addCart = async (productId: number, skuId: number) => {
		if (!cartId) {
			const response = await HeadlessCommerceDeliveryCart.createCart(
				channelId,
				{
					accountId,
					currencyCode: Liferay.CommerceContext.currency.currencyCode,
					orderTypeExternalReferenceCode,
				}
			);

			setCart(response);
		}

		const existingItem = cartItems.find((item) => item?.skuId === skuId);

		if (existingItem) {
			const newCartItems = cartItems.map((item) =>
				item.skuId === skuId
					? {...item, quantity: item.quantity + 1}
					: item
			);

			return setCartItems(newCartItems);
		}

		setCartItems([
			...cartItems,
			{productId, quantity: 1, skuId} as CartItem,
		]);
	};

	const removeFromCart = (skuId: number) =>
		setCartItems(
			cartItems
				.map((item) =>
					item.skuId === skuId
						? {...item, quantity: item.quantity - 1}
						: item
				)
				.filter((item) => item.quantity > 0)
		);

	const removeCart = useCallback(
		(id: number) =>
			HeadlessCommerceDeliveryCart.deleteCart(id)
				.then(() => cartStore.send({type: 'reset'}))
				.catch(console.error),
		[]
	);

	useEffect(() => {
		(async () => {
			if (!accountId) {
				return;
			}

			const {items: carts} =
				await HeadlessCommerceDeliveryCart.getAccountCarts(
					accountId,
					channelId
				);

			if (!carts?.length) {
				return;
			}

			const [cart] = carts;

			setCart(cart);

			const openOrders = carts.some(
				(cart: Cart) => cart?.orderStatusInfo?.label === 'open'
			);

			if (!openOrders) {
				return;
			}

			const {items: cartItems} =
				await HeadlessCommerceDeliveryCart.getCartItems(cart.id);

			if (
				!cartItems.some(
					(cartItem: CartItem) =>
						cartItem.productId === product.productId
				)
			) {
				return removeCart(cart.id);
			}

			setCartItems(cartItems);
		})();
	}, [accountId, product.productId, removeCart, setCart, setCartItems]);

	return {
		addCart,
		cart,
		cartItems,
		removeCart,
		removeFromCart,
		setCart,
		updateCart: HeadlessCommerceDeliveryCart.updateCart.bind(
			HeadlessCommerceDeliveryCart
		),
	};
};

export default useProductPurchaseCart;
