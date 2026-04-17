/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.internal;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.exception.CommerceShippingEngineException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.internal.configuration.FunctionCommerceShippingEngineConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	configurationPid = "com.liferay.commerce.shipping.engine.internal.configuration.FunctionCommerceShippingEngineConfiguration",
	service = CommerceShippingEngine.class
)
public class FunctionCommerceShippingEngine implements CommerceShippingEngine {

	@Override
	public String getCommerceShippingOptionLabel(String name, Locale locale) {
		try {
			JSONObject jsonObject = _getJSONObject(
				JSONUtil.put(
					"bcp47LanguageId", _language.getBCP47LanguageId(locale)
				).put(
					"locale", locale
				).put(
					"name", name
				),
				"option-label");

			return jsonObject.getString("name");
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return StringPool.BLANK;
	}

	@Override
	public List<CommerceShippingOption> getCommerceShippingOptions(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			Locale locale)
		throws CommerceShippingEngineException {

		try {
			return _getCommerceShippingOptions(
				commerceOrder,
				_getJSONObject(
					_getPayloadJSONObject(commerceContext, commerceOrder),
					"options"));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Collections.emptyList();
	}

	@Override
	public String getDescription(Locale locale) {
		try {
			JSONObject jsonObject = _getJSONObject(
				JSONUtil.put("locale", locale), "description");

			return jsonObject.getString("description");
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return StringPool.BLANK;
	}

	@Override
	public List<CommerceShippingOption> getEnabledCommerceShippingOptions(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			Locale locale)
		throws CommerceShippingEngineException {

		try {
			return _getCommerceShippingOptions(
				commerceOrder,
				_getJSONObject(
					_getPayloadJSONObject(commerceContext, commerceOrder),
					"options-enabled"));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Collections.emptyList();
	}

	@Override
	public String getKey() {
		return _functionCommerceShippingEngineConfiguration.key();
	}

	@Override
	public String getName(Locale locale) {
		try {
			JSONObject jsonObject = _getJSONObject(
				JSONUtil.put("locale", locale), "name");

			return jsonObject.getString("name");
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return _functionCommerceShippingEngineConfiguration.name();
	}

	public UnicodeProperties getTypeSettingsUnicodeProperties() {
		return UnicodePropertiesBuilder.create(
			true
		).putAll(
			ObjectMapperUtil.readValue(
				Map.class,
				_functionCommerceShippingEngineConfiguration.
					shippingEngineTypeSettings())
		).build();
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_functionCommerceShippingEngineConfiguration =
			ConfigurableUtil.createConfigurable(
				FunctionCommerceShippingEngineConfiguration.class, properties);
	}

	@Deactivate
	protected void deactivate() {
		_functionCommerceShippingEngineConfiguration = null;
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_functionCommerceShippingEngineConfiguration =
			ConfigurableUtil.createConfigurable(
				FunctionCommerceShippingEngineConfiguration.class, properties);
	}

	private JSONObject _getCommerceOrderJSONObject(CommerceOrder commerceOrder)
		throws Exception {

		DTOConverter<?, ?> commerceOrderDTOConverter =
			_dtoConverterRegistry.getDTOConverter(
				"Liferay.Headless.Commerce.Admin.Order",
				CommerceOrder.class.getName(), "v1.0");

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, commerceOrder.getCommerceOrderId(),
				LocaleUtil.getSiteDefault(), null, null);

		dtoConverterContext.setAttribute("secure", Boolean.FALSE);

		JSONObject commerceOrderJSONObject = _jsonFactory.createJSONObject(
			String.valueOf(
				commerceOrderDTOConverter.toDTO(dtoConverterContext)));

		DTOConverter<?, ?> accountEntryDTOConverter =
			_dtoConverterRegistry.getDTOConverter(
				"Liferay.Headless.Commerce.Admin.Order",
				"com.liferay.account.model.AccountEntry", "v1.0");

		dtoConverterContext = new DefaultDTOConverterContext(
			_dtoConverterRegistry, commerceOrder.getCommerceAccountId(),
			LocaleUtil.getSiteDefault(), null, null);

		dtoConverterContext.setAttribute("secure", Boolean.FALSE);

		JSONObject accountEntryJSONObject = _jsonFactory.createJSONObject(
			_jsonFactory.looseSerializeDeep(
				accountEntryDTOConverter.toDTO(dtoConverterContext)));

		commerceOrderJSONObject.put("account", accountEntryJSONObject);

		JSONArray commerceOrderItemsJSONArray = _jsonFactory.createJSONArray();

		DTOConverter<?, ?> commerceOrderItemDTOConverter =
			_dtoConverterRegistry.getDTOConverter(
				"Liferay.Headless.Commerce.Admin.Order",
				CommerceOrderItem.class.getName(), "v1.0");

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			dtoConverterContext = new DefaultDTOConverterContext(
				_dtoConverterRegistry,
				commerceOrderItem.getCommerceOrderItemId(),
				LocaleUtil.getSiteDefault(), null, null);

			dtoConverterContext.setAttribute("secure", Boolean.FALSE);

			JSONObject commerceOrderItemJSONObject =
				_jsonFactory.createJSONObject(
					_jsonFactory.looseSerializeDeep(
						commerceOrderItemDTOConverter.toDTO(
							dtoConverterContext)));

			commerceOrderItemsJSONArray.put(commerceOrderItemJSONObject);
		}

		commerceOrderJSONObject.put("orderItems", commerceOrderItemsJSONArray);

		if (commerceOrder.getShippingAddressId() == 0) {
			return commerceOrderJSONObject;
		}

		DTOConverter<?, ?> commerceShippingAddressDTOConverter =
			_dtoConverterRegistry.getDTOConverter(
				"Liferay.Headless.Commerce.Admin.Order", "ShippingAddress",
				"v1.0");

		dtoConverterContext = new DefaultDTOConverterContext(
			_dtoConverterRegistry, commerceOrder.getShippingAddressId(),
			LocaleUtil.getSiteDefault(), null, null);

		dtoConverterContext.setAttribute("secure", Boolean.FALSE);

		JSONObject commerceShippingAddressJSONObject =
			_jsonFactory.createJSONObject(
				_jsonFactory.looseSerializeDeep(
					commerceShippingAddressDTOConverter.toDTO(
						dtoConverterContext)));

		commerceOrderJSONObject.put(
			"shippingAddress", commerceShippingAddressJSONObject);

		return commerceOrderJSONObject;
	}

	private List<CommerceShippingOption> _getCommerceShippingOptions(
		CommerceOrder commerceOrder, JSONObject jsonObject) {

		List<CommerceShippingOption> commerceShippingOptions =
			new ArrayList<>();

		JSONArray shippingOptionJSONArray = jsonObject.getJSONArray(
			"shippingOptions");

		shippingOptionJSONArray.forEach(
			object -> {
				JSONObject shippingOptionJSONObject = (JSONObject)object;

				BigDecimal amount = new BigDecimal(
					shippingOptionJSONObject.getString("amount", "0"));

				String currencyCode = shippingOptionJSONObject.getString(
					"currencyCode");

				if (Validator.isNotNull(currencyCode)) {
					try {
						CommerceCurrency commerceCurrency =
							_commerceCurrencyLocalService.getCommerceCurrency(
								commerceOrder.getCompanyId(), currencyCode);

						String commerceCurrencyCode =
							commerceCurrency.getCode();

						if (!StringUtil.equals(
								commerceCurrencyCode,
								commerceOrder.getCommerceCurrencyCode())) {

							CommerceCurrency commerceOrderCommerceCurrency =
								commerceOrder.getCommerceCurrency();

							BigDecimal rate =
								commerceOrderCommerceCurrency.getRate();

							amount = amount.multiply(
								rate.divide(
									commerceCurrency.getRate(),
									RoundingMode.HALF_UP));
						}
					}
					catch (Exception exception) {
						_log.error(exception);
					}
				}

				commerceShippingOptions.add(
					new CommerceShippingOption(
						amount, getKey(),
						shippingOptionJSONObject.getString("key"),
						shippingOptionJSONObject.getString("name"),
						shippingOptionJSONObject.getDouble("priority")));
			});

		return commerceShippingOptions;
	}

	private JSONObject _getJSONObject(
			JSONObject payloadJSONObject, String resourcePath)
		throws Exception {

		User currentUser = _userService.getCurrentUser();

		return _jsonFactory.createJSONObject(
			new String(
				_portalCatapult.launch(
					currentUser.getCompanyId(), Http.Method.POST,
					_functionCommerceShippingEngineConfiguration.
						oAuth2ApplicationExternalReferenceCode(),
					payloadJSONObject, "/" + resourcePath,
					currentUser.getUserId()
				).get()));
	}

	private JSONObject _getPayloadJSONObject(
			CommerceContext commerceContext, CommerceOrder commerceOrder)
		throws Exception {

		JSONObject typeSettingsJSONObject = _jsonFactory.createJSONObject();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				commerceContext.getCommerceChannelId());

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.fetchCommerceShippingMethod(
				commerceChannel.getGroupId(), getKey());

		UnicodeProperties typeSettingsUnicodeProperties =
			commerceShippingMethod.getTypeSettingsUnicodeProperties();

		typeSettingsUnicodeProperties.forEach(
			(key, value) -> typeSettingsJSONObject.put(key, value));

		return JSONUtil.put(
			"order", _getCommerceOrderJSONObject(commerceOrder)
		).put(
			"typeSettings", typeSettingsJSONObject
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FunctionCommerceShippingEngine.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	private volatile FunctionCommerceShippingEngineConfiguration
		_functionCommerceShippingEngineConfiguration;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private PortalCatapult _portalCatapult;

	@Reference
	private UserService _userService;

}