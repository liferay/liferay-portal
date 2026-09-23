/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal;

import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.mercanet.internal.configuration.MercanetGroupServiceConfiguration;
import com.liferay.commerce.payment.method.mercanet.internal.connector.Environment;
import com.liferay.commerce.payment.method.mercanet.internal.connector.PaypageClient;
import com.liferay.commerce.payment.method.mercanet.internal.constants.MercanetCommercePaymentMethodConstants;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.result.CommercePaymentResult;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.security.key.secret.SecretResolver;

import com.worldline.sips.model.CaptureMode;
import com.worldline.sips.model.Currency;
import com.worldline.sips.model.InitializationResponse;
import com.worldline.sips.model.OrderChannel;
import com.worldline.sips.model.PaymentRequest;
import com.worldline.sips.model.RedirectionStatusCode;

import java.math.BigDecimal;

import java.net.URL;
import java.net.URLEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "commerce.payment.engine.method.key=" + MercanetCommercePaymentMethod.KEY,
	service = CommercePaymentMethod.class
)
public class MercanetCommercePaymentMethod implements CommercePaymentMethod {

	public static final String KEY = "mercanet";

	@Override
	public CommercePaymentResult cancelPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		MercanetCommercePaymentRequest mercanetCommercePaymentRequest =
			(MercanetCommercePaymentRequest)commercePaymentRequest;

