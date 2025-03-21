/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.defaultpermissions.web.internal.display.context.CompanyViewPortalDefaultPermissionsConfigurationDisplayContext;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResourceRegistry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ConfigurationScreen.class)
public class DefaultPermissionsPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new DefaultPermissionsPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private Language _language;

	@Reference
	private PortalDefaultPermissionsModelResourceRegistry
		_portalDefaultPermissionsModelResourceRegistry;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.defaultpermissions.web)"
	)
	private ServletContext _servletContext;

	private class DefaultPermissionsPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "default-permissions";
		}

		@Override
		public String getJspPath() {
			return "/configuration" +
				"/view_portal_default_permissions_configuration.jsp";
		}

		@Override
		public String getKey() {
			return "default-permissions-company-configuration";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "default-permissions");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return null;
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new CompanyViewPortalDefaultPermissionsConfigurationDisplayContext(
					httpServletRequest, _language,
					_portalDefaultPermissionsModelResourceRegistry));
		}

	}

}