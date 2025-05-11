/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.price;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.discount.CommerceDiscountCalculation;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.discount.application.strategy.CommerceDiscountApplicationStrategy;
import com.liferay.commerce.discount.application.strategy.CommerceDiscountApplicationStrategyRegistry;
import com.liferay.commerce.internal.util.CommercePriceConverterUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceImpl;
import com.liferay.commerce.price.CommerceProductPriceRequest;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.discovery.CommercePriceListDiscovery;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryLocalService;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.pricing.modifier.CommercePriceModifierHelper;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommerceProductPriceCalculation.class)
public class CommerceProductPriceCalculationV2Impl
	extends BaseCommerceProductPriceCalculation {

	@Override
	public CommerceMoney getBasePrice(
			long cpInstanceId, CommerceCurrency commerceCurrency,
			String unitOfMeasureKey)
		throws PortalException {

		return commerceMoneyFactory.create(
			commerceCurrency,
			_getBasePrice(
				cpInstanceId, commerceCurrency,
				CommercePriceListConstants.TYPE_PRICE_LIST, unitOfMeasureKey));
	}

	@Override
	public CommerceMoney getBasePromoPrice(
			long cpInstanceId, CommerceCurrency commerceCurrency,
			String unitOfMeasureKey)
		throws PortalException {

		return commerceMoneyFactory.create(
			commerceCurrency,
			_getBasePrice(
				cpInstanceId, commerceCurrency,
				CommercePriceListConstants.TYPE_PROMOTION, unitOfMeasureKey));
	}

	@Override
	public CommerceProductPrice getCommerceProductPrice(
			CommerceProductPriceRequest commerceProductPriceRequest)
		throws PortalException {

		long cpInstanceId = commerceProductPriceRequest.getCpInstanceId();
		BigDecimal quantity = commerceProductPriceRequest.getQuantity();
		String unitOfMeasureKey =
			commerceProductPriceRequest.getUnitOfMeasureKey();

		CommerceContext commerceContext =
			commerceProductPriceRequest.getCommerceContext();

		long commercePriceListId = _getCommercePriceListId(
			cpInstanceId, commerceProductPriceRequest.getUnitOfMeasureKey(),
			commerceContext);

		CommercePriceEntry unitCommercePriceEntry = _getUnitPriceEntry(
			commercePriceListId, cpInstanceId, unitOfMeasureKey);

		BigDecimal unitOfMeasureIncrementalOrderQuantity = BigDecimal.ONE;

		if ((unitCommercePriceEntry != null) &&
			(unitCommercePriceEntry.getQuantity() != null)) {

			unitOfMeasureIncrementalOrderQuantity =
				unitCommercePriceEntry.getQuantity();
		}

		CommerceMoney unitPriceCommerceMoney = _getUnitPriceCommerceMoney(
			unitCommercePriceEntry, commercePriceListId, quantity,
			commerceContext);

		CommerceMoney pricingQuantityUnitPriceCommerceMoney =
			_getPricingQuantityUnitPriceCommerceMoney(
				unitCommercePriceEntry, commercePriceListId, commerceContext);

		boolean priceOnApplication =
			unitPriceCommerceMoney.isPriceOnApplication();

		BigDecimal finalPrice = unitPriceCommerceMoney.getPrice();

		long commercePromoPriceListId = _getCommercePromoPriceListId(
			cpInstanceId, commerceContext, unitOfMeasureKey);

		CommerceMoney promoPriceCommerceMoney = _getPromoPriceCommerceMoney(
			commercePromoPriceListId, cpInstanceId, quantity, unitOfMeasureKey,
			commerceContext);

		if (!promoPriceCommerceMoney.isEmpty() &&
			BigDecimalUtil.gt(
				promoPriceCommerceMoney.getPrice(), BigDecimal.ZERO) &&
			(BigDecimalUtil.lt(
				promoPriceCommerceMoney.getPrice(),
				unitPriceCommerceMoney.getPrice()) ||
			 unitPriceCommerceMoney.isPriceOnApplication())) {

			commercePriceListId = commercePromoPriceListId;
			finalPrice = promoPriceCommerceMoney.getPrice();
			priceOnApplication =
				priceOnApplication &&
				promoPriceCommerceMoney.isPriceOnApplication();
		}
		else {
			promoPriceCommerceMoney = commerceMoneyFactory.emptyCommerceMoney();
		}

		BigDecimal[] updatedPrices = getUpdatedPrices(
			unitPriceCommerceMoney, promoPriceCommerceMoney, finalPrice,
			commerceContext,
			commerceProductPriceRequest.getCommerceOptionValues());

		finalPrice = updatedPrices[2];

		CommerceDiscountValue commerceDiscountValue;

		BigDecimal finalPriceWithTaxAmount = null;

		if (commerceProductPriceRequest.isCalculateTax()) {
			finalPriceWithTaxAmount = getConvertedPrice(
				cpInstanceId, finalPrice, false, commerceContext);
		}

		boolean discountsTargetNetPrice = true;

		CommerceChannel commerceChannel =
			commerceChannelLocalService.fetchCommerceChannel(
				commerceContext.getCommerceChannelId());

		if (commerceChannel != null) {
			discountsTargetNetPrice =
				commerceChannel.isDiscountsTargetNetPrice();
		}

		BigDecimal baseQuantity = quantity.divide(
			unitOfMeasureIncrementalOrderQuantity, _SCALE,
			RoundingMode.HALF_UP);

		if (discountsTargetNetPrice) {
			commerceDiscountValue = _getCommerceDiscountValue(
				cpInstanceId, commercePriceListId, baseQuantity, finalPrice,
				unitOfMeasureKey, commerceContext);

			finalPrice = finalPrice.multiply(baseQuantity);

			if (commerceDiscountValue != null) {
				CommerceMoney discountAmountCommerceMoney =
					commerceDiscountValue.getDiscountAmount();

				finalPrice = finalPrice.subtract(
					discountAmountCommerceMoney.getPrice());
			}

			if (commerceProductPriceRequest.isCalculateTax()) {
				finalPriceWithTaxAmount = getConvertedPrice(
					cpInstanceId, finalPrice, false, commerceContext);
			}
		}
		else {
			commerceDiscountValue = _getCommerceDiscountValue(
				cpInstanceId, commercePriceListId, baseQuantity,
				finalPriceWithTaxAmount, unitOfMeasureKey, commerceContext);

			if (commerceProductPriceRequest.isCalculateTax()) {
				finalPriceWithTaxAmount = finalPriceWithTaxAmount.multiply(
					baseQuantity);

				if (commerceDiscountValue != null) {
					CommerceMoney discountAmountCommerceMoney =
						commerceDiscountValue.getDiscountAmount();

					finalPriceWithTaxAmount = finalPriceWithTaxAmount.subtract(
						discountAmountCommerceMoney.getPrice());
				}
			}

			finalPrice = getConvertedPrice(
				cpInstanceId, finalPriceWithTaxAmount, true, commerceContext);
		}

		// fill data

		CommerceProductPriceImpl commerceProductPriceImpl =
			_getCommerceProductPriceImpl();

		commerceProductPriceImpl.setCommercePriceListId(commercePriceListId);
		commerceProductPriceImpl.setPriceOnApplication(priceOnApplication);
		commerceProductPriceImpl.setPricingQuantityUnitPrice(
			pricingQuantityUnitPriceCommerceMoney);
		commerceProductPriceImpl.setQuantity(quantity);
		commerceProductPriceImpl.setUnitOfMeasureKey(unitOfMeasureKey);
		commerceProductPriceImpl.setUnitOfMeasureIncrementalOrderQuantity(
			unitOfMeasureIncrementalOrderQuantity);

		if (unitPriceCommerceMoney.isEmpty()) {
			if (unitPriceCommerceMoney.isPriceOnApplication()) {
				commerceProductPriceImpl.setUnitPrice(
					commerceMoneyFactory.priceOnApplicationCommerceMoney());
			}
			else {
				commerceProductPriceImpl.setUnitPrice(
					commerceMoneyFactory.emptyCommerceMoney());
			}
		}
		else {
			commerceProductPriceImpl.setUnitPrice(
				commerceMoneyFactory.create(
					commerceContext.getCommerceCurrency(), updatedPrices[0]));
		}

		if (promoPriceCommerceMoney.isEmpty()) {
			if (promoPriceCommerceMoney.isPriceOnApplication()) {
				commerceProductPriceImpl.setUnitPromoPrice(
					commerceMoneyFactory.priceOnApplicationCommerceMoney());
			}
			else {
				if (BigDecimalUtil.gt(updatedPrices[1], BigDecimal.ZERO)) {
					commerceProductPriceImpl.setUnitPromoPrice(
						commerceMoneyFactory.create(
							commerceContext.getCommerceCurrency(),
							updatedPrices[1]));
				}
				else {
					commerceProductPriceImpl.setUnitPromoPrice(
						commerceMoneyFactory.emptyCommerceMoney());
				}
			}
		}
		else {
			commerceProductPriceImpl.setUnitPromoPrice(
				commerceMoneyFactory.create(
					commerceContext.getCommerceCurrency(), updatedPrices[1]));
		}

		if (discountsTargetNetPrice) {
			commerceProductPriceImpl.setCommerceDiscountValue(
				commerceDiscountValue);
		}
		else {
			CommerceCurrency commerceCurrency =
				commerceContext.getCommerceCurrency();

			commerceProductPriceImpl.setCommerceDiscountValue(
				CommercePriceConverterUtil.getConvertedCommerceDiscountValue(
					commerceDiscountValue,
					updatedPrices[2].multiply(baseQuantity), finalPrice,
					commerceMoneyFactory,
					RoundingMode.valueOf(commerceCurrency.getRoundingMode())));
		}

		commerceProductPriceImpl.setFinalPrice(
			commerceMoneyFactory.create(
				commerceContext.getCommerceCurrency(), finalPrice));

		if (commerceProductPriceRequest.isCalculateTax() ||
			_hasGrossPricePriceList(
				cpInstanceId, unitOfMeasureKey, commerceContext)) {

			setCommerceProductPriceWithTaxAmount(
				cpInstanceId, finalPriceWithTaxAmount, commerceProductPriceImpl,
				commerceContext, commerceDiscountValue,
				discountsTargetNetPrice);
		}

		return commerceProductPriceImpl;
	}

	@Override
	public CommerceProductPrice getCommerceProductPrice(
			long cpInstanceId, BigDecimal quantity, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		CommerceProductPriceRequest commerceProductPriceRequest =
			new CommerceProductPriceRequest();

		commerceProductPriceRequest.setCalculateTax(
			_isTaxIncludedInPrice(commerceContext.getCommerceChannelId()));
		commerceProductPriceRequest.setCommerceContext(commerceContext);
		commerceProductPriceRequest.setCommerceOptionValues(
			Collections.emptyList());
		commerceProductPriceRequest.setCpInstanceId(cpInstanceId);
		commerceProductPriceRequest.setQuantity(quantity);
		commerceProductPriceRequest.setSecure(secure);
		commerceProductPriceRequest.setUnitOfMeasureKey(unitOfMeasureKey);

		return getCommerceProductPrice(commerceProductPriceRequest);
	}

	@Override
	public CommerceProductPrice getCommerceProductPrice(
			long cpInstanceId, BigDecimal quantity, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException {

		return getCommerceProductPrice(
			cpInstanceId, quantity, true, unitOfMeasureKey, commerceContext);
	}

	@Override
	public CommerceMoney getFinalPrice(
			long cpInstanceId, BigDecimal quantity, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		CommerceProductPrice commerceProductPrice = getCommerceProductPrice(
			cpInstanceId, quantity, secure, unitOfMeasureKey, commerceContext);

		if (commerceProductPrice == null) {
			return commerceMoneyFactory.emptyCommerceMoney();
		}

		if (commerceProductPrice.isPriceOnApplication()) {
			return commerceMoneyFactory.priceOnApplicationCommerceMoney();
		}

		return commerceProductPrice.getFinalPrice();
	}

	@Override
	public CommerceMoney getFinalPrice(
			long cpInstanceId, BigDecimal quantity, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException {

		return getFinalPrice(
			cpInstanceId, quantity, true, unitOfMeasureKey, commerceContext);
	}

	@Override
	public CommerceMoney getPromoPrice(
			long cpInstanceId, BigDecimal quantity,
			CommerceCurrency commerceCurrency, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		return _getPromoPriceCommerceMoney(
			_getCommercePromoPriceListId(
				cpInstanceId, commerceContext, unitOfMeasureKey),
			cpInstanceId, quantity, unitOfMeasureKey, commerceContext);
	}

	@Override
	public CommercePriceEntry getUnitCommercePriceEntry(
			CommerceContext commerceContext, long cpInstanceId,
			String unitOfMeasureKey)
		throws PortalException {

		CommercePriceList commercePriceList = _getCommercePriceList(
			cpInstanceId, commerceContext,
			CommercePriceListConstants.TYPE_PRICE_LIST, unitOfMeasureKey);

		long commercePriceListId = 0;

		if (commercePriceList != null) {
			commercePriceListId = commercePriceList.getCommercePriceListId();
		}

		CPInstance cpInstance = cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceListId, cpInstance.getCPInstanceUuid(),
				unitOfMeasureKey, true);

		if (commercePriceEntry != null) {
			return commercePriceEntry;
		}

		CommerceCatalog commerceCatalog = cpInstance.getCommerceCatalog();

		CommercePriceList basePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		if (basePriceList == null) {
			return null;
		}

		return _commercePriceEntryLocalService.fetchCommercePriceEntry(
			basePriceList.getCommercePriceListId(),
			cpInstance.getCPInstanceUuid(), unitOfMeasureKey, true);
	}

	@Override
	public CommerceMoney getUnitMaxPrice(
			long cpDefinitionId, BigDecimal quantity, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		CommerceMoney commerceMoney = commerceMoneyFactory.emptyCommerceMoney();

		List<CPInstance> cpInstances =
			cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionId, WorkflowConstants.STATUS_APPROVED,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (CPInstance cpInstance : cpInstances) {
			CommerceMoney cpInstanceCommerceMoney = getUnitPrice(
				cpInstance.getCPInstanceId(), quantity,
				commerceContext.getCommerceCurrency(), secure, unitOfMeasureKey,
				commerceContext);

			if (commerceMoney.isEmpty() ||
				BigDecimalUtil.lt(
					commerceMoney.getPrice(),
					cpInstanceCommerceMoney.getPrice())) {

				commerceMoney = cpInstanceCommerceMoney;
			}
		}

		return commerceMoney;
	}

	@Override
	public CommerceMoney getUnitMaxPrice(
			long cpDefinitionId, BigDecimal quantity, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException {

		return getUnitMaxPrice(
			cpDefinitionId, quantity, true, unitOfMeasureKey, commerceContext);
	}

	@Override
	public CommerceMoney getUnitMinPrice(
			long cpDefinitionId, BigDecimal quantity, boolean secure,
			CommerceContext commerceContext)
		throws PortalException {

		CommerceMoney commerceMoney = commerceMoneyFactory.emptyCommerceMoney();

		List<CPInstance> cpInstances =
			cpInstanceLocalService.getCPDefinitionInstances(
				cpDefinitionId, WorkflowConstants.STATUS_APPROVED,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (CPInstance cpInstance : cpInstances) {
			List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
				_cpInstanceUnitOfMeasureLocalService.
					getActiveCPInstanceUnitOfMeasures(
						cpInstance.getCPInstanceId());

			if (cpInstanceUnitOfMeasures.isEmpty()) {
				commerceMoney = _getCommerceMoney(
					commerceMoney,
					getUnitPrice(
						cpInstance.getCPInstanceId(), quantity,
						commerceContext.getCommerceCurrency(), secure,
						StringPool.BLANK, commerceContext));
			}
			else {
				for (CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure :
						cpInstanceUnitOfMeasures) {

					commerceMoney = _getCommerceMoney(
						commerceMoney,
						getUnitPrice(
							cpInstance.getCPInstanceId(), quantity,
							commerceContext.getCommerceCurrency(), secure,
							cpInstanceUnitOfMeasure.getKey(), commerceContext));
				}
			}
		}

		return commerceMoney;
	}

	@Override
	public CommerceMoney getUnitMinPrice(
			long cpDefinitionId, BigDecimal quantity,
			CommerceContext commerceContext)
		throws PortalException {

		return getUnitMinPrice(cpDefinitionId, quantity, true, commerceContext);
	}

	@Override
	public CommerceMoney getUnitPrice(
			long cpInstanceId, BigDecimal quantity,
			CommerceCurrency commerceCurrency, boolean secure,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		long commercePriceListId = _getCommercePriceListId(
			cpInstanceId, unitOfMeasureKey, commerceContext);

		return _getUnitPriceCommerceMoney(
			_getUnitPriceEntry(
				commercePriceListId, cpInstanceId, unitOfMeasureKey),
			commercePriceListId, quantity, commerceContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, CommercePriceListDiscovery.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(commercePriceListDiscovery, emitter) -> emitter.emit(
					commercePriceListDiscovery.
						getCommercePriceListDiscoveryKey())));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private CommerceDiscountValue _calculateCommerceDiscountValue(
			BigDecimal[] values, BigDecimal quantity, BigDecimal finalPrice,
			CommerceContext commerceContext)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		RoundingMode roundingMode = RoundingMode.valueOf(
			commerceCurrency.getRoundingMode());

		CommerceDiscountApplicationStrategy
			commerceDiscountApplicationStrategy =
				_getCommerceDiscountApplicationStrategy();

		BigDecimal discountedAmount =
			commerceDiscountApplicationStrategy.applyCommerceDiscounts(
				finalPrice, values);

		BigDecimal currentDiscountAmount = finalPrice.subtract(
			discountedAmount);

		currentDiscountAmount = currentDiscountAmount.setScale(
			_SCALE, roundingMode);

		CommerceMoney discountAmountCommerceMoney = commerceMoneyFactory.create(
			commerceCurrency, currentDiscountAmount.multiply(quantity));

		return new CommerceDiscountValue(
			0, discountAmountCommerceMoney,
			_getDiscountPercentage(discountedAmount, finalPrice, roundingMode),
			values);
	}

	private BigDecimal _getBasePrice(
			long cpInstanceId, CommerceCurrency commerceCurrency, String type,
			String unitOfMeasureKey)
		throws PortalException {

		CPInstance cpInstance = cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					cpInstance.getGroupId(), type);

		if (commercePriceList == null) {
			return BigDecimal.ZERO;
		}

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceList.getCommercePriceListId(),
				cpInstance.getCPInstanceUuid(), unitOfMeasureKey, false);

		if (commercePriceEntry == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal price = commercePriceEntry.getPrice();

		CommerceCurrency priceListCurrency =
			commercePriceList.getCommerceCurrency();

		if (priceListCurrency.getCommerceCurrencyId() !=
				commerceCurrency.getCommerceCurrencyId()) {

			price = price.divide(
				priceListCurrency.getRate(),
				RoundingMode.valueOf(priceListCurrency.getRoundingMode()));

			price = price.multiply(commerceCurrency.getRate());
		}

		return price;
	}

	private long _getBasePriceListId(CPInstance cpInstance)
		throws PortalException {

		CommerceCatalog commerceCatalog = cpInstance.getCommerceCatalog();

		CommercePriceList basePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId());

		if (basePriceList != null) {
			return basePriceList.getCommercePriceListId();
		}

		_log.error(
			"There is no base price list configured for the current catalog");

		return 0;
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

	private CommerceDiscountValue _getCommerceDiscountValue(
			long cpInstanceId, long commercePriceListId, BigDecimal quantity,
			BigDecimal finalPrice, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException {

		if ((finalPrice == null) ||
			BigDecimalUtil.lte(finalPrice, BigDecimal.ZERO)) {

			return null;
		}

		CPInstance cpInstance = cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceListId, cpInstance.getCPInstanceUuid(),
				unitOfMeasureKey, true);

		if (commercePriceEntry == null) {
			return _commerceDiscountCalculation.getProductCommerceDiscountValue(
				cpInstanceId, quantity, finalPrice, unitOfMeasureKey,
				commerceContext);
		}

		BigDecimal[] values = new BigDecimal[4];

		if (!commercePriceEntry.isHasTierPrice() &&
			!commercePriceEntry.isDiscountDiscovery()) {

			values[0] = commercePriceEntry.getDiscountLevel1();
			values[1] = commercePriceEntry.getDiscountLevel2();
			values[2] = commercePriceEntry.getDiscountLevel3();
			values[3] = commercePriceEntry.getDiscountLevel4();

			return _calculateCommerceDiscountValue(
				values, quantity, finalPrice, commerceContext);
		}

		if (!commercePriceEntry.isBulkPricing()) {
			return _commerceDiscountCalculation.getProductCommerceDiscountValue(
				cpInstanceId, quantity, finalPrice, unitOfMeasureKey,
				commerceContext);
		}

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryLocalService.
				fetchClosestCommerceTierPriceEntry(
					commercePriceEntry.getCommercePriceEntryId(), quantity);

		if ((commerceTierPriceEntry == null) ||
			commerceTierPriceEntry.isDiscountDiscovery()) {

			return _commerceDiscountCalculation.getProductCommerceDiscountValue(
				cpInstanceId, quantity, finalPrice, unitOfMeasureKey,
				commerceContext);
		}

		values[0] = commerceTierPriceEntry.getDiscountLevel1();
		values[1] = commerceTierPriceEntry.getDiscountLevel2();
		values[2] = commerceTierPriceEntry.getDiscountLevel3();
		values[3] = commerceTierPriceEntry.getDiscountLevel4();

		return _calculateCommerceDiscountValue(
			values, quantity, finalPrice, commerceContext);
	}

	private CommerceMoney _getCommerceMoney(
		CommerceMoney commerceMoney, CommerceMoney cpInstanceCommerceMoney) {

		if (commerceMoney.isEmpty()) {
			commerceMoney = cpInstanceCommerceMoney;
		}
		else if (!cpInstanceCommerceMoney.isPriceOnApplication() &&
				 BigDecimalUtil.gt(
					 commerceMoney.getPrice(),
					 cpInstanceCommerceMoney.getPrice())) {

			commerceMoney = cpInstanceCommerceMoney;
		}

		return commerceMoney;
	}

	private CommerceMoney _getCommerceMoney(
			long commercePriceListId, CommerceCurrency commerceCurrency,
			BigDecimal price)
		throws PortalException {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				commercePriceListId);

		CommerceCurrency priceListCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				commercePriceList.getCompanyId(),
				commercePriceList.getCommerceCurrencyCode());

		if (priceListCurrency.getCommerceCurrencyId() !=
				commerceCurrency.getCommerceCurrencyId()) {

			price = price.divide(
				priceListCurrency.getRate(),
				RoundingMode.valueOf(priceListCurrency.getRoundingMode()));

			price = price.multiply(commerceCurrency.getRate());
		}

		if (price != null) {
			return commerceMoneyFactory.create(commerceCurrency, price);
		}

		return commerceMoneyFactory.emptyCommerceMoney();
	}

	private BigDecimal _getCommercePrice(
			long commercePriceListId, CommercePriceEntry commercePriceEntry,
			BigDecimal quantity)
		throws PortalException {

		if (commercePriceEntry == null) {
			return null;
		}

		BigDecimal commercePrice = commercePriceEntry.getPrice();

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				commercePriceEntry.getCommercePriceListId());

		CommercePriceList modifierCommercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				commercePriceListId);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				commercePriceList.getCompanyId(),
				commercePriceList.getCommerceCurrencyCode());

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		if ((cpInstance != null) && !commercePriceEntry.isHasTierPrice()) {
			if ((commercePriceEntry.getCommercePriceListId() !=
					commercePriceListId) &&
				(commercePriceList.isNetPrice() ==
					modifierCommercePriceList.isNetPrice())) {

				commercePrice =
					_commercePriceModifierHelper.applyCommercePriceModifier(
						commercePriceListId, cpInstance.getCPDefinitionId(),
						commercePriceEntry.getPriceCommerceMoney(
							commerceCurrency.getCommerceCurrencyId()));
			}

			return commercePrice;
		}

		if (commercePriceEntry.isBulkPricing()) {
			CommerceTierPriceEntry commerceTierPriceEntry =
				_commerceTierPriceEntryLocalService.
					fetchClosestCommerceTierPriceEntry(
						commercePriceEntry.getCommercePriceEntryId(), quantity);

			if (commerceTierPriceEntry == null) {
				return commercePriceEntry.getPrice();
			}

			if ((cpInstance != null) &&
				(commercePriceEntry.getCommercePriceListId() !=
					commercePriceListId) &&
				(commercePriceList.isNetPrice() ==
					modifierCommercePriceList.isNetPrice())) {

				return _commercePriceModifierHelper.applyCommercePriceModifier(
					commercePriceListId, cpInstance.getCPDefinitionId(),
					commerceTierPriceEntry.getPriceCommerceMoney(
						commerceCurrency.getCommerceCurrencyId()));
			}

			return commerceTierPriceEntry.getPrice();
		}

		if ((commercePriceEntry.getCommercePriceListId() !=
				commercePriceListId) &&
			(commercePriceList.isNetPrice() ==
				modifierCommercePriceList.isNetPrice())) {

			return _commercePriceModifierHelper.applyCommercePriceModifier(
				commercePriceListId, cpInstance.getCPDefinitionId(),
				commerceMoneyFactory.create(commerceCurrency, commercePrice));
		}

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			_commerceTierPriceEntryLocalService.getCommerceTierPriceEntries(
				commercePriceEntry.getCommercePriceEntryId(), quantity);

		if (commerceTierPriceEntries.isEmpty()) {
			return commercePrice;
		}

		commercePrice = BigDecimal.ZERO;

		CommerceTierPriceEntry commerceTierPriceEntry1 =
			commerceTierPriceEntries.get(0);

		BigDecimal totalTierCounter = BigDecimal.ZERO;

		BigDecimal minQuantity1 = commerceTierPriceEntry1.getMinQuantity();

		BigDecimal tierCounter = minQuantity1.subtract(
			totalTierCounter.add(BigDecimal.ONE));

		BigDecimal currentPrice = commercePriceEntry.getPrice();

		currentPrice = currentPrice.multiply(tierCounter);

		commercePrice = commercePrice.add(currentPrice);

		totalTierCounter = totalTierCounter.add(tierCounter);

		for (int i = 0; i < (commerceTierPriceEntries.size() - 1); i++) {
			CommerceTierPriceEntry commerceTierPriceEntry2 =
				commerceTierPriceEntries.get(i);

			currentPrice = commerceTierPriceEntry2.getPrice();

			CommerceTierPriceEntry commerceTierPriceEntry3 =
				commerceTierPriceEntries.get(i + 1);

			BigDecimal minQuantity = commerceTierPriceEntry3.getMinQuantity();

			tierCounter = minQuantity.subtract(
				totalTierCounter.add(BigDecimal.ONE));

			currentPrice = currentPrice.multiply(tierCounter);

			commercePrice = commercePrice.add(currentPrice);

			totalTierCounter = totalTierCounter.add(tierCounter);
		}

		totalTierCounter = quantity.subtract(totalTierCounter);

		CommerceTierPriceEntry commerceTierPriceEntry2 =
			commerceTierPriceEntries.get(commerceTierPriceEntries.size() - 1);

		currentPrice = commerceTierPriceEntry2.getPrice();

		currentPrice = currentPrice.multiply(totalTierCounter);

		commercePrice = commercePrice.add(currentPrice);

		RoundingMode roundingMode = RoundingMode.valueOf(
			commerceCurrency.getRoundingMode());

		return commercePrice.divide(quantity, _SCALE, roundingMode);
	}

	private BigDecimal _getCommercePrice(
			long cpInstanceId, long commercePriceListId,
			CommerceMoney unitPriceCommerceMoney)
		throws PortalException {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCommercePriceList(
				commercePriceListId);

		if (commercePriceList == null) {
			return null;
		}

		CPInstance cpInstance = cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		return _commercePriceModifierHelper.applyCommercePriceModifier(
			commercePriceListId, cpInstance.getCPDefinitionId(),
			unitPriceCommerceMoney);
	}

	private CommercePriceList _getCommercePriceList(
			long cpInstanceId, CommerceContext commerceContext, String type,
			String unitOfMeasureKey)
		throws PortalException {

		long commerceAccountId = CommerceUtil.getCommerceAccountId(
			commerceContext);

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelLocalService.
				fetchCommerceChannelAccountEntryRel(
					commerceAccountId, commerceContext.getCommerceChannelId(),
					CommerceChannelAccountEntryRelConstants.TYPE_PRICE_LIST);

		if ((commerceChannelAccountEntryRel != null) &&
			commerceChannelAccountEntryRel.isOverrideEligibility()) {

			return _commercePriceListLocalService.getCommercePriceList(
				commerceChannelAccountEntryRel.getClassPK());
		}

		CommercePriceListDiscovery commercePriceListDiscovery =
			_getCommercePriceListDiscovery(type);

		if (commercePriceListDiscovery == null) {
			return null;
		}

		CPInstance cpInstance = cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		long commerceOrderTypeId = 0;

		if (commerceOrder != null) {
			commerceOrderTypeId = commerceOrder.getCommerceOrderTypeId();
		}

		List<String> currencyCodes = new ArrayList<>();

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		currencyCodes.add(commerceCurrency.getCode());

		CommercePriceList commercePriceList =
			commercePriceListDiscovery.getCommercePriceList(
				cpInstance.getGroupId(), commerceAccountId,
				commerceContext.getCommerceChannelId(), commerceOrderTypeId,
				cpInstance.getCPInstanceUuid(), commerceCurrency.getCode(),
				type, unitOfMeasureKey);

		if (commercePriceList == null) {
			AccountEntry accountEntry = commerceContext.getAccountEntry();

			if (accountEntry != null) {
				CommerceChannelAccountEntryRel
					currencyCommerceChannelAccountEntryRel =
						_commerceChannelAccountEntryRelLocalService.
							fetchCommerceChannelAccountEntryRel(
								accountEntry.getAccountEntryId(),
								commerceContext.getCommerceChannelId(),
								CommerceChannelAccountEntryRelConstants.
									TYPE_CURRENCY);

				if (currencyCommerceChannelAccountEntryRel != null) {
					commerceCurrency =
						_commerceCurrencyLocalService.fetchCommerceCurrency(
							currencyCommerceChannelAccountEntryRel.
								getClassPK());

					if ((commerceCurrency != null) &&
						commerceCurrency.isActive() &&
						!currencyCodes.contains(commerceCurrency.getCode())) {

						commercePriceList =
							commercePriceListDiscovery.getCommercePriceList(
								cpInstance.getGroupId(), commerceAccountId,
								commerceContext.getCommerceChannelId(),
								commerceOrderTypeId,
								cpInstance.getCPInstanceUuid(),
								commerceCurrency.getCode(), type,
								unitOfMeasureKey);

						currencyCodes.add(commerceCurrency.getCode());
					}
				}
			}
		}

		if (commercePriceList == null) {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannel(
					commerceContext.getCommerceChannelId());

			if (commerceChannel != null) {
				commerceCurrency =
					_commerceCurrencyLocalService.fetchCommerceCurrency(
						commerceChannel.getCompanyId(),
						commerceChannel.getCommerceCurrencyCode());

				if ((commerceCurrency != null) && commerceCurrency.isActive() &&
					!currencyCodes.contains(commerceCurrency.getCode())) {

					commercePriceList =
						commercePriceListDiscovery.getCommercePriceList(
							cpInstance.getGroupId(), commerceAccountId,
							commerceContext.getCommerceChannelId(),
							commerceOrderTypeId, cpInstance.getCPInstanceUuid(),
							commerceCurrency.getCode(), type, unitOfMeasureKey);

					currencyCodes.add(commerceCurrency.getCode());
				}
			}
		}

		if (commercePriceList == null) {
			commerceCurrency =
				_commerceCurrencyLocalService.fetchPrimaryCommerceCurrency(
					cpInstance.getCompanyId());

			if ((commerceCurrency != null) && commerceCurrency.isActive() &&
				!currencyCodes.contains(commerceCurrency.getCode())) {

				commercePriceList =
					commercePriceListDiscovery.getCommercePriceList(
						cpInstance.getGroupId(), commerceAccountId,
						commerceContext.getCommerceChannelId(),
						commerceOrderTypeId, cpInstance.getCPInstanceUuid(),
						commerceCurrency.getCode(), type, unitOfMeasureKey);
			}
		}

		if (commercePriceList == null) {
			commercePriceList = commercePriceListDiscovery.getCommercePriceList(
				cpInstance.getGroupId(), commerceAccountId,
				commerceContext.getCommerceChannelId(), commerceOrderTypeId,
				cpInstance.getCPInstanceUuid(), null, type, unitOfMeasureKey);
		}

		return commercePriceList;
	}

	private CommercePriceListDiscovery _getCommercePriceListDiscovery(
			String type)
		throws PortalException {

		CommercePricingConfiguration commercePricingConfiguration =
			_configurationProvider.getSystemConfiguration(
				CommercePricingConfiguration.class);

		String discoveryMethod = CommercePricingConstants.ORDER_BY_HIERARCHY;

		if (type.equals(CommercePriceListConstants.TYPE_PRICE_LIST)) {
			discoveryMethod =
				commercePricingConfiguration.commercePriceListDiscovery();
		}
		else if (type.equals(CommercePriceListConstants.TYPE_PROMOTION)) {
			discoveryMethod =
				commercePricingConfiguration.commercePromotionDiscovery();
		}

		CommercePriceListDiscovery commercePriceListDiscovery =
			_serviceTrackerMap.getService(discoveryMethod);

		if (commercePriceListDiscovery == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No commerce price list discovery specified for " +
						discoveryMethod);
			}
		}

		return commercePriceListDiscovery;
	}

	private long _getCommercePriceListId(
			long cpInstanceId, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException {

		CommercePriceList commercePriceList = _getCommercePriceList(
			cpInstanceId, commerceContext,
			CommercePriceListConstants.TYPE_PRICE_LIST, unitOfMeasureKey);

		long commercePriceListId = 0;

		if (commercePriceList != null) {
			commercePriceListId = commercePriceList.getCommercePriceListId();
		}

		CPInstance cpInstance = cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceListId, cpInstance.getCPInstanceUuid(),
				unitOfMeasureKey, true);

		if (commercePriceEntry != null) {
			return commercePriceEntry.getCommercePriceListId();
		}

		boolean hasCommercePriceModifiers =
			_commercePriceModifierHelper.hasCommercePriceModifiers(
				commercePriceListId, cpInstance.getCPDefinitionId());

		if (hasCommercePriceModifiers) {
			return commercePriceListId;
		}

		return _getBasePriceListId(cpInstance);
	}

	private CommerceProductPriceImpl _getCommerceProductPriceImpl() {
		CommerceProductPriceImpl commerceProductPriceImpl =
			new CommerceProductPriceImpl();

		commerceProductPriceImpl.setFinalPrice(
			commerceMoneyFactory.emptyCommerceMoney());
		commerceProductPriceImpl.setFinalPriceWithTaxAmount(
			commerceMoneyFactory.emptyCommerceMoney());
		commerceProductPriceImpl.setPriceOnApplication(false);
		commerceProductPriceImpl.setUnitPrice(
			commerceMoneyFactory.emptyCommerceMoney());
		commerceProductPriceImpl.setUnitPriceWithTaxAmount(
			commerceMoneyFactory.emptyCommerceMoney());
		commerceProductPriceImpl.setUnitPromoPrice(
			commerceMoneyFactory.emptyCommerceMoney());
		commerceProductPriceImpl.setUnitPromoPriceWithTaxAmount(
			commerceMoneyFactory.emptyCommerceMoney());

		return commerceProductPriceImpl;
	}

	private long _getCommercePromoPriceListId(
			long cpInstanceId, CommerceContext commerceContext,
			String unitOfMeasureKey)
		throws PortalException {

		CommercePriceList commercePriceList = _getCommercePriceList(
			cpInstanceId, commerceContext,
			CommercePriceListConstants.TYPE_PROMOTION, unitOfMeasureKey);

		if (commercePriceList != null) {
			return commercePriceList.getCommercePriceListId();
		}

		return 0;
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

	private CommerceMoney _getPricingQuantityUnitPriceCommerceMoney(
			CommercePriceEntry commercePriceEntry, long commercePriceListId,
			CommerceContext commerceContext)
		throws PortalException {

		if (commercePriceEntry == null) {
			return commerceMoneyFactory.create(
				commerceContext.getCommerceCurrency(), BigDecimal.ZERO);
		}

		BigDecimal pricingQuantity = commercePriceEntry.getPricingQuantity();

		if ((pricingQuantity == null) ||
			BigDecimalUtil.lte(pricingQuantity, BigDecimal.ZERO)) {

			return commerceMoneyFactory.emptyCommerceMoney();
		}

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		BigDecimal pricingQuantityUnitPrice = pricingQuantity.multiply(
			commercePriceEntry.getPrice()
		).divide(
			commercePriceEntry.getQuantity(),
			commerceCurrency.getMaxFractionDigits(),
			RoundingMode.valueOf(commerceCurrency.getRoundingMode())
		);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				commercePriceListId);

		if (!commercePriceList.isNetPrice()) {
			CPInstance cpInstance =
				_cpInstanceLocalService.fetchCProductInstance(
					commercePriceEntry.getCProductId(),
					commercePriceEntry.getCPInstanceUuid());

			if (cpInstance != null) {
				pricingQuantityUnitPrice = getConvertedPrice(
					cpInstance.getCPInstanceId(), pricingQuantityUnitPrice,
					true, commerceContext);
			}
		}

		return _getCommerceMoney(
			commercePriceEntry.getCommercePriceListId(),
			commerceContext.getCommerceCurrency(), pricingQuantityUnitPrice);
	}

	private CommerceMoney _getPromoPriceCommerceMoney(
			long commercePriceListId, long cpInstanceId, BigDecimal quantity,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws PortalException {

		if (commercePriceListId <= 0) {
			return commerceMoneyFactory.emptyCommerceMoney();
		}

		CPInstance cpInstance = cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				commercePriceListId);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceListId, cpInstance.getCPInstanceUuid(),
				unitOfMeasureKey, true);

		if (commercePriceEntry != null) {
			if (commercePriceEntry.isPriceOnApplication()) {
				return commerceMoneyFactory.priceOnApplicationCommerceMoney();
			}

			BigDecimal promoPrice = _getCommercePrice(
				commercePriceListId, commercePriceEntry, quantity);

			if (!commercePriceList.isNetPrice()) {
				promoPrice = getConvertedPrice(
					cpInstance.getCPInstanceId(), promoPrice, true,
					commerceContext);
			}

			return _getCommerceMoney(
				commercePriceListId, commerceContext.getCommerceCurrency(),
				promoPrice);
		}

		if (!_commercePriceModifierHelper.hasCommercePriceModifiers(
				commercePriceListId, cpInstance.getCPDefinitionId())) {

			return commerceMoneyFactory.emptyCommerceMoney();
		}

		CommerceMoney unitPriceCommerceMoney = getUnitPrice(
			cpInstanceId, quantity, commerceContext.getCommerceCurrency(),
			false, unitOfMeasureKey, commerceContext);

		if (unitPriceCommerceMoney.isPriceOnApplication()) {
			return commerceMoneyFactory.priceOnApplicationCommerceMoney();
		}

		BigDecimal promoPrice = _getCommercePrice(
			cpInstanceId, commercePriceListId, unitPriceCommerceMoney);

		if (!commercePriceList.isNetPrice()) {
			promoPrice = getConvertedPrice(
				cpInstance.getCPInstanceId(), promoPrice, true,
				commerceContext);
		}

		return _getCommerceMoney(
			commercePriceListId, commerceContext.getCommerceCurrency(),
			promoPrice);
	}

	private CommerceMoney _getUnitPriceCommerceMoney(
			CommercePriceEntry commercePriceEntry, long commercePriceListId,
			BigDecimal quantity, CommerceContext commerceContext)
		throws PortalException {

		if (commercePriceEntry == null) {
			return commerceMoneyFactory.create(
				commerceContext.getCommerceCurrency(), BigDecimal.ZERO);
		}

		if (commercePriceEntry.isPriceOnApplication()) {
			return commerceMoneyFactory.priceOnApplicationCommerceMoney();
		}

		BigDecimal unitPrice = _getCommercePrice(
			commercePriceListId, commercePriceEntry, quantity);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				commercePriceListId);

		if (!commercePriceList.isNetPrice()) {
			CPInstance cpInstance =
				_cpInstanceLocalService.fetchCProductInstance(
					commercePriceEntry.getCProductId(),
					commercePriceEntry.getCPInstanceUuid());

			if (cpInstance != null) {
				unitPrice = getConvertedPrice(
					cpInstance.getCPInstanceId(), unitPrice, true,
					commerceContext);
			}
		}

		return _getCommerceMoney(
			commercePriceEntry.getCommercePriceListId(),
			commerceContext.getCommerceCurrency(), unitPrice);
	}

	private CommercePriceEntry _getUnitPriceEntry(
			long commercePriceListId, long cpInstanceId,
			String unitOfMeasureKey)
		throws PortalException {

		if (commercePriceListId == 0) {
			return null;
		}

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				commercePriceListId);

		CPInstance cpInstance = cpInstanceLocalService.getCPInstance(
			cpInstanceId);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceList.getCommercePriceListId(),
				cpInstance.getCPInstanceUuid(), unitOfMeasureKey, true);

		if (commercePriceEntry != null) {
			return commercePriceEntry;
		}

		return _commercePriceEntryLocalService.fetchCommercePriceEntry(
			_getBasePriceListId(cpInstance), cpInstance.getCPInstanceUuid(),
			unitOfMeasureKey, false);
	}

	private boolean _hasGrossPricePriceList(
			long cpInstanceId, String unitOfMeasureKey,
			CommerceContext commerceContext)
		throws PortalException {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCommercePriceList(
				_getCommercePriceListId(
					cpInstanceId, unitOfMeasureKey, commerceContext));

		if ((commercePriceList != null) && !commercePriceList.isNetPrice()) {
			return true;
		}

		CommercePriceList commercePromoPriceList =
			_commercePriceListLocalService.fetchCommercePriceList(
				_getCommercePromoPriceListId(
					cpInstanceId, commerceContext, unitOfMeasureKey));

		if ((commercePromoPriceList != null) &&
			!commercePromoPriceList.isNetPrice()) {

			return true;
		}

		return false;
	}

	private boolean _isTaxIncludedInPrice(long commerceChannelId)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(commerceChannelId);

		String priceDisplayType = commerceChannel.getPriceDisplayType();

		return priceDisplayType.equals(
			CommercePricingConstants.TAX_INCLUDED_IN_PRICE);
	}

	private static final BigDecimal _ONE_HUNDRED = BigDecimal.valueOf(100);

	private static final int _SCALE = 10;

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceProductPriceCalculationV2Impl.class);

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceDiscountApplicationStrategyRegistry
		_commerceDiscountApplicationStrategyRegistry;

	@Reference
	private CommerceDiscountCalculation _commerceDiscountCalculation;

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference
	private CommercePriceModifierHelper _commercePriceModifierHelper;

	@Reference
	private CommerceTierPriceEntryLocalService
		_commerceTierPriceEntryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	private ServiceTrackerMap<String, CommercePriceListDiscovery>
		_serviceTrackerMap;

}