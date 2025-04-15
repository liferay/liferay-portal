/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.ServletContext;

import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.BundleContext;

/**
 * @author Peter Fellwock
 */
public class PortalWebResourcesUtil {

	public static String getContextPath(String resourceType) {
		String pathProxy = PortalUtil.getPathProxy();

		return pathProxy.concat(getModuleContextPath(resourceType));
	}

	public static long getLastModified(String resourceType) {
		PortalWebResources portalWebResources =
			_resourceTypeServiceTrackerMap.getService(resourceType);

		if (portalWebResources == null) {
			return -1;
		}

		return portalWebResources.getLastModified();
	}

	public static String getModuleContextPath(String resourceType) {
		PortalWebResources portalWebResources =
			_resourceTypeServiceTrackerMap.getService(resourceType);

		if (portalWebResources == null) {
			return StringPool.BLANK;
		}

		return portalWebResources.getContextPath();
	}

	public static long getPathLastModified(
		String requestURI, long defaultValue) {

		for (String contextPath : _contextPathServiceTrackerMap.keySet()) {
			if (requestURI.equals(Portal.PATH_MODULE) ||
				contextPath.startsWith(requestURI)) {

				PortalWebResources portalWebResources =
					_contextPathServiceTrackerMap.getService(contextPath);

				return portalWebResources.getLastModified();
			}
		}

		return defaultValue;
	}

	public static String getPathResourceType(String path) {
		for (String contextPath : _contextPathServiceTrackerMap.keySet()) {
			if (path.contains(contextPath)) {
				PortalWebResources portalWebResources =
					_contextPathServiceTrackerMap.getService(contextPath);

				return portalWebResources.getResourceType();
			}
		}

		return null;
	}

	public static ServletContext getPathServletContext(String path) {
		for (String contextPath : _contextPathServiceTrackerMap.keySet()) {
			PortalWebResources portalWebResources =
				_contextPathServiceTrackerMap.getService(contextPath);

			ServletContext servletContext =
				portalWebResources.getServletContext();

			URL url = getResource(servletContext, path);

			if (url != null) {
				return servletContext;
			}
		}

		return null;
	}

	public static PortalWebResources getPortalWebResources(
		String resourceType) {

		return _resourceTypeServiceTrackerMap.getService(resourceType);
	}

	public static URL getResource(ServletContext servletContext, String path) {
		if (servletContext == null) {
			return null;
		}

		path = stripContextPath(servletContext, path);

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

	public static ServletContext getServletContext(String resourceType) {
		PortalWebResources portalWebResources =
			_resourceTypeServiceTrackerMap.getService(resourceType);

		return portalWebResources.getServletContext();
	}

	public static boolean hasContextPath(String requestURI) {
		for (String contextPath : _contextPathServiceTrackerMap.keySet()) {
			if (requestURI.startsWith(contextPath)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAvailable(String path) {
		URL url = getResource(path);

		if (url != null) {
			return true;
		}

		return false;
	}

	public static String stripContextPath(
		ServletContext servletContext, String path) {

		String contextPath = servletContext.getContextPath();

		if (path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		return path;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalWebResourcesUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final ServiceTrackerMap<String, PortalWebResources>
		_contextPathServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext, PortalWebResources.class, null,
				ServiceReferenceMapperFactory.create(
					_bundleContext,
					(portalWebResources, emitter) -> emitter.emit(
						portalWebResources.getContextPath())));
	private static final ServiceTrackerMap<String, PortalWebResources>
		_resourceTypeServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext, PortalWebResources.class, null,
				ServiceReferenceMapperFactory.create(
					_bundleContext,
					(portalWebResources, emitter) -> emitter.emit(
						portalWebResources.getResourceType())));

}