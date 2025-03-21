/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.lang.CentralizedThreadLocal;

import jakarta.servlet.http.HttpSession;

/**
 * @author Shuyang Zhou
 */
public class PortalSessionThreadLocal {

	public static HttpSession getHttpSession() {
		String sessionId = _sessionId.get();

		if (sessionId == null) {
			return null;
		}

		return PortalSessionContext.get(sessionId);
	}

	public static void setHttpSession(HttpSession httpSession) {
		_sessionId.set(httpSession.getId());
	}

	private static final ThreadLocal<String> _sessionId =
		new CentralizedThreadLocal<>(
			PortalSessionThreadLocal.class + "._sessionId");

}