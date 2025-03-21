/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.click.to.chat.web.internal.configuration.admin.display;

import com.liferay.click.to.chat.web.internal.configuration.ClickToChatConfiguration;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Abelenda
 */
@Component(service = ConfigurationScreen.class)
public class ClickToChatPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new ClickToChatPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.click.to.chat.web)")
	private ServletContext _servletContext;

	private class ClickToChatPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "click-to-chat";
		}

		@Override
		public String getJspPath() {
			return "/portal_settings/click_to_chat.jsp";
		}

		@Override
		public String getKey() {
			return "click-to-chat";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "click-to-chat-configuration-name");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/click_to_chat/save_company_configuration";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			ClickToChatConfiguration clickToChatConfiguration = null;

			try {
				clickToChatConfiguration =
					_configurationProvider.getCompanyConfiguration(
						ClickToChatConfiguration.class,
						CompanyThreadLocal.getCompanyId());
			}
			catch (PortalException portalException) {
				ReflectionUtil.throwException(portalException);
			}

			httpServletRequest.setAttribute(
				ClickToChatConfiguration.class.getName(),
				clickToChatConfiguration);
		}

	}

}