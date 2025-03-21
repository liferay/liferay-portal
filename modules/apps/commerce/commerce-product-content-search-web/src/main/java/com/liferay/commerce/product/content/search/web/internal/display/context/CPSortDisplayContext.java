/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.display.context;

import com.liferay.commerce.product.content.search.web.internal.configuration.CPSortPortletInstanceConfiguration;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Balazs Breier
 */
public class CPSortDisplayContext {

	public CPSortDisplayContext(
			ConfigurationProvider configurationProvider,
			HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_configurationProvider = configurationProvider;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);

		_themeDisplay = _cpRequestHelper.getThemeDisplay();
	}

	public String getDefaultSort() throws PortalException {
		CPSortPortletInstanceConfiguration cpSortPortletInstanceConfiguration =
			_configurationProvider.getPortletInstanceConfiguration(
				CPSortPortletInstanceConfiguration.class, _themeDisplay);

		return cpSortPortletInstanceConfiguration.defaultSort();
	}

	private final ConfigurationProvider _configurationProvider;
	private final CPRequestHelper _cpRequestHelper;
	private final ThemeDisplay _themeDisplay;

}