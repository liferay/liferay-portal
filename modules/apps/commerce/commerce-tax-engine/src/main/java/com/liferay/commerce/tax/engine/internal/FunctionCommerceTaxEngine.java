/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal;

import com.liferay.commerce.exception.CommerceTaxEngineException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.CommerceTaxCalculateRequest;
import com.liferay.commerce.tax.CommerceTaxEngine;
import com.liferay.commerce.tax.CommerceTaxValue;
import com.liferay.commerce.tax.engine.internal.configuration.FunctionCommerceTaxEngineConfiguration;
import com.liferay.commerce.tax.model.CommerceTaxCategoryMapping;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxCategoryMappingLocalService;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	configurationPid = "com.liferay.commerce.tax.engine.internal.configuration.FunctionCommerceTaxEngineConfiguration",
	service = CommerceTaxEngine.class
)
public class FunctionCommerceTaxEngine implements CommerceTaxEngine {

	@Override
	public CommerceTaxValue getCommerceTaxValue(
			CommerceTaxCalculateRequest commerceTaxCalculateRequest)
		throws CommerceTaxEngineException {

		if ((commerceTaxCalculateRequest.getCommerceBillingAddressId() == 0) ||
			(commerceTaxCalculateRequest.getCommerceShippingAddressId() == 0)) {

			return null;
		}

		return _setCommerceTaxCalculateRequest(commerceTaxCalculateRequest);
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
	public String getKey() {
		return _functionCommerceTaxEngineConfiguration.key();
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

		return _functionCommerceTaxEngineConfiguration.name();
	}

	public UnicodeProperties getTypeSettingsUnicodeProperties() {
		return UnicodePropertiesBuilder.create(
			true
		).putAll(
			ObjectMapperUtil.readValue(
				Map.class,
				_functionCommerceTaxEngineConfiguration.taxEngineTypeSettings())
		).build();
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_functionCommerceTaxEngineConfiguration =
			ConfigurableUtil.createConfigurable(
				FunctionCommerceTaxEngineConfiguration.class, properties);
	}

	@Deactivate
	protected void deactivate() {
		_functionCommerceTaxEngineConfiguration = null;
	}

	protected JSONObject getPayloadJSONObject(
			CommerceTaxCalculateRequest commerceTaxCalculateRequest)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceTaxCalculateRequest.getCommerceChannelGroupId());

		CommerceTaxMethod commerceTaxMethod =
			_commerceTaxMethodLocalService.fetchCommerceTaxMethod(
				commerceChannel.getGroupId(), getKey());

		UnicodeProperties typeSettingsUnicodeProperties =
			commerceTaxMethod.getTypeSettingsUnicodeProperties();

		typeSettingsUnicodeProperties.forEach(jsonObject::put);

