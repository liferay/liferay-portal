/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast, sub} from 'frontend-js-web';

import {ACCOUNT_ENTRY_ID_DEFAULT} from '../../../utilities/constants';
import {
	DEFAULT_ORDER_DETAILS_PORTLET_ID,
	MAXIMUM_ALLOWED_QUANTITY_NOT_VALID_ERROR,
	MAXIMUM_PRODUCT_QUANTITY_NOT_VALID_ERROR,
	MINIMUM_PRODUCT_QUANTITY_NOT_VALID_ERROR,
	ORDER_DETAILS_ENDPOINT,
	ORDER_UUID_PARAMETER,
	PRODUCT_MULTIPLE_OF_QUANTITY_NOT_VALID_ERROR,
	PRODUCT_QUANTITY_NOT_VALID_ERROR,
	WORKFLOW_STATUS_APPROVED,
} from './constants';

export function canSubmit({
	accountId: rawAccountId,
	cartItems = [],
	id: orderId,
	workflowStatusInfo: {code: workflowStatus = WORKFLOW_STATUS_APPROVED} = {},
}) {
	const accountId = parseInt(rawAccountId, 10);

	const areAccountAndOrderSelected =
		accountId !== ACCOUNT_ENTRY_ID_DEFAULT && !!orderId;
	const areItemsPurchasable =
		!hasErrors(cartItems) && workflowStatus === WORKFLOW_STATUS_APPROVED;

	return areAccountAndOrderSelected && areItemsPurchasable;
}

export function getCorrectedQuantity(
	productConfiguration,
	sku,
	cartItems,
	precision = 0
) {
	const {
		allowedOrderQuantities,
		maxOrderQuantity,
		minOrderQuantity,
		multipleOrderQuantity,
	} = productConfiguration;

	let quantity;

	if (!allowedOrderQuantities.length) {
		quantity = minOrderQuantity;
	}

	const existingItem = cartItems.find((item) => item.sku === sku);

	const lastAllowedQuantity =
		allowedOrderQuantities[allowedOrderQuantities.length - 1];

	if (existingItem) {
		if (allowedOrderQuantities.length) {
			const nextAllowedQuantity = allowedOrderQuantities.find(
				(allowedQuantity) => {
					if (multipleOrderQuantity > 1) {
						return (
							allowedQuantity > existingItem.quantity &&
							allowedQuantity % multipleOrderQuantity === 0
						);
					}

					return allowedQuantity > existingItem.quantity;
				}
			);

			allowedOrderQuantities.forEach((allowedQuantity) => {
				if (allowedQuantity > existingItem.quantity) {
					quantity = nextAllowedQuantity - existingItem.quantity;
				}
			});

			if (multipleOrderQuantity > 1 && !nextAllowedQuantity) {
				openToast({
					message: sub(PRODUCT_QUANTITY_NOT_VALID_ERROR),
					type: 'danger',
				});

				return 0;
			}

			if (existingItem.quantity >= lastAllowedQuantity) {
				quantity = 0;
			}
		}
		else if (existingItem.quantity >= multipleOrderQuantity) {
			quantity = multipleOrderQuantity;
		}

		if (existingItem.quantity + quantity > maxOrderQuantity) {
			if (multipleOrderQuantity > 1) {
				openToast({
					message: sub(
						MAXIMUM_PRODUCT_QUANTITY_NOT_VALID_ERROR,
						maxOrderQuantity
					),
					type: 'danger',
				});

				return 0;
			}
			else {
				openToast({
					message: sub(
						MAXIMUM_PRODUCT_QUANTITY_NOT_VALID_ERROR,
						maxOrderQuantity
					),
					type: 'danger',
				});

				return 0;
			}
		}
	}
	else if (allowedOrderQuantities.length) {
		quantity = allowedOrderQuantities.find(
			(quantity) =>
				quantity >= minOrderQuantity &&
				quantity % multipleOrderQuantity === 0
		);

		if (maxOrderQuantity < allowedOrderQuantities[0]) {
			openToast({
				message: sub(
					MAXIMUM_PRODUCT_QUANTITY_NOT_VALID_ERROR,
					maxOrderQuantity
				),
				type: 'danger',
			});

			return 0;
		}

		if (minOrderQuantity > lastAllowedQuantity) {
			openToast({
				message: sub(
					MINIMUM_PRODUCT_QUANTITY_NOT_VALID_ERROR,
					minOrderQuantity
				),
				type: 'danger',
			});

			return 0;
		}
	}
	else if (multipleOrderQuantity > minOrderQuantity) {
		quantity = multipleOrderQuantity;

		if (multipleOrderQuantity > maxOrderQuantity) {
			openToast({
				message: sub(
					MAXIMUM_PRODUCT_QUANTITY_NOT_VALID_ERROR,
					maxOrderQuantity
				),
				type: 'danger',
			});

			return 0;
		}
	}
	else if (multipleOrderQuantity < minOrderQuantity) {
		quantity = multipleOrderQuantity;

		while (quantity < minOrderQuantity) {
			quantity += multipleOrderQuantity;
		}
	}

	if (minOrderQuantity > maxOrderQuantity) {
		quantity = 0;
	}

	if (multipleOrderQuantity > 1 && quantity % multipleOrderQuantity !== 0) {
		openToast({
			message: sub(
				PRODUCT_MULTIPLE_OF_QUANTITY_NOT_VALID_ERROR,
				multipleOrderQuantity
			),
			type: 'danger',
		});

		return 0;
	}

	if (quantity === 0) {
		openToast({
			message: sub(
				MAXIMUM_ALLOWED_QUANTITY_NOT_VALID_ERROR,
				sku,
				lastAllowedQuantity > 1 ? lastAllowedQuantity : maxOrderQuantity
			),
			type: 'danger',
		});
	}

	return Number(quantity.toFixed(precision));
}

