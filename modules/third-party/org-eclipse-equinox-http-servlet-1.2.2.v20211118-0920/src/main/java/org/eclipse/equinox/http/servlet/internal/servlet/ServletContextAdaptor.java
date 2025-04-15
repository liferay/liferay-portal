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
 *     Juan Gonzalez <juan.gonzalez@liferay.com> - Bug 486412
 *******************************************************************************/
package org.eclipse.equinox.http.servlet.internal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.net.URL;
import java.security.*;
import java.util.*;
import javax.servlet.*;
import org.eclipse.equinox.http.servlet.internal.context.*;
import org.eclipse.equinox.http.servlet.internal.util.Const;
import org.eclipse.equinox.http.servlet.internal.util.EventListeners;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.context.ServletContextHelper;

public class ServletContextAdaptor {

	private final static Map<Method, Method> contextToHandlerMethods;

	static {
		contextToHandlerMethods = createContextToHandlerMethods();
	}

	private static Map<Method, Method> createContextToHandlerMethods() {
		Map<Method, Method> methods = new HashMap<Method, Method>();
		Method[] handlerMethods =
			ServletContextAdaptor.class.getDeclaredMethods();

		for (int i = 0; i < handlerMethods.length; i++) {
			Method handlerMethod = handlerMethods[i];
			String name = handlerMethod.getName();
			Class<?>[] parameterTypes = handlerMethod.getParameterTypes();

			try {
				Method method = ServletContext.class.getMethod(
					name, parameterTypes);
				methods.put(method, handlerMethod);
			}
			catch (NoSuchMethodException e) {
				// do nothing
			}
		}

		try {
			Method equalsMethod = Object.class.getMethod("equals", Object.class);  //$NON-NLS-1$
			Method equalsHandlerMethod = ServletContextAdaptor.class.getMethod("equals", Object.class); //$NON-NLS-1$
			methods.put(equalsMethod, equalsHandlerMethod);

			Method hashCodeMethod = Object.class.getMethod("hashCode", (Class<?>[])null);  //$NON-NLS-1$
			Method hashCodeHandlerMethod = ServletContextAdaptor.class.getMethod("hashCode", (Class<?>[])null); //$NON-NLS-1$
			methods.put(hashCodeMethod, hashCodeHandlerMethod);
		}
		catch (NoSuchMethodException e) {
				// do nothing
		}

		return methods;
	}

	public ServletContextAdaptor(
		ContextController contextController, Bundle bundle,
		ServletContextHelper servletContextHelper,
		ServletContextHelperDataContext servletContextHelperDataContext,
		EventListeners eventListeners, AccessControlContext acc) {

		this.contextController = contextController;
		this.servletContextHelperDataContext = servletContextHelperDataContext;
		this.servletContext = servletContextHelperDataContext.getServletContext();
		this.servletContextHelper = servletContextHelper;
		this.eventListeners = eventListeners;
		this.acc = acc;
		this.bundle = bundle;

		BundleWiring bundleWiring = this.bundle.adapt(BundleWiring.class);

		this.classLoader = bundleWiring.getClassLoader();
	}

	public ServletContext createServletContext() {
		Class<?> clazz = getClass();
		ClassLoader curClassLoader = clazz.getClassLoader();
		Class<?>[] interfaces = new Class[] {ServletContext.class};

		return (ServletContext)Proxy.newProxyInstance(
			curClassLoader, interfaces, new AdaptorInvocationHandler());
	}

	public boolean equals (Object obj) {
		if (!(obj instanceof ServletContext)) {
			return false;
		}

		if (!(Proxy.isProxyClass(obj.getClass())))  {
			return false;
		}

		InvocationHandler invocationHandler = Proxy.getInvocationHandler(obj);

		if (!(invocationHandler instanceof AdaptorInvocationHandler)) {
			return false;
		}

		AdaptorInvocationHandler adaptorInvocationHandler = (AdaptorInvocationHandler)invocationHandler;

		return contextController.equals(adaptorInvocationHandler.getContextController());
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public String getContextPath() {
		return contextController.getFullContextPath();
	}

	public Object getAttribute(String attributeName) {
		Dictionary<String, Object> attributes = getContextAttributes();

		if (attributeName.equals("osgi-bundlecontext")) { //$NON-NLS-1$
			return bundle.getBundleContext();
		}

		return attributes.get(attributeName);
	}

	public Enumeration<String> getAttributeNames() {
		Dictionary<String, Object> attributes = getContextAttributes();
		return attributes.keys();
	}

	public String getInitParameter(String name) {
		return contextController.getInitParams().get(name);
	}

	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(
			contextController.getInitParams().keySet());
	}

