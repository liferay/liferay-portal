/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context.osgi.util.tracker;

import com.liferay.osgi.util.StringPlus;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServletEndpointController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ServletContextHelperDataContext;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ResourceRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServiceHolder;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ResourceServlet;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ServletConfigImpl;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ServletContextWrapper;

import jakarta.servlet.ServletException;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Dante Wang
 */
public class ResourceServiceTrackerCustomizer
	extends BaseServiceTrackerCustomizer
		<Object, ResourceRegistration, AtomicReference<ResourceRegistration>> {

	public ResourceServiceTrackerCustomizer(
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
	public AtomicReference<ResourceRegistration> addingService(
		ServiceReference<Object> serviceReference) {

		if (Objects.isNull(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX)) &&
			Objects.isNull(
				serviceReference.getProperty(
					HttpWhiteboardConstants.
						HTTP_WHITEBOARD_RESOURCE_PATTERN))) {

			return null;
		}

		if (!liferayContextController.matches(serviceReference) ||
			!httpServletEndpointController.matches(serviceReference)) {

			return null;
		}

		AtomicReference<ResourceRegistration>
			resourceRegistrationAtomicReference = new AtomicReference<>();

		try {
			resourceRegistrationAtomicReference.set(
				_addResourceRegistration(serviceReference));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return resourceRegistrationAtomicReference;
	}

	private ResourceRegistration _addResourceRegistration(
		ServiceReference<?> serviceReference) {

		liferayContextController.checkShutdown();

		String resourcePrefix = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX);

		if (resourcePrefix == null) {
			throw new IllegalArgumentException("Prefix is null");
		}

		if (resourcePrefix.endsWith(StringPool.SLASH) &&
			!resourcePrefix.equals(StringPool.SLASH)) {

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
			checkPattern(resourcePattern);
		}

		Bundle bundle = serviceReference.getBundle();

		ResourceDTO resourceDTO = new ResourceDTO();

		resourceDTO.patterns = sort(resourcePatterns);
		resourceDTO.prefix = resourcePrefix;
		resourceDTO.serviceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		resourceDTO.servletContextId = servletContextHelperServiceId;

		ServletContextHelper servletContextHelper =
			liferayContextController.getServletContextHelper(bundle);

		ResourceRegistration resourceRegistration = new ResourceRegistration(
			liferayContextController, resourceDTO,
			new ServiceHolder<>(
				bundle,
				new ResourceServlet(resourcePrefix, servletContextHelper),
				resourceDTO.serviceId,
				GetterUtil.getInteger(
					serviceReference.getProperty(Constants.SERVICE_RANKING))),
			servletContextHelper);

		try {
			resourceRegistration.init(
				new ServletConfigImpl(
					new HashMap<>(),
					new ServletContextWrapper(
						bundle, liferayContextController, servletContextHelper,
						servletContextHelperDataContext),
					resourceRegistration.getName()));
		}
		catch (ServletException servletException) {
			if (_log.isDebugEnabled()) {
				_log.debug(servletException);
			}

			return null;
		}

		Set<EndpointRegistration<?>> endpointRegistrations =
			liferayContextController.getEndpointRegistrations();

		endpointRegistrations.add(resourceRegistration);

		return resourceRegistration;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceServiceTrackerCustomizer.class);

}