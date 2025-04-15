/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.mentions.constants.MentionsWebKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurationScreen.class)
public class MentionsPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new MentionsPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.mentions.web)")
	private ServletContext _servletContext;

	private class MentionsPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "community-tools";
		}

		@Override
		public String getJspPath() {
			return "/portal_settings/mentions.jsp";
		}

		@Override
		public String getKey() {
			return "mentions";
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

			PortletPreferences companyPortletPreferences =
				PrefsPropsUtil.getPreferences(themeDisplay.getCompanyId());

			boolean companyMentionsEnabled = PrefsParamUtil.getBoolean(
				companyPortletPreferences, httpServletRequest,
				"mentionsEnabled", true);

			httpServletRequest.setAttribute(
				MentionsWebKeys.COMPANY_MENTIONS_ENABLED,
				companyMentionsEnabled);
		}

	}

}