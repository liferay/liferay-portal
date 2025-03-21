/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Pavel Savinov
 */
public class ServletAdapter extends HttpServlet {

	@Override
	public void destroy() {
		super.destroy();

		_serviceTracker.close();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		Filter filter = SystemBundleUtil.createFilter(
			StringBundler.concat(
				"(&", config.getInitParameter("filter"), "(objectClass=",
				Servlet.class.getName(), "))"));

		_serviceTracker = new ServiceTracker<>(
			_bundleContext, filter, new ServletTrackerCustomizer());

		_serviceTracker.open();
	}

	protected Servlet getServlet() {
		return _serviceTracker.getService();
	}

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		Servlet servlet = getServlet();

		if (servlet == null) {
			ServletConfig servletConfig = getServletConfig();

			PortalUtil.sendError(
				HttpServletResponse.SC_SERVICE_UNAVAILABLE,
				new ServletException(
					"A servlet matching the filter " +
						servletConfig.getInitParameter("filter") +
							" is unavailable"),
				httpServletRequest, httpServletResponse);

			return;
		}

		servlet.service(httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(ServletAdapter.class);

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private ServiceTracker<Servlet, Servlet> _serviceTracker;

	private class ServletTrackerCustomizer
		implements ServiceTrackerCustomizer<Servlet, Servlet> {

		@Override
		public Servlet addingService(
			ServiceReference<Servlet> serviceReference) {

			ServletConfig servletConfig = new ServletConfig() {

				@Override
				public String getInitParameter(String name) {
					return GetterUtil.getString(
						serviceReference.getProperty(name), null);
				}

				@Override
				public Enumeration<String> getInitParameterNames() {
					return Collections.enumeration(
						Arrays.asList(serviceReference.getPropertyKeys()));
				}

				@Override
				public ServletContext getServletContext() {
					return ServletContextPool.get(
						PortalUtil.getServletContextName());
				}

				@Override
				public String getServletName() {
					return GetterUtil.getString(
						serviceReference.getProperty(
							"osgi.http.whiteboard.servlet.name"));
				}

			};

			Servlet servlet = _bundleContext.getService(serviceReference);

			try {
				servlet.init(servletConfig);
			}
			catch (ServletException servletException) {
				_log.error("Unable to initialize servlet", servletException);
			}

			return servlet;
		}

		@Override
		public void modifiedService(
			ServiceReference<Servlet> serviceReference, Servlet service) {
		}

		@Override
		public void removedService(
			ServiceReference<Servlet> serviceReference, Servlet service) {

			service.destroy();

			_bundleContext.ungetService(serviceReference);
		}

	}

}