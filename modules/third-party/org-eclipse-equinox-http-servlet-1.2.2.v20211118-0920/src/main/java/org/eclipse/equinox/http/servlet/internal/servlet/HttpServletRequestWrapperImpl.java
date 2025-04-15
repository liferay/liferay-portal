/*******************************************************************************
 * Copyright (c) 2005, 2016 Cognos Incorporated, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cognos Incorporated - initial API and implementation
 *     IBM Corporation - bug fixes and enhancements
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/
package org.eclipse.equinox.http.servlet.internal.servlet;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.util.Const;
import org.eclipse.equinox.http.servlet.internal.util.EventListeners;
import org.osgi.service.http.HttpContext;

public class HttpServletRequestWrapperImpl extends HttpServletRequestWrapper {

	private final Deque<DispatchTargets> dispatchTargets = new LinkedList<DispatchTargets>();
	private final HttpServletRequest request;

	private static final Set<String> dispatcherAttributes =	new HashSet<String>();

	static {
		dispatcherAttributes.add(RequestDispatcher.ERROR_EXCEPTION);
		dispatcherAttributes.add(RequestDispatcher.ERROR_EXCEPTION_TYPE);
		dispatcherAttributes.add(RequestDispatcher.ERROR_MESSAGE);
		dispatcherAttributes.add(RequestDispatcher.ERROR_REQUEST_URI);
		dispatcherAttributes.add(RequestDispatcher.ERROR_SERVLET_NAME);
		dispatcherAttributes.add(RequestDispatcher.ERROR_STATUS_CODE);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_CONTEXT_PATH);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_PATH_INFO);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_QUERY_STRING);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_REQUEST_URI);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_SERVLET_PATH);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_CONTEXT_PATH);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_PATH_INFO);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_QUERY_STRING);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_REQUEST_URI);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_SERVLET_PATH);
	}

	private static final Object NULL_PLACEHOLDER = new Object();

	public static HttpServletRequestWrapperImpl findHttpRuntimeRequest(
		HttpServletRequest request) {

		while (request instanceof HttpServletRequestWrapper) {
			if (request instanceof HttpServletRequestWrapperImpl) {
				return (HttpServletRequestWrapperImpl)request;
			}

			request = (HttpServletRequest)((HttpServletRequestWrapper)request).getRequest();
		}

		return null;
	}

	public HttpServletRequestWrapperImpl(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	public String getAuthType() {
		String authType = (String) this.getAttribute(HttpContext.AUTHENTICATION_TYPE);
		if (authType != null)
			return authType;

		return request.getAuthType();
	}

	public String getRemoteUser() {
		String remoteUser = (String) this.getAttribute(HttpContext.REMOTE_USER);
		if (remoteUser != null)
			return remoteUser;

		return request.getRemoteUser();
	}

	public String getPathInfo() {
		DispatchTargets currentDispatchTargets = dispatchTargets.peek();

		if ((currentDispatchTargets.getServletName() != null) ||
			(currentDispatchTargets.getDispatcherType() == DispatcherType.INCLUDE)) {
			return this.dispatchTargets.getLast().getPathInfo();
		}
		return currentDispatchTargets.getPathInfo();
	}

	public DispatcherType getDispatcherType() {
		return dispatchTargets.peek().getDispatcherType();
	}

	public String getParameter(String name) {
		String[] values = getParameterValues(name);
		if ((values == null) || (values.length == 0)) {
			return null;
		}
		return values[0];
	}

	public Map<String, String[]> getParameterMap() {
		return dispatchTargets.peek().getParameterMap();
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	public String[] getParameterValues(String name) {
		return getParameterMap().get(name);
	}

	@Override
	public String getQueryString() {
		DispatchTargets currentDispatchTargets = dispatchTargets.peek();

		if ((currentDispatchTargets.getServletName() != null) ||
			(currentDispatchTargets.getDispatcherType() == DispatcherType.INCLUDE)) {
			return request.getQueryString();
		}
		return currentDispatchTargets.getQueryString();
	}

	@Override
	public String getRequestURI() {
		DispatchTargets currentDispatchTargets = dispatchTargets.peek();

		if ((currentDispatchTargets.getServletName() != null) ||
			(currentDispatchTargets.getDispatcherType() == DispatcherType.INCLUDE)) {
			return request.getRequestURI();
		}
		return currentDispatchTargets.getRequestURI();
	}

	public ServletContext getServletContext() {
		return dispatchTargets.peek().getServletRegistration().getServletContext();
	}

	public String getServletPath() {
		DispatchTargets currentDispatchTargets = dispatchTargets.peek();

		if ((currentDispatchTargets.getServletName() != null) ||
			(currentDispatchTargets.getDispatcherType() == DispatcherType.INCLUDE)) {
			return this.dispatchTargets.getLast().getServletPath();
		}
		if (currentDispatchTargets.getServletPath().equals(Const.SLASH)) {
			return Const.BLANK;
		}
		return currentDispatchTargets.getServletPath();
	}

	public String getContextPath() {
		return dispatchTargets.peek().getContextController().getFullContextPath();
	}

	public Object getAttribute(String attributeName) {
		DispatchTargets current = dispatchTargets.peek();

		DispatcherType dispatcherType = current.getDispatcherType();

		Map<String, Object> specialOverides = current.getSpecialOverides();

		if ((dispatcherType == DispatcherType.ASYNC) ||
			(dispatcherType == DispatcherType.REQUEST) ||
			!attributeName.startsWith("javax.servlet.")) {

			return request.getAttribute(attributeName);
		}

		boolean hasServletName = (current.getServletName() != null);
		boolean isDispatcherAttribute = dispatcherAttributes.contains(attributeName);

		if (dispatcherType == DispatcherType.ERROR) {
			if (isDispatcherAttribute &&
				!attributeName.startsWith("javax.servlet.error.")) { //$NON-NLS-1$

				return null;
			}
		}
		else if (dispatcherType == DispatcherType.INCLUDE) {
			if (hasServletName && attributeName.startsWith("javax.servlet.include")) {
				return null;
			}

			if (isDispatcherAttribute) {
				Object specialOveride = specialOverides.get(attributeName);

				if (specialOveride == NULL_PLACEHOLDER) {
					return null;
				}

				Object attributeValue = super.getAttribute(attributeName);

				if (attributeValue != null) {
					return attributeValue;
				}
			}

			if (attributeName.equals(RequestDispatcher.INCLUDE_CONTEXT_PATH)) {
				return current.getContextController().getContextPath();
			}
			else if (attributeName.equals(RequestDispatcher.INCLUDE_PATH_INFO)) {
				return current.getPathInfo();
			}
			else if (attributeName.equals(RequestDispatcher.INCLUDE_QUERY_STRING)) {
				return current.getQueryString();
			}
			else if (attributeName.equals(RequestDispatcher.INCLUDE_REQUEST_URI)) {
				return current.getRequestURI();
			}
			else if (attributeName.equals(RequestDispatcher.INCLUDE_SERVLET_PATH)) {
				return current.getServletPath();
			}

			if (isDispatcherAttribute) {
				return null;
			}
		}
		else if (dispatcherType == DispatcherType.FORWARD) {
			if (hasServletName && attributeName.startsWith("javax.servlet.forward")) {
				return null;
			}

			if (isDispatcherAttribute) {
				Object specialOveride = specialOverides.get(attributeName);

				if (specialOveride == NULL_PLACEHOLDER) {
					return null;
				}
			}

			DispatchTargets original = dispatchTargets.getLast();

			if (attributeName.equals(RequestDispatcher.FORWARD_CONTEXT_PATH)) {
				return original.getContextController().getContextPath();
			}
			else if (attributeName.equals(RequestDispatcher.FORWARD_PATH_INFO)) {
				return original.getPathInfo();
			}
			else if (attributeName.equals(RequestDispatcher.FORWARD_QUERY_STRING)) {
				return original.getQueryString();
			}
			else if (attributeName.equals(RequestDispatcher.FORWARD_REQUEST_URI)) {
				return original.getRequestURI();
			}
			else if (attributeName.equals(RequestDispatcher.FORWARD_SERVLET_PATH)) {
				return original.getServletPath();
			}

			if (isDispatcherAttribute) {
				return null;
			}
		}

		return request.getAttribute(attributeName);
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		DispatchTargets currentDispatchTarget = dispatchTargets.peek();

		ContextController contextController =
			currentDispatchTarget.getContextController();

		// support relative paths
		if (!path.startsWith(Const.SLASH)) {
			path = currentDispatchTarget.getServletPath() + Const.SLASH + path;
		}
		// if the path starts with the full context path strip it
		else if (path.startsWith(contextController.getFullContextPath())) {
			path = path.substring(contextController.getFullContextPath().length());
		}

		DispatchTargets requestedDispatchTargets = contextController.getDispatchTargets(path);

		if (requestedDispatchTargets == null) {
			return null;
		}

		return new RequestDispatcherAdaptor(requestedDispatchTargets, path);
	}

	public static String getDispatchPathInfo(HttpServletRequest req) {
		if (req.getDispatcherType() == DispatcherType.INCLUDE)
			return (String) req.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);

		return req.getPathInfo();
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public HttpSession getSession(boolean create) {
		HttpSession session = request.getSession(create);
		if (session != null) {
			DispatchTargets currentDispatchTarget = dispatchTargets.peek();

			return currentDispatchTarget.getContextController().getSessionAdaptor(
				session, currentDispatchTarget.getServletRegistration().getT().getServletConfig().getServletContext());
		}

		return null;
	}

	public synchronized void pop() {
		if (dispatchTargets.size() > 1) {
			this.dispatchTargets.pop();
		}
	}

	public synchronized void push(DispatchTargets toPush) {
		toPush.addRequestParameters(request);
		this.dispatchTargets.push(toPush);
	}

	public void removeAttribute(String name) {
		if (dispatcherAttributes.contains(name)) {
			DispatchTargets current = dispatchTargets.peek();

			current.getSpecialOverides().remove(name);
		}

		request.removeAttribute(name);

		DispatchTargets currentDispatchTarget = dispatchTargets.peek();

		EventListeners eventListeners = currentDispatchTarget.getContextController().getEventListeners();

		List<ServletRequestAttributeListener> listeners = eventListeners.get(
			ServletRequestAttributeListener.class);

		if (listeners.isEmpty()) {
			return;
		}

		ServletRequestAttributeEvent servletRequestAttributeEvent =
			new ServletRequestAttributeEvent(
				currentDispatchTarget.getServletRegistration().getServletContext(), this, name, null);

		for (ServletRequestAttributeListener servletRequestAttributeListener : listeners) {
			servletRequestAttributeListener.attributeRemoved(
				servletRequestAttributeEvent);
		}
	}

	public void setAttribute(String name, Object value) {
		boolean added = (request.getAttribute(name) == null);

		if ((value == null) && dispatcherAttributes.contains(name)) {
			DispatchTargets current = dispatchTargets.peek();

			current.getSpecialOverides().put(name, NULL_PLACEHOLDER);
		}

		request.setAttribute(name, value);

		DispatchTargets currentDispatchTarget = dispatchTargets.peek();

		EventListeners eventListeners = currentDispatchTarget.getContextController().getEventListeners();

		List<ServletRequestAttributeListener> listeners = eventListeners.get(
			ServletRequestAttributeListener.class);

		if (listeners.isEmpty()) {
			return;
		}

		ServletRequestAttributeEvent servletRequestAttributeEvent =
			new ServletRequestAttributeEvent(
				currentDispatchTarget.getServletRegistration().getServletContext(), this, name, value);

		for (ServletRequestAttributeListener servletRequestAttributeListener : listeners) {
			if (added) {
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
/* @generated */