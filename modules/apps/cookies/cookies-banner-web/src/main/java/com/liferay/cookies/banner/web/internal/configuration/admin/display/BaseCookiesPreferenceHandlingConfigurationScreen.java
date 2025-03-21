/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.cookies.banner.web.internal.constants.CookiesBannerWebKeys;
import com.liferay.cookies.banner.web.internal.display.context.CookiesPreferenceHandlingConfigurationDisplayContext;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
public abstract class BaseCookiesPreferenceHandlingConfigurationScreen
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "cookies";
	}

	@Override
	public String getKey() {
		return "cookie-preference-handling-configuration-" + getScope();
	}

	@Override
	public String getName(Locale locale) {
		return language.get(
			locale, "cookie-preference-handling-configuration-name");
	}

	@Override
	public boolean isVisible() {
		return FeatureFlagManagerUtil.isEnabled("LPD-10588");
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			ExtendedObjectClassDefinition.Scope scope =
				ExtendedObjectClassDefinition.Scope.getScope(getScope());

			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.
					COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT,
				new CookiesPreferenceHandlingConfigurationDisplayContext(
					cookiesConfigurationProvider, httpServletRequest,
					portal.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAVAX_PORTLET_RESPONSE)),
					scope, _getScopePK(httpServletRequest, scope)));

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(
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
	protected CookiesConfigurationProvider cookiesConfigurationProvider;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.cookies.banner.web)"
	)
	protected ServletContext servletContext;

	private long _getScopePK(
		HttpServletRequest httpServletRequest,
		ExtendedObjectClassDefinition.Scope scope) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			return themeDisplay.getCompanyId();
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			return themeDisplay.getScopeGroupId();
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			return 0L;
		}

		throw new IllegalArgumentException("Unsupported scope: " + scope);
	}

}