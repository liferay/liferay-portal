/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.webserver.WebServerServlet;

import jakarta.servlet.Servlet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alexander Chow
 */
public abstract class BaseWebServerTestCase extends BaseDLAppTestCase {

	public MockHttpServletResponse service(
			String method, String path, Map<String, String> headers,
			Map<String, String> params, User user, byte[] data)
		throws Exception {

		if (headers == null) {
			headers = new HashMap<>();
		}

		if (params == null) {
			params = new HashMap<>();
		}

		if (user == null) {
			user = TestPropsValues.getUser();
		}

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(
				method,
				StringBundler.concat(
					_CONTEXT_PATH, _SERVLET_PATH, _PATH_INFO_PREFACE, path));

		mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		mockHttpServletRequest.setContextPath(_CONTEXT_PATH);
		mockHttpServletRequest.setParameters(params);
		mockHttpServletRequest.setPathInfo(_PATH_INFO_PREFACE + path);
		mockHttpServletRequest.setServletPath(_SERVLET_PATH);

		if (data != null) {
			mockHttpServletRequest.setContent(data);

			String contentType = headers.remove(HttpHeaders.CONTENT_TYPE);

			if (contentType != null) {
				mockHttpServletRequest.setContentType(contentType);
			}
			else {
				mockHttpServletRequest.setContentType(ContentTypes.TEXT_PLAIN);
			}
		}

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			mockHttpServletRequest.addHeader(entry.getKey(), entry.getValue());
		}

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletResponse.setCharacterEncoding(StringPool.UTF8);

		Servlet httpServlet = getServlet();

		httpServlet.service(mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	protected Servlet getServlet() {
		return new WebServerServlet();
	}

	private static final String _CONTEXT_PATH = "/documents";

	private static final String _PATH_INFO_PREFACE = "";

	private static final String _SERVLET_PATH = "";

}