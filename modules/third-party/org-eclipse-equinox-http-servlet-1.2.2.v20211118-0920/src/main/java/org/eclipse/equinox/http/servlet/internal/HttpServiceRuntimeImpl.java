/*******************************************************************************
 * Copyright (c) 2014, 2015 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package org.eclipse.equinox.http.servlet.internal;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.*;
import javax.servlet.Filter;
import javax.servlet.http.*;
import org.eclipse.equinox.http.servlet.context.ContextPathCustomizer;
import org.eclipse.equinox.http.servlet.internal.context.*;
import org.eclipse.equinox.http.servlet.internal.error.*;
import org.eclipse.equinox.http.servlet.internal.servlet.Match;
import org.eclipse.equinox.http.servlet.internal.util.*;
import org.osgi.framework.*;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.HttpServiceRuntimeConstants;
import org.osgi.service.http.runtime.dto.*;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 */
public class HttpServiceRuntimeImpl
	implements
		HttpServiceRuntime, HttpServletEndpointController,
		ServiceTrackerCustomizer<ServletContextHelper, AtomicReference<ContextController>> {

	public HttpServiceRuntimeImpl(
		BundleContext trackingContext, BundleContext consumingContext,
		ServletContext parentServletContext, Map<String, Object> attributes) {

		this.trackingContext = trackingContext;
		this.consumingContext = consumingContext;

		this.parentServletContext = parentServletContext;
		this.attributes = attributes;
		this.targetFilter = "(http.servlet.endpoint.id=" + attributes.get("http.servlet.endpoint.id") + ")";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		contextServiceTracker =
			new ServiceTracker<ServletContextHelper, AtomicReference<ContextController>>(
				trackingContext, ServletContextHelper.class, this);

		contextPathCustomizerHolder = new ContextPathCustomizerHolder(consumingContext, contextServiceTracker);
		contextPathAdaptorTracker = new ServiceTracker<ContextPathCustomizer, ContextPathCustomizer>(
			consumingContext, ContextPathCustomizer.class, contextPathCustomizerHolder);
		contextPathAdaptorTracker.open();

		contextServiceTracker.open();

		Hashtable<String, Object> defaultContextProps = new Hashtable<String, Object>();
		defaultContextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		defaultContextProps.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);
		defaultContextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, Const.SLASH);
		defaultContextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, this.targetFilter);
		defaultContextReg = consumingContext.registerService(ServletContextHelper.class, new DefaultServletContextHelperFactory(), defaultContextProps);
	}

	@Override
	public synchronized AtomicReference<ContextController> addingService(
		ServiceReference<ServletContextHelper> serviceReference) {

		AtomicReference<ContextController> result = new AtomicReference<ContextController>();
		if (!matches(serviceReference)) {
			return result;
		}

		String contextName = (String)serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME);
		String contextPath = (String)serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH);

		try {
			if (contextName == null) {
				throw new IllegalContextNameException(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + " is null. Ignoring!", //$NON-NLS-1$
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
			}

			if (contextPath == null) {
				throw new IllegalContextPathException(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH + " is null. Ignoring!", //$NON-NLS-1$
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
			}

			contextPath = adaptContextPath(contextPath, serviceReference);

			ContextController contextController = new ContextController(
				trackingContext, consumingContext, serviceReference, new ProxyContext(contextName, parentServletContext),
				this, contextName, contextPath);

			controllerMap.put(serviceReference, contextController);

			result.set(contextController);
		}
		catch (HttpWhiteboardFailureException hwfe) {
			parentServletContext.log(hwfe.getMessage(), hwfe);
		}
		catch (Exception e) {
			parentServletContext.log(e.getMessage(), e);
		}

		return result;
	}

	private String adaptContextPath(String contextPath, ServiceReference<ServletContextHelper> helper) {
		ContextPathCustomizer pathAdaptor = contextPathCustomizerHolder.getHighestRanked();
		if (pathAdaptor != null) {
			String contextPrefix = pathAdaptor.getContextPathPrefix(helper);
			if (contextPrefix != null && !contextPrefix.isEmpty() && !contextPrefix.equals(Const.SLASH)) {
				if (!contextPrefix.startsWith(Const.SLASH)) {
					contextPrefix = Const.SLASH + contextPrefix;
				}
				// make sure we do not append SLASH context path here
				if (contextPath == null || contextPath.equals(Const.SLASH)) {
					contextPath = Const.BLANK;
				}
				return contextPrefix + contextPath;
			}
		}
		return contextPath;
	}

	public String getDefaultContextSelectFilter(ServiceReference<?> httpWhiteBoardService) {
		ContextPathCustomizer pathAdaptor = contextPathCustomizerHolder.getHighestRanked();
		if (pathAdaptor != null) {
			return pathAdaptor.getDefaultContextSelectFilter(httpWhiteBoardService);
		}
		return null;
	}

	@Override
	public RequestInfoDTO calculateRequestInfoDTO(String path) {
		return null;
	}

	public void destroy() {
		defaultContextReg.unregister();

		contextServiceTracker.close();
		contextPathAdaptorTracker.close();

		controllerMap.clear();
		registeredObjects.clear();

		attributes = null;
		trackingContext = null;
		consumingContext = null;
		legacyIdGenerator = null;
		parentServletContext = null;
		registeredObjects = null;
		contextServiceTracker = null;
		contextPathCustomizerHolder = null;
	}

	@Override
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

	public ServletContext getParentServletContext() {
		return parentServletContext;
	}

	public Set<Object> getRegisteredObjects() {
		return registeredObjects;
	}

	public List<String> getHttpServiceEndpoints() {
		return StringPlus.from(
			attributes.get(
				HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT));
	}

	@Override
	public RuntimeDTO getRuntimeDTO() {
		RuntimeDTO runtimeDTO = new RuntimeDTO();

		runtimeDTO.serviceDTO = getServiceDTO();

		// TODO FailedErrorDTOs

		runtimeDTO.failedErrorPageDTOs = null;
		runtimeDTO.failedFilterDTOs = null;
		runtimeDTO.failedListenerDTOs = null;
		runtimeDTO.failedResourceDTOs = null;
		runtimeDTO.failedServletContextDTOs = null;
		runtimeDTO.failedServletDTOs = null;
		runtimeDTO.servletContextDTOs = null;

		return runtimeDTO;
	}

	private ServiceReferenceDTO getServiceDTO() {
		ServiceReferenceDTO[] services = consumingContext.getBundle().adapt(ServiceReferenceDTO[].class);
		for (ServiceReferenceDTO serviceDTO : services) {
			String[] serviceTypes = (String[]) serviceDTO.properties.get(Constants.OBJECTCLASS);
			for (String type : serviceTypes) {
				if (HttpServiceRuntime.class.getName().equals(type)) {
					return serviceDTO;
				}
			}
		}
		return null;
	}

	public void log(String message, Throwable t) {
		parentServletContext.log(message, t);
	}

	public boolean matches(ServiceReference<?> serviceReference) {
		String target = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET);

		if (target == null) {
			return true;
		}

		org.osgi.framework.Filter targetFilter;

		try {
			targetFilter = FrameworkUtil.createFilter(target);
		}
		catch (InvalidSyntaxException ise) {
			throw new IllegalArgumentException(ise);
		}

		if (targetFilter.matches(attributes)) {
			return true;
		}

		return false;
	}

	@Override
	public synchronized void modifiedService(
		ServiceReference<ServletContextHelper> serviceReference,
		AtomicReference<ContextController> contextController) {

		removedService(serviceReference, contextController);
		AtomicReference<ContextController> added = addingService(serviceReference);
		contextController.set(added.get());
	}

	@Override
	public synchronized void removedService(
		ServiceReference<ServletContextHelper> serviceReference,
		AtomicReference<ContextController> contextControllerRef) {

		ContextController contextController = contextControllerRef.get();
		if (contextController != null) {
			contextController.destroy();
		}
		controllerMap.remove(serviceReference);
		trackingContext.ungetService(serviceReference);
	}

	Collection<ContextController> getContextControllers(String requestURI) {
		int pos = requestURI.lastIndexOf('/');

		do {
			List<ContextController> contextControllers = new ArrayList<ContextController>();

			for (ContextController contextController : controllerMap.values()) {
				if (contextController.getContextPath().equals(requestURI)) {
					contextControllers.add(contextController);
				}
			}

			if (!contextControllers.isEmpty()) {
				return contextControllers;
			}

			if (pos > -1) {
				requestURI = requestURI.substring(0, pos);
				pos = requestURI.lastIndexOf('/');

				continue;
			}

			break;
		}
		while (true);

		return null;
	}

	long generateLegacyId() {
		return legacyIdGenerator.getAndIncrement();
	}

	public boolean doDispatch(
			HttpServletRequest request, HttpServletResponse response, String path)
		throws IOException, ServletException {

		DispatchTargets dispatchTargets = getDispatchTargets(path);

		if (dispatchTargets == null) {
			return false;
		}

		return dispatchTargets.doDispatch(
			request, response, path, request.getDispatcherType());
	}

	private DispatchTargets getDispatchTargets(
		String requestURI, String extension, String queryString, Match match) {

		Collection<ContextController> contextControllers = getContextControllers(
			requestURI);

		if ((contextControllers == null) || contextControllers.isEmpty()) {
			return null;
		}

		String contextPath =
			contextControllers.iterator().next().getContextPath();

		requestURI = requestURI.substring(contextPath.length());

		int pos = requestURI.lastIndexOf('/');

		String servletPath = requestURI;
		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = Const.SLASH;
		}

		do {
			for (ContextController contextController : contextControllers) {
				DispatchTargets dispatchTargets =
					contextController.getDispatchTargets(
						null, requestURI, servletPath, pathInfo,
						extension, queryString, match);

				if (dispatchTargets != null) {
					return dispatchTargets;
				}
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

	public void registerHttpServiceFilter(
		Bundle bundle, String alias, Filter filter, Dictionary<String, String> initparams, HttpContext httpContext) throws ServletException {

		if (alias == null) {
			throw new IllegalArgumentException("Alias cannot be null");
		}
		if (filter == null) {
			throw new IllegalArgumentException("Filter cannot be null");
		}

		ContextController.checkPattern(alias);

		// need to make sure exact matching aliases are converted to wildcard pattern matches
		if (!alias.endsWith(Const.SLASH_STAR) && !alias.startsWith(Const.STAR_DOT) && !alias.contains(Const.SLASH_STAR_DOT)) {
			if (alias.endsWith(Const.SLASH)) {
				alias = alias + '*';
			} else {
				alias = alias + Const.SLASH_STAR;
			}
		}

		synchronized (legacyMappings) {
			if (getRegisteredObjects().contains(filter)) {
				throw new RegisteredFilterException(filter);
			}
			HttpServiceObjectRegistration existing = legacyMappings.get(filter);
			if (existing != null) {
				throw new RegisteredFilterException(filter);
			}
			String filterName = filter.getClass().getName();
			if ((initparams != null) && (initparams.get(Const.FILTER_NAME) != null)) {
				filterName = initparams.get(Const.FILTER_NAME);
			}
			HttpContextHelperFactory factory = getOrRegisterHttpContextHelperFactory(bundle, httpContext);

			HttpServiceObjectRegistration objectRegistration = null;
			ServiceRegistration<Filter> registration = null;
			try {
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, targetFilter);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, alias);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, filterName);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(" + Const.EQUINOX_LEGACY_CONTEXT_HELPER + "=true)"); //$NON-NLS-1$ //$NON-NLS-2$
				props.put(Const.EQUINOX_LEGACY_CONTEXT_SELECT, factory.getFilter());
				props.put(Const.EQUINOX_LEGACY_TCCL_PROP, Thread.currentThread().getContextClassLoader());
				props.put(Constants.SERVICE_RANKING, findFilterPriority(initparams));
				fillInitParams(props, initparams, HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX);

				LegacyFilterFactory filterFactory = new LegacyFilterFactory(filter);
				registration = bundle.getBundleContext().registerService(Filter.class, filterFactory, props);

				// check that init got called and did not throw an exception
				filterFactory.checkForError();

				objectRegistration = new HttpServiceObjectRegistration(filter, registration, factory, bundle);
				Set<HttpServiceObjectRegistration> objectRegistrations = bundleRegistrations.get(bundle);
				if (objectRegistrations == null) {
					objectRegistrations = new HashSet<HttpServiceObjectRegistration>();
					bundleRegistrations.put(bundle, objectRegistrations);
				}
				objectRegistrations.add(objectRegistration);
				legacyMappings.put(objectRegistration.serviceKey, objectRegistration);
			} finally {
				if (objectRegistration == null || !legacyMappings.containsKey(objectRegistration.serviceKey)) {
					// something bad happened above (likely going to throw a runtime exception)
					// need to clean up the factory reference
					decrementFactoryUseCount(factory);
					if (registration != null) {
						registration.unregister();
					}
				}
			}
		}
	}

	private void fillInitParams(
		Dictionary<String, Object> props,
		Dictionary<String, String> initparams, String prefix) {
		if (initparams != null) {
			for (Enumeration<String> eKeys = initparams.keys(); eKeys.hasMoreElements();) {
				String key = eKeys.nextElement();
				String value = initparams.get(key);
				if (value != null) {
					props.put(prefix + key, value);
				}
			}
		}
	}

	private static int findFilterPriority(Dictionary<String, String> initparams) {
		if (initparams == null) {
			return 0;
		}

		String filterPriority = initparams.get(Const.FILTER_PRIORITY);

		if (filterPriority == null) {
			return 0;
		}

		try {
			int result = Integer.parseInt(filterPriority);
			if (result >= -1000 && result <= 1000) {
				return result;
			}
		}
		catch (NumberFormatException e) {
			// fall through
		}

		throw new IllegalArgumentException(
			"filter-priority must be an integer between -1000 and 1000 but " +
				"was: " + filterPriority);
	}

	public void registerHttpServiceResources(
		Bundle bundle, String alias, String name, HttpContext httpContext) throws NamespaceException {
		if (alias == null) {
			throw new IllegalArgumentException("Alias cannot be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null");
		}
		String pattern = alias;
		if (pattern.startsWith("/*.")) { //$NON-NLS-1$
			pattern = pattern.substring(1);
		}
		else if (!pattern.contains("*.") && //$NON-NLS-1$
				!pattern.endsWith(Const.SLASH_STAR) &&
				!pattern.endsWith(Const.SLASH)) {
			pattern += Const.SLASH_STAR;
		}

		ContextController.checkPattern(alias);

		synchronized (legacyMappings) {
			HttpServiceObjectRegistration objectRegistration = null;
			HttpContextHelperFactory factory = getOrRegisterHttpContextHelperFactory(bundle, httpContext);
			try {
				String fullAlias = getFullAlias(alias, factory);
				HttpServiceObjectRegistration existing = legacyMappings.get(fullAlias);
				if (existing != null) {
					throw new PatternInUseException(alias);
				}
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, targetFilter);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, pattern);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, name);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, factory.getFilter());
				props.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
				props.put(Const.EQUINOX_LEGACY_TCCL_PROP, Thread.currentThread().getContextClassLoader());
				ServiceRegistration<?> registration = bundle.getBundleContext().registerService(Object.class, "resource", props); //$NON-NLS-1$
				objectRegistration = new HttpServiceObjectRegistration(fullAlias, registration, factory, bundle);

				Set<HttpServiceObjectRegistration> objectRegistrations = bundleRegistrations.get(bundle);
				if (objectRegistrations == null) {
					objectRegistrations = new HashSet<HttpServiceObjectRegistration>();
					bundleRegistrations.put(bundle, objectRegistrations);
				}
				objectRegistrations.add(objectRegistration);

				Map<String, String> aliasCustomizations = bundleAliasCustomizations.get(bundle);
				if (aliasCustomizations == null) {
					aliasCustomizations = new HashMap<String, String>();
					bundleAliasCustomizations.put(bundle, aliasCustomizations);
				}
				aliasCustomizations.put(alias, fullAlias);
				legacyMappings.put(objectRegistration.serviceKey, objectRegistration);
			} finally {
				if (objectRegistration == null || !legacyMappings.containsKey(objectRegistration.serviceKey)) {
					// something bad happened above (likely going to throw a runtime exception)
					// need to clean up the factory reference
					decrementFactoryUseCount(factory);
				}
			}
		}
	}

	public void registerHttpServiceServlet(
		Bundle bundle, String alias, Servlet servlet, Dictionary<String, String> initparams, HttpContext httpContext) throws NamespaceException, ServletException{
		if (alias == null) {
			throw new IllegalArgumentException("Alias cannot be null");
		}
		if (servlet == null) {
			throw new IllegalArgumentException("Servlet cannot be null");
		}

		// check the pattern against the original input
		ContextController.checkPattern(alias);

		Object pattern = alias;
		// need to make sure exact matching aliases are converted to exact matching + wildcard pattern matching
		if (!alias.endsWith(Const.SLASH_STAR) && !alias.startsWith(Const.STAR_DOT) && !alias.contains(Const.SLASH_STAR_DOT)) {
			if (alias.endsWith(Const.SLASH)) {
				pattern = new String[] {alias, alias + '*'};
			} else {
				pattern = new String[] {alias, alias + Const.SLASH_STAR};
			}
		}

		synchronized (legacyMappings) {
			LegacyServlet legacyServlet = new LegacyServlet(servlet);
			if (getRegisteredObjects().contains(legacyServlet)) {
				throw new ServletAlreadyRegisteredException(servlet);
			}
			HttpServiceObjectRegistration objectRegistration = null;
			ServiceRegistration<Servlet> registration = null;
			HttpContextHelperFactory factory = getOrRegisterHttpContextHelperFactory(bundle, httpContext);
			try {
				String fullAlias = getFullAlias(alias, factory);
				HttpServiceObjectRegistration existing = legacyMappings.get(fullAlias);
				if (existing != null) {
					throw new PatternInUseException(alias);
				}
				String servletName = servlet.getClass().getName();
				if ((initparams != null) && (initparams.get(Const.SERVLET_NAME) != null)) {
					servletName = initparams.get(Const.SERVLET_NAME);
				}

				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, targetFilter);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, pattern);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, servletName);
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, factory.getFilter());
				props.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
				props.put(Const.EQUINOX_LEGACY_TCCL_PROP, Thread.currentThread().getContextClassLoader());
				fillInitParams(props, initparams, HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX);

				registration = bundle.getBundleContext().registerService(Servlet.class, legacyServlet, props);

				// check that init got called and did not throw an exception
				legacyServlet.checkForError();

				objectRegistration = new HttpServiceObjectRegistration(fullAlias, registration, factory, bundle);

				Set<HttpServiceObjectRegistration> objectRegistrations = bundleRegistrations.get(bundle);
				if (objectRegistrations == null) {
					objectRegistrations = new HashSet<HttpServiceObjectRegistration>();
					bundleRegistrations.put(bundle, objectRegistrations);
				}
				objectRegistrations.add(objectRegistration);

				Map<String, String> aliasCustomizations = bundleAliasCustomizations.get(bundle);
				if (aliasCustomizations == null) {
					aliasCustomizations = new HashMap<String, String>();
					bundleAliasCustomizations.put(bundle, aliasCustomizations);
				}
				aliasCustomizations.put(alias, fullAlias);

				legacyMappings.put(objectRegistration.serviceKey, objectRegistration);
			} finally {
				if (objectRegistration == null || !legacyMappings.containsKey(objectRegistration.serviceKey)) {
					// something bad happened above (likely going to throw a runtime exception)
					// need to clean up the factory reference
					decrementFactoryUseCount(factory);
					if (registration != null) {
						registration.unregister();
					}
				}
			}
		}
	}

	private String getFullAlias(String alias, HttpContextHelperFactory factory) {
		AtomicReference<ContextController> controllerRef = contextServiceTracker.getService(factory.getServiceReference());
		if (controllerRef != null) {
			ContextController controller = controllerRef.get();
			if (controller != null) {
				return controller.getContextPath() + alias;
			}
		}
		return alias;
	}

	public void unregisterHttpServiceAlias(Bundle bundle, String alias) {
		synchronized (legacyMappings) {
			Map<String, String> aliasCustomizations = bundleAliasCustomizations.get(bundle);
			String aliasCustomization = aliasCustomizations == null ? null : aliasCustomizations.remove(alias);
			if (aliasCustomization == null) {
				throw new IllegalArgumentException("The bundle did not register the alias: " + alias); //$NON-NLS-1$
			}
			HttpServiceObjectRegistration objectRegistration = legacyMappings.get(aliasCustomization);
			if (objectRegistration == null) {
				throw new IllegalArgumentException("No registration found for alias: " + alias); //$NON-NLS-1$
			}
			Set<HttpServiceObjectRegistration> objectRegistrations = bundleRegistrations.get(bundle);
			if (objectRegistrations == null || !objectRegistrations.remove(objectRegistration))
			{
				throw new IllegalArgumentException("The bundle did not register the alias: " + alias); //$NON-NLS-1$
			}

			try {
				objectRegistration.registration.unregister();
			} catch (IllegalStateException e) {
				// ignore; already unregistered
			}
			decrementFactoryUseCount(objectRegistration.factory);
			legacyMappings.remove(aliasCustomization);

		}
	}

	public void unregisterHttpServiceFilter(Bundle bundle, Filter filter) {
		synchronized (legacyMappings) {
			HttpServiceObjectRegistration objectRegistration = legacyMappings.get(filter);
			if (objectRegistration == null) {
				throw new IllegalArgumentException("No registration found for filter: " + filter); //$NON-NLS-1$
			}
			Set<HttpServiceObjectRegistration> objectRegistrations = bundleRegistrations.get(bundle);
			if (objectRegistrations == null || !objectRegistrations.remove(objectRegistration))
			{
				throw new IllegalArgumentException("The bundle did not register the filter: " + filter); //$NON-NLS-1$
			}
			try {
				objectRegistration.registration.unregister();
			} catch (IllegalStateException e) {
				// ignore; already unregistered
			}
			decrementFactoryUseCount(objectRegistration.factory);
			legacyMappings.remove(filter);
		}
	}

	public void unregisterHttpServiceObjects(Bundle bundle) {
		synchronized (legacyMappings) {
			bundleAliasCustomizations.remove(bundle);
			Set<HttpServiceObjectRegistration> objectRegistrations = bundleRegistrations.remove(bundle);
			if (objectRegistrations != null) {
				for (HttpServiceObjectRegistration objectRegistration : objectRegistrations) {
					try {
						objectRegistration.registration.unregister();
					} catch (IllegalStateException e) {
						// ignore; already unregistered
					}
					decrementFactoryUseCount(objectRegistration.factory);
					legacyMappings.remove(objectRegistration.serviceKey);
				}
			}
		}
	}

	private HttpContextHelperFactory getOrRegisterHttpContextHelperFactory(Bundle initiatingBundle, HttpContext httpContext) {
		if (httpContext == null) {
			throw new NullPointerException("A null HttpContext is not allowed."); //$NON-NLS-1$
		}
		synchronized (httpContextHelperFactories) {
			HttpContextHelperFactory factory = httpContextHelperFactories.get(httpContext);
			if (factory == null) {
				factory = new HttpContextHelperFactory(httpContext);
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, httpContext.getClass().getName().replaceAll("[^a-zA-Z_0-9\\-]", "_") + "-" + generateLegacyId()); //$NON-NLS-1$
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/"); //$NON-NLS-1$
				props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, targetFilter);
				props.put(Const.EQUINOX_LEGACY_CONTEXT_HELPER, Boolean.TRUE);
				props.put(Const.EQUINOX_LEGACY_HTTP_CONTEXT_INITIATING_ID, initiatingBundle.getBundleId());
				factory.setRegistration(consumingContext.registerService(ServletContextHelper.class, factory, props));
				httpContextHelperFactories.put(httpContext, factory);
			}
			factory.incrementUseCount();
			return factory;
		}
	}

	private void decrementFactoryUseCount(HttpContextHelperFactory factory) {
		synchronized (httpContextHelperFactories) {
			if (factory.decrementUseCount() == 0) {
				httpContextHelperFactories.remove(factory.getHttpContext());
			}
		}
	}

	private Map<String, Object> attributes;
	private final String targetFilter;
	private final ServiceRegistration<ServletContextHelper> defaultContextReg;
	private ServletContext parentServletContext;

	private BundleContext trackingContext;
	private BundleContext consumingContext;

	// BEGIN of old HttpService support
	private Map<HttpContext, HttpContextHelperFactory> httpContextHelperFactories =
		Collections.synchronizedMap(new HashMap<HttpContext, HttpContextHelperFactory>());
	private Map<Object, HttpServiceObjectRegistration> legacyMappings =
		Collections.synchronizedMap(new HashMap<Object, HttpServiceObjectRegistration>());
	private Map<Bundle, Set<HttpServiceObjectRegistration>> bundleRegistrations =
		new HashMap<Bundle, Set<HttpServiceObjectRegistration>>();
	private Map<Bundle, Map<String, String>> bundleAliasCustomizations = new HashMap<Bundle, Map<String,String>>();
	// END of old HttpService support

	private ConcurrentMap<ServiceReference<ServletContextHelper>, ContextController> controllerMap =
		new ConcurrentHashMap<ServiceReference<ServletContextHelper>, ContextController>();

	private AtomicLong legacyIdGenerator = new AtomicLong(0);

	private Set<Object> registeredObjects = Collections.newSetFromMap(new ConcurrentHashMap<Object, Boolean>());

	private ServiceTracker<ServletContextHelper, AtomicReference<ContextController>> contextServiceTracker;
	private ServiceTracker<ContextPathCustomizer, ContextPathCustomizer> contextPathAdaptorTracker;
	private ContextPathCustomizerHolder contextPathCustomizerHolder;

	static class DefaultServletContextHelperFactory implements ServiceFactory<ServletContextHelper> {
		@Override
		public ServletContextHelper getService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> registration) {
			return new DefaultServletContextHelper(bundle);
		}

		@Override
		public void ungetService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> registration,
			ServletContextHelper service) {
			// do nothing
		}
	}

	static class DefaultServletContextHelper extends ServletContextHelper {
		public DefaultServletContextHelper(Bundle b) {
			super(b);
		}
	}

	static class LegacyServiceObject {
		final AtomicReference<Exception> error = new AtomicReference<Exception>(new ServletException("The init() method was never called.")); //$NON-NLS-1$
		public void checkForError() {
			Exception result = error.get();
			if (result != null) {
				HttpServiceImpl.unchecked(result);
			}
		}
	}

	public static class LegacyFilterFactory extends LegacyServiceObject implements PrototypeServiceFactory<Filter> {
		final Filter filter;

		public LegacyFilterFactory(Filter filter) {
			this.filter = filter;
		}

		@Override
		public Filter getService(Bundle bundle, ServiceRegistration<Filter> registration) {
			return new LegacyFilter();
		}

		@Override
		public void ungetService(
			Bundle bundle, ServiceRegistration<Filter> registration, Filter service) {
			// do nothing
		}

		// NOTE we do not do the same equals check here for filter that we do for servlet
		// this is because we must allow filter to be applied to all context helpers
		// TODO this means it is still possible that init() will get called if the same filter
		// is registered multiple times.  This is unfortunate but is an error case on the client anyway.
		class LegacyFilter implements Filter {
			/**
			 * @throws ServletException
			 */
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {
				try {
					filter.init(filterConfig);
					error.set(null);
				} catch (Exception e){
					error.set(e);
					HttpServiceImpl.unchecked(e);
				}
			}

			@Override
			public void doFilter(
				ServletRequest request, ServletResponse response, FilterChain chain)
				throws IOException, ServletException {
				filter.doFilter(request, response, chain);
			}

			@Override
			public void destroy() {
				filter.destroy();
			}
		}
	}

	static class LegacyServlet extends LegacyServiceObject implements Servlet {
		final Servlet servlet;

		public LegacyServlet(Servlet servlet) {
			this.servlet = servlet;
		}

		/**
		 * @throws ServletException
		 */
		@Override
		public void init(ServletConfig config)
			throws ServletException {
			try {
				servlet.init(config);
				error.set(null);
			} catch (Exception e){
				error.set(e);
				HttpServiceImpl.unchecked(e);
			}
		}

		@Override
		public ServletConfig getServletConfig() {
			return servlet.getServletConfig();
		}

		@Override
		public void
			service(ServletRequest req, ServletResponse res)
				throws ServletException, IOException {
			servlet.service(req, res);
		}

		@Override
		public String getServletInfo() {
			return servlet.getServletInfo();
		}

		@Override
		public void destroy() {
			servlet.destroy();
		}

		@Override
		public int hashCode() {
			return servlet.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof LegacyServlet) {
				other = ((LegacyServlet) other).servlet;
			}
			return servlet.equals(other);
		}
	}

	static class ContextPathCustomizerHolder implements ServiceTrackerCustomizer<ContextPathCustomizer, ContextPathCustomizer> {
		private final BundleContext context;
		private final ServiceTracker<ServletContextHelper, AtomicReference<ContextController>> contextServiceTracker;
		private final NavigableMap<ServiceReference<ContextPathCustomizer>, ContextPathCustomizer> pathCustomizers =
			new TreeMap<ServiceReference<ContextPathCustomizer>, ContextPathCustomizer>(Collections.reverseOrder());

		public ContextPathCustomizerHolder(
			BundleContext context,
			ServiceTracker<ServletContextHelper, AtomicReference<ContextController>> contextServiceTracker) {
			super();
			this.context = context;
			this.contextServiceTracker = contextServiceTracker;
		}

		@Override
		public ContextPathCustomizer addingService(
			ServiceReference<ContextPathCustomizer> reference) {
			ContextPathCustomizer service = context.getService(reference);
			boolean reset = false;
			synchronized (pathCustomizers) {
				pathCustomizers.put(reference, service);
				reset = pathCustomizers.firstKey().equals(reference);
			}
			if (reset) {
				contextServiceTracker.close();
				contextServiceTracker.open();
			}
			return service;
		}

		@Override
		public void modifiedService(
			ServiceReference<ContextPathCustomizer> reference,
			ContextPathCustomizer service) {
			removedService(reference, service);
			addingService(reference);
		}
		@Override
		public void removedService(
			ServiceReference<ContextPathCustomizer> reference,
			ContextPathCustomizer service) {
			boolean reset = false;
			synchronized (pathCustomizers) {
				ServiceReference<ContextPathCustomizer> currentFirst = pathCustomizers.firstKey();
				pathCustomizers.remove(reference);
				reset = currentFirst.equals(reference);
			}

			// only reset if the tracker is still open
			if (reset && contextServiceTracker.getTrackingCount() >= 0) {

				contextServiceTracker.close();
				contextServiceTracker.open();
			}
			context.ungetService(reference);
		}

		ContextPathCustomizer getHighestRanked() {
			synchronized (pathCustomizers) {
				Map.Entry<ServiceReference<ContextPathCustomizer>, ContextPathCustomizer> firstEntry = pathCustomizers.firstEntry();
				return firstEntry == null ? null : firstEntry.getValue();
			}
		}
	}

}
/* @generated */