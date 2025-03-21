/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context;

import com.liferay.osgi.util.StringPlus;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.net.URI;
import java.net.URISyntaxException;

import java.security.AccessController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.equinox.http.servlet.internal.HttpServletEndpointController;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.context.ServletContextHelperDataContext;
import org.eclipse.equinox.http.servlet.internal.customizer.ContextFilterTrackerCustomizer;
import org.eclipse.equinox.http.servlet.internal.customizer.ContextListenerTrackerCustomizer;
import org.eclipse.equinox.http.servlet.internal.customizer.ContextResourceTrackerCustomizer;
import org.eclipse.equinox.http.servlet.internal.customizer.ContextServletTrackerCustomizer;
import org.eclipse.equinox.http.servlet.internal.error.IllegalContextNameException;
import org.eclipse.equinox.http.servlet.internal.error.IllegalContextPathException;
import org.eclipse.equinox.http.servlet.internal.error.RegisteredFilterException;
import org.eclipse.equinox.http.servlet.internal.registration.EndpointRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.FilterRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.ListenerRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.ResourceRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.ServletRegistration;
import org.eclipse.equinox.http.servlet.internal.servlet.FilterConfigImpl;
import org.eclipse.equinox.http.servlet.internal.servlet.HttpSessionAdaptor;
import org.eclipse.equinox.http.servlet.internal.servlet.Match;
import org.eclipse.equinox.http.servlet.internal.servlet.ResourceServlet;
import org.eclipse.equinox.http.servlet.internal.servlet.ServletConfigImpl;
import org.eclipse.equinox.http.servlet.internal.servlet.ServletContextAdaptor;
import org.eclipse.equinox.http.servlet.internal.util.Const;
import org.eclipse.equinox.http.servlet.internal.util.EventListeners;
import org.eclipse.equinox.http.servlet.internal.util.Path;
import org.eclipse.equinox.http.servlet.internal.util.ServiceProperties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.runtime.dto.ListenerDTO;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Dante Wang
 */
public class LiferayContextController extends ContextController {

