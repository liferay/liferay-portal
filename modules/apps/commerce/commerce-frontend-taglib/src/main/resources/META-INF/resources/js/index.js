/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	AddToCart,
	AddToWishList,
	DropdownMenu,
	MiniCart,
	Modal,
	Price,
	RequestQuote,
	StepTracker,
	accountSelector,
	compareCheckbox,
} from 'commerce-frontend-js';

import '../css/main.scss';

export {default as discontinuedLabelCPInstanceChangeHandler} from './discontinued_label/DiscontinuedLabelCPInstanceChangeHandler';
export {default as infoBoxHandler} from './info_box';
export {default as searchBar} from './search_bar/SearchBar';
export {default as searchResults} from './search_results/SearchResults';

export function accountSelectorTag({
	accountEntryAllowedTypes,
	accountSelectorId,
	checkoutURL,
	commerceChannelId,
	createNewOrderURL,
	currencyCode,
	currentCommerceAccount,
	currentCommerceOrder,
	hasAddCommerceOrderPermission,
	hasCommerceOpenOrderContentPortlet,
	hasManageAccountsPermission,
	orderTypes,
	refreshPageOnAccountSelected,
	selectOrderURL,
	setCurrentAccountURL,
}) {
	accountSelector(accountSelectorId, accountSelectorId, {
		accountEntryAllowedTypes:
			typeof accountEntryAllowedTypes === 'string'
				? JSON.parse(accountEntryAllowedTypes)
				: accountEntryAllowedTypes,
		checkoutURL,
		commerceChannelId,
		createNewOrderURL,
		currencyCode,
		currentCommerceAccount,
		currentCommerceOrder,
		hasAddCommerceOrderPermission,
		hasCommerceOpenOrderContentPortlet,
		hasManageAccountsPermission,
		orderTypes,
		refreshPageOnAccountSelected,
		selectOrderURL,
		setCurrentAccountURL,
	});
}

export function addToListWish({
	accountId,
	addToWishListId,
	cpDefinitionId,
	isInWishList,
	large,
	skuId,
}) {
	AddToWishList(addToWishListId, addToWishListId, {
		accountId: Number(accountId),
		cpDefinitionId: Number(cpDefinitionId),
		isInWishList,
		large,
		skuId: Number(skuId),
	});
}

export function compareCheckboxTag({
	commerceChannelGroupId,
	disabled,
	inCompare,
	itemId,
	label,
	pictureUrl,
	refreshOnRemove,
	rootId,
}) {
	compareCheckbox(rootId, rootId, {
		commerceChannelGroupId: Number(commerceChannelGroupId),
		disabled,
		inCompare,
		itemId,
		label,
		pictureUrl,
		refreshOnRemove,
	});
}

export function dropdownMain({items, spritemap}) {
	DropdownMenu('dropdown-header', 'dropdown-header-container', {
		items,
		spritemap,
	});
}

export function modal({
	containerId,
	id,
	portletId,
	refreshPageOnClose,
	size,
	spritemap,
	title,
	url,
}) {
	Modal(id, containerId, {
		id,
		onClose: refreshPageOnClose
			? function () {
					window.location.reload();
				}
			: null,
		portletId,
		size,
		spritemap,
		title,
		url,
	});
}

export function stepTracker({portletId, spritemap, stepTrackerId, steps}) {
	StepTracker(stepTrackerId, stepTrackerId, {
		portletId,
		spritemap,
		steps,
	});
}

export function price({
	containerId,
	displayDiscountLevels,
	namespace,
	netPrice,
	price: priceProp,
	standalone,
}) {
	Price(containerId, containerId, {
		displayDiscountLevels,
		namespace,
		netPrice,
		price: priceProp,
		standalone,
	});
}

export function addToCart({
	accountId,
	addToCartId,
	cartId,
	cpInstance,
	productId,
	skuOptions,
	...otherProps
}) {
	cpInstance.skuOptions =
		cpInstance.skuOptions && JSON.parse(cpInstance.skuOptions);

	AddToCart(addToCartId, addToCartId, {
		...otherProps,
		accountId: Number(accountId),
		cartId: Number(cartId),
		cpInstance,
		productId: Number(productId),
		skuOptions: skuOptions && JSON.parse(skuOptions),
	});
}

export function requestQuote({
	accountId,
	channel,
	cpDefinitionId,
	cpInstance,
	disabled,
	namespace,
	orderDetailURL,
	requestQuoteElementId,
}) {
	if (cpInstance.skuOptions && typeof cpInstance.skuOptions === 'string') {
		try {
			cpInstance.skuOptions = JSON.parse(cpInstance.skuOptions);
		}
		catch (event) {}
	}

	RequestQuote(requestQuoteElementId, requestQuoteElementId, {
		accountId: Number(accountId),
		channel,
		cpDefinitionId,
		cpInstance,
		disabled,
		namespace,
		orderDetailURL,
	});
}

export function cart({
	accountId,
	cartViews,
	checkoutURL,
	currencyCode,
	detachedOpener,
	displayDiscountLevels,
	displayTotalItemsQuantity,
	groupId,
	guestOrderEnabled,
	id,
	itemsQuantity,
	labels,
	miniCartId,
	orderDetailURL,
	orderId,
	productURLSeparator,
	requestQuoteEnabled,
	signInURL,
	siteDefaultURL,
	toggleable,
}) {
	MiniCart(miniCartId, miniCartId, {
		accountId: Number(accountId),
		cartActionURLs: {
			checkoutURL,
			orderDetailURL,
			productURLSeparator,
			signInURL,
			siteDefaultURL,
		},
		cartViews,
		channel: {
			currencyCode,
			groupId,
			id,
		},
		detachedOpener,
		displayDiscountLevels,
		displayTotalItemsQuantity,
		guestOrderEnabled,
		itemsQuantity: Number(itemsQuantity),
		labels,
		orderId: Number(orderId),
		requestQuoteEnabled,
		toggleable,
	});
}
