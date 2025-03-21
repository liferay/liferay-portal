/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.module.framework;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.function.Supplier;

import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 */
public class ModuleFrameworkServletAdapter extends HttpServlet {

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		HttpServlet httpServlet = _supplier.get();

		if (httpServlet == null) {
			PortalUtil.sendError(
				HttpServletResponse.SC_SERVICE_UNAVAILABLE,
				new ServletException("Module framework is unavailable"),
				httpServletRequest, httpServletResponse);

			return;
		}

		httpServlet.service(httpServletRequest, httpServletResponse);
	}

	private static final Supplier<HttpServlet> _supplier;

	static {
		ServiceTracker<HttpServlet, HttpServlet> serviceTracker =
			new ServiceTracker<>(
				SystemBundleUtil.getBundleContext(),
				SystemBundleUtil.createFilter(
					"(&(bean.id=" + HttpServlet.class.getName() +
						")(original.bean=*))"),
				null);

		serviceTracker.open();

		_supplier = serviceTracker::getService;
	}

}