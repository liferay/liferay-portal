/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.servlet;

import com.liferay.commerce.checkout.helper.CommerceCheckoutStepHttpHelper;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.constants.CommercePaymentIntegrationConstants;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.engine.CommerceSubscriptionEngine;
import com.liferay.commerce.payment.gateway.CommercePaymentGateway;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.result.CommercePaymentResult;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
import com.liferay.commerce.payment.util.CommercePaymentHelper;
import com.liferay.commerce.payment.util.CommercePaymentHttpHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/" + CommercePaymentMethodConstants.SERVLET_PATH,
		"osgi.http.whiteboard.servlet.name=com.liferay.commerce.payment.internal.servlet.CommercePaymentServlet",
		"osgi.http.whiteboard.servlet.pattern=/" + CommercePaymentMethodConstants.SERVLET_PATH + "/*"
	},
	service = Servlet.class
)
public class CommercePaymentServlet extends HttpServlet {

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

			String entryKey = ParamUtil.getString(
				httpServletRequest, "entryKey");

			if (Validator.isBlank(entryKey)) {
				CommerceOrder commerceOrder =
					_commercePaymentHttpHelper.getCommerceOrder(
						httpServletRequest);

				_commerceOrderId = commerceOrder.getCommerceOrderId();

				CommerceChannel commerceChannel =
					_commerceChannelLocalService.
						getCommerceChannelByOrderGroupId(
							commerceOrder.getGroupId());

				_commerceChannelId = commerceChannel.getCommerceChannelId();

				_commercePaymentIntegration =
					_commercePaymentHelper.getCommercePaymentIntegration(
						_commerceChannelId,
						commerceOrder.getCommercePaymentMethodKey());
			}
			else {
				_commercePaymentIntegration =
					_commercePaymentHelper.getCommercePaymentIntegration(
						_commerceChannelId, entryKey);
			}

			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(_commerceOrderId);

