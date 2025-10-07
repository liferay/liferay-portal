/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import React, {useCallback, useContext, useEffect} from 'react';

import ServiceProvider from '../../ServiceProvider';
import {
	OPEN_MINICART_FOR_EDITING,
	OPEN_MINI_CART,
} from '../../utilities/eventsDefinitions';
import MiniCartContext from './MiniCartContext';
import {hasOptions} from './util/index';

const CartItemResource = ServiceProvider.DeliveryCartAPI('v1');

function Opener({disabled = false}) {
	const {cartState, displayTotalItemsQuantity, openCart, setEditedItem} =
		useContext(MiniCartContext);

	const {cartItems = [], summary = {}} = cartState;
	const {itemsCount = 0, itemsQuantity = 0} = summary;

	const numberOfItems = displayTotalItemsQuantity
		? itemsQuantity
		: itemsCount;

	const openMiniCartForEditing = useCallback(
		async ({dataSetId, orderItemId}) => {
			let cartItem = cartItems.find(
				(cartItem) => cartItem.id === orderItemId
			);

			if (!cartItem) {
				try {
					cartItem = await CartItemResource.getItemById(orderItemId);
				}
				catch (error) {
					console.error('Unable to fetch cart item.', error);
				}
			}

			if (
				cartItem &&
				(hasOptions(cartItem.options) || cartItem.skuUnitOfMeasure)
			) {
				setEditedItem({
					...cartItem,
					dataSetId,
				});

				openCart();
			}
		},
		[cartItems, openCart, setEditedItem]
	);

	useEffect(() => {
		Liferay.on(OPEN_MINICART_FOR_EDITING, openMiniCartForEditing);
		Liferay.on(OPEN_MINI_CART, openCart);

		return () => {
			Liferay.detach(OPEN_MINICART_FOR_EDITING, openMiniCartForEditing);
			Liferay.detach(OPEN_MINI_CART, openCart);
		};
	}, [openCart, openMiniCartForEditing]);

	return (
		<button
			className={classnames({
				'has-badge': numberOfItems > 0,
				'mini-cart-opener': true,
			})}
			data-badge-count={numberOfItems}
			data-qa-id="miniCartButton"
			disabled={disabled}
			onClick={openCart}
		>
			<ClayIcon symbol="shopping-cart" />
		</button>
	);
}

Opener.propTypes = {
	openCart: PropTypes.func,
};

export default Opener;
