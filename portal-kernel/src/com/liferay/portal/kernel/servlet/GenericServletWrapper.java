/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

/**
 * @author Eduardo García
 * @author Jorge Ferrer
 */
public class GenericServletWrapper extends GenericServlet {

	public GenericServletWrapper(Servlet servlet) {
		_servlet = servlet;
	}

	@Override
	public void service(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		_servlet.service(servletRequest, servletResponse);
	}

	private final Servlet _servlet;

}