/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet;

import com.liferay.osb.faro.engine.client.constants.OSBAsahHeaderConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/proxy/download",
		"osgi.http.whiteboard.servlet.name=com.liferay.osb.faro.web.internal.servlet.ProxyDownloadServlet",
		"osgi.http.whiteboard.servlet.pattern=/proxy/download/data-control-tasks/*",
		"osgi.http.whiteboard.servlet.pattern=/proxy/download/suppressions/logs"
	},
	service = Servlet.class
)
public class ProxyDownloadAsahServlet extends BaseAsahServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			URI uri = buildURI(
				httpServletRequest,
				StringUtil.removeSubstring(
					httpServletRequest.getRequestURI(), "/o/proxy/download"));

			URL url = uri.toURL();

			URLConnection urlConnection = url.openConnection();

			urlConnection.setRequestProperty(
				OSBAsahHeaderConstants.FARO_BACKEND_SECURITY_SIGNATURE,
				getSecuritySignature(uri));
			urlConnection.setRequestProperty(
				OSBAsahHeaderConstants.PROJECT_ID, getProjectId());

			urlConnection.connect();

			httpServletResponse.setContentLength(
				urlConnection.getContentLength());
			httpServletResponse.setContentType(urlConnection.getContentType());
			httpServletResponse.setHeader(
				HttpHeaders.CONTENT_DISPOSITION,
				urlConnection.getHeaderField(HttpHeaders.CONTENT_DISPOSITION));

			ServletResponseUtil.write(
				httpServletResponse, urlConnection.getInputStream());
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			URI uri = buildURI(
				httpServletRequest,
				StringUtil.removeSubstring(
					httpServletRequest.getRequestURI(), "/o/proxy/download"));

			URL url = uri.toURL();

			URLConnection urlConnection = url.openConnection();

			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty(
				HttpHeaders.CONTENT_TYPE,
				httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE));
			urlConnection.setRequestProperty(
				OSBAsahHeaderConstants.FARO_BACKEND_SECURITY_SIGNATURE,
				getSecuritySignature(uri));
			urlConnection.setRequestProperty(
				OSBAsahHeaderConstants.PROJECT_ID, getProjectId());

			try (OutputStream outputStream = urlConnection.getOutputStream();
				InputStream inputStream = httpServletRequest.getInputStream()) {

				StreamUtil.transfer(inputStream, outputStream);
			}

			ServletResponseUtil.write(
				httpServletResponse, urlConnection.getInputStream());
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProxyDownloadAsahServlet.class);

}