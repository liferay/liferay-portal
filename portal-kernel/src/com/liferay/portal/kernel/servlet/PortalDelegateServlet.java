/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.InstanceFactory;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

/**
 * <p>
 * See https://issues.liferay.com/browse/LEP-2297.
 * </p>
 *
 * @author Olaf Fricke
 * @author Brian Wing Shun Chan
 */
public class PortalDelegateServlet extends SecureServlet {

	@Override
	public void destroy() {
		PortalDelegatorServlet.removeDelegate(_subcontext);

		super.destroy();
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		this.servletConfig = servletConfig;

		ServletContext servletContext = servletConfig.getServletContext();

		ClassLoader classLoader = (ClassLoader)servletContext.getAttribute(
			PluginContextListener.PLUGIN_CLASS_LOADER);

		String servletClass = servletConfig.getInitParameter("servlet-class");

		_subcontext = servletConfig.getInitParameter("sub-context");

		if (_subcontext == null) {
			_subcontext = getServletName();
		}

		try {
			servlet = (Servlet)InstanceFactory.newInstance(
				classLoader, servletClass);
		}
		catch (Exception exception) {
			throw new ServletException(exception);
		}

		if (!(servlet instanceof HttpServlet)) {
			throw new IllegalArgumentException(
				"servlet-class is not an instance of " +
					HttpServlet.class.getName());
		}

		servlet.init(servletConfig);

		PortalDelegatorServlet.addDelegate(_subcontext, (HttpServlet)servlet);
	}

	private String _subcontext;

}