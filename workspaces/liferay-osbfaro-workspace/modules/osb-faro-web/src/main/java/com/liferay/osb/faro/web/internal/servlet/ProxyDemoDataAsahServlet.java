/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nilton Vieira
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/proxy/demo-data-uploads",
		"osgi.http.whiteboard.servlet.name=com.liferay.osb.faro.web.internal.servlet.ProxyDemoDataAsahServlet",
		"osgi.http.whiteboard.servlet.pattern=/proxy/demo-data-uploads/*"
	},
	service = Servlet.class
)
public class ProxyDemoDataAsahServlet extends BaseAsahServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_proxy(httpServletRequest, httpServletResponse, false);
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_proxy(httpServletRequest, httpServletResponse, true);
	}

	private InputStream _getInputStream(
		HttpURLConnection httpURLConnection, int responseCode) {

		if (responseCode >= HttpServletResponse.SC_BAD_REQUEST) {
			return httpURLConnection.getErrorStream();
		}

		try {
			return httpURLConnection.getInputStream();
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return null;
		}
	}

	private boolean _isCompanyAdmin() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			_log.error("Unable to check permissions");

			return false;
		}

		return permissionChecker.isCompanyAdmin();
	}

	private void _proxy(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean transferBody)
		throws IOException {

		if (!_isCompanyAdmin()) {
			httpServletResponse.sendError(
				HttpServletResponse.SC_FORBIDDEN,
				"You do not have the required permissions");

			return;
		}

		try {
			HttpURLConnection httpURLConnection = openHttpURLConnection(
				httpServletRequest, "/o/proxy");

			if (transferBody) {
				transferRequestBody(httpServletRequest, httpURLConnection);
			}

			httpServletResponse.setContentType(
				httpURLConnection.getContentType());

			int responseCode = httpURLConnection.getResponseCode();

			httpServletResponse.setStatus(responseCode);

			try (InputStream inputStream = _getInputStream(
					httpURLConnection, responseCode)) {

				if (inputStream != null) {
					ServletResponseUtil.write(httpServletResponse, inputStream);
				}
			}
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}

			httpServletResponse.sendError(
				HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProxyDemoDataAsahServlet.class);

}