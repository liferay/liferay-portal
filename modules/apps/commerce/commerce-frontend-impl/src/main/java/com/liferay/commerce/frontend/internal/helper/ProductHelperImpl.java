/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.helper;

import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.constants.CommercePriceConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.frontend.helper.ProductHelper;
import com.liferay.commerce.frontend.model.PriceModel;
import com.liferay.commerce.frontend.model.ProductSettingsModel;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceRequest;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(service = ProductHelper.class)
public class ProductHelperImpl implements ProductHelper {

	@Override
	public PriceModel getMinPriceModel(
			long cpDefinitionId, CommerceContext commerceContext, Locale locale)
		throws PortalException {

		CommerceMoney cpDefinitionMinimumPriceCommerceMoney =
			_commerceProductPriceCalculation.getCPDefinitionMinimumPrice(
				cpDefinitionId, commerceContext);

		PriceModel priceModel = null;

		if (cpDefinitionMinimumPriceCommerceMoney.isPriceOnApplication()) {
			priceModel = new PriceModel(
				CommercePriceConstants.PRICE_VALUE_PRICE_ON_APPLICATION);
		}
		else {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());

			priceModel = new PriceModel(
				_language.format(
					resourceBundle, "from-x",
					cpDefinitionMinimumPriceCommerceMoney.format(locale),
					false));
		}

