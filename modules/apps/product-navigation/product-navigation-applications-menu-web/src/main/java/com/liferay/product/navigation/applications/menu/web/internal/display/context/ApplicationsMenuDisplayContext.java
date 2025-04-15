/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.product.navigation.applications.menu.web.internal.constants.ProductNavigationApplicationsMenuPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class ApplicationsMenuDisplayContext {

	public ApplicationsMenuDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public Map<String, Object> getApplicationsMenuComponentData()
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"liferayLogoURL",
			() -> {
				LiferayPortletURL applicationsMenuLiferayLogoURL =
					PortletURLFactoryUtil.create(
						_httpServletRequest,
						ProductNavigationApplicationsMenuPortletKeys.
							PRODUCT_NAVIGATION_APPLICATIONS_MENU,
						PortletRequest.RESOURCE_PHASE);

				applicationsMenuLiferayLogoURL.setResourceID(
					"/applications_menu/liferay_logo");

				return applicationsMenuLiferayLogoURL.toString();
			}
		).put(
			"liferayName",
			GetterUtil.getString(
				PropsValues.APPLICATIONS_MENU_DEFAULT_LIFERAY_NAME, "Liferay")
		).put(
			"panelAppsURL",
			() -> {
				LiferayPortletURL applicationsMenuPanelAppsURL =
					PortletURLFactoryUtil.create(
						_httpServletRequest,
						ProductNavigationApplicationsMenuPortletKeys.
							PRODUCT_NAVIGATION_APPLICATIONS_MENU,
						PortletRequest.RESOURCE_PHASE);

				applicationsMenuPanelAppsURL.setResourceID(
					"/applications_menu/panel_apps");
				applicationsMenuPanelAppsURL.setParameter(
					"selectedPortletId", themeDisplay.getPpid());

				return applicationsMenuPanelAppsURL.toString();
			}
		).put(
			"selectedPortletId", themeDisplay.getPpid()
		).put(
			"virtualInstance",
			() -> {
				Company company = themeDisplay.getCompany();

				return HashMapBuilder.<String, Object>put(
					"label", company.getName()
				).put(
					"logoURL",
					StringBundler.concat(
						themeDisplay.getPathImage(), "/company_logo?img_id=",
						company.getLogoId(), "&t=",
						WebServerServletTokenUtil.getToken(company.getLogoId()))
				).put(
					"url",
					PortalUtil.addPreservedParameters(
						themeDisplay, themeDisplay.getURLPortal(), false, true)
				).build();
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;

}