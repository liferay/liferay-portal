/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.InstanceFactory;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.Enumeration;

/**
 * @author Brian Wing Shun Chan
 */
public class SecureServlet implements Servlet, ServletConfig {

	@Override
	public void destroy() {
		servlet.destroy();
	}

	@Override
	public String getInitParameter(String name) {
		return servletConfig.getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return servletConfig.getInitParameterNames();
	}

	@Override
	public ServletConfig getServletConfig() {
		return servletConfig;
	}

	@Override
	public ServletContext getServletContext() {
		return servletConfig.getServletContext();
	}

	@Override
	public String getServletInfo() {
		if (servlet == null) {
			return StringPool.BLANK;
		}

		return servlet.getServletInfo();
	}

	@Override
	public String getServletName() {
		return servletConfig.getServletName();
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		this.servletConfig = servletConfig;

		ServletContext servletContext = servletConfig.getServletContext();

		ClassLoader classLoader = (ClassLoader)servletContext.getAttribute(
			PluginContextListener.PLUGIN_CLASS_LOADER);

		String servletClass = servletConfig.getInitParameter("servlet-class");

		try {
			servlet = (Servlet)InstanceFactory.newInstance(
				classLoader, servletClass);
		}
		catch (Exception exception) {
			throw new ServletException(exception);
		}

		servlet.init(servletConfig);
	}

	@Override
	public void service(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		servlet.service(servletRequest, servletResponse);
	}

	protected Servlet servlet;
	protected ServletConfig servletConfig;

}