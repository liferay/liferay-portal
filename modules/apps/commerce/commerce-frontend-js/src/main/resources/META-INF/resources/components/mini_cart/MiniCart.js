/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';

import ServiceProvider from '../../ServiceProvider/index';
import cartAtom from '../../utilities/atoms/cartAtom';
import {
	CART_RESET,
	CART_UPDATED,
	CURRENT_ACCOUNT_UPDATED,
	CURRENT_ORDER_DELETED,
	CURRENT_ORDER_UPDATED,
	GUEST_ORDER_ENABLED,
} from '../../utilities/eventsDefinitions';
import {showErrorNotification} from '../../utilities/notifications';
import MiniCartContext from './MiniCartContext';
import {
	ADD_PRODUCT,
	CART,
	CART_ITEMS_PAGINATION_DEFAULT,
	HEADER,
	ITEM,
	ITEMS_LIST,
	ITEMS_LIST_ACTIONS,
	OPENER,
	ORDER_BUTTON,
	ORDER_IS_EMPTY,
	REMOVE_ALL_ITEMS,
	REVIEW_ORDER,
	SUBMIT_ORDER,
	SUMMARY,
	VIEW_DETAILS,
	YOUR_ORDER,
} from './util/constants';
import {regenerateOrderDetailURL, summaryDataMapper} from './util/index';
import {DEFAULT_LABELS} from './util/labels';
import {resolveCartViews} from './util/views';

import './mini_cart.scss';

import LoadingIndicator from '@clayui/loading-indicator';

import {isLowEndDevice} from '../../utilities/device';

const CartResource = ServiceProvider.DeliveryCartAPI('v1');

