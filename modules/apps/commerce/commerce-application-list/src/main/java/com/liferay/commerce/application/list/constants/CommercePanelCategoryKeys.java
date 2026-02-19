/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.application.list.constants;

import com.liferay.application.list.constants.PanelCategoryKeys;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CommercePanelCategoryKeys {

	public static final String COMMERCE = PanelCategoryKeys.COMMERCE;

	public static final String COMMERCE_INVENTORY_MANAGEMENT =
		"commerce.inventory_management";

	public static final String COMMERCE_ORDER_MANAGEMENT =
		"commerce.order_management";

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static final String COMMERCE_ORDERS_MANAGEMENT =
		COMMERCE_ORDER_MANAGEMENT;

	public static final String COMMERCE_PAYMENT_MANAGEMENT =
		"commerce.payment_management";

	public static final String COMMERCE_PRICING = "commerce.pricing";

	public static final String COMMERCE_PRODUCT_MANAGEMENT =
		"commerce.product_management";

	public static final String COMMERCE_SETTINGS = "commerce.settings";

	public static final String COMMERCE_STORE_MANAGEMENT =
		"commerce.store_management";

}