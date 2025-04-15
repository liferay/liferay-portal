/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.util;

import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletRequest;

/**
 * @author Drew Brokke
 */
public class DisplayStyleUtil {

	public static String getDisplayStyle(
		PortletRequest portletRequest, String defaultDisplayStyle) {

		return SearchDisplayStyleUtil.getDisplayStyle(
			PortalUtil.getHttpServletRequest(portletRequest),
			UsersAdminPortletKeys.USERS_ADMIN, defaultDisplayStyle, true);
	}

}