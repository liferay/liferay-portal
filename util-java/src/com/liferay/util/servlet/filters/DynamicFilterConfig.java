/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.servlet.filters;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 */
public class DynamicFilterConfig implements FilterConfig {

	public DynamicFilterConfig(FilterConfig filterConfig) {
		this(null, null);

		Enumeration<String> enumeration = filterConfig.getInitParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			addInitParameter(name, filterConfig.getInitParameter(name));
		}
	}

	public DynamicFilterConfig(
		String filterName, ServletContext servletContext) {

		_filterName = filterName;
		_servletContext = servletContext;
	}

	public void addInitParameter(String name, String value) {
		_parameters.put(name, value);
	}

	@Override
	public String getFilterName() {
		return _filterName;
	}

	@Override
	public String getInitParameter(String name) {
		return _parameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(_parameters.keySet());
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	private final String _filterName;
	private final Map<String, String> _parameters = new LinkedHashMap<>();
	private final ServletContext _servletContext;

}