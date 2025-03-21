/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.integration;

import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.internal.configuration.FunctionCommercePaymentIntegrationConfiguration;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Crescenzo Rega
 */
@Component(
	configurationPid = "com.liferay.commerce.payment.internal.configuration.FunctionCommercePaymentIntegrationConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = CommercePaymentIntegration.class
)
public class FunctionCommercePaymentIntegration
	implements CommercePaymentIntegration {

	@Override
	public CommercePaymentEntry authorize(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		return _setCommercePaymentEntry(
			httpServletRequest, commercePaymentEntry, "/authorize");
	}

	@Override
	public CommercePaymentEntry cancel(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		return _setCommercePaymentEntry(
			httpServletRequest, commercePaymentEntry, "/cancel");
	}

	@Override
	public CommercePaymentEntry capture(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		return _setCommercePaymentEntry(
			httpServletRequest, commercePaymentEntry, "/capture");
	}

	@Override
	public String getDescription(Locale locale) {
		return _functionCommercePaymentIntegrationConfiguration.name();
	}

	@Override
	public String getKey() {
		return _functionCommercePaymentIntegrationConfiguration.key();
	}

	@Override
	public String getName(Locale locale) {
		return _functionCommercePaymentIntegrationConfiguration.name();
	}

	@Override
	public int getPaymentIntegrationType() {
		return _functionCommercePaymentIntegrationConfiguration.
			paymentIntegrationType();
	}

	@Override
	public UnicodeProperties getPaymentIntegrationTypeSettings() {
		return UnicodePropertiesBuilder.create(
			true
		).putAll(
			ObjectMapperUtil.readValue(
				Map.class,
				_functionCommercePaymentIntegrationConfiguration.
					paymentIntegrationTypeSettings())
		).build();
	}

	@Override
	public CommercePaymentEntry refund(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		return _setCommercePaymentEntry(
			httpServletRequest, commercePaymentEntry, "/refund");
	}

	@Override
	public CommercePaymentEntry setUpPayment(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		return _setCommercePaymentEntry(
			httpServletRequest, commercePaymentEntry, "/set-up-payment");
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_functionCommercePaymentIntegrationConfiguration =
			ConfigurableUtil.createConfigurable(
				FunctionCommercePaymentIntegrationConfiguration.class,
				properties);
	}

	@Deactivate
	protected void deactivate() throws PortalException {
		String key = getKey();

		if (key == null) {
			return;
		}

		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
			_commercePaymentMethodGroupRelLocalService.
				getCommercePaymentMethodGroupRels(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				commercePaymentMethodGroupRels) {

			if (key.equals(
					commercePaymentMethodGroupRel.getPaymentIntegrationKey())) {

				_commercePaymentMethodGroupRelLocalService.
					deleteCommercePaymentMethodGroupRel(
						commercePaymentMethodGroupRel.
							getCommercePaymentMethodGroupRelId());
			}
		}
	}

	@Modified
	protected void modified(Map<String, Object> properties)
		throws PortalException {

		_functionCommercePaymentIntegrationConfiguration =
			ConfigurableUtil.createConfigurable(
				FunctionCommercePaymentIntegrationConfiguration.class,
				properties);

		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
			_commercePaymentMethodGroupRelLocalService.
				getCommercePaymentMethodGroupRels(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				commercePaymentMethodGroupRels) {

			String key = (String)properties.get("key");

			if (key.equals(
					commercePaymentMethodGroupRel.getPaymentIntegrationKey())) {

				_commercePaymentMethodGroupRelLocalService.
					deleteCommercePaymentMethodGroupRel(
						commercePaymentMethodGroupRel.
							getCommercePaymentMethodGroupRelId());
			}
		}
	}

	private JSONObject _getPayloadJSONObject(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				commercePaymentEntry.getCommerceChannelId());

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceChannel.getGroupId(), getKey());

		UnicodeProperties typeSettingsUnicodeProperties =
			commercePaymentMethodGroupRel.getTypeSettingsUnicodeProperties();

		JSONObject typeSettingsJSONObject = _jsonFactory.createJSONObject();

		typeSettingsUnicodeProperties.forEach(typeSettingsJSONObject::put);

		JSONObject commercePaymentEntryJSONObject =
			_jsonFactory.createJSONObject(
				_jsonFactory.looseSerializeDeep(commercePaymentEntry));

		String className = _portal.getClassName(
			commercePaymentEntry.getClassNameId());

		commercePaymentEntryJSONObject.put(
			"className", className
		).put(
			"classNameLabel",
			_language.get(
				ResourceBundleUtil.getBundle(
					"content.Language",
					LocaleUtil.fromLanguageId(
						commercePaymentEntry.getLanguageId()),
					getClass()),
				"model.resource." + className)
		);

		return JSONUtil.put(
			"commercePaymentEntry", commercePaymentEntryJSONObject
		).put(
			"httpServletRequestParameterMap",
			httpServletRequest.getParameterMap()
		).put(
			"typeSettings", typeSettingsJSONObject
		);
	}

	private CommercePaymentEntry _setCommercePaymentEntry(
		HttpServletRequest httpServletRequest,
		CommercePaymentEntry commercePaymentEntry, String resourcePath) {

		try {
			commercePaymentEntry.setPaymentStatus(
				CommercePaymentEntryConstants.STATUS_FAILED);

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				new String(
					_portalCatapult.launch(
						commercePaymentEntry.getCompanyId(), Http.Method.POST,
						_functionCommercePaymentIntegrationConfiguration.
							oAuth2ApplicationExternalReferenceCode(),
						_getPayloadJSONObject(
							httpServletRequest, commercePaymentEntry),
						resourcePath, commercePaymentEntry.getUserId()
					).get()));

			if (jsonObject.has("amount")) {
				commercePaymentEntry.setAmount(
					BigDecimal.valueOf(jsonObject.getDouble("amount")));
			}

			if (jsonObject.has("callbackURL")) {
				commercePaymentEntry.setCallbackURL(
					jsonObject.getString("callbackURL"));
			}

			if (jsonObject.has("cancelURL")) {
				commercePaymentEntry.setCancelURL(
					jsonObject.getString("cancelURL"));
			}

			if (jsonObject.has("errorMessages")) {
				commercePaymentEntry.setErrorMessages(
					jsonObject.getString("errorMessages"));
			}

			if (jsonObject.has("note")) {
				commercePaymentEntry.setNote(jsonObject.getString("note"));
			}

			if (jsonObject.has("payload")) {
				commercePaymentEntry.setPayload(
					jsonObject.getString("payload"));
			}

			if (jsonObject.has("paymentStatus")) {
				commercePaymentEntry.setPaymentStatus(
					jsonObject.getInt("paymentStatus"));
			}

			if (jsonObject.has("redirectURL")) {
				commercePaymentEntry.setRedirectURL(
					jsonObject.getString("redirectURL"));
			}

			if (jsonObject.has("transactionCode")) {
				commercePaymentEntry.setTransactionCode(
					jsonObject.getString("transactionCode"));
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			commercePaymentEntry.setErrorMessages(exception.getMessage());
			commercePaymentEntry.setPaymentStatus(
				CommercePaymentEntryConstants.STATUS_FAILED);
		}

		return commercePaymentEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FunctionCommercePaymentIntegration.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	private volatile FunctionCommercePaymentIntegrationConfiguration
		_functionCommercePaymentIntegrationConfiguration;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortalCatapult _portalCatapult;

}