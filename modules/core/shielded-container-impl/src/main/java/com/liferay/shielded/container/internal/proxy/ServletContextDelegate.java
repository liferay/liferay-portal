/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal.proxy;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.http.HttpSessionListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class ServletContextDelegate {

	public static ServletContext create(
		ClassLoader classLoader, ServletContext servletContext) {

		ProxyFactory proxyFactory = new ProxyFactory(classLoader);

		ServletContextDelegate servletContextDelegate =
			new ServletContextDelegate(
				classLoader, proxyFactory, servletContext);

		servletContext = proxyFactory.createASMWrapper(
			classLoader, ServletContext.class, servletContextDelegate,
			servletContext);

		servletContextDelegate._proxiedServletContext = servletContext;

		return servletContext;
	}

	public FilterRegistration.Dynamic addFilter(
		String filterName, Class<? extends Filter> filterClass) {

		try {
			return _servletContext.addFilter(
				filterName,
				new FilterWrapper(
					_proxyFactory, new LazyInstanceSupplier<>(filterClass),
					_proxiedServletContext));
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new RuntimeException(noSuchMethodException);
		}
	}

	public FilterRegistration.Dynamic addFilter(
		String filterName, Filter filter) {

		return _servletContext.addFilter(
			filterName,
			new FilterWrapper(
				_proxyFactory, () -> filter, _proxiedServletContext));
	}

	public FilterRegistration.Dynamic addFilter(
		String filterName, String filterClassName) {

		try {
			return addFilter(
				filterName,
				(Class<? extends Filter>)_classLoader.loadClass(
					filterClassName));
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(classNotFoundException);
		}
	}

	public void addListener(Class<? extends EventListener> listenerClass) {
		try {
			Constructor<? extends EventListener> constructor =
				listenerClass.getConstructor();

			addListener(constructor.newInstance());
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	public void addListener(String listenerClassName) {
		try {
			addListener(
				(Class<? extends EventListener>)_classLoader.loadClass(
					listenerClassName));
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(classNotFoundException);
		}
	}

	public <T extends EventListener> void addListener(T t) {
		Class<?> clazz = t.getClass();

		Set<Class<?>> interfaceClasses = new LinkedHashSet<>();

		while (clazz != null) {
			for (Class<?> interfaceClass : clazz.getInterfaces()) {
				interfaceClasses.add(interfaceClass);
			}

			clazz = clazz.getSuperclass();
		}

		InvocationHandler invocationHandler =
			new EventListenerInvocationHandler(
				_proxiedServletContext, _classLoader, t);

		if (interfaceClasses.contains(HttpSessionListener.class)) {
			invocationHandler = new HttpSessionListenerInvocationHandlerWrapper(
				invocationHandler, _proxyFactory, _classLoader);
		}

		_servletContext.addListener(
			_proxyFactory.<T>newProxyInstance(
				_classLoader, interfaceClasses.toArray(new Class<?>[0]),
				invocationHandler));
	}

	public ServletRegistration.Dynamic addServlet(
		String servletName, Class<? extends Servlet> servletClass) {

		try {
			return _servletContext.addServlet(
				servletName,
				new ServletWrapper(
					_proxyFactory, new LazyInstanceSupplier<>(servletClass),
					_proxiedServletContext));
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new RuntimeException(noSuchMethodException);
		}
	}

	public ServletRegistration.Dynamic addServlet(
		String servletName, Servlet servlet) {

		return _servletContext.addServlet(
			servletName,
			new ServletWrapper(
				_proxyFactory, () -> servlet, _proxiedServletContext));
	}

	public ServletRegistration.Dynamic addServlet(
		String servletName, String servletClassName) {

		try {
			return addServlet(
				servletName,
				(Class<? extends Servlet>)_classLoader.loadClass(
					servletClassName));
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(classNotFoundException);
		}
	}

	public Object getAttribute(String name) {
		return _servletContext.getAttribute(_encodeName(name));
	}

	public Enumeration<String> getAttributeNames() {
		List<String> names = new ArrayList<>();

		Enumeration<String> enumeration = _servletContext.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			names.add(_decodeName(enumeration.nextElement()));
		}

		return Collections.enumeration(names);
	}

	public ClassLoader getClassLoader() {
		return _classLoader;
	}

	public String getContextPath() {
		String contextPath = _servletContext.getContextPath();

		if (contextPath.equals("/")) {
			return "";
		}

		return contextPath;
	}

	public String getInitParameter(String name) {
		return _servletContext.getInitParameter(_encodeName(name));
	}

	public Enumeration<String> getInitParameterNames() {
		List<String> names = new ArrayList<>();

		Enumeration<String> enumeration =
			_servletContext.getInitParameterNames();

		while (enumeration.hasMoreElements()) {
			names.add(_decodeName(enumeration.nextElement()));
		}

		return Collections.enumeration(names);
	}

	public void removeAttribute(String name) {
		_servletContext.removeAttribute(_encodeName(name));
	}

	public void setAttribute(String name, Object object) {
		_servletContext.setAttribute(_encodeName(name), object);
	}

	public boolean setInitParameter(String name, String value) {
		return _servletContext.setInitParameter(_encodeName(name), value);
	}

	private ServletContextDelegate(
		ClassLoader classLoader, ProxyFactory proxyFactory,
		ServletContext servletContext) {

		_classLoader = classLoader;
		_proxyFactory = proxyFactory;
		_servletContext = servletContext;
	}

	private String _decodeName(String name) {
		if (name.startsWith(_LIFERAY_NAMESPACE)) {
			return name.substring(_LIFERAY_NAMESPACE.length());
		}

		return name;
	}

	private String _encodeName(String name) {
		if (name.startsWith(_APACHE_NAMESPACE)) {
			return _LIFERAY_NAMESPACE.concat(name);
		}

		return name;
	}

	private static final String _APACHE_NAMESPACE = "org.apache.";

	private static final String _LIFERAY_NAMESPACE = "com.liferay.";

	private final ClassLoader _classLoader;
	private ServletContext _proxiedServletContext;
	private final ProxyFactory _proxyFactory;
	private final ServletContext _servletContext;

}