/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.configuration.admin.display;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.web.internal.constants.AnalyticsSettingsWebKeys;
import com.liferay.analytics.settings.web.internal.user.AnalyticsUsersManager;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
public abstract class BaseAnalyticsConfigurationScreen
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "analytics-cloud";
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(locale, getKey());
	}

	@Override
	public String getScope() {
		return "company";
	}

	@Override
	public boolean isVisible() {
		return FeatureFlagManagerUtil.isEnabled("LRAC-10757");
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			ServletContext servletContext = getServletContext();

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(getJspPath());

			_setHttpServletRequestAttributes(httpServletRequest);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render " + getJspPath(), exception);
		}
	}

	protected abstract String getJspPath();

	protected abstract ServletContext getServletContext();

	@Reference
	protected AnalyticsUsersManager analyticsUsersManager;

	@Reference
	protected ConfigurationProvider configurationProvider;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.analytics.settings.web)(release.schema.version>=1.0.1))"
	)
	protected Release release;

	private void _setHttpServletRequestAttributes(
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		httpServletRequest.setAttribute(
			AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION,
			configurationProvider.getCompanyConfiguration(
				AnalyticsConfiguration.class, themeDisplay.getCompanyId()));

		httpServletRequest.setAttribute(
			AnalyticsSettingsWebKeys.ANALYTICS_USERS_MANAGER,
			analyticsUsersManager);
	}

}