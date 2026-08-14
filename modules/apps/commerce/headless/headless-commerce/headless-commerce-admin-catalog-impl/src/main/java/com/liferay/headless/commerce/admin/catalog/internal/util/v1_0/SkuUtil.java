/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionValueRelException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuSubscriptionConfiguration;
import com.liferay.headless.commerce.admin.catalog.internal.util.DateConfigUtil;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @author Alessio Antonio Rendina
 */
public class SkuUtil {

	public static CPInstance addOrUpdateCPInstance(
			CPInstanceService cpInstanceService, Sku sku,
			CPDefinition cpDefinition,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService, ServiceContext serviceContext)
		throws PortalException {

		long replacementCProductId = 0;
		String replacementCPInstanceUuid = null;
		int discontinuedDateMonth = 0;
		int discontinuedDateDay = 0;
		int discontinuedDateYear = 0;

		if (GetterUtil.getBoolean(sku.getDiscontinued())) {
			CPInstance discontinuedCPInstance = null;

			if (Validator.isNotNull(
					sku.getReplacementSkuExternalReferenceCode())) {

				discontinuedCPInstance =
					cpInstanceService.fetchCPInstanceByExternalReferenceCode(
						sku.getReplacementSkuExternalReferenceCode(),
						cpDefinition.getCompanyId());
			}

			long replacementSkuId = GetterUtil.getLong(
				sku.getReplacementSkuId());

			if ((discontinuedCPInstance == null) && (replacementSkuId > 0)) {
				discontinuedCPInstance = cpInstanceService.fetchCPInstance(
					replacementSkuId);
			}

			if (discontinuedCPInstance != null) {
				CPDefinition discontinuedCPDefinition =
					discontinuedCPInstance.getCPDefinition();

				replacementCProductId =
					discontinuedCPDefinition.getCProductId();

				replacementCPInstanceUuid =
					discontinuedCPInstance.getCPInstanceUuid();
			}

			if (sku.getDiscontinuedDate() != null) {
				Date discontinuedDate = GetterUtil.getDate(
					sku.getDiscontinuedDate(),
					DateFormatFactoryUtil.getSimpleDateFormat("MM/dd/yyyy"),
					null);

				Calendar discontinuedCalendar = CalendarFactoryUtil.getCalendar(
					discontinuedDate.getTime());

				discontinuedDateDay = discontinuedCalendar.get(
					Calendar.DAY_OF_MONTH);
				discontinuedDateMonth = discontinuedCalendar.get(
					Calendar.MONTH);
				discontinuedDateYear = discontinuedCalendar.get(Calendar.YEAR);
			}
		}

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		if (sku.getDisplayDate() != null) {
			displayCalendar = DateConfigUtil.convertDateToCalendar(
				sku.getDisplayDate());
		}

		DateConfig displayDateConfig = new DateConfig(displayCalendar);

		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			sku.getExpirationDate(), serviceContext.getTimeZone());

		SkuSubscriptionConfiguration skuSubscriptionConfiguration =
			sku.getSkuSubscriptionConfiguration();

		boolean deliverySubscriptionEnable = false;
		int deliverySubscriptionLength = 1;
		long deliverySubscriptionMaxSubscriptionCycles = 0;
		UnicodeProperties deliverySubscriptionTypeSettingsUnicodeProperties =
			null;
		String deliverySubscriptionTypeValue = StringPool.BLANK;
		boolean overrideSubscriptionInfo = false;
		boolean subscriptionEnable = false;
		int subscriptionLength = 1;
		long subscriptionMaxSubscriptionCycles = 0;
		UnicodeProperties subscriptionTypeSettingsUnicodeProperties = null;
		String subscriptionTypeValue = StringPool.BLANK;

