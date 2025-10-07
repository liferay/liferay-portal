/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayDispatchTargets;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EventListeners;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dante Wang
 */
public class LiferayHttpServletRequestWrapper
	extends HttpServletRequestWrapper {

	public static LiferayHttpServletRequestWrapper find(
		HttpServletRequest httpServletRequest) {

		while (httpServletRequest instanceof
					HttpServletRequestWrapper httpServletRequestWrapper) {

			if (httpServletRequestWrapper instanceof
					LiferayHttpServletRequestWrapper
						liferayHttpServletRequestWrapper) {

				return liferayHttpServletRequestWrapper;
			}

			httpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		return null;
	}

	public static String getDispatchPathInfo(
		HttpServletRequest httpServletRequest) {

		if (httpServletRequest.getDispatcherType() == DispatcherType.INCLUDE) {
			return (String)httpServletRequest.getAttribute(
				RequestDispatcher.INCLUDE_PATH_INFO);
		}

		return httpServletRequest.getPathInfo();
	}

	public LiferayHttpServletRequestWrapper(
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);

		_httpServletRequest = httpServletRequest;
	}

	@Override
	public Object getAttribute(String attributeName) {
		LiferayDispatchTargets currentLiferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		DispatcherType dispatcherType =
			currentLiferayDispatchTargets.getDispatcherType();

		if ((dispatcherType == DispatcherType.ASYNC) ||
			(dispatcherType == DispatcherType.REQUEST) ||
			!attributeName.startsWith("jakarta.servlet.")) {

			return _httpServletRequest.getAttribute(attributeName);
		}

		Map<String, Object> specialOverrides =
			currentLiferayDispatchTargets.getSpecialOverides();

		boolean hasServletName = false;

		if (currentLiferayDispatchTargets.getServletName() != null) {
			hasServletName = true;
		}

		boolean dispatcherAttribute = _dispatcherAttributes.contains(
			attributeName);

		if (dispatcherType == DispatcherType.ERROR) {
			if (dispatcherAttribute &&
				!attributeName.startsWith("javax.servlet.error.")) {

				return null;
			}
		}
		else if (dispatcherType == DispatcherType.INCLUDE) {
			if (hasServletName &&
				attributeName.startsWith("javax.servlet.include")) {

				return null;
			}

			if (dispatcherAttribute) {
				Object specialOveride = specialOverrides.get(attributeName);

				if (specialOveride == _NULL_PLACEHOLDER) {
					return null;
				}

				Object attributeValue = super.getAttribute(attributeName);

				if (attributeValue != null) {
					return attributeValue;
				}
			}

			if (attributeName.equals(RequestDispatcher.INCLUDE_CONTEXT_PATH)) {
				LiferayContextController liferayContextController =
					currentLiferayDispatchTargets.getContextController();

				return liferayContextController.getContextPath();
			}

			if (attributeName.equals(RequestDispatcher.INCLUDE_PATH_INFO)) {
				return currentLiferayDispatchTargets.getPathInfo();
			}

			if (attributeName.equals(RequestDispatcher.INCLUDE_QUERY_STRING)) {
				return currentLiferayDispatchTargets.getQueryString();
			}

			if (attributeName.equals(RequestDispatcher.INCLUDE_REQUEST_URI)) {
				return currentLiferayDispatchTargets.getRequestURI();
			}

			if (attributeName.equals(RequestDispatcher.INCLUDE_SERVLET_PATH)) {
				return currentLiferayDispatchTargets.getServletPath();
			}

			if (dispatcherAttribute) {
				return null;
			}
		}
		else if (dispatcherType == DispatcherType.FORWARD) {
			if (hasServletName &&
				attributeName.startsWith("javax.servlet.forward")) {

				return null;
			}

			if (dispatcherAttribute) {
				Object specialOveride = specialOverrides.get(attributeName);

				if (specialOveride == _NULL_PLACEHOLDER) {
					return null;
				}
			}

			if (attributeName.equals(RequestDispatcher.FORWARD_CONTEXT_PATH)) {
				LiferayContextController liferayContextController =
					currentLiferayDispatchTargets.getContextController();

				return liferayContextController.getContextPath();
			}

			LiferayDispatchTargets originalLiferayDispatchTargets =
				_liferayDispatchTargetsDeque.getLast();

			if (attributeName.equals(RequestDispatcher.FORWARD_PATH_INFO)) {
				return originalLiferayDispatchTargets.getPathInfo();
			}

			if (attributeName.equals(RequestDispatcher.FORWARD_QUERY_STRING)) {
				return originalLiferayDispatchTargets.getQueryString();
			}

			if (attributeName.equals(RequestDispatcher.FORWARD_REQUEST_URI)) {
				return originalLiferayDispatchTargets.getRequestURI();
			}

			if (attributeName.equals(RequestDispatcher.FORWARD_SERVLET_PATH)) {
				return originalLiferayDispatchTargets.getServletPath();
			}

			if (dispatcherAttribute) {
				return null;
			}
		}

		return _httpServletRequest.getAttribute(attributeName);
	}

	@Override
	public String getAuthType() {
		String authType = (String)getAttribute(
			"org.osgi.service.http.authentication.type");

		if (authType != null) {
			return authType;
		}

		return _httpServletRequest.getAuthType();
	}

	@Override
	public String getContextPath() {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		LiferayContextController liferayContextController =
			liferayDispatchTargets.getContextController();

		return liferayContextController.getFullContextPath();
	}

	@Override
	public DispatcherType getDispatcherType() {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		return liferayDispatchTargets.getDispatcherType();
	}

	@Override
	public String getParameter(String name) {
		String[] values = getParameterValues(name);

		if (ArrayUtil.isEmpty(values)) {
			return null;
		}

		return values[0];
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		return liferayDispatchTargets.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		Map<String, String[]> parameterMap = getParameterMap();

		return Collections.enumeration(parameterMap.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		Map<String, String[]> parameterMap = getParameterMap();

		return parameterMap.get(name);
	}

	@Override
	public String getPathInfo() {
		LiferayDispatchTargets currentLiferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		if ((currentLiferayDispatchTargets.getServletName() == null) &&
			(currentLiferayDispatchTargets.getDispatcherType() !=
				DispatcherType.INCLUDE)) {

			return currentLiferayDispatchTargets.getPathInfo();
		}

		LiferayDispatchTargets originalLiferayDispatchTargets =
			_liferayDispatchTargetsDeque.getLast();

		return originalLiferayDispatchTargets.getPathInfo();
	}

	@Override
	public String getQueryString() {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		if ((liferayDispatchTargets.getServletName() == null) &&
			(liferayDispatchTargets.getDispatcherType() !=
				DispatcherType.INCLUDE)) {

			return liferayDispatchTargets.getQueryString();
		}

		return _httpServletRequest.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		String remoteUser = (String)getAttribute(
			"org.osgi.service.http.authentication.remote.user");

		if (remoteUser != null) {
			return remoteUser;
		}

		return _httpServletRequest.getRemoteUser();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		LiferayContextController liferayContextController =
			liferayDispatchTargets.getContextController();

		if (!path.startsWith(StringPool.SLASH)) {
			path =
				liferayDispatchTargets.getServletPath() + StringPool.SLASH +
					path;
		}
		else {
			String fullContextPath =
				liferayContextController.getFullContextPath();

			if (path.startsWith(fullContextPath)) {
				path = path.substring(fullContextPath.length());
			}
		}

		LiferayDispatchTargets requestedLiferayDispatchTargets =
			liferayContextController.getDispatchTargets(path);

		if (requestedLiferayDispatchTargets == null) {
			return null;
		}

		return new RequestDispatcherImpl(requestedLiferayDispatchTargets, path);
	}

	@Override
	public String getRequestURI() {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		if ((liferayDispatchTargets.getServletName() == null) &&
			(liferayDispatchTargets.getDispatcherType() !=
				DispatcherType.INCLUDE)) {

			return liferayDispatchTargets.getRequestURI();
		}

		return _httpServletRequest.getRequestURI();
	}

	@Override
	public ServletContext getServletContext() {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		return _getServletContext(liferayDispatchTargets);
	}

	@Override
	public String getServletPath() {
		LiferayDispatchTargets currentLiferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		if ((currentLiferayDispatchTargets.getServletName() == null) &&
			(currentLiferayDispatchTargets.getDispatcherType() !=
				DispatcherType.INCLUDE)) {

			String servletPath = currentLiferayDispatchTargets.getServletPath();

			if (servletPath.equals(StringPool.SLASH)) {
				return StringPool.BLANK;
			}

			return servletPath;
		}

		LiferayDispatchTargets originalLiferayDispatchTargets =
			_liferayDispatchTargetsDeque.getLast();

		return originalLiferayDispatchTargets.getServletPath();
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpSession httpSession = _httpServletRequest.getSession(create);

		if (httpSession != null) {
			LiferayDispatchTargets liferayDispatchTargets =
				_liferayDispatchTargetsDeque.peek();

			LiferayContextController liferayContextController =
				liferayDispatchTargets.getContextController();

			return liferayContextController.getHttpSessionWrapper(
				httpSession, _getServletContext(liferayDispatchTargets));
		}

		return null;
	}

	public synchronized void pop() {
		if (_liferayDispatchTargetsDeque.size() > 1) {
			_liferayDispatchTargetsDeque.pop();
		}
	}

	public synchronized void push(
		LiferayDispatchTargets liferayDispatchTargets) {

		liferayDispatchTargets.addRequestParameters(_httpServletRequest);

		_liferayDispatchTargetsDeque.push(liferayDispatchTargets);
	}

	@Override
	public void removeAttribute(String name) {
		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		if (_dispatcherAttributes.contains(name)) {
			Map<String, Object> specialOverrides =
				liferayDispatchTargets.getSpecialOverides();

			specialOverrides.remove(name);
		}

		_httpServletRequest.removeAttribute(name);

		LiferayContextController liferayContextController =
			liferayDispatchTargets.getContextController();

		EventListeners eventListeners =
			liferayContextController.getEventListeners();

		List<ServletRequestAttributeListener> servletRequestAttributeListeners =
			eventListeners.get(ServletRequestAttributeListener.class);

		if (!servletRequestAttributeListeners.isEmpty()) {
			ServletRequestAttributeEvent servletRequestAttributeEvent =
				new ServletRequestAttributeEvent(
					_getServletContext(liferayDispatchTargets), this, name,
					null);

			for (ServletRequestAttributeListener
					servletRequestAttributeListener :
						servletRequestAttributeListeners) {

				servletRequestAttributeListener.attributeRemoved(
					servletRequestAttributeEvent);
			}
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		boolean attributeAdded = false;

		if (_httpServletRequest.getAttribute(name) == null) {
			attributeAdded = true;
		}

		LiferayDispatchTargets liferayDispatchTargets =
			_liferayDispatchTargetsDeque.peek();

		if ((value == null) && _dispatcherAttributes.contains(name)) {
			Map<String, Object> specialOverrides =
				liferayDispatchTargets.getSpecialOverides();

			specialOverrides.put(name, _NULL_PLACEHOLDER);
		}

		_httpServletRequest.setAttribute(name, value);

		LiferayContextController liferayContextController =
			liferayDispatchTargets.getContextController();

		EventListeners eventListeners =
			liferayContextController.getEventListeners();

		List<ServletRequestAttributeListener> servletRequestAttributeListeners =
			eventListeners.get(ServletRequestAttributeListener.class);

		if (!servletRequestAttributeListeners.isEmpty()) {
			ServletRequestAttributeEvent servletRequestAttributeEvent =
				new ServletRequestAttributeEvent(
					_getServletContext(liferayDispatchTargets), this, name,
					value);

			for (ServletRequestAttributeListener
					servletRequestAttributeListener :
						servletRequestAttributeListeners) {

				if (attributeAdded) {
					servletRequestAttributeListener.attributeAdded(
						servletRequestAttributeEvent);
				}
				else {
					servletRequestAttributeListener.attributeReplaced(
						servletRequestAttributeEvent);
				}
			}
		}
	}

	private ServletContext _getServletContext(
		LiferayDispatchTargets liferayDispatchTargets) {

		EndpointRegistration<?> endpointRegistration =
			liferayDispatchTargets.getServletRegistration();

		Servlet servlet = endpointRegistration.getService();

		ServletConfig servletConfig = servlet.getServletConfig();

		return servletConfig.getServletContext();
	}

	private static final Object _NULL_PLACEHOLDER = new Object();

	private static final Set<String> _dispatcherAttributes = Set.of(
		RequestDispatcher.ERROR_EXCEPTION,
		RequestDispatcher.ERROR_EXCEPTION_TYPE, RequestDispatcher.ERROR_MESSAGE,
		RequestDispatcher.ERROR_REQUEST_URI,
		RequestDispatcher.ERROR_SERVLET_NAME,
		RequestDispatcher.ERROR_STATUS_CODE,
		RequestDispatcher.FORWARD_CONTEXT_PATH,
		RequestDispatcher.FORWARD_PATH_INFO,
		RequestDispatcher.FORWARD_QUERY_STRING,
		RequestDispatcher.FORWARD_REQUEST_URI,
		RequestDispatcher.FORWARD_SERVLET_PATH,
		RequestDispatcher.INCLUDE_CONTEXT_PATH,
		RequestDispatcher.INCLUDE_PATH_INFO,
		RequestDispatcher.INCLUDE_QUERY_STRING,
		RequestDispatcher.INCLUDE_REQUEST_URI,
		RequestDispatcher.INCLUDE_SERVLET_PATH);

	private final HttpServletRequest _httpServletRequest;
	private final Deque<LiferayDispatchTargets> _liferayDispatchTargetsDeque =
		new LinkedList<>();

}