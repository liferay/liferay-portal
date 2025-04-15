/*******************************************************************************
 * Copyright (c) 2016 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package org.eclipse.equinox.http.servlet.internal.context;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.Filter;
import javax.servlet.http.*;
import org.eclipse.equinox.http.servlet.internal.HttpServletEndpointController;
import org.eclipse.equinox.http.servlet.internal.customizer.*;
import org.eclipse.equinox.http.servlet.internal.error.*;
import org.eclipse.equinox.http.servlet.internal.registration.*;
import org.eclipse.equinox.http.servlet.internal.registration.FilterRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.ServletRegistration;
import org.eclipse.equinox.http.servlet.internal.servlet.*;
import org.eclipse.equinox.http.servlet.internal.util.*;
import org.osgi.framework.*;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.*;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
public class ContextController {

	public static final class ServiceHolder<S> implements Comparable<ServiceHolder<?>> {
		final ServiceObjects<S> serviceObjects;
		final S service;
		final Bundle bundle;
		final long serviceId;
		final int serviceRanking;
		public ServiceHolder(ServiceObjects<S> serviceObjects) {
			this.serviceObjects = serviceObjects;
			this.bundle = serviceObjects.getServiceReference().getBundle();
			this.service = serviceObjects.getService();
			this.serviceId = (Long) serviceObjects.getServiceReference().getProperty(Constants.SERVICE_ID);
			Integer rankProp = (Integer) serviceObjects.getServiceReference().getProperty(Constants.SERVICE_RANKING);
			this.serviceRanking = rankProp == null ? 0 : rankProp.intValue();
		}
		public ServiceHolder(S service, Bundle bundle, long serviceId, int serviceRanking) {
			this.service = service;
			this.bundle = bundle;
			this.serviceObjects = null;
			this.serviceId = serviceId;
			this.serviceRanking = serviceRanking;
		}
		public S get() {
			return service;
		}

		public Bundle getBundle() {
			return bundle;
		}
		public void release() {
			if (serviceObjects != null && service != null) {
				try {
					serviceObjects.ungetService(service);
				} catch (IllegalStateException e) {
					// this can happen if the whiteboard bundle is in the process of stopping
					// and the framework is in the middle of auto-unregistering any services
					// the bundle forgot to unregister on stop
				}
			}
		}

		public ServiceReference<S> getServiceReference() {
			return serviceObjects == null ? null : serviceObjects.getServiceReference();
		}
		@Override
		public int compareTo(ServiceHolder<?> o) {
			final int thisRanking = serviceRanking;
			final int otherRanking = o.serviceRanking;
			if (thisRanking != otherRanking) {
				if (thisRanking < otherRanking) {
					return 1;
				}
				return -1;
			}
			final long thisId = this.serviceId;
			final long otherId = o.serviceId;
			if (thisId == otherId) {
				return 0;
			}
			if (thisId < otherId) {
				return -1;
			}
			return 1;
		}
	}

	public ContextController() {
	}

	public ContextController(
		BundleContext trackingContextParam, BundleContext consumingContext,
		ServiceReference<ServletContextHelper> servletContextHelperRef,
		ServletContextHelperDataContext servletContextHelperDataContext,
		HttpServletEndpointController httpServletEndpointController,
		String contextName, String contextPath) {

		validate(contextName, contextPath);

		listenerRegistrations = new HashSet<>();
		endpointRegistrations = new ConcurrentSkipListSet<>();
		eventListeners = new EventListeners();
		filterRegistrations = new ConcurrentSkipListSet<>();
		activeSessions = new ConcurrentHashMap<>();

		this.servletContextHelperRef = servletContextHelperRef;

		long serviceId = (Long)servletContextHelperRef.getProperty(Constants.SERVICE_ID);

		this.servletContextHelperDataContext = servletContextHelperDataContext;
		this.httpServletEndpointController = httpServletEndpointController;
		this.contextName = contextName;

		if (contextPath.equals(Const.SLASH)) {
			contextPath = Const.BLANK;
		}

		this.contextPath = contextPath;
		this.contextServiceId = serviceId;

		this.initParams = ServiceProperties.parseInitParams(
			servletContextHelperRef, HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX, servletContextHelperDataContext.getServletContext());

		this.trackingContext = trackingContextParam;
		this.consumingContext = consumingContext;

		servletContextListenerServiceTracker = new ServiceTracker<EventListener, AtomicReference<ListenerRegistration>>(
			trackingContext, ServletContextListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		servletContextListenerServiceTracker.open();

		servletContextAttributeListenerServiceTracker = new ServiceTracker<EventListener, AtomicReference<ListenerRegistration>>(
			trackingContext, ServletContextAttributeListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		servletContextAttributeListenerServiceTracker.open();

		servletRequestListenerServiceTracker = new ServiceTracker<EventListener, AtomicReference<ListenerRegistration>>(
			trackingContext, ServletRequestListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		servletRequestListenerServiceTracker.open();

		servletRequestAttributeListenerServiceTracker = new ServiceTracker<EventListener, AtomicReference<ListenerRegistration>>(
			trackingContext, ServletRequestAttributeListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		servletRequestAttributeListenerServiceTracker.open();

		httpSessionListenerServiceTracker = new ServiceTracker<EventListener, AtomicReference<ListenerRegistration>>(
			trackingContext, HttpSessionListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		httpSessionListenerServiceTracker.open();

		httpSessionAttributeListenerServiceTracker = new ServiceTracker<EventListener, AtomicReference<ListenerRegistration>>(
			trackingContext, HttpSessionAttributeListener.class.getName(),
			new ContextListenerTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		httpSessionAttributeListenerServiceTracker.open();

		ServletContext servletContext = httpServletEndpointController.getParentServletContext();

		if ((servletContext.getMajorVersion() >= 3) && (servletContext.getMinorVersion() > 0)) {
			httpSessionIdListenerServiceTracker = new ServiceTracker<EventListener, AtomicReference<ListenerRegistration>>(
				trackingContext, HttpSessionIdListener.class.getName(),
				new ContextListenerTrackerCustomizer(
					trackingContext, httpServletEndpointController, this));

			httpSessionIdListenerServiceTracker.open();
		}
		else {
			httpSessionIdListenerServiceTracker = null;
		}

		filterServiceTracker = new ServiceTracker<Filter, AtomicReference<FilterRegistration>>(
			trackingContext, Filter.class,
			new ContextFilterTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		filterServiceTracker.open();

		servletServiceTracker =  new ServiceTracker<Servlet, AtomicReference<ServletRegistration>>(
			trackingContext, Servlet.class,
			new ContextServletTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		servletServiceTracker.open();

		resourceServiceTracker = new ServiceTracker<Object, AtomicReference<ResourceRegistration>>(
			trackingContext, Object.class,
			new ContextResourceTrackerCustomizer(
				trackingContext, httpServletEndpointController, this));

		resourceServiceTracker.open();
	}

	public FilterRegistration addFilterRegistration(ServiceReference<Filter> filterRef) throws ServletException {
		checkShutdown();

		ServiceHolder<Filter> filterHolder = new ServiceHolder<Filter>(consumingContext.getServiceObjects(filterRef));
		Filter filter = filterHolder.get();
		FilterRegistration registration = null;
		boolean addedRegisteredObject = false;
		try {
			if (filter == null) {
				throw new IllegalArgumentException("Filter cannot be null");
			}
			addedRegisteredObject = httpServletEndpointController.getRegisteredObjects().add(filter);
			if (addedRegisteredObject) {
				registration = doAddFilterRegistration(filterHolder, filterRef);
			}
		} finally {
			if (registration == null) {
				filterHolder.release();
				if (addedRegisteredObject) {
					httpServletEndpointController.getRegisteredObjects().remove(filter);
				}
			}
		}
		return registration;
	}

	private FilterRegistration doAddFilterRegistration(ServiceHolder<Filter> filterHolder, ServiceReference<Filter> filterRef) throws ServletException {

		ClassLoader legacyTCCL = (ClassLoader)filterRef.getProperty(Const.EQUINOX_LEGACY_TCCL_PROP);
		boolean asyncSupported = ServiceProperties.parseBoolean(
			filterRef, HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED);

		List<String> dispatcherList = StringPlus.from(
			filterRef.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER));
		String[] dispatchers = dispatcherList.toArray(
			new String[dispatcherList.size()]);
		Long serviceId = (Long)filterRef.getProperty(
			Constants.SERVICE_ID);
		if (legacyTCCL != null) {
			// this is a legacy registration; use a negative id for the DTO
			serviceId = -serviceId;
		}
		Integer filterPriority = (Integer)filterRef.getProperty(
			Constants.SERVICE_RANKING);
		if (filterPriority == null) {
			filterPriority = Integer.valueOf(0);
		}
		Map<String, String> filterInitParams = ServiceProperties.parseInitParams(
			filterRef, HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX);
		List<String> patternList = StringPlus.from(
			filterRef.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN));
		String[] patterns = patternList.toArray(new String[patternList.size()]);
		List<String> regexList = StringPlus.from(
			filterRef.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX));
		String[] regexs = regexList.toArray(new String[regexList.size()]);
		List<String> servletList = StringPlus.from(
			filterRef.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET));
		String[] servletNames = servletList.toArray(new String[servletList.size()]);

		String name = ServiceProperties.parseName(filterRef.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME), filterHolder.get());

		Filter filter = filterHolder.get();

		if (((patterns == null) || (patterns.length == 0)) &&
			((regexs == null) || (regexs.length == 0)) &&
			((servletNames == null) || (servletNames.length == 0))) {

			throw new IllegalArgumentException(
				"Patterns, regex or servletNames must contain a value.");
		}

		if (patterns != null) {
			for (String pattern : patterns) {
				checkPattern(pattern);
			}
		}

		if (filter == null) {
			throw new IllegalArgumentException("Filter cannot be null");
		}

		if (name == null) {
			name = filter.getClass().getName();
		}

		for (FilterRegistration filterRegistration : filterRegistrations) {
			if (filterRegistration.getT().equals(filter)) {
				throw new RegisteredFilterException(filter);
			}
		}

		dispatchers = checkDispatcher(dispatchers);

		FilterDTO filterDTO = new FilterDTO();

		filterDTO.asyncSupported = asyncSupported;
		filterDTO.dispatcher = sort(dispatchers);
		filterDTO.initParams = filterInitParams;
		filterDTO.name = name;
		filterDTO.patterns = sort(patterns);
		filterDTO.regexs = regexs;
		filterDTO.serviceId = serviceId;
		filterDTO.servletContextId = contextServiceId;
		filterDTO.servletNames = sort(servletNames);

		ServletContextHelper curServletContextHelper = getServletContextHelper(
			filterHolder.getBundle());

		ServletContext servletContext = createServletContext(
			filterHolder.getBundle(), curServletContextHelper);
		FilterRegistration newRegistration  = new FilterRegistration(
			filterHolder, filterDTO, filterPriority, this, legacyTCCL);
		FilterConfig filterConfig = new FilterConfigImpl(
			name, filterInitParams, servletContext);

		newRegistration.init(filterConfig);

		filterRegistrations.add(newRegistration);
		return newRegistration;
	}

	public ListenerRegistration addListenerRegistration(ServiceReference<EventListener> listenerRef) throws ServletException {

		checkShutdown();

		ServiceHolder<EventListener> listenerHolder = new ServiceHolder<EventListener>(consumingContext.getServiceObjects(listenerRef));
		EventListener listener = listenerHolder.get();
		ListenerRegistration registration = null;
		try {
			if (listener == null) {
				throw new IllegalArgumentException("EventListener cannot be null");
			}
			registration = doAddListenerRegistration(listenerHolder, listenerRef);
		} finally {
			if (registration == null) {
				listenerHolder.release();
			}
		}
		return registration;
	}

	private ListenerRegistration doAddListenerRegistration(
		ServiceHolder<EventListener> listenerHolder,
		ServiceReference<EventListener> listenerRef) throws ServletException {


		EventListener eventListener = listenerHolder.get();
		List<Class<? extends EventListener>> classes = getListenerClasses(listenerRef);

		if (classes.isEmpty()) {
			throw new IllegalArgumentException(
				"EventListener does not implement a supported type.");
		}

		for (ListenerRegistration listenerRegistration : listenerRegistrations) {
			if (listenerRegistration.getT().equals(eventListener)) {
				return null;
			}
		}

		ListenerDTO listenerDTO = new ListenerDTO();

		listenerDTO.serviceId = (Long) listenerRef.getProperty(Constants.SERVICE_ID);
		listenerDTO.servletContextId = contextServiceId;
		listenerDTO.types = asStringArray(classes);

		ServletContextHelper curServletContextHelper = getServletContextHelper(
			listenerHolder.getBundle());

		ServletContext servletContext = createServletContext(
			listenerHolder.getBundle(), curServletContextHelper);
		ListenerRegistration listenerRegistration = new ListenerRegistration(
			listenerHolder, classes, listenerDTO, servletContext, this);

		if (classes.contains(ServletContextListener.class)) {
			ServletContextListener servletContextListener =
				(ServletContextListener)listenerRegistration.getT();

			servletContextListener.contextInitialized(
				new ServletContextEvent(servletContext));
		}

		listenerRegistrations.add(listenerRegistration);

		eventListeners.put(classes, listenerRegistration);

		return listenerRegistration;
	}

	public ResourceRegistration addResourceRegistration(ServiceReference<?> resourceRef) {

		checkShutdown();

		ClassLoader legacyTCCL = (ClassLoader)resourceRef.getProperty(Const.EQUINOX_LEGACY_TCCL_PROP);
		Integer rankProp = (Integer) resourceRef.getProperty(Constants.SERVICE_RANKING);
		int serviceRanking = rankProp == null ? 0 : rankProp.intValue();
		List<String> patternList = StringPlus.from(
			resourceRef.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN));
		String[] patterns = patternList.toArray(new String[patternList.size()]);
		Long serviceId = (Long)resourceRef.getProperty(
			Constants.SERVICE_ID);
		if (legacyTCCL != null) {
			// this is a legacy registration; use a negative id for the DTO
			serviceId = -serviceId;
		}
		String prefix = (String)resourceRef.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX);

		checkPrefix(prefix);

		if ((patterns == null) || (patterns.length < 1)) {
			throw new IllegalArgumentException(
				"Patterns must contain a value.");
		}

		for (String pattern : patterns) {
			checkPattern(pattern);
		}

		Bundle bundle = resourceRef.getBundle();
		ServletContextHelper curServletContextHelper = getServletContextHelper(
			bundle);
		Servlet servlet = new ResourceServlet(
			prefix, curServletContextHelper, AccessController.getContext());

		ResourceDTO resourceDTO = new ResourceDTO();

		resourceDTO.patterns = sort(patterns);
		resourceDTO.prefix = prefix;
		resourceDTO.serviceId = serviceId;
		resourceDTO.servletContextId = contextServiceId;

		ServletContext servletContext = createServletContext(
			bundle, curServletContextHelper);
		ResourceRegistration resourceRegistration = new ResourceRegistration(
			new ServiceHolder<Servlet>(servlet, bundle, serviceId, serviceRanking),
			resourceDTO, curServletContextHelper, this, legacyTCCL);
		ServletConfig servletConfig = new ServletConfigImpl(
			resourceRegistration.getName(), new HashMap<String, String>(),
			servletContext);

		try {
			resourceRegistration.init(servletConfig);
		}
		catch (ServletException e) {
			return null;
		}

		endpointRegistrations.add(resourceRegistration);

		return resourceRegistration;
	}

	public ServletRegistration addServletRegistration(ServiceReference<Servlet> servletRef) throws ServletException {

		checkShutdown();

		ServiceHolder<Servlet> servletHolder = new ServiceHolder<Servlet>(consumingContext.getServiceObjects(servletRef));
		Servlet servlet = servletHolder.get();
		ServletRegistration registration = null;
		boolean addedRegisteredObject = false;
		try {
			if (servlet == null) {
				throw new IllegalArgumentException("Servlet cannot be null");
			}
			addedRegisteredObject = httpServletEndpointController.getRegisteredObjects().add(servlet);
			if (addedRegisteredObject) {
				registration = doAddServletRegistration(servletHolder, servletRef);
			}
		} finally {
			if (registration == null) {
				servletHolder.release();
				if (addedRegisteredObject) {
					httpServletEndpointController.getRegisteredObjects().remove(servlet);
				}
			}
		}
		return registration;
	}

	private ServletRegistration doAddServletRegistration(ServiceHolder<Servlet> servletHolder, ServiceReference<Servlet> servletRef) throws ServletException {

		boolean asyncSupported = ServiceProperties.parseBoolean(
			servletRef,	HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED);
		ClassLoader legacyTCCL = (ClassLoader)servletRef.getProperty(Const.EQUINOX_LEGACY_TCCL_PROP);
		List<String> errorPageList = StringPlus.from(
			servletRef.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE));
		String[] errorPages = errorPageList.toArray(new String[errorPageList.size()]);
		Map<String, String> servletInitParams = ServiceProperties.parseInitParams(
			servletRef, HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX);
		List<String> patternList = StringPlus.from(
			servletRef.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN));
		String[] patterns = patternList.toArray(new String[patternList.size()]);
		Long serviceId = (Long)servletRef.getProperty(Constants.SERVICE_ID);
		if (legacyTCCL != null) {
			// this is a legacy registration; use a negative id for the DTO
			serviceId = -serviceId;
		}
		String servletNameFromProperties = (String)servletRef.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME);
		String generatedServletName = ServiceProperties.parseName(
			servletRef.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME), servletHolder.get());

		if (((patterns == null) || (patterns.length == 0)) &&
			((errorPages == null) || errorPages.length == 0) &&
			(servletNameFromProperties == null)) {

			StringBuilder sb = new StringBuilder();
			sb.append("One of the service properties "); //$NON-NLS-1$
			sb.append(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE);
			sb.append(", "); //$NON-NLS-1$
			sb.append(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME);
			sb.append(", "); //$NON-NLS-1$
			sb.append(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN);
			sb.append(" must contain a value."); //$NON-NLS-1$

			throw new IllegalArgumentException(sb.toString());
		}

		if (patterns != null) {
			for (String pattern : patterns) {
				checkPattern(pattern);
			}
		}

		ServletDTO servletDTO = new ServletDTO();

		servletDTO.asyncSupported = asyncSupported;
		servletDTO.initParams = servletInitParams;
		servletDTO.name = generatedServletName;
		servletDTO.patterns = sort(patterns);
		servletDTO.serviceId = serviceId;
		servletDTO.servletContextId = contextServiceId;
		servletDTO.servletInfo = servletHolder.get().getServletInfo();

		ErrorPageDTO errorPageDTO = null;

		if ((errorPages != null) && (errorPages.length > 0)) {
			errorPageDTO = new ErrorPageDTO();

			errorPageDTO.asyncSupported = asyncSupported;
			List<String> exceptions = new ArrayList<String>();
			// Not sure if it is important to maintain order of insertion or natural ordering here.
			// Using insertion ordering with linked hash set.
			Set<Long> errorCodeSet = new LinkedHashSet<Long>();

			for(String errorPage : errorPages) {
				try {
					if ("4xx".equals(errorPage)) { //$NON-NLS-1$
						for (long code = 400; code < 500; code++) {
							errorCodeSet.add(code);
						}
					} else if ("5xx".equals(errorPage)) { //$NON-NLS-1$
						for (long code = 500; code < 600; code++) {
							errorCodeSet.add(code);
						}
					} else {
						long code = Long.parseLong(errorPage);
						errorCodeSet.add(code);
					}
				}
				catch (NumberFormatException nfe) {
					exceptions.add(errorPage);
				}
			}

			long[] errorCodes = new long[errorCodeSet.size()];
			int i = 0;
			for(Long code : errorCodeSet) {
				errorCodes[i] = code;
				i++;
			}
			errorPageDTO.errorCodes = errorCodes;
			errorPageDTO.exceptions = exceptions.toArray(new String[exceptions.size()]);
			errorPageDTO.initParams = servletInitParams;
			errorPageDTO.name = generatedServletName;
			errorPageDTO.serviceId = serviceId;
			errorPageDTO.servletContextId = contextServiceId;
			errorPageDTO.servletInfo = servletHolder.get().getServletInfo();
		}

		ServletContextHelper curServletContextHelper = getServletContextHelper(
			servletHolder.getBundle());

		ServletContext servletContext = createServletContext(
			servletHolder.getBundle(), curServletContextHelper);
		ServletRegistration servletRegistration = new ServletRegistration(
			servletHolder, servletDTO, errorPageDTO, curServletContextHelper, this,
			legacyTCCL);
		ServletConfig servletConfig = new ServletConfigImpl(
			generatedServletName, servletInitParams, servletContext);

		servletRegistration.init(servletConfig);

		endpointRegistrations.add(servletRegistration);

		return servletRegistration;
	}

	public void destroy() {
		flushActiveSessions();
		resourceServiceTracker.close();
		servletServiceTracker.close();
		filterServiceTracker.close();

		if (httpSessionIdListenerServiceTracker != null) {
			httpSessionIdListenerServiceTracker.close();
		}

		httpSessionAttributeListenerServiceTracker.close();
		httpSessionListenerServiceTracker.close();
		servletRequestAttributeListenerServiceTracker.close();
		servletRequestListenerServiceTracker.close();
		servletContextAttributeListenerServiceTracker.close();
		servletContextListenerServiceTracker.close();

		endpointRegistrations.clear();
		filterRegistrations.clear();
		listenerRegistrations.clear();
		eventListeners.clear();
		servletContextHelperDataContext.destroy();

		shutdown = true;
	}

	public String getContextName() {
		return contextName;
	}

	public String getContextPath() {
		return contextPath;
	}

	public DispatchTargets getDispatchTargets(String pathString) {
		Path path = new Path(pathString);

		String queryString = path.getQueryString();
		String requestURI = path.getRequestURI();

		// perfect match
		DispatchTargets dispatchTargets = getDispatchTargets(
			requestURI, null, queryString, Match.EXACT);

		if (dispatchTargets == null) {
			// extension match

			dispatchTargets = getDispatchTargets(
				requestURI, path.getExtension(), queryString, Match.EXTENSION);
		}

		if (dispatchTargets == null) {
			// regex match
			dispatchTargets = getDispatchTargets(
				requestURI, null, queryString, Match.REGEX);
		}

		if (dispatchTargets == null) {
			// handle '/' aliases
			dispatchTargets = getDispatchTargets(
				requestURI, null, queryString, Match.DEFAULT_SERVLET);
		}

		return dispatchTargets;
	}

	private DispatchTargets getDispatchTargets(
		String requestURI, String extension, String queryString, Match match) {

		int pos = requestURI.lastIndexOf('/');

		String servletPath = requestURI;
		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = Const.SLASH;
		}

		do {
			DispatchTargets dispatchTargets = getDispatchTargets(
				null, requestURI, servletPath, pathInfo,
				extension, queryString, match);

			if (dispatchTargets != null) {
				return dispatchTargets;
			}

			if (match == Match.EXACT) {
				break;
			}

			if (pos > -1) {
				String newServletPath = requestURI.substring(0, pos);
				pathInfo = requestURI.substring(pos);
				servletPath = newServletPath;
				pos = servletPath.lastIndexOf('/');

				continue;
			}

			break;
		}
		while (true);

		return null;
	}

	public DispatchTargets getDispatchTargets(
		String servletName, String requestURI, String servletPath,
		String pathInfo, String extension, String queryString, Match match) {

		checkShutdown();

		EndpointRegistration<?> endpointRegistration = null;
		for (EndpointRegistration<?> curEndpointRegistration : endpointRegistrations) {
			if (curEndpointRegistration.match(servletName, servletPath, pathInfo, extension, match) != null) {
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

		if (filterRegistrations.isEmpty()) {
			return new DispatchTargets(
				this, endpointRegistration, servletName, requestURI, servletPath,
				pathInfo, queryString);
		}

		if (requestURI != null) {
			int x = requestURI.lastIndexOf('.');

			if (x != -1) {
				extension = requestURI.substring(x + 1);
			}
		}

		List<FilterRegistration> matchingFilterRegistrations =
			new ArrayList<FilterRegistration>();

		collectFilters(
			matchingFilterRegistrations, endpointRegistration.getName(), requestURI,
			servletPath, pathInfo, extension);

		return new DispatchTargets(
			this, endpointRegistration, matchingFilterRegistrations, servletName,
			requestURI, servletPath, pathInfo, queryString);
	}

	private void collectFilters(
		List<FilterRegistration> matchingFilterRegistrations,
		String servletName, String requestURI, String servletPath, String pathInfo, String extension) {

		for (FilterRegistration filterRegistration : filterRegistrations) {
			if ((filterRegistration.match(
					servletName, requestURI, extension, null) != null) &&
				!matchingFilterRegistrations.contains(filterRegistration)) {

				matchingFilterRegistrations.add(filterRegistration);
			}
		}
	}

	public Map<String, HttpSessionAdaptor> getActiveSessions() {
		return activeSessions;
	}

	public Set<EndpointRegistration<?>> getEndpointRegistrations() {
		return endpointRegistrations;
	}

	public EventListeners getEventListeners() {
		return eventListeners;
	}

	public Set<FilterRegistration> getFilterRegistrations() {
		return filterRegistrations;
	}

	public String getFullContextPath() {
		List<String> endpoints = httpServletEndpointController.getHttpServiceEndpoints();

		if (endpoints.isEmpty()) {
			return contextPath;
		}

		String defaultEndpoint = endpoints.get(0);

		if ((defaultEndpoint.length() > 0) && defaultEndpoint.endsWith("/")) {
			defaultEndpoint = defaultEndpoint.substring(
				0, defaultEndpoint.length() - 1);
		}

		return defaultEndpoint + contextPath;
	}

	public HttpServletEndpointController getHttpServletEndpointController() {
		return httpServletEndpointController;
	}

	public Map<String, String> getInitParams() {
		return initParams;
	}

	public Set<ListenerRegistration> getListenerRegistrations() {
		return listenerRegistrations;
	}

	public boolean matches(ServiceReference<?> whiteBoardService) {
		String contextSelector = (String) whiteBoardService.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT);

		// custom equinox behaviour
		if (contextName.equals(contextSelector)) {
			return true;
		}

		if (contextSelector == null) {
			contextSelector = "(" + //$NON-NLS-1$
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=" //$NON-NLS-1$
				+ HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME + ")"; //$NON-NLS-1$
		}

		if (contextSelector.startsWith(Const.OPEN_PAREN)) {
			org.osgi.framework.Filter targetFilter;

			try {
				targetFilter = FrameworkUtil.createFilter(contextSelector);
			}
			catch (InvalidSyntaxException ise) {
				throw new IllegalArgumentException(ise);
			}

			if (matches(targetFilter)) {
				return true;
			}
		}

		return false;
	}

	private boolean visibleContextHelper(ServiceReference<?> whiteBoardService) {
		if (consumingContext.getBundle().equals(servletContextHelperRef.getBundle())) {
			return true;
		}

		return servletContextHelperRef.isAssignableTo(
			whiteBoardService.getBundle(),
			ServletContextHelper.class.getName());
	}

	public boolean matches(org.osgi.framework.Filter targetFilter) {
		return targetFilter.match(servletContextHelperRef);
	}

	@Override
	public String toString() {
		String value = string;

		if (value == null) {
			value = SIMPLE_NAME + '[' + contextName + ", " + trackingContext.getBundle() + ']'; //$NON-NLS-1$

			string = value;
		}

		return value;
	}

	private String[] asStringArray(
		List<Class<? extends EventListener>> clazzes) {

		String[] classesArray = new String[clazzes.size()];

		for (int i = 0; i < classesArray.length; i++) {
			classesArray[i] = clazzes.get(i).getName();
		}

		Arrays.sort(classesArray);

		return classesArray;
	}

	private String[] checkDispatcher(String[] dispatcher) {
		if ((dispatcher == null) || (dispatcher.length == 0)) {
			return DISPATCHER;
		}

		for (String type : dispatcher) {
			try {
				DispatcherType.valueOf(type);
			}
			catch (IllegalArgumentException iae) {
				throw new IllegalArgumentException(
					"Invalid dispatcher '" + type + "'", iae);
			}
		}

		Arrays.sort(dispatcher);

		return dispatcher;
	}

	public static void checkPattern(String pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern cannot be null");
		}

		if (pattern.indexOf("*.") == 0) { //$NON-NLS-1$
			return;
		}

		if (!pattern.startsWith(Const.SLASH) ||
			(pattern.endsWith(Const.SLASH) && !pattern.equals(Const.SLASH))) {

			throw new IllegalArgumentException(
				"Invalid pattern '" + pattern + "'");
		}
	}

	private void checkPrefix(String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException("Prefix cannot be null");
		}

		if (prefix.endsWith(Const.SLASH) && !prefix.equals(Const.SLASH)) {
			throw new IllegalArgumentException("Invalid prefix '" + prefix + "'");
		}
	}

	private void checkShutdown() {
		if (shutdown) {
			throw new IllegalStateException(
				"Context is already shutdown"); //$NON-NLS-1$
		}
	}

	private ServletContext createServletContext(
		Bundle curBundle, ServletContextHelper curServletContextHelper) {

		ServletContextAdaptor adaptor = new ServletContextAdaptor(
			this, curBundle, curServletContextHelper, servletContextHelperDataContext,
			eventListeners, AccessController.getContext());

		return adaptor.createServletContext();
	}

	private List<Class<? extends EventListener>> getListenerClasses(
		ServiceReference<EventListener> serviceReference) {

		List<String> objectClassList = StringPlus.from(serviceReference.getProperty(Constants.OBJECTCLASS));

		List<Class<? extends EventListener>> classes =
			new ArrayList<Class<? extends EventListener>>();

		if (objectClassList.contains(ServletContextListener.class.getName())) {
			classes.add(ServletContextListener.class);
		}
		if (objectClassList.contains(ServletContextAttributeListener.class.getName())) {
			classes.add(ServletContextAttributeListener.class);
		}
		if (objectClassList.contains(ServletRequestListener.class.getName())) {
			classes.add(ServletRequestListener.class);
		}
		if (objectClassList.contains(ServletRequestAttributeListener.class.getName())) {
			classes.add(ServletRequestAttributeListener.class);
		}
		if (objectClassList.contains(HttpSessionListener.class.getName())) {
			classes.add(HttpSessionListener.class);
		}
		if (objectClassList.contains(HttpSessionAttributeListener.class.getName())) {
			classes.add(HttpSessionAttributeListener.class);
		}

		ServletContext servletContext = servletContextHelperDataContext.getServletContext();
		if ((servletContext.getMajorVersion() >= 3) && (servletContext.getMinorVersion() > 0)) {
			if (objectClassList.contains(javax.servlet.http.HttpSessionIdListener.class.getName())) {
				classes.add(javax.servlet.http.HttpSessionIdListener.class);
			}
		}

		return classes;
	}

	private ServletContextHelper getServletContextHelper(Bundle curBundle) {
		BundleContext context = curBundle.getBundleContext();
		return context.getService(servletContextHelperRef);
	}

	public void ungetServletContextHelper(Bundle curBundle) {
		BundleContext context = curBundle.getBundleContext();
		try {
			context.ungetService(servletContextHelperRef);
		} catch (IllegalStateException e) {
			// this can happen if the whiteboard bundle is in the process of stopping
			// and the framework is in the middle of auto-unregistering any services
			// the bundle forgot to unregister on stop
		}
	}

	private String[] sort(String[] values) {
		if (values == null) {
			return null;
		}

		Arrays.sort(values);

		return values;
	}

	private void flushActiveSessions() {
		Collection<HttpSessionAdaptor> httpSessionAdaptors =
			activeSessions.values();

		Iterator<HttpSessionAdaptor> iterator = httpSessionAdaptors.iterator();

		while (iterator.hasNext()) {
			HttpSessionAdaptor httpSessionAdaptor = iterator.next();

			httpSessionAdaptor.invalidate();

			iterator.remove();
		}
	}

	public void removeActiveSession(String id) {
		activeSessions.remove(id);
	}

	public HttpSessionAdaptor getSessionAdaptor(
		HttpSession session, ServletContext servletContext) {

		String sessionId = session.getId();

		HttpSessionAdaptor httpSessionAdaptor = activeSessions.get(sessionId);

		if (httpSessionAdaptor != null) {
			return httpSessionAdaptor;
		}

		httpSessionAdaptor = HttpSessionAdaptor.createHttpSessionAdaptor(
			session, servletContext, this);

		HttpSessionAdaptor previousHttpSessionAdaptor =
			activeSessions.putIfAbsent(sessionId, httpSessionAdaptor);

		if (previousHttpSessionAdaptor != null) {
			return previousHttpSessionAdaptor;
		}

		List<HttpSessionListener> listeners = eventListeners.get(HttpSessionListener.class);

		if (listeners.isEmpty()) {
			return httpSessionAdaptor;
		}

		HttpSessionEvent httpSessionEvent = new HttpSessionEvent(
			httpSessionAdaptor);

		for (HttpSessionListener listener : listeners) {
			listener.sessionCreated(httpSessionEvent);
		}

		return httpSessionAdaptor;
	}

	private void validate(String preValidationContextName, String preValidationContextPath) {
		if (!contextNamePattern.matcher(preValidationContextName).matches()) {
			throw new IllegalContextNameException(
				"The context name '" + preValidationContextName + "' does not follow Bundle-SymbolicName syntax.", //$NON-NLS-1$ //$NON-NLS-2$
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
		}

		try {
			@SuppressWarnings("unused")
			URI uri = new URI(Const.HTTP, Const.LOCALHOST, preValidationContextPath, null);
		}
		catch (URISyntaxException use) {
			throw new IllegalContextPathException(
				"The context path '" + preValidationContextPath + "' is not valid URI path syntax.", //$NON-NLS-1$ //$NON-NLS-2$
				DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
		}
	}

	private static final String[] DISPATCHER =
		new String[] {DispatcherType.REQUEST.toString()};

	private static final String SIMPLE_NAME = ContextController.class.getSimpleName();

	private static final Pattern contextNamePattern = Pattern.compile("^([a-zA-Z_0-9\\-]+\\.)*[a-zA-Z_0-9\\-]+$"); //$NON-NLS-1$

	private Map<String, String> initParams;
	private BundleContext trackingContext;
	private BundleContext consumingContext;
	private String contextName;
	private String contextPath;
	private long contextServiceId;
	private Set<EndpointRegistration<?>> endpointRegistrations;
	private EventListeners eventListeners;
	private Set<FilterRegistration> filterRegistrations;
	private ConcurrentMap<String, HttpSessionAdaptor> activeSessions;

	private HttpServletEndpointController httpServletEndpointController;
	private Set<ListenerRegistration> listenerRegistrations;
	private ServletContextHelperDataContext servletContextHelperDataContext;
	private ServiceReference<ServletContextHelper> servletContextHelperRef;
	private boolean shutdown;
	private String string;

	private ServiceTracker<Filter, AtomicReference<FilterRegistration>> filterServiceTracker;
	private ServiceTracker<EventListener, AtomicReference<ListenerRegistration>> servletContextListenerServiceTracker;
	private ServiceTracker<EventListener, AtomicReference<ListenerRegistration>> servletContextAttributeListenerServiceTracker;
	private ServiceTracker<EventListener, AtomicReference<ListenerRegistration>> servletRequestListenerServiceTracker;
	private ServiceTracker<EventListener, AtomicReference<ListenerRegistration>> servletRequestAttributeListenerServiceTracker;
	private ServiceTracker<EventListener, AtomicReference<ListenerRegistration>> httpSessionListenerServiceTracker;
	private ServiceTracker<EventListener, AtomicReference<ListenerRegistration>> httpSessionAttributeListenerServiceTracker;
	private ServiceTracker<EventListener, AtomicReference<ListenerRegistration>> httpSessionIdListenerServiceTracker;
	private ServiceTracker<Servlet, AtomicReference<ServletRegistration>> servletServiceTracker;
	private ServiceTracker<Object, AtomicReference<ResourceRegistration>> resourceServiceTracker;
}
/* @generated */