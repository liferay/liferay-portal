/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurationScreen.class)
public class ExportLDAPPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new ExportLDAPPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.authentication.ldap.web)"
	)
	private ServletContext _servletContext;

	private class ExportLDAPPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "ldap";
		}

		@Override
		public String getJspPath() {
			return "/dynamic_include/com.liferay.portal.settings.web/ldap" +
				"/export.jsp";
		}

		@Override
		public String getKey() {
			return LDAPExportConfiguration.class.getName();
		}

		@Override
		public String getName(Locale locale) {
			return "export";
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/portal_settings_authentication_ldap/ldap_form";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

	}

}