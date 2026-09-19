/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.portal.kernel.exception.PortalException;

import java.math.BigDecimal;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public interface CommerceProductPriceCalculation {

	public CommerceMoney getBasePrice(
			long cpInstanceId, CommerceCurrency commerceCurrency,
			String unitOfMeasureKey)
		throws PortalException;

	public CommerceMoney getBasePromoPrice(
			long cpInstanceId, CommerceCurrency commerceCurrency,
			String unitOfMeasureKey)
		throws PortalException;

	public CommerceMoney getCPDefinitionMinimumPrice(
			long cpDefinitionId, CommerceContext commerceContext)
		throws PortalException;

	public CommerceMoney getCPDefinitionOptionValueRelativePrice(
			CommerceProductOptionValueRelativePriceRequest
				commerceProductOptionValueRelativePriceRequest)
		throws PortalException;

	public CommerceProductPrice getCommerceProductPrice(
			CommerceProductPriceRequest commerceProductPriceRequest)
		throws PortalException;

	public CommerceProductPrice getCommerceProductPrice(
			long cpInstanceId, BigDecimal quantity, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException;

	public CommerceProductPrice getCommerceProductPrice(
			long cpInstanceId, BigDecimal quantity, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException;

	public CommerceMoney getFinalPrice(
			long cpInstanceId, BigDecimal quantity, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException;

	public CommerceMoney getFinalPrice(
			long cpInstanceId, BigDecimal quantity, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException;

	public CommerceMoney getPromoPrice(
			long cpInstanceId, BigDecimal quantity,
			CommerceCurrency commerceCurrency, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException;

	public CommercePriceEntry getUnitCommercePriceEntry(
			CommerceContext commerceContext, long cpInstanceId,
			String unitOfMeasureKey)
		throws PortalException;

	public CommerceMoney getUnitMaxPrice(
			long cpDefinitionId, BigDecimal quantity, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException;

	public CommerceMoney getUnitMaxPrice(
			long cpDefinitionId, BigDecimal quantity, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException;

	public CommerceMoney getUnitMinPrice(
			long cpDefinitionId, BigDecimal quantity, boolean secure,
			CommerceContext commerceContext)
		throws PortalException;

	public CommerceMoney getUnitMinPrice(
			long cpDefinitionId, BigDecimal quantity,
			CommerceContext commerceContext)
		throws PortalException;

	public CommerceMoney getUnitPrice(
			long cpInstanceId, BigDecimal quantity,
			CommerceCurrency commerceCurrency, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException;

}