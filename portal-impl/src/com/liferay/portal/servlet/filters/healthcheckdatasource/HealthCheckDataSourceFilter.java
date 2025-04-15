/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.healthcheckdatasource;

import com.liferay.portal.kernel.util.InfrastructureUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author Shuyang Zhou
 */
public class HealthCheckDataSourceFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		// curl -i http://localhost:8080/health_check/data_source

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)servletResponse;

		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection()) {
			if (connection.isValid(0)) {
				_writeMessage(
					httpServletResponse, HttpServletResponse.SC_OK,
					"Data source is healthy.");
			}
			else {
				_writeMessage(
					httpServletResponse,
					HttpServletResponse.SC_SERVICE_UNAVAILABLE,
					"Data source is not healthy.");
			}
		}
		catch (SQLException sqlException) {
			_writeMessage(
				httpServletResponse,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				sqlException.getMessage());
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	private void _writeMessage(
			HttpServletResponse httpServletResponse, int status, String message)
		throws IOException {

		httpServletResponse.setStatus(status);

		try (PrintWriter printWriter = httpServletResponse.getWriter()) {
			printWriter.println(message);
		}
	}

}