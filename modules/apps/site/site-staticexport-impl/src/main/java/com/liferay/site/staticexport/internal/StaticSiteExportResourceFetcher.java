/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportResourceFetcher {

	public StaticSiteExportResourceFetcher(
		BundleContext bundleContext, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, ServletContext servletContext,
		StaticSiteExportBundleResourceResolver
			staticSiteExportBundleResourceResolver) {

		_bundleContext = bundleContext;
		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_servletContext = servletContext;
		_staticSiteExportBundleResourceResolver =
			staticSiteExportBundleResourceResolver;
	}

	public File fetch(String url) throws Exception {
		String path = HttpComponentsUtil.getPath(url);

		File file = _staticSiteExportBundleResourceResolver.resolve(path);

		if (file != null) {
			return file;
		}

		file = _include(path, url);

		if (file != null) {
			return file;
		}

		return _service(path, url);
	}

	private HttpServletRequest _getHttpServletRequest(String url) {
		String queryString = HttpComponentsUtil.getQueryString(url);

		if (Validator.isNull(queryString)) {
			return _httpServletRequest;
		}

		return DynamicServletRequest.addQueryString(
			_httpServletRequest, queryString, false);
	}

	private int _getMatchLength(String path, Object patterns) {
		if (patterns instanceof String[]) {
			int matchLength = -1;

			for (String pattern : (String[])patterns) {
				matchLength = Math.max(
					matchLength, _getMatchLength(path, pattern));
			}

			return matchLength;
		}

		return _getMatchLength(path, String.valueOf(patterns));
	}

	private int _getMatchLength(String path, String pattern) {
		if (pattern.equals(path)) {
			return Integer.MAX_VALUE;
		}

		if (!pattern.endsWith("/*")) {
			return -1;
		}

		String prefix = pattern.substring(0, pattern.length() - 2);

		if (prefix.isEmpty() || path.equals(prefix) ||
			!path.startsWith(prefix + StringPool.SLASH)) {

			return -1;
		}

		return prefix.length();
	}

	private File _include(String path, String url) throws Exception {
		ServletContext servletContext = _servletContext;

		if (path.startsWith(_MODULE_PATH_PREFIX)) {
			int slashIndex = path.indexOf(
				CharPool.SLASH, _MODULE_PATH_PREFIX.length());

			if (slashIndex == -1) {
				return null;
			}

			servletContext = ServletContextPool.get(
				path.substring(_MODULE_PATH_PREFIX.length(), slashIndex));

			path = path.substring(slashIndex);
		}

		if (servletContext == null) {
			return null;
		}

		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				servletContext, path);

		if (requestDispatcher == null) {
			return null;
		}

		return _write(
			new PathHttpServletRequestWrapper(
				_getHttpServletRequest(url), path, false),
			requestDispatcher::include);
	}

	private File _service(String path, String url) throws Exception {
		if (!path.startsWith(_MODULE_PATH_PREFIX)) {
			return null;
		}

		String servletPath = path.substring(_MODULE_PATH_PREFIX.length() - 1);

		int bestMatchLength = -1;
		ServiceReference<Servlet> bestServiceReference = null;

		Collection<ServiceReference<Servlet>> serviceReferences =
			_bundleContext.getServiceReferences(
				Servlet.class, "(osgi.http.whiteboard.servlet.pattern=*)");

		for (ServiceReference<Servlet> serviceReference : serviceReferences) {
			int matchLength = _getMatchLength(
				servletPath,
				serviceReference.getProperty(
					"osgi.http.whiteboard.servlet.pattern"));

			if (matchLength > bestMatchLength) {
				bestMatchLength = matchLength;
				bestServiceReference = serviceReference;
			}
		}

		if (bestServiceReference == null) {
			return null;
		}

		Servlet servlet = _bundleContext.getService(bestServiceReference);

		try {
			return _write(
				new PathHttpServletRequestWrapper(
					_getHttpServletRequest(url), servletPath, true),
				servlet::service);
		}
		finally {
			_bundleContext.ungetService(bestServiceReference);
		}
	}

	private File _write(
			HttpServletRequest httpServletRequest,
			UnsafeBiConsumer<HttpServletRequest, HttpServletResponse, Exception>
				unsafeBiConsumer)
		throws Exception {

		File file = FileUtil.createTempFile();

		StatusHttpServletResponseWrapper statusHttpServletResponseWrapper =
			new StatusHttpServletResponseWrapper(_httpServletResponse);

		try (OutputStream outputStream = new FileOutputStream(file)) {
			PipingServletResponse pipingServletResponse =
				new PipingServletResponse(
					statusHttpServletResponseWrapper, outputStream);

			unsafeBiConsumer.accept(httpServletRequest, pipingServletResponse);

			PrintWriter printWriter = pipingServletResponse.getWriter();

			printWriter.flush();
		}

		if ((file.length() == 0) ||
			(statusHttpServletResponseWrapper.getStatus() !=
				HttpServletResponse.SC_OK)) {

			FileUtil.delete(file);

			return null;
		}

		return file;
	}

	private static final String _MODULE_PATH_PREFIX = "/o/";

	private final BundleContext _bundleContext;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final ServletContext _servletContext;
	private final StaticSiteExportBundleResourceResolver
		_staticSiteExportBundleResourceResolver;

	private static class PathHttpServletRequestWrapper
		extends HttpServletRequestWrapper {

		public PathHttpServletRequestWrapper(
			HttpServletRequest httpServletRequest, String path,
			boolean themeDisplayHidden) {

			super(httpServletRequest);

			_path = path;
			_themeDisplayHidden = themeDisplayHidden;
		}

		@Override
		public Object getAttribute(String name) {
			if (_themeDisplayHidden &&
				StringUtil.equals(name, WebKeys.THEME_DISPLAY)) {

				return null;
			}

			return super.getAttribute(name);
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
		private final boolean _themeDisplayHidden;

	}

	private static class StatusHttpServletResponseWrapper
		extends HttpServletResponseWrapper {

		public StatusHttpServletResponseWrapper(
			HttpServletResponse httpServletResponse) {

			super(httpServletResponse);
		}

		@Override
		public int getStatus() {
			return _status;
		}

		@Override
		public void sendError(int status) {
			_status = status;
		}

		@Override
		public void sendError(int status, String message) {
			_status = status;
		}

		@Override
		public void setStatus(int status) {
			_status = status;
		}

		private int _status = HttpServletResponse.SC_OK;

	}

}