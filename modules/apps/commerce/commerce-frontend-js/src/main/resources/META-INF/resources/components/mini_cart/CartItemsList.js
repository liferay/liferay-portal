/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayIcon from '@clayui/icon';
import React, {useContext, useRef} from 'react';

import InfiniteScroller from '../infinite_scroller/InfiniteScroller';
import CartQuickAdd from './CartQuickAdd';
import MiniCartContext from './MiniCartContext';
import {ADD_PRODUCT} from './util/constants';

export default function CartItemsList({showPriceOnApplicationInfo = false}) {
	const {
		CartViews,
		cartItemsPagination,
		cartState,
		getCartItems,
		isUpdating,
		labels,
		replacementSKUList,
		setCartState,
		summaryDataMapper,
	} = useContext(MiniCartContext);

	const {accountId, cartItems = [], summary = {}} = cartState;

	const showReplacementAlert = Boolean(replacementSKUList.length);

	const cartItemsListRef = useRef(null);

	return (
		<div className="mini-cart-items-list">
			<CartViews.ItemsListActions />

			{accountId ? <CartQuickAdd /> : null}

			{showReplacementAlert ? (
				<div className="info-wrapper">
					<ClayAlert
						displayType="info"
						hideCloseIcon
						title={Liferay.Language.get('info')}
					>
						{Liferay.Language.get(
							'there-are-replacement-products-in-your-cart'
						)}
					</ClayAlert>
				</div>
			) : null}

			{showPriceOnApplicationInfo && (
				<div className="info-wrapper">
					<ClayAlert
						displayType="info"
						title={Liferay.Language.get('info')}
					>
						{Liferay.Language.get(
							'your-cart-has-products-that-require-a-quote-to-complete-the-checkout'
						)}
					</ClayAlert>
				</div>
			)}

			{summary?.itemsQuantity ? (
				<>
					<div
						className="mini-cart-cart-items"
						ref={cartItemsListRef}
					>
						<InfiniteScroller
							maxHeight="100%"
							onBottomTouched={getCartItems}
							scrollCompleted={
								cartItems.length === summary.itemsCount &&
								cartItemsPagination.page >=
									cartItemsPagination.lastPage
							}
							scrollingElementRef={cartItemsListRef}
						>
							{cartItems.map((currentCartItem, index) => {
								const updateCartItem = (callback) => {
									const updatedCartItem =
										callback(currentCartItem);

									setCartState((cartState) => ({
										...cartState,
										cartItems: cartItems.map((cartItem) =>
											cartItem.id === currentCartItem.id
												? updatedCartItem
												: cartItem
										),
									}));
								};

								return (
									<CartViews.Item
										index={index}
										key={`${currentCartItem.id}`}
										updateCartItem={updateCartItem}
										{...currentCartItem}
									/>
								);
							})}
						</InfiniteScroller>
					</div>

					<CartViews.Summary
						dataMapper={summaryDataMapper}
						isLoading={isUpdating}
						summaryData={summary}
					/>
				</>
			) : (
				<div className="empty-cart">
					<div className="empty-cart-icon mb-3">
						<ClayIcon symbol="shopping-cart" />
					</div>

					<p className="empty-cart-label">{labels[ADD_PRODUCT]}</p>
				</div>
			)}
		</div>
	);
}
