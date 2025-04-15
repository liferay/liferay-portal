/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.site.admin.web.internal.constants.SiteAdminWebKeys;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class SiteSettingsConfigurationScreen implements ConfigurationScreen {

	public SiteSettingsConfigurationScreen(
		SiteSettingsConfigurationScreenContributor
			siteSettingsConfigurationScreenContributor,
		ServletContext servletContext) {

		_siteSettingsConfigurationScreenContributor =
			siteSettingsConfigurationScreenContributor;
		_servletContext = servletContext;
	}

	@Override
	public String getCategoryKey() {
		return _siteSettingsConfigurationScreenContributor.getCategoryKey();
	}

	@Override
	public String getKey() {
		return _siteSettingsConfigurationScreenContributor.getKey();
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(
			locale,
			_siteSettingsConfigurationScreenContributor.getName(locale));
	}

	@Override
	public String getScope() {
		return ExtendedObjectClassDefinition.Scope.GROUP.getValue();
	}

	@Override
	public boolean isVisible() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		return _siteSettingsConfigurationScreenContributor.isVisible(
			themeDisplay.getSiteGroup());
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			httpServletRequest.setAttribute(
				SiteAdminWebKeys.SITE_SETTINGS_CONFIGURATION_SCREEN_CONTRIBUTOR,
				_siteSettingsConfigurationScreenContributor);

			_siteSettingsConfigurationScreenContributor.setAttributes(
				httpServletRequest, httpServletResponse);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/configuration/screen/entry.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			throw new IOException(
				"Unable to render /configuration/screen/entry.jsp",
				servletException);
		}
	}

	private final ServletContext _servletContext;
	private final SiteSettingsConfigurationScreenContributor
		_siteSettingsConfigurationScreenContributor;

}