	public String getMimeType(final String name) {
		String mimeType = null;

		try {
			mimeType = AccessController.doPrivileged(
				new PrivilegedExceptionAction<String>() {
					public String run() throws Exception {
						return servletContextHelper.getMimeType(name);
					}
				}, acc
			);
		}
		catch (PrivilegedActionException e) {
			servletContext.log(e.getException().getMessage(), e.getException());
		}

		return (mimeType != null) ? mimeType : servletContext.getMimeType(name);
	}

	public RequestDispatcher getNamedDispatcher(String servletName) {
		DispatchTargets dispatchTargets = contextController.getDispatchTargets(
			servletName, null, null, null, null, null, Match.EXACT);

		if (dispatchTargets == null) {
			return null;
		}

		return new RequestDispatcherAdaptor(dispatchTargets, servletName);
	}

	public String getRealPath(final String path) {
		try {
			return AccessController.doPrivileged(
				new PrivilegedExceptionAction<String>() {
					public String run() throws Exception {
						return servletContextHelper.getRealPath(path);
					}
				}, acc
			);
		}
		catch (PrivilegedActionException e) {
			servletContext.log(e.getException().getMessage(), e.getException());
		}

		return null;
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		// no relative paths supported, must begin with '/'
		if (!path.startsWith(Const.SLASH)) {
			return null;
		}
		// if the path starts with the full context path strip it
		if (path.startsWith(contextController.getFullContextPath())) {
			path = path.substring(contextController.getFullContextPath().length());
		}

		DispatchTargets dispatchTargets = contextController.getDispatchTargets(path);

		if (dispatchTargets == null) {
			return null;
		}

		return new RequestDispatcherAdaptor(dispatchTargets, path);
	}

	public URL getResource(final String name) {
		try {
			return AccessController.doPrivileged(
				new PrivilegedExceptionAction<URL>() {
					public URL run() throws Exception {
						return servletContextHelper.getResource(name);
					}
				}, acc
			);
		}
		catch (PrivilegedActionException e) {
			servletContext.log(e.getException().getMessage(), e.getException());
		}

		return null;
	}

	public InputStream getResourceAsStream(String name) {
		URL url = getResource(name);

		if (url != null) {
			try {
				return url.openStream();
			}
			catch (IOException ioe) {
				servletContext.log(ioe.getMessage(), ioe);
			}
		}

		return null;
	}

	/**
	 * 	@see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
	 */
	public Set<String> getResourcePaths(final String name) {
		if (name == null || !name.startsWith(Const.SLASH))
			return null;

		try {
			return AccessController.doPrivileged(
				new PrivilegedExceptionAction<Set<String>>() {
					public Set<String> run() throws Exception {
						return servletContextHelper.getResourcePaths(name);
					}
				}, acc
			);
		}
		catch (PrivilegedActionException e) {
			servletContext.log(e.getException().getMessage(), e.getException());
		}

		return null;
	}

	public String getServletContextName() {
		return contextController.getContextName();
	}

	public int hashCode() {
		return contextController.hashCode();
	}

	public void removeAttribute(String attributeName) {
		Dictionary<String, Object> attributes = getContextAttributes();
		Object attributeValue = attributes.remove(attributeName);

		List<ServletContextAttributeListener> listeners =
			eventListeners.get(ServletContextAttributeListener.class);

		if (listeners.isEmpty()) {
			return;
		}

		ServletContextAttributeEvent servletContextAttributeEvent =
			new ServletContextAttributeEvent(
				servletContextTL.get(), attributeName, attributeValue);

		for (ServletContextAttributeListener servletContextAttributeListener : listeners) {
			servletContextAttributeListener.attributeRemoved(
				servletContextAttributeEvent);
		}
	}

