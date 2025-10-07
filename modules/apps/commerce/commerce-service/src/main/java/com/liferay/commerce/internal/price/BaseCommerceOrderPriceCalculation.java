/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.price;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.internal.util.CommercePriceConverterUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.price.CommerceOrderPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.price.CommerceOrderPriceImpl;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
public abstract class BaseCommerceOrderPriceCalculation
	implements CommerceOrderPriceCalculation {

	@Override
	public CommerceOrderItemPrice getCommerceOrderItemPrice(
			CommerceCurrency commerceCurrency,
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		return _getCommerceOrderItemPrice(
			commerceCurrency, commerceOrderItem, false);
	}

	@Override
	public CommerceOrderItemPrice getCommerceOrderItemPricePerUnit(
			CommerceCurrency commerceCurrency,
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		return _getCommerceOrderItemPrice(
			commerceCurrency, commerceOrderItem, true);
	}

	protected CommerceOrderPrice getCommerceOrderPriceFromOrder(
			CommerceOrder commerceOrder)
		throws PortalException {

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		CommerceDiscountValue shippingDiscountValue =
			_createCommerceDiscountValue(
				commerceOrder.getShippingAmount(), commerceCurrency,
				commerceOrder.getShippingDiscountAmount(),
				commerceOrder.getShippingDiscountPercentageLevel1(),
				commerceOrder.getShippingDiscountPercentageLevel2(),
				commerceOrder.getShippingDiscountPercentageLevel3(),
				commerceOrder.getShippingDiscountPercentageLevel4());

		CommerceDiscountValue shippingDiscountValueWithTaxAmount =
			_createCommerceDiscountValue(
				commerceOrder.getShippingWithTaxAmount(), commerceCurrency,
				commerceOrder.getShippingDiscountWithTaxAmount(),
				commerceOrder.
					getShippingDiscountPercentageLevel1WithTaxAmount(),
				commerceOrder.
					getShippingDiscountPercentageLevel2WithTaxAmount(),
				commerceOrder.
					getShippingDiscountPercentageLevel3WithTaxAmount(),
				commerceOrder.
					getShippingDiscountPercentageLevel4WithTaxAmount());

		CommerceDiscountValue subtotalDiscountValue =
			_createCommerceDiscountValue(
				commerceOrder.getSubtotal(), commerceCurrency,
				commerceOrder.getSubtotalDiscountAmount(),
				commerceOrder.getSubtotalDiscountPercentageLevel1(),
				commerceOrder.getSubtotalDiscountPercentageLevel2(),
				commerceOrder.getSubtotalDiscountPercentageLevel3(),
				commerceOrder.getSubtotalDiscountPercentageLevel4());

		CommerceDiscountValue subtotalDiscountValueWithTaxAmount =
			_createCommerceDiscountValue(
				commerceOrder.getSubtotalWithTaxAmount(), commerceCurrency,
				commerceOrder.getSubtotalDiscountWithTaxAmount(),
				commerceOrder.
					getSubtotalDiscountPercentageLevel1WithTaxAmount(),
				commerceOrder.
					getSubtotalDiscountPercentageLevel2WithTaxAmount(),
				commerceOrder.
					getSubtotalDiscountPercentageLevel3WithTaxAmount(),
				commerceOrder.
					getSubtotalDiscountPercentageLevel4WithTaxAmount());

		BigDecimal total = commerceOrder.getTotal();

		if (BigDecimalUtil.gte(total, commerceOrder.getTaxAmount())) {
			total = total.subtract(commerceOrder.getTaxAmount());
		}

		BigDecimal totalDiscountAmount = BigDecimal.ZERO;

		if (commerceOrder.getTotalDiscountAmount() != null) {
			totalDiscountAmount = commerceOrder.getTotalDiscountAmount();
		}

		CommerceDiscountValue totalDiscountValue = _createCommerceDiscountValue(
			total.add(totalDiscountAmount), commerceCurrency,
			commerceOrder.getTotalDiscountAmount(),
			commerceOrder.getTotalDiscountPercentageLevel1(),
			commerceOrder.getTotalDiscountPercentageLevel2(),
			commerceOrder.getTotalDiscountPercentageLevel3(),
			commerceOrder.getTotalDiscountPercentageLevel4());

		BigDecimal totalWithTaxAmount = total;

		if (commerceOrder.getTotalWithTaxAmount() != null) {
			totalWithTaxAmount = commerceOrder.getTotalWithTaxAmount();
		}

		BigDecimal totalDiscountWithTaxAmount = totalDiscountAmount;

		if (commerceOrder.getTotalDiscountWithTaxAmount() != null) {
			totalDiscountWithTaxAmount =
				commerceOrder.getTotalDiscountWithTaxAmount();
		}

		CommerceDiscountValue totalDiscountValueWithTaxAmount =
			_createCommerceDiscountValue(
				totalWithTaxAmount.add(totalDiscountWithTaxAmount),
				commerceCurrency, commerceOrder.getTotalDiscountWithTaxAmount(),
				commerceOrder.getTotalDiscountPercentageLevel1WithTaxAmount(),
				commerceOrder.getTotalDiscountPercentageLevel2WithTaxAmount(),
				commerceOrder.getTotalDiscountPercentageLevel3WithTaxAmount(),
				commerceOrder.getTotalDiscountPercentageLevel4WithTaxAmount());

		CommerceOrderPriceImpl commerceOrderPriceImpl =
			new CommerceOrderPriceImpl();

		commerceOrderPriceImpl.setShippingDiscountValue(shippingDiscountValue);
		commerceOrderPriceImpl.setShippingDiscountValueWithTaxAmount(
			shippingDiscountValueWithTaxAmount);
		commerceOrderPriceImpl.setShippingValue(
			commerceMoneyFactory.create(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getShippingAmount()));
		commerceOrderPriceImpl.setShippingValueWithTaxAmount(
			commerceMoneyFactory.create(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getShippingWithTaxAmount()));
		commerceOrderPriceImpl.setSubtotal(
			commerceMoneyFactory.create(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getSubtotal()));
		commerceOrderPriceImpl.setSubtotalDiscountValue(subtotalDiscountValue);
		commerceOrderPriceImpl.setSubtotalDiscountValueWithTaxAmount(
			subtotalDiscountValueWithTaxAmount);
		commerceOrderPriceImpl.setSubtotalWithTaxAmount(
			commerceMoneyFactory.create(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getSubtotalWithTaxAmount()));
		commerceOrderPriceImpl.setTaxValue(
			commerceMoneyFactory.create(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getTaxAmount()));
		commerceOrderPriceImpl.setTotal(
			commerceMoneyFactory.create(
				commerceOrder.getCommerceCurrency(), commerceOrder.getTotal()));
		commerceOrderPriceImpl.setTotalDiscountValue(totalDiscountValue);
		commerceOrderPriceImpl.setTotalDiscountValueWithTaxAmount(
			totalDiscountValueWithTaxAmount);
		commerceOrderPriceImpl.setTotalWithTaxAmount(
			commerceMoneyFactory.create(
				commerceOrder.getCommerceCurrency(),
				commerceOrder.getTotalWithTaxAmount()));

		return commerceOrderPriceImpl;
	}

	protected CommerceOrderPriceImpl getEmptyCommerceOrderPrice(
		CommerceCurrency commerceCurrency) {

		CommerceMoney zeroCommerceMoney = commerceMoneyFactory.create(
			commerceCurrency, BigDecimal.ZERO);

		CommerceOrderPriceImpl commerceOrderPriceImpl =
			new CommerceOrderPriceImpl();

		commerceOrderPriceImpl.setShippingDiscountValue(null);
		commerceOrderPriceImpl.setShippingDiscountValueWithTaxAmount(null);
		commerceOrderPriceImpl.setShippingValue(zeroCommerceMoney);
		commerceOrderPriceImpl.setShippingValueWithTaxAmount(zeroCommerceMoney);
		commerceOrderPriceImpl.setSubtotal(zeroCommerceMoney);
		commerceOrderPriceImpl.setSubtotalDiscountValue(null);
		commerceOrderPriceImpl.setSubtotalDiscountValueWithTaxAmount(null);
		commerceOrderPriceImpl.setSubtotalWithTaxAmount(zeroCommerceMoney);
		commerceOrderPriceImpl.setTaxValue(zeroCommerceMoney);
		commerceOrderPriceImpl.setTotal(zeroCommerceMoney);
		commerceOrderPriceImpl.setTotalDiscountValue(null);
		commerceOrderPriceImpl.setTotalDiscountValueWithTaxAmount(null);
		commerceOrderPriceImpl.setTotalWithTaxAmount(zeroCommerceMoney);

		return commerceOrderPriceImpl;
	}

	protected void setDiscountValues(
			boolean discountsTargetNetPrice, BigDecimal shippingAmount,
			BigDecimal shippingDiscounted,
			CommerceDiscountValue orderShippingCommerceDiscountValue,
			BigDecimal subtotalAmount, BigDecimal subtotalDiscounted,
			CommerceDiscountValue orderSubtotalCommerceDiscountValue,
			BigDecimal totalAmount, BigDecimal totalDiscounted,
			CommerceDiscountValue orderTotalCommerceDiscountValue,
			CommerceOrderPriceImpl commerceOrderPriceImpl,
			CommerceOrder commerceOrder)
		throws PortalException {

		if (discountsTargetNetPrice) {
			commerceOrderPriceImpl.setShippingDiscountValue(
				orderShippingCommerceDiscountValue);
			commerceOrderPriceImpl.setSubtotalDiscountValue(
				orderSubtotalCommerceDiscountValue);
			commerceOrderPriceImpl.setTotalDiscountValue(
				orderTotalCommerceDiscountValue);
		}
		else {
			CommerceCurrency commerceCurrency =
				commerceOrder.getCommerceCurrency();

			RoundingMode roundingMode = RoundingMode.valueOf(
				commerceCurrency.getRoundingMode());

			commerceOrderPriceImpl.setShippingDiscountValue(
				CommercePriceConverterUtil.getConvertedCommerceDiscountValue(
					orderShippingCommerceDiscountValue, shippingAmount,
					shippingDiscounted, commerceMoneyFactory, roundingMode));
			commerceOrderPriceImpl.setSubtotalDiscountValue(
				CommercePriceConverterUtil.getConvertedCommerceDiscountValue(
					orderSubtotalCommerceDiscountValue, subtotalAmount,
					subtotalDiscounted, commerceMoneyFactory, roundingMode));
			commerceOrderPriceImpl.setTotalDiscountValue(
				CommercePriceConverterUtil.getConvertedCommerceDiscountValue(
					orderTotalCommerceDiscountValue, totalAmount,
					totalDiscounted, commerceMoneyFactory, roundingMode));
		}
	}

	protected void setDiscountValuesWithTaxAmount(
			boolean discountsTargetNetPrice, BigDecimal shippingWithTaxAmount,
			BigDecimal shippingDiscountedWithTaxAmount,
			CommerceDiscountValue orderShippingCommerceDiscountValue,
			BigDecimal subtotalWithTaxAmount,
			BigDecimal subtotalDiscountedWithTaxAmount,
			CommerceDiscountValue orderSubtotalCommerceDiscountValue,
			BigDecimal totalWithTaxAmount,
			BigDecimal totalDiscountedWithTaxAmount,
			CommerceDiscountValue orderTotalCommerceDiscountValue,
			CommerceOrderPriceImpl commerceOrderPriceImpl,
			CommerceOrder commerceOrder)
		throws PortalException {

		if (discountsTargetNetPrice) {
			CommerceCurrency commerceCurrency =
				commerceOrder.getCommerceCurrency();

			RoundingMode roundingMode = RoundingMode.valueOf(
				commerceCurrency.getRoundingMode());

			commerceOrderPriceImpl.setShippingDiscountValueWithTaxAmount(
				CommercePriceConverterUtil.getConvertedCommerceDiscountValue(
					orderShippingCommerceDiscountValue, shippingWithTaxAmount,
					shippingDiscountedWithTaxAmount, commerceMoneyFactory,
					roundingMode));
			commerceOrderPriceImpl.setSubtotalDiscountValueWithTaxAmount(
				CommercePriceConverterUtil.getConvertedCommerceDiscountValue(
					orderSubtotalCommerceDiscountValue, subtotalWithTaxAmount,
					subtotalDiscountedWithTaxAmount, commerceMoneyFactory,
					roundingMode));
			commerceOrderPriceImpl.setTotalDiscountValueWithTaxAmount(
				CommercePriceConverterUtil.getConvertedCommerceDiscountValue(
					orderTotalCommerceDiscountValue, totalWithTaxAmount,
					totalDiscountedWithTaxAmount, commerceMoneyFactory,
					roundingMode));
		}
		else {
			commerceOrderPriceImpl.setShippingDiscountValueWithTaxAmount(
				orderShippingCommerceDiscountValue);
			commerceOrderPriceImpl.setSubtotalDiscountValueWithTaxAmount(
				orderSubtotalCommerceDiscountValue);
			commerceOrderPriceImpl.setTotalDiscountValueWithTaxAmount(
				orderTotalCommerceDiscountValue);
		}
	}

	@Reference
	protected CommerceChannelLocalService commerceChannelLocalService;

	@Reference
	protected CommerceMoneyFactory commerceMoneyFactory;

	private CommerceDiscountValue _createCommerceDiscountValue(
		BigDecimal amount, CommerceCurrency commerceCurrency,
		BigDecimal discountAmount, BigDecimal level1, BigDecimal level2,
		BigDecimal level3, BigDecimal level4) {

		if ((discountAmount == null) || (amount == null) ||
			BigDecimalUtil.lte(amount, BigDecimal.ZERO)) {

			return new CommerceDiscountValue(
				0,
				commerceMoneyFactory.create(commerceCurrency, BigDecimal.ZERO),
				BigDecimal.ZERO,
				new BigDecimal[] {
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO
				});
		}

		BigDecimal[] discountPercentageValues = {
			level1, level2, level3, level4
		};

		BigDecimal discountPercentage = discountAmount.divide(
			amount, RoundingMode.valueOf(commerceCurrency.getRoundingMode()));

		discountPercentage = discountPercentage.multiply(
			BigDecimal.valueOf(100));

		return new CommerceDiscountValue(
			0, commerceMoneyFactory.create(commerceCurrency, discountAmount),
			discountPercentage, discountPercentageValues);
	}

	private boolean _equalsZero(BigDecimal value) {
		if ((value != null) && BigDecimalUtil.isZero(value)) {
			return true;
		}

		return false;
	}

	private CommerceOrderItemPrice _getCommerceOrderItemPrice(
			CommerceCurrency commerceCurrency,
			CommerceOrderItem commerceOrderItem, boolean unit)
		throws PortalException {

		BigDecimal parentQuantity = commerceOrderItem.getQuantity();

		CommerceMoney unitPriceCommerceMoney =
			commerceOrderItem.getUnitPriceMoney();
		CommerceMoney promoPriceCommerceMoney =
			commerceOrderItem.getPromoPriceMoney();

		CommerceMoney discountAmountCommerceMoney =
			commerceOrderItem.getDiscountAmountMoney();

		CommerceMoney finalPriceCommerceMoney =
			commerceOrderItem.getFinalPriceMoney();

		BigDecimal discountPercentageLevel1 =
			commerceOrderItem.getDiscountPercentageLevel1();
		BigDecimal discountPercentageLevel2 =
			commerceOrderItem.getDiscountPercentageLevel2();
		BigDecimal discountPercentageLevel3 =
			commerceOrderItem.getDiscountPercentageLevel3();
		BigDecimal discountPercentageLevel4 =
			commerceOrderItem.getDiscountPercentageLevel4();

		CommerceChannel commerceChannel =
			commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrderItem.getGroupId());

		String priceDisplayType = commerceChannel.getPriceDisplayType();

		if (priceDisplayType.equals(
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			unitPriceCommerceMoney =
				commerceOrderItem.getUnitPriceWithTaxAmountMoney();
			promoPriceCommerceMoney =
				commerceOrderItem.getPromoPriceWithTaxAmountMoney();

			discountAmountCommerceMoney =
				commerceOrderItem.getDiscountWithTaxAmountMoney();

			discountPercentageLevel1 =
				commerceOrderItem.getDiscountPercentageLevel1WithTaxAmount();
			discountPercentageLevel2 =
				commerceOrderItem.getDiscountPercentageLevel2WithTaxAmount();
			discountPercentageLevel3 =
				commerceOrderItem.getDiscountPercentageLevel3WithTaxAmount();
			discountPercentageLevel4 =
				commerceOrderItem.getDiscountPercentageLevel4WithTaxAmount();

			finalPriceCommerceMoney =
				commerceOrderItem.getFinalPriceWithTaxAmountMoney();
		}

		BigDecimal unitPrice = BigDecimal.ZERO;
		BigDecimal promoPrice = BigDecimal.ZERO;
		BigDecimal finalPrice = BigDecimal.ZERO;
		BigDecimal discountAmount = BigDecimal.ZERO;

		if (!unitPriceCommerceMoney.isEmpty()) {
			unitPrice = unitPriceCommerceMoney.getPrice();
		}

		if (!promoPriceCommerceMoney.isEmpty()) {
			promoPrice = promoPriceCommerceMoney.getPrice();
		}

		if (!finalPriceCommerceMoney.isEmpty()) {
			finalPrice = finalPriceCommerceMoney.getPrice();
		}

		if (!discountAmountCommerceMoney.isEmpty()) {
			discountAmount = discountAmountCommerceMoney.getPrice();
		}

		List<CommerceOrderItem> childCommerceOrderItems =
			commerceOrderItem.getChildCommerceOrderItems();

		for (CommerceOrderItem childCommerceOrderItem :
				childCommerceOrderItems) {

			BigDecimal childUnitPrice = childCommerceOrderItem.getUnitPrice();
			BigDecimal childPromoPrice = childCommerceOrderItem.getPromoPrice();
			BigDecimal childDiscountAmount =
				childCommerceOrderItem.getDiscountAmount();
			BigDecimal childFinalPrice = childCommerceOrderItem.getFinalPrice();

			if (priceDisplayType.equals(
					CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

				childUnitPrice =
					childCommerceOrderItem.getUnitPriceWithTaxAmount();
				childPromoPrice =
					childCommerceOrderItem.getPromoPriceWithTaxAmount();
				childDiscountAmount =
					childCommerceOrderItem.getDiscountWithTaxAmount();
				childFinalPrice =
					childCommerceOrderItem.getFinalPriceWithTaxAmount();
			}

			BigDecimal incrementalOrderQuantity = BigDecimal.ONE;

			if (Validator.isNotNull(
					childCommerceOrderItem.getUnitOfMeasureKey()) &&
				BigDecimalUtil.gt(
					childCommerceOrderItem.
						getUnitOfMeasureIncrementalOrderQuantity(),
					BigDecimal.ZERO)) {

				incrementalOrderQuantity =
					childCommerceOrderItem.
						getUnitOfMeasureIncrementalOrderQuantity();
			}

			if (_equalsZero(promoPrice) && _greaterThanZero(childPromoPrice)) {
				promoPrice = promoPrice.add(unitPrice);
			}
			else if (_equalsZero(childPromoPrice) &&
					 _greaterThanZero(promoPrice)) {

				promoPrice = promoPrice.add(
					_getPricePerUnit(
						commerceCurrency, incrementalOrderQuantity,
						childUnitPrice, childCommerceOrderItem.getQuantity(),
						parentQuantity));
			}

			unitPrice = unitPrice.add(childUnitPrice);

			promoPrice = promoPrice.add(
				_getPricePerUnit(
					commerceCurrency, incrementalOrderQuantity, childPromoPrice,
					childCommerceOrderItem.getQuantity(), parentQuantity));

			discountAmount = discountAmount.add(childDiscountAmount);

			finalPrice = finalPrice.add(childFinalPrice);
		}

		BigDecimal unitOfMeasureIncrementalOrderQuantity =
			commerceOrderItem.getUnitOfMeasureIncrementalOrderQuantity();

		if ((unitOfMeasureIncrementalOrderQuantity == null) ||
			unitOfMeasureIncrementalOrderQuantity.equals(BigDecimal.ZERO)) {

			unitOfMeasureIncrementalOrderQuantity = BigDecimal.ONE;
		}

		if (unit) {
			finalPrice = finalPrice.divide(
				parentQuantity.divide(
					unitOfMeasureIncrementalOrderQuantity,
					RoundingMode.valueOf(commerceCurrency.getRoundingMode())),
				RoundingMode.valueOf(commerceCurrency.getRoundingMode()));
		}

		CommerceOrderItemPrice commerceOrderItemPrice =
			new CommerceOrderItemPrice(
				commerceMoneyFactory.create(commerceCurrency, unitPrice));

		commerceOrderItemPrice.setPriceOnApplication(
			commerceOrderItem.isPriceOnApplication());

		_updatePromoPrice(commerceCurrency, commerceOrderItemPrice, promoPrice);

		_updateFinalPrice(commerceCurrency, commerceOrderItemPrice, finalPrice);

		_updateDiscounts(
			commerceCurrency, commerceOrderItemPrice, discountAmount,
			discountPercentageLevel1, discountPercentageLevel2,
			discountPercentageLevel3, discountPercentageLevel4,
			parentQuantity.divide(
				unitOfMeasureIncrementalOrderQuantity,
				RoundingMode.valueOf(commerceCurrency.getRoundingMode())),
			unitPrice);

		return commerceOrderItemPrice;
	}

	private BigDecimal _getDiscountPercentage(
		BigDecimal amount, BigDecimal discount, RoundingMode roundingMode) {

		if ((amount == null) || BigDecimalUtil.isZero(amount)) {
			return BigDecimal.ZERO;
		}

		BigDecimal discountedAmount = amount.subtract(discount);

		double percentage =
			discountedAmount.doubleValue() / amount.doubleValue();

		BigDecimal discountPercentage = new BigDecimal(percentage);

		discountPercentage = discountPercentage.multiply(_ONE_HUNDRED);

		MathContext mathContext = new MathContext(
			discountPercentage.precision(), roundingMode);

		return _ONE_HUNDRED.subtract(discountPercentage, mathContext);
	}

	private BigDecimal _getPricePerUnit(
		CommerceCurrency commerceCurrency, BigDecimal incrementalOrderQuantity,
		BigDecimal price, BigDecimal quantity, BigDecimal parentQuantity) {

		BigDecimal pricePerUnit = price.multiply(quantity);

		RoundingMode roundingMode = RoundingMode.valueOf(
			commerceCurrency.getRoundingMode());

		pricePerUnit = pricePerUnit.divide(
			incrementalOrderQuantity, commerceCurrency.getMaxFractionDigits(),
			roundingMode);

		return pricePerUnit.divide(parentQuantity, roundingMode);
	}

	private boolean _greaterThanZero(BigDecimal value) {
		if ((value == null) || BigDecimalUtil.lte(value, BigDecimal.ZERO)) {
			return false;
		}

		return true;
	}

	private void _updateDiscounts(
		CommerceCurrency commerceCurrency,
		CommerceOrderItemPrice commerceOrderItemPrice,
		BigDecimal discountAmount, BigDecimal discountPercentageLevel1,
		BigDecimal discountPercentageLevel2,
		BigDecimal discountPercentageLevel3,
		BigDecimal discountPercentageLevel4, BigDecimal quantity,
		BigDecimal unitPrice) {

		BigDecimal activePrice = unitPrice;

		CommerceMoney promoPriceCommerceMoney =
			commerceOrderItemPrice.getPromoPrice();

		if ((promoPriceCommerceMoney != null) &&
			!promoPriceCommerceMoney.isEmpty() &&
			BigDecimalUtil.gt(
				promoPriceCommerceMoney.getPrice(), BigDecimal.ZERO) &&
			BigDecimalUtil.gt(unitPrice, promoPriceCommerceMoney.getPrice())) {

			activePrice = promoPriceCommerceMoney.getPrice();
		}

		commerceOrderItemPrice.setDiscountAmount(
			commerceMoneyFactory.create(commerceCurrency, discountAmount));
		commerceOrderItemPrice.setDiscountPercentage(
			_getDiscountPercentage(
				activePrice.multiply(quantity), discountAmount,
				RoundingMode.valueOf(commerceCurrency.getRoundingMode())));
		commerceOrderItemPrice.setDiscountPercentageLevel1(
			discountPercentageLevel1);
		commerceOrderItemPrice.setDiscountPercentageLevel2(
			discountPercentageLevel2);
		commerceOrderItemPrice.setDiscountPercentageLevel3(
			discountPercentageLevel3);
		commerceOrderItemPrice.setDiscountPercentageLevel4(
			discountPercentageLevel4);
	}

	private void _updateFinalPrice(
		CommerceCurrency commerceCurrency,
		CommerceOrderItemPrice commerceOrderItemPrice, BigDecimal finalPrice) {

		commerceOrderItemPrice.setFinalPrice(
			commerceMoneyFactory.create(commerceCurrency, finalPrice));
	}

	private void _updatePromoPrice(
		CommerceCurrency commerceCurrency,
		CommerceOrderItemPrice commerceOrderItemPrice, BigDecimal promoPrice) {

		CommerceMoney unitPriceCommerceMoney =
			commerceOrderItemPrice.getUnitPrice();

		if (!_greaterThanZero(promoPrice) ||
			(BigDecimalUtil.gte(
				promoPrice, unitPriceCommerceMoney.getPrice()) &&
			 _greaterThanZero(unitPriceCommerceMoney.getPrice()))) {

			return;
		}

		commerceOrderItemPrice.setPromoPrice(
			commerceMoneyFactory.create(commerceCurrency, promoPrice));
	}

	private static final BigDecimal _ONE_HUNDRED = BigDecimal.valueOf(100);

}