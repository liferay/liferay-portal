/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import jakarta.servlet.ServletContext;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael Bradford
 */
public class PortletResourcesUtil {

	public static ServletContext getPathServletContext(String path) {
		for (ServletContext servletContext : _servletContexts.values()) {
			if (path.startsWith(servletContext.getContextPath())) {
				return servletContext;
			}
		}

		return null;
	}

	public static URL getResource(ServletContext servletContext, String path) {
		if (servletContext == null) {
			return null;
		}

		path = PortalWebResourcesUtil.stripContextPath(servletContext, path);

		try {
			URL url = servletContext.getResource(path);

			if (url != null) {
				return url;
			}
		}
		catch (MalformedURLException malformedURLException) {
			if (_log.isDebugEnabled()) {
				_log.debug(malformedURLException);
			}
		}

		return null;
	}

	public static URL getResource(String path) {
		ServletContext servletContext = getPathServletContext(path);

		if (servletContext != null) {
			return getResource(servletContext, path);
		}

		return null;
	}

	private PortletResourcesUtil() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletResourcesUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final ServiceTracker<Portlet, Portlet> _serviceTracker;
	private static final Map<ServiceReference<Portlet>, ServletContext>
		_servletContexts = new ConcurrentHashMap<>();

	private static class PortletResourcesServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Portlet, Portlet> {

		@Override
		public Portlet addingService(
			ServiceReference<Portlet> serviceReference) {

			Portlet portlet = _bundleContext.getService(serviceReference);

			PortletApp portletApp = portlet.getPortletApp();

			if (portletApp.isWARFile()) {
				_servletContexts.put(
					serviceReference, portletApp.getServletContext());
			}

			return portlet;
		}

		@Override
		public void modifiedService(
			ServiceReference<Portlet> serviceReference, Portlet portlet) {
		}

		@Override
		public void removedService(
			ServiceReference<Portlet> serviceReference, Portlet portlet) {

			_bundleContext.ungetService(serviceReference);

			_servletContexts.remove(serviceReference);
		}

	}

	static {
		_serviceTracker = new ServiceTracker<>(
			_bundleContext, Portlet.class,
			new PortletResourcesServiceTrackerCustomizer());

		_serviceTracker.open();
	}

}