export function generateProductPageURL(
	baseURL,
	productRelativeURLs,
	productURLSeparator
) {
	const actualLang = themeDisplay.getLanguageId();
	let productLocalizedURL = productRelativeURLs[actualLang];

	if (!productLocalizedURL) {
		const defaultLang = themeDisplay.getDefaultLanguageId();

		productLocalizedURL = productRelativeURLs[defaultLang];
	}

	return productLocalizedURL
		? [baseURL, productURLSeparator, productLocalizedURL]
				.map((url) => url.replace(/^\//, '').replace(/\/$/, ''))
				.join('/')
		: '';
}

export function hasErrors(cartItems) {
	return cartItems.some(({errorMessages}) => Boolean(errorMessages?.length));
}

export function hasOptions(jsonString) {
	let options = [];

	try {
		options = JSON.parse(jsonString) || [];
	}
	catch (ignore) {}

	return options.length;
}

export function hasPriceOnApplication(cartItems) {
	return cartItems.some(({price}) => price.priceOnApplication);
}

export function parseOptions(options) {
	return Array.isArray(options)
		? options.map(({value}) => `${value}`).join(', ')
		: options;
}

export function filterOptions(jsonString) {
	let options;

	try {
		options = JSON.parse(jsonString) || [];
	}
	catch (ignore) {
		options = [];
	}

	return options.filter((option) => !!option.value.length);
}

export function parseValue(value) {
	return Array.isArray(value)
		? value.filter((item) => item === 0 || item).join(', ')
		: value;
}

export function regenerateOrderDetailURL(orderUUID, siteDefaultURL) {
	if (!orderUUID || !siteDefaultURL) {
		throw new Error(
			`Cannot generate a new Order Detail URL. Invalid "${
				siteDefaultURL ? 'orderUUID' : 'siteDefaultURL'
			}"`
		);
	}

	const orderDetailURL = new URL(
		`${siteDefaultURL}${ORDER_DETAILS_ENDPOINT}`
	);

	orderDetailURL.searchParams.append(
		'p_p_id',
		DEFAULT_ORDER_DETAILS_PORTLET_ID
	);
	orderDetailURL.searchParams.append('p_p_lifecycle', '0');
	orderDetailURL.searchParams.append(
		`_${DEFAULT_ORDER_DETAILS_PORTLET_ID}_mvcRenderCommandName`,
		'/commerce_open_order_content/edit_commerce_order'
	);

	orderDetailURL.searchParams.append(
		`_${DEFAULT_ORDER_DETAILS_PORTLET_ID}_${ORDER_UUID_PARAMETER}`,
		orderUUID
	);

	return orderDetailURL.toString();
}

export function summaryDataMapper({
	itemsQuantity,
	subtotalDiscountValueFormatted,
	subtotalFormatted,
	totalDiscountValueFormatted,
	totalFormatted,
}) {
	return [
		{
			label: Liferay.Language.get('quantity'),
			value: itemsQuantity,
		},
		{
			label: Liferay.Language.get('subtotal'),
			value: subtotalFormatted,
		},
		{
			label: Liferay.Language.get('subtotal-discount'),
			value: subtotalDiscountValueFormatted,
		},
		{
			label: Liferay.Language.get('order-discount'),
			value: totalDiscountValueFormatted,
		},
		{
			label: Liferay.Language.get('total'),
			style: 'big',
			value: totalFormatted,
		},
	];
}