function MiniCart({
	accountId,
	cartActionURLs,
	cartViews,
	channel,
	displayDiscountLevels,
	displayTotalItemsQuantity,
	guestOrderEnabled,
	itemsQuantity,
	labels,
	onAddToCart,
	orderId,
	productURLSeparator,
	requestQuoteEnabled,
	slowConnectionOrderFlowEnabled,
	summaryDataMapper,
	toggleable,
	undoCartItemDeletionDisabled,
}) {
	const [actionURLs, setActionURLs] = useState(cartActionURLs);
	const [CartViews, setCartViews] = useState({});
	const [cartItemsPagination, setCartItemsPagination] = useState(
		CART_ITEMS_PAGINATION_DEFAULT
	);
	const [cartState, setCartState] = useState({
		accountId,
		cartItems: [],
		channel: {channel},
		id: orderId,
		summary: {itemsQuantity},
	});
	const [editedItem, setEditedItem] = useState(null);
	const [isOpen, setIsOpen] = useState(!toggleable);
	const [isUpdating, setIsUpdating] = useState(false);
	const [cartAtomState] = useLiferayState(cartAtom);
	const [replacementSKUList, setReplacementSKUList] = useState([]);

	const manageSlowConnections =
		cartAtomState.updating &&
		isLowEndDevice() &&
		slowConnectionOrderFlowEnabled;

	const closeCart = () => {
		if (isUpdating) {
			return;
		}

		setIsOpen(false);

		if (toggleable) {
			document.body.classList.remove('overflow-hidden');
		}

		if (editedItem) {
			setEditedItem(null);
		}
	};

	const openCart = () => {
		if (toggleable) {
			document.body.classList.add('overflow-hidden');
		}

		setIsOpen(true);
	};

	const getCartItems = useCallback(async () => {
		const {items, lastPage} = await CartResource.getCartItemsByCartId(
			cartState.id,
			{
				page: cartItemsPagination.page,
				pageSize: cartItemsPagination.pageSize,
			}
		);

		const remainder = cartItemsPagination.pageSize - items.length;

		let nextPage = cartItemsPagination.page;

		if (!remainder) {
			nextPage += 1;
		}

		setCartState((currentState) => {
			return {
				...currentState,
				cartItems: [
					...currentState.cartItems,
					...items.reduce((appended, item) => {
						const found = currentState.cartItems.find(
							(currentItem) => currentItem.id === item.id
						);

						if (!found) {
							appended.push(item);
						}

						return appended;
					}, []),
				],
			};
		});

		setCartItemsPagination({
			lastPage,
			page: nextPage,
			pageSize: cartItemsPagination.pageSize,
		});
	}, [cartItemsPagination, cartState, setCartItemsPagination, setCartState]);

	const resetCartState = useCallback(
		({accountId = 0, id = 0}) => {
			const isAccountChanged = cartState.accountId !== accountId;
			const isCartEmptied = cartState.id === id;
			const isOrderDeleted =
				id === 0 && cartState.accountId === accountId;

			if (isAccountChanged || isCartEmptied || isOrderDeleted) {
				setCartState({
					accountId,
					channel: {channel},
					id,
					summary: {itemsQuantity: 0},
				});
			}
		},
		[cartState.accountId, cartState.id, channel, setCartState]
	);

	const updateCartModel = useCallback(
		async ({order, refreshItems = false, updatedFromCart = true}) => {
			try {
				const updatedCart = await CartResource.getCartById(order.id);

				let latestActionURLs;
				let latestCartState;

				setActionURLs((currentURLs) => {
					const orderDetailURL = currentURLs.orderDetailURL;

					latestActionURLs = {
						...currentURLs,
						orderDetailURL: !orderDetailURL
							? regenerateOrderDetailURL(
									currentURLs.baseOrderDetailURL,
									updatedCart.id,
									updatedCart.orderUUID
								)
							: new URL(orderDetailURL),
					};

					return latestActionURLs;
				});

				if (refreshItems) {
					setCartItemsPagination(CART_ITEMS_PAGINATION_DEFAULT);
				}

				setCartState((currentState) => {
					latestCartState = {
						...currentState,
						...updatedCart,
						...(refreshItems ? {cartItems: []} : {}),
					};

					return latestCartState;
				});

				Liferay.fire(CART_UPDATED, {
					order: updatedCart,
					updatedFromCart,
				});

				onAddToCart(latestActionURLs, latestCartState);
			}
			catch (error) {
				showErrorNotification(error);
			}
		},
		[onAddToCart]
	);

	const updateReplacedSKUList = useCallback(
		() =>
			cartState.cartItems
				? setReplacementSKUList(
						cartState.cartItems.filter(
							({replacedSku: replacedSKU}) => Boolean(replacedSKU)
						)
					)
				: null,
		[cartState.cartItems]
	);

	useEffect(() => {
		updateReplacedSKUList();
	}, [updateReplacedSKUList]);

	useEffect(() => {
		resolveCartViews(cartViews).then((views) => setCartViews(views));
	}, [cartViews]);

	useEffect(() => {
		Liferay.on(CURRENT_ORDER_UPDATED, updateCartModel);

		return () => {
			Liferay.detach(CURRENT_ORDER_UPDATED, updateCartModel);
		};
	}, [updateCartModel]);

	useEffect(() => {
		if (orderId) {
			updateCartModel({order: {id: orderId}});
		}
	}, [orderId, updateCartModel]);

	useEffect(() => {
		Liferay.on(CART_RESET, resetCartState);
		Liferay.on(CURRENT_ACCOUNT_UPDATED, resetCartState);
		Liferay.on(CURRENT_ORDER_DELETED, resetCartState);

		return () => {
			Liferay.detach(CART_RESET, resetCartState);
			Liferay.detach(CURRENT_ACCOUNT_UPDATED, resetCartState);
			Liferay.detach(CURRENT_ORDER_DELETED, resetCartState);
		};
	}, [resetCartState]);

	useEffect(() => {
		Liferay.fire(GUEST_ORDER_ENABLED, {guestOrderEnabled});
	}, [guestOrderEnabled]);

	return (
		<MiniCartContext.Provider
			value={{
				CartViews,
				actionURLs,
				cartItemsPagination,
				cartState,
				closeCart,
				displayDiscountLevels,
				displayTotalItemsQuantity,
				editedItem,
				getCartItems,
				guestOrderEnabled,
				isOpen,
				isUpdating,
				labels: {...DEFAULT_LABELS, ...labels},
				openCart,
				productURLSeparator,
				replacementSKUList,
				requestQuoteEnabled,
				setCartState,
				setEditedItem,
				setIsUpdating,
				setReplacementSKUList,
				slowConnectionOrderFlowEnabled,
				summaryDataMapper,
				toggleable,
				undoCartItemDeletionDisabled,
				updateCartModel,
			}}
		>
			{!!CartViews[CART] && (
				<>
					<div
						className={classnames({
							'is-open': isOpen || !toggleable,
							'mini-cart': true,
						})}
					>
						{toggleable && (
							<>
								<div
									className="mini-cart-overlay"
									onClick={() => closeCart()}
								/>

								<CartViews.Opener
									disabled={manageSlowConnections}
								/>
							</>
						)}

						<CartViews.Cart />
					</div>

					{manageSlowConnections && (
						<div className="mini-cart-slow-connection-overlay">
							<LoadingIndicator
								displayType="secondary"
								size="sm"
							/>
						</div>
					)}
				</>
			)}
		</MiniCartContext.Provider>
	);
}

