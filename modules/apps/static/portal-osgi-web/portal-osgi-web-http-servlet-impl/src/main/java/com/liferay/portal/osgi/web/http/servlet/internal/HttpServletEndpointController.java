/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.util.StringPlus;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayDispatchTargets;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ServletContextHelperDataContext;
import com.liferay.portal.osgi.web.http.servlet.internal.exception.IllegalContextNameException;
import com.liferay.portal.osgi.web.http.servlet.internal.exception.IllegalContextPathException;
import com.liferay.portal.osgi.web.http.servlet.internal.util.Path;

import jakarta.servlet.ServletContext;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
public class HttpServletEndpointController {

	public HttpServletEndpointController(
		Map<String, Object> attributesMap, BundleContext bundleContext,
		ServletContext parentServletContext) {

		_attributesMap = attributesMap;
		_bundleContext = bundleContext;
		_parentServletContext = parentServletContext;

		File parentServletContextTempDir =
			(File)parentServletContext.getAttribute(
				JavaConstants.JAKARTA_SERVLET_CONTEXT_TEMPDIR);

		if (parentServletContextTempDir != null) {
			_parentServletContextTempDir = new File(
				parentServletContextTempDir,
				HttpServletEndpointController.class.getName() + hashCode());
		}
		else {
			_parentServletContextTempDir = null;
		}

		_liferayContextControllers = ServiceTrackerListFactory.open(
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
				StringPool.SLASH
			).put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET,
				"(http.servlet.endpoint.id=" +
					attributesMap.get("http.servlet.endpoint.id") + ")"
			).build());
	}

	public void destroy() {
		_serviceRegistration.unregister();

		_liferayContextControllers.close();
	}

	public LiferayDispatchTargets getDispatchTargets(String pathString) {
		Path path = new Path(pathString);

		String requestURI = path.getRequestURI();

		List<LiferayContextController> liferayContextControllers =
			_getLiferayContextControllers(requestURI);

		if (ListUtil.isEmpty(liferayContextControllers)) {
			return null;
		}

		String queryString = path.getQueryString();

		LiferayDispatchTargets liferayDispatchTargets =
			_getLiferayDispatchTargets(
				liferayContextControllers, requestURI, null, queryString,
				Match.EXACT);

		if (liferayDispatchTargets == null) {
			liferayDispatchTargets = _getLiferayDispatchTargets(
				liferayContextControllers, requestURI, path.getExtension(),
				queryString, Match.EXTENSION);
		}

		if (liferayDispatchTargets == null) {
			liferayDispatchTargets = _getLiferayDispatchTargets(
				liferayContextControllers, requestURI, null, queryString,
				Match.REGEX);
		}

		if (liferayDispatchTargets == null) {
			liferayDispatchTargets = _getLiferayDispatchTargets(
				liferayContextControllers, requestURI, null, queryString,
				Match.DEFAULT_SERVLET);
		}

		return liferayDispatchTargets;
	}

	public List<String> getHttpServiceEndpoints() {
		return StringPlus.asList(
			_attributesMap.get(
				HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT));
	}

	public ServletContext getParentServletContext() {
		return _parentServletContext;
	}

	public Set<Object> getRegisteredObjects() {
		return _registeredObjects;
	}

	public void log(String message, Throwable throwable) {
		_log.error(message, throwable);
	}

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

	private List<LiferayContextController> _getLiferayContextControllers(
		String requestURI) {

		int index = requestURI.lastIndexOf(CharPool.SLASH);

		while (true) {
			List<LiferayContextController> liferayContextControllers =
				new ArrayList<>();

			for (LiferayContextController liferayContextController :
					_liferayContextControllers) {

				if (Objects.equals(
						liferayContextController.getContextPath(),
						requestURI)) {

					liferayContextControllers.add(liferayContextController);
				}
			}

			if (!liferayContextControllers.isEmpty()) {
				return liferayContextControllers;
			}

			if (index == -1) {
				break;
			}

			requestURI = requestURI.substring(0, index);

			index = requestURI.lastIndexOf(CharPool.SLASH);
		}

		return null;
	}

	private LiferayDispatchTargets _getLiferayDispatchTargets(
		List<LiferayContextController> liferayContextControllers,
		String requestURI, String extension, String queryString, Match match) {

		LiferayContextController firstLiferayContextController =
			liferayContextControllers.get(0);

		String contextPath = firstLiferayContextController.getContextPath();

		requestURI = requestURI.substring(contextPath.length());

		int index = requestURI.lastIndexOf(CharPool.SLASH);

		String servletPath = requestURI;

		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = StringPool.SLASH;
		}

		while (true) {
			for (LiferayContextController liferayContextController :
					liferayContextControllers) {

				LiferayDispatchTargets liferayDispatchTargets =
					liferayContextController.getDispatchTargets(
						null, requestURI, servletPath, pathInfo, extension,
						queryString, match);

				if (liferayDispatchTargets != null) {
					return liferayDispatchTargets;
				}
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
		HttpServletEndpointController.class.getName());

	private final Map<String, Object> _attributesMap;
	private final BundleContext _bundleContext;
	private final ServiceTrackerList<LiferayContextController>
		_liferayContextControllers;
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
			<ServletContextHelper, LiferayContextController> {

		@Override
		public LiferayContextController addingService(
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
					new ServletContextHelperDataContext(
						contextName, _parentServletContext,
						_parentServletContextTempDir),
					HttpServletEndpointController.this, contextName,
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
			LiferayContextController liferayContextController) {
		}

		@Override
		public void removedService(
			ServiceReference<ServletContextHelper> serviceReference,
			LiferayContextController liferayContextController) {

			liferayContextController.destroy();
		}

	}

}