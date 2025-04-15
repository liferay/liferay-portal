/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal.adapter;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * @author Dante Wang
 */
public class AsyncAttributeAdapterServlet implements Servlet {

	public AsyncAttributeAdapterServlet(Servlet servlet) {
		_servlet = servlet;
	}

	@Override
	public void destroy() {
		_servlet.destroy();
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
	public void init(ServletConfig servletConfig) throws ServletException {
		_servlet.init(servletConfig);
	}

	@Override
	public void service(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		if (servletRequest.isAsyncSupported() &&
			(servletRequest.getDispatcherType() == DispatcherType.ASYNC) &&
			(servletRequest instanceof HttpServletRequest)) {

			HttpServletRequest httpServletRequest =
				(HttpServletRequest)servletRequest;

			httpServletRequest.setAttribute(
				AsyncContext.ASYNC_CONTEXT_PATH,
				httpServletRequest.getContextPath());
			httpServletRequest.setAttribute(
				AsyncContext.ASYNC_PATH_INFO, httpServletRequest.getPathInfo());
			httpServletRequest.setAttribute(
				AsyncContext.ASYNC_QUERY_STRING,
				httpServletRequest.getQueryString());
			httpServletRequest.setAttribute(
				AsyncContext.ASYNC_REQUEST_URI,
				httpServletRequest.getRequestURI());
			httpServletRequest.setAttribute(
				AsyncContext.ASYNC_SERVLET_PATH,
				httpServletRequest.getServletPath());
		}

		_servlet.service(servletRequest, servletResponse);
	}

	private final Servlet _servlet;

}