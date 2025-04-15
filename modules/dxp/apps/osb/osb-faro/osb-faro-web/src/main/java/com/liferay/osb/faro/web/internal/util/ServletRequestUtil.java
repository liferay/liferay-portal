/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Shinn Lok
 */
public class ServletRequestUtil {

	public static String getOriginalURL(HttpServletRequest httpServletRequest) {
		StringBuilder sb = new StringBuilder();

		sb.append(_getScheme(httpServletRequest));
		sb.append("://");
		sb.append(_getServerName(httpServletRequest));

		int serverPort = _getServerPort(httpServletRequest);

		if (serverPort > 0) {
			sb.append(":");
			sb.append(serverPort);
		}

		return sb.toString();
	}

	private static String _getScheme(HttpServletRequest httpServletRequest) {
		String forwardedProtocol = httpServletRequest.getHeader(
			"X-Forwarded-Proto");

		if (forwardedProtocol != null) {
			return forwardedProtocol;
		}

		return httpServletRequest.getScheme();
	}

	private static String _getServerName(
		HttpServletRequest httpServletRequest) {

		String forwardedHost = httpServletRequest.getHeader("X-Forwarded-Host");

		if (forwardedHost != null) {
			return forwardedHost;
		}

		return httpServletRequest.getServerName();
	}

	private static int _getServerPort(HttpServletRequest httpServletRequest) {
		int serverPort = 0;

		String forwardedPort = httpServletRequest.getHeader("X-Forwarded-Port");

		if (forwardedPort != null) {
			serverPort = GetterUtil.getInteger(forwardedPort);
		}
		else {
			serverPort = httpServletRequest.getServerPort();
		}

		if ((serverPort == Http.HTTP_PORT) || (serverPort == Http.HTTPS_PORT)) {
			return -1;
		}

		return serverPort;
	}

}