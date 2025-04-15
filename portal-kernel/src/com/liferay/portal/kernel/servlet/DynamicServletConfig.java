/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class DynamicServletConfig implements ServletConfig {

	public DynamicServletConfig(
		ServletConfig servletConfig, Map<String, String> params) {

		_servletConfig = servletConfig;
		_params = params;
	}

	@Override
	public String getInitParameter(String name) {
		return _params.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(_params.keySet());
	}

	@Override
	public ServletContext getServletContext() {
		return _servletConfig.getServletContext();
	}

	@Override
	public String getServletName() {
		return _servletConfig.getServletName();
	}

	private final Map<String, String> _params;
	private final ServletConfig _servletConfig;

}