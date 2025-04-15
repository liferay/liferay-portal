/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.admin.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class AdminUtil {

	public static String getUpdateUserPassword(
		ActionRequest actionRequest, long userId) {

		return getUpdateUserPassword(
			PortalUtil.getHttpServletRequest(actionRequest), userId);
	}

	public static String getUpdateUserPassword(
		HttpServletRequest httpServletRequest, long userId) {

		String password = PortalUtil.getUserPassword(httpServletRequest);

		if ((userId != PortalUtil.getUserId(httpServletRequest)) ||
			(password == null)) {

			password = StringPool.BLANK;
		}

		return password;
	}

}