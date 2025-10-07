/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import classnames from 'classnames';
import React, {useContext, useState} from 'react';

import ServiceProvider from '../../ServiceProvider/index';
import {CART_PRODUCT_QUANTITY_CHANGED} from '../../utilities/eventsDefinitions';
import {liferayNavigate} from '../../utilities/index';
import {ALL} from '../add_to_cart/constants';
import MiniCartContext from './MiniCartContext';
import {REMOVE_ALL_ITEMS, VIEW_DETAILS} from './util/constants';

const CartResource = ServiceProvider.DeliveryCartAPI('v1');

function CartItemsListActions() {
	const {actionURLs, cartState, labels, setIsUpdating, updateCartModel} =
		useContext(MiniCartContext);

	const {
		id: orderId,
		summary: {itemsCount},
	} = cartState;
	const {orderDetailURL} = actionURLs;

	const [isAsking, setIsAsking] = useState(false);

	const askConfirmation = () => setIsAsking(true);
	const cancel = () => setIsAsking(false);
	const flushCart = () => {
		setIsUpdating(true);

		CartResource.updateCartById(orderId, {cartItems: []})
			.then(() => updateCartModel({order: {id: orderId}}))
			.then(() => {
				setIsAsking(false);
				setIsUpdating(false);

				Liferay.fire(CART_PRODUCT_QUANTITY_CHANGED, {
					quantity: 0,
					skuId: ALL,
				});
			});
	};

	return (
		<div className="mini-cart-header">
			<div className="mini-cart-header-block">
				<div className="mini-cart-header-resume">
					{!!itemsCount && (
						<>
							<span className="items">{itemsCount}</span>
							{itemsCount > 1
								? ' ' + Liferay.Language.get('products')
								: ' ' + Liferay.Language.get('product')}
						</>
					)}
				</div>

				<div className="mini-cart-header-actions">
					<span
						className={classnames({
							actions: true,
							hide: isAsking,
						})}
					>
						<ClayButton
							className="action"
							disabled={!itemsCount}
							displayType="link"
							onClick={() => {
								liferayNavigate(orderDetailURL);
							}}
							small
						>
							{labels[VIEW_DETAILS]}
						</ClayButton>

						<ClayButton
							className="action text-danger"
							disabled={!itemsCount}
							displayType="link"
							onClick={askConfirmation}
							small
						>
							{labels[REMOVE_ALL_ITEMS]}
						</ClayButton>
					</span>

					<div
						className={classnames({
							'confirmation-prompt': true,
							'hide': !isAsking,
						})}
					>
						<span>{Liferay.Language.get('are-you-sure')}</span>

						<span>
							<button
								className="btn btn-outline-success btn-sm"
								onClick={flushCart}
								type="button"
							>
								{Liferay.Language.get('yes')}
							</button>

							<button
								className="btn btn-outline-danger btn-sm"
								onClick={cancel}
								type="button"
							>
								{Liferay.Language.get('no')}
							</button>
						</span>
					</div>
				</div>
			</div>
		</div>
	);
}

export default CartItemsListActions;
