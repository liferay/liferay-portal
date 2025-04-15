/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = ConfigurationScreen.class)
public class AnalyticsSyncedContactsDataConfigurationScreen
	extends BaseAnalyticsConfigurationScreen {

	@Override
	public String getKey() {
		return "2-synced-contact-data";
	}

	@Override
	protected String getJspPath() {
		return "/edit_synced_contacts_data.jsp";
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.analytics.settings.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}