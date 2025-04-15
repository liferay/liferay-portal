/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryMenuDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Drew Brokke
 */
public class ConfigurationCategoryUtil {

	public static String getHREF(
		ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay,
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		if (!configurationCategoryMenuDisplay.isEmpty()) {
			ConfigurationEntry configurationEntry =
				configurationCategoryMenuDisplay.getFirstConfigurationEntry();

			return configurationEntry.getEditURL(renderRequest, renderResponse);
		}

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		return portletURL.toString();
	}

}