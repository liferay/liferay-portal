/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.MetaInfoCacheServletResponse;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
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

import java.net.InetAddress;

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

	public StaticSiteExportResourceFile fetch(String url) throws Exception {
		StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(url);

		if (staticSiteExportURL.isExternal()) {
			return _fetchExternalStaticSiteExportResourceFile(
				staticSiteExportURL);
		}

		StaticSiteExportResourceFile staticSiteExportResourceFile = null;

		String moduleName = staticSiteExportURL.getModuleName();

		if (moduleName == null) {
			staticSiteExportResourceFile =
				_getServletStaticSiteExportResourceFile(
					_servletContext, staticSiteExportURL);
		}
		else {
			String dispatchPath = staticSiteExportURL.getDispatchPath();

			File file = _staticSiteExportBundleResourceResolver.resolve(
				moduleName, dispatchPath);

			if (file != null) {
				return new StaticSiteExportResourceFile(
					MimeTypesUtil.getContentType(dispatchPath), file);
			}

			staticSiteExportResourceFile =
				_getServletStaticSiteExportResourceFile(
					ServletContextPool.get(moduleName), staticSiteExportURL);
		}

		if (staticSiteExportResourceFile != null) {
			return staticSiteExportResourceFile;
		}

		return _fetchStaticSiteExportResourceFile(
			true, _portalURL + staticSiteExportURL.getURL());
	}

	private StaticSiteExportResourceFile
			_fetchExternalStaticSiteExportResourceFile(
				StaticSiteExportURL staticSiteExportURL)
		throws Exception {

		String scheme = staticSiteExportURL.getScheme();

		if ((scheme != null) &&
			!StringUtil.equalsIgnoreCase(scheme, Http.HTTP) &&
			!StringUtil.equalsIgnoreCase(scheme, Http.HTTPS)) {

			throw new IllegalArgumentException(
				"Scheme " + scheme + " is not supported");
		}

		String hostName = staticSiteExportURL.getHostName();

		if (Validator.isNull(hostName) || !_isPublicHost(hostName)) {
			throw new IllegalArgumentException(
				"Host " + hostName + " does not resolve to a public address");
		}

		String location = staticSiteExportURL.getURL();

		if (!staticSiteExportURL.hasScheme()) {
			location = Http.HTTPS + StringPool.COLON + location;
		}

		return _fetchStaticSiteExportResourceFile(false, location);
	}

	private StaticSiteExportResourceFile _fetchStaticSiteExportResourceFile(
			boolean followRedirects, String location)
		throws Exception {

		Http.Options options = new Http.Options();

		options.setFollowRedirects(followRedirects);
		options.setLocation(location);

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

		return new StaticSiteExportResourceFile(
			response.getContentType(), file);
	}

	private StaticSiteExportResourceFile
			_getServletStaticSiteExportResourceFile(
				ServletContext servletContext,
				StaticSiteExportURL staticSiteExportURL)
		throws Exception {

		if (servletContext == null) {
			return null;
		}

		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				servletContext, staticSiteExportURL.getDispatchPath());

		if (requestDispatcher == null) {
			return null;
		}

		HttpServletRequest httpServletRequest = _httpServletRequest;

		String queryString = staticSiteExportURL.getQueryString();

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
				new PathHttpServletRequestWrapper(
					httpServletRequest, staticSiteExportURL),
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

		return new StaticSiteExportResourceFile(
			metaInfoCacheServletResponse.getContentType(), file);
	}

	private boolean _isPublicHost(String hostName) throws Exception {
		for (InetAddress inetAddress : InetAddress.getAllByName(hostName)) {
			if (inetAddress.isAnyLocalAddress() ||
				inetAddress.isLinkLocalAddress() ||
				inetAddress.isLoopbackAddress() ||
				inetAddress.isMulticastAddress() ||
				inetAddress.isSiteLocalAddress()) {

				return false;
			}
		}

		return true;
	}

	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final String _portalURL;
	private final ServletContext _servletContext;
	private final StaticSiteExportBundleResourceResolver
		_staticSiteExportBundleResourceResolver;

	private static class PathHttpServletRequestWrapper
		extends HttpServletRequestWrapper {

		public PathHttpServletRequestWrapper(
			HttpServletRequest httpServletRequest,
			StaticSiteExportURL staticSiteExportURL) {

			super(httpServletRequest);

			_staticSiteExportURL = staticSiteExportURL;
		}

		@Override
		public String getPathInfo() {
			return _staticSiteExportURL.getPathInfo();
		}

		@Override
		public String getQueryString() {
			return _staticSiteExportURL.getQueryString();
		}

		@Override
		public String getRequestURI() {
			return _staticSiteExportURL.getDispatchPath();
		}

		@Override
		public String getServletPath() {
			return _staticSiteExportURL.getServletPath();
		}

		private final StaticSiteExportURL _staticSiteExportURL;

	}

}