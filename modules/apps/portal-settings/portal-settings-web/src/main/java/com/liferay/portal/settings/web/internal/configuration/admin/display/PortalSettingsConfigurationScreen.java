/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.web.internal.constants.PortalSettingsWebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class PortalSettingsConfigurationScreen implements ConfigurationScreen {

	public PortalSettingsConfigurationScreen(
		PortalSettingsConfigurationScreenContributor
			portalSettingsConfigurationScreenContributor,
		ServletContext servletContext) {

		_portalSettingsConfigurationScreenContributor =
			portalSettingsConfigurationScreenContributor;
		_servletContext = servletContext;
	}

	@Override
	public String getCategoryKey() {
		return _portalSettingsConfigurationScreenContributor.getCategoryKey();
	}

	@Override
	public String getKey() {
		return _portalSettingsConfigurationScreenContributor.getKey();
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(
			locale,
			_portalSettingsConfigurationScreenContributor.getName(locale));
	}

	@Override
	public String getScope() {
		return "company";
	}

	@Override
	public boolean isDeprecated() {
		return _portalSettingsConfigurationScreenContributor.isDeprecated();
	}

	@Override
	public boolean isVisible() {
		return _portalSettingsConfigurationScreenContributor.isVisible();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			httpServletRequest.setAttribute(
				PortalSettingsWebKeys.DELETE_CONFIRMATION_TEXT,
				UnicodeLanguageUtil.get(
					ResourceBundleUtil.getBundle(
						httpServletRequest.getLocale(),
						PortalSettingsConfigurationScreen.class),
					"are-you-sure-you-want-to-reset-the-configured-values"));
			httpServletRequest.setAttribute(
				PortalSettingsWebKeys.
					PORTAL_SETTINGS_CONFIGURATION_SCREEN_CONTRIBUTOR,
				_portalSettingsConfigurationScreenContributor);

			_portalSettingsConfigurationScreenContributor.setAttributes(
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

	private final PortalSettingsConfigurationScreenContributor
		_portalSettingsConfigurationScreenContributor;
	private final ServletContext _servletContext;

}