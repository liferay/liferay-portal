/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import MiniCartContext from './MiniCartContext';
import {hasPriceOnApplication} from './util/index';

function Wrapper() {
	const {
		CartViews,
		cartState,
		editedItem,
		isOpen,
		isUpdating,
		requestQuoteEnabled,
	} = useContext(MiniCartContext);

	const {
		cartItems = [],
		summary: {itemsCount = 0},
	} = cartState;
	const cartHasPriceOnApplicationItems = hasPriceOnApplication(cartItems);

	return (
		<div className="mini-cart-wrapper">
			<CartViews.Header />

			{editedItem ? (
				<>
					<CartViews.EditItem />
				</>
			) : (
				<>
					<div className="mini-cart-wrapper-items">
						{isOpen && (
							<>
								<CartViews.ItemsList
									showPriceOnApplicationInfo={
										cartHasPriceOnApplicationItems
									}
								/>
							</>
						)}
					</div>

					<CartViews.OrderButton
						disabled={
							!itemsCount ||
							cartHasPriceOnApplicationItems ||
							isUpdating
						}
					/>

					{(requestQuoteEnabled || cartHasPriceOnApplicationItems) &&
						!!itemsCount && (
							<CartViews.RequestQuoteButton
								disabled={isUpdating}
							/>
						)}
				</>
			)}
		</div>
	);
}

export default Wrapper;