	public LiferayContextController(
		BundleContext bundleContext,
		ServiceReference<ServletContextHelper> serviceReference,
		ServletContextHelperDataContext servletContextHelperDataContext,
		HttpServletEndpointController httpServletEndpointController,
		String contextName, String contextPath) {

		Matcher matcher = _contextNamePattern.matcher(contextName);

		if (!matcher.matches()) {
			throw new IllegalContextNameException(
				"The context name '" + contextName +
					"' does not follow Bundle-SymbolicName syntax",
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
		}

		try {
			new URI(Const.HTTP, Const.LOCALHOST, contextPath, null);
		}
		catch (URISyntaxException uriSyntaxException) {
			IllegalContextPathException illegalContextPathException =
				new IllegalContextPathException(
					"The context path \"" + contextPath + "\" is invalid",
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);

			illegalContextPathException.addSuppressed(uriSyntaxException);

			throw illegalContextPathException;
		}

		_bundleContext = bundleContext;
		_serviceReference = serviceReference;
		_servletContextHelperDataContext = servletContextHelperDataContext;
		_httpServletEndpointController = httpServletEndpointController;
		_contextName = contextName;

		if (contextPath.equals(Const.SLASH)) {
			contextPath = Const.BLANK;
		}

		_contextPath = contextPath;

		_servletContextHelperServiceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		_servletContextInitParams = ServiceProperties.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX,
			servletContextHelperDataContext.getServletContext());

		_filterServiceTracker = new ServiceTracker<>(
			bundleContext, Filter.class,
			new ContextFilterTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_filterServiceTracker.open(true);

		_httpSessionAttributeListenerServiceTracker = new ServiceTracker<>(
			bundleContext, HttpSessionAttributeListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_httpSessionAttributeListenerServiceTracker.open(true);

		_httpSessionListenerServiceTracker = new ServiceTracker<>(
			bundleContext, HttpSessionListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_httpSessionListenerServiceTracker.open(true);

		_resourceServiceTracker = new ServiceTracker<>(
			bundleContext, Object.class,
			new ContextResourceTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_resourceServiceTracker.open(true);

		_servletContextAttributeListenerServiceTracker = new ServiceTracker<>(
			bundleContext, ServletContextAttributeListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_servletContextAttributeListenerServiceTracker.open(true);

		_servletContextListenerServiceTracker = new ServiceTracker<>(
			bundleContext, ServletContextListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_servletContextListenerServiceTracker.open(true);

		_servletRequestAttributeListenerServiceTracker = new ServiceTracker<>(
			bundleContext, ServletRequestAttributeListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_servletRequestAttributeListenerServiceTracker.open(true);

		_servletRequestListenerServiceTracker = new ServiceTracker<>(
			bundleContext, ServletRequestListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_servletRequestListenerServiceTracker.open(true);

		_servletServiceTracker = new ServiceTracker<>(
			bundleContext, Servlet.class,
			new ContextServletTrackerCustomizer(
				bundleContext, httpServletEndpointController, this));

		_servletServiceTracker.open(true);
	}

	@Override
	public FilterRegistration addFilterRegistration(
			ServiceReference<Filter> serviceReference)
		throws ServletException {

		_checkShutdown();

		ContextController.ServiceHolder<Filter> serviceHolder =
			new ContextController.ServiceHolder<>(
				_bundleContext.getServiceObjects(serviceReference));

		Filter filter = serviceHolder.get();

		FilterRegistration filterRegistration = null;

		boolean addedRegisteredObject = false;

		Set<Object> registeredObjects =
			_httpServletEndpointController.getRegisteredObjects();

		try {
			if (filter == null) {
				throw new IllegalArgumentException("Filter is null");
			}

			addedRegisteredObject = registeredObjects.add(filter);

			if (addedRegisteredObject) {
				for (FilterRegistration curFilterRegistration :
						_filterRegistrations) {

					if (Objects.equals(curFilterRegistration.getT(), filter)) {
						throw new RegisteredFilterException(filter);
					}
				}

				FilterDTO filterDTO = _createFilterDTO(
					serviceReference, filter);

				filterRegistration = new FilterRegistration(
					serviceHolder, filterDTO,
					GetterUtil.getInteger(
						serviceReference.getProperty(
							Constants.SERVICE_RANKING)),
					this, null);

				filterRegistration.init(
					new FilterConfigImpl(
						filterDTO.name, filterDTO.initParams,
						_createServletContextAdaptor(
							serviceHolder.getBundle(),
							_getServletContextHelper(
								serviceHolder.getBundle()))));

				_filterRegistrations.add(filterRegistration);
			}
		}
		finally {
			if (filterRegistration == null) {
				serviceHolder.release();

				if (addedRegisteredObject) {
					registeredObjects.remove(filter);
				}
			}
		}

		return filterRegistration;
	}

	@Override
	public ListenerRegistration addListenerRegistration(
		ServiceReference<EventListener> serviceReference) {

		_checkShutdown();

		ContextController.ServiceHolder<EventListener> serviceHolder =
			new ContextController.ServiceHolder<>(
				_bundleContext.getServiceObjects(serviceReference));

		EventListener eventListener = serviceHolder.get();

		ListenerRegistration listenerRegistration = null;

		try {
			if (eventListener == null) {
				throw new IllegalArgumentException("Event listener is null");
			}

			List<Class<? extends EventListener>> eventListenerClasses =
				_getEventListenerClasses(serviceReference);

			if (eventListenerClasses.isEmpty()) {
				throw new IllegalArgumentException(
					"Event listener does not implement a supported interface");
			}

			for (ListenerRegistration curListenerRegistration :
					_listenerRegistrations) {

				if (Objects.equals(
						curListenerRegistration.getT(), eventListener)) {

					return null;
				}
			}

			ServletContext servletContext = _createServletContextAdaptor(
				serviceHolder.getBundle(),
				_getServletContextHelper(serviceHolder.getBundle()));

			listenerRegistration = new ListenerRegistration(
				serviceHolder, eventListenerClasses,
				_createListenerDTO(serviceReference, eventListenerClasses),
				servletContext, this);

			if (eventListenerClasses.contains(ServletContextListener.class)) {
				ServletContextListener servletContextListener =
					(ServletContextListener)listenerRegistration.getT();

				servletContextListener.contextInitialized(
					new ServletContextEvent(servletContext));
			}

			_listenerRegistrations.add(listenerRegistration);

			_eventListeners.put(eventListenerClasses, listenerRegistration);
		}
		finally {
			if (listenerRegistration == null) {
				serviceHolder.release();
			}
		}

		return listenerRegistration;
	}

	@Override
	public ResourceRegistration addResourceRegistration(
		ServiceReference<?> serviceReference) {

		_checkShutdown();

		String resourcePrefix = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX);

		if (resourcePrefix == null) {
			throw new IllegalArgumentException("Prefix is null");
		}

		if (resourcePrefix.endsWith(Const.SLASH) &&
			!resourcePrefix.equals(Const.SLASH)) {

			throw new IllegalArgumentException(
				"Invalid prefix \"" + resourcePrefix + "\"");
		}

		String[] resourcePatterns = ArrayUtil.toStringArray(
			StringPlus.asList(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN)));

		if (resourcePatterns.length < 1) {
			throw new IllegalArgumentException("Patterns must contain a value");
		}

		for (String resourcePattern : resourcePatterns) {
			ContextController.checkPattern(resourcePattern);
		}

		Bundle bundle = serviceReference.getBundle();

		ResourceDTO resourceDTO = new ResourceDTO();

		resourceDTO.patterns = _sort(resourcePatterns);
		resourceDTO.prefix = resourcePrefix;
		resourceDTO.serviceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		resourceDTO.servletContextId = _servletContextHelperServiceId;

		ServletContextHelper servletContextHelper = _getServletContextHelper(
			bundle);

		ResourceRegistration resourceRegistration = new ResourceRegistration(
			new ContextController.ServiceHolder<>(
				new ResourceServlet(
					resourcePrefix, servletContextHelper,
					AccessController.getContext()),
				bundle, resourceDTO.serviceId,
				GetterUtil.getInteger(
					serviceReference.getProperty(Constants.SERVICE_RANKING))),
			resourceDTO, servletContextHelper, this, null);

		try {
			resourceRegistration.init(
				new ServletConfigImpl(
					resourceRegistration.getName(), new HashMap<>(),
					_createServletContextAdaptor(
						bundle, servletContextHelper)));
		}
		catch (ServletException servletException) {
			if (_log.isDebugEnabled()) {
				_log.debug(servletException);
			}

			return null;
		}

		_endpointRegistrations.add(resourceRegistration);

		return resourceRegistration;
	}

	@Override
	public ServletRegistration addServletRegistration(
			ServiceReference<Servlet> serviceReference)
		throws ServletException {

		_checkShutdown();

		ContextController.ServiceHolder<Servlet> serviceHolder =
			new ContextController.ServiceHolder<>(
				_bundleContext.getServiceObjects(serviceReference));

		Servlet servlet = serviceHolder.get();

		ServletRegistration servletRegistration = null;

		boolean addedRegisteredObject = false;

		Set<Object> registeredObjects =
			_httpServletEndpointController.getRegisteredObjects();

		try {
			if (servlet == null) {
				throw new IllegalArgumentException("Servlet is null");
			}

			addedRegisteredObject = registeredObjects.add(servlet);

			if (addedRegisteredObject) {
				ObjectValuePair<ServletDTO, ErrorPageDTO> objectValuePair =
					_createServletDTOs(serviceReference, servlet);

				ServletDTO servletDTO = objectValuePair.getKey();

				ServletContextHelper curServletContextHelper =
					_getServletContextHelper(serviceHolder.getBundle());

				servletRegistration = new ServletRegistration(
					serviceHolder, servletDTO, objectValuePair.getValue(),
					curServletContextHelper, this, null);

				servletRegistration.init(
					new ServletConfigImpl(
						servletDTO.name, servletDTO.initParams,
						_createServletContextAdaptor(
							serviceHolder.getBundle(),
							curServletContextHelper)));

				_endpointRegistrations.add(servletRegistration);
			}
		}
		finally {
			if (servletRegistration == null) {
				serviceHolder.release();

				if (addedRegisteredObject) {
					registeredObjects.remove(servlet);
				}
			}
		}

		return servletRegistration;
	}

	@Override
	public void destroy() {
		Collection<HttpSessionAdaptor> httpSessionAdaptors =
			_activeHttpSessionAdaptors.values();

		Iterator<HttpSessionAdaptor> iterator = httpSessionAdaptors.iterator();

		while (iterator.hasNext()) {
			HttpSessionAdaptor httpSessionAdaptor = iterator.next();

			httpSessionAdaptor.invalidate();

			iterator.remove();
		}

		_filterServiceTracker.close();
		_httpSessionAttributeListenerServiceTracker.close();
		_httpSessionListenerServiceTracker.close();
		_resourceServiceTracker.close();
		_servletContextAttributeListenerServiceTracker.close();
		_servletContextListenerServiceTracker.close();
		_servletRequestAttributeListenerServiceTracker.close();
		_servletRequestListenerServiceTracker.close();
		_servletServiceTracker.close();

		_endpointRegistrations.clear();
		_eventListeners.clear();
		_filterRegistrations.clear();
		_listenerRegistrations.clear();
		_servletContextHelperDataContext.destroy();

		_shutdown = true;
	}

	@Override
	public Map<String, HttpSessionAdaptor> getActiveSessions() {
		return _activeHttpSessionAdaptors;
	}

	@Override
	public String getContextName() {
		return _contextName;
	}

	@Override
	public String getContextPath() {
		return _contextPath;
	}

	@Override
	public DispatchTargets getDispatchTargets(String pathString) {
		Path path = new Path(pathString);

		String queryString = path.getQueryString();
		String requestURI = path.getRequestURI();

		DispatchTargets dispatchTargets = _getDispatchTargets(
			requestURI, null, queryString, Match.EXACT);

		if (dispatchTargets == null) {
			dispatchTargets = _getDispatchTargets(
				requestURI, path.getExtension(), queryString, Match.EXTENSION);
		}

		if (dispatchTargets == null) {
			dispatchTargets = _getDispatchTargets(
				requestURI, null, queryString, Match.REGEX);
		}

		if (dispatchTargets == null) {
			dispatchTargets = _getDispatchTargets(
				requestURI, null, queryString, Match.DEFAULT_SERVLET);
		}

		return dispatchTargets;
	}

	@Override
	public DispatchTargets getDispatchTargets(
		String servletName, String requestURI, String servletPath,
		String pathInfo, String extension, String queryString, Match match) {

		_checkShutdown();

		EndpointRegistration<?> endpointRegistration = null;

		for (EndpointRegistration<?> curEndpointRegistration :
				_endpointRegistrations) {

			if (Objects.nonNull(
					curEndpointRegistration.match(
						servletName, servletPath, pathInfo, extension,
						match))) {

				endpointRegistration = curEndpointRegistration;

				break;
			}
		}

		if (endpointRegistration == null) {
			return null;
		}

		if (match == Match.EXTENSION) {
			servletPath = servletPath + pathInfo;

			pathInfo = null;
		}

		if (_filterRegistrations.isEmpty()) {
			return new DispatchTargets(
				this, endpointRegistration, servletName, requestURI,
				servletPath, pathInfo, queryString);
		}

		if (requestURI != null) {
			int index = requestURI.lastIndexOf('.');

			if (index != -1) {
				extension = requestURI.substring(index + 1);
			}
		}

		List<FilterRegistration> matchingFilterRegistrations =
			new ArrayList<>();

		String endpointRegistrationName = endpointRegistration.getName();

		for (FilterRegistration filterRegistration : _filterRegistrations) {
			if (Objects.nonNull(
					filterRegistration.match(
						endpointRegistrationName, requestURI, extension,
						null)) &&
				!matchingFilterRegistrations.contains(filterRegistration)) {

				matchingFilterRegistrations.add(filterRegistration);
			}
		}

		return new DispatchTargets(
			this, endpointRegistration, matchingFilterRegistrations,
			servletName, requestURI, servletPath, pathInfo, queryString);
	}

	@Override
	public Set<EndpointRegistration<?>> getEndpointRegistrations() {
		return _endpointRegistrations;
	}

	@Override
	public EventListeners getEventListeners() {
		return _eventListeners;
	}

	@Override
	public Set<FilterRegistration> getFilterRegistrations() {
		return _filterRegistrations;
	}

	@Override
	public String getFullContextPath() {
		List<String> httpServiceEndpoints =
			_httpServletEndpointController.getHttpServiceEndpoints();

		if (httpServiceEndpoints.isEmpty()) {
			return _contextPath;
		}

		String defaultHttpServiceEndpoint = httpServiceEndpoints.get(0);

		if (defaultHttpServiceEndpoint.endsWith("/")) {
			defaultHttpServiceEndpoint = defaultHttpServiceEndpoint.substring(
				0, defaultHttpServiceEndpoint.length() - 1);
		}

		return defaultHttpServiceEndpoint.concat(_contextPath);
	}

	@Override
	public HttpServletEndpointController getHttpServletEndpointController() {
		return _httpServletEndpointController;
	}

	@Override
	public Map<String, String> getInitParams() {
		return _servletContextInitParams;
	}

	@Override
	public Set<ListenerRegistration> getListenerRegistrations() {
		return _listenerRegistrations;
	}

	@Override
	public HttpSessionAdaptor getSessionAdaptor(
		HttpSession httpSession, ServletContext servletContext) {

		String sessionId = httpSession.getId();

		HttpSessionAdaptor httpSessionAdaptor = _activeHttpSessionAdaptors.get(
			sessionId);

		if (httpSessionAdaptor != null) {
			return httpSessionAdaptor;
		}

		httpSessionAdaptor = HttpSessionAdaptor.createHttpSessionAdaptor(
			httpSession, servletContext, this);

		HttpSessionAdaptor previousHttpSessionAdaptor =
			_activeHttpSessionAdaptors.putIfAbsent(
				sessionId, httpSessionAdaptor);

		if (previousHttpSessionAdaptor != null) {
			return previousHttpSessionAdaptor;
		}

		List<HttpSessionListener> httpSessionListeners = _eventListeners.get(
			HttpSessionListener.class);

		if (httpSessionListeners.isEmpty()) {
			return httpSessionAdaptor;
		}

		HttpSessionEvent httpSessionEvent = new HttpSessionEvent(
			httpSessionAdaptor);

		for (HttpSessionListener httpSessionListener : httpSessionListeners) {
			httpSessionListener.sessionCreated(httpSessionEvent);
		}

		return httpSessionAdaptor;
	}

	@Override
	public boolean matches(org.osgi.framework.Filter osgiFilter) {
		return osgiFilter.match(_serviceReference);
	}

	@Override
	public boolean matches(ServiceReference<?> serviceReference) {
		String contextSelect = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT);

		if (contextSelect == null) {
			return _contextName.equals(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		}

		if (_contextName.equals(contextSelect)) {
			return true;
		}

		if (!contextSelect.startsWith(Const.OPEN_PAREN)) {
			return false;
		}

		try {
			return matches(FrameworkUtil.createFilter(contextSelect));
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new IllegalArgumentException(invalidSyntaxException);
		}
	}

	@Override
	public void removeActiveSession(String sessionId) {
		_activeHttpSessionAdaptors.remove(sessionId);
	}

	@Override
	public void ungetServletContextHelper(Bundle bundle) {
		BundleContext bundleContext = bundle.getBundleContext();

		try {
			bundleContext.ungetService(_serviceReference);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}
		}
	}

	private void _checkShutdown() {
		if (_shutdown) {
			throw new IllegalStateException("Context is shutdown");
		}
	}

	private FilterDTO _createFilterDTO(
		ServiceReference<Filter> filterServiceReference, Filter filter) {

		String[] filterPatterns = ArrayUtil.toStringArray(
			StringPlus.asList(
				filterServiceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN)));
		String[] filterRegexes = ArrayUtil.toStringArray(
			StringPlus.asList(
				filterServiceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX)));
		String[] filterServletNames = ArrayUtil.toStringArray(
			StringPlus.asList(
				filterServiceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET)));

		if ((filterPatterns.length == 0) && (filterRegexes.length == 0) &&
			(filterServletNames.length == 0)) {

			throw new IllegalArgumentException(
				"Patterns, regex, and servlet names must contain a value");
		}

		for (String filterPattern : filterPatterns) {
			ContextController.checkPattern(filterPattern);
		}

		String[] filterDispatcherTypes = ArrayUtil.toStringArray(
			StringPlus.asList(
				filterServiceReference.getProperty(
					HttpWhiteboardConstants.
						HTTP_WHITEBOARD_FILTER_DISPATCHER)));

		if (filterDispatcherTypes.length == 0) {
			filterDispatcherTypes = _DISPATCHER_TYPES;
		}

		for (String filterDispatcherType : filterDispatcherTypes) {
			try {
				DispatcherType.valueOf(filterDispatcherType);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				throw new IllegalArgumentException(
					"Invalid dispatcher \"" + filterDispatcherType + "\"",
					illegalArgumentException);
			}
		}

		FilterDTO filterDTO = new FilterDTO();

		filterDTO.asyncSupported = ServiceProperties.parseBoolean(
			filterServiceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED);
		filterDTO.dispatcher = _sort(filterDispatcherTypes);
		filterDTO.initParams = ServiceProperties.parseInitParams(
			filterServiceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX);

		Class<?> filterClass = filter.getClass();

		filterDTO.name = GetterUtil.getString(
			ServiceProperties.parseName(
				filterServiceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME),
				filter),
			filterClass.getName());

		filterDTO.patterns = _sort(filterPatterns);
		filterDTO.regexs = filterRegexes;
		filterDTO.serviceId = (long)filterServiceReference.getProperty(
			Constants.SERVICE_ID);
		filterDTO.servletContextId = _servletContextHelperServiceId;
		filterDTO.servletNames = _sort(filterServletNames);

		return filterDTO;
	}

	private ListenerDTO _createListenerDTO(
		ServiceReference<EventListener> serviceReference,
		List<Class<? extends EventListener>> eventListenerClasses) {

		ListenerDTO listenerDTO = new ListenerDTO();

		listenerDTO.serviceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		listenerDTO.servletContextId = _servletContextHelperServiceId;
		listenerDTO.types = TransformUtil.transformToArray(
			eventListenerClasses, Class::getName, String.class);

		return listenerDTO;
	}

	private ServletContext _createServletContextAdaptor(
		Bundle bundle, ServletContextHelper servletContextHelper) {

		ServletContextAdaptor servletContextAdaptor = new ServletContextAdaptor(
			this, bundle, servletContextHelper,
			_servletContextHelperDataContext, _eventListeners,
			AccessController.getContext()) {

			public JspConfigDescriptor getJspConfigDescriptor() {
				return null;
			}

		};

		return servletContextAdaptor.createServletContext();
	}

	private ObjectValuePair<ServletDTO, ErrorPageDTO> _createServletDTOs(
		ServiceReference<Servlet> serviceReference, Servlet servlet) {

		String[] servletErrorPages = ArrayUtil.toStringArray(
			StringPlus.asList(
				serviceReference.getProperty(
					HttpWhiteboardConstants.
						HTTP_WHITEBOARD_SERVLET_ERROR_PAGE)));
		String servletName = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME);
		String[] servletPatterns = ArrayUtil.toStringArray(
			StringPlus.asList(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN)));

