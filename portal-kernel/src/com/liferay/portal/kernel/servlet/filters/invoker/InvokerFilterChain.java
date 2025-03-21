/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.DirectCallFilter;
import com.liferay.portal.kernel.servlet.LiferayFilter;
import com.liferay.portal.kernel.servlet.TryFilter;
import com.liferay.portal.kernel.servlet.TryFinallyFilter;
import com.liferay.portal.kernel.servlet.WrapHttpServletRequestFilter;
import com.liferay.portal.kernel.servlet.WrapHttpServletResponseFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class InvokerFilterChain implements FilterChain {

	public InvokerFilterChain(FilterChain filterChain) {
		_filterChain = filterChain;
	}

	public void addFilter(Filter filter) {
		if (_filters == null) {
			_filters = new ArrayList<>();
		}

		_filters.add(filter);
	}

	public InvokerFilterChain clone(FilterChain filterChain) {
		InvokerFilterChain invokerFilterChain = new InvokerFilterChain(
			filterChain);

		invokerFilterChain._filters = _filters;

		return invokerFilterChain;
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		if (_filters != null) {
			HttpServletRequest httpServletRequest =
				(HttpServletRequest)servletRequest;
			HttpServletResponse httpServletResponse =
				(HttpServletResponse)servletResponse;

			while (_index < _filters.size()) {
				Filter filter = _filters.get(_index++);

				if (filter instanceof LiferayFilter) {
					LiferayFilter liferayFilter = (LiferayFilter)filter;

					if (!liferayFilter.isFilterEnabled() ||
						!liferayFilter.isFilterEnabled(
							httpServletRequest, httpServletResponse)) {

						if (_log.isDebugEnabled()) {
							_log.debug(
								"Skip disabled filter " + filter.getClass());
						}

						continue;
					}
				}

				if (filter instanceof DirectCallFilter) {
					try {
						processDirectCallFilter(
							filter, httpServletRequest, httpServletResponse);
					}
					catch (IOException ioException) {
						throw ioException;
					}
					catch (RuntimeException runtimeException) {
						throw runtimeException;
					}
					catch (ServletException servletException) {
						throw servletException;
					}
					catch (Exception exception) {
						throw new ServletException(exception);
					}
				}
				else {
					processDoFilter(
						filter, httpServletRequest, httpServletResponse);
				}

				return;
			}
		}

		_filterChain.doFilter(servletRequest, servletResponse);
	}

	public void setContextClassLoader(ClassLoader contextClassLoader) {
		_contextClassLoader = contextClassLoader;
	}

	protected void processDirectCallFilter(
			Filter filter, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (filter instanceof WrapHttpServletRequestFilter) {
			if (_log.isDebugEnabled()) {
				_log.debug("Wrap response with filter " + filter.getClass());
			}

			WrapHttpServletRequestFilter wrapHttpServletRequestFilter =
				(WrapHttpServletRequestFilter)filter;

			httpServletRequest =
				wrapHttpServletRequestFilter.getWrappedHttpServletRequest(
					httpServletRequest, httpServletResponse);
		}

		if (filter instanceof WrapHttpServletResponseFilter) {
			if (_log.isDebugEnabled()) {
				_log.debug("Wrap request with filter " + filter.getClass());
			}

			WrapHttpServletResponseFilter wrapHttpServletResponseFilter =
				(WrapHttpServletResponseFilter)filter;

			httpServletResponse =
				wrapHttpServletResponseFilter.getWrappedHttpServletResponse(
					httpServletRequest, httpServletResponse);
		}

		if (filter instanceof TryFinallyFilter) {
			TryFinallyFilter tryFinallyFilter = (TryFinallyFilter)filter;

			Object object = null;

			try {
				if (_log.isDebugEnabled()) {
					_log.debug("Invoke try for filter " + filter.getClass());
				}

				object = tryFinallyFilter.doFilterTry(
					httpServletRequest, httpServletResponse);

				doFilter(httpServletRequest, httpServletResponse);
			}
			finally {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Invoke finally for filter " + filter.getClass());
				}

				tryFinallyFilter.doFilterFinally(
					httpServletRequest, httpServletResponse, object);
			}
		}
		else if (filter instanceof TryFilter) {
			TryFilter tryFilter = (TryFilter)filter;

			if (_log.isDebugEnabled()) {
				_log.debug("Invoke try for filter " + filter.getClass());
			}

			tryFilter.doFilterTry(httpServletRequest, httpServletResponse);

			doFilter(httpServletRequest, httpServletResponse);
		}
		else {
			doFilter(httpServletRequest, httpServletResponse);
		}
	}

	protected void processDoFilter(
			Filter filter, ServletRequest servletRequest,
			ServletResponse servletResponse)
		throws IOException, ServletException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_contextClassLoader)) {

			filter.doFilter(servletRequest, servletResponse, this);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InvokerFilterChain.class);

	private ClassLoader _contextClassLoader;
	private final FilterChain _filterChain;
	private List<Filter> _filters;
	private int _index;

}