/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;

/**
 * @author Raymond Augé
 */
public class DirectRequestDispatcherFactoryUtil {

	public static RequestDispatcher getRequestDispatcher(
		ServletContext servletContext, String path) {

		return new IndirectRequestDispatcher(
			_getRequestDispatcher(servletContext, path));
	}

	public static RequestDispatcher getRequestDispatcher(
		ServletRequest servletRequest, String path) {

		ServletContext servletContext =
			(ServletContext)servletRequest.getAttribute(WebKeys.CTX);

		if (servletContext == null) {
			return servletRequest.getRequestDispatcher(path);
		}

		return getRequestDispatcher(servletContext, path);
	}

	private static RequestDispatcher _getRequestDispatcher(
		ServletContext servletContext, String path) {

		if (!GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.DIRECT_SERVLET_CONTEXT_ENABLED))) {

			return servletContext.getRequestDispatcher(path);
		}

		if ((path == null) || (path.length() == 0)) {
			return null;
		}

		if (path.charAt(0) != CharPool.SLASH) {
			throw new IllegalArgumentException(
				"Path " + path + " is not relative to context root");
		}

		String contextPath = servletContext.getContextPath();

		String fullPath = contextPath.concat(path);

		String queryString = null;

		int pos = fullPath.indexOf(CharPool.QUESTION);

		if (pos != -1) {
			queryString = fullPath.substring(pos + 1);

			fullPath = fullPath.substring(0, pos);
		}

		Servlet servlet = DirectServletRegistryUtil.getServlet(fullPath);

		if (servlet == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No servlet found for " + fullPath);
			}

			return new DirectServletPathRegisterDispatcher(
				path, servletContext.getRequestDispatcher(path));
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Servlet found for " + fullPath);
		}

		return new DirectRequestDispatcher(servlet, path, queryString);
	}

	private static final String _EQUINOX_REQUEST_CLASS_NAME =
		"org.eclipse.equinox.http.servlet.internal.servlet." +
			"HttpServletRequestWrapperImpl";

	private static final Log _log = LogFactoryUtil.getLog(
		DirectRequestDispatcherFactoryUtil.class);

	private static class DirectRequestDispatcherServletRequest
		extends HttpServletRequestWrapper {

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		private DirectRequestDispatcherServletRequest(
			ServletRequest servletRequest, ServletContext servletContext) {

			super((HttpServletRequest)servletRequest);

			_servletContext = servletContext;
		}

		private final ServletContext _servletContext;

	}

	/**
	 * See LPS-79937. We need to protect against redispatch from the module
	 * framework back to the portal, which means we have to unwrap the request.
	 */
	private static class IndirectRequestDispatcher
		implements RequestDispatcher {

		@Override
		public void forward(
				ServletRequest servletRequest, ServletResponse servletResponse)
			throws IOException, ServletException {

			Class<?> clazz = servletRequest.getClass();

			if (_EQUINOX_REQUEST_CLASS_NAME.equals(clazz.getName())) {
				HttpServletRequestWrapper wrapper =
					(HttpServletRequestWrapper)servletRequest;

				servletRequest = new DirectRequestDispatcherServletRequest(
					wrapper.getRequest(), wrapper.getServletContext());
			}

			_requestDispatcher.forward(servletRequest, servletResponse);
		}

		@Override
		public void include(
				ServletRequest servletRequest, ServletResponse servletResponse)
			throws IOException, ServletException {

			Class<?> clazz = servletRequest.getClass();

			if (_EQUINOX_REQUEST_CLASS_NAME.equals(clazz.getName())) {
				HttpServletRequestWrapper wrapper =
					(HttpServletRequestWrapper)servletRequest;

				servletRequest = new DirectRequestDispatcherServletRequest(
					wrapper.getRequest(), wrapper.getServletContext());
			}

			_requestDispatcher.include(servletRequest, servletResponse);
		}

		private IndirectRequestDispatcher(RequestDispatcher requestDispatcher) {
			_requestDispatcher = requestDispatcher;
		}

		private final RequestDispatcher _requestDispatcher;

	}

}