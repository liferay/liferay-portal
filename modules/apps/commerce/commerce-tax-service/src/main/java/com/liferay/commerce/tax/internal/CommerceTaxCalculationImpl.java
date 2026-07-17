/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.internal;

import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.exception.CommerceTaxEngineException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.tax.CommerceTaxCalculateRequest;
import com.liferay.commerce.tax.CommerceTaxCalculation;
import com.liferay.commerce.tax.CommerceTaxEngine;
import com.liferay.commerce.tax.CommerceTaxValue;
import com.liferay.commerce.tax.configuration.CommerceShippingTaxConfiguration;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.commerce.util.CommerceTaxEngineRegistry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = CommerceTaxCalculation.class)
public class CommerceTaxCalculationImpl implements CommerceTaxCalculation {

	@Override
	public List<CommerceTaxValue> getCommerceTaxValues(
			CommerceOrder commerceOrder)
		throws PortalException {

		if (commerceOrder == null) {
			return Collections.emptyList();
		}

		Map<String, CommerceTaxValue> taxValueMap = new HashMap<>();

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			List<CommerceTaxValue> commerceTaxValues = getCommerceTaxValues(
				commerceOrder.getBillingAddressId(), commerceOrder.getGroupId(),
				commerceOrder.getCommerceCurrencyCode(),
				commerceOrder.getShippingAddressId(),
				commerceOrderItem.getCPInstanceId(), false,
				commerceOrderItem.getFinalPrice());

			for (CommerceTaxValue commerceTaxValue : commerceTaxValues) {
				CommerceTaxValue aggregatedCommerceTaxValue = taxValueMap.get(
					commerceTaxValue.getName());

				BigDecimal amount = commerceTaxValue.getAmount();

				if (aggregatedCommerceTaxValue != null) {
					amount = amount.add(aggregatedCommerceTaxValue.getAmount());
				}

				aggregatedCommerceTaxValue = new CommerceTaxValue(
					commerceTaxValue.getName(), commerceTaxValue.getLabel(),
					amount);

				taxValueMap.put(
					commerceTaxValue.getName(), aggregatedCommerceTaxValue);
			}
		}

		return new ArrayList<>(taxValueMap.values());
	}

	@Override
	public List<CommerceTaxValue> getCommerceTaxValues(
			long commerceBillingAddressId, long commerceChannelGroupId,
			String commerceCurrencyCode, long commerceShippingAddressId,
			long cpInstanceId, boolean includeTax, BigDecimal price)
		throws PortalException {

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			cpInstanceId);

		if (cpInstance == null) {
			return Collections.emptyList();
		}

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		if (cpDefinition.isTaxExempt() ||
			(cpDefinition.getCPTaxCategoryId() <= 0)) {

			return Collections.emptyList();
		}

		return _getCommerceTaxValues(
			commerceChannelGroupId, commerceBillingAddressId,
			commerceShippingAddressId, price, commerceCurrencyCode, includeTax,
			false, cpDefinition.getCPTaxCategoryId());
	}

	@Override
	public CommerceMoney getShippingTaxValue(
			CommerceOrder commerceOrder, CommerceCurrency commerceCurrency)
		throws PortalException {

		CommerceShippingTaxConfiguration commerceShippingTaxConfiguration =
			_configurationProvider.getConfiguration(
				CommerceShippingTaxConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceOrder.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_TAX));

		List<CommerceTaxValue> commerceTaxValues = _getCommerceTaxValues(
			commerceOrder.getGroupId(), commerceOrder.getBillingAddressId(),
			commerceOrder.getShippingAddressId(),
			commerceOrder.getShippingAmount(),
			commerceOrder.getCommerceCurrencyCode(), false, true,
			commerceShippingTaxConfiguration.taxCategoryId());

		BigDecimal taxAmount = BigDecimal.ZERO;

		if (commerceTaxValues != null) {
			for (CommerceTaxValue commerceTaxValue : commerceTaxValues) {
				taxAmount = taxAmount.add(commerceTaxValue.getAmount());
			}
		}

		return _commerceMoneyFactory.create(commerceCurrency, taxAmount);
	}

	@Override
	public CommerceMoney getTaxAmount(
			CommerceOrder commerceOrder, CommerceCurrency commerceCurrency)
		throws PortalException {

		BigDecimal taxAmount = BigDecimal.ZERO;

		List<CommerceTaxValue> commerceTaxValues = getCommerceTaxValues(
			commerceOrder);

		for (CommerceTaxValue commerceTaxValue : commerceTaxValues) {
			taxAmount = taxAmount.add(commerceTaxValue.getAmount());
		}

		return _commerceMoneyFactory.create(commerceCurrency, taxAmount);
	}

	private List<CommerceTaxValue> _getCommerceTaxValues(
		long groupId, long commerceBillingAddressId,
		long commerceShippingAddressId, BigDecimal amount,
		String commerceCurrencyCode, boolean includeTax, boolean shipping,
		long taxCategoryId) {

		CommerceTaxCalculateRequest commerceTaxCalculateRequest =
			new CommerceTaxCalculateRequest();

		commerceTaxCalculateRequest.setCommerceBillingAddressId(
			commerceBillingAddressId);
		commerceTaxCalculateRequest.setCommerceCurrencyCode(
			commerceCurrencyCode);
		commerceTaxCalculateRequest.setCommerceChannelGroupId(groupId);
		commerceTaxCalculateRequest.setCommerceShippingAddressId(
			commerceShippingAddressId);
		commerceTaxCalculateRequest.setPrice(amount);
		commerceTaxCalculateRequest.setIncludeTax(includeTax);
		commerceTaxCalculateRequest.setShipping(shipping);
		commerceTaxCalculateRequest.setTaxCategoryId(taxCategoryId);

		return TransformUtil.transform(
			_commerceTaxMethodLocalService.getCommerceTaxMethods(groupId, true),
			commerceTaxMethod -> {
				commerceTaxCalculateRequest.setCommerceTaxMethodId(
					commerceTaxMethod.getCommerceTaxMethodId());
				commerceTaxCalculateRequest.setPercentage(
					commerceTaxMethod.isPercentage());

				CommerceTaxEngine commerceTaxEngine =
					_commerceTaxEngineRegistry.getCommerceTaxEngine(
						commerceTaxMethod.getEngineKey());

				try {
					CommerceTaxValue commerceTaxValue =
						commerceTaxEngine.getCommerceTaxValue(
							commerceTaxCalculateRequest);

					if (commerceTaxValue != null) {
						return commerceTaxValue;
					}
				}
				catch (CommerceTaxEngineException commerceTaxEngineException) {
					_log.error(commerceTaxEngineException);
				}

				return null;
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceTaxCalculationImpl.class);

	@Reference
	private CommerceMoneyFactory _commerceMoneyFactory;

	@Reference
	private CommerceTaxEngineRegistry _commerceTaxEngineRegistry;

	@Reference
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

}