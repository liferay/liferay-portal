/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.util.Collection;

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

	public File resolve(String moduleName, String resourcePath)
		throws Exception {

		Collection<ServiceReference<ServletContextHelper>> serviceReferences =
			_bundleContext.getServiceReferences(
				ServletContextHelper.class,
				StringBundler.concat(
					StringPool.OPEN_PARENTHESIS,
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "=/",
					moduleName, StringPool.CLOSE_PARENTHESIS));

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

	private File _getFile(URL url) throws Exception {
		File file = FileUtil.createTempFile();

		try (InputStream inputStream = url.openStream();
			OutputStream outputStream = new FileOutputStream(file)) {

			StreamUtil.transfer(inputStream, outputStream);
		}

		return file;
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

	private final BundleContext _bundleContext;

}