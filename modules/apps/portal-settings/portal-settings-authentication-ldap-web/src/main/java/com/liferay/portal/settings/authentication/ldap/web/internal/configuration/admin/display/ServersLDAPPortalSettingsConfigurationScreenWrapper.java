/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.configuration.admin.display;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class ServersLDAPPortalSettingsConfigurationScreenWrapper
	extends BaseLDAPPortalSettingsConfigurationScreenWrapper {

	public ServersLDAPPortalSettingsConfigurationScreenWrapper(
		PortalSettingsConfigurationScreenFactory
			portalSettingsConfigurationScreenFactory,
		ExtendedObjectClassDefinition.Scope scope,
		ServletContext servletContext) {

		super(portalSettingsConfigurationScreenFactory, scope);

		_servletContext = servletContext;
	}

	@Override
	protected PortalSettingsConfigurationScreenContributor
		getPortalSettingsConfigurationScreenContributor() {

		return new ServersLDAPPortalSettingsConfigurationScreenContributor();
	}

	private final ServletContext _servletContext;

	private class ServersLDAPPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "ldap";
		}

		@Override
		public String getJspPath() {
			return "/dynamic_include/com.liferay.portal.settings.web/ldap" +
				"/servers.jsp";
		}

		@Override
		public String getKey() {
			return StringBundler.concat(
				LDAPServerConfiguration.class.getName(), StringPool.POUND,
				getScope());
		}

		@Override
		public String getName(Locale locale) {
			return "servers";
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