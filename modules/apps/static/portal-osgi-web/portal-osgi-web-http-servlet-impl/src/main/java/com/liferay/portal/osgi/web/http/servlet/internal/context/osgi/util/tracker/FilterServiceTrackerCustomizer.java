/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context.osgi.util.tracker;

import com.liferay.osgi.util.StringPlus;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServletEndpointController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ServletContextHelperDataContext;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.FilterRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServiceHolder;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.FilterConfigImpl;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ServletContextWrapper;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServicePropertiesUtil;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class FilterServiceTrackerCustomizer
	extends BaseServiceTrackerCustomizer
		<Filter, FilterRegistration, AtomicReference<FilterRegistration>> {

	public FilterServiceTrackerCustomizer(
		BundleContext bundleContext,
		HttpServletEndpointController httpServletEndpointController,
		LiferayContextController liferayContextController,
		ServletContextHelperDataContext servletContextHelperDataContext,
		long servletContextHelperServiceId) {

		super(
			bundleContext, httpServletEndpointController,
			liferayContextController, servletContextHelperDataContext,
			servletContextHelperServiceId);
	}

	@Override
	public AtomicReference<FilterRegistration> addingService(
		ServiceReference<Filter> serviceReference) {

		if (Objects.isNull(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN)) &&
			Objects.isNull(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX)) &&
			Objects.isNull(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET))) {

			return null;
		}

		if (!liferayContextController.matches(serviceReference) ||
			!httpServletEndpointController.matches(serviceReference)) {

			return null;
		}

		AtomicReference<FilterRegistration> filterRegistrationAtomicReference =
			new AtomicReference<>();

		try {
			filterRegistrationAtomicReference.set(
				_addFilterRegistration(serviceReference));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return filterRegistrationAtomicReference;
	}

	private FilterRegistration _addFilterRegistration(
			ServiceReference<Filter> serviceReference)
		throws Exception {

		liferayContextController.checkShutdown();

		ServiceHolder<Filter> serviceHolder = new ServiceHolder<>(
			bundleContext.getServiceObjects(serviceReference));

		Filter filter = serviceHolder.get();

		FilterRegistration filterRegistration = null;

		boolean addedRegisteredObject = false;

		Set<Object> registeredObjects =
			httpServletEndpointController.getRegisteredObjects();

		try {
			if (filter == null) {
				throw new IllegalArgumentException("Filter is null");
			}

			addedRegisteredObject = registeredObjects.add(filter);

			if (addedRegisteredObject) {
				Set<FilterRegistration> filterRegistrations =
					liferayContextController.getFilterRegistrations();

				for (FilterRegistration curFilterRegistration :
						filterRegistrations) {

					if (Objects.equals(
							curFilterRegistration.getService(), filter)) {

						throw new ServletException(
							"Filter " + filter + " is already registered");
					}
				}

				FilterDTO filterDTO = _createFilterDTO(
					filter, serviceReference);

				filterRegistration = new FilterRegistration(
					filterDTO, liferayContextController,
					GetterUtil.getInteger(
						serviceReference.getProperty(
							Constants.SERVICE_RANKING)),
					serviceHolder);

				filterRegistration.init(
					new FilterConfigImpl(
						filterDTO.name, filterDTO.initParams,
						new ServletContextWrapper(
							serviceHolder.getBundle(), liferayContextController,
							liferayContextController.getServletContextHelper(
								serviceHolder.getBundle()),
							servletContextHelperDataContext)));

				filterRegistrations.add(filterRegistration);
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

	private FilterDTO _createFilterDTO(
		Filter filter, ServiceReference<Filter> serviceReference) {

		String[] filterPatterns = ArrayUtil.toStringArray(
			StringPlus.asList(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN)));
		String[] filterRegexes = ArrayUtil.toStringArray(
			StringPlus.asList(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX)));
		String[] filterServlets = ArrayUtil.toStringArray(
			StringPlus.asList(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET)));

		if ((filterPatterns.length == 0) && (filterRegexes.length == 0) &&
			(filterServlets.length == 0)) {

			throw new IllegalArgumentException(
				"Patterns, regex, and servlet names must contain a value");
		}

		for (String filterPattern : filterPatterns) {
			checkPattern(filterPattern);
		}

		String[] filterDispatcherTypes = ArrayUtil.toStringArray(
			StringPlus.asList(
				serviceReference.getProperty(
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

		filterDTO.asyncSupported = GetterUtil.getBoolean(
			serviceReference.getProperty(
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED));
		filterDTO.dispatcher = sort(filterDispatcherTypes);
		filterDTO.initParams = ServicePropertiesUtil.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX);

		Class<?> filterClass = filter.getClass();

		filterDTO.name = GetterUtil.getString(
			ServicePropertiesUtil.parseName(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME),
				filter),
			filterClass.getName());

		filterDTO.patterns = sort(filterPatterns);
		filterDTO.regexs = filterRegexes;
		filterDTO.serviceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		filterDTO.servletContextId = servletContextHelperServiceId;
		filterDTO.servletNames = sort(filterServlets);

		return filterDTO;
	}

	private static final String[] _DISPATCHER_TYPES = {
		DispatcherType.REQUEST.toString()
	};

	private static final Log _log = LogFactoryUtil.getLog(
		FilterServiceTrackerCustomizer.class.getName());

}