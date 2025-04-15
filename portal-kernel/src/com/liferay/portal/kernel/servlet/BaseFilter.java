/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterHelper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Raymond Augé
 * @author Eduardo Lundgren
 */
public abstract class BaseFilter implements LiferayFilter {

	@Override
	public void destroy() {
		LiferayFilterTracker.removeLiferayFilter(this);
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		try {
			processFilter(
				(HttpServletRequest)servletRequest,
				(HttpServletResponse)servletResponse, filterChain);
		}
		catch (IOException ioException) {
			throw ioException;
		}
		catch (ServletException servletException) {
			throw servletException;
		}
		catch (Exception exception) {
			Log log = getLog();

			log.error(exception, exception);
		}
	}

	public FilterConfig getFilterConfig() {
		return _filterConfig;
	}

	@Override
	public void init(FilterConfig filterConfig) {
		_filterConfig = filterConfig;

		LiferayFilterTracker.addLiferayFilter(this);
	}

	@Override
	public boolean isFilterEnabled() {
		return _filterEnabled;
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _filterEnabled;
	}

	@Override
	public void setFilterEnabled(boolean filterEnabled) {
		if (filterEnabled != _filterEnabled) {
			ServletContext servletContext = _filterConfig.getServletContext();

			InvokerFilterHelper invokerFilterHelper =
				(InvokerFilterHelper)servletContext.getAttribute(
					InvokerFilterHelper.class.getName());

			if (invokerFilterHelper != null) {
				invokerFilterHelper.clearFilterChainsCache();
			}
		}

		_filterEnabled = filterEnabled;
	}

	protected abstract Log getLog();

	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		Class<?> clazz = getClass();

		processFilter(
			clazz.getName(), httpServletRequest, httpServletResponse,
			filterChain);
	}

	protected void processFilter(
			String logName, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		long startTime = 0;

		String threadName = null;
		String depther = null;
		String path = null;

		Log log = getLog();

		if (log.isDebugEnabled()) {
			startTime = System.currentTimeMillis();

			Thread currentThread = Thread.currentThread();

			threadName = currentThread.getName();

			depther = (String)httpServletRequest.getAttribute(_DEPTHER);

			if (depther == null) {
				depther = StringPool.BLANK;
			}
			else {
				depther += StringPool.EQUAL;
			}

			httpServletRequest.setAttribute(_DEPTHER, depther);

			path = httpServletRequest.getRequestURI();

			log.debug(
				StringBundler.concat(
					"[", threadName, "]", depther, "> ", logName, " ", path));
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);

		if (!log.isDebugEnabled()) {
			return;
		}

		long endTime = System.currentTimeMillis();

		depther = (String)httpServletRequest.getAttribute(_DEPTHER);

		if (depther == null) {
			return;
		}

		log.debug(
			StringBundler.concat(
				"[", threadName, "]", depther, "< ", logName, " ", path, " ",
				endTime - startTime, " ms"));

		if (depther.length() > 0) {
			depther = depther.substring(1);
		}

		httpServletRequest.setAttribute(_DEPTHER, depther);
	}

	private static final String _DEPTHER = "DEPTHER";

	private FilterConfig _filterConfig;
	private boolean _filterEnabled = true;

}