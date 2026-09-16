/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.MetaInfoCacheServletResponse;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportResourceFetcher {

	public StaticSiteExportResourceFetcher(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String portalURL,
		ServletContext servletContext,
		StaticSiteExportBundleResourceResolver
			staticSiteExportBundleResourceResolver) {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_portalURL = portalURL;
		_servletContext = servletContext;
		_staticSiteExportBundleResourceResolver =
			staticSiteExportBundleResourceResolver;
	}

	public File fetch(String url) throws Exception {
		String path = HttpComponentsUtil.getPath(url);

		File file = null;

		String moduleName = _getModuleName(path);

		if (moduleName == null) {
			file = _getServletFile(path, _servletContext, url);
		}
		else {
			String resourcePath = StringUtil.removeFirst(
				path, _MODULE_PATH_PREFIX + moduleName);

			file = _staticSiteExportBundleResourceResolver.resolve(
				moduleName, resourcePath);

			if (file == null) {
				file = _getServletFile(
					resourcePath, ServletContextPool.get(moduleName), url);
			}
		}

		if (file != null) {
			return file;
		}

		return _fetchFile(url);
	}

	private File _fetchFile(String url) throws Exception {
		Http.Options options = new Http.Options();

		options.setFollowRedirects(true);
		options.setLocation(_portalURL + url);

		File file = FileUtil.createTempFile();

		try (InputStream inputStream = HttpUtil.URLtoInputStream(options);
			OutputStream outputStream = new FileOutputStream(file)) {

			StreamUtil.transfer(inputStream, outputStream);
		}

		Http.Response response = options.getResponse();

		if ((file.length() == 0) ||
			(response.getResponseCode() != HttpServletResponse.SC_OK)) {

			FileUtil.delete(file);

			return null;
		}

		return file;
	}

	private String _getModuleName(String path) {
		if (!path.startsWith(_MODULE_PATH_PREFIX)) {
			return null;
		}

		int index = path.indexOf(CharPool.SLASH, _MODULE_PATH_PREFIX.length());

		if (index == -1) {
			return null;
		}

		return path.substring(_MODULE_PATH_PREFIX.length(), index);
	}

	private File _getServletFile(
			String path, ServletContext servletContext, String url)
		throws Exception {

		if (servletContext == null) {
			return null;
		}

		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				servletContext, path);

		if (requestDispatcher == null) {
			return null;
		}

		HttpServletRequest httpServletRequest = _httpServletRequest;

		String queryString = HttpComponentsUtil.getQueryString(url);

		if (Validator.isNotNull(queryString)) {
			httpServletRequest = DynamicServletRequest.addQueryString(
				httpServletRequest, queryString, false);
		}

		File file = FileUtil.createTempFile();

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(_httpServletResponse);

		try (OutputStream outputStream = new FileOutputStream(file)) {
			PipingServletResponse pipingServletResponse =
				new PipingServletResponse(
					metaInfoCacheServletResponse, outputStream);

			requestDispatcher.include(
				new PathHttpServletRequestWrapper(httpServletRequest, path),
				pipingServletResponse);

			PrintWriter printWriter = pipingServletResponse.getWriter();

			printWriter.flush();
		}

		if ((file.length() == 0) ||
			(metaInfoCacheServletResponse.getStatus() !=
				HttpServletResponse.SC_OK)) {

			FileUtil.delete(file);

			return null;
		}

		return file;
	}

	private static final String _MODULE_PATH_PREFIX = "/o/";

	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final String _portalURL;
	private final ServletContext _servletContext;
	private final StaticSiteExportBundleResourceResolver
		_staticSiteExportBundleResourceResolver;

	private static class PathHttpServletRequestWrapper
		extends HttpServletRequestWrapper {

		public PathHttpServletRequestWrapper(
			HttpServletRequest httpServletRequest, String path) {

			super(httpServletRequest);

			_path = path;
		}

		@Override
		public String getPathInfo() {
			int index = _path.indexOf(CharPool.SLASH, 1);

			if (index == -1) {
				return null;
			}

			return _path.substring(index);
		}

		@Override
		public String getRequestURI() {
			return _path;
		}

		@Override
		public String getServletPath() {
			int index = _path.indexOf(CharPool.SLASH, 1);

			if (index == -1) {
				return _path;
			}

			return _path.substring(0, index);
		}

		private final String _path;

	}

}