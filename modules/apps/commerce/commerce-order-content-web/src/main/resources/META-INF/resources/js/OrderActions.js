/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {
	CommerceServiceProvider,
	RequestQuote,
	commerceEvents,
} from 'commerce-frontend-js';
import {openModal, openToast} from 'frontend-js-components-web';
import React, {useCallback, useEffect, useState} from 'react';

import {handleOrderActionRedirect} from './orderActionRedirectHelper';
import {PAYMENT_METHOD_TYPE_OFFLINE, getOrder} from './util';

function OrderActions({
	checkoutURL,
	isOpen,
	isPriceOnApplication,
	manageNotesPermission,
	manageRestrictedNotesPermission,
	namespace,
	orderId,
	orderSummaryURL,
	quickCheckoutEnabled,
	reorderURL,
	viewReturnableOrderItemsURL,
}) {
	const [actions, setActions] = useState([]);
	const [currentOrder, setCurrentOrder] = useState({});
	const [errorMessage, setErrorMessage] = useState('');
	const [open, setOpen] = useState(isOpen);

	const getActions = useCallback(
		({order}) => {
			const getTransitions = open
				? CommerceServiceProvider.DeliveryCartAPI('v1')
						.getCartTransitionsById
				: CommerceServiceProvider.DeliveryOrderAPI('v1')
						.getOrderTransitionsById;

			getTransitions(orderId)
				.then(({items: availableTransitions}) => {
					let actions = availableTransitions;

					if (!isPriceOnApplication && quickCheckoutEnabled) {
						const quickCheckoutTransition =
							availableTransitions.find(
								(item) => item.name === 'quick-checkout'
							);

						if (open && !quickCheckoutTransition) {
							actions.push({
								disabled: true,
								label: Liferay.Language.get('quick-checkout'),
								name: 'quick-checkout',
							});
						}
					}

					if (viewReturnableOrderItemsURL) {
						actions = [
							...actions,
							{
								label: Liferay.Language.get('make-a-return'),
								name: 'make-return',
							},
						];
					}

					setActions(actions);
					setCurrentOrder(order);
				})
				.catch((error) => {
					openToast({
						message:
							error.message ||
							Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
						type: 'danger',
					});
				});
		},
		[
			isPriceOnApplication,
			orderId,
			open,
			quickCheckoutEnabled,
			viewReturnableOrderItemsURL,
		]
	);

	const refreshOrder = useCallback(() => {
		getOrder(open, null, orderId).then((order) => {
			setCurrentOrder(order);
			setErrorMessage(order?.errorMessages?.[0] || '');
		});
	}, [open, orderId]);

	useEffect(() => refreshOrder(), [refreshOrder]);

	useEffect(() => getActions({}), [getActions]);

	useEffect(() => {
		const refreshActionsAndOrder = (event) => {
			getActions(event);

			refreshOrder();
		};

		Liferay.on(commerceEvents.CART_UPDATED, refreshActionsAndOrder);
		Liferay.on(
			commerceEvents.ORDER_INFORMATION_ALTERED,
			refreshActionsAndOrder
		);

		return () => {
			Liferay.detach(commerceEvents.CART_UPDATED, refreshActionsAndOrder);
			Liferay.detach(
				commerceEvents.ORDER_INFORMATION_ALTERED,
				refreshActionsAndOrder
			);
		};
	}, [getActions, refreshOrder]);

	useEffect(() => {
		if (open && errorMessage) {
			openModal({
				bodyHTML: errorMessage,
				center: true,
				containerProps: {
					className: 'commerce-modal',
				},
				size: 'md',
				status: 'warning',
				title: Liferay.Language.get('warning'),
			});
		}
	}, [errorMessage, open]);

	const onClick = (event, action) => {
		event.preventDefault();

		if (action.name === 'checkout') {
			handleOrderActionRedirect({
				checkoutURL,
				id: action.name,
			});
		}
		else if (action.name === 'make-return') {
			Liferay.fire(`${namespace || ''}makeReturn`, {
				accountId: Liferay.CommerceContext.account.accountId,
				orderId,
			});
		}
		else if (action.name === 'quick-checkout') {
			if (
				currentOrder?.paymentMethodType === PAYMENT_METHOD_TYPE_OFFLINE
			) {
				CommerceServiceProvider.DeliveryCartAPI('v1')
					.checkoutCartById(orderId)
					.then(() => {
						window.location.reload();
					})
					.catch((error) => {
						openToast({
							message:
								error.message ||
								Liferay.Language.get(
									'an-unexpected-error-occurred'
								),
							type: 'danger',
						});
					});
			}
			else {
				window.location.href = orderSummaryURL;
			}
		}
		else {
			const executeTransitions = open
				? CommerceServiceProvider.DeliveryCartAPI('v1')
						.executeCartTransitionsById
				: CommerceServiceProvider.DeliveryOrderAPI('v1')
						.executeOrderTransitionsById;

			executeTransitions(orderId, action)
				.then((response) => {
					Liferay.fire(commerceEvents.ORDER_INFORMATION_ALTERED);

					if (open !== response?.open) {
						setOpen(response.open);
					}
					else {
						getActions({});
					}

					if (action.name === 'reorder') {
						handleOrderActionRedirect({
							id: response.name,
							orderId: response.orderId,
							reorderURL,
						});
					}
				})
				.catch((error) => {
					openToast({
						message:
							error.message ||
							Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
						type: 'danger',
					});
				});
		}
	};

	return (
		<div className="align-items-center d-flex">
			{actions.map((action) => {
				if (action.name === 'request-quote') {
					return (
						<RequestQuote
							data={{
								cartId: orderId,
								createCart: false,
								notesPermission: manageNotesPermission,
								orderDetailURL: orderSummaryURL,
								restrictedNotesPermission:
									manageRestrictedNotesPermission,
							}}
							key={action.name}
							style={{
								displayType: 'primary',
								size: 'md',
							}}
						/>
					);
				}

				return (
					<div key={action.name}>
						<ClayButton
							aria-label={action.label}
							className="mx-1"
							disabled={action?.disabled}
							displayType="primary"
							onClick={(event) => onClick(event, action)}
						>
							{action.label}
						</ClayButton>
					</div>
				);
			})}
		</div>
	);
}

export default OrderActions;
