/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;

import jakarta.ws.rs.core.Application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.Function;

import org.apache.aries.component.dsl.CachingServiceReference;
import org.apache.aries.jax.rs.whiteboard.internal.AriesJaxrsServiceRuntime;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Shuyang Zhou
 */
public class JAXRSServiceTracker
	implements SafeCloseable,
			   ServiceTrackerCustomizer
				   <Object, JAXRSServiceTracker.TrackedService> {

	public static boolean isApplication(
		CachingServiceReference<Object> cachingServiceReference) {

		ServiceReference<Object> serviceReference =
			cachingServiceReference.getServiceReference();

		if (!ArrayUtil.contains(
				(String[])serviceReference.getProperty(Constants.OBJECTCLASS),
				Application.class.getName())) {

			return false;
		}

		Object applicationBase = cachingServiceReference.getProperty(
			JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE);

		if (applicationBase != null) {
			return true;
		}

		return false;
	}

	public static boolean isExtension(
		CachingServiceReference<Object> cachingServiceReference) {

		return _isTrue(
			cachingServiceReference.getProperty(
				JaxrsWhiteboardConstants.JAX_RS_EXTENSION));
	}

	public static boolean isResource(
		CachingServiceReference<Object> cachingServiceReference) {

		return _isTrue(
			cachingServiceReference.getProperty(
				JaxrsWhiteboardConstants.JAX_RS_RESOURCE));
	}

	public JAXRSServiceTracker(
		Function<CachingServiceReference<Object>, Runnable>
			applicationsFunction,
		BundleContext bundleContext,
		Function<CachingServiceReference<Object>, Runnable> extensionsFunction,
		Function<CachingServiceReference<Object>, Runnable> resourcesFunction) {

		_applicationsFunction = applicationsFunction;
		_extensionsFunction = extensionsFunction;
		_resourcesFunction = resourcesFunction;

		_serviceTracker = new ServiceTracker<>(
			bundleContext,
			SystemBundleUtil.createFilter(
				"(|(osgi.jaxrs.extension=true)(osgi.jaxrs.resource=true)" +
					"(&(objectClass=jakarta.ws.rs.core.Application)" +
						"(osgi.jaxrs.application.base=*)))"),
			this);
	}

	@Override
	public synchronized TrackedService addingService(
		ServiceReference<Object> serviceReference) {

		TrackedService trackedService = new TrackedService(
			new CachingServiceReference<>(serviceReference));

		_add(trackedService);

		return trackedService;
	}

	@Override
	public void close() {
		_serviceTracker.close();
	}

	public synchronized List<CachingServiceReference<Object>>
		getDuplicateNameCachingServiceReferences() {

		List<CachingServiceReference<Object>> cachingServiceReferences =
			new ArrayList<>();

		for (PriorityQueue<TrackedService> priorityQueue :
				_priorityQueues.values()) {

			TrackedService headTrackedService = priorityQueue.peek();

			for (TrackedService trackedService : priorityQueue) {
				if (trackedService == headTrackedService) {
					continue;
				}

				cachingServiceReferences.add(
					trackedService._cachingServiceReference);
			}
		}

		return cachingServiceReferences;
	}

	@Override
	public synchronized void modifiedService(
		ServiceReference<Object> serviceReference,
		TrackedService trackedService) {

		if (_serviceTracker.getService(serviceReference) != trackedService) {
			return;
		}

		if (trackedService._cachingServiceReference.isDirty()) {
			_remove(trackedService);

			trackedService._cachingServiceReference =
				new CachingServiceReference<>(serviceReference);

			_add(trackedService);
		}
	}

	public synchronized void open(
		ServiceRegistration<JaxrsServiceRuntime>
			jaxrsServiceRuntimeServiceRegistration) {

		_jaxrsServiceRuntimeServiceRegistration =
			jaxrsServiceRuntimeServiceRegistration;

		_serviceTracker.open();
	}

	@Override
	public synchronized void removedService(
		ServiceReference<Object> serviceReference,
		TrackedService trackedService) {

		_remove(trackedService);
	}

	protected static class TrackedService {

		private TrackedService(
			CachingServiceReference<Object> cachingServiceReference) {

			_cachingServiceReference = cachingServiceReference;
		}

		private CachingServiceReference<Object> _cachingServiceReference;
		private List<Runnable> _runnables;

	}

	private static boolean _isTrue(Object value) {
		if (Objects.equals(value, Boolean.TRUE) ||
			Objects.equals(value, "true")) {

			return true;
		}

		return false;
	}

	private void _add(TrackedService trackedService) {
		PriorityQueue<TrackedService> priorityQueue =
			_priorityQueues.computeIfAbsent(
				AriesJaxrsServiceRuntime.getServiceName(
					trackedService._cachingServiceReference::getProperty),
				name -> new PriorityQueue<>(_comparator));

		TrackedService previousTrackedService = priorityQueue.peek();

		priorityQueue.add(trackedService);

		if (priorityQueue.peek() == trackedService) {
			if (previousTrackedService != null) {
				_withdraw(previousTrackedService);
			}

			_publish(trackedService);
		}

		_incrementChangeCount();
	}

	private void _incrementChangeCount() {
		ServiceReference<JaxrsServiceRuntime> serviceReference =
			_jaxrsServiceRuntimeServiceRegistration.getReference();

		HashMapDictionary<String, Object> properties =
			new HashMapDictionary<>();

		for (String key : serviceReference.getPropertyKeys()) {
			properties.put(key, serviceReference.getProperty(key));
		}

		properties.put("service.changecount", ++_changeCount);

		_jaxrsServiceRuntimeServiceRegistration.setProperties(properties);
	}

	private void _publish(TrackedService trackedService) {
		CachingServiceReference<Object> cachingServiceReference =
			trackedService._cachingServiceReference;

		List<Runnable> runnables = new ArrayList<>(3);

		trackedService._runnables = runnables;

		if (isApplication(cachingServiceReference)) {
			runnables.add(_applicationsFunction.apply(cachingServiceReference));
		}

		if (isResource(cachingServiceReference)) {
			runnables.add(_resourcesFunction.apply(cachingServiceReference));
		}

		if (isExtension(cachingServiceReference)) {
			runnables.add(_extensionsFunction.apply(cachingServiceReference));
		}
	}

	private void _remove(TrackedService trackedService) {
		String name = AriesJaxrsServiceRuntime.getServiceName(
			trackedService._cachingServiceReference::getProperty);

		PriorityQueue<TrackedService> priorityQueue = _priorityQueues.get(name);

		TrackedService previousTrackedService = priorityQueue.peek();

		priorityQueue.remove(trackedService);

		if (trackedService == previousTrackedService) {
			_withdraw(trackedService);

			TrackedService currentTrackedService = priorityQueue.peek();

			if (currentTrackedService == null) {
				_priorityQueues.remove(name);
			}
			else {
				_publish(currentTrackedService);
			}
		}

		_incrementChangeCount();
	}

	private void _withdraw(TrackedService trackedService) {
		for (Runnable runnable : trackedService._runnables) {
			runnable.run();
		}

		trackedService._runnables = null;
	}

	private static final Comparator<TrackedService> _comparator =
		Comparator.comparing(
			trackedService -> trackedService._cachingServiceReference,
			Comparator.reverseOrder());

	private final Function<CachingServiceReference<Object>, Runnable>
		_applicationsFunction;
	private long _changeCount;
	private final Function<CachingServiceReference<Object>, Runnable>
		_extensionsFunction;
	private ServiceRegistration<JaxrsServiceRuntime>
		_jaxrsServiceRuntimeServiceRegistration;
	private final Map<String, PriorityQueue<TrackedService>> _priorityQueues =
		new HashMap<>();
	private final Function<CachingServiceReference<Object>, Runnable>
		_resourcesFunction;
	private final ServiceTracker<Object, TrackedService> _serviceTracker;

}