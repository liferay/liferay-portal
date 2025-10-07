/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServletEndpointController;
import com.liferay.portal.osgi.web.http.servlet.internal.Match;
import com.liferay.portal.osgi.web.http.servlet.internal.context.osgi.util.tracker.EventListenerServiceTrackerCustomizer;
import com.liferay.portal.osgi.web.http.servlet.internal.context.osgi.util.tracker.FilterServiceTrackerCustomizer;
import com.liferay.portal.osgi.web.http.servlet.internal.context.osgi.util.tracker.ResourceServiceTrackerCustomizer;
import com.liferay.portal.osgi.web.http.servlet.internal.context.osgi.util.tracker.ServletServiceTrackerCustomizer;
import com.liferay.portal.osgi.web.http.servlet.internal.exception.IllegalContextNameException;
import com.liferay.portal.osgi.web.http.servlet.internal.exception.IllegalContextPathException;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EventListenerRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EventListeners;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.FilterRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ResourceRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServletRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.HttpSessionWrapper;
import com.liferay.portal.osgi.web.http.servlet.internal.util.Path;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServicePropertiesUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Dante Wang
 */
public class LiferayContextController {

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
			new URI("http", "localhost", contextPath, null);
		}
		catch (URISyntaxException uriSyntaxException) {
			IllegalContextPathException illegalContextPathException =
				new IllegalContextPathException(
					"The context path \"" + contextPath + "\" is invalid",
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);

			illegalContextPathException.addSuppressed(uriSyntaxException);

			throw illegalContextPathException;
		}

		_serviceReference = serviceReference;
		_servletContextHelperDataContext = servletContextHelperDataContext;
		_httpServletEndpointController = httpServletEndpointController;
		_contextName = contextName;

		if (contextPath.equals(StringPool.SLASH)) {
			contextPath = StringPool.BLANK;
		}

		_contextPath = contextPath;

		_servletContextInitParams = ServicePropertiesUtil.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX,
			servletContextHelperDataContext.getServletContext());

		long servletContextHelperServiceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);

		_filterServiceTracker = new ServiceTracker<>(
			bundleContext, Filter.class,
			new FilterServiceTrackerCustomizer(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_filterServiceTracker.open(true);

		_httpSessionAttributeListenerServiceTracker = new ServiceTracker<>(
			bundleContext, HttpSessionAttributeListener.class,
			new EventListenerServiceTrackerCustomizer<>(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_httpSessionAttributeListenerServiceTracker.open(true);

		_httpSessionListenerServiceTracker = new ServiceTracker<>(
			bundleContext, HttpSessionListener.class,
			new EventListenerServiceTrackerCustomizer<>(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_httpSessionListenerServiceTracker.open(true);

		_resourceServiceTracker = new ServiceTracker<>(
			bundleContext, Object.class,
			new ResourceServiceTrackerCustomizer(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_resourceServiceTracker.open(true);

		_servletContextAttributeListenerServiceTracker = new ServiceTracker<>(
			bundleContext, ServletContextAttributeListener.class,
			new EventListenerServiceTrackerCustomizer<>(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_servletContextAttributeListenerServiceTracker.open(true);

		_servletContextListenerServiceTracker = new ServiceTracker<>(
			bundleContext, ServletContextListener.class,
			new EventListenerServiceTrackerCustomizer<>(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_servletContextListenerServiceTracker.open(true);

		_servletRequestAttributeListenerServiceTracker = new ServiceTracker<>(
			bundleContext, ServletRequestAttributeListener.class,
			new EventListenerServiceTrackerCustomizer<>(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_servletRequestAttributeListenerServiceTracker.open(true);

		_servletRequestListenerServiceTracker = new ServiceTracker<>(
			bundleContext, ServletRequestListener.class,
			new EventListenerServiceTrackerCustomizer<>(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_servletRequestListenerServiceTracker.open(true);

		_servletServiceTracker = new ServiceTracker<>(
			bundleContext, Servlet.class,
			new ServletServiceTrackerCustomizer(
				bundleContext, httpServletEndpointController, this,
				_servletContextHelperDataContext,
				servletContextHelperServiceId));

		_servletServiceTracker.open(true);
	}

	public void checkShutdown() {
		if (_shutdown) {
			throw new IllegalStateException("Context is shutdown");
		}
	}

	public void destroy() {
		Collection<HttpSessionWrapper> httpSessionWrappers =
			_activeHttpSessionWrappersMap.values();

		Iterator<HttpSessionWrapper> iterator = httpSessionWrappers.iterator();

		while (iterator.hasNext()) {
			HttpSessionWrapper httpSessionWrapper = iterator.next();

			httpSessionWrapper.invalidate();

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
		_eventListenerRegistrations.clear();
		_servletContextHelperDataContext.destroy();

		_shutdown = true;
	}

	public Map<String, HttpSessionWrapper> getActiveSessions() {
		return _activeHttpSessionWrappersMap;
	}

	public String getContextName() {
		return _contextName;
	}

	public String getContextPath() {
		return _contextPath;
	}

	public LiferayDispatchTargets getDispatchTargets(String pathString) {
		Path path = new Path(pathString);

		String queryString = path.getQueryString();
		String requestURI = path.getRequestURI();

		LiferayDispatchTargets liferayDispatchTargets =
			_getLiferayDispatchTargets(
				requestURI, null, queryString, Match.EXACT);

		if (liferayDispatchTargets == null) {
			liferayDispatchTargets = _getLiferayDispatchTargets(
				requestURI, path.getExtension(), queryString, Match.EXTENSION);
		}

		if (liferayDispatchTargets == null) {
			liferayDispatchTargets = _getLiferayDispatchTargets(
				requestURI, null, queryString, Match.REGEX);
		}

		if (liferayDispatchTargets == null) {
			liferayDispatchTargets = _getLiferayDispatchTargets(
				requestURI, null, queryString, Match.DEFAULT_SERVLET);
		}

		return liferayDispatchTargets;
	}

	public LiferayDispatchTargets getDispatchTargets(
		String servletName, String requestURI, String servletPath,
		String pathInfo, String extension, String queryString, Match match) {

		checkShutdown();

		EndpointRegistration<?> endpointRegistration = null;

		for (EndpointRegistration<?> curEndpointRegistration :
				_endpointRegistrations) {

			if (Objects.nonNull(
					curEndpointRegistration.match(
						extension, match, servletName, pathInfo,
						servletPath))) {

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
			return new LiferayDispatchTargets(
				endpointRegistration, this, pathInfo, queryString, requestURI,
				servletName, servletPath);
		}

		if (requestURI != null) {
			int index = requestURI.lastIndexOf(CharPool.PERIOD);

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
						extension, endpointRegistrationName, requestURI)) &&
				!matchingFilterRegistrations.contains(filterRegistration)) {

				matchingFilterRegistrations.add(filterRegistration);
			}
		}

		return new LiferayDispatchTargets(
			endpointRegistration, this, matchingFilterRegistrations, pathInfo,
			queryString, requestURI, servletName, servletPath);
	}

	public Set<EndpointRegistration<?>> getEndpointRegistrations() {
		return _endpointRegistrations;
	}

	public EventListeners getEventListeners() {
		return _eventListeners;
	}

	public Set<FilterRegistration> getFilterRegistrations() {
		return _filterRegistrations;
	}

	public String getFullContextPath() {
		List<String> httpServiceEndpoints =
			_httpServletEndpointController.getHttpServiceEndpoints();

		if (httpServiceEndpoints.isEmpty()) {
			return _contextPath;
		}

		String defaultHttpServiceEndpoint = httpServiceEndpoints.get(0);

		if (defaultHttpServiceEndpoint.endsWith(StringPool.SLASH)) {
			defaultHttpServiceEndpoint = defaultHttpServiceEndpoint.substring(
				0, defaultHttpServiceEndpoint.length() - 1);
		}

		return defaultHttpServiceEndpoint.concat(_contextPath);
	}

	public HttpServletEndpointController getHttpServletEndpointController() {
		return _httpServletEndpointController;
	}

	public HttpSessionWrapper getHttpSessionWrapper(
		HttpSession httpSession, ServletContext servletContext) {

		String sessionId = httpSession.getId();

		HttpSessionWrapper httpSessionAdaptor =
			_activeHttpSessionWrappersMap.get(sessionId);

		if (httpSessionAdaptor != null) {
			return httpSessionAdaptor;
		}

		httpSessionAdaptor = HttpSessionWrapper.createHttpSessionWrapper(
			httpSession, this, servletContext);

		HttpSessionWrapper previousHttpSessionAdaptor =
			_activeHttpSessionWrappersMap.putIfAbsent(
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

	public Map<String, String> getInitParams() {
		return _servletContextInitParams;
	}

	public Set<EventListenerRegistration> getListenerRegistrations() {
		return _eventListenerRegistrations;
	}

	public ServletContextHelper getServletContextHelper(Bundle bundle) {
		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.getService(_serviceReference);
	}

	public boolean matches(org.osgi.framework.Filter osgiFilter) {
		return osgiFilter.match(_serviceReference);
	}

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

		if (!contextSelect.startsWith(StringPool.OPEN_PARENTHESIS)) {
			return false;
		}

		try {
			return matches(FrameworkUtil.createFilter(contextSelect));
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new IllegalArgumentException(invalidSyntaxException);
		}
	}

	public void removeActiveSession(String sessionId) {
		_activeHttpSessionWrappersMap.remove(sessionId);
	}

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

	private LiferayDispatchTargets _getLiferayDispatchTargets(
		String requestURI, String extension, String queryString, Match match) {

		int index = requestURI.lastIndexOf(CharPool.SLASH);

		String servletPath = requestURI;

		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = StringPool.SLASH;
		}

		while (true) {
			LiferayDispatchTargets liferayDispatchTargets = getDispatchTargets(
				null, requestURI, servletPath, pathInfo, extension, queryString,
				match);

			if (liferayDispatchTargets != null) {
				return liferayDispatchTargets;
			}

			if ((match == Match.EXACT) || (index == -1)) {
				break;
			}

			servletPath = requestURI.substring(0, index);

			pathInfo = requestURI.substring(index);

			index = servletPath.lastIndexOf(CharPool.SLASH);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayContextController.class.getName());

	private static final Pattern _contextNamePattern = Pattern.compile(
		"^([a-zA-Z_0-9\\-]+\\.)*[a-zA-Z_0-9\\-]+$");

	private final ConcurrentMap<String, HttpSessionWrapper>
		_activeHttpSessionWrappersMap = new ConcurrentHashMap<>();
	private final String _contextName;
	private final String _contextPath;
	private final Set<EndpointRegistration<?>> _endpointRegistrations =
		new ConcurrentSkipListSet<>();
	private final Set<EventListenerRegistration> _eventListenerRegistrations =
		new HashSet<>();
	private final EventListeners _eventListeners = new EventListeners();
	private final Set<FilterRegistration> _filterRegistrations =
		new ConcurrentSkipListSet<>();
	private final ServiceTracker<Filter, AtomicReference<FilterRegistration>>
		_filterServiceTracker;
	private final HttpServletEndpointController _httpServletEndpointController;
	private final ServiceTracker
		<HttpSessionAttributeListener,
		 AtomicReference<EventListenerRegistration>>
			_httpSessionAttributeListenerServiceTracker;
	private final ServiceTracker
		<HttpSessionListener, AtomicReference<EventListenerRegistration>>
			_httpSessionListenerServiceTracker;
	private final ServiceTracker<Object, AtomicReference<ResourceRegistration>>
		_resourceServiceTracker;
	private final ServiceReference<ServletContextHelper> _serviceReference;
	private final ServiceTracker
		<ServletContextAttributeListener,
		 AtomicReference<EventListenerRegistration>>
			_servletContextAttributeListenerServiceTracker;
	private final ServletContextHelperDataContext
		_servletContextHelperDataContext;
	private final Map<String, String> _servletContextInitParams;
	private final ServiceTracker
		<ServletContextListener, AtomicReference<EventListenerRegistration>>
			_servletContextListenerServiceTracker;
	private final ServiceTracker
		<ServletRequestAttributeListener,
		 AtomicReference<EventListenerRegistration>>
			_servletRequestAttributeListenerServiceTracker;
	private final ServiceTracker
		<ServletRequestListener, AtomicReference<EventListenerRegistration>>
			_servletRequestListenerServiceTracker;
	private final ServiceTracker<Servlet, AtomicReference<ServletRegistration>>
		_servletServiceTracker;
	private boolean _shutdown;

}