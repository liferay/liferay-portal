/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.opensso.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurationScreen.class)
public class OpenSSOPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	public boolean isDeprecated() {
		return true;
	}

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new OpenSSOPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private Language _language;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.authentication.opensso.web)"
	)
	private ServletContext _servletContext;

	private class OpenSSOPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "sso";
		}

		@Override
		public String getDeleteMVCActionCommandName() {
			return "/portal_settings/opensso_delete";
		}

		@Override
		public String getJspPath() {
			return "/dynamic_include/com.liferay.portal.settings.web" +
				"/opensso.jsp";
		}

		@Override
		public String getKey() {
			return "opensso";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "opensso-configuration-name");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/portal_settings/opensso";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public String getTestButtonLabel(Locale locale) {
			return "test-opensso-configuration";
		}

		@Override
		public String getTestButtonOnClick(
			RenderRequest renderRequest, RenderResponse renderResponse) {

			return renderResponse.getNamespace() + "testOpenSSOSettings();";
		}

		@Override
		public boolean isDeprecated() {
			return true;
		}

		@Override
		public boolean isVisible() {
			return FeatureFlagManagerUtil.isEnabled("LPD-36719");
		}

	}

}