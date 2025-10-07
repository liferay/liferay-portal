/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AJAX from '../../../utilities/AJAX/index';

const CART_COMMENTS_PATH = '/cart-comments';
const CARTS_PATH = '/carts';
const CHANNELS_PATH = '/channels';

const VERSION = 'v1.0';

function resolveCartsPath(basePath = '', cartId) {
	return `${basePath}${VERSION}${CARTS_PATH}/${cartId}`;
}

function resolveCartsBatchPath(basePath = '') {
	return `${basePath}${VERSION}${CARTS_PATH}/batch`;
}
function resolveChannelsPath(basePath = '', channelId) {
	return `${basePath}${VERSION}${CHANNELS_PATH}/${channelId}`;
}

function resolveCartsByAccountIdAndChannelIdPath(
	basePath = '',
	accountId,
	channelId,
	searchParams
) {
	const url = new URL(
		`${Liferay.ThemeDisplay.getPathContext()}${resolveChannelsPath(
			basePath,
			channelId
		)}/account/${accountId}${CARTS_PATH}`,
		Liferay.ThemeDisplay.getPortalURL()
	);

	if (searchParams) {
		Object.keys(searchParams).forEach((searchParamKey) => {
			url.searchParams.set(searchParamKey, searchParams[searchParamKey]);
		});
	}

	return url.pathname + url.search;
}

function resolveCartCommentsPath(basePath = '', cartCommentId) {
	return `${basePath}${VERSION}${CART_COMMENTS_PATH}/${cartCommentId}`;
}

export default function Cart(basePath) {
	return {
		addAttachment: (cartId, json) =>
			AJAX.POST(
				`${resolveCartsPath(basePath, cartId)}/attachments/by-base64`,
				json
			),

		cartsByAccountIdAndChannelIdURL: (accountId, channelId) =>
			resolveCartsByAccountIdAndChannelIdPath(
				basePath,
				accountId,
				channelId
			),

		checkoutCartById: (cartId) =>
			AJAX.POST(`${resolveCartsPath(basePath, cartId)}/checkout`),

		createCartByChannelId: (channelId, json) =>
			AJAX.POST(
				`${resolveChannelsPath(
					basePath,
					channelId
				)}${CARTS_PATH}?nestedFields=cartItems`,
				json
			),

		createCommentsByCartId: (cartId, json) =>
			AJAX.POST(resolveCartsPath(basePath, cartId) + '/comments', json),

		createCouponCodeByCartId: (cartId, json) =>
			AJAX.POST(
				`${resolveCartsPath(basePath, cartId)}/coupon-code`,
				json
			),

		deleteAttachment: (cartId, attachmentId) =>
			AJAX.DELETE(
				`${resolveCartsPath(basePath, cartId)}/attachments/${attachmentId}`
			),

		deleteCartById: (cartId) =>
			AJAX.DELETE(resolveCartsPath(basePath, cartId)),

		deleteCartsById: (items) =>
			AJAX.DELETE(resolveCartsBatchPath(basePath), {
				body: JSON.stringify(items),
			}),

		deleteCommentsByCartId: (cartCommentId) =>
			AJAX.DELETE(resolveCartCommentsPath(basePath, cartCommentId)),

		executeCartTransitionsById: (cartId, json) =>
			AJAX.POST(
				resolveCartsPath(basePath, cartId) + '/cart-transitions',
				json
			),

		getCartById: (cartId) => AJAX.GET(resolveCartsPath(basePath, cartId)),

		getCartByIdWithItems: (cartId) =>
			AJAX.GET(
				resolveCartsPath(basePath, cartId) + '?nestedFields=cartItems'
			),

		getCartDeliveryTermsPage: (cartId) =>
			AJAX.GET(resolveCartsPath(basePath, cartId) + '/delivery-terms'),

		getCartItemsByCartId: (cartId, params = {}) =>
			AJAX.GET(`${resolveCartsPath(basePath, cartId)}/items`, {}, params),

		getCartPaymentMethodsPage: (cartId) =>
			AJAX.GET(resolveCartsPath(basePath, cartId) + '/payment-methods'),

		getCartPaymentTermsPage: (cartId) =>
			AJAX.GET(resolveCartsPath(basePath, cartId) + '/payment-terms'),

		getCartShippingMethodsPage: (cartId) =>
			AJAX.GET(resolveCartsPath(basePath, cartId) + '/shipping-methods'),

		getCartTransitionsById: (cartId) =>
			AJAX.GET(resolveCartsPath(basePath, cartId) + '/cart-transitions'),

		getCartsByAccountIdAndChannelId: (accountId, channelId, searchParams) =>
			AJAX.GET(
				resolveCartsByAccountIdAndChannelIdPath(
					basePath,
					accountId,
					channelId,
					searchParams
				)
			),

		getCommentsByCartId: (cartId) =>
			AJAX.GET(resolveCartsPath(basePath, cartId) + '/comments'),

		patchCommentsByCartId: (cartId) =>
			AJAX.PATCH(resolveCartsPath(basePath, cartId) + '/comments'),

		replaceCartById: (cartId, json) =>
			AJAX.PUT(resolveCartsPath(basePath, cartId), json),

		updateCartById: (cartId, jsonProps, params) =>
			AJAX.PATCH(
				resolveCartsPath(basePath, cartId) + '?nestedFields=cartItems',
				jsonProps,
				{},
				params
			),
	};
}
