/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth.tunnel;

import com.liferay.portal.kernel.security.auth.AuthException;

import jakarta.servlet.http.HttpServletRequest;

import java.net.HttpURLConnection;

/**
 * @author Tomas Polesovsky
 */
public class TunnelAuthenticationManagerUtil {

	public static long getUserId(HttpServletRequest httpServletRequest)
		throws AuthException {

		return _tunnelAuthenticationManager.getUserId(httpServletRequest);
	}

	public static void setCredentials(
			String login, HttpURLConnection httpURLConnection)
		throws Exception {

		_tunnelAuthenticationManager.setCredentials(login, httpURLConnection);
	}

	public void setTunnelAuthenticationManager(
		TunnelAuthenticationManager tunnelAuthenticationManager) {

		_tunnelAuthenticationManager = tunnelAuthenticationManager;
	}

	private static TunnelAuthenticationManager _tunnelAuthenticationManager;

}