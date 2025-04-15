/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.portlet.LiferayPortletContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.servlet.URLEncoder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.servlet.DynamicServletRequestUtil;
import com.liferay.portlet.LiferayPortletUtil;
import com.liferay.portlet.PortletServletRequest;
import com.liferay.portlet.PortletServletResponse;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Raymond Augé
 */
public class PortletRequestDispatcherImpl
	implements PortletRequestDispatcher, RequestDispatcher {

	public PortletRequestDispatcherImpl(
		RequestDispatcher requestDispatcher, boolean named,
		PortletContext portletContext) {

		this(requestDispatcher, named, portletContext, null);
	}

	public PortletRequestDispatcherImpl(
		RequestDispatcher requestDispatcher, boolean named,
		PortletContext portletContext, String path) {

		_requestDispatcher = requestDispatcher;
		_named = named;
		_path = path;

		_liferayPortletContext = (LiferayPortletContext)portletContext;

		_portlet = _liferayPortletContext.getPortlet();
	}

	public PortletRequestDispatcherImpl(
		RequestDispatcher requestDispatcher, String path) {

		_requestDispatcher = requestDispatcher;
		_path = path;

		_named = false;
		_liferayPortletContext = null;
		_portlet = null;
	}

	@Override
	public void forward(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IllegalStateException, IOException, PortletException {

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(portletResponse);

		if (httpServletResponse.isCommitted()) {
			throw new IllegalStateException("Response is already committed");
		}

		dispatch(portletRequest, portletResponse, false);
	}

	@Override
	public void forward(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		dispatch(servletRequest, servletResponse, false);
	}

	@Override
	public void include(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortletException {

		dispatch(portletRequest, portletResponse, true);
	}

	@Override
	public void include(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		dispatch(renderRequest, renderResponse, true);
	}

	@Override
	public void include(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		dispatch(servletRequest, servletResponse, true);
	}

	protected void checkCalledFlushBuffer(
		boolean include, PortletResponse portletResponse) {

		if (!include && (portletResponse instanceof MimeResponseImpl)) {
			MimeResponseImpl mimeResponseImpl =
				(MimeResponseImpl)portletResponse;

			if (mimeResponseImpl.isCalledFlushBuffer()) {
				throw new IllegalStateException();
			}
		}
	}

	protected HttpServletRequest createDynamicServletRequest(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		Map<String, String[]> parameterMap) {

		return DynamicServletRequestUtil.createDynamicServletRequest(
			httpServletRequest, liferayPortletRequest.getPortlet(),
			parameterMap, true);
	}

	protected void dispatch(
			PortletRequest portletRequest, PortletResponse portletResponse,
			boolean include)
		throws IOException, PortletException {

		checkCalledFlushBuffer(include, portletResponse);

		LiferayPortletRequest liferayPortletRequest =
			LiferayPortletUtil.getLiferayPortletRequest(portletRequest);
		LiferayPortletResponse liferayPortletResponse =
			LiferayPortletUtil.getLiferayPortletResponse(portletResponse);

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(portletRequest);

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST, portletRequest);
		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE, portletResponse);

		String pathInfo = null;
		String queryString = null;
		String requestURI = null;
		String servletPath = null;

		if (_path != null) {
			String pathNoQueryString = _path;

			int pos = _path.indexOf(CharPool.QUESTION);

			if (pos != -1) {
				pathNoQueryString = _path.substring(0, pos);

				queryString = _path.substring(pos + 1);

				httpServletRequest = createDynamicServletRequest(
					httpServletRequest, liferayPortletRequest,
					toParameterMap(queryString));
			}

			Portlet portlet = liferayPortletRequest.getPortlet();

			PortletApp portletApp = portlet.getPortletApp();

			Set<String> servletURLPatterns = portletApp.getServletURLPatterns();

			for (String urlPattern : servletURLPatterns) {
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
				int extensionIndex = pathNoQueryString.lastIndexOf(
					CharPool.PERIOD);

				if (extensionIndex >= 0) {
					for (String urlPattern : servletURLPatterns) {
						if (urlPattern.startsWith("*.") &&
							pathNoQueryString.regionMatches(
								extensionIndex, urlPattern, 1,
								urlPattern.length() - 1)) {

							servletPath = pathNoQueryString;

							break;
						}
					}

					if ((servletPath == null) &&
						(pathNoQueryString.endsWith(".jsp") ||
						 pathNoQueryString.endsWith(".jspx"))) {

						servletPath = pathNoQueryString;
					}
				}
			}

			if (servletPath == null) {
				if (!include &&
					!servletURLPatterns.contains(pathNoQueryString)) {

					pathInfo = pathNoQueryString;
				}

				servletPath = pathNoQueryString;
			}

			String contextPath = portletRequest.getContextPath();

			if (contextPath.equals(StringPool.SLASH)) {
				requestURI = pathNoQueryString;
			}
			else {
				requestURI = contextPath + pathNoQueryString;
			}
		}

		PortletServletRequest portletServletRequest = new PortletServletRequest(
			httpServletRequest, portletRequest, pathInfo, queryString,
			requestURI, servletPath, _named, include);

		PortletServletResponse portletServletResponse =
			new PortletServletResponse(
				PortalUtil.getHttpServletResponse(portletResponse),
				portletResponse, include);

		URLEncoder urlEncoder = _portlet.getURLEncoderInstance();

		if (urlEncoder != null) {
			liferayPortletResponse.setURLEncoder(urlEncoder);
		}

		try {
			if (include) {
				_requestDispatcher.include(
					portletServletRequest, portletServletResponse);
			}
			else {
				_requestDispatcher.forward(
					portletServletRequest, portletServletResponse);
			}
		}
		catch (ServletException servletException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to dispatch request", servletException);
			}

			_log.error(
				"Unable to dispatch request: " + servletException.getMessage());

			throw new PortletException(servletException);
		}
		finally {
			liferayPortletRequest.setPortletRequestDispatcherRequest(null);
		}
	}

	protected void dispatch(
			ServletRequest servletRequest, ServletResponse servletResponse,
			boolean include)
		throws IOException, ServletException {

		HttpServletRequest oldPortletRequestDispatcherHttpServletRequest = null;

		LiferayPortletRequest liferayPortletRequest = null;

		if (servletRequest instanceof PortletServletRequest) {
			PortletRequest portletRequest =
				(PortletRequest)servletRequest.getAttribute(
					"jakarta.portlet.request");

			liferayPortletRequest = LiferayPortletUtil.getLiferayPortletRequest(
				portletRequest);

			oldPortletRequestDispatcherHttpServletRequest =
				liferayPortletRequest.getPortletRequestDispatcherRequest();

			PortletServletRequest portletServletRequest =
				(PortletServletRequest)servletRequest;

			HttpServletRequest httpServletRequest =
				(HttpServletRequest)portletServletRequest.getRequest();

			if (_path != null) {
				int pos = _path.indexOf(CharPool.QUESTION);

				if (pos != -1) {
					String queryString = _path.substring(pos + 1);

					httpServletRequest = createDynamicServletRequest(
						httpServletRequest, liferayPortletRequest,
						toParameterMap(queryString));
				}
			}

			servletRequest = new PortletServletRequest(
				httpServletRequest, portletRequest,
				portletServletRequest.getPathInfo(),
				portletServletRequest.getQueryString(),
				portletServletRequest.getRequestURI(),
				portletServletRequest.getServletPath(), _named, include);
		}

		try {
			if (include) {
				_requestDispatcher.include(servletRequest, servletResponse);
			}
			else {
				_requestDispatcher.forward(servletRequest, servletResponse);
			}
		}
		catch (ServletException servletException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to dispatch request", servletException);
			}

			_log.error(
				"Unable to dispatch request: " + servletException.getMessage());

			throw new ServletException(servletException);
		}
		finally {
			if (liferayPortletRequest != null) {
				liferayPortletRequest.setPortletRequestDispatcherRequest(
					oldPortletRequestDispatcherHttpServletRequest);
			}
		}
	}

	protected Map<String, String[]> toParameterMap(String queryString) {
		Map<String, String[]> parameterMap = new HashMap<>();

		for (String parameter :
				StringUtil.split(queryString, CharPool.AMPERSAND)) {

			String[] parameterArray = StringUtil.split(
				parameter, CharPool.EQUAL);

			String name = parameterArray[0];

			String value = StringPool.BLANK;

			if (parameterArray.length == 2) {
				value = parameterArray[1];
			}

			String[] values = parameterMap.get(name);

			if (values == null) {
				parameterMap.put(name, new String[] {value});
			}
			else {
				String[] newValues = new String[values.length + 1];

				System.arraycopy(values, 0, newValues, 0, values.length);

				newValues[newValues.length - 1] = value;

				parameterMap.put(name, newValues);
			}
		}

		return parameterMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRequestDispatcherImpl.class);

	private final LiferayPortletContext _liferayPortletContext;
	private final boolean _named;
	private final String _path;
	private final Portlet _portlet;
	private final RequestDispatcher _requestDispatcher;

}