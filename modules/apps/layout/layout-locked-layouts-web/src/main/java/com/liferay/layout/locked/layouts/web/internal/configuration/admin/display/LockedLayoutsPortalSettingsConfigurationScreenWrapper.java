/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.locked.layouts.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.layout.locked.layouts.web.internal.configuration.LockedLayoutsCompanyConfiguration;
import com.liferay.layout.locked.layouts.web.internal.display.context.LockedLayoutsConfigurationDisplayContext;
import com.liferay.layout.locked.layouts.web.internal.display.context.LockedLayoutsPortalSettingsConfigurationDisplayContext;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ConfigurationScreen.class)
public class LockedLayoutsPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new LockedLayoutsPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.locked.layouts.web)"
	)
	private ServletContext _servletContext;

	private class LockedLayoutsPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "pages";
		}

		@Override
		public String getJspPath() {
			return "/configuration/view.jsp";
		}

		@Override
		public String getKey() {
			return "locked-layouts-portal-settings";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "locked-pages");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/instance_settings/save_locked_layouts_instance_settings";
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

			try {
				httpServletRequest.setAttribute(
					LockedLayoutsConfigurationDisplayContext.class.getName(),
					new LockedLayoutsPortalSettingsConfigurationDisplayContext(
						_hasConfiguration(themeDisplay.getCompanyId()),
						_configurationProvider.getCompanyConfiguration(
							LockedLayoutsCompanyConfiguration.class,
							themeDisplay.getCompanyId())));
			}
			catch (PortalException portalException) {
				ReflectionUtil.throwException(portalException);
			}
		}

		private boolean _hasConfiguration(long companyId)
			throws ConfigurationException {

			try {
				String filterString = StringBundler.concat(
					"(&(", ConfigurationAdmin.SERVICE_FACTORYPID,
					StringPool.EQUAL,
					LockedLayoutsCompanyConfiguration.class.getName(),
					".scoped)(companyId=", companyId, "))");

				Configuration[] configuration =
					_configurationAdmin.listConfigurations(filterString);

				return ArrayUtil.isNotEmpty(configuration);
			}
			catch (InvalidSyntaxException | IOException exception) {
				throw new ConfigurationException(exception);
			}
		}

	}

}