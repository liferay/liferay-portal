/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.settings.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Diniz
 * @author Keven Leone
 */
@Component(service = ConfigurationScreen.class)
public class MarketplaceConfigurationScreen
	extends BaseMarketplaceConfigurationScreen {

	@Override
	public String getKey() {
		return "marketplace";
	}

	@Override
	public boolean isVisible() {
		return FeatureFlagManagerUtil.isEnabled("LPD-35941");
	}

	@Override
	protected String getJspPath() {
		return "/view.jsp";
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.marketplace.settings.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}