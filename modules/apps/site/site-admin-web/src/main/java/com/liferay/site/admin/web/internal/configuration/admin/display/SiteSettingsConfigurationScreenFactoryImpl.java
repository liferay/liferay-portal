/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = SiteSettingsConfigurationScreenFactory.class)
public class SiteSettingsConfigurationScreenFactoryImpl
	implements SiteSettingsConfigurationScreenFactory {

	@Override
	public ConfigurationScreen create(
		SiteSettingsConfigurationScreenContributor
			siteSettingsConfigurationScreenContributor) {

		return new SiteSettingsConfigurationScreen(
			siteSettingsConfigurationScreenContributor, _servletContext);
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

}