		if (skuSubscriptionConfiguration != null) {
			deliverySubscriptionEnable = GetterUtil.getBoolean(
				skuSubscriptionConfiguration.getDeliverySubscriptionEnable(),
				deliverySubscriptionEnable);
			deliverySubscriptionLength = GetterUtil.getInteger(
				skuSubscriptionConfiguration.getDeliverySubscriptionLength(),
				deliverySubscriptionLength);
			deliverySubscriptionMaxSubscriptionCycles = GetterUtil.getLong(
				skuSubscriptionConfiguration.
					getDeliverySubscriptionNumberOfLength(),
				deliverySubscriptionMaxSubscriptionCycles);

			if (Validator.isNotNull(
					skuSubscriptionConfiguration.
						getDeliverySubscriptionTypeSettings())) {

				deliverySubscriptionTypeSettingsUnicodeProperties =
					UnicodePropertiesBuilder.create(
						skuSubscriptionConfiguration.
							getDeliverySubscriptionTypeSettings(),
						true
					).build();
			}

			SkuSubscriptionConfiguration.DeliverySubscriptionType
				deliverySubscriptionType =
					skuSubscriptionConfiguration.getDeliverySubscriptionType();

			if (deliverySubscriptionType != null) {
				deliverySubscriptionTypeValue =
					deliverySubscriptionType.getValue();
			}

			overrideSubscriptionInfo = GetterUtil.getBoolean(
				skuSubscriptionConfiguration.getOverrideSubscriptionInfo(),
				overrideSubscriptionInfo);
			subscriptionEnable = GetterUtil.getBoolean(
				skuSubscriptionConfiguration.getEnable(), subscriptionEnable);
			subscriptionLength = GetterUtil.getInteger(
				skuSubscriptionConfiguration.getLength(), subscriptionLength);
			subscriptionMaxSubscriptionCycles = GetterUtil.getLong(
				skuSubscriptionConfiguration.getNumberOfLength(),
				subscriptionMaxSubscriptionCycles);

			if (Validator.isNotNull(
					skuSubscriptionConfiguration.
						getSubscriptionTypeSettings())) {

				subscriptionTypeSettingsUnicodeProperties =
					UnicodePropertiesBuilder.create(
						skuSubscriptionConfiguration.
							getSubscriptionTypeSettings(),
						true
					).build();
			}

			SkuSubscriptionConfiguration.SubscriptionType subscriptionType =
				skuSubscriptionConfiguration.getSubscriptionType();

			if (subscriptionType != null) {
				subscriptionTypeValue = subscriptionType.getValue();
			}
		}