			if (_commercePaymentIntegration != null) {
				_managePaymentIntegration(
					httpServletRequest, httpServletResponse, _commerceChannelId,
					commerceOrder, _commercePaymentIntegration, portalURL);
			}
			else {
				_managePaymentMethod(
					httpServletRequest, httpServletResponse, commerceOrder,
					portalURL);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			try {
				CommerceOrder commerceOrder =
					_commerceOrderLocalService.fetchCommerceOrder(
						_commerceOrderId);

				if ((commerceOrder != null) &&
					!(commerceOrder.getPaymentStatus() ==
						CommercePaymentEntryConstants.STATUS_COMPLETED)) {

					PermissionThreadLocal.setPermissionChecker(
						PermissionCheckerFactoryUtil.create(
							_portal.getUser(httpServletRequest)));

					_commercePaymentEngine.updateOrderPaymentStatus(
						_commerceOrderId,
						CommerceOrderPaymentConstants.STATUS_FAILED,
						StringPool.BLANK, StringPool.BLANK);
				}

				httpServletResponse.sendRedirect(
					_portal.getPortalURL(httpServletRequest));
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
	}

	private String _getApplicationContextURL(
		long entryId, String entryKey, HttpServletRequest httpServletRequest,
		String queryString, String redirect) {

		StringBundler sb = new StringBundler(13);

		sb.append(_portal.getPortalURL(httpServletRequest));
		sb.append(_portal.getPathModule());
		sb.append(CharPool.SLASH);
		sb.append(CommercePaymentMethodConstants.SERVLET_PATH);
		sb.append(CharPool.QUESTION);

		if (Validator.isNotNull(redirect)) {
			sb.append("redirect=");
			sb.append(URLCodec.encodeURL(redirect));
			sb.append(CharPool.AMPERSAND);
		}

		sb.append("entryId=");
		sb.append(entryId);
		sb.append("&entryKey=");
		sb.append(entryKey);
		sb.append(queryString);

		return sb.toString();
	}

	private Map<String, String> _getQueryMap(String query) {
		String[] params = query.split(StringPool.AMPERSAND);

		Map<String, String> map = new HashMap<>();

		for (String param : params) {
			String name = param.split(StringPool.EQUAL)[0];
			String value = param.split(StringPool.EQUAL)[1];

			map.put(
				StringUtil.toUpperCase(
					CamelCaseUtil.fromCamelCase(name, CharPool.UNDERLINE)),
				value);
		}

		return map;
	}

	private void _managePaymentIntegration(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long commerceChannelId,
			CommerceOrder commerceOrder,
			CommercePaymentIntegration commercePaymentIntegration,
			URL portalURL)
		throws Exception {

		long entryId = ParamUtil.getLong(httpServletRequest, "entryId");

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				entryId);

		if (commercePaymentEntry == null) {
			_redirect = ParamUtil.getString(httpServletRequest, "nextStep");

			URL url = new URL(_redirect);

			if (!Objects.equals(portalURL.getHost(), url.getHost())) {
				throw new ServletException();
			}

			User currentUser = _portal.getUser(httpServletRequest);

			if (currentUser == null) {
				currentUser = _userLocalService.getUserById(
					commerceOrder.getUserId());
			}

			CommerceCurrency commerceCurrency =
				commerceOrder.getCommerceCurrency();

			commercePaymentEntry =
				_commercePaymentEntryLocalService.addCommercePaymentEntry(
					currentUser.getUserId(),
					_portal.getClassNameId(CommerceOrder.class),
					commerceOrder.getCommerceOrderId(), commerceChannelId,
					commerceOrder.getTotal(), null, null,
					commerceCurrency.getCode(),
					_language.getLanguageId(httpServletRequest), null, null,
					commerceOrder.getCommercePaymentMethodKey(),
					commercePaymentIntegration.getPaymentIntegrationType(),
					null, null, CommercePaymentEntryConstants.TYPE_PAYMENT,
					ServiceContextFactory.getInstance(httpServletRequest));

			commercePaymentEntry.setCallbackURL(
				_getApplicationContextURL(
					commercePaymentEntry.getCommercePaymentEntryId(),
					commercePaymentIntegration.getKey(), httpServletRequest,
					"&orderType=normal", _redirect));
			commercePaymentEntry.setCancelURL(
				_getApplicationContextURL(
					commercePaymentEntry.getCommercePaymentEntryId(),
					commercePaymentIntegration.getKey(), httpServletRequest,
					"&cancel=true", _redirect));

			commercePaymentEntry =
				_commercePaymentEntryLocalService.updateCommercePaymentEntry(
					_commercePaymentGateway.setUpPayment(
						httpServletRequest, commercePaymentEntry));
		}
		else {
			boolean cancel = ParamUtil.getBoolean(httpServletRequest, "cancel");

			if (cancel) {
				if (commercePaymentEntry.getPaymentStatus() ==
						CommercePaymentEntryConstants.STATUS_CREATED) {

					PermissionThreadLocal.setPermissionChecker(
						PermissionCheckerFactoryUtil.create(
							_portal.getUser(httpServletRequest)));

					_commercePaymentEngine.updateOrderPaymentStatus(
						_commerceOrderId,
						CommerceOrderPaymentConstants.STATUS_CANCELLED,
						StringPool.BLANK, StringPool.BLANK);

					commercePaymentEntry.setPaymentStatus(
						CommerceOrderPaymentConstants.STATUS_CANCELLED);

					commercePaymentEntry =
						_commercePaymentEntryLocalService.
							updateCommercePaymentEntry(commercePaymentEntry);

					if (ParamUtil.getBoolean(
							httpServletRequest, "redirect", true)) {

						httpServletResponse.sendRedirect(_redirect);
					}

					return;
				}

				_commercePaymentGateway.cancel(
					httpServletRequest, commercePaymentEntry);
			}

			String token = ParamUtil.getString(httpServletRequest, "token");

			if (!Validator.isBlank(token)) {
				commercePaymentEntry.setTransactionCode(token);
			}

			if (commercePaymentEntry.getPaymentStatus() ==
					CommercePaymentEntryConstants.STATUS_CREATED) {

				commercePaymentEntry = _commercePaymentGateway.authorize(
					httpServletRequest, commercePaymentEntry);
			}
			else if (commercePaymentEntry.getPaymentStatus() ==
						CommercePaymentEntryConstants.STATUS_AUTHORIZED) {

				commercePaymentEntry = _commercePaymentGateway.capture(
					httpServletRequest, commercePaymentEntry);
			}
		}

		int paymentStatus = commercePaymentEntry.getPaymentStatus();

		if (CommercePaymentEntryConstants.STATUS_FAILED == paymentStatus) {
			httpServletResponse.sendRedirect(_redirect);

			return;
		}

		if (((commercePaymentIntegration.getPaymentIntegrationType() ==
				CommercePaymentIntegrationConstants.
					TYPE_INTERNAL_ONLINE_REDIRECT) ||
			 (commercePaymentIntegration.getPaymentIntegrationType() ==
				 CommercePaymentIntegrationConstants.
					 TYPE_FUNCTION_ONLINE_REDIRECT)) &&
			((CommercePaymentEntryConstants.STATUS_CREATED == paymentStatus) ||
			 (CommercePaymentEntryConstants.STATUS_AUTHORIZED ==
				 paymentStatus))) {

			if (Validator.isNull(commercePaymentEntry.getRedirectURL())) {
				if (CommercePaymentEntryConstants.STATUS_CREATED ==
						paymentStatus) {

					if (!ParamUtil.getBoolean(
							httpServletRequest, "redirect", true)) {

						return;
					}

					commercePaymentEntry = _commercePaymentGateway.authorize(
						httpServletRequest, commercePaymentEntry);

					if (CommercePaymentEntryConstants.STATUS_FAILED ==
							commercePaymentEntry.getPaymentStatus()) {

						httpServletResponse.sendRedirect(_redirect);

						return;
					}
				}
				else {
					_commercePaymentGateway.capture(
						httpServletRequest, commercePaymentEntry);

					httpServletResponse.sendRedirect(_redirect);

					return;
				}
			}

			URL redirectURL = new URL(commercePaymentEntry.getRedirectURL());

			if (Objects.equals(portalURL.getHost(), redirectURL.getHost())) {
				Map<String, String> paramsMap = _getQueryMap(
					redirectURL.getQuery());

				Set<Map.Entry<String, String>> entries = paramsMap.entrySet();

				for (Map.Entry<String, String> param : entries) {
					httpServletRequest.setAttribute(
						param.getKey(), param.getValue());
				}

				RequestDispatcher requestDispatcher =
					httpServletRequest.getRequestDispatcher(
						redirectURL.getPath());

				requestDispatcher.forward(
					httpServletRequest, httpServletResponse);
			}
			else {
				httpServletResponse.sendRedirect(redirectURL.toString());
			}

			return;
		}

		if ((commercePaymentIntegration.getPaymentIntegrationType() ==
				CommercePaymentIntegrationConstants.TYPE_INTERNAL_OFFLINE) ||
			(commercePaymentIntegration.getPaymentIntegrationType() ==
				CommercePaymentIntegrationConstants.TYPE_FUNCTION_OFFLINE) ||
			(commercePaymentIntegration.getPaymentIntegrationType() ==
				CommercePaymentIntegrationConstants.
					TYPE_FUNCTION_ONLINE_STANDARD) ||
			(commercePaymentIntegration.getPaymentIntegrationType() ==
				CommercePaymentIntegrationConstants.
					TYPE_INTERNAL_ONLINE_STANDARD)) {

			if (commercePaymentEntry.getPaymentStatus() ==
					CommercePaymentEntryConstants.STATUS_CREATED) {

				commercePaymentEntry = _commercePaymentGateway.authorize(
					httpServletRequest, commercePaymentEntry);
			}

			if (commercePaymentEntry.getPaymentStatus() ==
					CommercePaymentEntryConstants.STATUS_AUTHORIZED) {

				_commercePaymentGateway.capture(
					httpServletRequest, commercePaymentEntry);
			}
		}

		httpServletResponse.sendRedirect(_redirect);
	}

