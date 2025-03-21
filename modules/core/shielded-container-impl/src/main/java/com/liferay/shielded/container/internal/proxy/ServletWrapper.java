/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal.proxy;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.function.Supplier;

/**
 * @author Shuyang Zhou
 */
public class ServletWrapper implements Servlet {

	public ServletWrapper(
		ProxyFactory proxyFactory, Supplier<? extends Servlet> servletSupplier,
		ServletContext servletContext) {

		_proxyFactory = proxyFactory;
		_servletSupplier = servletSupplier;
		_servletContext = servletContext;

		_classLoader = servletContext.getClassLoader();
	}

	@Override
	public void destroy() {
		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(_classLoader);

		try {
			Servlet servlet = _servletSupplier.get();

			servlet.destroy();
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	@Override
	public ServletConfig getServletConfig() {
		Servlet servlet = _servletSupplier.get();

		return servlet.getServletConfig();
	}

	@Override
	public String getServletInfo() {
		Servlet servlet = _servletSupplier.get();

		return servlet.getServletInfo();
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(_classLoader);

		try {
			_servletConfig = _proxyFactory.createASMWrapper(
				_servletContext.getClassLoader(), ServletConfig.class,
				new ServletConfigDelegate(_servletContext), servletConfig);

			Servlet servlet = _servletSupplier.get();

			servlet.init(_servletConfig);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	@Override
	public void service(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(_classLoader);

		try {
			Servlet servlet = _servletSupplier.get();

			servlet.service(servletRequest, servletResponse);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	private final ClassLoader _classLoader;
	private final ProxyFactory _proxyFactory;
	private ServletConfig _servletConfig;
	private final ServletContext _servletContext;
	private final Supplier<? extends Servlet> _servletSupplier;

}