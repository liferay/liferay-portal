/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.web.internal.constants.DigitalSignatureWebKeys;
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
 * @author José Abelenda
 */
@Component(service = ConfigurationScreen.class)
public class DigitalSignatureSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new DigitalSignatureSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.digital.signature.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class DigitalSignatureSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "digital-signature";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/digital_signature.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-digital-signature";
		}

		@Override
		public String getName(Locale locale) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());

			return _language.get(resourceBundle, "digital-signature");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/digital_signature/save_site_configuration";
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

			DigitalSignatureConfiguration digitalSignatureConfiguration =
				DigitalSignatureConfigurationUtil.
					getDigitalSignatureConfiguration(
						themeDisplay.getCompanyId(),
						themeDisplay.getSiteGroupId());

			httpServletRequest.setAttribute(
				DigitalSignatureConfiguration.class.getName(),
				DigitalSignatureConfigurationUtil.
					getDigitalSignatureConfiguration(
						themeDisplay.getCompanyId(), 0));

			httpServletRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_ACCOUNT_BASE_URI,
				digitalSignatureConfiguration.accountBaseURI());
			httpServletRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_API_ACCOUNT_ID,
				digitalSignatureConfiguration.apiAccountId());
			httpServletRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_API_USERNAME,
				digitalSignatureConfiguration.apiUsername());
			httpServletRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_ENABLED,
				digitalSignatureConfiguration.enabled());
			httpServletRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_ENVIRONMENT,
				digitalSignatureConfiguration.environment());
			httpServletRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_INTEGRATION_KEY,
				digitalSignatureConfiguration.integrationKey());
			httpServletRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_RSA_PRIVATE_KEY,
				digitalSignatureConfiguration.rsaPrivateKey());
		}

	}

}