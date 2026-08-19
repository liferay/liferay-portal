/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet;

import com.liferay.osb.faro.engine.client.constants.OSBAsahHeaderConstants;
import com.liferay.osb.faro.engine.client.util.EngineServiceURLUtil;
import com.liferay.osb.faro.engine.client.util.TokenUtil;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.util.FaroProjectThreadLocal;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.utils.URIBuilder;

/**
 * @author Matthew Kong
 */
public abstract class BaseAsahServlet extends HttpServlet {

	protected URI buildURI(HttpServletRequest httpServletRequest, String path)
		throws URISyntaxException {

		URIBuilder uriBuilder = new URIBuilder(
			EngineServiceURLUtil.getBackendURL(
				FaroProjectThreadLocal.getFaroProject(), path));

		Map<String, String[]> requestParameterMap =
			httpServletRequest.getParameterMap();

		requestParameterMap.forEach(
			(key, valueArray) -> {
				for (String value : valueArray) {
					uriBuilder.addParameter(key, value);
				}
			});

		return uriBuilder.build();
	}

	protected String getProjectId() {
		FaroProject faroProject = FaroProjectThreadLocal.getFaroProject();

		return faroProject.getProjectId();
	}

	protected String getSecuritySignature(URI uri) {
		String url = uri.toString();

		return DigestUtils.sha256Hex(
			TokenUtil.getOSBAsahSecurityToken() +
				url.substring(0, url.lastIndexOf(uri.getPath())));
	}

	protected HttpURLConnection openHttpURLConnection(
			HttpServletRequest httpServletRequest, String contextPath)
		throws IOException, URISyntaxException {

		URI uri = buildURI(
			httpServletRequest,
			StringUtil.removeSubstring(
				httpServletRequest.getRequestURI(), contextPath));

		URL url = uri.toURL();

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestMethod(httpServletRequest.getMethod());
		httpURLConnection.setRequestProperty(
			OSBAsahHeaderConstants.FARO_BACKEND_SECURITY_SIGNATURE,
			getSecuritySignature(uri));
		httpURLConnection.setRequestProperty(
			OSBAsahHeaderConstants.PROJECT_ID, getProjectId());

		return httpURLConnection;
	}

	protected void transferRequestBody(
			HttpServletRequest httpServletRequest,
			HttpURLConnection httpURLConnection)
		throws IOException {

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestProperty(
			HttpHeaders.CONTENT_TYPE,
			httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE));

		try (OutputStream outputStream = httpURLConnection.getOutputStream();
			InputStream inputStream = httpServletRequest.getInputStream()) {

			StreamUtil.transfer(inputStream, outputStream);
		}
	}

	private static final long serialVersionUID = 1L;

}