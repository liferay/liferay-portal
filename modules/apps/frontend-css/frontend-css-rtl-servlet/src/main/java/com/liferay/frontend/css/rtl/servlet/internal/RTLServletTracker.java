/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.css.rtl.servlet.internal;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.Servlet;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = {})
public class RTLServletTracker {

	@Activate
	protected void activate(final BundleContext bundleContext) {
		String filterString = StringBundler.concat(
			"(&(objectClass=", ServletContextHelper.class.getName(), ")",
			"(rtl.required=true))");

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, filterString,
			new ServiceTrackerCustomizer
				<ServletContextHelper, ServiceRegistration<Servlet>>() {

				@Override
				public ServiceRegistration<Servlet> addingService(
					ServiceReference<ServletContextHelper> serviceReference) {

					ServletContextHelper servletContextHelper =
						bundleContext.getService(serviceReference);

					Servlet servlet = new RTLServlet(
						serviceReference.getBundle(), servletContextHelper);

					return bundleContext.registerService(
						Servlet.class, servlet,
						_buildProperties(serviceReference));
				}

				@Override
				public void modifiedService(
					ServiceReference<ServletContextHelper> serviceReference,
					ServiceRegistration<Servlet> serviceRegistration) {

					serviceRegistration.setProperties(
						_buildProperties(serviceReference));
				}

				@Override
				public void removedService(
					ServiceReference<ServletContextHelper> serviceReference,
					ServiceRegistration<Servlet> serviceRegistration) {

					serviceRegistration.unregister();

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private Hashtable<String, Object> _buildProperties(
		ServiceReference<ServletContextHelper> serviceReference) {

		Hashtable<String, Object> properties = new Hashtable<>();

		Object contextName = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME);

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
			contextName);

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
			RTLServlet.class.getName());
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "*.css");

		Bundle bundle = serviceReference.getBundle();

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		int themeContributorWeight = GetterUtil.getInteger(
			headers.get("Liferay-Theme-Contributor-Weight"));

		properties.put("service.ranking", themeContributorWeight);

		return properties;
	}

	private ServiceTracker<ServletContextHelper, ServiceRegistration<Servlet>>
		_serviceTracker;

}