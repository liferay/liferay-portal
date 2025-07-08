/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.cookies.banner.web.internal.constants.CookiesBannerWebKeys;
import com.liferay.cookies.banner.web.internal.display.context.CookiesPreferenceHandlingConfigurationDisplayContext;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(service = ConfigurationFormRenderer.class)
public class CookiesPreferenceHandlingConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return CookiesPreferenceHandlingConfiguration.class.getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"enabled", ParamUtil.getBoolean(httpServletRequest, "enabled")
		).put(
			"explicitConsentMode",
			ParamUtil.getBoolean(httpServletRequest, "explicitConsentMode")
		).build();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			String portletId = PortalUtil.getPortletId(
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST));

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (portletId.equals(
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)) {

				httpServletRequest.setAttribute(
					CookiesBannerWebKeys.
						COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT,
					new CookiesPreferenceHandlingConfigurationDisplayContext(
						_cookiesConfigurationProvider,
						ExtendedObjectClassDefinition.Scope.COMPANY,
						themeDisplay.getCompanyId()));
			}
			else if (portletId.equals(
						ConfigurationAdminPortletKeys.SITE_SETTINGS)) {

				httpServletRequest.setAttribute(
					CookiesBannerWebKeys.
						COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT,
					new CookiesPreferenceHandlingConfigurationDisplayContext(
						_cookiesConfigurationProvider,
						ExtendedObjectClassDefinition.Scope.GROUP,
						themeDisplay.getScopeGroupId()));
			}
			else {
				httpServletRequest.setAttribute(
					CookiesBannerWebKeys.
						COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT,
					new CookiesPreferenceHandlingConfigurationDisplayContext(
						_cookiesConfigurationProvider,
						ExtendedObjectClassDefinition.Scope.SYSTEM, 0L));
			}

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/cookies_preference_handling_configuration/view.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render /cookies_preference_handling_configuration" +
					"/view.jsp",
				exception);
		}
	}

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.cookies.banner.web)"
	)
	private ServletContext _servletContext;

}