/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.constants;

import com.liferay.commerce.country.CommerceCountryManager;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.petra.string.StringBundler;

/**
 * @author Luca Pellizzon
 */
public class CommerceSAPConstants {

	public static final String CLASS_NAME_COMMERCE_CART_RESOURCE =
		"com.liferay.commerce.frontend.internal.cart.CommerceCartResource";

	public static final String CLASS_NAME_COMMERCE_HEADLESS_CART_ITEM_RESOURCE =
		"com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0." +
			"CartItemResourceImpl";

	public static final String CLASS_NAME_COMMERCE_HEADLESS_CART_RESOURCE =
		"com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0." +
			"CartResourceImpl";

	public static final String CLASS_NAME_COMMERCE_HEADLESS_CHANNEL_RESOURCE =
		"com.liferay.headless.commerce.delivery.catalog.internal.resource." +
			"v1_0.ChannelResourceImpl";

	public static final String CLASS_NAME_COMMERCE_HEADLESS_CURRENCY_RESOURCE =
		"com.liferay.headless.commerce.delivery.catalog.internal.resource." +
			"v1_0.CurrencyResourceImpl";

	public static final String
		CLASS_NAME_COMMERCE_HEADLESS_MAPPED_PRODUCT_RESOURCE =
			"com.liferay.headless.commerce.delivery.catalog.internal." +
				"resource.v1_0.MappedProductResourceImpl";

	public static final String CLASS_NAME_COMMERCE_HEADLESS_PIN_RESOURCE =
		"com.liferay.headless.commerce.delivery.catalog.internal.resource." +
			"v1_0.PinResourceImpl";

	public static final String CLASS_NAME_COMMERCE_HEADLESS_PRODUCT_RESOURCE =
		"com.liferay.headless.commerce.delivery.catalog.internal.resource." +
			"v1_0.ProductResourceImpl";

	public static final String CLASS_NAME_COMMERCE_HEADLESS_SKU_RESOURCE =
		"com.liferay.headless.commerce.delivery.catalog.internal.resource." +
			"v1_0.SkuResourceImpl";

	public static final String CLASS_NAME_COMMERCE_SEARCH_RESOURCE =
		"com.liferay.commerce.frontend.internal.search.CommerceSearchResource";

	public static final String SAP_ENTRY_NAME = "COMMERCE_DEFAULT";

	public static final String[][] SAP_ENTRY_OBJECT_ARRAYS = {
		{
			SAP_ENTRY_NAME,
			StringBundler.concat(
				CommerceCountryManager.class.getName(), "*\n",
				CommerceInventoryWarehouseItemService.class.getName(),
				"#getStockQuantity\n", CommerceOrderItemService.class.getName(),
				"#addOrUpdateCommerceOrderItem\n",
				CommerceOrderItemService.class.getName(),
				"#getCommerceOrderItem\n",
				CommerceOrderItemService.class.getName(),
				"#getCommerceOrderItems\n",
				CommerceOrderItemService.class.getName(),
				"#getCommerceOrderItemsQuantity\n",
				CommerceOrderService.class.getName(), "#addCommerceOrder\n",
				CommerceOrderService.class.getName(), "#fetchCommerceOrder\n",
				CommerceOrderService.class.getName(), "#getCommerceOrder\n",
				CLASS_NAME_COMMERCE_CART_RESOURCE, "*\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_ITEM_RESOURCE,
				"#deleteCartItem\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_ITEM_RESOURCE,
				"#getCartItem\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_ITEM_RESOURCE,
				"#patchCartItem\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_ITEM_RESOURCE,
				"#postCartItem\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_ITEM_RESOURCE,
				"#getCartItemsPage\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_RESOURCE, "#getCart\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_RESOURCE,
				"#getChannelCartsPage\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_RESOURCE, "#patchCart\n",
				CLASS_NAME_COMMERCE_HEADLESS_CART_RESOURCE,
				"#postChannelCart\n",
				CLASS_NAME_COMMERCE_HEADLESS_CHANNEL_RESOURCE,
				"#getChannelsPage\n",
				CLASS_NAME_COMMERCE_HEADLESS_CURRENCY_RESOURCE, "*\n",
				CLASS_NAME_COMMERCE_HEADLESS_MAPPED_PRODUCT_RESOURCE,
				"#getChannelProductMappedProductsPage\n",
				CLASS_NAME_COMMERCE_HEADLESS_PIN_RESOURCE,
				"#getChannelProductPinsPage\n",
				CLASS_NAME_COMMERCE_HEADLESS_PRODUCT_RESOURCE,
				"#getChannelProduct\n",
				CLASS_NAME_COMMERCE_HEADLESS_PRODUCT_RESOURCE,
				"#getChannelProductsPage\n",
				CLASS_NAME_COMMERCE_HEADLESS_SKU_RESOURCE,
				"#postChannelProductSku\n", CLASS_NAME_COMMERCE_SEARCH_RESOURCE)
		}
	};

}