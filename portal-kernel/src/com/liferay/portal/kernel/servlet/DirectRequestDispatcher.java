/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * @author Shuyang Zhou
 */
public class DirectRequestDispatcher implements RequestDispatcher {

	public DirectRequestDispatcher(
		Servlet servlet, String path, String queryString) {

		_servlet = servlet;
		_path = path;
		_queryString = queryString;
	}

	@Override
	public void forward(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		servletRequest = DynamicServletRequest.addQueryString(
			(HttpServletRequest)servletRequest, _queryString);

		_servlet.service(servletRequest, servletResponse);
	}

	@Override
	public void include(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		servletRequest.setAttribute(RequestDispatcher.INCLUDE_PATH_INFO, null);
		servletRequest.setAttribute(
			RequestDispatcher.INCLUDE_SERVLET_PATH, _path);

		servletRequest = DynamicServletRequest.addQueryString(
			(HttpServletRequest)servletRequest, _queryString);

		_servlet.service(servletRequest, servletResponse);
	}

	private final String _path;
	private final String _queryString;
	private final Servlet _servlet;

}