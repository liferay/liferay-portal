/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringPool;

import jakarta.servlet.ServletContext;

import java.net.URL;

import java.util.Collection;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
public class JSBundleConfigRegistryUtil {

	public static Collection<JSConfig> getJSConfigs() {
		return _serviceTrackerList.toList();
	}

	public static long getLastModified() {
		return _lastModified;
	}

	public static class JSConfig {

		public ServletContext getServletContext() {
			return _servletContext;
		}

		public URL getURL() {
			return _url;
		}

		private JSConfig(ServletContext servletContext, URL url) {
			_servletContext = servletContext;
			_url = url;
		}

		private final ServletContext _servletContext;
		private final URL _url;

	}

	private static volatile long _lastModified = System.currentTimeMillis();
	private static final ServiceTrackerList<JSConfig> _serviceTrackerList;

	private static class JSBundleConfigServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<ServletContext, JSConfig> {

		public JSBundleConfigServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public JSConfig addingService(
			ServiceReference<ServletContext> serviceReference) {

			Bundle bundle = serviceReference.getBundle();

			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			String jsConfig = headers.get("Liferay-JS-Config");

			if (jsConfig == null) {
				return null;
			}

			URL url = bundle.getEntry(jsConfig);

			if (url == null) {
				return null;
			}

			ServletContext servletContext = _bundleContext.getService(
				serviceReference);

			_lastModified = System.currentTimeMillis();

			return new JSConfig(servletContext, url);
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContext> serviceReference,
			JSConfig jsConfig) {
		}

		@Override
		public void removedService(
			ServiceReference<ServletContext> serviceReference,
			JSConfig jsConfig) {

			if (jsConfig != null) {
				_bundleContext.ungetService(serviceReference);

				_lastModified = System.currentTimeMillis();
			}
		}

		private final BundleContext _bundleContext;

	}

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			JSBundleConfigRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ServletContext.class, "(osgi.web.contextpath=*)",
			new JSBundleConfigServiceTrackerCustomizer(bundleContext));
	}

}