		if ((servletErrorPages.length == 0) && (servletName == null) &&
			(servletPatterns.length == 0)) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"One of the service properties ",
					HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE,
					", ", HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
					", and ",
					HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
					" must contain a value"));
		}

		for (String servletPattern : servletPatterns) {
			ContextController.checkPattern(servletPattern);
		}

		ServletDTO servletDTO = new ServletDTO();

		servletDTO.asyncSupported = ServiceProperties.parseBoolean(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED);
		servletDTO.initParams = ServiceProperties.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX);
		servletDTO.name = ServiceProperties.parseName(servletName, servlet);
		servletDTO.patterns = _sort(servletPatterns);
		servletDTO.serviceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		servletDTO.servletContextId = _servletContextHelperServiceId;
		servletDTO.servletInfo = servlet.getServletInfo();

		ErrorPageDTO errorPageDTO = null;

		if (servletErrorPages.length > 0) {
			errorPageDTO = new ErrorPageDTO();

			errorPageDTO.asyncSupported = servletDTO.asyncSupported;

			Set<Long> httpErrorCodes = new LinkedHashSet<>();

			List<String> exceptionErrorPages = new ArrayList<>();

			for (String servletErrorPage : servletErrorPages) {
				try {
					if (Objects.equals(servletErrorPage, "4xx")) {
						for (long code = 400; code < 500; code++) {
							httpErrorCodes.add(code);
						}
					}
					else if (Objects.equals(servletErrorPage, "5xx")) {
						for (long code = 500; code < 600; code++) {
							httpErrorCodes.add(code);
						}
					}
					else {
						httpErrorCodes.add(Long.parseLong(servletErrorPage));
					}
				}
				catch (NumberFormatException numberFormatException) {
					if (_log.isDebugEnabled()) {
						_log.debug(numberFormatException);
					}

					exceptionErrorPages.add(servletErrorPage);
				}
			}

			errorPageDTO.errorCodes = TransformUtil.transformToLongArray(
				httpErrorCodes, errorCode -> errorCode);
			errorPageDTO.exceptions = exceptionErrorPages.toArray(
				new String[0]);
			errorPageDTO.initParams = servletDTO.initParams;
			errorPageDTO.name = servletDTO.name;
			errorPageDTO.serviceId = servletDTO.serviceId;
			errorPageDTO.servletContextId = _servletContextHelperServiceId;
			errorPageDTO.servletInfo = servlet.getServletInfo();
		}

		return new ObjectValuePair<>(servletDTO, errorPageDTO);
	}

	private DispatchTargets _getDispatchTargets(
		String requestURI, String extension, String queryString, Match match) {

		int index = requestURI.lastIndexOf('/');

		String servletPath = requestURI;

		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = Const.SLASH;
		}

		while (true) {
			DispatchTargets dispatchTargets = getDispatchTargets(
				null, requestURI, servletPath, pathInfo, extension, queryString,
				match);

			if (dispatchTargets != null) {
				return dispatchTargets;
			}

			if ((match == Match.EXACT) || (index == -1)) {
				break;
			}

			servletPath = requestURI.substring(0, index);

			pathInfo = requestURI.substring(index);

			index = servletPath.lastIndexOf('/');
		}

		return null;
	}

	private List<Class<? extends EventListener>> _getEventListenerClasses(
		ServiceReference<EventListener> serviceReference) {

		List<Class<? extends EventListener>> eventListenerClasses =
			new ArrayList<>();

		List<String> objectClasses = StringPlus.asList(
			serviceReference.getProperty(Constants.OBJECTCLASS));

		if (objectClasses.contains(
				HttpSessionAttributeListener.class.getName())) {

			eventListenerClasses.add(HttpSessionAttributeListener.class);
		}

		if (objectClasses.contains(HttpSessionListener.class.getName())) {
			eventListenerClasses.add(HttpSessionListener.class);
		}

		if (objectClasses.contains(
				ServletContextAttributeListener.class.getName())) {

			eventListenerClasses.add(ServletContextAttributeListener.class);
		}

		if (objectClasses.contains(ServletContextListener.class.getName())) {
			eventListenerClasses.add(ServletContextListener.class);
		}

		if (objectClasses.contains(
				ServletRequestAttributeListener.class.getName())) {

			eventListenerClasses.add(ServletRequestAttributeListener.class);
		}

		if (objectClasses.contains(ServletRequestListener.class.getName())) {
			eventListenerClasses.add(ServletRequestListener.class);
		}

		return eventListenerClasses;
	}

	private ServletContextHelper _getServletContextHelper(Bundle bundle) {
		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.getService(_serviceReference);
	}

	private String[] _sort(String[] values) {
		if (values == null) {
			return null;
		}

		Arrays.sort(values);

		return values;
	}

	private static final String[] _DISPATCHER_TYPES = {
		DispatcherType.REQUEST.toString()
	};

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayContextController.class.getName());

	private static final Pattern _contextNamePattern = Pattern.compile(
		"^([a-zA-Z_0-9\\-]+\\.)*[a-zA-Z_0-9\\-]+$");

	private final ConcurrentMap<String, HttpSessionAdaptor>
		_activeHttpSessionAdaptors = new ConcurrentHashMap<>();
	private final BundleContext _bundleContext;
	private final String _contextName;
	private final String _contextPath;
	private final Set<EndpointRegistration<?>> _endpointRegistrations =
		new ConcurrentSkipListSet<>();
	private final EventListeners _eventListeners = new EventListeners();
	private final Set<FilterRegistration> _filterRegistrations =
		new ConcurrentSkipListSet<>();
	private final ServiceTracker<Filter, AtomicReference<FilterRegistration>>
		_filterServiceTracker;
	private final HttpServletEndpointController _httpServletEndpointController;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_httpSessionAttributeListenerServiceTracker;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_httpSessionListenerServiceTracker;
	private final Set<ListenerRegistration> _listenerRegistrations =
		new HashSet<>();
	private final ServiceTracker<Object, AtomicReference<ResourceRegistration>>
		_resourceServiceTracker;
	private final ServiceReference<ServletContextHelper> _serviceReference;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_servletContextAttributeListenerServiceTracker;
	private final ServletContextHelperDataContext
		_servletContextHelperDataContext;
	private final long _servletContextHelperServiceId;
	private final Map<String, String> _servletContextInitParams;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_servletContextListenerServiceTracker;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_servletRequestAttributeListenerServiceTracker;
	private final ServiceTracker
		<EventListener, AtomicReference<ListenerRegistration>>
			_servletRequestListenerServiceTracker;
	private final ServiceTracker<Servlet, AtomicReference<ServletRegistration>>
		_servletServiceTracker;
	private boolean _shutdown;

}