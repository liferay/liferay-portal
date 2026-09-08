/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommercePriceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.inventory.constants.CommerceInventoryAvailabilityConstants;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceRequest;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryLocalService;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Availability;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Price;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ReplacementSku;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.TierPrice;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.converter.SkuDTOConverterContext;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.delivery.catalog.internal.util.v1_0.SkuOptionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.TransformUtil;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {"default=true", "dto.class.name=CPSku"},
	service = DTOConverter.class
)
public class SkuDTOConverter implements DTOConverter<CPInstance, Sku> {

	@Override
	public String getContentType() {
		return Product.class.getSimpleName();
	}

	@Override
	public Sku toDTO(DTOConverterContext dtoConverterContext) throws Exception {
		SkuDTOConverterContext skuDTOConverterContext =
			(SkuDTOConverterContext)dtoConverterContext;

		CommerceContext commerceContext =
			skuDTOConverterContext.getCommerceContext();

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			(Long)skuDTOConverterContext.getId());

		List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.
				getActiveCPInstanceUnitOfMeasures(cpInstance.getCPInstanceId());

		JSONArray jsonArray = null;

		JSONArray skuOptionsJSONArray =
			skuDTOConverterContext.getSkuOptionsJSONArray();

		if (JSONUtil.isEmpty(skuOptionsJSONArray)) {
			jsonArray = CPJSONUtil.toJSONArray(
				_cpDefinitionOptionRelLocalService.
					getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
						cpInstance.getCPInstanceId()));
		}
		else {
			jsonArray = skuOptionsJSONArray;
		}

		SkuOption[] skuOptionsArray = _getSkuOptions(
			_cpInstanceHelper.getCPDefinitionOptionValueRelsMap(
				cpInstance.getCPDefinitionId(), jsonArray.toString()),
			skuDTOConverterContext.getLocale());

		CPInstance replacementCPInstance = _checkReplacementCPInstance(
			_cpInstanceLocalService.fetchCProductInstance(
				cpInstance.getReplacementCProductId(),
				cpInstance.getReplacementCPInstanceUuid()));

		return new Sku() {
			{
				setAvailability(
					() -> {
						long accountEntryId = 0;

						if (accountEntry != null) {
							accountEntryId = accountEntry.getAccountEntryId();
						}

						return _getAvailability(
							accountEntryId, cpInstance.getGroupId(),
							commerceContext.getCommerceChannelGroupId(),
							skuDTOConverterContext.getCompanyId(),
							commerceContext.getCPConfigurationListId(
								cpInstance.getGroupId()),
							cpInstance, cpInstance.getSku(),
							skuDTOConverterContext.getUnitOfMeasureKey(),
							skuDTOConverterContext.getLocale());
					});
				setBackOrderAllowed(
					() -> {
						CPDefinitionInventory cpDefinitionInventory =
							_cpDefinitionInventoryLocalService.
								fetchCPDefinitionInventoryByCPDefinitionId(
									cpInstance.getCPDefinitionId());

						CPDefinitionInventoryEngine
							cpDefinitionInventoryEngine =
								_cpDefinitionInventoryEngineRegistry.
									getCPDefinitionInventoryEngine(
										cpDefinitionInventory);

						return cpDefinitionInventoryEngine.isBackOrderAllowed(
							commerceContext.getCPConfigurationListId(
								cpInstance.getGroupId()),
							cpInstance);
					});
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPInstance.class.getName(),
						cpInstance.getCPInstanceId(), cpInstance.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDepth(cpInstance::getDepth);
				setDiscontinued(cpInstance::isDiscontinued);
				setDiscontinuedDate(cpInstance::getDiscontinuedDate);
				setDisplayDate(cpInstance::getDisplayDate);
				setDisplayDiscountLevels(
					() -> {
						CommercePriceConfiguration commercePriceConfiguration =
							_configurationProvider.getConfiguration(
								CommercePriceConfiguration.class,
								new SystemSettingsLocator(
									CommerceConstants.
										SERVICE_NAME_COMMERCE_PRICE));

						return commercePriceConfiguration.
							displayDiscountLevels();
					});
				setExpirationDate(cpInstance::getExpirationDate);
				setExternalReferenceCode(cpInstance::getExternalReferenceCode);
				setGtin(cpInstance::getGtin);
				setHeight(cpInstance::getHeight);
				setId(cpInstance::getCPInstanceId);
				setIncomingQuantityLabel(
					() -> _cpContentHelper.getIncomingQuantityLabel(
						skuDTOConverterContext.getCompanyId(),
						skuDTOConverterContext.getLocale(), cpInstance.getSku(),
						skuDTOConverterContext.getUnitOfMeasureKey(),
						skuDTOConverterContext.getUser()));
				setManufacturerPartNumber(
					cpInstance::getManufacturerPartNumber);
				setPrice(
					() -> _getPrice(
						skuDTOConverterContext.getCommerceContext(), cpInstance,
						_cpInstanceUnitOfMeasureLocalService.
							fetchCPInstanceUnitOfMeasure(
								cpInstance.getCPInstanceId(),
								skuDTOConverterContext.getUnitOfMeasureKey()),
						JSONUtil.toString(
							JSONUtil.toJSONArray(
								skuOptionsArray,
								skuOption -> _jsonFactory.createJSONObject(
									skuOption.toString()))),
						skuDTOConverterContext.getLocale(),
						skuDTOConverterContext.getQuantity()));
				setProductConfiguration(
					() -> _productConfigurationDTOConverter.toDTO(
						new ProductConfigurationDTOConverterContext(
							commerceContext, cpInstance.getCPDefinitionId(),
							skuDTOConverterContext.getLocale())));
				setProductId(
					() -> {
						CPDefinition cpDefinition =
							cpInstance.getCPDefinition();

						return cpDefinition.getCProductId();
					});
				setPublished(cpInstance::isPublished);
				setPurchasable(cpInstance::isPurchasable);
				setReplacementSku(
					() -> _toReplacementSku(
						commerceContext, replacementCPInstance,
						skuDTOConverterContext));
				setReplacementSkuExternalReferenceCode(
					() -> {
						if (replacementCPInstance == null) {
							return null;
						}

						return replacementCPInstance.getExternalReferenceCode();
					});
				setReplacementSkuId(
					() -> {
						if (replacementCPInstance != null) {
							return replacementCPInstance.getCPInstanceId();
						}

						return null;
					});
				setSku(cpInstance::getSku);
				setSkuOptions(() -> skuOptionsArray);
				setSkuUnitOfMeasures(
					() -> _toSkuUnitOfMeasures(
						commerceContext, cpInstanceUnitOfMeasures,
						skuDTOConverterContext.getLocale()));
				setTierPrices(
					() -> {
						if (!cpInstanceUnitOfMeasures.isEmpty()) {
							return null;
						}

						CommercePriceEntry commercePriceEntry =
							_commerceProductPriceCalculation.
								getUnitCommercePriceEntry(
									commerceContext,
									cpInstance.getCPInstanceId(),
									StringPool.BLANK);

						if (commercePriceEntry == null) {
							return null;
						}

						return _toTierPrices(
							commerceContext,
							_commerceTierPriceEntryLocalService.
								getCommerceTierPriceEntries(
									commercePriceEntry.
										getCommercePriceEntryId(),
									WorkflowConstants.STATUS_APPROVED),
							skuDTOConverterContext.getLocale());
					});
				setWeight(cpInstance::getWeight);
				setWidth(cpInstance::getWidth);
			}
		};
	}

	private CPInstance _checkReplacementCPInstance(CPInstance cpInstance) {
		if ((cpInstance != null) && cpInstance.isDiscontinued()) {
			CPInstance replacedCPInstance =
				_cpInstanceLocalService.fetchCPInstance(
					cpInstance.getReplacementCProductId(),
					cpInstance.getReplacementCPInstanceUuid());

			return _checkReplacementCPInstance(replacedCPInstance);
		}

		return cpInstance;
	}

	private Availability _getAvailability(
			long accountEntryId, long commerceCatalogGroupId,
			long commerceChannelGroupId, long companyId,
			long cpConfigurationListId, CPInstance cpInstance, String sku,
			String unitOfMeasureKey, Locale locale)
		throws Exception {

		Availability availability = new Availability();

		if (_cpDefinitionInventoryEngine.isDisplayAvailability(
				cpConfigurationListId, cpInstance)) {

			if (Objects.equals(
					_commerceInventoryEngine.getAvailabilityStatus(
						cpInstance.getCompanyId(), accountEntryId,
						commerceCatalogGroupId, commerceChannelGroupId,
						_cpDefinitionInventoryEngine.getMinStockQuantity(
							cpConfigurationListId, cpInstance),
						cpInstance.getSku(), unitOfMeasureKey),
					CommerceInventoryAvailabilityConstants.AVAILABLE)) {

				availability.setLabel_i18n(
					() -> _language.get(locale, "available"));
				availability.setLabel(() -> "available");
			}
			else {
				availability.setLabel_i18n(
					() -> _language.get(locale, "unavailable"));
				availability.setLabel(() -> "unavailable");
			}
		}

		if (_cpDefinitionInventoryEngine.isDisplayStockQuantity(
				cpConfigurationListId, cpInstance)) {

			availability.setStockQuantity(
				() -> _commerceInventoryEngine.getStockQuantity(
					companyId, accountEntryId, commerceCatalogGroupId,
					commerceChannelGroupId, sku, unitOfMeasureKey));
		}

		return availability;
	}

	private CommerceProductPriceRequest _getCommerceProductPriceRequest(
			CommerceContext commerceContext,
			List<CommerceOptionValue> commerceOptionValues, long cpInstanceId,
			BigDecimal quantity, String unitOfMeasureKey)
		throws Exception {

		return new CommerceProductPriceRequest() {
			{
				setCalculateTax(
					_isTaxIncludedInPrice(
						commerceContext.getCommerceChannelId()));
				setCommerceContext(commerceContext);
				setCommerceOptionValues(commerceOptionValues);
				setCpInstanceId(cpInstanceId);
				setQuantity(quantity);
				setSecure(true);
				setUnitOfMeasureKey(unitOfMeasureKey);
			}
		};
	}

	private String[] _getFormattedDiscountPercentages(
			BigDecimal[] discountPercentages, Locale locale)
		throws Exception {

		List<String> formattedDiscountPercentages = new ArrayList<>();

		for (BigDecimal percentage : discountPercentages) {
			formattedDiscountPercentages.add(
				_commercePriceFormatter.format(percentage, locale));
		}

		return formattedDiscountPercentages.toArray(new String[0]);
	}

	private Price _getPrice(
			CommerceContext commerceContext, CPInstance cpInstance,
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure,
			String formFieldValues, Locale locale, BigDecimal quantity)
		throws Exception {

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				_getCommerceProductPriceRequest(
					commerceContext,
					_commerceOptionValueHelper.
						getCPDefinitionCommerceOptionValues(
							cpInstance.getCPDefinitionId(), formFieldValues),
					cpInstance.getCPInstanceId(), quantity,
					(cpInstanceUnitOfMeasure == null) ? StringPool.BLANK :
						cpInstanceUnitOfMeasure.getKey()));

		if (commerceProductPrice == null) {
			return new Price();
		}

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();
		CommerceMoney pricingQuantityUnitPriceCommerceMoney =
			commerceProductPrice.getPricingQuantityUnitPrice();
		CommerceMoney unitPriceCommerceMoney =
			commerceProductPrice.getUnitPrice();

		CommerceMoney unitPromoPriceCommerceMoney =
			commerceProductPrice.getUnitPromoPrice();

		BigDecimal unitPrice = unitPriceCommerceMoney.getPrice();

		Price price = new Price() {
			{
				setCurrency(() -> commerceCurrency.getName(locale));
				setPrice(unitPrice::doubleValue);
				setPriceFormatted(() -> unitPriceCommerceMoney.format(locale));
				setPriceOnApplication(
					commerceProductPrice::isPriceOnApplication);
				setPricingQuantityPrice(
					() -> {
						if ((cpInstanceUnitOfMeasure == null) ||
							(pricingQuantityUnitPriceCommerceMoney == null)) {

							return null;
						}

						BigDecimal pricingQuantityUnitPrice =
							pricingQuantityUnitPriceCommerceMoney.getPrice();

						if (pricingQuantityUnitPrice == null) {
							return null;
						}

						return pricingQuantityUnitPrice.doubleValue();
					});
				setPricingQuantityPriceFormatted(
					() -> {
						if ((cpInstanceUnitOfMeasure == null) ||
							(pricingQuantityUnitPriceCommerceMoney == null)) {

							return null;
						}

						BigDecimal pricingQuantity = BigDecimalUtil.get(
							cpInstanceUnitOfMeasure.getPricingQuantity(),
							BigDecimal.ZERO);

						if (BigDecimalUtil.lte(
								pricingQuantity, BigDecimal.ZERO)) {

							pricingQuantity = BigDecimal.ONE;
						}

						return pricingQuantityUnitPriceCommerceMoney.format(
							locale, pricingQuantity,
							cpInstanceUnitOfMeasure.getName(locale));
					});
			}
		};

		BigDecimal unitPromoPrice = unitPromoPriceCommerceMoney.getPrice();

		if ((unitPromoPrice != null) &&
			(unitPromoPrice.compareTo(BigDecimal.ZERO) > 0) &&
			((unitPromoPrice.compareTo(unitPrice) < 0) ||
			 unitPriceCommerceMoney.isPriceOnApplication())) {

			price.setPromoPrice(unitPromoPrice::doubleValue);
			price.setPromoPriceFormatted(
				() -> unitPromoPriceCommerceMoney.format(locale));
		}

		CommerceDiscountValue discountValue =
			commerceProductPrice.getDiscountValue();

		if (discountValue != null) {
			CommerceMoney discountAmountCommerceMoney =
				discountValue.getDiscountAmount();

			price.setDiscount(() -> discountAmountCommerceMoney.format(locale));

			price.setDiscountPercentage(
				() -> _commercePriceFormatter.format(
					discountValue.getDiscountPercentage(), locale));
			price.setDiscountPercentages(
				() -> _getFormattedDiscountPercentages(
					discountValue.getPercentages(), locale));
		}

		CommerceMoney finalPriceCommerceMoney =
			commerceProductPrice.getFinalPrice();

		BigDecimal finalPrice = finalPriceCommerceMoney.getPrice();

		if (finalPrice != null) {
			price.setFinalPrice(() -> finalPriceCommerceMoney.format(locale));
		}

		return price;
	}

	private SkuOption[] _getSkuOptions(
			Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
				cpDefinitionOptionValueRelsMap,
			Locale locale)
		throws Exception {

		if (MapUtil.isEmpty(cpDefinitionOptionValueRelsMap)) {
			return null;
		}

		return SkuOptionUtil.getSkuOptions(
			cpDefinitionOptionValueRelsMap, _cpInstanceLocalService, locale);
	}

	private boolean _isTaxIncludedInPrice(long commerceChannelId)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(commerceChannelId);

		String priceDisplayType = commerceChannel.getPriceDisplayType();

		return priceDisplayType.equals(
			CommercePricingConstants.TAX_INCLUDED_IN_PRICE);
	}

	private ReplacementSku _toReplacementSku(
			CommerceContext commerceContext, CPInstance replacementCPInstance,
			SkuDTOConverterContext skuDTOConverterContext)
		throws Exception {

		if (replacementCPInstance == null) {
			return null;
		}

		CPDefinition replacementCPDefinition =
			replacementCPInstance.getCPDefinition();

		JSONArray jsonArray = CPJSONUtil.toJSONArray(
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
					replacementCPInstance.getCPInstanceId()));

		SkuOption[] replacementSkuSkuOptions = _getSkuOptions(
			_cpInstanceHelper.getCPDefinitionOptionValueRelsMap(
				replacementCPInstance.getCPDefinitionId(),
				jsonArray.toString()),
			skuDTOConverterContext.getLocale());

		List<CPInstanceUnitOfMeasure> replacementUnitOfMeasures =
			_cpInstanceUnitOfMeasureLocalService.
				getActiveCPInstanceUnitOfMeasures(
					replacementCPInstance.getCPInstanceId());

		return new ReplacementSku() {
			{
				setPrice(
					() -> {
						String replacementUnitOfMeasureKey = StringPool.BLANK;

						if (!replacementUnitOfMeasures.isEmpty()) {
							CPInstanceUnitOfMeasure replacementUnitOfMeasure =
								replacementUnitOfMeasures.get(0);

							replacementUnitOfMeasureKey =
								replacementUnitOfMeasure.getKey();
						}

						return _getPrice(
							skuDTOConverterContext.getCommerceContext(),
							replacementCPInstance,
							_cpInstanceUnitOfMeasureLocalService.
								fetchCPInstanceUnitOfMeasure(
									replacementCPInstance.getCPInstanceId(),
									replacementUnitOfMeasureKey),
							JSONUtil.toString(
								JSONUtil.toJSONArray(
									replacementSkuSkuOptions,
									replacementSkuSkuOption ->
										_jsonFactory.createJSONObject(
											replacementSkuSkuOption.
												toString()))),
							skuDTOConverterContext.getLocale(),
							skuDTOConverterContext.getQuantity());
					});
				setProductConfiguration(
					() -> {
						if (replacementCPDefinition == null) {
							return null;
						}

						return _productConfigurationDTOConverter.toDTO(
							new ProductConfigurationDTOConverterContext(
								commerceContext,
								replacementCPDefinition.getCPDefinitionId(),
								skuDTOConverterContext.getLocale()));
					});
				setSku(replacementCPInstance::getSku);
				setSkuExternalReferenceCode(
					replacementCPInstance::getExternalReferenceCode);
				setSkuId(replacementCPInstance::getCPInstanceId);
				setSkuOptions(
					() -> SkuOptionUtil.getSkuOptions(
						_cpInstanceHelper.
							getCPInstanceCPDefinitionOptionRelsMap(
								replacementCPInstance.getCPInstanceId()),
						_cpInstanceLocalService,
						skuDTOConverterContext.getLocale()));
				setSkuUnitOfMeasures(
					() -> _toSkuUnitOfMeasures(
						commerceContext, replacementUnitOfMeasures,
						skuDTOConverterContext.getLocale()));
				setUrls(
					() -> LanguageUtils.getLanguageIdMap(
						_cpDefinitionLocalService.getUrlTitleMap(
							replacementCPInstance.getCPDefinitionId())));
			}
		};
	}

	private SkuUnitOfMeasure[] _toSkuUnitOfMeasures(
			CommerceContext commerceContext,
			List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures,
			Locale locale)
		throws Exception {

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(null, locale);

		dtoConverterContext.setAttribute("commerceContext", commerceContext);

		return TransformUtil.transformToArray(
			cpInstanceUnitOfMeasures,
			cpInstanceUnitOfMeasure -> _skuUnitOfMeasureDTOConverter.toDTO(
				dtoConverterContext, cpInstanceUnitOfMeasure),
			SkuUnitOfMeasure.class);
	}

	private TierPrice[] _toTierPrices(
			CommerceContext commerceContext,
			List<CommerceTierPriceEntry> commerceTierPriceEntries,
			Locale locale)
		throws Exception {

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(null, locale);

		dtoConverterContext.setAttribute("commerceContext", commerceContext);

		return TransformUtil.transformToArray(
			commerceTierPriceEntries,
			commerceTierPriceEntry -> _tierPriceDTOConverter.toDTO(
				dtoConverterContext, commerceTierPriceEntry),
			TierPrice.class);
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceInventoryEngine _commerceInventoryEngine;

	@Reference
	private CommerceOptionValueHelper _commerceOptionValueHelper;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CommerceTierPriceEntryLocalService
		_commerceTierPriceEntryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPContentHelper _cpContentHelper;

	@Reference
	private CPDefinitionInventoryEngine _cpDefinitionInventoryEngine;

	@Reference
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ProductConfigurationDTOConverter)"
	)
	private DTOConverter<CPDefinitionInventory, ProductConfiguration>
		_productConfigurationDTOConverter;

	@Reference(target = DTOConverterConstants.SKU_UNIT_OF_MEASURE_DTO_CONVERTER)
	private DTOConverter<CPInstanceUnitOfMeasure, SkuUnitOfMeasure>
		_skuUnitOfMeasureDTOConverter;

	@Reference(target = DTOConverterConstants.TIER_PRICE_DTO_CONVERTER)
	private DTOConverter<CommerceTierPriceEntry, TierPrice>
		_tierPriceDTOConverter;

}