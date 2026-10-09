/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.price;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.currency.util.PriceFormat;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.internal.util.CommercePriceConverterUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.price.CommerceProductOptionValueRelativePriceRequest;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceImpl;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.CommerceTaxCalculation;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
public abstract class BaseCommerceProductPriceCalculation
	implements CommerceProductPriceCalculation {

	@Override
	public CommerceMoney getCPDefinitionMinimumPrice(
			long cpDefinitionId, CommerceContext commerceContext)
		throws PortalException {

		CommerceMoney commerceMoney = getUnitMinPrice(
			cpDefinitionId, BigDecimal.ONE, commerceContext);

		if (commerceMoney.isEmpty()) {
			return commerceMoney;
		}

		BigDecimal cpDefinitionMinimumPrice = BigDecimal.ZERO;

		cpDefinitionMinimumPrice = cpDefinitionMinimumPrice.add(
			commerceMoney.getPrice());

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinitionId);

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			if (!_isRequiredPriceContributor(cpDefinitionOptionRel)) {
				continue;
			}

			if (cpDefinitionOptionRel.isPriceTypeStatic()) {
				cpDefinitionMinimumPrice = cpDefinitionMinimumPrice.add(
					_getCPDefinitionOptionMinStaticPrice(
						cpDefinitionOptionRel, commerceContext));

				continue;
			}

			cpDefinitionMinimumPrice = cpDefinitionMinimumPrice.add(
				_getCPDefinitionOptionMinDynamicPrice(
					cpDefinitionOptionRel, commerceContext));
		}

		return commerceMoneyFactory.create(
			commerceContext.getCommerceCurrency(), cpDefinitionMinimumPrice);
	}

	@Override
	public CommerceMoney getCPDefinitionOptionValueRelativePrice(
			CommerceProductOptionValueRelativePriceRequest
				commerceProductOptionValueRelativePriceRequest)
		throws PortalException {

		_validate(
			commerceProductOptionValueRelativePriceRequest.
				getCPDefinitionOptionValueRel(),
			commerceProductOptionValueRelativePriceRequest.
				getSelectedCPDefinitionOptionValueRel());

		BigDecimal relativePrice = BigDecimal.ZERO;

		CommerceContext commerceContext =
			commerceProductOptionValueRelativePriceRequest.getCommerceContext();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			commerceProductOptionValueRelativePriceRequest.
				getCPDefinitionOptionValueRel();

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		if (!cpDefinitionOptionRel.isPriceContributor()) {
			return commerceMoneyFactory.create(
				commerceContext.getCommerceCurrency(), relativePrice,
				PriceFormat.RELATIVE);
		}

		relativePrice = relativePrice.add(
			_getCPInstancePriceDifference(
				commerceProductOptionValueRelativePriceRequest.
					getCPInstanceId(),
				commerceProductOptionValueRelativePriceRequest.
					getCPInstanceMinQuantity(),
				commerceProductOptionValueRelativePriceRequest.
					getCPInstanceUnitOfMeasureKey(),
				commerceProductOptionValueRelativePriceRequest.
					getSelectedCPInstanceId(),
				commerceProductOptionValueRelativePriceRequest.
					getSelectedCPInstanceMinQuantity(),
				commerceProductOptionValueRelativePriceRequest.
					getSelectedCPInstanceUnitOfMeasureKey(),
				commerceContext));

		relativePrice = relativePrice.add(
			_getCPDefinitionOptionValuePriceDifference(
				commerceProductOptionValueRelativePriceRequest.
					getCPDefinitionOptionValueRel(),
				commerceProductOptionValueRelativePriceRequest.
					getSelectedCPDefinitionOptionValueRel(),
				cpDefinitionOptionRel.getPriceType(), commerceContext));

		return commerceMoneyFactory.create(
			commerceContext.getCommerceCurrency(), relativePrice,
			PriceFormat.RELATIVE);
	}

	protected BigDecimal getConvertedPrice(
			long cpInstanceId, BigDecimal price, boolean includeTax,
			CommerceContext commerceContext)
		throws PortalException {

		long commerceBillingAddressId = 0;
		long commerceChannelGroupId =
			commerceContext.getCommerceChannelGroupId();
		String commerceCurrencyCode = null;
		long commerceShippingAddressId = 0;

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (commerceOrder != null) {
			commerceChannelGroupId = commerceOrder.getGroupId();
			commerceBillingAddressId = commerceOrder.getBillingAddressId();
			commerceShippingAddressId = commerceOrder.getShippingAddressId();
			commerceCurrencyCode = commerceOrder.getCommerceCurrencyCode();
		}
		else {
			AccountEntry accountEntry = commerceContext.getAccountEntry();

			if (accountEntry != null) {
				CommerceChannelAccountEntryRel
					billingAddressCommerceChannelAccountEntryRel =
						commerceChannelAccountEntryRelLocalService.
							fetchCommerceChannelAccountEntryRel(
								accountEntry.getAccountEntryId(),
								commerceContext.getCommerceChannelId(),
								CommerceChannelAccountEntryRelConstants.
									TYPE_BILLING_ADDRESS);

				if (billingAddressCommerceChannelAccountEntryRel != null) {
					commerceBillingAddressId =
						billingAddressCommerceChannelAccountEntryRel.
							getClassPK();
				}

				CommerceChannelAccountEntryRel
					shippingAddressCommerceChannelAccountEntryRel =
						commerceChannelAccountEntryRelLocalService.
							fetchCommerceChannelAccountEntryRel(
								accountEntry.getAccountEntryId(),
								commerceContext.getCommerceChannelId(),
								CommerceChannelAccountEntryRelConstants.
									TYPE_SHIPPING_ADDRESS);

				if (shippingAddressCommerceChannelAccountEntryRel != null) {
					commerceShippingAddressId =
						shippingAddressCommerceChannelAccountEntryRel.
							getClassPK();
				}
			}
		}

		return CommercePriceConverterUtil.getConvertedPrice(
			commerceBillingAddressId, commerceChannelGroupId,
			commerceCurrencyCode, commerceShippingAddressId,
			commerceTaxCalculation, cpInstanceId, includeTax, price);
	}

	protected BigDecimal[] getUpdatedPrices(
			CommerceMoney unitPriceCommerceMoney,
			CommerceMoney promoPriceCommerceMoney, BigDecimal finalPrice,
			CommerceContext commerceContext,
			List<CommerceOptionValue> commerceOptionValues)
		throws PortalException {

		if (commerceOptionValues.isEmpty()) {
			return _toPriceArray(
				unitPriceCommerceMoney, promoPriceCommerceMoney, finalPrice);
		}

		BigDecimal promoPrice = BigDecimal.ZERO;

		if (!promoPriceCommerceMoney.isEmpty()) {
			promoPrice = promoPriceCommerceMoney.getPrice();
		}

		BigDecimal unitPrice = BigDecimal.ZERO;

		if (!unitPriceCommerceMoney.isEmpty()) {
			unitPrice = unitPriceCommerceMoney.getPrice();
		}

		for (CommerceOptionValue commerceOptionValue : commerceOptionValues) {
			if (_isStaticPriceType(commerceOptionValue.getPriceType())) {
				BigDecimal optionValuePrice = commerceOptionValue.getPrice();

				if ((optionValuePrice != null) &&
					BigDecimalUtil.gt(optionValuePrice, BigDecimal.ZERO)) {

					if (commerceOptionValue.getCPInstanceId() > 0) {
						optionValuePrice = optionValuePrice.multiply(
							commerceOptionValue.getQuantity());
					}

					unitPrice = unitPrice.add(optionValuePrice);

					if (BigDecimalUtil.gt(promoPrice, BigDecimal.ZERO)) {
						promoPrice = promoPrice.add(optionValuePrice);
					}

					finalPrice = finalPrice.add(optionValuePrice);
				}
			}
			else if (Objects.equals(
						commerceOptionValue.getPriceType(),
						CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC) &&
					 (commerceOptionValue.getCPInstanceId() > 0)) {

				BigDecimal optionValueQuantity =
					commerceOptionValue.getQuantity();

				CommerceProductPrice optionValueProductPrice =
					getCommerceProductPrice(
						commerceOptionValue.getCPInstanceId(),
						optionValueQuantity, true,
						commerceOptionValue.getUnitOfMeasureKey(),
						commerceContext);

				CommerceMoney optionValueUnitPriceCommerceMoney =
					optionValueProductPrice.getUnitPrice();

				BigDecimal optionValueUnitPrice =
					optionValueUnitPriceCommerceMoney.getPrice();

				CommerceMoney optionValueUnitPromoPriceCommerceMoney =
					optionValueProductPrice.getUnitPromoPrice();

				BigDecimal optionValueUnitPromoPrice = BigDecimal.ZERO;

				if (!optionValueUnitPromoPriceCommerceMoney.isEmpty()) {
					optionValueUnitPromoPrice =
						optionValueUnitPromoPriceCommerceMoney.getPrice();
				}

				BigDecimal incrementalOrderQuantity = BigDecimal.ONE;

				if (Validator.isNotNull(
						commerceOptionValue.getUnitOfMeasureKey())) {

					CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
						cpInstanceUnitOfMeasureLocalService.
							fetchCPInstanceUnitOfMeasure(
								commerceOptionValue.getCPInstanceId(),
								commerceOptionValue.getUnitOfMeasureKey());

					if (cpInstanceUnitOfMeasure != null) {
						incrementalOrderQuantity =
							cpInstanceUnitOfMeasure.
								getIncrementalOrderQuantity();
					}
				}

				CommerceCurrency commerceCurrency =
					optionValueUnitPriceCommerceMoney.getCommerceCurrency();

				int precision = commerceCurrency.getMaxFractionDigits();

				if (BigDecimalUtil.gt(
						optionValueUnitPromoPrice, BigDecimal.ZERO) &&
					BigDecimalUtil.isZero(promoPrice)) {

					promoPrice = promoPrice.add(unitPrice);
				}
				else if (BigDecimalUtil.isZero(optionValueUnitPromoPrice) &&
						 BigDecimalUtil.gt(promoPrice, BigDecimal.ZERO)) {

					BigDecimal price = optionValueUnitPrice.multiply(
						optionValueQuantity);

					promoPrice = promoPrice.add(
						price.divide(
							incrementalOrderQuantity, precision,
							RoundingMode.HALF_UP));
				}

				BigDecimal price = optionValueUnitPrice.multiply(
					optionValueQuantity);

				unitPrice = unitPrice.add(
					price.divide(
						incrementalOrderQuantity, precision,
						RoundingMode.HALF_UP));

				price = optionValueUnitPromoPrice.multiply(optionValueQuantity);

				promoPrice = promoPrice.add(
					price.divide(
						incrementalOrderQuantity, precision,
						RoundingMode.HALF_UP));

				CommerceMoney optionValueFinalPriceCommerceMoney =
					optionValueProductPrice.getFinalPrice();

				finalPrice = finalPrice.add(
					optionValueFinalPriceCommerceMoney.getPrice());
			}
		}

		return new BigDecimal[] {unitPrice, promoPrice, finalPrice};
	}

	protected void setCommerceProductPriceWithTaxAmount(
			long cpInstanceId, BigDecimal finalPriceWithTaxAmount,
			CommerceProductPriceImpl commerceProductPriceImpl,
			CommerceContext commerceContext,
			CommerceDiscountValue commerceDiscountValue,
			boolean discountsTargetNetPrice)
		throws PortalException {

		CommerceMoney unitPriceCommerceMoney =
			commerceProductPriceImpl.getUnitPrice();

		BigDecimal unitPriceWithTaxAmount = getConvertedPrice(
			cpInstanceId, unitPriceCommerceMoney.getPrice(), false,
			commerceContext);

		BigDecimal activePrice = unitPriceWithTaxAmount;

		CommerceMoney promoPriceCommerceMoney =
			commerceProductPriceImpl.getUnitPromoPrice();

		if (!promoPriceCommerceMoney.isEmpty() &&
			BigDecimalUtil.gt(
				promoPriceCommerceMoney.getPrice(), BigDecimal.ZERO)) {

			BigDecimal unitPromoPriceWithTaxAmount = getConvertedPrice(
				cpInstanceId, promoPriceCommerceMoney.getPrice(), false,
				commerceContext);

			commerceProductPriceImpl.setUnitPromoPriceWithTaxAmount(
				commerceMoneyFactory.create(
					commerceContext.getCommerceCurrency(),
					unitPromoPriceWithTaxAmount));

			activePrice = unitPromoPriceWithTaxAmount;
		}
		else {
			commerceProductPriceImpl.setUnitPromoPriceWithTaxAmount(
				commerceMoneyFactory.emptyCommerceMoney());
		}

		commerceProductPriceImpl.setUnitPriceWithTaxAmount(
			commerceMoneyFactory.create(
				commerceContext.getCommerceCurrency(), unitPriceWithTaxAmount));

		BigDecimal quantity = commerceProductPriceImpl.getQuantity();

		if (activePrice == null) {
			activePrice = BigDecimal.ZERO;
		}

		if (discountsTargetNetPrice) {
			CommerceCurrency commerceCurrency =
				commerceContext.getCommerceCurrency();

			activePrice = activePrice.multiply(quantity);

			commerceProductPriceImpl.setCommerceDiscountValueWithTaxAmount(
				CommercePriceConverterUtil.getConvertedCommerceDiscountValue(
					commerceDiscountValue, activePrice, finalPriceWithTaxAmount,
					commerceMoneyFactory,
					RoundingMode.valueOf(commerceCurrency.getRoundingMode())));
		}
		else {
			commerceProductPriceImpl.setCommerceDiscountValueWithTaxAmount(
				commerceDiscountValue);
		}

		commerceProductPriceImpl.setFinalPriceWithTaxAmount(
			commerceMoneyFactory.create(
				commerceContext.getCommerceCurrency(),
				finalPriceWithTaxAmount));
	}

	@Reference
	protected AccountEntryLocalService accountEntryLocalService;

	@Reference
	protected CommerceChannelAccountEntryRelLocalService
		commerceChannelAccountEntryRelLocalService;

	@Reference
	protected CommerceChannelLocalService commerceChannelLocalService;

	@Reference
	protected CommerceMoneyFactory commerceMoneyFactory;

	@Reference
	protected CommerceTaxCalculation commerceTaxCalculation;

	@Reference
	protected CPDefinitionOptionRelLocalService
		cpDefinitionOptionRelLocalService;

	@Reference
	protected CPInstanceLocalService cpInstanceLocalService;

	@Reference
	protected CPInstanceUnitOfMeasureLocalService
		cpInstanceUnitOfMeasureLocalService;

	private BigDecimal _getCPDefinitionOptionMinDynamicPrice(
			CPDefinitionOptionRel cpDefinitionOptionRel,
			CommerceContext commerceContext)
		throws PortalException {

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		if (cpDefinitionOptionValueRels.isEmpty()) {
			return BigDecimal.ZERO;
		}

		Iterator<CPDefinitionOptionValueRel> iterator =
			cpDefinitionOptionValueRels.iterator();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel = iterator.next();

		BigDecimal quantity = cpDefinitionOptionValueRel.getQuantity();

		BigDecimal cpDefinitionOptionMinDynamicPrice = _getCPInstanceFinalPrice(
			cpDefinitionOptionValueRel.getCProductId(),
			cpDefinitionOptionValueRel.getCPInstanceUuid(), quantity,
			cpDefinitionOptionValueRel.getUnitOfMeasureKey(), commerceContext);

		while (iterator.hasNext()) {
			cpDefinitionOptionValueRel = iterator.next();

			quantity = cpDefinitionOptionValueRel.getQuantity();

			BigDecimal cpInstanceFinalPrice = _getCPInstanceFinalPrice(
				cpDefinitionOptionValueRel.getCProductId(),
				cpDefinitionOptionValueRel.getCPInstanceUuid(), quantity,
				cpDefinitionOptionValueRel.getUnitOfMeasureKey(),
				commerceContext);

			if (!BigDecimalUtil.gt(
					cpDefinitionOptionMinDynamicPrice, cpInstanceFinalPrice)) {

				continue;
			}

			cpDefinitionOptionMinDynamicPrice = cpInstanceFinalPrice;
		}

		return cpDefinitionOptionMinDynamicPrice;
	}

	private BigDecimal _getCPDefinitionOptionMinStaticPrice(
			CPDefinitionOptionRel cpDefinitionOptionRel,
			CommerceContext commerceContext)
		throws PortalException {

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		if (cpDefinitionOptionValueRels.isEmpty()) {
			return BigDecimal.ZERO;
		}

		Iterator<CPDefinitionOptionValueRel> iterator =
			cpDefinitionOptionValueRels.iterator();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel = iterator.next();

		BigDecimal quantity = cpDefinitionOptionValueRel.getQuantity();

		BigDecimal cpDefinitionOptionMinStaticPrice =
			_getCPDefinitionOptionValueFinalPrice(
				cpDefinitionOptionValueRel.getPrice(), quantity);

		while (iterator.hasNext()) {
			cpDefinitionOptionValueRel = iterator.next();

			quantity = cpDefinitionOptionValueRel.getQuantity();

			BigDecimal cpDefinitionOptionValueFinalPrice =
				_getCPDefinitionOptionValueFinalPrice(
					cpDefinitionOptionValueRel.getPrice(), quantity);

			if (!BigDecimalUtil.gt(
					cpDefinitionOptionMinStaticPrice,
					cpDefinitionOptionValueFinalPrice)) {

				continue;
			}

			cpDefinitionOptionMinStaticPrice =
				cpDefinitionOptionValueFinalPrice;
		}

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		return cpDefinitionOptionMinStaticPrice.multiply(
			commerceCurrency.getRate());
	}

	private BigDecimal _getCPDefinitionOptionValueFinalPrice(
		BigDecimal price, BigDecimal quantity) {

		return price.multiply(quantity);
	}

	private BigDecimal _getCPDefinitionOptionValuePriceDifference(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			CPDefinitionOptionValueRel selectedCPDefinitionOptionValueRel,
			String priceType, CommerceContext commerceContext)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		if (_isStaticPriceType(priceType)) {
			BigDecimal price = cpDefinitionOptionValueRel.getPrice();

			if (selectedCPDefinitionOptionValueRel != null) {
				price = price.subtract(
					selectedCPDefinitionOptionValueRel.getPrice());
			}

			return price.multiply(commerceCurrency.getRate());
		}

		BigDecimal cpInstanceFinalPrice = _getCPInstanceFinalPrice(
			cpDefinitionOptionValueRel.getCProductId(),
			cpDefinitionOptionValueRel.getCPInstanceUuid(),
			cpDefinitionOptionValueRel.getQuantity(),
			cpDefinitionOptionValueRel.getUnitOfMeasureKey(), commerceContext);

		if (selectedCPDefinitionOptionValueRel == null) {
			return cpInstanceFinalPrice;
		}

		BigDecimal selectedCPInstanceFinalPrice = _getCPInstanceFinalPrice(
			selectedCPDefinitionOptionValueRel.getCProductId(),
			selectedCPDefinitionOptionValueRel.getCPInstanceUuid(),
			selectedCPDefinitionOptionValueRel.getQuantity(),
			selectedCPDefinitionOptionValueRel.getUnitOfMeasureKey(),
			commerceContext);

		return cpInstanceFinalPrice.subtract(selectedCPInstanceFinalPrice);
	}

	private BigDecimal _getCPInstanceFinalPrice(
			long cProductId, String cpInstanceUuid, BigDecimal quantity,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		CPInstance cpInstance = cpInstanceLocalService.fetchCProductInstance(
			cProductId, cpInstanceUuid);

		if (cpInstance == null) {
			return BigDecimal.ZERO;
		}

		CommerceMoney commerceMoney = getFinalPrice(
			cpInstance.getCPInstanceId(), quantity, unitOfMeasureKey,
			commerceContext);

		if (commerceMoney.isEmpty()) {
			return BigDecimal.ZERO;
		}

		return commerceMoney.getPrice();
	}

	private BigDecimal _getCPInstancePriceDifference(
			long cpInstanceId1, BigDecimal cpInstance1MinQuantity,
			String cpInstance1UnitOfMeasureKey, long cpInstanceId2,
			BigDecimal cpInstance2MinQuantity,
			String cpInstance2UnitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		BigDecimal priceDifference = BigDecimal.ZERO;

		if (cpInstanceId1 > 0) {
			CommerceMoney cpInstance1FinalPriceCommerceMoney = getFinalPrice(
				cpInstanceId1, cpInstance1MinQuantity,
				cpInstance1UnitOfMeasureKey, commerceContext);

			if (!cpInstance1FinalPriceCommerceMoney.isEmpty()) {
				priceDifference = priceDifference.add(
					cpInstance1FinalPriceCommerceMoney.getPrice());
			}
		}

		if (cpInstanceId2 > 0) {
			CommerceMoney cpInstance2FinalPriceCommerceMoney = getFinalPrice(
				cpInstanceId2, cpInstance2MinQuantity,
				cpInstance2UnitOfMeasureKey, commerceContext);

			if (!cpInstance2FinalPriceCommerceMoney.isEmpty()) {
				priceDifference = priceDifference.subtract(
					cpInstance2FinalPriceCommerceMoney.getPrice());
			}
		}

		return priceDifference;
	}

	private boolean _isRequiredPriceContributor(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		if (cpDefinitionOptionRel.isPriceContributor() &&
			(cpDefinitionOptionRel.isRequired() ||
			 cpDefinitionOptionRel.isSkuContributor())) {

			return true;
		}

		return false;
	}

	private boolean _isStaticPriceType(String value) {
		return Objects.equals(
			value, CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);
	}

	private BigDecimal[] _toPriceArray(
		CommerceMoney unitPriceCommerceMoney,
		CommerceMoney promoPriceCommerceMoney, BigDecimal finalPrice) {

		BigDecimal[] prices = {BigDecimal.ZERO, BigDecimal.ZERO, finalPrice};

		if (!unitPriceCommerceMoney.isEmpty()) {
			prices[0] = unitPriceCommerceMoney.getPrice();
		}

		if (!promoPriceCommerceMoney.isEmpty()) {
			prices[1] = promoPriceCommerceMoney.getPrice();
		}

		return prices;
	}

	private void _validate(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
		CPDefinitionOptionValueRel selectedCPDefinitionOptionValueRel) {

		if ((selectedCPDefinitionOptionValueRel != null) &&
			(cpDefinitionOptionValueRel.getCPDefinitionOptionRelId() !=
				selectedCPDefinitionOptionValueRel.
					getCPDefinitionOptionRelId())) {

			throw new IllegalArgumentException(
				"Provided CPDefinitionOptionValueRel parameters must belong " +
					"to the same CPDefinitionOptionRel");
		}
	}

}