/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {DEFAULT_LABELS} from './util/labels';

/**
 * MiniCartContext Default Shape and Values
 *
 * (exported for test purposes)
 */
export const DEFAULT_MINI_CART_CONTEXT_VALUE = {
	CartViews: {},
	accountId: 0,
	actionURLs: {},
	cartItemsPagination: {},
	cartState: {},
	closeCart: () => {},
	displayDiscountLevels: false,
	displayTotalItemsQuantity: false,
	editedItem: null,
	getCartItems: () => {},
	guestOrderEnabled: false,
	isOpen: false,
	isUpdating: false,
	labels: DEFAULT_LABELS,
	openCart: () => {},
	replacementSKUList: [],
	requestQuoteEnabled: false,
	setCartState: () => {},
	setEditedItem: () => {},
	setIsUpdating: () => {},
	setReplacementSKUList: () => {},
	summaryDataMapper: () => {},
	toggleable: true,
	undoCartItemDeletionDisabled: true,
	updateCartModel: () => {},
};

export default React.createContext(DEFAULT_MINI_CART_CONTEXT_VALUE);
