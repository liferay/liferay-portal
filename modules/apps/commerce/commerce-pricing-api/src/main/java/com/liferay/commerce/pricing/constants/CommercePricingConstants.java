/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.constants;

/**
 * @author Riccardo Alberti
 * @author Alessio Antonio Rendina
 */
public class CommercePricingConstants {

	public static final String DISCOUNT_ADDITION_METHOD = "addition";

	public static final String DISCOUNT_CHAIN_METHOD = "chain";

	public static final String ORDER_BY_HIERARCHY = "hierarchy";

	public static final String ORDER_BY_LOWEST_ENTRY = "lowest";

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static final int TAX_EXCLUDED_FROM_FINAL_PRICE = 1;

	public static final String TAX_EXCLUDED_FROM_PRICE = "tax-excluded";

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static final int TAX_INCLUDED_IN_FINAL_PRICE = 0;

	public static final String TAX_INCLUDED_IN_PRICE = "tax-included";

	public static final String VERSION_1_0 = "v1.0";

	public static final String VERSION_2_0 = "v2.0";

}