/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportBundleResourceResolver {

	public StaticSiteExportBundleResourceResolver(BundleContext bundleContext) {
		for (Bundle bundle : bundleContext.getBundles()) {
			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			String webContextPath = headers.get("Web-ContextPath");

			if (Validator.isNotNull(webContextPath)) {
				_bundles.put(
					StringUtil.removeFirst(webContextPath, StringPool.SLASH),
					bundle);
			}
		}
	}

	public File resolve(String path) throws Exception {
		if (!path.startsWith(_MODULE_PATH_PREFIX)) {
			return null;
		}

		int index = path.indexOf(CharPool.SLASH, _MODULE_PATH_PREFIX.length());

		if (index == -1) {
			return null;
		}

		Bundle bundle = _bundles.get(
			path.substring(_MODULE_PATH_PREFIX.length(), index));

		if (bundle == null) {
			return null;
		}

		URL url = _getEntryURL(bundle, path.substring(index));

		if (url == null) {
			return null;
		}

		File file = FileUtil.createTempFile();

		try (InputStream inputStream = url.openStream();
			OutputStream outputStream = new FileOutputStream(file)) {

			StreamUtil.transfer(inputStream, outputStream);
		}

		return file;
	}

	private URL _getEntryURL(Bundle bundle, String path) {
		for (String prefix : _ENTRY_PREFIXES) {
			URL url = bundle.getEntry(prefix + path);

			if (url != null) {
				return url;
			}
		}

		String unhashedPath = _unhash(path);

		for (String prefix : _ENTRY_PREFIXES) {
			URL url = bundle.getEntry(prefix + unhashedPath);

			if (url != null) {
				return url;
			}
		}

		int index = unhashedPath.lastIndexOf(CharPool.SLASH);

		String fileName = unhashedPath.substring(index + 1);

		for (String prefix : _ENTRY_PREFIXES) {
			String directoryPath = prefix + unhashedPath.substring(0, index);

			if (Validator.isNull(directoryPath)) {
				directoryPath = StringPool.SLASH;
			}

			Enumeration<URL> enumeration = bundle.findEntries(
				directoryPath, StringPool.STAR, false);

			while ((enumeration != null) && enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				String urlPath = url.getPath();

				String candidateFileName = urlPath.substring(
					urlPath.lastIndexOf(CharPool.SLASH) + 1);

				if (fileName.equals(_unhash(candidateFileName))) {
					return url;
				}
			}
		}

		return null;
	}

	private String _unhash(String path) {
		int index = path.indexOf(".(");

		if (index == -1) {
			return path;
		}

		int endIndex = path.indexOf(CharPool.CLOSE_PARENTHESIS, index);

		if (endIndex == -1) {
			return path;
		}

		return path.substring(0, index) + path.substring(endIndex + 1);
	}

	private static final String[] _ENTRY_PREFIXES = {
		"META-INF/resources", StringPool.BLANK
	};

	private static final String _MODULE_PATH_PREFIX = "/o/";

	private final Map<String, Bundle> _bundles = new HashMap<>();

}