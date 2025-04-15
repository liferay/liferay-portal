/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

/**
 * @author Shuyang Zhou
 */
public class DirectServletPathRegisterDispatcher implements RequestDispatcher {

	public DirectServletPathRegisterDispatcher(
		String path, RequestDispatcher requestDispatcher) {

		_path = path;
		_requestDispatcher = requestDispatcher;
	}

	@Override
	public void forward(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		servletRequest.setAttribute(WebKeys.SERVLET_PATH, _path);

		_requestDispatcher.forward(servletRequest, servletResponse);
	}

	@Override
	public void include(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		String includePathInfo = (String)servletRequest.getAttribute(
			RequestDispatcher.INCLUDE_PATH_INFO);

		servletRequest.setAttribute(RequestDispatcher.INCLUDE_PATH_INFO, null);

		String includeServletPath = (String)servletRequest.getAttribute(
			RequestDispatcher.INCLUDE_SERVLET_PATH);

		servletRequest.setAttribute(
			RequestDispatcher.INCLUDE_SERVLET_PATH, _path);
		servletRequest.setAttribute(WebKeys.SERVLET_PATH, _path);

		try {
			_requestDispatcher.include(servletRequest, servletResponse);
		}
		finally {
			servletRequest.setAttribute(
				RequestDispatcher.INCLUDE_PATH_INFO, includePathInfo);
			servletRequest.setAttribute(
				RequestDispatcher.INCLUDE_SERVLET_PATH, includeServletPath);
		}
	}

	private final String _path;
	private final RequestDispatcher _requestDispatcher;

}