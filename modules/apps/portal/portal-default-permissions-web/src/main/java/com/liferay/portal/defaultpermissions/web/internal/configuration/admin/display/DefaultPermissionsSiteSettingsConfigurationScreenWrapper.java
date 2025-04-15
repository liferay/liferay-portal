/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.defaultpermissions.web.internal.display.context.GroupViewPortalDefaultPermissionsConfigurationDisplayContext;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResourceRegistry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

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
public class DefaultPermissionsSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new DefaultPermissionsSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private Language _language;

	@Reference
	private PortalDefaultPermissionsModelResourceRegistry
		_portalDefaultPermissionsModelResourceRegistry;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.defaultpermissions.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class DefaultPermissionsSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

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
			return "default-permissions-group-configuration";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "default-permissions");
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
				new GroupViewPortalDefaultPermissionsConfigurationDisplayContext(
					httpServletRequest, _language,
					_portalDefaultPermissionsModelResourceRegistry));
		}

	}

}