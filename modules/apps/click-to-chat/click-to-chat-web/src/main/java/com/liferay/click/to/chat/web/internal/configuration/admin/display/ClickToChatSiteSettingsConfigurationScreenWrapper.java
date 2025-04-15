/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.click.to.chat.web.internal.configuration.admin.display;

import com.liferay.click.to.chat.web.internal.configuration.ClickToChatConfiguration;
import com.liferay.click.to.chat.web.internal.configuration.ClickToChatConfigurationUtil;
import com.liferay.click.to.chat.web.internal.constants.ClickToChatWebKeys;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = ConfigurationScreen.class)
public class ClickToChatSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new ClickToChatSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.click.to.chat.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class ClickToChatSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "click-to-chat";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/click_to_chat.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-click-to-chat";
		}

		@Override
		public String getName(Locale locale) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());

			return _language.get(resourceBundle, "click-to-chat");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/click_to_chat/save_site_configuration";
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

			ClickToChatConfiguration clickToChatConfiguration =
				ClickToChatConfigurationUtil.getClickToChatConfiguration(
					themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId());

			httpServletRequest.setAttribute(
				ClickToChatConfiguration.class.getName(),
				ClickToChatConfigurationUtil.getClickToChatConfiguration(
					themeDisplay.getCompanyId(), 0));

			httpServletRequest.setAttribute(
				ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_ACCOUNT_ID,
				clickToChatConfiguration.chatProviderAccountId());
			httpServletRequest.setAttribute(
				ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_ID,
				clickToChatConfiguration.chatProviderId());
			httpServletRequest.setAttribute(
				ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_KEY_ID,
				clickToChatConfiguration.chatProviderKeyId());
			httpServletRequest.setAttribute(
				ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_SECRET_KEY,
				clickToChatConfiguration.chatProviderSecretKey());
			httpServletRequest.setAttribute(
				ClickToChatWebKeys.CLICK_TO_CHAT_ENABLED,
				clickToChatConfiguration.enabled());
			httpServletRequest.setAttribute(
				ClickToChatWebKeys.CLICK_TO_CHAT_GUEST_USERS_ALLOWED,
				clickToChatConfiguration.guestUsersAllowed());
			httpServletRequest.setAttribute(
				ClickToChatWebKeys.CLICK_TO_CHAT_HIDE_IN_CONTROL_PANEL,
				clickToChatConfiguration.hideInControlPanel());
		}

	}

}