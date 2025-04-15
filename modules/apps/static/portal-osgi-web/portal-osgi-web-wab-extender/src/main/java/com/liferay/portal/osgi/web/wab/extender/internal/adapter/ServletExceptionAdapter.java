/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal.adapter;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.Enumeration;

/**
 * @author Raymond Augé
 */
public class ServletExceptionAdapter implements Servlet {

	public ServletExceptionAdapter(
		Servlet servlet, ModifiableServletContext modifiableServletContext) {

		_servlet = servlet;
		_modifiableServletContext = modifiableServletContext;
	}

	@Override
	public void destroy() {
		_servlet.destroy();
	}

	public Exception getException() {
		return _exception;
	}

	@Override
	public ServletConfig getServletConfig() {
		return _servlet.getServletConfig();
	}

	@Override
	public String getServletInfo() {
		return _servlet.getServletInfo();
	}

	@Override
	public void init(ServletConfig servletConfig) {
		try {
			_servlet.init(
				new ServletConfigWrapper(
					servletConfig, _modifiableServletContext));
		}
		catch (Exception exception) {
			_exception = exception;
		}
	}

	@Override
	public void service(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		_servlet.service(servletRequest, servletResponse);
	}

	private Exception _exception;
	private ModifiableServletContext _modifiableServletContext;
	private final Servlet _servlet;

	private static class ServletConfigWrapper implements ServletConfig {

		public ServletConfigWrapper(
			ServletConfig wrappedServletConfig,
			ModifiableServletContext modifiableServletContext) {

			_wrappedServletConfig = wrappedServletConfig;
			_modifiableServletContext = modifiableServletContext;
		}

		public String getInitParameter(String name) {
			return _wrappedServletConfig.getInitParameter(name);
		}

		public Enumeration<String> getInitParameterNames() {
			return _wrappedServletConfig.getInitParameterNames();
		}

		public ServletContext getServletContext() {
			return (ServletContext)_modifiableServletContext;
		}

		public String getServletName() {
			return _wrappedServletConfig.getServletName();
		}

		private ModifiableServletContext _modifiableServletContext;
		private final ServletConfig _wrappedServletConfig;

	}

}