	private void _managePaymentMethod(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			CommerceOrder commerceOrder, URL portalURL)
		throws Exception {

		_redirect = ParamUtil.getString(httpServletRequest, "nextStep");

		URL url = new URL(_redirect);

		if (!Objects.equals(portalURL.getHost(), url.getHost())) {
			throw new ServletException();
		}

		if (_commerceCheckoutStepHttpHelper.isCommercePaymentComplete(
				httpServletRequest, commerceOrder)) {

			_commercePaymentEngine.completePayment(
				_commerceOrderId, null, httpServletRequest);

			httpServletResponse.sendRedirect(_redirect);

			return;
		}

		CommercePaymentResult commercePaymentResult = _startPayment(
			httpServletRequest);

		if (commercePaymentResult.isSuccess() &&
			commercePaymentResult.isOnlineRedirect()) {

			URL redirectURL = new URL(commercePaymentResult.getRedirectUrl());

			if (Objects.equals(portalURL.getHost(), redirectURL.getHost())) {
				Map<String, String> paramsMap = _getQueryMap(
					redirectURL.getQuery());

				Set<Map.Entry<String, String>> entries = paramsMap.entrySet();

				for (Map.Entry<String, String> param : entries) {
					httpServletRequest.setAttribute(
						param.getKey(), param.getValue());
				}

				RequestDispatcher requestDispatcher =
					httpServletRequest.getRequestDispatcher(
						redirectURL.getPath());

				requestDispatcher.forward(
					httpServletRequest, httpServletResponse);
			}
			else {
				httpServletResponse.sendRedirect(redirectURL.toString());
			}
		}

		// Offline methods, payment complete

		int commercePaymentMethodType =
			_commercePaymentEngine.getCommercePaymentMethodType(
				_commerceOrderId);

		if ((CommercePaymentMethodConstants.TYPE_OFFLINE ==
				commercePaymentMethodType) ||
			(commercePaymentMethodType == -1)) {

			_commercePaymentEngine.completePayment(
				_commerceOrderId, null, httpServletRequest);

			httpServletResponse.sendRedirect(_redirect);
		}

		if (commercePaymentResult.isSuccess() &&
			(CommercePaymentMethodConstants.TYPE_ONLINE_STANDARD ==
				commercePaymentMethodType)) {

			if (commerceOrder.isSubscriptionOrder()) {
				_commerceSubscriptionEngine.completeRecurringPayment(
					_commerceOrderId,
					commercePaymentResult.getAuthTransactionId(),
					httpServletRequest);
			}
			else {
				_commercePaymentEngine.completePayment(
					_commerceOrderId,
					commercePaymentResult.getAuthTransactionId(),
					httpServletRequest);
			}

			httpServletResponse.sendRedirect(_redirect);
		}

		if (!commercePaymentResult.isSuccess() &&
			!httpServletResponse.isCommitted()) {

			httpServletResponse.sendRedirect(_redirect);
		}
	}

	private CommercePaymentResult _startPayment(
			HttpServletRequest httpServletRequest)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			_commerceOrderId);

		if (commerceOrder.isSubscriptionOrder() &&
			!_commercePaymentHelper.isDeliveryOnlySubscription(commerceOrder)) {

			return _commerceSubscriptionEngine.processRecurringPayment(
				_commerceOrderId, _redirect, httpServletRequest);
		}

		return _commercePaymentEngine.processPayment(
			_commerceOrderId, _redirect, httpServletRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentServlet.class);

	private long _commerceChannelId;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCheckoutStepHttpHelper _commerceCheckoutStepHttpHelper;

	private long _commerceOrderId;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommercePaymentEngine _commercePaymentEngine;

	@Reference
	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

	@Reference
	private CommercePaymentGateway _commercePaymentGateway;

	@Reference
	private CommercePaymentHelper _commercePaymentHelper;

	@Reference
	private CommercePaymentHttpHelper _commercePaymentHttpHelper;

	private CommercePaymentIntegration _commercePaymentIntegration;

	@Reference
	private CommerceSubscriptionEngine _commerceSubscriptionEngine;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private String _redirect;

	@Reference
	private UserLocalService _userLocalService;

}