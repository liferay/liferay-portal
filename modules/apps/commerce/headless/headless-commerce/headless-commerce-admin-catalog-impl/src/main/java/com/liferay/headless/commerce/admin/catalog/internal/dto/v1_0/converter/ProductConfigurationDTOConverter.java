/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.constants.CPConfigurationEntrySettingConstants;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPConfigurationEntryService;
import com.liferay.commerce.product.service.CPConfigurationEntrySettingLocalService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductShippingConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.model.CPDefinitionInventory",
	service = DTOConverter.class
)
public class ProductConfigurationDTOConverter
	implements DTOConverter<CPDefinitionInventory, ProductConfiguration> {

	@Override
	public String getContentType() {
		return ProductConfiguration.class.getSimpleName();
	}

	@Override
	public ProductConfiguration toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		ProductConfiguration productConfiguration = new ProductConfiguration();

		CPConfigurationEntry cpConfigurationEntry;

		if (dtoConverterContext.getId() != null) {
			CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
				(Long)dtoConverterContext.getId());

			cpConfigurationEntry =
				cpDefinition.fetchMasterCPConfigurationEntry();
		}
		else {
			ProductConfigurationDTOConverterContext
				productConfigurationDTOConverterContext =
					(ProductConfigurationDTOConverterContext)
						dtoConverterContext;

			cpConfigurationEntry =
				_cpConfigurationEntryService.getCPConfigurationEntry(
					productConfigurationDTOConverterContext.
						getCPConfigurationEntryId());
		}

		if (cpConfigurationEntry == null) {
			return productConfiguration;
		}

		productConfiguration.setActions(dtoConverterContext::getActions);
		productConfiguration.setAllowBackOrder(
			cpConfigurationEntry::isBackOrders);
		productConfiguration.setAllowedOrderQuantities(
			cpConfigurationEntry::getAllowedOrderQuantitiesArray);
		productConfiguration.setDifferences(
			() -> _getDifferences(cpConfigurationEntry, dtoConverterContext));
		productConfiguration.setEntityExternalReferenceCode(
			() -> _getEntityExternalReferenceCode(cpConfigurationEntry));
		productConfiguration.setEntityId(cpConfigurationEntry::getClassPK);
		productConfiguration.setEntityName(
			() -> _getEntityName(
				cpConfigurationEntry, dtoConverterContext.getLocale()));
		productConfiguration.setExternalReferenceCode(
			cpConfigurationEntry::getExternalReferenceCode);
		productConfiguration.setId(
			cpConfigurationEntry::getCPConfigurationEntryId);
		productConfiguration.setInventoryEngine(
			cpConfigurationEntry::getCPDefinitionInventoryEngine);
		productConfiguration.setLowStockAction(
			cpConfigurationEntry::getLowStockActivity);
		productConfiguration.setMaxOrderQuantity(
			() -> BigDecimalUtil.stripTrailingZeros(
				cpConfigurationEntry.getMaxOrderQuantity()));
		productConfiguration.setMinOrderQuantity(
			() -> BigDecimalUtil.stripTrailingZeros(
				cpConfigurationEntry.getMinOrderQuantity()));
		productConfiguration.setMinStockQuantity(
			() -> BigDecimalUtil.stripTrailingZeros(
				cpConfigurationEntry.getMinStockQuantity()));
		productConfiguration.setMultipleOrderQuantity(
			() -> BigDecimalUtil.stripTrailingZeros(
				cpConfigurationEntry.getMultipleOrderQuantity()));
		productConfiguration.setProductShippingConfiguration(
			() -> {
				ProductShippingConfiguration productShippingConfiguration =
					new ProductShippingConfiguration();

				productShippingConfiguration.setDepth(
					() -> BigDecimal.valueOf(cpConfigurationEntry.getDepth()));
				productShippingConfiguration.setFreeShipping(
					cpConfigurationEntry::isFreeShipping);
				productShippingConfiguration.setHeight(
					() -> BigDecimal.valueOf(cpConfigurationEntry.getHeight()));
				productShippingConfiguration.setShippable(
					cpConfigurationEntry::isShippable);
				productShippingConfiguration.setShippingExtraPrice(
					() -> BigDecimal.valueOf(
						cpConfigurationEntry.getShippingExtraPrice()));
				productShippingConfiguration.setShippingSeparately(
					cpConfigurationEntry::isShipSeparately);
				productShippingConfiguration.setWeight(
					() -> BigDecimal.valueOf(cpConfigurationEntry.getWeight()));
				productShippingConfiguration.setWidth(
					() -> BigDecimal.valueOf(cpConfigurationEntry.getWidth()));

				return productShippingConfiguration;
			});
		productConfiguration.setProductTaxConfiguration(
			() -> {
				ProductTaxConfiguration productTaxConfiguration =
					new ProductTaxConfiguration();

				productTaxConfiguration.setId(
					cpConfigurationEntry::getCPTaxCategoryId);
				productTaxConfiguration.setTaxable(
					() -> !cpConfigurationEntry.isTaxExempt());
				productTaxConfiguration.setTaxCategory(
					() -> _getTaxCategory(
						cpConfigurationEntry.getCPTaxCategory(),
						dtoConverterContext.getLocale()));

				return productTaxConfiguration;
			});
		productConfiguration.setPurchasable(
			cpConfigurationEntry::getPurchasable);

		return productConfiguration;
	}

	private String[] _getDifferences(
			CPConfigurationEntry cpConfigurationEntry,
			DTOConverterContext dtoConverterContext)
		throws PortalException {

		if (dtoConverterContext.getId() != null) {
			return null;
		}

		ProductConfigurationDTOConverterContext
			productConfigurationDTOConverterContext =
				(ProductConfigurationDTOConverterContext)dtoConverterContext;

		if (!productConfigurationDTOConverterContext.getShowDifferences()) {
			return null;
		}

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			_cpConfigurationEntrySettingLocalService.
				fetchCPConfigurationEntrySetting(
					cpConfigurationEntry.getCPConfigurationEntryId(),
					CPConfigurationEntrySettingConstants.TYPE_CHANGE_LOG);

		if ((cpConfigurationEntrySetting == null) ||
			(cpConfigurationEntry.getParentCPConfigurationList() == null)) {

			return null;
		}

		List<String> differences = new ArrayList<>();

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				cpConfigurationEntrySetting.getValue());

			if (!Objects.equals(
					jsonObject.getString("allowedOrderQuantities"),
					cpConfigurationEntry.getAllowedOrderQuantities())) {

				differences.add("allowedOrderQuantities");
			}

			if (jsonObject.getBoolean("backOrders", true) !=
					cpConfigurationEntry.isBackOrders()) {

				differences.add("backOrders");
			}

			if (jsonObject.getLong("commerceAvailabilityEstimateId") !=
					cpConfigurationEntry.getCommerceAvailabilityEstimateId()) {

				differences.add("commerceAvailabilityEstimateId");
			}

			if (!Objects.equals(
					jsonObject.getString(
						"CPDefinitionInventoryEngine", "default"),
					cpConfigurationEntry.getCPDefinitionInventoryEngine())) {

				differences.add("CPDefinitionInventoryEngine");
			}

			if (jsonObject.getLong("CPTaxCategoryId") !=
					cpConfigurationEntry.getCPTaxCategoryId()) {

				differences.add("CPTaxCategoryId");
			}

			if (jsonObject.getDouble("depth") !=
					cpConfigurationEntry.getDepth()) {

				differences.add("depth");
			}

			if (jsonObject.getBoolean("displayAvailability", false) !=
					cpConfigurationEntry.isDisplayAvailability()) {

				differences.add("displayAvailability");
			}

			if (jsonObject.getBoolean("displayStockQuantity", false) !=
					cpConfigurationEntry.isDisplayStockQuantity()) {

				differences.add("displayStockQuantity");
			}

			if (jsonObject.getBoolean("freeShipping", false) !=
					cpConfigurationEntry.isFreeShipping()) {

				differences.add("freeShipping");
			}

			if (jsonObject.getDouble("height") !=
					cpConfigurationEntry.getHeight()) {

				differences.add("height");
			}

			if (!Objects.equals(
					jsonObject.getString("lowStockActivity"),
					cpConfigurationEntry.getLowStockActivity())) {

				differences.add("lowStockActivity");
			}

			if (!BigDecimalUtil.eq(
					BigDecimal.valueOf(
						jsonObject.getDouble(
							"maxOrderQuantity",
							CPDefinitionInventoryConstants.
								DEFAULT_MAX_ORDER_QUANTITY.doubleValue())),
					cpConfigurationEntry.getMaxOrderQuantity())) {

				differences.add("maxOrderQuantity");
			}

			if (!BigDecimalUtil.eq(
					BigDecimal.valueOf(
						jsonObject.getDouble(
							"minOrderQuantity",
							CPDefinitionInventoryConstants.
								DEFAULT_MIN_ORDER_QUANTITY.doubleValue())),
					cpConfigurationEntry.getMinOrderQuantity())) {

				differences.add("minOrderQuantity");
			}

			if (!BigDecimalUtil.eq(
					BigDecimal.valueOf(
						jsonObject.getDouble("minStockQuantity")),
					cpConfigurationEntry.getMinStockQuantity())) {

				differences.add("minStockQuantity");
			}

			if (!BigDecimalUtil.eq(
					BigDecimal.valueOf(
						jsonObject.getDouble(
							"multipleOrderQuantity",
							CPDefinitionInventoryConstants.
								DEFAULT_MULTIPLE_ORDER_QUANTITY.doubleValue())),
					cpConfigurationEntry.getMultipleOrderQuantity())) {

				differences.add("multipleOrderQuantity");
			}

			if (jsonObject.getBoolean("purchasable", true) !=
					cpConfigurationEntry.isPurchasable()) {

				differences.add("purchasable");
			}

			if (jsonObject.getBoolean("shipSeparately", false) !=
					cpConfigurationEntry.isShipSeparately()) {

				differences.add("shipSeparately");
			}

			if (jsonObject.getBoolean("shippable", true) !=
					cpConfigurationEntry.isShippable()) {

				differences.add("shippable");
			}

			if (jsonObject.getDouble("shippingExtraPrice") !=
					cpConfigurationEntry.getShippingExtraPrice()) {

				differences.add("shippingExtraPrice");
			}

			if (jsonObject.getBoolean("taxExempt", false) !=
					cpConfigurationEntry.isTaxExempt()) {

				differences.add("taxExempt");
			}

			if (jsonObject.getDouble("weight") !=
					cpConfigurationEntry.getWeight()) {

				differences.add("weight");
			}

			if (jsonObject.getDouble("width") !=
					cpConfigurationEntry.getWidth()) {

				differences.add("width");
			}
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return ArrayUtil.toStringArray(differences);
	}

	private String _getEntityExternalReferenceCode(
			CPConfigurationEntry cpConfigurationEntry)
		throws Exception {

		if (!StringUtil.equals(
				CPDefinition.class.getName(),
				cpConfigurationEntry.getClassName())) {

			return null;
		}

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			cpConfigurationEntry.getClassPK());

		CProduct cProduct = cpDefinition.getCProduct();

		return cProduct.getExternalReferenceCode();
	}

	private String _getEntityName(
			CPConfigurationEntry cpConfigurationEntry, Locale locale)
		throws PortalException {

		if (!StringUtil.equals(
				CPDefinition.class.getName(),
				cpConfigurationEntry.getClassName())) {

			return null;
		}

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			cpConfigurationEntry.getClassPK());

		return cpDefinition.getName(LocaleUtil.toLanguageId(locale));
	}

	private String _getTaxCategory(CPTaxCategory cpTaxCategory, Locale locale) {
		if (cpTaxCategory == null) {
			return null;
		}

		return cpTaxCategory.getName(locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductConfigurationDTOConverter.class);

	@Reference
	private CPConfigurationEntryService _cpConfigurationEntryService;

	@Reference
	private CPConfigurationEntrySettingLocalService
		_cpConfigurationEntrySettingLocalService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private JSONFactory _jsonFactory;

}