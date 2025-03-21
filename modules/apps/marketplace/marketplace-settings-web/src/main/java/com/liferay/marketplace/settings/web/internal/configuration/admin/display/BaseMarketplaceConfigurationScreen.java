/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.settings.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Eduardo Diniz
 * @author Keven Leone
 */
public abstract class BaseMarketplaceConfigurationScreen
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "marketplace";
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(locale, getKey());
	}

	@Override
	public String getScope() {
		return "company";
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			ServletContext servletContext = getServletContext();

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(getJspPath());

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render " + getJspPath(), exception);
		}
	}

	protected abstract String getJspPath();

	protected abstract ServletContext getServletContext();

}