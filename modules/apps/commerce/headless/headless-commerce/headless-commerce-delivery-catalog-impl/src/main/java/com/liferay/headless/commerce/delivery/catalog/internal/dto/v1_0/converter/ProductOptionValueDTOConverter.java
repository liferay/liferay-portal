/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.order.rule.constants.COREntryConstants;
import com.liferay.commerce.order.rule.entry.type.COREntryType;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeItem;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeRegistry;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.commerce.price.CommerceProductOptionValueRelativePriceRequest;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.configuration.CPDefinitionOptionRelConfiguration;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPDefinitionLinkTypeConstants;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceModel;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.SkuOption;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=CPDefinitionOptionValueRel",
	service = DTOConverter.class
)
public class ProductOptionValueDTOConverter
	implements DTOConverter<CPDefinitionOptionValueRel, ProductOptionValue> {

	@Override
	public String getContentType() {
		return ProductOptionValue.class.getSimpleName();
	}

	@Override
	public ProductOptionValue toDTO(
			DTOConverterContext dtoConverterContext,
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws Exception {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			cpDefinitionOptionValueRel.getCProductId(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		BigDecimal priceBigDecimal = cpDefinitionOptionValueRel.getPrice();

		CPDefinitionOptionRelConfiguration cpDefinitionOptionRelConfiguration =
			_configurationProvider.getCompanyConfiguration(
				CPDefinitionOptionRelConfiguration.class,
				cpDefinitionOptionRel.getCompanyId());

		return new ProductOptionValue() {
			{
				setId(
					cpDefinitionOptionValueRel::
						getCPDefinitionOptionValueRelId);
				setInfoMessage(
					() -> {
						if ((cpInstance == null) ||
							!cpDefinitionOptionRelConfiguration.
								showUnselectableOptions()) {

							return null;
						}

						Long skuId = (Long)dtoConverterContext.getAttribute(
							"skuId");

						if (Validator.isNull(skuId)) {
							return null;
						}

						CPInstance selectedCPInstance =
							_cpInstanceLocalService.fetchCPInstance(skuId);

						if (selectedCPInstance == null) {
							return null;
						}

						List<CommerceOptionValue> commerceOptionValues =
							new ArrayList<>();

						JSONArray jsonArray = _getSelectedSkuOptionsJSONArray(
							cpDefinitionOptionRel, cpDefinitionOptionValueRel,
							(SkuOption[])dtoConverterContext.getAttribute(
								"skuOptions"));

						if (jsonArray == null) {
							jsonArray = _getClonedJSONArray(
								cpDefinitionOptionRel,
								cpDefinitionOptionValueRel,
								selectedCPInstance.getCPInstanceId());
						}

						if (jsonArray != null) {
							commerceOptionValues =
								_commerceOptionValueHelper.
									getCPDefinitionCommerceOptionValues(
										cpDefinitionOptionRel.
											getCPDefinitionId(),
										jsonArray.toString());
						}

						CPDefinition cpDefinition =
							cpInstance.getCPDefinition();

						String corEntryInfoMessage = _getCOREntryInfoMessage(
							commerceOptionValues, cpDefinitionOptionRel,
							cpDefinitionOptionValueRel, cpInstance,
							_getCProductExternalReferenceCode(cpDefinition),
							dtoConverterContext);

						if (Validator.isNotNull(corEntryInfoMessage)) {
							return corEntryInfoMessage;
						}

						String cpDefinitionLinkInfoMessage =
							_getCPDefinitionLinkInfoMessage(
								commerceOptionValues,
								cpDefinitionOptionValueRel.getKey(),
								cpDefinition, dtoConverterContext);

						if (Validator.isNotNull(cpDefinitionLinkInfoMessage)) {
							return cpDefinitionLinkInfoMessage;
						}

						return null;
					});
				setKey(cpDefinitionOptionValueRel::getKey);
				setName(
					() -> {
						if (!Objects.equals(
								CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
								cpDefinitionOptionRel.
									getCommerceOptionTypeKey())) {

							return cpDefinitionOptionValueRel.getName(
								_language.getLanguageId(
									dtoConverterContext.getLocale()));
						}

						String cpDefinitionOptionValueRelKey =
							cpDefinitionOptionValueRel.getKey();

						String[] parts = cpDefinitionOptionValueRelKey.split(
							StringPool.DASH);

						TimeZone timeZone = TimeZoneUtil.getTimeZone(
							_getTimeZoneId(parts));

						Calendar calendar = CalendarFactoryUtil.getCalendar(
							Integer.valueOf(parts[2]),
							Integer.valueOf(parts[0]) - 1,
							Integer.valueOf(parts[1]),
							Integer.valueOf(parts[3]),
							Integer.valueOf(parts[4]));

						DateFormat dateFormat = DateFormat.getDateTimeInstance(
							DateFormat.LONG, DateFormat.SHORT,
							dtoConverterContext.getLocale());

						return StringBundler.concat(
							dateFormat.format(calendar.getTime()),
							StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
							timeZone.getDisplayName(
								dtoConverterContext.getLocale()),
							StringPool.CLOSE_PARENTHESIS,
							StringPool.COMMA_AND_SPACE, parts[5],
							StringPool.SPACE,
							_language.get(
								dtoConverterContext.getLocale(), parts[6]));
					});
				setPreselected(cpDefinitionOptionValueRel::isPreselected);
				setPrice(
					() ->
						(priceBigDecimal == null) ? BigDecimal.ZERO.toString() :
							priceBigDecimal.toString());
				setPriceType(cpDefinitionOptionRel::getPriceType);
				setPriority(cpDefinitionOptionValueRel::getPriority);
				setProductOptionId(
					cpDefinitionOptionRel::getCPDefinitionOptionRelId);
				setQuantity(
					() -> String.valueOf(
						cpDefinitionOptionValueRel.getQuantity()));
				setRelativePriceFormatted(
					() -> {
						CommerceContext commerceContext =
							(CommerceContext)dtoConverterContext.getAttribute(
								"commerceContext");
						Long productOptionValueId =
							(Long)dtoConverterContext.getAttribute(
								"productOptionValueId");
						Long skuId = (Long)dtoConverterContext.getAttribute(
							"skuId");

						if ((commerceContext == null) ||
							Validator.isNull(productOptionValueId) ||
							Validator.isNull(skuId)) {

							return null;
						}

						CPInstance selectedCPInstance =
							_cpInstanceLocalService.fetchCPInstance(skuId);

						if (selectedCPInstance == null) {
							return null;
						}

						JSONArray clonedJSONArray = _getClonedJSONArray(
							cpDefinitionOptionRel, cpDefinitionOptionValueRel,
							selectedCPInstance.getCPInstanceId());

						if (clonedJSONArray == null) {
							return null;
						}

						String unitOfMeasureKey = null;

						CPInstance cpInstance =
							_cpInstanceHelper.fetchCPInstance(
								cpDefinitionOptionRel.getCPDefinitionId(),
								clonedJSONArray.toString());

						CPDefinitionOptionValueRel
							selectedCPDefinitionOptionValueRel =
								_cpDefinitionOptionValueRelLocalService.
									getCPDefinitionOptionValueRel(
										productOptionValueId);

						long selectedCPDefinitionOptionRelId =
							selectedCPDefinitionOptionValueRel.
								getCPDefinitionOptionRelId();

						if (selectedCPDefinitionOptionRelId !=
								cpDefinitionOptionValueRel.
									getCPDefinitionOptionRelId()) {

							JSONArray jsonArray = CPJSONUtil.toJSONArray(
								_cpDefinitionOptionRelLocalService.
									getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
										selectedCPInstance.getCPInstanceId()));

							selectedCPDefinitionOptionValueRel =
								_getSelectedCPDefinitionOptionValueRel(
									cpDefinitionOptionRel, jsonArray);
						}

						if (selectedCPDefinitionOptionValueRel != null) {
							unitOfMeasureKey =
								selectedCPDefinitionOptionValueRel.
									getUnitOfMeasureKey();
						}

						CommerceProductOptionValueRelativePriceRequest.Builder
							builder =
								new CommerceProductOptionValueRelativePriceRequest.Builder(
									commerceContext,
									cpDefinitionOptionValueRel);

						CommerceMoney commerceMoney =
							_commerceProductPriceCalculation.
								getCPDefinitionOptionValueRelativePrice(
									builder.cpInstanceId(
										(cpInstance == null) ? 0 :
											cpInstance.getCPInstanceId()
									).cpInstanceMinQuantity(
										_getMinOrderQuantity(
											commerceContext.
												getCPConfigurationListId(
													cpDefinitionOptionRel.
														getGroupId()),
											cpInstance)
									).cpInstanceUnitOfMeasureKey(
										cpDefinitionOptionValueRel.
											getUnitOfMeasureKey()
									).selectedCPInstanceId(
										selectedCPInstance.getCPInstanceId()
									).selectedCPInstanceMinQuantity(
										_getMinOrderQuantity(
											commerceContext.
												getCPConfigurationListId(
													cpDefinitionOptionRel.
														getGroupId()),
											selectedCPInstance)
									).selectedCPDefinitionOptionValueRel(
										selectedCPDefinitionOptionValueRel
									).selectedCPInstanceUnitOfMeasureKey(
										unitOfMeasureKey
									).build());

						return commerceMoney.format(
							dtoConverterContext.getLocale());
					});
				setSelectable(
					() -> {
						if (cpInstance == null) {
							return true;
						}

						Long skuId = (Long)dtoConverterContext.getAttribute(
							"skuId");

						if (Validator.isNull(skuId)) {
							return true;
						}

						CPInstance selectedCPInstance =
							_cpInstanceLocalService.fetchCPInstance(skuId);

						if (selectedCPInstance == null) {
							return true;
						}

						List<CommerceOptionValue> commerceOptionValues =
							new ArrayList<>();

						JSONArray jsonArray = _getSelectedSkuOptionsJSONArray(
							cpDefinitionOptionRel, cpDefinitionOptionValueRel,
							(SkuOption[])dtoConverterContext.getAttribute(
								"skuOptions"));

						if (jsonArray == null) {
							jsonArray = _getClonedJSONArray(
								cpDefinitionOptionRel,
								cpDefinitionOptionValueRel,
								selectedCPInstance.getCPInstanceId());
						}

						if (jsonArray != null) {
							commerceOptionValues =
								_commerceOptionValueHelper.
									getCPDefinitionCommerceOptionValues(
										cpDefinitionOptionRel.
											getCPDefinitionId(),
										jsonArray.toString());
						}

						CPDefinition cpDefinition =
							cpInstance.getCPDefinition();

						if (Validator.isNotNull(
								_getCOREntryInfoMessage(
									commerceOptionValues, cpDefinitionOptionRel,
									cpDefinitionOptionValueRel, cpInstance,
									_getCProductExternalReferenceCode(
										cpDefinition),
									dtoConverterContext)) ||
							Validator.isNotNull(
								_getCPDefinitionLinkInfoMessage(
									commerceOptionValues,
									cpDefinitionOptionValueRel.getKey(),
									cpDefinition, dtoConverterContext))) {

							return false;
						}

						return true;
					});
				setSkuId(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return cpInstance.getCPInstanceId();
					});
				setUnitOfMeasureKey(
					cpDefinitionOptionValueRel::getUnitOfMeasureKey);
				setVisible(
					() -> {
						if (!cpDefinitionOptionRel.isSkuContributor() ||
							cpDefinitionOptionRelConfiguration.
								showUnselectableOptions()) {

							return true;
						}

						Long skuId = (Long)dtoConverterContext.getAttribute(
							"skuId");

						if (Validator.isNull(skuId)) {
							return true;
						}

						CPInstance selectedCPInstance =
							_cpInstanceLocalService.fetchCPInstance(skuId);

						if (selectedCPInstance == null) {
							return true;
						}

						JSONArray clonedJSONArray = _getClonedJSONArray(
							cpDefinitionOptionRel, cpDefinitionOptionValueRel,
							selectedCPInstance.getCPInstanceId());

						if (clonedJSONArray == null) {
							return true;
						}

						CPInstance cpInstance =
							_cpInstanceHelper.fetchCPInstance(
								cpDefinitionOptionRel.getCPDefinitionId(),
								clonedJSONArray.toString());

						if (cpInstance == null) {
							return false;
						}

						return true;
					});
			}
		};
	}

	private JSONArray _getClonedJSONArray(
			CPDefinitionOptionRel cpDefinitionOptionRel,
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			long cpInstanceId)
		throws PortalException {

		JSONArray jsonArray = CPJSONUtil.toJSONArray(
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
					cpInstanceId));

		JSONArray clonedJSONArray = _jsonFactory.createJSONArray(
			jsonArray.toString());

		if (_updateJSONArray(
				cpDefinitionOptionRel.getKey(),
				cpDefinitionOptionValueRel.getKey(), clonedJSONArray)) {

			return clonedJSONArray;
		}

		return null;
	}

	private String _getCOREntryInfoMessage(
			List<CommerceOptionValue> commerceOptionValues,
			CPDefinitionOptionRel cpDefinitionOptionRel,
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
			CPInstance cpInstance, String cProductExternalReferenceCode,
			DTOConverterContext dtoConverterContext)
		throws Exception {

		String infoMessage = null;

		List<COREntryTypeItem> corEntryTypeItems = new ArrayList<>();

		for (CommerceOptionValue commerceOptionValue : commerceOptionValues) {
			if (Objects.equals(
					commerceOptionValue.getOptionValueKey(),
					cpDefinitionOptionValueRel.getKey())) {

				continue;
			}

			if (commerceOptionValue.getCPInstanceId() > 0) {
				CPInstance commerceOptionValueCPInstance =
					_cpInstanceLocalService.getCPInstance(
						commerceOptionValue.getCPInstanceId());

				corEntryTypeItems.add(
					new COREntryTypeItem(
						commerceOptionValueCPInstance.getCPDefinitionId(),
						commerceOptionValueCPInstance.getCPInstanceId(),
						cpDefinitionOptionValueRel.getQuantity()));
			}
		}

		corEntryTypeItems.add(
			new COREntryTypeItem(
				cpInstance.getCPDefinitionId(), cpInstance.getCPInstanceId(),
				cpDefinitionOptionValueRel.getQuantity()));

		COREntryType corEntryType = _corEntryTypeRegistry.getCOREntryType(
			COREntryConstants.TYPE_PRODUCTS_LIMIT);
		List<String> cProductExternalReferenceCodes = new ArrayList<>();

		List<COREntry> corEntries = _corEntryLocalService.getCOREntries(
			cpDefinitionOptionRel.getCompanyId(), true,
			COREntryConstants.TYPE_PRODUCTS_LIMIT, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		for (COREntry corEntry : corEntries) {
			if (!corEntryType.evaluate(corEntry, corEntryTypeItems)) {
				if (infoMessage == null) {
					infoMessage = corEntryType.getErrorMessage(
						corEntry, null, dtoConverterContext.getLocale());
				}

				UnicodeProperties typeSettingsUnicodeProperties =
					UnicodePropertiesBuilder.fastLoad(
						corEntry.getTypeSettings()
					).build();

				cProductExternalReferenceCodes.addAll(
					StringUtil.split(
						typeSettingsUnicodeProperties.getProperty(
							COREntryConstants.
								TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_EXTERNAL_REFERENCE_CODES)));
			}
		}

		if (cProductExternalReferenceCodes.contains(
				cProductExternalReferenceCode)) {

			return infoMessage;
		}

		return null;
	}

	private String _getCPDefinitionLinkInfoMessage(
			List<CommerceOptionValue> commerceOptionValues,
			String cpDefinitionOptionValueRelKey, CPDefinition cpDefinition,
			DTOConverterContext dtoConverterContext)
		throws Exception {

		String languageId = _language.getLanguageId(
			dtoConverterContext.getLocale());

		for (CommerceOptionValue commerceOptionValue : commerceOptionValues) {
			if (Objects.equals(
					commerceOptionValue.getOptionValueKey(),
					cpDefinitionOptionValueRelKey) ||
				(commerceOptionValue.getCPInstanceId() <= 0)) {

				continue;
			}

			CPInstance commerceOptionValueCPInstance =
				_cpInstanceLocalService.getCPInstance(
					commerceOptionValue.getCPInstanceId());

			List<CPDefinitionLink> incompatibleInBundleCPDefinitionLinks =
				_cpDefinitionLinkLocalService.getCPDefinitionLinks(
					commerceOptionValueCPInstance.getCPDefinitionId(),
					CPDefinitionLinkTypeConstants.INCOMPATIBLE_IN_BUNDLE,
					WorkflowConstants.STATUS_APPROVED);

			for (CPDefinitionLink cpDefinitionLink :
					incompatibleInBundleCPDefinitionLinks) {

				CProduct cProduct = cpDefinitionLink.getCProduct();

				if (cpDefinition.getCPDefinitionId() ==
						cProduct.getPublishedCPDefinitionId()) {

					CPDefinition linkedCPDefinition =
						cpDefinitionLink.getCPDefinition();

					return _language.format(
						dtoConverterContext.getLocale(),
						"x-cannot-be-combined-with-x",
						new String[] {
							cpDefinition.getName(languageId),
							linkedCPDefinition.getName(languageId)
						});
				}
			}
		}

		List<CPDefinitionLink> requiresInBundleCPDefinitionLinks =
			_cpDefinitionLinkLocalService.getCPDefinitionLinks(
				cpDefinition.getCPDefinitionId(),
				CPDefinitionLinkTypeConstants.REQUIRES_IN_BUNDLE,
				WorkflowConstants.STATUS_APPROVED);

		for (CPDefinitionLink cpDefinitionLink :
				requiresInBundleCPDefinitionLinks) {

			CProduct cProduct = cpDefinitionLink.getCProduct();

			List<Long> cpDefinitionIds = TransformUtil.transform(
				ListUtil.filter(
					TransformUtil.transform(
						commerceOptionValues,
						commerceOptionValue ->
							_cpInstanceLocalService.fetchCPInstance(
								commerceOptionValue.getCPInstanceId())),
					Objects::nonNull),
				CPInstanceModel::getCPDefinitionId);

			if (!cpDefinitionIds.contains(
					cProduct.getPublishedCPDefinitionId())) {

				CPDefinition linkedCPDefinition =
					_cpDefinitionLocalService.getCPDefinition(
						cProduct.getPublishedCPDefinitionId());

				return _language.format(
					dtoConverterContext.getLocale(),
					"x-requires-x-to-be-purchased-also",
					new String[] {
						cpDefinition.getName(languageId),
						linkedCPDefinition.getName(languageId)
					});
			}
		}

		return null;
	}

	private String _getCProductExternalReferenceCode(CPDefinition cpDefinition)
		throws Exception {

		CProduct cProduct = cpDefinition.getCProduct();

		return cProduct.getExternalReferenceCode();
	}

	private BigDecimal _getMinOrderQuantity(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpInstance == null) {
			return BigDecimal.ZERO;
		}

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					cpInstance.getCPDefinitionId());

		CPDefinitionInventoryEngine cpDefinitionInventoryEngine =
			_cpDefinitionInventoryEngineRegistry.getCPDefinitionInventoryEngine(
				cpDefinitionInventory);

		return cpDefinitionInventoryEngine.getMinOrderQuantity(
			cpConfigurationListId, cpInstance);
	}

	private CPDefinitionOptionValueRel _getSelectedCPDefinitionOptionValueRel(
			CPDefinitionOptionRel cpDefinitionOptionRel, JSONArray jsonArray)
		throws Exception {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String keyValue = jsonObject.getString("key");

			if (!Objects.equals(cpDefinitionOptionRel.getKey(), keyValue)) {
				continue;
			}

			String value = jsonObject.getString("value");

			JSONArray jsonArray1 = _jsonFactory.createJSONArray(value);

			return _cpDefinitionOptionValueRelLocalService.
				fetchCPDefinitionOptionValueRel(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					String.valueOf(jsonArray1.get(0)));
		}

		return null;
	}

	private JSONArray _getSelectedSkuOptionsJSONArray(
		CPDefinitionOptionRel cpDefinitionOptionRel,
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
		SkuOption[] skuOptions) {

		if (skuOptions == null) {
			return null;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (SkuOption skuOption : skuOptions) {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			if (Objects.equals(
					cpDefinitionOptionRel.getKey(),
					skuOption.getSkuOptionKey())) {

				jsonObject.put(
					"key", skuOption.getSkuOptionKey()
				).put(
					"value", cpDefinitionOptionValueRel.getKey()
				);
			}
			else {
				jsonObject.put(
					"key", skuOption.getSkuOptionKey()
				).put(
					"value", skuOption.getSkuOptionValueKey()
				);
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private String _getTimeZoneId(String[] parts) {
		if (parts.length <= 8) {
			return parts[7].toUpperCase();
		}

		String timeZoneId = StringBundler.concat(
			com.liferay.portal.kernel.util.StringUtil.upperCaseFirstLetter(
				parts[7]),
			StringPool.FORWARD_SLASH,
			com.liferay.portal.kernel.util.StringUtil.upperCaseFirstLetter(
				parts[8]));

		if ((parts.length > 9) && Validator.isNotNull(parts[9])) {
			return StringBundler.concat(
				timeZoneId, StringPool.UNDERLINE,
				com.liferay.portal.kernel.util.StringUtil.upperCaseFirstLetter(
					parts[9]));
		}

		return timeZoneId;
	}

	private boolean _updateJSONArray(
		String key, String value, JSONArray jsonArray1) {

		for (int i = 0; i < jsonArray1.length(); i++) {
			JSONObject jsonObject = jsonArray1.getJSONObject(i);

			String keyValue = jsonObject.getString("key");

			if (!Objects.equals(key, keyValue)) {
				continue;
			}

			JSONArray jsonArray2 = _jsonFactory.createJSONArray();

			jsonObject.put("value", jsonArray2.put(value));

			return true;
		}

		return false;
	}

	@Reference
	private CommerceOptionValueHelper _commerceOptionValueHelper;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private COREntryLocalService _corEntryLocalService;

	@Reference
	private COREntryTypeRegistry _corEntryTypeRegistry;

	@Reference
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}