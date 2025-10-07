/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classnames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useCallback, useMemo, useState} from 'react';

import cartAtom from '../../utilities/atoms/cartAtom';
import skuOptionsAtom from '../../utilities/atoms/skuOptionsAtom';
import {showErrorNotification} from '../../utilities/notifications';
import {addToCart} from './data';

import './add_to_cart.scss';
import {ACCOUNT_ENTRY_ID_DEFAULT} from '../../utilities/constants';
import {selectOrderType} from '../../utilities/modals/selectOrderType';

function AddToCartButton({
	accountId,
	cartId,
	channel,
	className,
	cpInstances,
	disabled = false,
	hideIcon,
	notAllowed,
	onAdd,
	onError,
	settings,
}) {
	const [cartAtomState, setCartAtomState] = useLiferayState(cartAtom);
	const [skuOptionsAtomState] = useLiferayState(skuOptionsAtom);
	const [isTriggeringCartUpdate, setIsTriggeringCartUpdate] = useState(false);
	const isMounted = useIsMounted();

	const buttonDisabled = useMemo(
		() =>
			(skuOptionsAtomState.errors?.length &&
				skuOptionsAtomState.namespace === settings.namespace) ||
			disabled,
		[
			disabled,
			skuOptionsAtomState.errors,
			skuOptionsAtomState.namespace,
			settings.namespace,
		]
	);

	const handleClickAddToCart = useCallback(
		(orderTypeId = null) => {
			if (cartAtomState.updating) {
				return;
			}

			setIsTriggeringCartUpdate(true);

			setCartAtomState({updating: true});

			return addToCart(
				cpInstances,
				cartId,
				channel,
				accountId,
				orderTypeId,
				settings.namespace,
				skuOptionsAtomState.skuOptions,
				skuOptionsAtomState.namespace
			)
				.then(onAdd)
				.catch((error) => {
					console.error(error);

					let errorMessage;

					if (error.message) {
						errorMessage = error.message;
					}
					else if (error.detail) {
						errorMessage = error.detail;
					}
					else {
						errorMessage =
							cpInstances.length > 1
								? Liferay.Language.get(
										'unable-to-add-products-to-the-cart'
									)
								: Liferay.Language.get(
										'unable-to-add-product-to-the-cart'
									);
					}

					showErrorNotification(errorMessage);

					onError(error);
				})
				.finally(() => {
					setCartAtomState({updating: false});

					setIsTriggeringCartUpdate(false);
				});
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[
			accountId,
			cartAtomState.updating,
			cartId,
			channel,
			cpInstances,
			isMounted,
			onAdd,
			onError,
			setCartAtomState,
			skuOptionsAtomState.namespace,
			skuOptionsAtomState.skuOptions,
		]
	);

	return (
		<ClayButton
			block={settings.alignment === 'full-width'}
			className={classnames(className, {
				[`btn-${settings.size}`]: settings.size,
				'btn-add-to-cart': true,
				'icon-only': settings.iconOnly,
				'is-added': cpInstances.length === 1 && cpInstances[0].inCart,
				'not-allowed':
					notAllowed ||
					(cartAtomState.updating && !isTriggeringCartUpdate),
			})}
			disabled={buttonDisabled}
			displayType="primary"
			monospaced={settings.iconOnly && settings.inline}
			onClick={async (event) => {
				event.preventDefault();

				const hasInvalidQuantities = cpInstances.some(
					({validQuantity}) => !validQuantity
				);

				if (hasInvalidQuantities) {
					return;
				}

				const {orderTypes = []} = Liferay?.CommerceContext;

				let orderTypeId = null;

				if (
					accountId > ACCOUNT_ENTRY_ID_DEFAULT &&
					!cartId &&
					orderTypes.length > 1
				) {
					try {
						orderTypeId = await selectOrderType(orderTypes);
					}
					catch ({message, title}) {
						if (message !== 'cancel') {
							openToast({
								message:
									title ||
									Liferay.Language.get(
										'an-unexpected-error-occurred'
									),
								type: 'danger',
							});
						}

						return;
					}
				}

				return handleClickAddToCart(orderTypeId);
			}}
		>
			{!settings.iconOnly && (
				<span className="text-truncate-inline">
					<span className="text-truncate">
						{settings.buttonText ||
							Liferay.Language.get('add-to-cart')}
					</span>
				</span>
			)}

			{!hideIcon && (
				<span className="cart-icon">
					<ClayIcon symbol="shopping-cart" />
				</span>
			)}
		</ClayButton>
	);
}

AddToCartButton.defaultProps = {
	accountId: null,
	cartId: 0,
	cpInstances: [
		{
			inCart: false,
			skuOptions: '[]',
		},
	],
	hideIcon: false,
	onAdd: () => {},
	onError: () => {},
	settings: {
		iconOnly: false,
		inline: false,
	},
};

AddToCartButton.propTypes = {
	accountId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	cartId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	channel: PropTypes.shape({
		currencyCode: PropTypes.string.isRequired,
		id: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
			.isRequired,
	}),
	cpInstances: PropTypes.arrayOf(
		PropTypes.shape({
			inCart: PropTypes.bool,
			quantity: PropTypes.number,
			skuId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
			skuOptions: PropTypes.oneOfType([
				PropTypes.string,
				PropTypes.array,
			]),
		})
	).isRequired,
	disabled: PropTypes.bool,
	hideIcon: PropTypes.bool,
	notAllowed: PropTypes.bool,
	onAdd: PropTypes.func.isRequired,
	onError: PropTypes.func.isRequired,
	settings: PropTypes.shape({
		alignment: PropTypes.oneOf(['center', 'left', 'right', 'full-width']),
		buttonText: PropTypes.string,
		iconOnly: PropTypes.bool,
		inline: PropTypes.bool,
	}),
};

export default AddToCartButton;
