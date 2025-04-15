/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
public class InvokerFilterConfig implements FilterConfig {

	public InvokerFilterConfig(
		ServletContext servletContext, String filterName,
		Map<String, String> initParameterMap) {

		_servletContext = servletContext;
		_filterName = filterName;
		_initParameterMap = initParameterMap;
	}

	@Override
	public String getFilterName() {
		return _filterName;
	}

	@Override
	public String getInitParameter(String key) {
		return _initParameterMap.get(key);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return new Enumeration<String>() {

			@Override
			public boolean hasMoreElements() {
				return _iterator.hasNext();
			}

			@Override
			public String nextElement() {
				return _iterator.next();
			}

			private final Iterator<String> _iterator = _initParameterMap.keySet(
			).iterator();

		};
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	private final String _filterName;
	private final Map<String, String> _initParameterMap;
	private final ServletContext _servletContext;

}