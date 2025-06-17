/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ServletContextHelperDataContextImpl;

import jakarta.servlet.ServletContext;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.equinox.http.servlet.internal.HttpServletEndpointController;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.error.IllegalContextNameException;
import org.eclipse.equinox.http.servlet.internal.error.IllegalContextPathException;
import org.eclipse.equinox.http.servlet.internal.servlet.Match;
import org.eclipse.equinox.http.servlet.internal.util.Const;
import org.eclipse.equinox.http.servlet.internal.util.Path;
import org.eclipse.equinox.http.servlet.internal.util.StringPlus;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntimeConstants;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Dante Wang
 */
public class HttpServletEndpointControllerImpl
	implements HttpServletEndpointController {

	public HttpServletEndpointControllerImpl(
		Map<String, Object> attributesMap, BundleContext bundleContext,
		ServletContext parentServletContext) {

		_attributesMap = attributesMap;
		_bundleContext = bundleContext;
		_parentServletContext = parentServletContext;

		File parentServletContextTempDir =
			(File)parentServletContext.getAttribute(
				JavaConstants.JAKARTA_SERVLET_CONTEXT_TEMPDIR);

		if (parentServletContextTempDir != null) {
			parentServletContextTempDir = new File(
				parentServletContextTempDir,
				HttpServletEndpointController.class.getName() + hashCode());

			_parentServletContextTempDir = parentServletContextTempDir;

			_parentServletContextTempDir.mkdirs();
		}
		else {
			_parentServletContextTempDir = null;
		}

		_contextControllers = ServiceTrackerListFactory.open(
			bundleContext, ServletContextHelper.class, null,
			new ServletContextHelperServiceTrackerCustomizer());

		_serviceRegistration = bundleContext.registerService(
			ServletContextHelper.class,
			new DefaultServletContextHelperFactory(),
			HashMapDictionaryBuilder.<String, Object>put(
				Constants.SERVICE_RANKING, Integer.MIN_VALUE
			).put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME
			).put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
				Const.SLASH
			).put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET,
				"(http.servlet.endpoint.id=" +
					attributesMap.get("http.servlet.endpoint.id") + ")"
			).build());
	}

	@Override
	public void destroy() {
		_serviceRegistration.unregister();

		_contextControllers.close();
	}

	@Override
	public DispatchTargets getDispatchTargets(String pathString) {
		Path path = new Path(pathString);

		String requestURI = path.getRequestURI();

		List<ContextController> contextControllers = _getContextControllers(
			requestURI);

		if (ListUtil.isEmpty(contextControllers)) {
			return null;
		}

		String queryString = path.getQueryString();

		DispatchTargets dispatchTargets = _getDispatchTargets(
			contextControllers, requestURI, null, queryString, Match.EXACT);

		if (dispatchTargets == null) {
			dispatchTargets = _getDispatchTargets(
				contextControllers, requestURI, path.getExtension(),
				queryString, Match.EXTENSION);
		}

		if (dispatchTargets == null) {
			dispatchTargets = _getDispatchTargets(
				contextControllers, requestURI, null, queryString, Match.REGEX);
		}

		if (dispatchTargets == null) {
			dispatchTargets = _getDispatchTargets(
				contextControllers, requestURI, null, queryString,
				Match.DEFAULT_SERVLET);
		}

		return dispatchTargets;
	}

	@Override
	public List<String> getHttpServiceEndpoints() {
		return StringPlus.from(
			_attributesMap.get(
				HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT));
	}

	@Override
	public ServletContext getParentServletContext() {
		return _parentServletContext;
	}

	@Override
	public Set<Object> getRegisteredObjects() {
		return _registeredObjects;
	}

	@Override
	public void log(String message, Throwable throwable) {
		_log.error(message, throwable);
	}

	@Override
	public boolean matches(ServiceReference<?> serviceReference) {
		String target = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET);

		if (target == null) {
			return true;
		}

		try {
			Filter targetFilter = FrameworkUtil.createFilter(target);

			if (targetFilter.matches(_attributesMap)) {
				return true;
			}
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new IllegalArgumentException(invalidSyntaxException);
		}

		return false;
	}

	private List<ContextController> _getContextControllers(String requestURI) {
		int index = requestURI.lastIndexOf('/');

		while (true) {
			List<ContextController> contextControllers = new ArrayList<>();

			for (ContextController contextController : _contextControllers) {
				if (Objects.equals(
						contextController.getContextPath(), requestURI)) {

					contextControllers.add(contextController);
				}
			}

			if (!contextControllers.isEmpty()) {
				return contextControllers;
			}

			if (index == -1) {
				break;
			}

			requestURI = requestURI.substring(0, index);

			index = requestURI.lastIndexOf('/');
		}

		return null;
	}

	private DispatchTargets _getDispatchTargets(
		List<ContextController> contextControllers, String requestURI,
		String extension, String queryString, Match match) {

		ContextController firstContextController = contextControllers.get(0);

		String contextPath = firstContextController.getContextPath();

		requestURI = requestURI.substring(contextPath.length());

		int index = requestURI.lastIndexOf('/');

		String servletPath = requestURI;

		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = StringPool.SLASH;
		}

		while (true) {
			for (ContextController contextController : contextControllers) {
				DispatchTargets dispatchTargets =
					contextController.getDispatchTargets(
						null, requestURI, servletPath, pathInfo, extension,
						queryString, match);

				if (dispatchTargets != null) {
					return dispatchTargets;
				}
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

	private static final Log _log = LogFactoryUtil.getLog(
		HttpServletEndpointControllerImpl.class.getName());

	private final Map<String, Object> _attributesMap;
	private final BundleContext _bundleContext;
	private final ServiceTrackerList<ContextController> _contextControllers;
	private final ServletContext _parentServletContext;
	private final File _parentServletContextTempDir;
	private final Set<Object> _registeredObjects = Collections.newSetFromMap(
		new ConcurrentHashMap<>());
	private final ServiceRegistration<ServletContextHelper>
		_serviceRegistration;

	private static class DefaultServletContextHelperFactory
		implements ServiceFactory<ServletContextHelper> {

		@Override
		public ServletContextHelper getService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> serviceRegistration) {

			return new ServletContextHelper(bundle) {
			};
		}

		@Override
		public void ungetService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> serviceRegistration,
			ServletContextHelper servletContextHelper) {
		}

	}

	private class ServletContextHelperServiceTrackerCustomizer
		implements EagerServiceTrackerCustomizer
			<ServletContextHelper, ContextController> {

		@Override
		public ContextController addingService(
			ServiceReference<ServletContextHelper> serviceReference) {

			if (!matches(serviceReference)) {
				return null;
			}

			String contextName = (String)serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME);
			String contextPath = (String)serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH);

			try {
				if (contextName == null) {
					throw new IllegalContextNameException(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +
							" is null",
						DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
				}

				if (contextPath == null) {
					throw new IllegalContextPathException(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH +
							" is null",
						DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
				}

				return new LiferayContextController(
					_bundleContext, serviceReference,
					new ServletContextHelperDataContextImpl(
						contextName, _parentServletContext,
						_parentServletContextTempDir),
					HttpServletEndpointControllerImpl.this, contextName,
					contextPath);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContextHelper> serviceReference,
			ContextController contextController) {
		}

		@Override
		public void removedService(
			ServiceReference<ServletContextHelper> serviceReference,
			ContextController contextController) {

			contextController.destroy();
		}

	}

}