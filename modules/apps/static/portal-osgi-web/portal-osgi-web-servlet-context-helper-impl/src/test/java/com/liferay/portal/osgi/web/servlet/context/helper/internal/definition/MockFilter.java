/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.internal.definition;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * @author Miguel Pastor
 */
public class MockFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
		ServletRequest servletRequest, ServletResponse servletResponse,
		FilterChain filterChain) {
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

}