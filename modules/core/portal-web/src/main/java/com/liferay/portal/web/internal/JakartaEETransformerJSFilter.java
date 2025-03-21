/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.web.internal;

import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.EnumSet;
import java.util.function.BiFunction;

/**
 * @author Shuyang Zhou
 */
public class JakartaEETransformerJSFilter implements Filter {

	public static void register(ServletContext servletContext) {
		if (_textReplacerBiFunction != null) {
			FilterRegistration.Dynamic dynamic = servletContext.addFilter(
				JakartaEETransformerJSFilter.class.getName(),
				new JakartaEETransformerJSFilter());

			dynamic.addMappingForUrlPatterns(
				EnumSet.of(DispatcherType.REQUEST), false, "*.js");
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		String uri = httpServletRequest.getRequestURI();

		if (!uri.startsWith("/o/") || !uri.endsWith("/__liferay__/index.js")) {
			filterChain.doFilter(servletRequest, servletResponse);

			return;
		}

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)servletResponse;

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		filterChain.doFilter(servletRequest, bufferCacheServletResponse);

		ServletResponseUtil.write(
			httpServletResponse,
			_textReplacerBiFunction.apply(
				"JS#" + uri, bufferCacheServletResponse.getString()));
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	private static final BiFunction<String, String, String>
		_textReplacerBiFunction;

	static {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object instance = null;

		try {
			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.tools.jakarta.ee.transformer.function." +
					"TextReplacerBiFunction");

			instance = clazz.newInstance();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			if (!(reflectiveOperationException instanceof
					ClassNotFoundException)) {

				throw new ExceptionInInitializerError(
					reflectiveOperationException);
			}
		}

		_textReplacerBiFunction = (BiFunction<String, String, String>)instance;
	}

}