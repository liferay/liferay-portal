/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.internal.util.ContextResourcePathsUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;

import jakarta.servlet.ServletContext;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author James Lefeu
 */
public class ServletContextUtil {

	public static final String PATH_WEB_INF = "/WEB-INF";

	public static final String URI_ATTRIBUTE =
		ServletContextUtil.class.getName() + ".rootURI";

	public static Set<String> getClassNames(ServletContext servletContext)
		throws IOException {

		Set<String> classNames = new HashSet<>();

		_getClassNames(servletContext, "/WEB-INF/classes", classNames);
		_getClassNames(servletContext, "/WEB-INF/lib", classNames);

		return classNames;
	}

	public static long getLastModified(ServletContext servletContext) {
		return getLastModified(servletContext, StringPool.SLASH);
	}

	public static long getLastModified(
		ServletContext servletContext, String path) {

		return getLastModified(servletContext, path, false);
	}

	public static long getLastModified(
		ServletContext servletContext, String path, boolean cache) {

		String lastModifiedCacheKey = null;

		if (cache) {
			lastModifiedCacheKey = ServletContextUtil.class.getName();
			lastModifiedCacheKey = StringBundler.concat(
				lastModifiedCacheKey, StringPool.PERIOD, path);

			Long lastModified = (Long)servletContext.getAttribute(
				lastModifiedCacheKey);

			if (lastModified != null) {
				return lastModified.longValue();
			}
		}

		long lastModified = _getLastModified(servletContext, path);

		if (cache) {
			servletContext.setAttribute(
				lastModifiedCacheKey, Long.valueOf(lastModified));
		}

		return lastModified;
	}

	public static String getResourcePath(URL url) throws URISyntaxException {
		URI uri = getResourceURI(url);

		return uri.toString();
	}

	public static URI getResourceURI(URL url) throws URISyntaxException {
		return new URI(url.getProtocol(), url.getPath(), null);
	}

	public static String getRootPath(ServletContext servletContext)
		throws MalformedURLException {

		return String.valueOf(getRootURI(servletContext));
	}

	public static URI getRootURI(ServletContext servletContext)
		throws MalformedURLException {

		URI rootURI = (URI)servletContext.getAttribute(URI_ATTRIBUTE);

		if (rootURI != null) {
			return rootURI;
		}

		try {
			URL rootURL = servletContext.getResource(PATH_WEB_INF);

			String path = rootURL.getPath();

			int index = path.indexOf(PATH_WEB_INF);

			if (index < 0) {
				throw new MalformedURLException("Invalid URL " + rootURL);
			}

			if (index == 0) {
				path = StringPool.SLASH;
			}
			else {
				path = path.substring(0, index);
			}

			rootURI = new URI(rootURL.getProtocol(), path, null);

			servletContext.setAttribute(URI_ATTRIBUTE, rootURI);
		}
		catch (URISyntaxException uriSyntaxException) {
			throw new MalformedURLException(uriSyntaxException.getMessage());
		}

		return rootURI;
	}

	private static String _getClassName(String rootPath, String path) {
		String className = path.substring(
			0, path.length() - _EXT_CLASS.length());

		if (rootPath != null) {
			className = className.substring(rootPath.length() + 1);
		}

		return StringUtil.replace(className, CharPool.SLASH, CharPool.PERIOD);
	}

	private static void _getClassNames(
			ServletContext servletContext, String rootPath,
			Set<String> classNames)
		throws IOException {

		_getClassNames(
			servletContext, rootPath, servletContext.getResourcePaths(rootPath),
			classNames);
	}

	private static void _getClassNames(
			ServletContext servletContext, String rootPath, Set<String> paths,
			Set<String> classNames)
		throws IOException {

		if (paths == null) {
			return;
		}

		for (String path : paths) {
			if (path.endsWith(_EXT_CLASS)) {
				classNames.add(_getClassName(rootPath, path));
			}
			else if (path.endsWith(_EXT_JAR)) {
				try (JarInputStream jarInputStream = new JarInputStream(
						servletContext.getResourceAsStream(path))) {

					while (true) {
						JarEntry jarEntry = jarInputStream.getNextJarEntry();

						if (jarEntry == null) {
							break;
						}

						String jarEntryName = jarEntry.getName();

						if (jarEntryName.endsWith(_EXT_CLASS)) {
							classNames.add(_getClassName(null, jarEntryName));
						}
					}
				}
			}
			else if (path.endsWith(StringPool.SLASH)) {
				_getClassNames(
					servletContext, rootPath,
					servletContext.getResourcePaths(path), classNames);
			}
		}
	}

	private static long _getLastModified(
		ServletContext servletContext, String path) {

		boolean root = StringPool.SLASH.equals(path);

		Long lastModifiedLong = null;

		if (root) {
			lastModifiedLong = (Long)servletContext.getAttribute(
				_LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED);

			if (lastModifiedLong != null) {
				return lastModifiedLong;
			}
		}

		lastModifiedLong = ContextResourcePathsUtil.visitResources(
			servletContext, path, null,
			enumeration -> {
				long lastModified = 0;

				if (enumeration == null) {
					return lastModified;
				}

				while (enumeration.hasMoreElements()) {
					URL url = enumeration.nextElement();

					String curPath = url.getPath();

					if (curPath.charAt(curPath.length() - 1) ==
							CharPool.SLASH) {

						continue;
					}

					try {
						long curLastModified = URLUtil.getLastModifiedTime(url);

						if (curLastModified > lastModified) {
							lastModified = curLastModified;
						}
					}
					catch (IOException ioException) {
						if (_log.isDebugEnabled()) {
							_log.debug(ioException);
						}
					}
				}

				return lastModified;
			});

		if (lastModifiedLong != null) {
			if (root) {
				servletContext.setAttribute(
					_LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED,
					lastModifiedLong);
			}

			return lastModifiedLong;
		}

		String curPath = null;

		long lastModified = 0;

		Queue<String> pathQueue = new LinkedList<>();

		pathQueue.offer(path);

		while ((curPath = pathQueue.poll()) != null) {
			if (curPath.charAt(curPath.length() - 1) == CharPool.SLASH) {
				Set<String> pathSet = servletContext.getResourcePaths(curPath);

				if (pathSet != null) {
					pathQueue.addAll(pathSet);
				}
			}
			else {
				long curLastModified = FileTimestampUtil.getTimestamp(
					servletContext, curPath);

				if (curLastModified > lastModified) {
					lastModified = curLastModified;
				}
			}
		}

		return lastModified;
	}

	private static final String _EXT_CLASS = ".class";

	private static final String _EXT_JAR = ".jar";

	private static final String _LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED =
		"LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED";

	private static final Log _log = LogFactoryUtil.getLog(
		ServletContextUtil.class);

}