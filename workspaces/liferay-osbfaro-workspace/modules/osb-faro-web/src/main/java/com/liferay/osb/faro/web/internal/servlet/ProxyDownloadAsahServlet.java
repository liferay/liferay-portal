/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;

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
			HttpURLConnection httpURLConnection = openHttpURLConnection(
				httpServletRequest, "/o/proxy/download");

			httpURLConnection.connect();

			httpServletResponse.setContentLength(
				httpURLConnection.getContentLength());
			httpServletResponse.setContentType(
				httpURLConnection.getContentType());
			httpServletResponse.setHeader(
				HttpHeaders.CONTENT_DISPOSITION,
				httpURLConnection.getHeaderField(
					HttpHeaders.CONTENT_DISPOSITION));

			ServletResponseUtil.write(
				httpServletResponse, httpURLConnection.getInputStream());
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
			HttpURLConnection httpURLConnection = openHttpURLConnection(
				httpServletRequest, "/o/proxy/download");

			transferRequestBody(httpServletRequest, httpURLConnection);

			ServletResponseUtil.write(
				httpServletResponse, httpURLConnection.getInputStream());
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