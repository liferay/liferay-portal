/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal.servlet;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.method.mercanet.internal.MercanetCommercePaymentMethod;
import com.liferay.commerce.payment.method.mercanet.internal.configuration.MercanetGroupServiceConfiguration;
import com.liferay.commerce.payment.method.mercanet.internal.connector.Environment;
import com.liferay.commerce.payment.method.mercanet.internal.connector.PaypageClient;
import com.liferay.commerce.payment.method.mercanet.internal.constants.MercanetCommercePaymentMethodConstants;
import com.liferay.commerce.payment.util.CommercePaymentHttpHelper;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import com.worldline.sips.model.PaypageResponse;
import com.worldline.sips.model.ResponseCode;
import com.worldline.sips.model.ResponseData;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/" + MercanetCommercePaymentMethodConstants.SERVLET_PATH,
		"osgi.http.whiteboard.servlet.name=com.liferay.commerce.payment.method.mercanet.internal.servlet.MercanetServlet",
		"osgi.http.whiteboard.servlet.pattern=/" + MercanetCommercePaymentMethodConstants.SERVLET_PATH + "/*"
	},
	service = Servlet.class
)
public class MercanetServlet extends HttpServlet {

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

			// Handle initializing permission checker for guests

			_commercePaymentHttpHelper.getCommerceOrder(httpServletRequest);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/mercanet_form/mercanet_form.jsp");

			requestDispatcher.forward(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			String type = ParamUtil.getString(httpServletRequest, "type");

			String data = ParamUtil.getString(httpServletRequest, "Data");

			Map<String, String> parameterMap = _getResponseParameters(data);

			if (Objects.equals(type, "normal")) {
				if (PortalSessionThreadLocal.getHttpSession() == null) {
					PortalSessionThreadLocal.setHttpSession(
						httpServletRequest.getSession());
				}

				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						_portal.getUser(httpServletRequest)));

				URL portalURL = new URL(
					_portal.getPortalURL(httpServletRequest));

				String redirect = ParamUtil.getString(
					httpServletRequest, "redirect");

				URL url = new URL(redirect);

				if (!Objects.equals(portalURL.getHost(), url.getHost())) {
					throw new ServletException();
				}

				if (!Objects.equals(parameterMap.get("responseCode"), "00")) {
					String orderId = parameterMap.get("orderId");

					long commerceOrderId = GetterUtil.getLong(orderId);

					CommerceOrder commerceOrder =
						_commerceOrderLocalService.getCommerceOrder(
							commerceOrderId);

					if (!Objects.equals(
							commerceOrder.getCommercePaymentMethodKey(),
							MercanetCommercePaymentMethod.KEY)) {

						throw new ServletException();
					}

					String transactionReference = parameterMap.get(
						"transactionReference");

					_commercePaymentEngine.cancelPayment(
						commerceOrderId, transactionReference,
						httpServletRequest);
				}

				httpServletResponse.sendRedirect(redirect);
			}

			if (Objects.equals(type, "automatic")) {
				String uuid = ParamUtil.getString(httpServletRequest, "uuid");
				long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

				CommerceOrder commerceOrder =
					_commerceOrderLocalService.getCommerceOrderByUuidAndGroupId(
						uuid, groupId);

				if (!Objects.equals(
						commerceOrder.getCommercePaymentMethodKey(),
						MercanetCommercePaymentMethod.KEY)) {

					throw new ServletException();
				}

				MercanetGroupServiceConfiguration
					mercanetGroupServiceConfiguration =
						_getMercanetGroupServiceConfiguration(
							commerceOrder.getGroupId());

				String environment = StringUtil.toUpperCase(
					mercanetGroupServiceConfiguration.environment());

				String keyVersion =
					mercanetGroupServiceConfiguration.keyVersion();

				PaypageClient paypageClient = new PaypageClient(
					Environment.valueOf(environment),
					mercanetGroupServiceConfiguration.merchantId(),
					Integer.valueOf(keyVersion),
					mercanetGroupServiceConfiguration.secretKey());

				PaypageResponse paypageResponse = paypageClient.decodeResponse(
					HashMapBuilder.put(
						"Data", data
					).put(
						"Seal", ParamUtil.getString(httpServletRequest, "Seal")
					).build());

				ResponseData responseData = paypageResponse.getData();

				ResponseCode responseCode = responseData.getResponseCode();

				if (Objects.equals(responseCode.getCode(), "00") &&
					Objects.equals(
						responseData.getMerchantId(),
						mercanetGroupServiceConfiguration.merchantId()) &&
					Objects.equals(
						parameterMap.get("customerId"),
						String.valueOf(commerceOrder.getUserId())) &&
					Objects.equals(
						parameterMap.get("orderId"),
						String.valueOf(commerceOrder.getCommerceOrderId())) &&
					Objects.equals(
						responseData.getTransactionReference(),
						commerceOrder.getTransactionId())) {

					_commercePaymentEngine.completePayment(
						commerceOrder.getCommerceOrderId(),
						commerceOrder.getTransactionId(), httpServletRequest);
				}
			}
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);
		}
	}

	private MercanetGroupServiceConfiguration
			_getMercanetGroupServiceConfiguration(Long groupId)
		throws ConfigurationException {

		return _configurationProvider.getConfiguration(
			MercanetGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId, MercanetCommercePaymentMethodConstants.SERVICE_NAME));
	}

	private Map<String, String> _getResponseParameters(String data) {
		Map<String, String> map = new HashMap<>();

		String[] params = data.split(StringPool.BACK_SLASH + StringPool.PIPE);

		for (String param : params) {
			String[] kvp = StringUtil.split(param, CharPool.EQUAL);

			if (kvp.length < 2) {
				map.put(kvp[0], StringPool.BLANK);
			}
			else {
				map.put(kvp[0], kvp[1]);
			}
		}

		return map;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MercanetServlet.class);

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommercePaymentEngine _commercePaymentEngine;

	@Reference
	private CommercePaymentHttpHelper _commercePaymentHttpHelper;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.payment.method.mercanet)"
	)
	private ServletContext _servletContext;

}