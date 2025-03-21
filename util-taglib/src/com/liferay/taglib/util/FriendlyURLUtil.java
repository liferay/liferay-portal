/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Preston Crary
 */
public class FriendlyURLUtil {

	public static String getFriendlyURL(
		HttpServletRequest httpServletRequest, ServletConfig servletConfig) {

		boolean privateParam = GetterUtil.getBoolean(
			servletConfig.getInitParameter("private"));

		String proxyPath = PortalUtil.getPathProxy();

		String friendlyURLPathPrefix = null;

		if (privateParam) {
			boolean userParam = GetterUtil.getBoolean(
				servletConfig.getInitParameter("user"));

			if (userParam) {
				friendlyURLPathPrefix =
					PortalUtil.getPathFriendlyURLPrivateUser();
			}
			else {
				friendlyURLPathPrefix =
					PortalUtil.getPathFriendlyURLPrivateGroup();
			}
		}
		else {
			friendlyURLPathPrefix = PortalUtil.getPathFriendlyURLPublic();
		}

		int pathInfoOffset =
			friendlyURLPathPrefix.length() - proxyPath.length();

		String pathInfo = null;

		String requestURI = httpServletRequest.getRequestURI();

		int pos = requestURI.indexOf(Portal.JSESSIONID);

		if (pos == -1) {
			pathInfo = requestURI.substring(pathInfoOffset);
		}
		else {
			pathInfo = requestURI.substring(pathInfoOffset, pos);
		}

		if (Validator.isNotNull(pathInfo)) {
			return friendlyURLPathPrefix.concat(pathInfo);
		}

		return friendlyURLPathPrefix;
	}

}