		return JSONUtil.put(
			"commerceTaxCalculateRequest",
			_getCommerceTaxCalculateRequestJSONObject(
				commerceChannel, commerceTaxCalculateRequest)
		).put(
			"typeSettings", jsonObject
		);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_functionCommerceTaxEngineConfiguration =
			ConfigurableUtil.createConfigurable(
				FunctionCommerceTaxEngineConfiguration.class, properties);
	}

	private JSONObject _getCommerceTaxCalculateRequestJSONObject(
			CommerceChannel commerceChannel,
			CommerceTaxCalculateRequest commerceTaxCalculateRequest)
		throws Exception {

		JSONObject commerceTaxCalculateRequestJSONObject =
			_jsonFactory.createJSONObject();

		DTOConverter<?, ?> billingAddressrDTOConverter =
			_dtoConverterRegistry.getDTOConverter("BillingAddress");

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry,
				commerceTaxCalculateRequest.getCommerceBillingAddressId(),
				LocaleUtil.getSiteDefault(), null, null);

		dtoConverterContext.setAttribute("secure", Boolean.FALSE);

		JSONObject commerceBillingAddressJSONObject =
			_jsonFactory.createJSONObject(
				_jsonFactory.looseSerializeDeep(
					billingAddressrDTOConverter.toDTO(dtoConverterContext)));

		String commerceCurrencyCode =
			commerceTaxCalculateRequest.getCommerceCurrencyCode();

		if (Validator.isNull(commerceCurrencyCode)) {
			commerceCurrencyCode = commerceChannel.getCommerceCurrencyCode();
		}

		commerceTaxCalculateRequestJSONObject.put(
			"billingAddress", commerceBillingAddressJSONObject
		).put(
			"currencyCode", commerceCurrencyCode
		).put(
			"price", commerceTaxCalculateRequest.getPrice()
		);

		DTOConverter<?, ?> shippingAddressrDTOConverter =
			_dtoConverterRegistry.getDTOConverter("ShippingAddress");

		dtoConverterContext = new DefaultDTOConverterContext(
			_dtoConverterRegistry,
			commerceTaxCalculateRequest.getCommerceShippingAddressId(),
			LocaleUtil.getSiteDefault(), null, null);

		dtoConverterContext.setAttribute("secure", Boolean.FALSE);

		JSONObject commerceShippingAddressJSONObject =
			_jsonFactory.createJSONObject(
				_jsonFactory.looseSerializeDeep(
					shippingAddressrDTOConverter.toDTO(dtoConverterContext)));

		commerceTaxCalculateRequestJSONObject.put(
			"shippingAddress", commerceShippingAddressJSONObject);

		CommerceTaxMethod commerceTaxMethod =
			_commerceTaxMethodLocalService.fetchCommerceTaxMethod(
				commerceChannel.getGroupId(), getKey());

		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			_commerceTaxCategoryMappingLocalService.
				fetchCommerceTaxCategoryMapping(
					commerceTaxMethod.getCommerceTaxMethodId(),
					commerceTaxCalculateRequest.getTaxCategoryId());

		if (commerceTaxCategoryMapping != null) {
			commerceTaxCalculateRequestJSONObject.put(
				"taxCode",
				commerceTaxCategoryMapping.getExternalReferenceCode());
		}

		return commerceTaxCalculateRequestJSONObject;
	}

	private JSONObject _getJSONObject(
			JSONObject payloadJSONObject, String resourcePath)
		throws Exception {

		User currentUser = _userService.getCurrentUser();

		return _jsonFactory.createJSONObject(
			new String(
				_portalCatapult.launch(
					currentUser.getCompanyId(), Http.Method.POST,
					_functionCommerceTaxEngineConfiguration.
						oAuth2ApplicationExternalReferenceCode(),
					payloadJSONObject, "/" + resourcePath,
					currentUser.getUserId()
				).get()));
	}

	private CommerceTaxValue _setCommerceTaxCalculateRequest(
		CommerceTaxCalculateRequest commerceTaxCalculateRequest) {

		CommerceTaxValue commerceTaxValue = null;

		try {
			JSONObject jsonObject = _getJSONObject(
				getPayloadJSONObject(commerceTaxCalculateRequest),
				"/calculate-tax");

			BigDecimal rate = BigDecimal.valueOf(
				jsonObject.getDouble("amount"));

			BigDecimal taxValue = rate;

			if (commerceTaxCalculateRequest.isPercentage()) {
				BigDecimal amount = commerceTaxCalculateRequest.getPrice();

				taxValue = amount.multiply(taxValue);

				BigDecimal denominator = _ONE_HUNDRED;

				if (commerceTaxCalculateRequest.isIncludeTax()) {
					denominator = _ONE_HUNDRED.add(rate);
				}

				taxValue = taxValue.divide(
					denominator, _SCALE, RoundingMode.HALF_EVEN);
			}

			commerceTaxValue = new CommerceTaxValue(
				"function", "function", taxValue);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return commerceTaxValue;
	}

	private static final BigDecimal _ONE_HUNDRED = BigDecimal.valueOf(100);

	private static final int _SCALE = 10;

	private static final Log _log = LogFactoryUtil.getLog(
		FunctionCommerceTaxEngine.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceTaxCategoryMappingLocalService
		_commerceTaxCategoryMappingLocalService;

	@Reference
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	private volatile FunctionCommerceTaxEngineConfiguration
		_functionCommerceTaxEngineConfiguration;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PortalCatapult _portalCatapult;

	@Reference
	private UserService _userService;

}