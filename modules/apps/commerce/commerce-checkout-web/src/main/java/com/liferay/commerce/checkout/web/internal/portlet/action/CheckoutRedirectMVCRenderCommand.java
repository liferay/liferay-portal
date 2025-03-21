/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CHECKOUT,
		"mvc.command.name=/commerce_checkout/checkout_redirect"
	},
	service = MVCRenderCommand.class
)
public class CheckoutRedirectMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(renderRequest));

		HttpServletResponse originalHttpServletResponse =
			_portal.getHttpServletResponse(renderResponse);

		while (originalHttpServletResponse instanceof
					HttpServletResponseWrapper) {

			HttpServletResponseWrapper httpServletResponseWrapper =
				(HttpServletResponseWrapper)originalHttpServletResponse;

			originalHttpServletResponse =
				(HttpServletResponse)httpServletResponseWrapper.getResponse();
		}

		try {
			originalHttpServletResponse.sendRedirect(
				String.valueOf(
					_commerceOrderHttpHelper.getCommerceCheckoutPortletURL(
						originalHttpServletRequest)));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return "/view.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CheckoutRedirectMVCRenderCommand.class);

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private Portal _portal;

}