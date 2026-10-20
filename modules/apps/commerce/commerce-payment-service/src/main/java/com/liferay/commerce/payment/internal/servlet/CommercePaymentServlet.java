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
import com.liferay.commerce.payment.helper.CommercePaymentHelper;
import com.liferay.commerce.payment.helper.CommercePaymentHttpHelper;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.result.CommercePaymentResult;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
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
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
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
import jakarta.servlet.http.HttpSession;

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

		CommerceOrder commerceOrder = null;

		try {
			if (PortalSessionThreadLocal.getHttpSession() == null) {
				PortalSessionThreadLocal.setHttpSession(
					httpServletRequest.getSession());
			}

			URL portalURL = new URL(_portal.getPortalURL(httpServletRequest));

			long commerceChannelId = 0;
			CommercePaymentEntry commercePaymentEntry = null;
			CommercePaymentIntegration commercePaymentIntegration = null;
			String redirect = null;

			long entryId = ParamUtil.getLong(httpServletRequest, "entryId");
			String entryKey = ParamUtil.getString(
				httpServletRequest, "entryKey");

			if ((entryId == 0) && Validator.isBlank(entryKey)) {
				commerceOrder = _commercePaymentHttpHelper.getCommerceOrder(
					httpServletRequest);

				HttpSession httpSession = httpServletRequest.getSession();

				httpSession.setAttribute(
					_getSessionAttributeName(
						commerceOrder.getCommerceOrderId()),
					Boolean.TRUE);

				CommerceChannel commerceChannel =
					_commerceChannelLocalService.
						getCommerceChannelByOrderGroupId(
							commerceOrder.getGroupId());

				commerceChannelId = commerceChannel.getCommerceChannelId();

				commercePaymentIntegration =
					_commercePaymentHelper.getCommercePaymentIntegration(
						commerceChannelId,
						commerceOrder.getCommercePaymentMethodKey());

				redirect = ParamUtil.getString(httpServletRequest, "nextStep");

				URL url = new URL(redirect);

				if (!Objects.equals(portalURL.getHost(), url.getHost())) {
					throw new ServletException();
				}
			}
			else {
				commercePaymentEntry =
					_commercePaymentEntryLocalService.getCommercePaymentEntry(
						entryId);

				if (!Validator.isBlank(entryKey) &&
					!Objects.equals(
						entryKey,
						commercePaymentEntry.getPaymentIntegrationKey())) {

					throw new PrincipalException();
				}

				commerceChannelId = commercePaymentEntry.getCommerceChannelId();

				commercePaymentIntegration =
					_commercePaymentHelper.getCommercePaymentIntegration(
						commerceChannelId,
						commercePaymentEntry.getPaymentIntegrationKey());

				if (commercePaymentIntegration == null) {
					throw new ServletException();
				}

				commerceOrder = _getCommerceOrder(
					commercePaymentEntry, httpServletRequest);

				redirect = _getRedirect(
					commercePaymentEntry, httpServletRequest, portalURL);
			}

			if (commercePaymentIntegration != null) {
				_managePaymentIntegration(
					httpServletRequest, httpServletResponse, commerceChannelId,
					commerceOrder, commercePaymentEntry,
					commercePaymentIntegration, portalURL, redirect);
			}
			else {
				_managePaymentMethod(
					httpServletRequest, httpServletResponse, commerceOrder,
					portalURL, redirect);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			try {
				if (commerceOrder != null) {
					commerceOrder =
						_commerceOrderLocalService.fetchCommerceOrder(
							commerceOrder.getCommerceOrderId());
				}

				if ((commerceOrder != null) &&
					!(commerceOrder.getPaymentStatus() ==
						CommercePaymentEntryConstants.STATUS_COMPLETED)) {

					PermissionThreadLocal.setPermissionChecker(
						PermissionCheckerFactoryUtil.create(
							_getUser(commerceOrder, httpServletRequest)));

					_commercePaymentEngine.updateOrderPaymentStatus(
						commerceOrder.getCommerceOrderId(),
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

	private CommerceOrder _getCommerceOrder(
			CommercePaymentEntry commercePaymentEntry,
			HttpServletRequest httpServletRequest)
		throws Exception {

		if (commercePaymentEntry.getClassNameId() != _portal.getClassNameId(
				CommerceOrder.class)) {

			throw new PrincipalException();
		}

		long commerceOrderId = commercePaymentEntry.getClassPK();

		HttpSession httpSession = httpServletRequest.getSession(false);

		if ((httpSession != null) &&
			GetterUtil.getBoolean(
				httpSession.getAttribute(
					_getSessionAttributeName(commerceOrderId)))) {

			return _commerceOrderLocalService.getCommerceOrder(commerceOrderId);
		}

		if (Validator.isNotNull(
				ParamUtil.getString(httpServletRequest, "guestToken"))) {

			CommerceOrder commerceOrder =
				_commercePaymentHttpHelper.getCommerceOrder(httpServletRequest);

			if (commerceOrder.getCommerceOrderId() != commerceOrderId) {
				throw new PrincipalException();
			}

			return commerceOrder;
		}

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) || user.isGuestUser()) {
			throw new PrincipalException();
		}

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		if (commerceOrder.isGuestOrder()) {
			throw new PrincipalException();
		}

		return commerceOrder;
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

	private String _getRedirect(
			CommercePaymentEntry commercePaymentEntry,
			HttpServletRequest httpServletRequest, URL portalURL)
		throws Exception {

		String redirect = URLCodec.decodeURL(
			HttpComponentsUtil.getParameter(
				commercePaymentEntry.getCallbackURL(), "redirect", false));

		if (Validator.isNotNull(redirect)) {
			URL url = new URL(redirect);

			if (Objects.equals(portalURL.getHost(), url.getHost())) {
				return redirect;
			}
		}

		return _portal.getPortalURL(httpServletRequest);
	}

	private String _getSessionAttributeName(long commerceOrderId) {
		return StringBundler.concat(
			CommercePaymentServlet.class.getName(), StringPool.POUND,
			commerceOrderId);
	}

	private User _getUser(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest)
		throws PortalException {

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) && (commerceOrder != null)) {
			user = _userLocalService.fetchUser(commerceOrder.getUserId());
		}

		return user;
	}

	private void _managePaymentIntegration(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long commerceChannelId,
			CommerceOrder commerceOrder,
			CommercePaymentEntry commercePaymentEntry,
			CommercePaymentIntegration commercePaymentIntegration,
			URL portalURL, String redirect)
		throws Exception {

		if (commercePaymentEntry == null) {
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
					"&orderType=normal", redirect));
			commercePaymentEntry.setCancelURL(
				_getApplicationContextURL(
					commercePaymentEntry.getCommercePaymentEntryId(),
					commercePaymentIntegration.getKey(), httpServletRequest,
					"&cancel=true", redirect));

			commercePaymentEntry =
				_commercePaymentEntryLocalService.updateCommercePaymentEntry(
					_commercePaymentGateway.setUpPayment(
						httpServletRequest, commercePaymentEntry));
		}
		else {
			commercePaymentIntegration.validate(
				httpServletRequest, commercePaymentEntry);

			boolean cancel = ParamUtil.getBoolean(httpServletRequest, "cancel");

			if (cancel) {
				if (commercePaymentEntry.getPaymentStatus() ==
						CommercePaymentEntryConstants.STATUS_CREATED) {

					PermissionThreadLocal.setPermissionChecker(
						PermissionCheckerFactoryUtil.create(
							_getUser(commerceOrder, httpServletRequest)));

					_commercePaymentEngine.updateOrderPaymentStatus(
						commerceOrder.getCommerceOrderId(),
						CommerceOrderPaymentConstants.STATUS_CANCELLED,
						StringPool.BLANK, StringPool.BLANK);

					commercePaymentEntry.setPaymentStatus(
						CommerceOrderPaymentConstants.STATUS_CANCELLED);

					commercePaymentEntry =
						_commercePaymentEntryLocalService.
							updateCommercePaymentEntry(commercePaymentEntry);

					if (ParamUtil.getBoolean(
							httpServletRequest, "redirect", true)) {

						httpServletResponse.sendRedirect(redirect);
					}

					return;
				}

				_commercePaymentGateway.cancel(
					httpServletRequest, commercePaymentEntry);
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
			httpServletResponse.sendRedirect(redirect);

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

						httpServletResponse.sendRedirect(redirect);

						return;
					}
				}
				else {
					_commercePaymentGateway.capture(
						httpServletRequest, commercePaymentEntry);

					httpServletResponse.sendRedirect(redirect);

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

		httpServletResponse.sendRedirect(redirect);
	}

	private void _managePaymentMethod(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			CommerceOrder commerceOrder, URL portalURL, String redirect)
		throws Exception {

		if (_commerceCheckoutStepHttpHelper.isCommercePaymentComplete(
				httpServletRequest, commerceOrder)) {

			_commercePaymentEngine.completePayment(
				commerceOrder.getCommerceOrderId(), null, httpServletRequest);

			httpServletResponse.sendRedirect(redirect);

			return;
		}

		CommercePaymentResult commercePaymentResult = _startPayment(
			commerceOrder.getCommerceOrderId(), httpServletRequest, redirect);

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
				commerceOrder.getCommerceOrderId());

		if ((CommercePaymentMethodConstants.TYPE_OFFLINE ==
				commercePaymentMethodType) ||
			(commercePaymentMethodType == -1)) {

			_commercePaymentEngine.completePayment(
				commerceOrder.getCommerceOrderId(), null, httpServletRequest);

			httpServletResponse.sendRedirect(redirect);
		}

		if (commercePaymentResult.isSuccess() &&
			(CommercePaymentMethodConstants.TYPE_ONLINE_STANDARD ==
				commercePaymentMethodType)) {

			if (commerceOrder.isSubscriptionOrder()) {
				_commerceSubscriptionEngine.completeRecurringPayment(
					commerceOrder.getCommerceOrderId(),
					commercePaymentResult.getAuthTransactionId(),
					httpServletRequest);
			}
			else {
				_commercePaymentEngine.completePayment(
					commerceOrder.getCommerceOrderId(),
					commercePaymentResult.getAuthTransactionId(),
					httpServletRequest);
			}

			httpServletResponse.sendRedirect(redirect);
		}

		if (!commercePaymentResult.isSuccess() &&
			!httpServletResponse.isCommitted()) {

			httpServletResponse.sendRedirect(redirect);
		}
	}

	private CommercePaymentResult _startPayment(
			long commerceOrderId, HttpServletRequest httpServletRequest,
			String redirect)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		if (commerceOrder.isSubscriptionOrder() &&
			!_commercePaymentHelper.isDeliveryOnlySubscription(commerceOrder)) {

			return _commerceSubscriptionEngine.processRecurringPayment(
				commerceOrderId, redirect, httpServletRequest);
		}

		return _commercePaymentEngine.processPayment(
			commerceOrderId, redirect, httpServletRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentServlet.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCheckoutStepHttpHelper _commerceCheckoutStepHttpHelper;

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

	@Reference
	private CommerceSubscriptionEngine _commerceSubscriptionEngine;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}