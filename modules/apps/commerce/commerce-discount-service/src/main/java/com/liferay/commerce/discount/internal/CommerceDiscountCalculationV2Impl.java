/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.discount.CommerceDiscountCalculation;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.discount.application.strategy.CommerceDiscountApplicationStrategy;
import com.liferay.commerce.discount.application.strategy.CommerceDiscountApplicationStrategyRegistry;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountUsageEntryLocalService;
import com.liferay.commerce.discount.validator.helper.CommerceDiscountValidatorHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.commerce.price.list.service.CommercePriceListDiscountRelLocalService;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "commerce.discount.calculation.key=v2.0",
	service = CommerceDiscountCalculation.class
)
public class CommerceDiscountCalculationV2Impl
	extends BaseCommerceDiscountCalculation {

	@Override
	public CommerceDiscountValue getOrderShippingCommerceDiscountValue(
			CommerceOrder commerceOrder, BigDecimal shippingAmount,
			CommerceContext commerceContext)
		throws PortalException {

		if (commerceOrder == null) {
			return null;
		}

		return _getCommerceDiscountValue(
			commerceOrder, shippingAmount, commerceContext,
			CommerceDiscountConstants.TARGET_SHIPPING);
	}

	@Override
	public CommerceDiscountValue getOrderSubtotalCommerceDiscountValue(
			CommerceOrder commerceOrder, BigDecimal subtotalAmount,
			CommerceContext commerceContext)
		throws PortalException {

		if (commerceOrder == null) {
			return null;
		}

		return _getCommerceDiscountValue(
			commerceOrder, subtotalAmount, commerceContext,
			CommerceDiscountConstants.TARGET_SUBTOTAL);
	}

	@Override
	public CommerceDiscountValue getOrderTotalCommerceDiscountValue(
			CommerceOrder commerceOrder, BigDecimal totalAmount,
			CommerceContext commerceContext)
		throws PortalException {

		if (commerceOrder == null) {
			return null;
		}

		return _getCommerceDiscountValue(
			commerceOrder, totalAmount, commerceContext,
			CommerceDiscountConstants.TARGET_TOTAL);
	}

	@Override
	public CommerceDiscountValue getProductCommerceDiscountValue(
			long cpInstanceId, BigDecimal quantity, BigDecimal productUnitPrice,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		return getProductCommerceDiscountValue(
			cpInstanceId, 0, quantity, productUnitPrice, unitOfMeasureKey,
			commerceContext);
	}

	@Override
	public CommerceDiscountValue getProductCommerceDiscountValue(
			long cpInstanceId, long commercePriceListId, BigDecimal quantity,
			BigDecimal productUnitPrice, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException {

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		List<CommercePriceListDiscountRel> commercePriceListDiscountRels =
			_commercePriceListDiscountRelLocalService.
				getCommercePriceListDiscountRels(commercePriceListId);

		if ((commercePriceListDiscountRels != null) &&
			!commercePriceListDiscountRels.isEmpty()) {

			List<CommerceDiscount> commerceDiscounts =
				commerceDiscountLocalService.getPriceListCommerceDiscounts(
					TransformUtil.transformToLongArray(
						commercePriceListDiscountRels,
						CommercePriceListDiscountRel::getCommerceDiscountId),
					cpInstance.getCPDefinitionId());

			if (commerceDiscounts.isEmpty()) {
				return null;
			}

			return _getCommerceDiscountValues(
				productUnitPrice, quantity, commerceContext, commerceDiscounts);
		}

		long commerceOrderTypeId = 0;

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (commerceOrder != null) {
			commerceOrderTypeId = commerceOrder.getCommerceOrderTypeId();
		}

		List<CommerceDiscount> commerceDiscounts =
			getProductCommerceDiscountByHierarchy(
				cpInstance.getCompanyId(), commerceContext, commerceOrderTypeId,
				cpInstance.getCPDefinitionId(), cpInstanceId, unitOfMeasureKey);

		if (commerceDiscounts.isEmpty()) {
			return null;
		}

		return _getCommerceDiscountValues(
			productUnitPrice, quantity, commerceContext, commerceDiscounts);
	}

	private CommerceDiscountApplicationStrategy
			_getCommerceDiscountApplicationStrategy()
		throws ConfigurationException {

		CommercePricingConfiguration commercePricingConfiguration =
			_configurationProvider.getSystemConfiguration(
				CommercePricingConfiguration.class);

		String commerceDiscountApplicationStrategyKey =
			commercePricingConfiguration.commerceDiscountApplicationStrategy();

		CommerceDiscountApplicationStrategy
			commerceDiscountApplicationStrategy =
				_commerceDiscountApplicationStrategyRegistry.get(
					commerceDiscountApplicationStrategyKey);

		if (commerceDiscountApplicationStrategy == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No commerce discount application strategy specified for " +
						commerceDiscountApplicationStrategyKey);
			}
		}

		return commerceDiscountApplicationStrategy;
	}

	private BigDecimal _getCommerceDiscountLevel(
			BigDecimal currentDiscountLevel, BigDecimal commercePrice,
			CommerceCurrency commerceCurrency, long commerceDiscountId,
			BigDecimal commerceDiscountValue, boolean usePercentage)
		throws PortalException {

		if ((commerceDiscountValue == null) ||
			BigDecimalUtil.isZero(commercePrice)) {

			return null;
		}

		BigDecimal discountAmount = BigDecimal.ZERO;

		if (usePercentage) {
			discountAmount = commercePrice.multiply(commerceDiscountValue);
			discountAmount = discountAmount.divide(_ONE_HUNDRED);

			CommerceDiscount commerceDiscount =
				commerceDiscountLocalService.getCommerceDiscount(
					commerceDiscountId);

			BigDecimal maximumDiscountAmount =
				commerceDiscount.getMaximumDiscountAmount();

			if (BigDecimalUtil.gt(maximumDiscountAmount, BigDecimal.ZERO) &&
				BigDecimalUtil.gt(discountAmount, maximumDiscountAmount)) {

				discountAmount = commerceDiscount.getMaximumDiscountAmount();
			}
		}
		else {
			discountAmount = commerceDiscountValue;

			if (BigDecimalUtil.gt(commerceDiscountValue, commercePrice)) {
				discountAmount = commercePrice;
			}
		}

		RoundingMode roundingMode = RoundingMode.valueOf(
			commerceCurrency.getRoundingMode());

		BigDecimal discountedAmount = commercePrice.subtract(discountAmount);

		BigDecimal discountPercentage = _getDiscountPercentage(
			discountedAmount, commercePrice, roundingMode);

		if ((currentDiscountLevel == null) ||
			BigDecimalUtil.gt(discountPercentage, currentDiscountLevel)) {

			return discountPercentage;
		}

		return currentDiscountLevel;
	}

	private BigDecimal[] _getCommerceDiscountLevels(
			String couponCode, BigDecimal commercePrice,
			CommerceContext commerceContext,
			List<CommerceDiscount> commerceDiscounts)
		throws PortalException {

		if (couponCode.isEmpty()) {
			CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

			if (commerceOrder != null) {
				couponCode = commerceOrder.getCouponCode();
			}
		}

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		BigDecimal[] levels = {
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
		};

		for (CommerceDiscount commerceDiscount : commerceDiscounts) {
			String discountCouponCode = commerceDiscount.getCouponCode();

			if (!_isValidCouponCode(
					commerceDiscount.getCommerceDiscountId(), couponCode,
					discountCouponCode, commerceContext) ||
				!_isValidDiscount(commerceContext, commerceDiscount)) {

				continue;
			}

			String discountLevel = commerceDiscount.getLevel();

			if (discountLevel.isEmpty() ||
				discountLevel.equals(CommerceDiscountConstants.LEVEL_L1)) {

				levels[0] = _getCommerceDiscountLevel(
					levels[0], commercePrice, commerceCurrency,
					commerceDiscount.getCommerceDiscountId(),
					commerceDiscount.getLevel1(),
					commerceDiscount.isUsePercentage());
			}

			if (!commerceDiscount.isUsePercentage()) {
				continue;
			}

			if (discountLevel.isEmpty() ||
				discountLevel.equals(CommerceDiscountConstants.LEVEL_L2)) {

				levels[1] = _getCommerceDiscountLevel(
					levels[1], commercePrice, commerceCurrency,
					commerceDiscount.getCommerceDiscountId(),
					commerceDiscount.getLevel2(),
					commerceDiscount.isUsePercentage());
			}

			if (discountLevel.isEmpty() ||
				discountLevel.equals(CommerceDiscountConstants.LEVEL_L3)) {

				levels[2] = _getCommerceDiscountLevel(
					levels[2], commercePrice, commerceCurrency,
					commerceDiscount.getCommerceDiscountId(),
					commerceDiscount.getLevel3(),
					commerceDiscount.isUsePercentage());
			}

			if (!discountLevel.isEmpty() &&
				!discountLevel.equals(CommerceDiscountConstants.LEVEL_L4)) {

				continue;
			}

			levels[3] = _getCommerceDiscountLevel(
				levels[3], commercePrice, commerceCurrency,
				commerceDiscount.getCommerceDiscountId(),
				commerceDiscount.getLevel4(),
				commerceDiscount.isUsePercentage());
		}

		return levels;
	}

	private CommerceDiscountValue _getCommerceDiscountValue(
			CommerceOrder commerceOrder, BigDecimal amount,
			CommerceContext commerceContext, String target)
		throws PortalException {

		if ((amount == null) || BigDecimalUtil.lte(amount, BigDecimal.ZERO)) {
			return null;
		}

		List<CommerceDiscount> commerceDiscounts =
			getOrderCommerceDiscountByHierarchy(
				commerceOrder.getCompanyId(), commerceContext,
				commerceOrder.getCommerceOrderTypeId(), target);

		if (commerceDiscounts.isEmpty()) {
			return null;
		}

		BigDecimal[] commerceDiscountLevels = _getCommerceDiscountLevels(
			commerceOrder.getCouponCode(), amount, commerceContext,
			commerceDiscounts);

		CommerceDiscountApplicationStrategy
			commerceDiscountApplicationStrategy =
				_getCommerceDiscountApplicationStrategy();

		BigDecimal discountedAmount =
			commerceDiscountApplicationStrategy.applyCommerceDiscounts(
				amount, commerceDiscountLevels);

		BigDecimal currentDiscountAmount = amount.subtract(discountedAmount);

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		RoundingMode roundingMode = RoundingMode.valueOf(
			commerceCurrency.getRoundingMode());

		currentDiscountAmount = currentDiscountAmount.setScale(
			_SCALE, roundingMode);

		CommerceMoney discountAmountCommerceMoney =
			_commerceMoneyFactory.create(
				commerceCurrency, currentDiscountAmount);

		return new CommerceDiscountValue(
			0, discountAmountCommerceMoney,
			_getDiscountPercentage(discountedAmount, amount, roundingMode),
			commerceDiscountLevels);
	}

	private CommerceDiscountValue _getCommerceDiscountValues(
			BigDecimal commercePrice, BigDecimal quantity,
			CommerceContext commerceContext,
			List<CommerceDiscount> commerceDiscounts)
		throws PortalException {

		BigDecimal[] commerceDiscountLevels = _getCommerceDiscountLevels(
			StringPool.BLANK, commercePrice, commerceContext,
			commerceDiscounts);

		CommerceDiscountApplicationStrategy
			commerceDiscountApplicationStrategy =
				_getCommerceDiscountApplicationStrategy();

		BigDecimal discountedAmount =
			commerceDiscountApplicationStrategy.applyCommerceDiscounts(
				commercePrice, commerceDiscountLevels);

		BigDecimal currentDiscountAmount = commercePrice.subtract(
			discountedAmount);

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		RoundingMode roundingMode = RoundingMode.valueOf(
			commerceCurrency.getRoundingMode());

		currentDiscountAmount = currentDiscountAmount.setScale(
			_SCALE, roundingMode);

		if (BigDecimalUtil.isZero(currentDiscountAmount)) {
			return null;
		}

		CommerceMoney discountAmountCommerceMoney =
			_commerceMoneyFactory.create(
				commerceCurrency, currentDiscountAmount.multiply(quantity));

		return new CommerceDiscountValue(
			0, discountAmountCommerceMoney,
			_getDiscountPercentage(
				discountedAmount, commercePrice, roundingMode),
			commerceDiscountLevels);
	}

	private BigDecimal _getDiscountPercentage(
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

	private boolean _isValidCouponCode(
			long commerceDiscountId, String couponCode,
			String discountCouponCode, CommerceContext commerceContext)
		throws PortalException {

		if ((Validator.isBlank(discountCouponCode) ||
			 StringUtil.equalsIgnoreCase(couponCode, discountCouponCode)) &&
			_commerceDiscountUsageEntryLocalService.
				validateDiscountLimitationUsage(
					CommerceUtil.getCommerceAccountId(commerceContext),
					commerceDiscountId)) {

			return true;
		}

		return false;
	}

	private boolean _isValidDiscount(
			CommerceContext commerceContext, CommerceDiscount commerceDiscount)
		throws PortalException {

		return _commerceDiscountValidatorHelper.isValid(
			commerceContext, commerceDiscount,
			CommerceDiscountConstants.VALIDATOR_TYPE_POST_QUALIFICATION);
	}

	private static final BigDecimal _ONE_HUNDRED = BigDecimal.valueOf(100);

	private static final int _SCALE = 10;

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDiscountCalculationV2Impl.class);

	@Reference
	private CommerceDiscountApplicationStrategyRegistry
		_commerceDiscountApplicationStrategyRegistry;

	@Reference
	private CommerceDiscountUsageEntryLocalService
		_commerceDiscountUsageEntryLocalService;

	@Reference
	private CommerceDiscountValidatorHelper _commerceDiscountValidatorHelper;

	@Reference
	private CommerceMoneyFactory _commerceMoneyFactory;

	@Reference
	private CommercePriceListDiscountRelLocalService
		_commercePriceListDiscountRelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

}