/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Plugin;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.osgi.web.servlet.JSPServletFactory;
import com.liferay.portal.osgi.web.servlet.context.helper.ServletContextHelperRegistration;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.FilterDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.ListenerDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.ServletDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebXMLDefinition;
import com.liferay.portal.osgi.web.wab.extender.internal.adapter.AsyncAttributeAdapterServlet;
import com.liferay.portal.osgi.web.wab.extender.internal.adapter.FilterExceptionAdapter;
import com.liferay.portal.osgi.web.wab.extender.internal.adapter.ModifiableServletContext;
import com.liferay.portal.osgi.web.wab.extender.internal.adapter.ModifiableServletContextAdapter;
import com.liferay.portal.osgi.web.wab.extender.internal.adapter.ServletContextListenerExceptionAdapter;
import com.liferay.portal.osgi.web.wab.extender.internal.adapter.ServletExceptionAdapter;
import com.liferay.portal.osgi.web.wab.extender.internal.registration.FilterRegistrationImpl;
import com.liferay.portal.osgi.web.wab.extender.internal.registration.ListenerServiceRegistrationComparator;
import com.liferay.portal.osgi.web.wab.extender.internal.registration.ServletRegistrationImpl;
import com.liferay.portal.plugin.PluginPackageUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.HandlesTypes;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class WabBundleProcessor {

	public WabBundleProcessor(
		Bundle bundle, JSPServletFactory jspServletFactory) {

		_bundle = bundle;
		_jspServletFactory = jspServletFactory;

		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		_bundleClassLoader = bundleWiring.getClassLoader();

		_bundleContext = _bundle.getBundleContext();
	}

	public void destroy() throws Exception {
		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_bundleClassLoader)) {

			_destroyServlets();

			_destroyFilters();

			_destroyListeners();

			_bundleContext.ungetService(
				_servletContextHelperRegistrationServiceReference);
		}
	}

	public void init(Dictionary<String, Object> properties) throws Exception {
		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_bundleClassLoader)) {

			ServletContextHelperRegistration servletContextHelperRegistration =
				_initContext();

			boolean wabShapedBundle =
				servletContextHelperRegistration.isWabShapedBundle();

			if (!wabShapedBundle) {
				return;
			}

			WebXMLDefinition webXMLDefinition =
				servletContextHelperRegistration.getWebXMLDefinition();

			Exception exception = webXMLDefinition.getException();

			if (exception != null) {
				throw exception;
			}

			ServletContext servletContext =
				ModifiableServletContextAdapter.createInstance(
					_bundle.getBundleContext(),
					servletContextHelperRegistration.getServletContext(),
					_jspServletFactory, webXMLDefinition);

			Set<Class<?>> allClasses =
				servletContextHelperRegistration.getClasses();

			Set<Class<?>> annotatedClasses =
				servletContextHelperRegistration.getAnnotatedClasses();

			_initServletContainerInitializers(
				_bundle, servletContext, allClasses, annotatedClasses);

			_saveScannedAnnotatedClasses(annotatedClasses);

			ModifiableServletContext modifiableServletContext =
				(ModifiableServletContext)servletContext;

			Map<String, String> unregisteredInitParameters =
				modifiableServletContext.getUnregisteredInitParameters();

			if ((unregisteredInitParameters != null) &&
				!unregisteredInitParameters.isEmpty()) {

				Map<String, Object> attributes = new HashMap<>();

				Enumeration<String> enumeration =
					servletContext.getAttributeNames();

				while (enumeration.hasMoreElements()) {
					String attributeName = enumeration.nextElement();

					attributes.put(
						attributeName,
						servletContext.getAttribute(attributeName));
				}

				List<ListenerDefinition> listenerDefinitions =
					modifiableServletContext.getListenerDefinitions();
				Map<String, FilterRegistrationImpl> filterRegistrationImpls =
					modifiableServletContext.getFilterRegistrationImpls();
				Map<String, ServletRegistrationImpl> servletRegistrationImpls =
					modifiableServletContext.getServletRegistrationImpls();

				servletContextHelperRegistration.setProperties(
					unregisteredInitParameters);

				ServletContext newServletContext =
					servletContextHelperRegistration.getServletContext();

				servletContext = ModifiableServletContextAdapter.createInstance(
					_bundle.getBundleContext(), newServletContext,
					_jspServletFactory, webXMLDefinition, listenerDefinitions,
					filterRegistrationImpls, servletRegistrationImpls,
					attributes);

				modifiableServletContext =
					(ModifiableServletContext)servletContext;
			}

			_scanTLDsForListeners(webXMLDefinition, servletContext);

			Set<ListenerDefinition> listenerDefinitions = new LinkedHashSet<>();

			listenerDefinitions.addAll(
				modifiableServletContext.getListenerDefinitions());
			listenerDefinitions.addAll(
				webXMLDefinition.getListenerDefinitions());

			_initListeners(listenerDefinitions, servletContext);

			modifiableServletContext.registerFilters();

			_initFilters(webXMLDefinition.getFilterDefinitions());

			modifiableServletContext.registerServlets();

			_initServlets(
				webXMLDefinition.getServletDefinitions(),
				modifiableServletContext);
		}
		catch (Exception exception) {
			_log.error(
				"Catastrophic initialization failure! Shutting down " +
					_contextName + " WAB due to: " + exception.getMessage(),
				exception);

			destroy();

			throw exception;
		}
	}

	private void _collectAnnotatedClasses(
		Class<?> annotatedClass, Class<?>[] handlesTypesClasses,
		Set<Class<?>> annotationHandlesTypesClasses,
		Set<Class<?>> annotatedClasses) {

		// Class extends/implements

		for (Class<?> handlesTypesClass : handlesTypesClasses) {
			if (handlesTypesClass.isAssignableFrom(annotatedClass) &&
				!Modifier.isAbstract(annotatedClass.getModifiers())) {

				annotatedClasses.add(annotatedClass);

				return;
			}
		}

		if (annotationHandlesTypesClasses == null) {
			return;
		}

		// Class annotation

		Annotation[] classAnnotations = new Annotation[0];

		try {
			classAnnotations = annotatedClass.getAnnotations();
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable.getMessage());
			}
		}

		for (Annotation classAnnotation : classAnnotations) {
			if (annotationHandlesTypesClasses.contains(
					classAnnotation.annotationType())) {

				annotatedClasses.add(annotatedClass);

				return;
			}
		}

		// Method annotation

		Method[] classMethods = new Method[0];

		try {
			classMethods = annotatedClass.getDeclaredMethods();
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable.getMessage());
			}
		}

		for (Method method : classMethods) {
			Annotation[] methodAnnotations = new Annotation[0];

			try {
				methodAnnotations = method.getDeclaredAnnotations();
			}
			catch (Throwable throwable) {
				if (_log.isDebugEnabled()) {
					_log.debug(throwable.getMessage());
				}
			}

			for (Annotation methodAnnotation : methodAnnotations) {
				if (annotationHandlesTypesClasses.contains(
						methodAnnotation.annotationType())) {

					annotatedClasses.add(annotatedClass);

					return;
				}
			}
		}

		// Field annotation

		Field[] declaredFields = new Field[0];

		try {
			declaredFields = annotatedClass.getDeclaredFields();
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable.getMessage());
			}
		}

		for (Field field : declaredFields) {
			Annotation[] fieldAnnotations = new Annotation[0];

			try {
				fieldAnnotations = field.getDeclaredAnnotations();
			}
			catch (Throwable throwable) {
				if (_log.isDebugEnabled()) {
					_log.debug(throwable.getMessage());
				}
			}

			for (Annotation fieldAnnotation : fieldAnnotations) {
				if (annotationHandlesTypesClasses.contains(
						fieldAnnotation.annotationType())) {

					annotatedClasses.add(annotatedClass);

					return;
				}
			}
		}
	}

	private void _destroyFilters() {
		for (ServiceRegistration<?> serviceRegistration :
				_filterServiceRegistrations) {

			try {
				serviceRegistration.unregister();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		_filterServiceRegistrations.clear();
	}

	private void _destroyListeners() {
		for (ServiceRegistration<?> serviceRegistration :
				_listenerServiceRegistrations) {

			try {
				serviceRegistration.unregister();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		_listenerServiceRegistrations.clear();
	}

	private void _destroyServlets() {
		for (ServiceRegistration<?> serviceRegistration :
				_servletServiceRegistrations) {

			try {
				serviceRegistration.unregister();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		_servletServiceRegistrations.clear();
	}

	private String[] _getClassNames(EventListener eventListener) {
		List<String> classNamesList = new ArrayList<>();

		if (HttpSessionAttributeListener.class.isInstance(eventListener)) {
			classNamesList.add(HttpSessionAttributeListener.class.getName());
		}

		if (HttpSessionListener.class.isInstance(eventListener)) {
			classNamesList.add(HttpSessionListener.class.getName());
		}

		if (ServletContextAttributeListener.class.isInstance(eventListener)) {
			classNamesList.add(ServletContextAttributeListener.class.getName());
		}

		// The following supported listener is omitted on purpose because it is
		// registered individually.

		/*if (ServletContextListener.class.isInstance(eventListener)) {
			classNamesList.add(ServletContextListener.class.getName());
		}*/

		if (ServletRequestAttributeListener.class.isInstance(eventListener)) {
			classNamesList.add(ServletRequestAttributeListener.class.getName());
		}

		if (ServletRequestListener.class.isInstance(eventListener)) {
			classNamesList.add(ServletRequestListener.class.getName());
		}

		return classNamesList.toArray(new String[0]);
	}

	private ServletContextHelperRegistration _initContext() {
		_servletContextHelperRegistrationServiceReference =
			_bundleContext.getServiceReference(
				ServletContextHelperRegistration.class);

		ServletContextHelperRegistration servletContextHelperRegistration =
			_bundleContext.getService(
				_servletContextHelperRegistrationServiceReference);

		WebXMLDefinition webXMLDefinition =
			servletContextHelperRegistration.getWebXMLDefinition();

		ServletContext servletContext =
			servletContextHelperRegistration.getServletContext();

		_contextName = servletContext.getServletContextName();

		servletContext.setAttribute(
			"jsp.taglib.mappings", webXMLDefinition.getJspTaglibMappings());
		servletContext.setAttribute("osgi-bundlecontext", _bundleContext);
		servletContext.setAttribute("osgi-runtime-vendor", _VENDOR);

		return servletContextHelperRegistration;
	}

	private void _initFilters(Map<String, FilterDefinition> filterDefinitions)
		throws Exception {

		for (Map.Entry<String, FilterDefinition> entry :
				filterDefinitions.entrySet()) {

			FilterDefinition filterDefinition = entry.getValue();

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				_contextName);
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED,
				filterDefinition.isAsyncSupported());
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER,
				filterDefinition.getDispatchers());
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME,
				filterDefinition.getName());
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN,
				filterDefinition.getURLPatterns());
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET,
				filterDefinition.getServletNames());
			properties.put(
				Constants.SERVICE_RANKING, filterDefinition.getPriority());

			Map<String, String> initParameters =
				filterDefinition.getInitParameters();

			for (Map.Entry<String, String> initParametersEntry :
					initParameters.entrySet()) {

				String key = initParametersEntry.getKey();

				properties.put(
					HttpWhiteboardConstants.
						HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX + key,
					initParametersEntry.getValue());
			}

			FilterExceptionAdapter filterExceptionAdaptor =
				new FilterExceptionAdapter(filterDefinition.getFilter());

			ServiceRegistration<Filter> serviceRegistration =
				_bundleContext.registerService(
					Filter.class, filterExceptionAdaptor, properties);

			Exception exception = filterExceptionAdaptor.getException();

			if (exception != null) {
				serviceRegistration.unregister();

				throw exception;
			}

			_filterServiceRegistrations.add(serviceRegistration);
		}
	}

	private void _initListeners(
			Collection<ListenerDefinition> listenerDefinitions,
			ServletContext servletContext)
		throws Exception {

		boolean registeredPortletContextLoaderListener = false;

		PluginPackage pluginPackage =
			PluginPackageUtil.readPluginPackageServletContext(servletContext);

		if (_themeTypes.equals(pluginPackage.getTypes())) {
			registeredPortletContextLoaderListener = true;
		}

		for (ListenerDefinition listenerDefinition : listenerDefinitions) {
			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				_contextName);
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER,
				Boolean.TRUE.toString());

			String[] classNames = _getClassNames(
				listenerDefinition.getEventListener());

			if (classNames.length > 0) {
				ServiceRegistration<?> serviceRegistration =
					_bundleContext.registerService(
						classNames, listenerDefinition.getEventListener(),
						properties);

				_listenerServiceRegistrations.add(serviceRegistration);
			}

			if (!ServletContextListener.class.isInstance(
					listenerDefinition.getEventListener())) {

				continue;
			}

			if (!registeredPortletContextLoaderListener) {
				PortletContextLoaderListener portletContextLoaderListener =
					new PortletContextLoaderListener(_bundleContext);

				ServletContextListenerExceptionAdapter
					servletContextListenerExceptionAdaptor =
						new ServletContextListenerExceptionAdapter(
							portletContextLoaderListener, servletContext);

				ServiceRegistration<?> serviceRegistration =
					_bundleContext.registerService(
						ServletContextListener.class,
						servletContextListenerExceptionAdaptor, properties);

				Exception exception =
					servletContextListenerExceptionAdaptor.getException();

				List<ServiceRegistration<?>> contextServiceRegistrations =
					portletContextLoaderListener.getServiceRegistrations();

				if (exception != null) {
					for (ServiceRegistration<?> contextServiceRegistration :
							contextServiceRegistrations) {

						contextServiceRegistration.unregister();
					}

					serviceRegistration.unregister();

					throw exception;
				}

				_listenerServiceRegistrations.add(serviceRegistration);

				_listenerServiceRegistrations.addAll(
					contextServiceRegistrations);

				registeredPortletContextLoaderListener = true;
			}

			ServletContextListenerExceptionAdapter
				servletContextListenerExceptionAdaptor =
					new ServletContextListenerExceptionAdapter(
						(ServletContextListener)
							listenerDefinition.getEventListener(),
						servletContext);

			ServiceRegistration<?> serviceRegistration =
				_bundleContext.registerService(
					ServletContextListener.class,
					servletContextListenerExceptionAdaptor, properties);

			Exception exception =
				servletContextListenerExceptionAdaptor.getException();

			if (exception != null) {
				serviceRegistration.unregister();

				throw exception;
			}

			_listenerServiceRegistrations.add(serviceRegistration);
		}
	}

	private void _initServletContainerInitializers(
			Bundle bundle, ServletContext servletContext, Set<Class<?>> classes,
			Set<Class<?>> annotatedClasses)
		throws IOException {

		Enumeration<URL> enumeration = bundle.getResources(
			"META-INF/services/jakarta.servlet.ServletContainerInitializer");

		if (enumeration == null) {
			return;
		}

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			try (InputStream inputStream = url.openStream()) {
				Collection<String> fqcns = new ArrayList<>();

				StringUtil.readLines(inputStream, fqcns);

				for (String fqcn : fqcns) {
					int index = fqcn.indexOf(StringPool.POUND);

					if (index == 0) {
						continue;
					}

					if (index > 0) {
						fqcn = fqcn.substring(0, index);
					}

					fqcn = fqcn.trim();

					if (Validator.isNotNull(fqcn)) {
						_processServletContainerInitializerClass(
							fqcn, bundle, servletContext, classes,
							annotatedClasses);
					}
				}
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}
		}
	}

	private void _initServlets(
			Map<String, ServletDefinition> servletDefinitions,
			ModifiableServletContext modifiableServletContext)
		throws Exception {

		for (Map.Entry<String, ServletDefinition> entry :
				servletDefinitions.entrySet()) {

			ServletDefinition servletDefinition = entry.getValue();

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				_contextName);
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED,
				servletDefinition.isAsyncSupported());
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE,
				servletDefinition.getErrorPages());
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
				servletDefinition.getName());

			String jspFile = servletDefinition.getJspFile();
			List<String> urlPatterns = servletDefinition.getURLPatterns();

			if (urlPatterns.isEmpty() && (jspFile != null)) {
				urlPatterns.add(jspFile);
			}

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
				urlPatterns);

			Map<String, String> initParameters =
				servletDefinition.getInitParameters();

			for (Map.Entry<String, String> initParametersEntry :
					initParameters.entrySet()) {

				String key = initParametersEntry.getKey();

				properties.put(
					HttpWhiteboardConstants.
						HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + key,
					initParametersEntry.getValue());
			}

			ServletExceptionAdapter servletExceptionAdaptor =
				new ServletExceptionAdapter(
					new AsyncAttributeAdapterServlet(
						servletDefinition.getServlet()),
					modifiableServletContext);

			ServiceRegistration<Servlet> serviceRegistration =
				_bundleContext.registerService(
					Servlet.class, servletExceptionAdaptor, properties);

			Exception exception = servletExceptionAdaptor.getException();

			if (exception != null) {
				serviceRegistration.unregister();

				throw exception;
			}

			_servletServiceRegistrations.add(serviceRegistration);
		}
	}

	private void _processServletContainerInitializerClass(
		String fqcn, Bundle bundle, ServletContext servletContext,
		Set<Class<?>> classes, Set<Class<?>> annotatedClasses) {

		Class<? extends ServletContainerInitializer> initializerClass = null;

		try {
			Class<?> clazz = bundle.loadClass(fqcn);

			if (!ServletContainerInitializer.class.isAssignableFrom(clazz)) {
				return;
			}

			initializerClass = clazz.asSubclass(
				ServletContainerInitializer.class);
		}
		catch (Exception exception) {
			_log.error(exception);

			return;
		}

		HandlesTypes handlesTypes = initializerClass.getAnnotation(
			HandlesTypes.class);

		Set<Class<?>> localAnnotatedClasses = null;

		if (handlesTypes != null) {
			Class<?>[] handlesTypesClasses = handlesTypes.value();

			if (handlesTypesClasses != null) {
				Set<Class<?>> annotationHandlesTypesClasses = null;

				for (Class<?> handlesTypesClass : handlesTypesClasses) {
					if (handlesTypesClass.isAnnotation()) {
						if (annotationHandlesTypesClasses == null) {
							annotationHandlesTypesClasses = new HashSet<>();
						}

						annotationHandlesTypesClasses.add(handlesTypesClass);
					}
				}

				localAnnotatedClasses = new HashSet<>();

				for (Class<?> clazz : classes) {
					_collectAnnotatedClasses(
						clazz, handlesTypesClasses,
						annotationHandlesTypesClasses, localAnnotatedClasses);
				}

				if (localAnnotatedClasses.isEmpty()) {
					localAnnotatedClasses = null;
				}
				else {
					annotatedClasses.addAll(localAnnotatedClasses);
				}
			}
		}

		try {
			ServletContainerInitializer servletContainerInitializer =
				initializerClass.newInstance();

			servletContainerInitializer.onStartup(
				localAnnotatedClasses, servletContext);
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	private void _saveScannedAnnotatedClasses(Set<Class<?>> annotatedClasses) {
		File annotatedClassesFile = _bundle.getDataFile("annotated.classes");

		try (OutputStream outputStream = new FileOutputStream(
				annotatedClassesFile);
			PrintWriter printWriter = new PrintWriter(outputStream)) {

			printWriter.println("last.modified=" + _bundle.getLastModified());

			if (annotatedClasses.isEmpty()) {
				printWriter.println("annotated.classes=");
			}
			else {
				StringBundler sb = new StringBundler(
					(annotatedClasses.size() * 2) + 1);

				sb.append("annotated.classes=");

				for (Class<?> clazz : annotatedClasses) {
					sb.append(clazz.getName());
					sb.append(StringPool.COMMA);
				}

				sb.setIndex(sb.index() - 1);

				printWriter.println(sb.toString());
			}
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}
	}

	private void _scanTLDsForListeners(
		WebXMLDefinition webXMLDefinition, ServletContext servletContext) {

		List<String> listenerClassNames = new ArrayList<>();

		JSPTaglibHelperUtil.scanTLDs(
			_bundle, servletContext, listenerClassNames);

		for (String listenerClassName : listenerClassNames) {
			try {
				Class<?> clazz = _bundle.loadClass(listenerClassName);

				Class<? extends EventListener> eventListenerClass =
					clazz.asSubclass(EventListener.class);

				EventListener eventListener = eventListenerClass.newInstance();

				ListenerDefinition listenerDefinition =
					new ListenerDefinition();

				listenerDefinition.setEventListener(eventListener);

				webXMLDefinition.addListenerDefinition(listenerDefinition);
			}
			catch (Exception exception) {
				_log.error(
					"Bundle " + _bundle + " is unable to load listener " +
						listenerClassName,
					exception);
			}
		}
	}

	private static final String _VENDOR = "Liferay, Inc.";

	private static final Log _log = LogFactoryUtil.getLog(
		WabBundleProcessor.class);

	private static final List<String> _themeTypes = Arrays.asList(
		Plugin.TYPE_THEME);

	private final Bundle _bundle;
	private final ClassLoader _bundleClassLoader;
	private final BundleContext _bundleContext;
	private String _contextName;
	private final Set<ServiceRegistration<Filter>> _filterServiceRegistrations =
		new ConcurrentSkipListSet<>();
	private final JSPServletFactory _jspServletFactory;
	private final Set<ServiceRegistration<?>> _listenerServiceRegistrations =
		new ConcurrentSkipListSet<>(
			new ListenerServiceRegistrationComparator());
	private ServiceReference<ServletContextHelperRegistration>
		_servletContextHelperRegistrationServiceReference;
	private final Set<ServiceRegistration<Servlet>>
		_servletServiceRegistrations = new ConcurrentSkipListSet<>();

}