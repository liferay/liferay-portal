/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

/**
 * @author Dante Wang
 */
public class ServletConfigImpl implements ServletConfig {

	public ServletConfigImpl(
		Map<String, String> initParameters, ServletContext servletContext,
		String servletName) {

		_initParameters = Objects.requireNonNullElse(
			initParameters, Collections.emptyMap());
		_servletContext = servletContext;
		_servletName = servletName;
	}

	@Override
	public String getInitParameter(String name) {
		return _initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(_initParameters.keySet());
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public String getServletName() {
		return _servletName;
	}

	private final Map<String, String> _initParameters;
	private final ServletContext _servletContext;
	private final String _servletName;

}