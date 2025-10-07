/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;
import java.net.URLConnection;

import org.osgi.service.http.context.ServletContextHelper;

/**
 * @author Dante Wang
 */
public class ResourceServlet extends HttpServlet {

	public ResourceServlet(
		String internalName, ServletContextHelper servletContextHelper) {

		if (internalName.equals(StringPool.SLASH)) {
			_internalName = StringPool.BLANK;
		}
		else {
			_internalName = internalName;
		}

		_servletContextHelper = servletContextHelper;
	}

	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String method = httpServletRequest.getMethod();

		if (!method.equals("GET") && !method.equals("POST") &&
			!method.equals("HEAD")) {

			httpServletResponse.setStatus(405);

			return;
		}

		String pathInfo = LiferayHttpServletRequestWrapper.getDispatchPathInfo(
			httpServletRequest);

		if (pathInfo == null) {
			pathInfo = StringPool.BLANK;
		}

		String resourcePath = _internalName + pathInfo;

		URL resourceURL = _servletContextHelper.getResource(resourcePath);

		if (resourceURL == null) {
			httpServletResponse.sendError(
				404, httpServletRequest.getRequestURI());
		}
		else {
			_writeResource(
				httpServletRequest, httpServletResponse, resourcePath,
				resourceURL);
		}
	}

	private void _sendError(
			HttpServletResponse httpServletResponse, int statusCode)
		throws IOException {

		try {
			httpServletResponse.reset();

			httpServletResponse.sendError(statusCode);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}
		}
	}

	private int _write(InputStream inputStream, OutputStream outputStream)
		throws IOException {

		int writtenContentLength = 0;

		byte[] buffer = new byte[8192];

		int bytesRead = inputStream.read(buffer);

		for (writtenContentLength = 0; bytesRead != -1;
			 bytesRead = inputStream.read(buffer)) {

			outputStream.write(buffer, 0, bytesRead);

			writtenContentLength += bytesRead;
		}

		return writtenContentLength;
	}

	private void _write(InputStream inputStream, Writer writer)
		throws IOException {

		try (Reader reader = new InputStreamReader(inputStream)) {
			char[] buffer = new char[8192];

			for (int charsRead = reader.read(buffer); charsRead != -1;
				 charsRead = reader.read(buffer)) {

				writer.write(buffer, 0, charsRead);
			}
		}
	}

	private void _writeResource(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String resourcePath,
			URL resourceURL)
		throws IOException {

		URLConnection urlConnection = resourceURL.openConnection();

		int contentLength = urlConnection.getContentLength();

		if (contentLength == 0) {
			return;
		}

		long lastModified = urlConnection.getLastModified();

		String etag = null;

		if ((lastModified != -1L) && (contentLength != -1)) {
			etag = StringBundler.concat(
				"W/\"", contentLength, StringPool.DASH, lastModified,
				StringPool.QUOTE);
		}

		String ifNoneMatch = httpServletRequest.getHeader(_IF_NONE_MATCH);

		if ((ifNoneMatch != null) && (etag != null) &&
			(ifNoneMatch.indexOf(etag) != -1)) {

			httpServletResponse.setStatus(304);

			return;
		}

		long ifModifiedSince = httpServletRequest.getDateHeader(
			_IF_MODIFIED_SINCE);

		if ((ifModifiedSince > -1L) && (lastModified > 0L) &&
			(lastModified <= (ifModifiedSince + 999L))) {

			httpServletResponse.setStatus(304);

			return;
		}

		if (contentLength != -1) {
			httpServletResponse.setContentLength(contentLength);
		}

		String contentType = _servletContextHelper.getMimeType(resourcePath);

		if (contentType == null) {
			ServletConfig servletConfig = getServletConfig();

			ServletContext servletContext = servletConfig.getServletContext();

			contentType = servletContext.getMimeType(resourcePath);
		}

		if (contentType != null) {
			httpServletResponse.setContentType(contentType);
		}

		if (lastModified > 0L) {
			httpServletResponse.setDateHeader(_LAST_MODIFIED, lastModified);
		}

		if (etag != null) {
			httpServletResponse.setHeader(_ETAG, etag);
		}

		try (InputStream inputStream = urlConnection.getInputStream()) {
			try {
				int writtenContentLength = _write(
					inputStream, httpServletResponse.getOutputStream());

				if ((contentLength == -1) ||
					(contentLength != writtenContentLength)) {

					httpServletResponse.setContentLength(writtenContentLength);
				}
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalStateException);
				}

				_write(inputStream, httpServletResponse.getWriter());
			}
		}
		catch (FileNotFoundException fileNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(fileNotFoundException);
			}

			_sendError(httpServletResponse, 403);
		}
		catch (SecurityException securityException) {
			if (_log.isDebugEnabled()) {
				_log.debug(securityException);
			}

			_sendError(httpServletResponse, 403);
		}
	}

	private static final String _ETAG = "ETag";

	private static final String _IF_MODIFIED_SINCE = "If-Modified-Since";

	private static final String _IF_NONE_MATCH = "If-None-Match";

	private static final String _LAST_MODIFIED = "Last-Modified";

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceServlet.class);

	private final String _internalName;
	private final ServletContextHelper _servletContextHelper;

}