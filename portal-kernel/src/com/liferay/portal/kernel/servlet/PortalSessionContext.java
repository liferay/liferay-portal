/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpSession;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class PortalSessionContext {

	public static int count() {
		return _sessionPool.size();
	}

	public static HttpSession get(String sessionId) {
		if (Validator.isNull(sessionId)) {
			return null;
		}

		return _sessionPool.get(sessionId);
	}

	public static void put(String sessionId, HttpSession httpSession) {
		_sessionPool.put(sessionId, httpSession);
	}

	public static HttpSession remove(String sessionId) {
		return _sessionPool.remove(sessionId);
	}

	public static Collection<HttpSession> values() {
		return _sessionPool.values();
	}

	protected PortalSessionContext() {
	}

	private static final Map<String, HttpSession> _sessionPool =
		new ConcurrentHashMap<>();

}