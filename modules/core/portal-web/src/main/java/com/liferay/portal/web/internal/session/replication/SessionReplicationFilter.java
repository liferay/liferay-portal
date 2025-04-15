/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.web.internal.session.replication;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;

/**
 * @author Dante Wang
 */
public class SessionReplicationFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		if (servletRequest instanceof HttpServletRequest) {
			servletRequest = _getWrappedHttpServletRequest(
				(HttpServletRequest)servletRequest);
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	private HttpServletRequest _getWrappedHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		HttpServletRequest wrappedHttpServletRequest = httpServletRequest;

		while (wrappedHttpServletRequest instanceof HttpServletRequestWrapper) {
			if (wrappedHttpServletRequest instanceof
					SessionReplicationHttpServletRequest) {

				return httpServletRequest;
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)wrappedHttpServletRequest;

			wrappedHttpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		return new SessionReplicationHttpServletRequest(httpServletRequest);
	}

}