	public void addFilter(String arg1, Class<? extends Filter> arg2) {
		throw new UnsupportedOperationException();
	}
	public void addFilter(String arg1, String arg2) {
		throw new UnsupportedOperationException();
	}
	public void addFilter(String arg1, Filter arg2) {
		throw new UnsupportedOperationException();
	}

	public void addListener(Class<?> arg1){
		throw new UnsupportedOperationException();
	}
	public void addListener(String arg1){
		throw new UnsupportedOperationException();
	}
	public void addListener(EventListener arg1){
		throw new UnsupportedOperationException();
	}

	public void addServlet(String arg1, Class<? extends Servlet> arg2) {
		throw new UnsupportedOperationException();
	}
	public void addServlet(String arg1, String arg2) {
		throw new UnsupportedOperationException();
	}
	public void addServlet(String arg1, Servlet arg2) {
		throw new UnsupportedOperationException();
	}

	public void createFilter(Class<?> arg1) {
		throw new UnsupportedOperationException();
	}
	public void createServlet(Class<?> arg1) {
		throw new UnsupportedOperationException();
	}
	public void createListener(Class<?> arg1) {
		throw new UnsupportedOperationException();
	}

	public void declareRoles(String... arg1) {
		throw new UnsupportedOperationException();
	}

	public void setAttribute(String attributeName, Object attributeValue) {
		if (attributeValue == null) {
			removeAttribute(attributeName);

			return;
		}

		Dictionary<String, Object> attributes = getContextAttributes();
		boolean added = (attributes.get(attributeName) == null);
		attributes.put(attributeName, attributeValue);

		List<ServletContextAttributeListener> listeners =
			eventListeners.get(ServletContextAttributeListener.class);

		if (listeners.isEmpty()) {
			return;
		}

		ServletContextAttributeEvent servletContextAttributeEvent =
			new ServletContextAttributeEvent(
				servletContextTL.get(), attributeName, attributeValue);

		for (ServletContextAttributeListener servletContextAttributeListener : listeners) {
			if (added) {
				servletContextAttributeListener.attributeAdded(
					servletContextAttributeEvent);
			}
			else {
				servletContextAttributeListener.attributeReplaced(
					servletContextAttributeEvent);
			}
		}
	}

	public String toString() {
		String value = string;

		if (value == null) {
			value = SIMPLE_NAME + '[' + contextController + ']';

			string = value;
		}

		return value;
	}

	Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		boolean useThreadLocal =
			"removeAttribute".equals(method.getName()) ||
			"setAttribute".equals(method.getName());

		if (useThreadLocal) {
			servletContextTL.set((ServletContext)proxy);
		}

		try {
			Method m = contextToHandlerMethods.get(method);
			try {
				if (m != null) {
					return m.invoke(this, args);
				}
				return method.invoke(servletContext, args);
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}
		finally {
			if (useThreadLocal) {
				servletContextTL.remove();
			}
		}
	}

	private Dictionary<String, Object> getContextAttributes() {
		return servletContextHelperDataContext.getContextAttributes();
	}

	private class AdaptorInvocationHandler implements InvocationHandler {
		public AdaptorInvocationHandler() {}

		public ContextController getContextController() {
			return contextController;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			return ServletContextAdaptor.this.invoke(proxy, method, args);
		}
	}

	private final static String SIMPLE_NAME =
		ServletContextAdaptor.class.getSimpleName();

	private final static ThreadLocal<ServletContext> servletContextTL = new ThreadLocal<ServletContext>();

	private final AccessControlContext acc;
	private final Bundle bundle;
	private final ClassLoader classLoader;
	final ContextController contextController;
	private final EventListeners eventListeners;
	private final ServletContextHelperDataContext servletContextHelperDataContext;
	private final ServletContext servletContext;
	final ServletContextHelper servletContextHelper;
	private String string;

}
/* @generated */