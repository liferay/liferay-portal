/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.friendly.url.configuration.manager.FriendlyURLSeparatorConfigurationManager;
import com.liferay.friendly.url.web.internal.display.context.FriendlyURLSeparatorCompanyConfigurationDisplayContext;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ConfigurationScreen.class)
public class FriendlyURLSeparatorPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new FriendlyURLSeparatorPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private FriendlyURLSeparatorConfigurationManager
		_friendlyURLSeparatorConfigurationManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.friendly.url.web)")
	private ServletContext _servletContext;

	private class
		FriendlyURLSeparatorPortalSettingsConfigurationScreenContributor
			implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "seo";
		}

		@Override
		public String getJspPath() {
			return "/configuration" +
				"/friendly_url_separator_company_configuration.jsp";
		}

		@Override
		public String getKey() {
			return "friendly-url-separator-company-configuration";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "friendly-url");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/instance_settings" +
				"/friendly_url_separator_save_company_configuration";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public boolean isVisible() {
			return true;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			PortalSettingsConfigurationScreenContributor.super.setAttributes(
				httpServletRequest, httpServletResponse);

			httpServletRequest.setAttribute(
				FriendlyURLSeparatorCompanyConfigurationDisplayContext.class.
					getName(),
				new FriendlyURLSeparatorCompanyConfigurationDisplayContext(
					_friendlyURLSeparatorConfigurationManager,
					httpServletRequest, _jsonFactory, _language, _portal));
		}

	}

}