		return cpInstanceService.addOrUpdateCPInstance(
			sku.getExternalReferenceCode(), cpDefinition.getCPDefinitionId(),
			cpDefinition.getGroupId(), sku.getSku(), sku.getGtin(),
			sku.getManufacturerPartNumber(),
			GetterUtil.get(sku.getPurchasable(), false),
			_getOptions(
				cpDefinition, cpDefinitionOptionRelService,
				cpDefinitionOptionValueRelService, cpOptionService, sku),
			GetterUtil.get(sku.getWidth(), 0.0),
			GetterUtil.get(sku.getHeight(), 0.0),
			GetterUtil.get(sku.getDepth(), 0.0),
			GetterUtil.get(sku.getWeight(), 0.0),
			(BigDecimal)GetterUtil.get(sku.getPrice(), BigDecimal.ZERO),
			(BigDecimal)GetterUtil.get(sku.getPromoPrice(), BigDecimal.ZERO),
			(BigDecimal)GetterUtil.get(sku.getCost(), BigDecimal.ZERO),
			GetterUtil.get(sku.getPublished(), false),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.get(sku.getNeverExpire(), false),
			overrideSubscriptionInfo, subscriptionEnable, subscriptionLength,
			subscriptionTypeValue, subscriptionTypeSettingsUnicodeProperties,
			subscriptionMaxSubscriptionCycles, deliverySubscriptionEnable,
			deliverySubscriptionLength, deliverySubscriptionTypeValue,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliverySubscriptionMaxSubscriptionCycles, sku.getUnspsc(),
			GetterUtil.get(sku.getDiscontinued(), false),
			replacementCPInstanceUuid, replacementCProductId,
			discontinuedDateMonth, discontinuedDateDay, discontinuedDateYear,
			serviceContext);
	}

	public static void updateCommercePriceEntries(
			CommercePriceEntryLocalService commercePriceEntryLocalService,
			CommercePriceListLocalService commercePriceListLocalService,
			ConfigurationProvider configurationProvider, CPInstance cpInstance,
			BigDecimal price, BigDecimal promoPrice, String unitOfMeasureKey,
			ServiceContext serviceContext)
		throws Exception {

		if (Objects.equals(
				_getCommercePricingConfigurationKey(configurationProvider),
				CommercePricingConstants.VERSION_2_0)) {

			_updateCommercePriceEntry(
				commercePriceEntryLocalService, commercePriceListLocalService,
				cpInstance, CommercePriceListConstants.TYPE_PRICE_LIST, price,
				unitOfMeasureKey, serviceContext);
			_updateCommercePriceEntry(
				commercePriceEntryLocalService, commercePriceListLocalService,
				cpInstance, CommercePriceListConstants.TYPE_PROMOTION,
				promoPrice, unitOfMeasureKey, serviceContext);
		}
	}

	private static CPDefinitionOptionRel _fetchCPDefinitionOptionRel(
			CPDefinition cpDefinition,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPOptionService cpOptionService, SkuOption skuOption)
		throws PortalException {

		String externalReferenceCode =
			skuOption.getOptionExternalReferenceCode();

		if (Validator.isNotNull(externalReferenceCode)) {
			CPOption cpOption = cpOptionService.fetchCPOption(
				cpDefinition.getCompanyId(), skuOption.getKey());

			if (cpOption == null) {
				return cpDefinitionOptionRelService.
					getCPDefinitionOptionRelByExternalReferenceCode(
						externalReferenceCode, cpDefinition.getCompanyId());
			}

			return cpDefinitionOptionRelService.
				getOrAddEmptyCPDefinitionOptionRel(
					externalReferenceCode, cpDefinition.getCPDefinitionId(),
					cpOption.getCPOptionId(),
					cpOption.getCommerceOptionTypeKey());
		}

		if (Validator.isNull(skuOption.getKey())) {
			return cpDefinitionOptionRelService.fetchCPDefinitionOptionRel(
				GetterUtil.getLong(skuOption.getOptionId()));
		}

		try {
			return cpDefinitionOptionRelService.fetchCPDefinitionOptionRel(
				GetterUtil.getLongStrict(skuOption.getKey()));
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}
		}

		return null;
	}

	private static CPDefinitionOptionValueRel _fetchCPDefinitionOptionValueRel(
			long companyId, CPDefinitionOptionRel cpDefinitionOptionRel,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			SkuOption skuOption)
		throws PortalException {

		String externalReferenceCode =
			skuOption.getOptionValueExternalReferenceCode();

		if (Validator.isNotNull(externalReferenceCode)) {
			if (cpDefinitionOptionRel == null) {
				return cpDefinitionOptionValueRelService.
					fetchCPDefinitionOptionValueRelByExternalReferenceCode(
						externalReferenceCode, companyId);
			}

			long cpDefinitionOptionRelId =
				cpDefinitionOptionRel.getCPDefinitionOptionRelId();

			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				cpDefinitionOptionValueRelService.
					getOrAddEmptyCPDefinitionOptionValueRel(
						externalReferenceCode, cpDefinitionOptionRelId);

			if (cpDefinitionOptionValueRel.getCPDefinitionOptionRelId() !=
					cpDefinitionOptionRelId) {

				throw new NoSuchCPDefinitionOptionValueRelException(
					StringBundler.concat(
						"Product option value with external reference code ",
						externalReferenceCode,
						" does not belong to product option ",
						cpDefinitionOptionRel.getKey()));
			}

			return cpDefinitionOptionValueRel;
		}

		if (Validator.isNull(skuOption.getValue())) {
			return cpDefinitionOptionValueRelService.
				fetchCPDefinitionOptionValueRel(
					GetterUtil.getLong(skuOption.getOptionValueId()));
		}

		try {
			return cpDefinitionOptionValueRelService.
				fetchCPDefinitionOptionValueRel(
					GetterUtil.getLongStrict(skuOption.getValue()));
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}
		}

		return null;
	}

	private static String _getCommercePricingConfigurationKey(
			ConfigurationProvider configurationProvider)
		throws Exception {

		CommercePricingConfiguration commercePricingConfiguration =
			configurationProvider.getConfiguration(
				CommercePricingConfiguration.class,
				new SystemSettingsLocator(
					CommercePricingConstants.SERVICE_NAME));

		return commercePricingConfiguration.commercePricingCalculationKey();
	}

	private static String _getOptions(
			CPDefinition cpDefinition,
			CPDefinitionOptionRelService cpDefinitionOptionRelService,
			CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService,
			CPOptionService cpOptionService, Sku sku)
		throws PortalException {

		SkuOption[] skuOptions = sku.getSkuOptions();

		if (skuOptions == null) {
			return StringPool.BLANK;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (SkuOption skuOption : skuOptions) {
			CPDefinitionOptionRel cpDefinitionOptionRel =
				_fetchCPDefinitionOptionRel(
					cpDefinition, cpDefinitionOptionRelService, cpOptionService,
					skuOption);

			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				_fetchCPDefinitionOptionValueRel(
					cpDefinition.getCompanyId(), cpDefinitionOptionRel,
					cpDefinitionOptionValueRelService, skuOption);

			jsonArray.put(
				JSONUtil.put(
					"key",
					() -> {
						if (cpDefinitionOptionRel != null) {
							return cpDefinitionOptionRel.getKey();
						}

						return skuOption.getKey();
					}
				).put(
					"value",
					JSONUtil.put(
						() -> {
							if (cpDefinitionOptionValueRel != null) {
								return cpDefinitionOptionValueRel.getKey();
							}

							return skuOption.getValue();
						})
				));
		}

		return jsonArray.toString();
	}

	private static void _updateCommercePriceEntry(
			CommercePriceEntryLocalService commercePriceEntryLocalService,
			CommercePriceListLocalService commercePriceListLocalService,
			CPInstance cpInstance, String type, BigDecimal price,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws Exception {

		CommercePriceList commercePriceList =
			commercePriceListLocalService.getCatalogBaseCommercePriceListByType(
				cpInstance.getGroupId(), type);

		CommercePriceEntry commercePriceEntry =
			commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceList.getCommercePriceListId(),
				cpInstance.getCPInstanceUuid(), unitOfMeasureKey);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		if (commercePriceEntry == null) {
			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			commercePriceEntryLocalService.addCommercePriceEntry(
				null, cpDefinition.getCProductId(),
				cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price, false, null,
				unitOfMeasureKey, serviceContext);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(SkuUtil.class);

}