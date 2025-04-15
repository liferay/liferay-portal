/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = PortalSettingsConfigurationScreenFactory.class)
public class PortalSettingsConfigurationScreenFactoryImpl
	implements PortalSettingsConfigurationScreenFactory {

	@Override
	public ConfigurationScreen create(
		PortalSettingsConfigurationScreenContributor
			portalSettingsConfigurationScreenContributor) {

		return new PortalSettingsConfigurationScreen(
			portalSettingsConfigurationScreenContributor, _servletContext);
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.web)"
	)
	private ServletContext _servletContext;

}