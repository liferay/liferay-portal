/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal.servlet;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.method.authorize.net.internal.AuthorizeNetCommercePaymentMethod;
import com.liferay.commerce.payment.method.authorize.net.internal.constants.AuthorizeNetCommercePaymentMethodConstants;
import com.liferay.commerce.payment.util.CommercePaymentHttpHelper;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.URL;

import java.util.Objects;

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
			else {
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

	@Reference
	private CommercePaymentEngine _commercePaymentEngine;

	@Reference
	private CommercePaymentHttpHelper _commercePaymentHttpHelper;

	@Reference
	private Portal _portal;

}