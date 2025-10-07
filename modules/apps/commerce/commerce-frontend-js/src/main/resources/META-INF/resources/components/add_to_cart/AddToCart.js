/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classnames from 'classnames';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import ServiceProvider from '../../ServiceProvider/index';
import {
	CART_PRODUCT_QUANTITY_CHANGED,
	CP_INSTANCE_CHANGED,
	CP_QUANTITY_SELECTOR_CHANGED,
	CP_UNIT_OF_MEASURE_SELECTOR_CHANGED,
} from '../../utilities/eventsDefinitions';
import {useCommerceAccount, useCommerceCart} from '../../utilities/hooks';
import {getMinQuantity, getMultipleQuantity} from '../../utilities/quantities';
import QuantitySelector from '../quantity_selector/QuantitySelector';
import UnitOfMeasureSelector from '../unit_of_measure_selector/UnitOfMeasureSelector';
import AddToCartButton from './AddToCartButton';
import {ALL} from './constants';

const CartResource = ServiceProvider.DeliveryCartAPI('v1');

function getQuantity(settings, skuUnitOfMeasure) {
	if (settings?.productConfiguration?.allowedOrderQuantities?.length) {
		return Math.min(
			...settings.productConfiguration.allowedOrderQuantities
		);
	}

	return Number(
		getMinQuantity(
			skuUnitOfMeasure
				? settings?.productConfiguration?.minOrderQuantity
				: Math.ceil(settings?.productConfiguration?.minOrderQuantity),
			getMultipleQuantity(
				skuUnitOfMeasure?.incrementalOrderQuantity,
				settings?.productConfiguration?.multipleOrderQuantity,
				skuUnitOfMeasure?.precision || 0
			),
			skuUnitOfMeasure?.precision || 0
		)
	);
}

