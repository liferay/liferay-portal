/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.validator.vies.web.internal.configuration.admin.display;

import com.liferay.account.validator.vies.web.internal.display.context.VIESAccountEntryValidatorConfigurationDisplayContext;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = ConfigurationScreen.class)
public class VIESAccountEntryValidatorPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new VIESAccountEntryValidatorPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CountryService _countryService;

	@Reference
	private Language _language;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.account.validator.vies.web)"
	)
	private ServletContext _servletContext;

	private class
		VIESAccountEntryValidatorPortalSettingsConfigurationScreenContributor
			implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "accounts";
		}

		@Override
		public String getJspPath() {
			return "/configuration.jsp";
		}

		@Override
		public String getKey() {
			return "vies-account-entry-validator-configuration";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(
				locale, "vies-account-entry-configuration-name");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/instance_settings" +
				"/edit_vies_account_entry_validator_configuration";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			PortalSettingsConfigurationScreenContributor.super.setAttributes(
				httpServletRequest, httpServletResponse);

			httpServletRequest.setAttribute(
				VIESAccountEntryValidatorConfigurationDisplayContext.class.
					getName(),
				new VIESAccountEntryValidatorConfigurationDisplayContext(
					_configurationProvider, _countryService,
					httpServletRequest));
		}

	}

}