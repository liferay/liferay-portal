/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.portlet.action;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Daniel Kocsis
 */
public class ActionUtil {

	public static Group getGroup(HttpServletRequest httpServletRequest)
		throws Exception {

		Group group = null;

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);
		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

		if (groupId > 0) {
			group = GroupLocalServiceUtil.getGroup(groupId);
		}
		else if (!cmd.equals(Constants.ADD)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getSiteGroup();
		}

		httpServletRequest.setAttribute(WebKeys.GROUP, group);

		return group;
	}

	public static Group getGroup(PortletRequest portletRequest)
		throws Exception {

		return getGroup(PortalUtil.getHttpServletRequest(portletRequest));
	}

}