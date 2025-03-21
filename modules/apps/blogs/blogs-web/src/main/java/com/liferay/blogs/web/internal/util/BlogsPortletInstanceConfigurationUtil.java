/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.util;

import com.liferay.blogs.web.internal.configuration.BlogsPortletInstanceConfiguration;
import com.liferay.blogs.web.internal.constants.BlogsWebConstants;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.settings.PortletInstanceSettingsLocator;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public class BlogsPortletInstanceConfigurationUtil {

	public static BlogsPortletInstanceConfiguration
			getBlogsPortletInstanceConfiguration(ThemeDisplay themeDisplay)
		throws ConfigurationException {

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		BlogsPortletInstanceConfiguration blogsPortletInstanceConfiguration =
			(BlogsPortletInstanceConfiguration)httpServletRequest.getAttribute(
				BlogsWebConstants.BLOGS_PORTLET_INSTANCE_CONFIGURATION);

		if (blogsPortletInstanceConfiguration == null) {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			blogsPortletInstanceConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					BlogsPortletInstanceConfiguration.class,
					new PortletInstanceSettingsLocator(
						themeDisplay.getLayout(), portletDisplay.getId()));

			httpServletRequest.setAttribute(
				BlogsWebConstants.BLOGS_PORTLET_INSTANCE_CONFIGURATION,
				blogsPortletInstanceConfiguration);
		}

		return blogsPortletInstanceConfiguration;
	}

}