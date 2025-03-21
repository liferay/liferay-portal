/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.product.content.contributor;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceRequest;
import com.liferay.commerce.product.constants.CPContentContributorConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPContentContributor;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "commerce.product.content.contributor.name=" + CPContentContributorConstants.PRICE,
	service = CPContentContributor.class
)
public class PriceCPContentContributor implements CPContentContributor {

	@Override
	public String getName() {
		return CPContentContributorConstants.PRICE;
	}

	@Override
	public JSONObject getValue(
			CPInstance cpInstance, HttpServletRequest httpServletRequest)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		if (cpInstance == null) {
			return jsonObject;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				_portal.getScopeGroupId(httpServletRequest));

		if (commerceChannel == null) {
			return jsonObject;
		}

		String formFieldValues = ParamUtil.getString(
			httpServletRequest, "formFieldValues");

		List<CommerceOptionValue> commerceOptionValues =
			_commerceOptionValueHelper.getCPDefinitionCommerceOptionValues(
				cpInstance.getCPDefinitionId(), formFieldValues);

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					cpInstance.getCPDefinitionId());

		CPDefinitionInventoryEngine cpDefinitionInventoryEngine =
			_cpDefinitionInventoryEngineRegistry.getCPDefinitionInventoryEngine(
				cpDefinitionInventory);

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_getCommerceProductPriceRequest(
					cpInstance, cpDefinitionInventoryEngine,
					commerceOptionValues, StringPool.BLANK, commerceContext));

		CommerceMoney unitPriceCommerceMoney =
			commerceProductPrice.getUnitPrice();

		if (unitPriceCommerceMoney.isEmpty()) {
			return jsonObject;
		}

		Locale locale = _portal.getLocale(httpServletRequest);

		jsonObject.put(
			CPContentContributorConstants.PRICE,
			unitPriceCommerceMoney.format(locale));

		CommerceMoney unitPromoPriceCommerceMoney =
			commerceProductPrice.getUnitPromoPrice();

		if (unitPromoPriceCommerceMoney.isEmpty()) {
			return jsonObject;
		}

		if (BigDecimalUtil.gt(
				unitPromoPriceCommerceMoney.getPrice(), BigDecimal.ZERO) &&
			BigDecimalUtil.lte(
				unitPromoPriceCommerceMoney.getPrice(),
				unitPriceCommerceMoney.getPrice())) {

			jsonObject.put(
				CPContentContributorConstants.PROMO_PRICE,
				unitPromoPriceCommerceMoney.format(locale));
		}

		return jsonObject;
	}

	private CommerceProductPriceRequest _getCommerceProductPriceRequest(
			CPInstance cpInstance,
			CPDefinitionInventoryEngine cpDefinitionInventoryEngine,
			List<CommerceOptionValue> commerceOptionValues,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		CommerceProductPriceRequest commerceProductPriceRequest =
			new CommerceProductPriceRequest();

		commerceProductPriceRequest.setCommerceContext(commerceContext);
		commerceProductPriceRequest.setCommerceOptionValues(
			commerceOptionValues);
		commerceProductPriceRequest.setCpInstanceId(
			cpInstance.getCPInstanceId());
		commerceProductPriceRequest.setQuantity(
			cpDefinitionInventoryEngine.getMinOrderQuantity(
				commerceContext.getCPConfigurationListId(
					cpInstance.getGroupId()),
				cpInstance));
		commerceProductPriceRequest.setSecure(false);
		commerceProductPriceRequest.setUnitOfMeasureKey(unitOfMeasureKey);

		return commerceProductPriceRequest;
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOptionValueHelper _commerceOptionValueHelper;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}