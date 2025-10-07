/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context.osgi.util.tracker;

import com.liferay.osgi.util.StringPlus;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServletEndpointController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ServletContextHelperDataContext;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServiceHolder;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServletRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ServletConfigImpl;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ServletContextWrapper;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServicePropertiesUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Dante Wang
 */
public class ServletServiceTrackerCustomizer
	extends BaseServiceTrackerCustomizer
		<Servlet, ServletRegistration, AtomicReference<ServletRegistration>> {

	public ServletServiceTrackerCustomizer(
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
	public AtomicReference<ServletRegistration> addingService(
		ServiceReference<Servlet> serviceReference) {

		if (Objects.isNull(
				serviceReference.getProperty(
					HttpWhiteboardConstants.
						HTTP_WHITEBOARD_SERVLET_ERROR_PAGE)) &&
			Objects.isNull(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME)) &&
			Objects.isNull(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN))) {

			return null;
		}

		if (!liferayContextController.matches(serviceReference) ||
			!httpServletEndpointController.matches(serviceReference)) {

			return null;
		}

		AtomicReference<ServletRegistration>
			servletRegistrationAtomicReference = new AtomicReference<>();

		try {
			servletRegistrationAtomicReference.set(
				_addServletRegistration(serviceReference));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return servletRegistrationAtomicReference;
	}

	private ServletRegistration _addServletRegistration(
			ServiceReference<Servlet> serviceReference)
		throws ServletException {

		liferayContextController.checkShutdown();

		ServiceHolder<Servlet> serviceHolder = new ServiceHolder<>(
			bundleContext.getServiceObjects(serviceReference));

		Servlet servlet = serviceHolder.get();

		ServletRegistration servletRegistration = null;

		boolean addedRegisteredObject = false;

		Set<Object> registeredObjects =
			httpServletEndpointController.getRegisteredObjects();

		try {
			if (servlet == null) {
				throw new IllegalArgumentException("Servlet is null");
			}

			addedRegisteredObject = registeredObjects.add(servlet);

			if (addedRegisteredObject) {
				ObjectValuePair<ServletDTO, ErrorPageDTO> objectValuePair =
					_createServletDTOs(serviceReference, servlet);

				ServletDTO servletDTO = objectValuePair.getKey();

				ServletContextHelper servletContextHelper =
					liferayContextController.getServletContextHelper(
						serviceHolder.getBundle());

				servletRegistration = new ServletRegistration(
					objectValuePair.getValue(), liferayContextController,
					serviceHolder, servletContextHelper, servletDTO);

				servletRegistration.init(
					new ServletConfigImpl(
						servletDTO.initParams,
						new ServletContextWrapper(
							serviceHolder.getBundle(), liferayContextController,
							servletContextHelper,
							servletContextHelperDataContext),
						servletDTO.name));

				Set<EndpointRegistration<?>> endpointRegistrations =
					liferayContextController.getEndpointRegistrations();

				endpointRegistrations.add(servletRegistration);
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
					StringPool.COMMA_AND_SPACE,
					HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
					", and ",
					HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
					" must contain a value"));
		}

		for (String servletPattern : servletPatterns) {
			checkPattern(servletPattern);
		}

		ServletDTO servletDTO = new ServletDTO();

		servletDTO.asyncSupported = GetterUtil.getBoolean(
			serviceReference.getProperty(
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED));
		servletDTO.initParams = ServicePropertiesUtil.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX);
		servletDTO.name = ServicePropertiesUtil.parseName(servletName, servlet);
		servletDTO.patterns = sort(servletPatterns);
		servletDTO.serviceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		servletDTO.servletContextId = servletContextHelperServiceId;
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
			errorPageDTO.servletContextId = servletContextHelperServiceId;
			errorPageDTO.servletInfo = servlet.getServletInfo();
		}

		return new ObjectValuePair<>(servletDTO, errorPageDTO);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServletServiceTrackerCustomizer.class);

}