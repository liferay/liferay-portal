/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.css.rtl.servlet.internal;

import com.liferay.frontend.css.rtl.servlet.internal.converter.CSSRTLConverter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.RequestDispatcherUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.URLUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import org.osgi.framework.Bundle;
import org.osgi.service.http.context.ServletContextHelper;

/**
 * @author Carlos Sierra Andrés
 */
public class RTLServlet extends HttpServlet {

	public RTLServlet(
		Bundle bundle, ServletContextHelper servletContextHelper) {

		_bundle = bundle;
		_servletContextHelper = servletContextHelper;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		URL url = _getResourceURL(httpServletRequest);

		if (url == null) {
			httpServletResponse.sendError(
				HttpServletResponse.SC_NOT_FOUND, "Not Found");
		}
		else {
			_transfer(url, httpServletResponse);
		}
	}

	@Override
	protected long getLastModified(HttpServletRequest httpServletRequest) {
		try {
			URL url = _getResourceURL(httpServletRequest);

			if (url != null) {
				URLConnection urlConnection = url.openConnection();

				return urlConnection.getLastModified();
			}

			return super.getLastModified(httpServletRequest);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return super.getLastModified(httpServletRequest);
		}
	}

	private URL _getResourceURL(HttpServletRequest httpServletRequest)
		throws IOException {

		String path = URLDecoder.decode(
			RequestDispatcherUtil.getEffectivePath(httpServletRequest),
			StringPool.UTF8);

		URL url = _servletContextHelper.getResource(path);

		if (url == null) {
			return null;
		}

		String languageId = httpServletRequest.getParameter("languageId");

		if ((languageId == null) ||
			!PortalUtil.isRightToLeft(httpServletRequest)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skip because specified language " + languageId +
						" is not right to left");
			}

			return url;
		}

		String rtlPath = FileUtil.appendSuffix(path, "_rtl");

		URL rtlURL = _servletContextHelper.getResource(rtlPath);

		if (rtlURL != null) {
			return rtlURL;
		}

		File dataFile = _bundle.getDataFile(rtlPath);

		if (dataFile.exists()) {
			URLConnection urlConnection = url.openConnection();

			if (dataFile.lastModified() > urlConnection.getLastModified()) {
				URI uri = dataFile.toURI();

				return uri.toURL();
			}
		}

		CSSRTLConverter cssRTLConverter = new CSSRTLConverter(false);

		String rtl = cssRTLConverter.process(URLUtil.toString(url));

		InputStream inputStream = new ByteArrayInputStream(
			rtl.getBytes(StringPool.UTF8));

		try {
			File parentFile = dataFile.getParentFile();

			parentFile.mkdirs();

			dataFile.createNewFile();

			try (OutputStream outputStream = new FileOutputStream(dataFile)) {
				StreamUtil.transfer(inputStream, outputStream, false);
			}
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to cache RTL CSS", ioException);
			}
		}

		inputStream.reset();

		URI uri = dataFile.toURI();

		return uri.toURL();
	}

	private void _transfer(URL url, HttpServletResponse httpServletResponse)
		throws IOException {

		URLConnection urlConnection = url.openConnection();

		httpServletResponse.setContentLength(urlConnection.getContentLength());

		httpServletResponse.setContentType(ContentTypes.TEXT_CSS_UTF8);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		StreamUtil.transfer(
			url.openStream(), httpServletResponse.getOutputStream());
	}

	private static final Log _log = LogFactoryUtil.getLog(RTLServlet.class);

	private final Bundle _bundle;
	private final ServletContextHelper _servletContextHelper;

}