		return new CommercePaymentResult(
			mercanetCommercePaymentRequest.getTransactionId(),
			mercanetCommercePaymentRequest.getCommerceOrderId(),
			CommerceOrderPaymentConstants.STATUS_CANCELLED, false, null, null,
			Collections.emptyList(), true);
	}

	@Override
	public CommercePaymentResult completePayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		MercanetCommercePaymentRequest mercanetCommercePaymentRequest =
			(MercanetCommercePaymentRequest)commercePaymentRequest;

		return new CommercePaymentResult(
			mercanetCommercePaymentRequest.getTransactionId(),
			mercanetCommercePaymentRequest.getCommerceOrderId(),
			CommerceOrderPaymentConstants.STATUS_COMPLETED, false, null, null,
			Collections.emptyList(), true);
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			_getResourceBundle(locale), "mercanet-description");
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(_getResourceBundle(locale), KEY);
	}

	@Override
	public int getPaymentType() {
		return CommercePaymentMethodConstants.TYPE_ONLINE_REDIRECT;
	}

	@Override
	public String getServletPath() {
		return MercanetCommercePaymentMethodConstants.SERVLET_PATH;
	}

	@Override
	public boolean isCancelEnabled() {
		return true;
	}

	@Override
	public boolean isCompleteEnabled() {
		return true;
	}

	@Override
	public boolean isProcessPaymentEnabled() {
		return true;
	}

	@Override
	public CommercePaymentResult processPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		MercanetCommercePaymentRequest mercanetCommercePaymentRequest =
			(MercanetCommercePaymentRequest)commercePaymentRequest;

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			mercanetCommercePaymentRequest.getCommerceOrderId());

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		if (!Objects.equals(commerceCurrency.getCode(), "EUR")) {
			throw new Exception("Mercanet accepts only EUR currency");
		}

		PaymentRequest paymentRequest = new PaymentRequest();

		int normalizedMultiplier = (int)Math.pow(
			10, commerceCurrency.getMaxFractionDigits());

		BigDecimal orderTotal = commerceOrder.getTotal();

		BigDecimal normalizedOrderTotal = orderTotal.multiply(
			new BigDecimal(normalizedMultiplier));

		paymentRequest.setAmount(normalizedOrderTotal.intValue());

		URL returnURL = new URL(mercanetCommercePaymentRequest.getReturnUrl());

		Map<String, String[]> parameters = HttpComponentsUtil.getParameterMap(
			returnURL.getQuery());

		URL baseURL = new URL(
			returnURL.getProtocol(), returnURL.getHost(), returnURL.getPort(),
			returnURL.getPath());

		URL automaticURL = new URL(
			StringBundler.concat(
				baseURL, "?groupId=", parameters.get("groupId")[0],
				"&type=automatic&uuid=", parameters.get("uuid")[0]));

		paymentRequest.setAutomaticResponseUrl(automaticURL);

		paymentRequest.setCaptureMode(CaptureMode.IMMEDIATE);
		paymentRequest.setCurrencyCode(Currency.EUR);
		paymentRequest.setCustomerId(String.valueOf(commerceOrder.getUserId()));

		StringBundler normalURLSB = new StringBundler(4);

		normalURLSB.append(baseURL.toString());
		normalURLSB.append("?redirect=");

		String encodeURL = URLCodec.encodeURL(parameters.get("redirect")[0]);

		normalURLSB.append(encodeURL);

		normalURLSB.append("&type=normal");

		URL normalURL = new URL(normalURLSB.toString());

		paymentRequest.setNormalReturnUrl(normalURL);

		paymentRequest.setOrderChannel(OrderChannel.INTERNET);
		paymentRequest.setOrderId(
			String.valueOf(commerceOrder.getCommerceOrderId()));

		String transactionUuid = PortalUUIDUtil.generate();

		String transactionId = StringUtil.replace(
			transactionUuid, CharPool.DASH, StringPool.BLANK);

		paymentRequest.setTransactionReference(transactionId);

		MercanetGroupServiceConfiguration mercanetGroupServiceConfiguration =
			_getMercanetGroupServiceConfiguration(commerceOrder.getGroupId());

		String environment = StringUtil.toUpperCase(
			mercanetGroupServiceConfiguration.environment());

		String keyVersion = mercanetGroupServiceConfiguration.keyVersion();

		PaypageClient paypageClient = new PaypageClient(
			Environment.valueOf(environment),
			mercanetGroupServiceConfiguration.merchantId(),
			Integer.valueOf(keyVersion),
			_secretResolver.resolve(
				commerceOrder.getCompanyId(),
				mercanetGroupServiceConfiguration.secretKey()));

		InitializationResponse initializationResponse =
			paypageClient.initialize(paymentRequest);

		List<String> resultMessage = Collections.singletonList(
			initializationResponse.getRedirectionStatusMessage());

		RedirectionStatusCode responseCode =
			initializationResponse.getRedirectionStatusCode();

		if (!Objects.equals(responseCode.getCode(), "00")) {
			return new CommercePaymentResult(
				transactionId, commerceOrder.getCommerceOrderId(),
				CommerceOrderPaymentConstants.STATUS_FAILED, true, null, null,
				resultMessage, false);
		}

		String url = StringBundler.concat(
			_getServletUrl(mercanetCommercePaymentRequest), "?redirectURL=",
			URLCodec.encodeURL(
				String.valueOf(initializationResponse.getRedirectionUrl())),
			"&redirectionData=",
			URLEncoder.encode(
				initializationResponse.getRedirectionData(), StringPool.UTF8),
			"&seal=",
			URLEncoder.encode(
				initializationResponse.getSeal(), StringPool.UTF8));

		return new CommercePaymentResult(
			transactionId, commerceOrder.getCommerceOrderId(),
			CommerceOrderPaymentConstants.STATUS_AUTHORIZED, true, url, null,
			resultMessage, true);
	}

	private MercanetGroupServiceConfiguration
			_getMercanetGroupServiceConfiguration(long groupId)
		throws Exception {

		return _configurationProvider.getConfiguration(
			MercanetGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId, MercanetCommercePaymentMethodConstants.SERVICE_NAME));
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private String _getServletUrl(
		MercanetCommercePaymentRequest mercanetCommercePaymentRequest) {

		return StringBundler.concat(
			_portal.getPortalURL(
				mercanetCommercePaymentRequest.getHttpServletRequest()),
			_portal.getPathModule(), StringPool.SLASH,
			MercanetCommercePaymentMethodConstants.SERVLET_PATH);
	}

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SecretResolver _secretResolver;

}