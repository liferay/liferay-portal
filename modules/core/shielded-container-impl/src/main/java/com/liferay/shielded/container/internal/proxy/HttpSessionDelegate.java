/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal.proxy;

import com.liferay.shielded.container.internal.ShieldedContainerClassLoader;
import com.liferay.shielded.container.internal.session.SerializationUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

/**
 * @author Shuyang Zhou
 */
public class HttpSessionDelegate {

	public HttpSessionDelegate(HttpSession httpSession) {
		_httpSession = httpSession;
	}

	public Object getAttribute(String name) {
		Object value = _httpSession.getAttribute(name);

		if (value instanceof byte[]) {
			ServletContext servletContext = _httpSession.getServletContext();

			ClassLoader classLoader = (ClassLoader)servletContext.getAttribute(
				ShieldedContainerClassLoader.NAME);

			try {
				return SerializationUtil.deserialize(
					(byte[])value, classLoader);
			}
			catch (Exception exception) {
			}
		}

		return value;
	}

	private final HttpSession _httpSession;

}