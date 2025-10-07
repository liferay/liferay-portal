/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect} from 'react';

import {Liferay} from '../liferay/liferay';
import {useGetAppContext} from '../pages/GetApp/GetAppContextProvider';
import fetcher from '../services/fetcher';
import HeadlessCommerceDeliveryCart from '../services/rest/HeadlessCommerceDeliveryCart';
import {createCart} from '../utils/api';

const channelId = Liferay.CommerceContext.commerceChannelId;

const useCart = ({
	accountId,
	orderType,
	product,
}: {
	accountId: number;
	orderType?: OrderType;
	product: DeliveryProduct;
}) => {
	const [
		{
			license: {cart, cartItems},
		},
		dispatch,
	] = useGetAppContext();

	const cartId = cart?.id;

	const setCart = useCallback(
		(payload?: Cart) => dispatch({payload, type: 'SET_CART'}),
		[dispatch]
	);

	const setCartItems = useCallback(
		(payload: CartItem[]) =>
			dispatch({payload: payload as any, type: 'SET_CART_ITEMS'}),
		[dispatch]
	);
	const currencyCode = Liferay.CommerceContext.currency.currencyCode;

	const addCart = async (productId: number, skuId: number) => {
		if (!cartId) {
			const response = await createCart({
				accountId,
				channelId,
				currencyCode,
				orderTypeExternalReferenceCode:
					orderType?.externalReferenceCode as string,
			});

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

	useEffect(() => {
		if (cartId && cartItems.length) {
			HeadlessCommerceDeliveryCart.updateCart(cartId, {
				cartItems,
			})
				.then(setCart)
				.catch(console.error);
		}
	}, [cartId, cartItems, setCart]);

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
				.then(() => {
					setCart(undefined);
					setCartItems([]);
				})
				.catch(console.error),
		[setCart, setCartItems]
	);

	useEffect(() => {
		(async () => {
			if (!accountId || !product?.id) {
				return;
			}

			const {items: orders = []} = await fetcher(
				`o/headless-commerce-delivery-cart/v1.0/channels/${channelId}/account/${accountId}/carts`
			);

			if (!orders?.length) {
				return;
			}

			const [order] = orders;

			setCart(order);

			const openOrders = orders.filter(
				(order: Order) => order?.orderStatusInfo?.label === 'open'
			);

			if (!openOrders) {
				return;
			}

			const cartItemsResponse = await fetcher(
				`o/headless-commerce-delivery-cart/v1.0/carts/${openOrders[0]?.id}/items`
			);

			const hasCartItem = cartItemsResponse.items.some(
				(cartItem: CartItem) => cartItem.productId === product.id + 1
			);

			if (!hasCartItem) {
				return removeCart(order.id);
			}

			const cartItemsList = await cartItemsResponse?.items?.map(
				(item: CartItem) => ({
					productId: item.productId,
					quantity: item.quantity,
					skuId: item.skuId,
				})
			);

			dispatch({payload: 'PAID', type: 'SET_LICENSE_TYPE'});

			setCartItems(cartItemsList);
		})();
	}, [accountId, dispatch, product?.id, removeCart, setCart, setCartItems]);

	return {
		addCart,
		cart,
		cartItems,
		removeCart,
		removeFromCart,
		setCart,
		updateCart: HeadlessCommerceDeliveryCart.updateCart,
	};
};

export default useCart;
