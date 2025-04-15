/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.portlet.action;

import com.liferay.commerce.checkout.web.internal.display.context.CheckoutDisplayContext;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.util.CommerceCheckoutStepRegistry;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balázs Breier
 */
@Component(
	property = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CHECKOUT,
	service = ConfigurationAction.class
)
public class CommerceCheckoutConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			CheckoutDisplayContext checkoutDisplayContext =
				new CheckoutDisplayContext(
					_commerceCheckoutStepRegistry, _configurationProvider,
					_portal.getLiferayPortletRequest(
						(PortletRequest)httpServletRequest.getAttribute(
							JavaConstants.JAVAX_PORTLET_REQUEST)),
					_portal.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAVAX_PORTLET_RESPONSE)),
					_portal);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, checkoutDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.UPDATE)) {
			PortletPreferences portletPreferences =
				actionRequest.getPreferences();

			portletPreferences.setValue(
				"orderSummaryShowFullAddress",
				getParameter(actionRequest, "orderSummaryShowFullAddress"));
			portletPreferences.setValue(
				"orderSummaryShowPhoneNumber",
				getParameter(actionRequest, "orderSummaryShowPhoneNumber"));

			portletPreferences.store();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCheckoutConfigurationAction.class);

	@Reference
	private CommerceCheckoutStepRegistry _commerceCheckoutStepRegistry;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}