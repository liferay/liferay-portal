/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Albert Gomes Cabral
 * @author Renato Rego
 */
@Component(service = ConfigurationScreen.class)
public class
	LocalePrependFriendlyURLStylePortalSettingsConfigurationScreenWrapper
		extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new LocalePrependFriendlyURLStylePortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.web)"
	)
	private ServletContext _servletContext;

	private class
		LocalePrependFriendlyURLStylePortalSettingsConfigurationScreenContributor
			implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "localization";
		}

		@Override
		public String getJspPath() {
			return "/locale_prepend_friendly_url_style.jsp";
		}

		@Override
		public String getKey() {
			return "locale-prepend-friendly-url-style";
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/portal_settings/edit_company";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			httpServletRequest.setAttribute(
				PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE,
				PrefsPropsUtil.getInteger(
					themeDisplay.getCompanyId(),
					PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE));
		}

	}

}