/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.site.map.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;
import com.liferay.site.navigation.site.map.web.internal.constants.SiteNavigationSiteMapPortletKeys;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "jakarta.portlet.name=" + SiteNavigationSiteMapPortletKeys.SITE_NAVIGATION_SITE_MAP,
	service = ConfigurationAction.class
)
public class SiteNavigationSiteMapConfigurationAction
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

		httpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Reference
	private ItemSelector _itemSelector;

}