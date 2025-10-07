/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Rafael Praxedes
 */
public class ActionUtil {

	public static long getCompanyId(ActionRequest actionRequest) {
		return getCompanyId(PortalUtil.getHttpServletRequest(actionRequest));
	}

	public static long getCompanyId(HttpServletRequest httpServletRequest) {
		String portletId = PortalUtil.getPortletId(httpServletRequest);

		if (portletId.equals(ConfigurationAdminPortletKeys.SYSTEM_SETTINGS)) {
			return 0;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getCompanyId();
	}

}