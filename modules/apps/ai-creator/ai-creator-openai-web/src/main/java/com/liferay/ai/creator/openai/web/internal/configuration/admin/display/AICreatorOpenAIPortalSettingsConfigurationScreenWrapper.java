/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.configuration.admin.display;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.ai.creator.openai.web.internal.display.context.AICreatorOpenAICompanyConfigurationDisplayContext;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ConfigurationScreen.class)
public class AICreatorOpenAIPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new AICreatorOpenAIPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

	@Reference
	private Language _language;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.ai.creator.openai.web)"
	)
	private ServletContext _servletContext;

	private class AICreatorOpenAIPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "ai-creator";
		}

		@Override
		public String getJspPath() {
			return "/configuration/openai_company_configuration.jsp";
		}

		@Override
		public String getKey() {
			return "ai-creator-openai-company-configuration";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "openai");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/instance_settings/save_company_configuration";
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
				AICreatorOpenAICompanyConfigurationDisplayContext.class.
					getName(),
				new AICreatorOpenAICompanyConfigurationDisplayContext(
					_aiCreatorOpenAIConfigurationManager, httpServletRequest));
		}

	}

}