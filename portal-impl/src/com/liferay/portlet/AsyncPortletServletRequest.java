/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.spring.context.PortalContextLoaderListener;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dante Wang
 */
public class AsyncPortletServletRequest extends HttpServletRequestWrapper {

	public static AsyncPortletServletRequest getAsyncPortletServletRequest(
		HttpServletRequest httpServletRequest) {

		while (httpServletRequest instanceof HttpServletRequestWrapper) {
			if (httpServletRequest instanceof AsyncPortletServletRequest) {
				return (AsyncPortletServletRequest)httpServletRequest;
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)httpServletRequest;

			httpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		return null;
	}

	public AsyncPortletServletRequest(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);

		_contextPath = httpServletRequest.getContextPath();
		_pathInfo = httpServletRequest.getPathInfo();
		_queryString = httpServletRequest.getQueryString();
		_requestURI = httpServletRequest.getRequestURI();
		_servletPath = httpServletRequest.getServletPath();
	}

	@Override
	public String getContextPath() {
		return _contextPath;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return DispatcherType.ASYNC;
	}

	@Override
	public String getPathInfo() {
		return _pathInfo;
	}

	@Override
	public String getQueryString() {
		return _queryString;
	}

	@Override
	public String getRequestURI() {
		return _requestURI;
	}

	@Override
	public String getServletPath() {
		return _servletPath;
	}

	public void setContextPath(String contextPath) {
		_contextPath = contextPath;
	}

	public void setPathInfo(String pathInfo) {
		_pathInfo = pathInfo;
	}

	public void setQueryString(String queryString) {
		_queryString = queryString;

		setRequest(
			DynamicServletRequest.addQueryString(
				(HttpServletRequest)getRequest(), queryString, true));
	}

	public void setRequestURI(String requestUri) {
		_requestURI = requestUri;
	}

	public void setServletPath(String servletPath) {
		_servletPath = servletPath;
	}

	public void update(String contextPath, String path) {
		String pathInfo = null;
		String queryString = null;
		String requestURI = null;
		String servletPath = null;

		if (path != null) {
			if (!contextPath.isEmpty() && path.startsWith(contextPath)) {
				path = path.substring(contextPath.length());
			}

			String pathNoQueryString = path;

			int pos = path.indexOf(CharPool.QUESTION);

			if (pos != -1) {
				pathNoQueryString = path.substring(0, pos);
				queryString = path.substring(pos + 1);
			}

			for (String urlPattern : _portalServletURLPatterns) {
				if (urlPattern.endsWith("/*")) {
					int length = urlPattern.length() - 2;

					if ((pathNoQueryString.length() > length) &&
						pathNoQueryString.regionMatches(
							0, urlPattern, 0, length) &&
						(pathNoQueryString.charAt(length) == CharPool.SLASH)) {

						pathInfo = pathNoQueryString.substring(length);
						servletPath = urlPattern.substring(0, length);

						break;
					}
				}
			}

			if (servletPath == null) {
				servletPath = pathNoQueryString;
			}

			if (contextPath.equals(StringPool.SLASH)) {
				requestURI = pathNoQueryString;
			}
			else {
				requestURI = contextPath + pathNoQueryString;
			}
		}

		setContextPath(contextPath);
		setPathInfo(pathInfo);
		setQueryString(queryString);
		setRequestURI(requestURI);
		setServletPath(servletPath);
	}

	private static final Set<String> _portalServletURLPatterns =
		new HashSet<String>() {
			{
				ServletContext servletContext = ServletContextPool.get(
					PortalContextLoaderListener.getPortalServletContextName());

				if (servletContext == null) {
					throw new ExceptionInInitializerError(
						"Portal servlet context is not initialized");
				}

				addAll(
					(Set<String>)servletContext.getAttribute(
						WebKeys.PORTAL_SERVLET_URL_PATTERNS));
			}
		};

	private String _contextPath;
	private String _pathInfo;
	private String _queryString;
	private String _requestURI;
	private String _servletPath;

}