		return priceModel;
	}

	@Override
	public PriceModel getPriceModel(
			long cpInstanceId, String commerceOptionValuesJSON,
			BigDecimal quantity, String unitOfMeasureKey,
			CommerceContext commerceContext, Locale locale)
		throws PortalException {

		CommerceProductPriceRequest commerceProductPriceRequest =
			new CommerceProductPriceRequest();

		commerceProductPriceRequest.setCommerceContext(commerceContext);
		commerceProductPriceRequest.setCommerceOptionValues(
			_getCommerceOptionValues(cpInstanceId, commerceOptionValuesJSON));
		commerceProductPriceRequest.setCpInstanceId(cpInstanceId);
		commerceProductPriceRequest.setQuantity(quantity);
		commerceProductPriceRequest.setSecure(true);
		commerceProductPriceRequest.setUnitOfMeasureKey(unitOfMeasureKey);

		boolean taxIncludedInPrice = _isTaxIncludedInPrice(
			commerceContext.getCommerceChannelId());

		commerceProductPriceRequest.setCalculateTax(taxIncludedInPrice);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				commerceProductPriceRequest);

		if (commerceProductPrice == null) {
			return null;
		}

		if (taxIncludedInPrice) {
			return _getPriceModel(
				commerceProductPrice.getFinalPriceWithTaxAmount(),
				commerceProductPrice.getUnitPriceWithTaxAmount(),
				commerceProductPrice.getUnitPromoPriceWithTaxAmount(),
				commerceProductPrice.getDiscountValueWithTaxAmount(), locale);
		}

		return _getPriceModel(
			commerceProductPrice.getFinalPrice(),
			commerceProductPrice.getUnitPrice(),
			commerceProductPrice.getUnitPromoPrice(),
			commerceProductPrice.getDiscountValue(), locale);
	}

	@Override
	public ProductSettingsModel getProductSettingsModel(
			long cpDefinitionId, CommerceContext commerceContext)
		throws PortalException {

		ProductSettingsModel productSettingsModel = new ProductSettingsModel();

		BigDecimal minOrderQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY;
		BigDecimal maxOrderQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MAX_ORDER_QUANTITY;
		BigDecimal multipleQuantity =
			CPDefinitionInventoryConstants.DEFAULT_MULTIPLE_ORDER_QUANTITY;

		CPDefinition cpDefinition = _cpDefinitionLocalService.fetchCPDefinition(
			cpDefinitionId);

		if (cpDefinition != null) {
			CPConfigurationEntry cpConfigurationEntry =
				cpDefinition.fetchCPConfigurationEntry(
					commerceContext.getCPConfigurationListId(
						cpDefinition.getGroupId()));

			if (cpConfigurationEntry == null) {
				cpConfigurationEntry =
					cpDefinition.fetchMasterCPConfigurationEntry();
			}

			minOrderQuantity = cpConfigurationEntry.getMinOrderQuantity();
			maxOrderQuantity = cpConfigurationEntry.getMaxOrderQuantity();
			multipleQuantity = cpConfigurationEntry.getMultipleOrderQuantity();

			BigDecimal[] allowedOrderQuantitiesArray =
				cpConfigurationEntry.getAllowedOrderQuantitiesArray();

			if ((allowedOrderQuantitiesArray != null) &&
				(allowedOrderQuantitiesArray.length > 0)) {

				productSettingsModel.setAllowedQuantities(
					allowedOrderQuantitiesArray);
			}

			productSettingsModel.setBackOrders(
				cpConfigurationEntry.isBackOrders());
			productSettingsModel.setLowStockQuantity(
				cpConfigurationEntry.getMinStockQuantity());
			productSettingsModel.setShowAvailabilityDot(
				cpConfigurationEntry.isDisplayAvailability());
		}

		productSettingsModel.setMinQuantity(minOrderQuantity);
		productSettingsModel.setMaxQuantity(maxOrderQuantity);
		productSettingsModel.setMultipleQuantity(multipleQuantity);

		return productSettingsModel;
	}

	private List<CommerceOptionValue> _getCommerceOptionValues(
			long cpInstanceId, String formFieldValues)
		throws PortalException {

		if (Validator.isNull(formFieldValues)) {
			return Collections.emptyList();
		}

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		return _commerceOptionValueHelper.getCPDefinitionCommerceOptionValues(
			cpInstance.getCPDefinitionId(), formFieldValues);
	}

	private String[] _getFormattedDiscountPercentages(
			BigDecimal[] discountPercentages, Locale locale)
		throws PortalException {

		List<String> formattedDiscountPercentages = new ArrayList<>();

		for (BigDecimal percentage : discountPercentages) {
			if (percentage == null) {
				percentage = BigDecimal.ZERO;
			}

			formattedDiscountPercentages.add(
				_commercePriceFormatter.format(percentage, locale));
		}

		return formattedDiscountPercentages.toArray(new String[0]);
	}

	private PriceModel _getPriceModel(
			CommerceMoney finalPriceCommerceMoney,
			CommerceMoney unitPriceCommerceMoney,
			CommerceMoney unitPromoPriceCommerceMoney,
			CommerceDiscountValue commerceDiscountValue, Locale locale)
		throws PortalException {

		PriceModel priceModel = null;

		if (unitPriceCommerceMoney.isPriceOnApplication()) {
			priceModel = new PriceModel(
				CommercePriceConstants.PRICE_VALUE_PRICE_ON_APPLICATION);
		}
		else {
			priceModel = new PriceModel(unitPriceCommerceMoney.format(locale));
		}

		if (!unitPromoPriceCommerceMoney.isEmpty()) {
			BigDecimal unitPrice = unitPriceCommerceMoney.getPrice();
			BigDecimal unitPromoPrice = unitPromoPriceCommerceMoney.getPrice();

			if ((unitPromoPrice.compareTo(BigDecimal.ZERO) > 0) &&
				((unitPromoPrice.compareTo(unitPrice) < 0) ||
				 unitPriceCommerceMoney.isPriceOnApplication())) {

				if (unitPromoPriceCommerceMoney.isPriceOnApplication()) {
					priceModel.setPromoPrice(
						CommercePriceConstants.
							PRICE_VALUE_PRICE_ON_APPLICATION);
				}
				else {
					priceModel.setPromoPrice(
						unitPromoPriceCommerceMoney.format(locale));
				}
			}
		}

		return _updatePriceModelDiscount(
			priceModel, commerceDiscountValue, finalPriceCommerceMoney, locale);
	}

	private boolean _isTaxIncludedInPrice(long commerceChannelId)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(commerceChannelId);

		String priceDisplayType = commerceChannel.getPriceDisplayType();

		return priceDisplayType.equals(
			CommercePricingConstants.TAX_INCLUDED_IN_PRICE);
	}

	private PriceModel _updatePriceModelDiscount(
			PriceModel priceModel, CommerceDiscountValue commerceDiscountValue,
			CommerceMoney finalPriceCommerceMoney, Locale locale)
		throws PortalException {

		if (commerceDiscountValue == null) {
			return priceModel;
		}

		CommerceMoney discountAmountCommerceMoney =
			commerceDiscountValue.getDiscountAmount();

		priceModel.setDiscount(discountAmountCommerceMoney.format(locale));

		CommerceCurrency commerceCurrency =
			discountAmountCommerceMoney.getCommerceCurrency();

		priceModel.setDiscountPercentage(
			_percentageFormatter.getLocalizedPercentage(
				locale, commerceCurrency.getMaxFractionDigits(),
				commerceCurrency.getMinFractionDigits(),
				commerceDiscountValue.getDiscountPercentage()));

		priceModel.setDiscountPercentages(
			_getFormattedDiscountPercentages(
				commerceDiscountValue.getPercentages(), locale));
		priceModel.setFinalPrice(finalPriceCommerceMoney.format(locale));

		return priceModel;
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOptionValueHelper _commerceOptionValueHelper;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

	@Reference
	private PercentageFormatter _percentageFormatter;

}