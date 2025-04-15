/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.util;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Iván Zaera
 */
public class JSPRenderer {

	public JSPRenderer(String jspPath) {
		_jspPath = jspPath;
	}

	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		Map<String, Object> savedAttributes = new HashMap<>();

		for (Map.Entry<String, Object> entry : _attributes.entrySet()) {
			String key = entry.getKey();

			savedAttributes.put(key, httpServletRequest.getAttribute(key));

			httpServletRequest.setAttribute(key, entry.getValue());
		}

		RequestDispatcher requestDispatcher =
			httpServletRequest.getRequestDispatcher(_jspPath);

		requestDispatcher.include(httpServletRequest, httpServletResponse);

		for (Map.Entry<String, Object> entry : savedAttributes.entrySet()) {
			httpServletRequest.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	public void setAttribute(String key, Object value) {
		_attributes.put(key, value);
	}

	private final Map<String, Object> _attributes = new HashMap<>();
	private final String _jspPath;

}