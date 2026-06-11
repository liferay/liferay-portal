/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal.servlet;

import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.helper.CommercePaymentHttpHelper;
import com.liferay.commerce.payment.method.authorize.net.internal.AuthorizeNetCommercePaymentMethod;
import com.liferay.commerce.payment.method.authorize.net.internal.configuration.AuthorizeNetGroupServiceConfiguration;
import com.liferay.commerce.payment.method.authorize.net.internal.constants.AuthorizeNetCommercePaymentMethodConstants;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/" + AuthorizeNetCommercePaymentMethodConstants.COMPLETE_PAYMENT_SERVLET_PATH,
		"osgi.http.whiteboard.servlet.name=com.liferay.commerce.payment.method.authorize.net.internal.servlet.CompletePaymentAuthorizeNetServlet",
		"osgi.http.whiteboard.servlet.pattern=/" + AuthorizeNetCommercePaymentMethodConstants.COMPLETE_PAYMENT_SERVLET_PATH + "/*"
	},
	service = Servlet.class
)
public class CompletePaymentAuthorizeNetServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			if (PortalSessionThreadLocal.getHttpSession() == null) {
				PortalSessionThreadLocal.setHttpSession(
					httpServletRequest.getSession());
			}

			URL portalURL = new URL(_portal.getPortalURL(httpServletRequest));

			String redirect = ParamUtil.getString(
				httpServletRequest, "redirect");

			URL url = new URL(redirect);

			if (!Objects.equals(portalURL.getHost(), url.getHost())) {
				throw new ServletException();
			}

			CommerceOrder commerceOrder =
				_commercePaymentHttpHelper.getCommerceOrder(httpServletRequest);

			if (!Objects.equals(
					commerceOrder.getCommercePaymentMethodKey(),
					AuthorizeNetCommercePaymentMethod.KEY)) {

				throw new ServletException();
			}

			boolean cancel = ParamUtil.getBoolean(httpServletRequest, "cancel");

			if (cancel) {
				_commercePaymentEngine.cancelPayment(
					commerceOrder.getCommerceOrderId(), null,
					httpServletRequest);
			}
			else if (commerceOrder.getPaymentStatus() !=
						CommerceOrderPaymentConstants.STATUS_COMPLETED) {

				_commercePaymentEngine.completePayment(
					commerceOrder.getCommerceOrderId(), null,
					httpServletRequest);
			}

			httpServletResponse.sendRedirect(redirect);
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);
		}
	}

	@Override
	protected void doPost(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			ServletInputStream servletInputStream =
				httpServletRequest.getInputStream();

			byte[] bytes = servletInputStream.readAllBytes();

			JSONObject jsonObject = null;

			try {
				jsonObject = _jsonFactory.createJSONObject(
					new String(bytes, StandardCharsets.UTF_8));
			}
			catch (JSONException jsonException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to parse the Authorize.net webhook payload",
						jsonException);
				}

				httpServletResponse.setStatus(
					HttpServletResponse.SC_BAD_REQUEST);

				return;
			}

			String eventType = jsonObject.getString("eventType");

			if (!Objects.equals(
					eventType,
					AuthorizeNetCommercePaymentMethodConstants.
						AUTH_CAPTURE_CREATED_EVENT_TYPE)) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Ignoring unsupported Authorize.net webhook event " +
							"type " + eventType);
				}

				httpServletResponse.setStatus(HttpServletResponse.SC_OK);

				return;
			}

			JSONObject payloadJSONObject = jsonObject.getJSONObject("payload");

			if (payloadJSONObject == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("The Authorize.net webhook payload is missing");
				}

				httpServletResponse.setStatus(
					HttpServletResponse.SC_BAD_REQUEST);

				return;
			}

			CommerceOrder commerceOrder =
				_commerceOrderLocalService.fetchCommerceOrder(
					GetterUtil.getLong(
						payloadJSONObject.getString("invoiceNumber")));

			if (commerceOrder == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to find the commerce order for the " +
							"Authorize.net webhook");
				}

				httpServletResponse.setStatus(
					HttpServletResponse.SC_UNAUTHORIZED);

				return;
			}

			AuthorizeNetGroupServiceConfiguration
				authorizeNetGroupServiceConfiguration =
					_configurationProvider.getConfiguration(
						AuthorizeNetGroupServiceConfiguration.class,
						new GroupServiceSettingsLocator(
							commerceOrder.getGroupId(),
							AuthorizeNetCommercePaymentMethodConstants.
								SERVICE_NAME));

			if (!_isValidSignature(
					bytes, httpServletRequest.getHeader("X-ANET-Signature"),
					authorizeNetGroupServiceConfiguration.signatureKey())) {

				_log.error(
					"Unable to verify the Authorize.net webhook signature");

				httpServletResponse.setStatus(
					HttpServletResponse.SC_UNAUTHORIZED);

				return;
			}

			String transactionId = payloadJSONObject.getString("id");

			if (commerceOrder.getPaymentStatus() ==
					CommerceOrderPaymentConstants.STATUS_COMPLETED) {

				commerceOrder.setTransactionId(transactionId);

				commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
					commerceOrder);
			}
			else {
				_commercePaymentEngine.completePayment(
					commerceOrder.getCommerceOrderId(), transactionId,
					httpServletRequest);
			}

			httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		}
		catch (Exception exception) {
			_log.error("Unable to process Authorize.net webhook", exception);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private String _generateHMAC(byte[] bytes, String signatureKey)
		throws Exception {

		Mac mac = Mac.getInstance("HmacSHA512");

		mac.init(
			new SecretKeySpec(
				signatureKey.getBytes(StandardCharsets.UTF_8),
				mac.getAlgorithm()));

		return StringUtil.bytesToHexString(mac.doFinal(bytes));
	}

	private boolean _isValidSignature(
			byte[] bytes, String signatureHeader, String signatureKey)
		throws Exception {

		if (Validator.isNull(signatureHeader) ||
			Validator.isNull(signatureKey)) {

			return false;
		}

		String generatedSignature = StringUtil.toLowerCase(
			_generateHMAC(bytes, signatureKey));
		String headerSignature = StringUtil.toLowerCase(
			StringUtil.extractLast(signatureHeader, CharPool.EQUAL));

		return MessageDigest.isEqual(
			generatedSignature.getBytes(StandardCharsets.UTF_8),
			headerSignature.getBytes(StandardCharsets.UTF_8));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompletePaymentAuthorizeNetServlet.class);

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommercePaymentEngine _commercePaymentEngine;

	@Reference
	private CommercePaymentHttpHelper _commercePaymentHttpHelper;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}