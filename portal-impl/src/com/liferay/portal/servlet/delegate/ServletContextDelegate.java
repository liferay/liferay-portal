/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.delegate;

import com.liferay.portal.kernel.util.ProxyUtil;

import jakarta.servlet.ServletContext;

/**
 * @author Shuyang Zhou
 */
public class ServletContextDelegate {

	public static ServletContext create(ServletContext servletContext) {
		Class<?> clazz = servletContext.getClass();

		return ProxyUtil.newDelegateProxyInstance(
			clazz.getClassLoader(), ServletContext.class,
			new ServletContextDelegate(servletContext), servletContext);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ServletContext)) {
			return false;
		}

		ServletContext servletContext = (ServletContext)object;

		return servletContext.equals(_servletContext);
	}

	public String getContextPath() {
		return _contextPath;
	}

	public String getServletContextName() {
		return _servletContextName;
	}

	@Override
	public int hashCode() {
		return _servletContext.hashCode();
	}

	private ServletContextDelegate(ServletContext servletContext) {
		_servletContext = servletContext;

		_contextPath = servletContext.getContextPath();
		_servletContextName = servletContext.getServletContextName();
	}

	private final String _contextPath;
	private final ServletContext _servletContext;
	private final String _servletContextName;

}