function AddToCart({
	accountId: initialAccountId,
	cartId: initialCartId,
	cartUUID: initialCartUUID,
	channel,
	cpInstance: initialCpInstance,
	disabled: initialDisabled,
	guestOrderEnabled,
	productId,
	settings,
}) {
	const account = useCommerceAccount({id: initialAccountId});
	const cart = useCommerceCart({
		channelGroupId: channel.groupId,
		guestOrderEnabled,
		initialCart: {
			UUID: initialCartUUID,
			id: initialCartId,
		},
	});
	const [cpInstance, setCpInstance] = useState({
		...initialCpInstance,
		quantity: getQuantity(settings, initialCpInstance.skuUnitOfMeasure),
		validQuantity: true,
	});
	const inputRef = useRef(null);

	const inputDisabled = useMemo(() => {
		if (
			initialDisabled ||
			!account?.id ||
			cpInstance.disabled ||
			cpInstance.published === false ||
			cpInstance.purchasable === false ||
			(cpInstance.availability?.stockQuantity !== undefined &&
				cpInstance.backOrderAllowed === false &&
				cpInstance.availability?.stockQuantity <= 0)
		) {
			return true;
		}

		return false;
	}, [account, cpInstance, initialDisabled]);

	const buttonDisabled = useMemo(() => {
		if (inputDisabled || !cpInstance.quantity) {
			return true;
		}

		return false;
	}, [cpInstance, inputDisabled]);

	useEffect(() => {
		setCpInstance({
			...initialCpInstance,
			quantity: getQuantity(settings, initialCpInstance.skuUnitOfMeasure),
			validQuantity: true,
		});
	}, [initialCpInstance, settings]);

	const handleCPInstanceReplaced = useCallback(
		({cpInstance: incomingCpInstance}) => {
			function updateInCartState(inCart) {
				setCpInstance((cpInstance) => ({
					...cpInstance,
					availability: incomingCpInstance.availability,
					backOrderAllowed: incomingCpInstance.backOrderAllowed,
					disabled: incomingCpInstance.disabled,
					inCart,
					published: incomingCpInstance.published,
					purchasable: incomingCpInstance.purchasable,
					skuId: incomingCpInstance.skuId,
					skuOptions: Array.isArray(incomingCpInstance.skuOptions)
						? incomingCpInstance.skuOptions
						: JSON.parse(incomingCpInstance.skuOptions),
					stockQuantity:
						incomingCpInstance.availability.stockQuantity,
				}));
			}

			if (cart.id) {
				updateInCartState(cpInstance.inCart);
			}
			else {
				updateInCartState(false);
			}
		},
		[cart.id, cpInstance]
	);

	useEffect(() => {
		function handleQuantityChanged({quantity, skuId}) {
			setCpInstance((cpInstance) => {
				const isModified =
					skuId === cpInstance.skuId ||
					skuId.toString() === cpInstance.skuId ||
					skuId === ALL;

				return {
					...cpInstance,
					inCart: isModified ? Boolean(quantity) : cpInstance.inCart,
				};
			});
		}

		function handleUOMChanged({unitOfMeasure}) {
			if (cart.id) {
				CartResource.getItemsByCartId(cart.id).then(({items}) => {
					let inCart = false;

					if (unitOfMeasure) {
						inCart = items.some(
							({skuId, skuUnitOfMeasure}) =>
								cpInstance.skuId === skuId &&
								skuUnitOfMeasure?.key &&
								unitOfMeasure?.key === skuUnitOfMeasure?.key
						);
					}
					else {
						inCart = items.some(({skuId, skuUnitOfMeasure}) => {
							return (
								cpInstance.skuId === skuId &&
								!skuUnitOfMeasure?.key
							);
						});
					}

					setCpInstance((cpInstance) => ({
						...cpInstance,
						inCart,
						skuUnitOfMeasure: unitOfMeasure,
					}));
				});
			}
			else {
				setCpInstance((cpInstance) => ({
					...cpInstance,
					inCart: false,
					skuUnitOfMeasure: unitOfMeasure,
				}));
			}
		}

		Liferay.on(CART_PRODUCT_QUANTITY_CHANGED, handleQuantityChanged);

		Liferay.on(
			`${settings.namespace}${CP_INSTANCE_CHANGED}`,
			handleCPInstanceReplaced
		);

		Liferay.on(
			`${settings.namespace}${CP_UNIT_OF_MEASURE_SELECTOR_CHANGED}`,
			handleUOMChanged
		);

		return () => {
			Liferay.detach(
				CART_PRODUCT_QUANTITY_CHANGED,
				handleQuantityChanged
			);

			Liferay.detach(
				`${settings.namespace}${CP_INSTANCE_CHANGED}`,
				handleCPInstanceReplaced
			);

			Liferay.detach(
				`${settings.namespace}${CP_UNIT_OF_MEASURE_SELECTOR_CHANGED}`,
				handleUOMChanged
			);
		};
	}, [cart.id, cpInstance.skuId, handleCPInstanceReplaced, settings]);

	const spaceDirection = settings.inline ? 'ml' : 'mt';
	const spacer = 0;

	return (
		<div
			className={classnames({
				'add-to-cart-wrapper': true,
				'align-items-end': true,
				'd-flex': false,
				'flex-column': !settings.inline,
			})}
		>
			<div
				className={classnames({
					'd-flex': true,
					'justify-content-center':
						!settings.showUnitOfMeasureSelector,
					'mb-3': true,
				})}
			>
				<QuantitySelector
					allowedQuantities={
						settings.productConfiguration?.allowedOrderQuantities
					}
					disabled={inputDisabled}
					max={settings.productConfiguration?.maxOrderQuantity}
					min={settings.productConfiguration?.minOrderQuantity}
					namespace={settings.namespace}
					onUpdate={({errors, value: quantity}) => {
						setCpInstance((cpInstance) => ({
							...cpInstance,
							quantity,
							validQuantity: !errors.length,
						}));
						Liferay.fire(
							`${settings.namespace}${CP_QUANTITY_SELECTOR_CHANGED}`,
							{errors, quantity}
						);
					}}
					quantity={cpInstance.quantity}
					ref={inputRef}
					size={settings.size}
					step={settings.productConfiguration?.multipleOrderQuantity}
					unitOfMeasure={cpInstance.skuUnitOfMeasure}
				/>

				{settings.showUnitOfMeasureSelector && (
					<UnitOfMeasureSelector
						accountId={account.id}
						channelId={channel.id}
						cpInstanceId={cpInstance.skuId}
						currencyCode={
							Liferay.CommerceContext
								? Liferay.CommerceContext.currency.currencyCode
								: ''
						}
						namespace={settings.namespace}
						productConfiguration={settings.productConfiguration}
						productId={productId}
						size={settings.size}
					/>
				)}
			</div>

			<AddToCartButton
				accountId={account.id}
				cartId={cart.id}
				channel={channel}
				className={`${spaceDirection}-${spacer}`}
				cpInstances={[cpInstance]}
				disabled={buttonDisabled}
				notAllowed={!cpInstance.validQuantity}
				onAdd={() => {
					setCpInstance((cpInstance) => ({
						...cpInstance,
						inCart: true,
					}));
				}}
				settings={settings}
			/>
		</div>
	);
}

AddToCart.propTypes = {
	accountId: PropTypes.number.isRequired,
	cartId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	cpInstance: PropTypes.shape({
		skuId: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
			.isRequired,
		skuOptions: PropTypes.array,
	}),
	disabled: PropTypes.bool,
	productId: PropTypes.number,
	settings: PropTypes.shape({
		alignment: PropTypes.oneOf(['center', 'left', 'right', 'full-width']),
		inline: PropTypes.bool,
		namespace: PropTypes.string,
		productConfiguration: PropTypes.shape({
			allowedOrderQuantities: PropTypes.arrayOf(PropTypes.number),
			maxOrderQuantity: PropTypes.number,
			minOrderQuantity: PropTypes.number,
			multipleOrderQuantity: PropTypes.number,
		}),
		showUnitOfMeasureSelector: PropTypes.bool,
		size: PropTypes.oneOf(['lg', 'md', 'sm']),
	}),
};

export default AddToCart;
