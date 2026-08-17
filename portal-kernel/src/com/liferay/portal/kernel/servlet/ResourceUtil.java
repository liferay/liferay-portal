/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;

import jakarta.servlet.ServletContext;

import java.io.IOException;

import java.net.URL;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Minhchau Dang
 */
public class ResourceUtil {

	public static ObjectValuePair<ServletContext, URL> getObjectValuePair(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ServletContext servletContext = defaultServletContext;

		URL resourceURL = servletContext.getResource(requestURI);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		servletContext = PortalWebResourcesUtil.getPathServletContext(
			requestPath);

		resourceURL = PortalWebResourcesUtil.getResource(
			servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		for (ServletContext portletServletContext : _portletServiceTrackerList) {
			if (requestPath.startsWith(
					portletServletContext.getContextPath())) {

				servletContext = portletServletContext;

				break;
			}
		}

		resourceURL = PortalWebResourcesUtil.getResource(
			servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		servletContext = DynamicResourceIncludeUtil.getPathServletContext(
			requestPath);

		resourceURL = PortalWebResourcesUtil.getResource(
			servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		return null;
	}

	public static ServletContext getPathServletContext(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ObjectValuePair<ServletContext, URL> objectValuePair =
			getObjectValuePair(requestPath, requestURI, defaultServletContext);

		if (objectValuePair == null) {
			return null;
		}

		return objectValuePair.getKey();
	}

	public static URL getResourceURL(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ObjectValuePair<ServletContext, URL> objectValuePair =
			getObjectValuePair(requestPath, requestURI, defaultServletContext);

		if (objectValuePair == null) {
			return null;
		}

		return objectValuePair.getValue();
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerList<ServletContext>
		_portletServiceTrackerList = ServiceTrackerListFactory.open(
			_bundleContext, Portlet.class, null,
			new ServiceTrackerCustomizer<Portlet, ServletContext>() {

				@Override
				public ServletContext addingService(
					ServiceReference<Portlet> serviceReference) {

					Portlet portlet = _bundleContext.getService(
						serviceReference);

					PortletApp portletApp = portlet.getPortletApp();

					if (portletApp.isWARFile()) {
						return portletApp.getServletContext();
					}

					_bundleContext.ungetService(serviceReference);

					return null;
				}

				@Override
				public void modifiedService(
					ServiceReference<Portlet> serviceReference,
					ServletContext servletContext) {
				}

				@Override
				public void removedService(
					ServiceReference<Portlet> serviceReference,
					ServletContext servletContext) {

					_bundleContext.ungetService(serviceReference);
				}

			});

}