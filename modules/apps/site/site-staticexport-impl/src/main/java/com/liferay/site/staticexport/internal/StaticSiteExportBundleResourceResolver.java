/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportBundleResourceResolver {

	public StaticSiteExportBundleResourceResolver(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	public Set<String> getResourcePaths(String moduleName, String resourcePath)
		throws Exception {

		Set<String> resourcePaths = new LinkedHashSet<>();

		Collection<ServiceReference<ServletContextHelper>> serviceReferences =
			_bundleContext.getServiceReferences(
				ServletContextHelper.class, _getFilterString(moduleName));

		for (ServiceReference<ServletContextHelper> serviceReference :
				serviceReferences) {

			ServletContextHelper servletContextHelper =
				_bundleContext.getService(serviceReference);

			try {
				_collectResourcePaths(
					_RESOURCES_PATH + resourcePath, resourcePaths,
					servletContextHelper);
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		}

		return resourcePaths;
	}

	public File resolve(String moduleName, String resourcePath)
		throws Exception {

		Collection<ServiceReference<ServletContextHelper>> serviceReferences =
			_bundleContext.getServiceReferences(
				ServletContextHelper.class, _getFilterString(moduleName));

		for (ServiceReference<ServletContextHelper> serviceReference :
				serviceReferences) {

			ServletContextHelper servletContextHelper =
				_bundleContext.getService(serviceReference);

			try {
				URL url = _getResourceURL(resourcePath, servletContextHelper);

				if (url != null) {
					return _getFile(url);
				}
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		}

		return null;
	}

	private void _collectResourcePaths(
		String resourcePath, Set<String> resourcePaths,
		ServletContextHelper servletContextHelper) {

		Set<String> childResourcePaths = servletContextHelper.getResourcePaths(
			resourcePath);

		if (childResourcePaths == null) {
			return;
		}

		for (String childResourcePath : childResourcePaths) {
			if (childResourcePath.endsWith(StringPool.SLASH)) {
				_collectResourcePaths(
					childResourcePath, resourcePaths, servletContextHelper);
			}
			else {
				resourcePaths.add(
					StringUtil.removeFirst(childResourcePath, _RESOURCES_PATH));
			}
		}
	}

	private File _getFile(URL url) throws Exception {
		File file = FileUtil.createTempFile();

		try (InputStream inputStream = url.openStream();
			OutputStream outputStream = new FileOutputStream(file)) {

			StreamUtil.transfer(inputStream, outputStream);
		}

		return file;
	}

	private String _getFilterString(String moduleName) {
		return StringBundler.concat(
			StringPool.OPEN_PARENTHESIS,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "=/",
			SystemBundleUtil.escapeFilterValue(moduleName),
			StringPool.CLOSE_PARENTHESIS);
	}

	private URL _getResourceURL(
		String resourcePath, ServletContextHelper servletContextHelper) {

		URL url = servletContextHelper.getResource(resourcePath);

		if (url != null) {
			return url;
		}

		if (!HashedFilesUtil.containsHash(resourcePath)) {
			return null;
		}

		return servletContextHelper.getResource(
			HashedFilesUtil.removeHash(resourcePath));
	}

	private static final String _RESOURCES_PATH = "/META-INF/resources";

	private final BundleContext _bundleContext;

}