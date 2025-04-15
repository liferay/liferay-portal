/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.address.content.web.internal.portlet.action;

import com.liferay.commerce.address.content.web.internal.display.context.CommerceAddressDisplayContext;
import com.liferay.commerce.address.content.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ADDRESS_CONTENT,
	service = ConfigurationAction.class
)
public class CommerceAddressContentConfigurationAction
	extends BaseConfigurationAction {

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
			CommerceAddressDisplayContext commerceAddressDisplayContext =
				new CommerceAddressDisplayContext(
					_actionHelper, _commerceAccountHelper,
					_commerceAddressService, _countryService,
					_groupLocalService, httpServletRequest, _regionService);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, commerceAddressDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAddressContentConfigurationAction.class);

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CountryService _countryService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private RegionService _regionService;

}