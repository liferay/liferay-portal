/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal.proxy;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.function.Supplier;

/**
 * @author Shuyang Zhou
 */
public class FilterWrapper implements Filter {

	public FilterWrapper(
		ProxyFactory proxyFactory, Supplier<? extends Filter> filterSupplier,
		ServletContext servletContext) {

		_proxyFactory = proxyFactory;
		_filterSupplier = filterSupplier;
		_servletContext = servletContext;

		_classLoader = servletContext.getClassLoader();
	}

	@Override
	public void destroy() {
		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(_classLoader);

		try {
			Filter filter = _filterSupplier.get();

			filter.destroy();
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(_classLoader);

		try {
			Filter filter = _filterSupplier.get();

			filter.doFilter(servletRequest, servletResponse, filterChain);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		currentThread.setContextClassLoader(_classLoader);

		try {
			Filter filter = _filterSupplier.get();

			filter.init(
				_proxyFactory.createASMWrapper(
					_classLoader, FilterConfig.class,
					new FilterConfigDelegate(_servletContext), filterConfig));
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	private final ClassLoader _classLoader;
	private final Supplier<? extends Filter> _filterSupplier;
	private final ProxyFactory _proxyFactory;
	private final ServletContext _servletContext;

}