MiniCart.defaultProps = {
	cartViews: {},
	displayDiscountLevels: false,
	displayTotalItemsQuantity: false,
	guestOrderEnabled: false,
	itemsQuantity: 0,
	labels: DEFAULT_LABELS,
	onAddToCart: () => {},
	orderId: 0,
	requestQuoteEnabled: false,
	slowConnectionOrderFlowEnabled: false,
	summaryDataMapper,
	toggleable: true,
	undoCartItemDeletionDisabled: false,
};

MiniCart.propTypes = {
	cartActionURLs: PropTypes.shape({
		baseOrderDetailURL: PropTypes.string,
		checkoutURL: PropTypes.string,
		orderDetailURL: PropTypes.string,
		productURLSeparator: PropTypes.string,
		signInURL: PropTypes.string,
		siteDefaultURL: PropTypes.string,
	}).isRequired,
	cartViews: PropTypes.shape({
		[CART]: PropTypes.oneOfType([
			PropTypes.shape({
				component: PropTypes.func,
			}),
			PropTypes.shape({
				contentRendererModuleUrl: PropTypes.string,
			}),
		]),
		[HEADER]: PropTypes.oneOfType([
			PropTypes.shape({
				component: PropTypes.func,
			}),
			PropTypes.shape({
				contentRendererModuleUrl: PropTypes.string,
			}),
		]),
		[ITEM]: PropTypes.oneOfType([
			PropTypes.shape({
				component: PropTypes.func,
			}),
			PropTypes.shape({
				contentRendererModuleUrl: PropTypes.string,
			}),
		]),
		[ITEMS_LIST]: PropTypes.oneOfType([
			PropTypes.shape({
				component: PropTypes.func,
			}),
			PropTypes.shape({
				contentRendererModuleUrl: PropTypes.string,
			}),
		]),
		[ITEMS_LIST_ACTIONS]: PropTypes.oneOfType([
			PropTypes.shape({
				component: PropTypes.func,
			}),
			PropTypes.shape({
				contentRendererModuleUrl: PropTypes.string,
			}),
		]),
		[OPENER]: PropTypes.oneOfType([
			PropTypes.shape({
				component: PropTypes.func,
			}),
			PropTypes.shape({
				contentRendererModuleUrl: PropTypes.string,
			}),
		]),
		[ORDER_BUTTON]: PropTypes.oneOfType([
			PropTypes.shape({
				component: PropTypes.func,
			}),
			PropTypes.shape({
				contentRendererModuleUrl: PropTypes.string,
			}),
		]),
		[SUMMARY]: PropTypes.oneOfType([
			PropTypes.shape({
				component: PropTypes.func,
			}),
			PropTypes.shape({
				contentRendererModuleUrl: PropTypes.string,
			}),
		]),
	}),
	displayDiscountLevels: PropTypes.bool,
	displayTotalItemsQuantity: PropTypes.bool,
	guestOrderEnabled: PropTypes.bool,
	itemsQuantity: PropTypes.number,
	labels: PropTypes.shape({
		[ADD_PRODUCT]: PropTypes.string,
		[ORDER_IS_EMPTY]: PropTypes.string,
		[REMOVE_ALL_ITEMS]: PropTypes.string,
		[REVIEW_ORDER]: PropTypes.string,
		[SUBMIT_ORDER]: PropTypes.string,
		[VIEW_DETAILS]: PropTypes.string,
		[YOUR_ORDER]: PropTypes.string,
	}),
	onAddToCart: PropTypes.func,
	orderId: PropTypes.number,
	requestQuoteEnabled: PropTypes.bool,
	summaryDataMapper: PropTypes.func,
	toggleable: PropTypes.bool,
};

export default MiniCart;
