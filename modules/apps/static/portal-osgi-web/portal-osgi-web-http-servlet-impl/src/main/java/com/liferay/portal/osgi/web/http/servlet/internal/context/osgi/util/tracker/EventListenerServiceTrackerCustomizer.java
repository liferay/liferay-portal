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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServletEndpointController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ServletContextHelperDataContext;
import com.liferay.portal.osgi.web.http.servlet.internal.exception.HttpWhiteboardFailureException;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EventListenerRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EventListeners;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServiceHolder;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ServletContextWrapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.ListenerDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Dante Wang
 */
public class EventListenerServiceTrackerCustomizer<S extends EventListener>
	extends BaseServiceTrackerCustomizer
		<S, EventListenerRegistration,
		 AtomicReference<EventListenerRegistration>> {

	public EventListenerServiceTrackerCustomizer(
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
	public AtomicReference<EventListenerRegistration> addingService(
		ServiceReference<S> serviceReference) {

		Object listenerObject = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER);

		if ((listenerObject == null) ||
			!liferayContextController.matches(serviceReference) ||
			!httpServletEndpointController.matches(serviceReference)) {

			return null;
		}

		AtomicReference<EventListenerRegistration>
			listenerRegistrationAtomicReference = new AtomicReference<>();

		try {
			if (!(listenerObject instanceof Boolean) &&
				!(listenerObject instanceof String)) {

				throw new HttpWhiteboardFailureException(
					StringBundler.concat(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER,
						StringPool.EQUAL, listenerObject, " is not valid"),
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
			}

			if (!GetterUtil.getBoolean(listenerObject)) {
				return listenerRegistrationAtomicReference;
			}

			listenerRegistrationAtomicReference.set(
				_addListenerRegistration(serviceReference));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return listenerRegistrationAtomicReference;
	}

	private EventListenerRegistration _addListenerRegistration(
		ServiceReference<S> serviceReference) {

		liferayContextController.checkShutdown();

		ServiceHolder<S> serviceHolder = new ServiceHolder<>(
			bundleContext.getServiceObjects(serviceReference));

		EventListener eventListener = serviceHolder.get();

		EventListenerRegistration listenerRegistration = null;

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

			Set<EventListenerRegistration> listenerRegistrations =
				liferayContextController.getListenerRegistrations();

			for (EventListenerRegistration curListenerRegistration :
					listenerRegistrations) {

				if (Objects.equals(
						curListenerRegistration.getService(), eventListener)) {

					return null;
				}
			}

			ServletContext servletContext = new ServletContextWrapper(
				serviceHolder.getBundle(), liferayContextController,
				liferayContextController.getServletContextHelper(
					serviceHolder.getBundle()),
				servletContextHelperDataContext);

			listenerRegistration = new EventListenerRegistration(
				eventListenerClasses, liferayContextController,
				_createListenerDTO(eventListenerClasses, serviceReference),
				(ServiceHolder<EventListener>)serviceHolder, servletContext);

			if (eventListenerClasses.contains(ServletContextListener.class)) {
				ServletContextListener servletContextListener =
					(ServletContextListener)listenerRegistration.getService();

				servletContextListener.contextInitialized(
					new ServletContextEvent(servletContext));
			}

			listenerRegistrations.add(listenerRegistration);

			EventListeners eventListeners =
				liferayContextController.getEventListeners();

			eventListeners.put(eventListenerClasses, listenerRegistration);
		}
		finally {
			if (listenerRegistration == null) {
				serviceHolder.release();
			}
		}

		return listenerRegistration;
	}

	private ListenerDTO _createListenerDTO(
		List<Class<? extends EventListener>> eventListenerClasses,
		ServiceReference<S> serviceReference) {

		ListenerDTO listenerDTO = new ListenerDTO();

		listenerDTO.serviceId = (long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		listenerDTO.servletContextId = servletContextHelperServiceId;
		listenerDTO.types = TransformUtil.transformToArray(
			eventListenerClasses, Class::getName, String.class);

		return listenerDTO;
	}

	private List<Class<? extends EventListener>> _getEventListenerClasses(
		ServiceReference<S> serviceReference) {

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

	private static final Log _log = LogFactoryUtil.getLog(
		EventListenerServiceTrackerCustomizer.class.getName());

}