/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Riccardo Alberti
 */
@ExtendedObjectClassDefinition(
	category = "pricing", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.commerce.pricing.configuration.CommercePricingConfiguration",
	localization = "content/Language",
	name = "commerce-pricing-configuration-name"
)
public interface CommercePricingConfiguration {

	@Meta.AD(
		deflt = "" + CommercePricingConstants.DISCOUNT_CHAIN_METHOD,
		name = "discount-application-strategy", required = false
	)
	public String commerceDiscountApplicationStrategy();

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Meta.AD(
		deflt = "" + CommercePricingConstants.TAX_INCLUDED_IN_FINAL_PRICE,
		name = "tax-display",
		optionLabels = {
			"tax-included-in-final-price", "tax-excluded-from-final-price"
		},
		optionValues = {"0", "1"}, required = false
	)
	public int commerceDisplayTax();

	@Meta.AD(
		deflt = CommercePricingConstants.ORDER_BY_HIERARCHY,
		name = "price-list-discovery-method", required = false
	)
	public String commercePriceListDiscovery();

	@Meta.AD(
		deflt = CommercePricingConstants.VERSION_2_0,
		name = "pricing-calculation-key", required = false
	)
	public String commercePricingCalculationKey();

	@Meta.AD(
		deflt = CommercePricingConstants.ORDER_BY_HIERARCHY,
		name = "promotion-discovery-method", required = false
	)
	public String commercePromotionDiscovery();

}