/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal.adapter;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class JspServletWrapper extends HttpServlet {

	public JspServletWrapper(Servlet servlet, String jspFile) {
		_servlet = servlet;
		_jspFile = jspFile;
	}

	@Override
	public void destroy() {
		_servlet.destroy();
	}

	@Override
	public ServletConfig getServletConfig() {
		return _servlet.getServletConfig();
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		_servlet.init(servletConfig);
	}

	@Override
	public void service(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		String curJspFile = (String)servletRequest.getAttribute(_JSP_FILE);

		if (_jspFile != null) {
			servletRequest.setAttribute(_JSP_FILE, _jspFile);
		}

		try {
			_servlet.service(servletRequest, servletResponse);
		}
		finally {
			servletRequest.setAttribute(_JSP_FILE, curJspFile);
		}
	}

	private static final String _JSP_FILE = "org.apache.catalina.jsp_file";

	private final String _jspFile;
	private final Servlet _servlet;

}