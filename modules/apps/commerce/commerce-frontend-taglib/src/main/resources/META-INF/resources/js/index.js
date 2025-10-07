/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	AddToCart,
	AddToWishList,
	DropdownMenu,
	MiniCart,
	Price,
	RequestQuote,
	StepTracker,
	accountSelector,
	compareCheckbox,
} from 'commerce-frontend-js';

import '../css/main.scss';

export {default as discontinuedLabelCPInstanceChangeHandler} from './discontinued_label/DiscontinuedLabelCPInstanceChangeHandler';
export {default as ExternalReferenceCodeButtonPropsTransformer} from './header';
export {default as ModalActionContextHandler} from './info_box';
export {default as ModalContentHandler} from './modal_content';
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
	orderSelectionDisabled,
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
		orderSelectionDisabled,
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
	baseOrderDetailURL,
	cartViews,
	checkoutURL,
	currencyCode,
	detachedOpener,
	displayDiscountLevels,
	displayTotalItemsQuantity,
	groupId,
	guestOrderEnabled,
	hasCommerceOpenOrderContentPortlet,
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
	slowConnectionOrderFlowEnabled,
	toggleable,
	undoCartItemDeletionDisabled,
}) {
	const props = {
		accountId: Number(accountId),
		cartActionURLs: {
			baseOrderDetailURL,
			checkoutURL,
			orderDetailURL,
			productURLSeparator,
			signInURL,
			siteDefaultURL,
		},
		channel: {
			currencyCode,
			groupId,
			id,
		},
		detachedOpener,
		displayDiscountLevels,
		displayTotalItemsQuantity,
		guestOrderEnabled,
		hasCommerceOpenOrderContentPortlet,
		itemsQuantity: Number(itemsQuantity),
		orderId: Number(orderId),
		requestQuoteEnabled,
		slowConnectionOrderFlowEnabled,
		toggleable,
		undoCartItemDeletionDisabled,
	};

	const customCartViews = Object.entries(cartViews);

	if (customCartViews.length) {
		props.cartViews = customCartViews.reduce(
			(views, [viewName, contentRendererModuleUrl]) => ({
				...views,
				[viewName]: {contentRendererModuleUrl},
			}),
			{}
		);
	}

	const customLabels = Object.entries(labels);

	if (customLabels.length) {
		props.labels = customLabels.reduce(
			(labels, [key, value]) => ({
				...labels,
				[key]: value,
			}),
			{}
		);
	}

	MiniCart(miniCartId, miniCartId, props);
}
