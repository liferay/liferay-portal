/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 */
public class PortalClassLoaderServlet extends HttpServlet {

	@Override
	public void destroy() {
		if (_servlet != null) {
			try (SafeCloseable safeCloseable =
					ThreadContextClassLoaderUtil.swap(
						PortalClassLoaderUtil.getClassLoader())) {

				_servlet.destroy();
			}
		}
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		_servletConfig = servletConfig;

		ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				portalClassLoader)) {

			String servletClass = _servletConfig.getInitParameter(
				"servlet-class");

			_servlet = (HttpServlet)InstanceFactory.newInstance(
				portalClassLoader, servletClass);

			_servlet.init(_servletConfig);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			_servlet.service(httpServletRequest, httpServletResponse);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalClassLoaderServlet.class);

	private volatile HttpServlet _servlet;
	private ServletConfig _servletConfig;

}