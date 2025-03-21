/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal.adapter;

import com.liferay.petra.io.BigEndianCodec;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.osgi.web.servlet.JSPServletFactory;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.FilterDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.ListenerDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.ServletDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebXMLDefinition;
import com.liferay.portal.osgi.web.wab.extender.internal.registration.FilterRegistrationImpl;
import com.liferay.portal.osgi.web.wab.extender.internal.registration.ServletRegistrationImpl;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.Registration;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Raymond Augé
 */
public class ModifiableServletContextAdapter
	implements InvocationHandler, ModifiableServletContext {

	public static ServletContext createInstance(
		BundleContext bundleContext, ServletContext servletContext,
		JSPServletFactory jspServletFactory,
		WebXMLDefinition webXMLDefinition) {

		return _servletContextProxyProviderFunction.apply(
			new ModifiableServletContextAdapter(
				servletContext, bundleContext, jspServletFactory,
				webXMLDefinition));
	}

	public static ServletContext createInstance(
		BundleContext bundleContext, ServletContext servletContext,
		JSPServletFactory jspServletFactory, WebXMLDefinition webXMLDefinition,
		List<ListenerDefinition> listenerDefinitions,
		Map<String, FilterRegistrationImpl> filterRegistrationImpls,
		Map<String, ServletRegistrationImpl> servletRegistrationImpls,
		Map<String, Object> attributes) {

		ServletContext newServletContext = createInstance(
			bundleContext, servletContext, jspServletFactory, webXMLDefinition);

		Set<String> attributeNames = attributes.keySet();

		if (attributeNames != null) {
			for (String attributeName : attributeNames) {
				newServletContext.setAttribute(
					attributeName, attributes.get(attributeName));
			}
		}

		if (listenerDefinitions != null) {
			for (ListenerDefinition listenerDefinition : listenerDefinitions) {
				newServletContext.addListener(
					listenerDefinition.getEventListener());
			}
		}

		ModifiableServletContext modifiableServletContext =
			(ModifiableServletContext)newServletContext;

		if (filterRegistrationImpls != null) {
			Map<String, FilterRegistrationImpl> newFilterRegistrationImpls =
				modifiableServletContext.getFilterRegistrationImpls();

			for (Map.Entry<String, FilterRegistrationImpl> entry :
					filterRegistrationImpls.entrySet()) {

				newFilterRegistrationImpls.put(
					entry.getKey(), entry.getValue());
			}
		}

		if (servletRegistrationImpls != null) {
			Map<String, ServletRegistrationImpl> newServletRegistrationImpls =
				modifiableServletContext.getServletRegistrationImpls();

			for (Map.Entry<String, ServletRegistrationImpl> entry :
					servletRegistrationImpls.entrySet()) {

				newServletRegistrationImpls.put(
					entry.getKey(), entry.getValue());
			}
		}

		return newServletContext;
	}

	public ModifiableServletContextAdapter(
		ServletContext servletContext, BundleContext bundleContext,
		JSPServletFactory jspServletFactory,
		WebXMLDefinition webXMLDefinition) {

		_servletContext = servletContext;
		_bundleContext = bundleContext;
		_jspServletFactory = jspServletFactory;
		_webXMLDefinition = webXMLDefinition;

		_bundle = bundleContext.getBundle();
	}

	public FilterRegistration.Dynamic addFilter(
		String filterName, Class<? extends Filter> filterClass) {

		return addFilter(filterName, filterClass.getName());
	}

	public FilterRegistration.Dynamic addFilter(
		String filterName, Filter filter) {

		FilterRegistrationImpl filterRegistrationImpl =
			getFilterRegistrationImpl(filterName);

		if (filterRegistrationImpl == null) {
			filterRegistrationImpl = new FilterRegistrationImpl();
		}

		Class<? extends Filter> filterClass = filter.getClass();

		filterRegistrationImpl.setClassName(filterClass.getName());

		filterRegistrationImpl.setName(filterName);
		filterRegistrationImpl.setInstance(filter);

		_filterRegistrationImpls.put(filterName, filterRegistrationImpl);

		return filterRegistrationImpl;
	}

	public FilterRegistration.Dynamic addFilter(
		String filterName, String className) {

		FilterRegistrationImpl filterRegistrationImpl =
			getFilterRegistrationImpl(filterName);

		if (filterRegistrationImpl == null) {
			filterRegistrationImpl = new FilterRegistrationImpl();
		}

		filterRegistrationImpl.setClassName(className);
		filterRegistrationImpl.setName(filterName);

		_filterRegistrationImpls.put(filterName, filterRegistrationImpl);

		return filterRegistrationImpl;
	}

	public void addListener(Class<? extends EventListener> eventListenerClass) {
		_eventListeners.put(eventListenerClass, null);
	}

	public void addListener(String className) {
		try {
			Class<?> clazz = _bundle.loadClass(className);

			if (!EventListener.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException();
			}

			Class<? extends EventListener> eventListenerClass =
				clazz.asSubclass(EventListener.class);

			_eventListeners.put(eventListenerClass, null);
		}
		catch (Exception exception) {
			_log.error(
				"Bundle " + _bundle + " is unable to load filter " + className);

			throw new IllegalArgumentException(exception);
		}
	}

	public <T extends EventListener> void addListener(T t) {
		_eventListeners.put(t.getClass(), t);
	}

	public Registration.Dynamic addServlet(
		String servletName, Class<? extends Servlet> servletClass) {

		return addServlet(servletName, servletClass.getName());
	}

	public Registration.Dynamic addServlet(
		String servletName, Servlet servlet) {

		ServletRegistrationImpl servletRegistrationImpl =
			getServletRegistrationImpl(servletName);

		if (servletRegistrationImpl == null) {
			servletRegistrationImpl = new ServletRegistrationImpl();
		}

		Class<? extends Servlet> servetClass = servlet.getClass();

		servletRegistrationImpl.setClassName(servetClass.getName());

		servletRegistrationImpl.setName(servletName);
		servletRegistrationImpl.setInstance(servlet);

		_servletRegistrationImpls.put(servletName, servletRegistrationImpl);

		return servletRegistrationImpl;
	}

	public Registration.Dynamic addServlet(
		String servletName, String className) {

		ServletRegistrationImpl servletRegistrationImpl =
			getServletRegistrationImpl(servletName);

		if (servletRegistrationImpl == null) {
			servletRegistrationImpl = new ServletRegistrationImpl();
		}

		servletRegistrationImpl.setClassName(className);
		servletRegistrationImpl.setName(servletName);

		_servletRegistrationImpls.put(servletName, servletRegistrationImpl);

		return servletRegistrationImpl;
	}

	public <T extends Filter> T createFilter(Class<T> clazz)
		throws ServletException {

		try {
			return clazz.newInstance();
		}
		catch (Throwable throwable) {
			_log.error(
				"Bundle " + _bundle + " is unable to load filter " + clazz);

			throw new ServletException(throwable);
		}
	}

	public <T extends EventListener> T createListener(Class<T> clazz)
		throws ServletException {

		try {
			return clazz.newInstance();
		}
		catch (Throwable throwable) {
			_log.error(
				"Bundle " + _bundle + " is unable to load listener " + clazz);

			throw new ServletException(throwable);
		}
	}

	public <T extends Servlet> T createServlet(Class<T> clazz)
		throws ServletException {

		try {
			return clazz.newInstance();
		}
		catch (Throwable throwable) {
			_log.error(
				"Bundle " + _bundle + " is unable to load servlet " + clazz);

			throw new ServletException(throwable);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ServletContext)) {
			return true;
		}

		ServletContext servletContext = (ServletContext)object;

		if (object instanceof ModifiableServletContext) {
			ModifiableServletContext modifiableServletContext =
				(ModifiableServletContext)object;

			servletContext =
				modifiableServletContext.getWrappedServletContext();
		}

		return _servletContext.equals(servletContext);
	}

	public Object getAttribute(String name) {
		if (_LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED.equals(name)) {
			File file = _bundle.getDataFile(
				_LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED);

			if ((file != null) && file.exists()) {
				try {
					byte[] data = Files.readAllBytes(file.toPath());

					if (data.length == 16) {
						long bundleLastModified = BigEndianCodec.getLong(
							data, 0);

						if (bundleLastModified == _bundle.getLastModified()) {
							return BigEndianCodec.getLong(data, 8);
						}
					}

					file.delete();
				}
				catch (IOException ioException) {
					if (_log.isDebugEnabled()) {
						_log.debug(ioException);
					}
				}

				return null;
			}
		}

		return _servletContext.getAttribute(name);
	}

	@Override
	public Bundle getBundle() {
		return _bundle;
	}

	public FilterRegistration getFilterRegistration(String filterName) {
		return getFilterRegistrationImpl(filterName);
	}

	public FilterRegistrationImpl getFilterRegistrationImpl(String filterName) {
		return _filterRegistrationImpls.get(filterName);
	}

	@Override
	public Map<String, FilterRegistrationImpl> getFilterRegistrationImpls() {
		return _filterRegistrationImpls;
	}

	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return getFilterRegistrationImpls();
	}

	public String getInitParameter(String name) {
		String value = _servletContext.getInitParameter(name);

		if (value == null) {
			return _initParameters.get(name);
		}

		return value;
	}

	public Enumeration<String> getInitParameterNames() {
		List<String> names = new ArrayList<>();

		names.addAll(Collections.list(_servletContext.getInitParameterNames()));
		names.addAll(_initParameters.keySet());

		return Collections.enumeration(names);
	}

	@Override
	public List<ListenerDefinition> getListenerDefinitions() {
		List<ListenerDefinition> listenerDefinitions = new ArrayList<>();

		for (Map.Entry<Class<? extends EventListener>, EventListener> entry :
				_eventListeners.entrySet()) {

			Class<? extends EventListener> eventListenerClass = entry.getKey();

			try {
				EventListener eventListener = entry.getValue();

				if (eventListener == null) {
					eventListener = eventListenerClass.newInstance();
				}

				ListenerDefinition listenerDefinition =
					new ListenerDefinition();

				listenerDefinition.setEventListener(eventListener);

				listenerDefinitions.add(listenerDefinition);
			}
			catch (Exception exception) {
				_log.error(
					"Bundle " + _bundle + " is unable to load listener " +
						eventListenerClass,
					exception);
			}
		}

		return listenerDefinitions;
	}

	public Map<Class<? extends EventListener>, EventListener>
		getListenersImpl() {

		return _eventListeners;
	}

	public ServletRegistration getServletRegistration(String servletName) {
		return getServletRegistrationImpl(servletName);
	}

	public ServletRegistrationImpl getServletRegistrationImpl(
		String servletName) {

		return _servletRegistrationImpls.get(servletName);
	}

	@Override
	public Map<String, ServletRegistrationImpl> getServletRegistrationImpls() {
		return _servletRegistrationImpls;
	}

	public Map<String, ? extends ServletRegistration>
		getServletRegistrations() {

		return getServletRegistrationImpls();
	}

	@Override
	public Map<String, String> getUnregisteredInitParameters() {
		return _initParameters;
	}

	@Override
	public ServletContext getWrappedServletContext() {
		return _servletContext;
	}

	@Override
	public int hashCode() {
		return _servletContext.hashCode();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		Method adapterMethod = _contextAdapterMethods.get(method);

		if (adapterMethod != null) {
			return adapterMethod.invoke(this, args);
		}

		return method.invoke(_servletContext, args);
	}

	@Override
	public void registerFilters() {
		Map<String, FilterDefinition> filterDefinitions =
			_webXMLDefinition.getFilterDefinitions();

		for (FilterRegistrationImpl filterRegistrationImpl :
				_filterRegistrationImpls.values()) {

			String filterClassName = filterRegistrationImpl.getClassName();

			try {
				Filter filter = filterRegistrationImpl.getInstance();

				if (filter == null) {
					Class<?> clazz = _bundle.loadClass(filterClassName);

					Class<? extends Filter> filterClass = clazz.asSubclass(
						Filter.class);

					filter = filterClass.newInstance();

					filterRegistrationImpl.setInstance(filter);
				}

				FilterDefinition filterDefinition = new FilterDefinition();

				filterDefinition.setAsyncSupported(
					filterRegistrationImpl.isAsyncSupported());

				FilterRegistrationImpl.FilterMapping filterMapping =
					filterRegistrationImpl.getFilterMapping();

				for (DispatcherType dispatcherType :
						filterMapping.getDispatchers()) {

					filterDefinition.addDispatcher(dispatcherType.toString());
				}

				filterDefinition.setFilter(filter);
				filterDefinition.setInitParameters(
					filterRegistrationImpl.getInitParameters());
				filterDefinition.setName(filterRegistrationImpl.getName());
				filterDefinition.setServletNames(
					new ArrayList<>(
						filterRegistrationImpl.getServletNameMappings()));
				filterDefinition.setURLPatterns(
					new ArrayList<>(
						filterRegistrationImpl.getUrlPatternMappings()));

				filterDefinitions.put(
					filterRegistrationImpl.getName(), filterDefinition);
			}
			catch (Exception exception) {
				_log.error(
					"Bundle " + _bundle + " is unable to load filter " +
						filterClassName,
					exception);
			}
		}

		for (FilterDefinition filterDefinition : filterDefinitions.values()) {
			Filter filter = filterDefinition.getFilter();

			if (!_filterRegistrationImpls.containsValue(filter)) {
				addFilter(filterDefinition.getName(), filter);
			}
		}
	}

	@Override
	public void registerServlets() {
		Map<String, ServletDefinition> servletDefinitions =
			_webXMLDefinition.getServletDefinitions();

		for (ServletRegistrationImpl servletRegistrationImpl :
				_servletRegistrationImpls.values()) {

			String servletClassName = servletRegistrationImpl.getClassName();

			try {
				Servlet servlet = servletRegistrationImpl.getInstance();

				if (servlet == null) {
					String jspFile = servletRegistrationImpl.getJspFile();

					if (Validator.isNotNull(jspFile)) {
						servlet = new JspServletWrapper(
							_jspServletFactory.createJSPServlet(), jspFile);
					}
					else {
						Class<?> clazz = _bundle.loadClass(servletClassName);

						Class<? extends Servlet> servletClass =
							clazz.asSubclass(Servlet.class);

						servlet = servletClass.newInstance();
					}

					servletRegistrationImpl.setInstance(servlet);
				}

				ServletDefinition servletDefinition = new ServletDefinition();

				servletDefinition.setAsyncSupported(
					servletRegistrationImpl.isAsyncSupported());
				servletDefinition.setInitParameters(
					servletRegistrationImpl.getInitParameters());
				servletDefinition.setJSPFile(
					servletRegistrationImpl.getJspFile());
				servletDefinition.setName(servletRegistrationImpl.getName());
				servletDefinition.setServlet(servlet);
				servletDefinition.setURLPatterns(
					new ArrayList<>(servletRegistrationImpl.getMappings()));

				servletDefinitions.put(
					servletRegistrationImpl.getName(), servletDefinition);
			}
			catch (Exception exception) {
				_log.error(
					"Bundle " + _bundle + " is unable to load servlet " +
						servletClassName,
					exception);
			}
		}

		for (ServletDefinition servletDefinition :
				servletDefinitions.values()) {

			Servlet servlet = servletDefinition.getServlet();

			if (!_servletRegistrationImpls.containsValue(servlet)) {
				addServlet(servletDefinition.getName(), servlet);
			}
		}
	}

	public void setAttribute(String name, Object value) {
		if (_LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED.equals(name)) {
			File file = _bundle.getDataFile(
				_LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED);

			if (file != null) {
				byte[] data = new byte[16];

				BigEndianCodec.putLong(data, 0, _bundle.getLastModified());
				BigEndianCodec.putLong(data, 8, (Long)value);

				try {
					Files.write(
						file.toPath(), data, StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.WRITE);
				}
				catch (IOException ioException) {
					if (_log.isDebugEnabled()) {
						_log.debug(ioException);
					}
				}
			}

			return;
		}

		_servletContext.setAttribute(name, value);
	}

	public boolean setInitParameter(String name, String value)
		throws IllegalStateException, UnsupportedOperationException {

		boolean exists = _initParameters.containsKey(name);

		if (!exists && (_servletContext.getInitParameter(name) != null)) {
			exists = true;
		}

		if (!exists) {
			_initParameters.put(name, value);
		}

		return !exists;
	}

	private static Map<Method, Method> _createContextAdapterMethods() {
		Map<Method, Method> methods = new HashMap<>();

		Method[] adapterMethods =
			ModifiableServletContextAdapter.class.getDeclaredMethods();

		for (Method adapterMethod : adapterMethods) {
			String name = adapterMethod.getName();
			Class<?>[] parameterTypes = adapterMethod.getParameterTypes();

			try {
				methods.put(
					ServletContext.class.getMethod(name, parameterTypes),
					adapterMethod);
			}
			catch (NoSuchMethodException noSuchMethodException1) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchMethodException1);
				}

				try {
					methods.put(
						ModifiableServletContext.class.getMethod(
							name, parameterTypes),
						adapterMethod);
				}
				catch (NoSuchMethodException noSuchMethodException2) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchMethodException2);
					}
				}
			}
		}

		try {
			Method equalsMethod = Object.class.getMethod(
				"equals", Object.class);

			Method equalsHandlerMethod =
				ModifiableServletContextAdapter.class.getMethod(
					"equals", Object.class);

			methods.put(equalsMethod, equalsHandlerMethod);

			Method hashCodeMethod = Object.class.getMethod(
				"hashCode", (Class<?>[])null);

			Method hashCodeHandlerMethod =
				ModifiableServletContextAdapter.class.getMethod(
					"hashCode", (Class<?>[])null);

			methods.put(hashCodeMethod, hashCodeHandlerMethod);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchMethodException);
			}
		}

		return Collections.unmodifiableMap(methods);
	}

	private static final String _LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED =
		"LIFERAY_WAB_BUNDLE_RESOURCES_LAST_MODIFIED";

	private static final Log _log = LogFactoryUtil.getLog(
		ModifiableServletContextAdapter.class);

	private static final Map<Method, Method> _contextAdapterMethods;
	private static final Function<InvocationHandler, ServletContext>
		_servletContextProxyProviderFunction =
			ProxyUtil.getProxyProviderFunction(
				ModifiableServletContext.class, ServletContext.class);

	static {
		_contextAdapterMethods = _createContextAdapterMethods();
	}

	private final Bundle _bundle;
	private final BundleContext _bundleContext;
	private final Map<Class<? extends EventListener>, EventListener>
		_eventListeners = new LinkedHashMap<>();
	private final Map<String, FilterRegistrationImpl> _filterRegistrationImpls =
		new LinkedHashMap<>();
	private final Map<String, String> _initParameters = new HashMap<>();
	private final JSPServletFactory _jspServletFactory;
	private final ServletContext _servletContext;
	private final Map<String, ServletRegistrationImpl>
		_servletRegistrationImpls = new LinkedHashMap<>();
	private final WebXMLDefinition _webXMLDefinition;

}