/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.util;

import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.tax.CommerceTaxCalculation;
import com.liferay.commerce.tax.CommerceTaxValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.List;

/**
 * @author Riccardo Alberti
 */
public class CommercePriceConverterUtil {

	public static CommerceDiscountValue getConvertedCommerceDiscountValue(
		CommerceDiscountValue commerceDiscountValue, BigDecimal initialPrice,
		BigDecimal discountedPrice, CommerceMoneyFactory commerceMoneyFactory,
		RoundingMode roundingMode) {

		if (commerceDiscountValue == null) {
			return null;
		}

		CommerceMoney currentDiscountAmountCommerceMoney =
			commerceDiscountValue.getDiscountAmount();

		BigDecimal discountAmount = initialPrice.subtract(discountedPrice);

		CommerceMoney convertedDiscountAmountCommerceMoney =
			commerceMoneyFactory.create(
				currentDiscountAmountCommerceMoney.getCommerceCurrency(),
				discountAmount);

		BigDecimal discountPercentage = _ONE_HUNDRED;

		if (BigDecimalUtil.gt(discountedPrice, BigDecimal.ZERO)) {
			discountPercentage = _getDiscountPercentage(
				discountedPrice, initialPrice, roundingMode);
		}

		return new CommerceDiscountValue(
			commerceDiscountValue.getId(), convertedDiscountAmountCommerceMoney,
			discountPercentage,
			_getPercentages(
				commerceDiscountValue.getDiscountPercentage(),
				discountPercentage, commerceDiscountValue.getPercentages(),
				roundingMode));
	}

	public static BigDecimal getConvertedPrice(
			long commerceBillingAddressId, long commerceChannelGroupId,
			String commerceCurrencyCode, long commerceShippingAddressId,
			CommerceTaxCalculation commerceTaxCalculation, long cpInstanceId,
			boolean includeTax, BigDecimal price)
		throws PortalException {

		if (BigDecimalUtil.isZero(price) && includeTax) {
			return price;
		}

		List<CommerceTaxValue> commerceTaxValues =
			commerceTaxCalculation.getCommerceTaxValues(
				commerceBillingAddressId, commerceChannelGroupId,
				commerceCurrencyCode, commerceShippingAddressId, cpInstanceId,
				includeTax, price);

		if (commerceTaxValues.isEmpty()) {
			return price;
		}

		BigDecimal taxAmount = BigDecimal.ZERO;

		for (CommerceTaxValue commerceTaxValue : commerceTaxValues) {
			taxAmount = taxAmount.add(commerceTaxValue.getAmount());
		}

		if (includeTax) {
			return price.subtract(taxAmount);
		}

		return price.add(taxAmount);
	}

	private static BigDecimal _getDiscountPercentage(
		BigDecimal discountedAmount, BigDecimal amount,
		RoundingMode roundingMode) {

		double actualPrice = discountedAmount.doubleValue();
		double originalPrice = amount.doubleValue();

		double percentage = actualPrice / originalPrice;

		BigDecimal discountPercentage = new BigDecimal(percentage);

		discountPercentage = discountPercentage.multiply(_ONE_HUNDRED);

		MathContext mathContext = new MathContext(
			discountPercentage.precision(), roundingMode);

		return _ONE_HUNDRED.subtract(discountPercentage, mathContext);
	}

	private static BigDecimal[] _getPercentages(
		BigDecimal currentPercentage, BigDecimal percentage,
		BigDecimal[] percentages, RoundingMode roundingMode) {

		if ((currentPercentage == null) ||
			BigDecimalUtil.isZero(currentPercentage) || (percentage == null) ||
			BigDecimalUtil.isZero(percentage)) {

			return new BigDecimal[] {
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO
			};
		}

		BigDecimal percentageRatio = percentage.divide(
			currentPercentage, _SCALE, roundingMode);

		for (int i = 0; i < percentages.length; i++) {
			if ((percentages[i] == null) ||
				BigDecimalUtil.isZero(percentages[i])) {

				continue;
			}

			percentages[i] = percentages[i].multiply(percentageRatio);
		}

		return percentages;
	}

	private static final BigDecimal _ONE_HUNDRED = BigDecimal.valueOf(100);

	private static final int _SCALE = 10;

}