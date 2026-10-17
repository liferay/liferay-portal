/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.module.service;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 */
public class Snapshot<T> {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> cast(Class<?> clazz) {
		return (Class<T>)clazz;
	}

	public Snapshot(Class<?> holderClass, Class<T> serviceClass) {
		this(holderClass, serviceClass, null);
	}

	public Snapshot(
		Class<?> holderClass, Class<T> serviceClass, String filterString) {

		this(holderClass, serviceClass, filterString, false);
	}

	public Snapshot(
		Class<?> holderClass, Class<T> serviceClass, String filterString,
		boolean dynamic) {

		if (dynamic) {
			DCLSingleton<ServiceTracker<T, T>> serviceTrackerDCLSingleton =
				new DCLSingleton<>();

			Supplier<ServiceTracker<T, T>> serviceTrackerSupplier = () -> {
				ServiceTracker<T, T> serviceTracker = null;

				BundleContext bundleContext = _getBundleContext(holderClass);

				if (filterString == null) {
					serviceTracker = new ServiceTracker<>(
						bundleContext, serviceClass);
				}
				else {
					try {
						serviceTracker = new ServiceTracker<>(
							bundleContext,
							bundleContext.createFilter(
								StringBundler.concat(
									"(&(objectClass=", serviceClass.getName(),
									")", filterString, ")")));
					}
					catch (InvalidSyntaxException invalidSyntaxException) {
						return ReflectionUtil.throwException(
							invalidSyntaxException);
					}
				}

				serviceTracker.open();

				return serviceTracker;
			};

			_serviceSupplier = () -> {
				ServiceTracker<T, T> serviceTracker =
					serviceTrackerDCLSingleton.getSingleton(
						serviceTrackerSupplier);

				BundleContext bundleContext = serviceTracker.getContext();

				try {
					bundleContext.getBundle();
				}
				catch (IllegalStateException illegalStateException) {
					serviceTrackerDCLSingleton.destroy(null);

					serviceTracker = serviceTrackerDCLSingleton.getSingleton(
						serviceTrackerSupplier);
				}

				return serviceTracker.getService();
			};
		}
		else {
			DCLSingleton<T> serviceDCLSingleton = new DCLSingleton<>();

			_serviceSupplier = () -> serviceDCLSingleton.getSingleton(
				() -> {
					BundleContext bundleContext = _getBundleContext(
						holderClass);

					ServiceReference<T> serviceReference = _getServiceReference(
						bundleContext, serviceClass, filterString);

					if (serviceReference == null) {
						return null;
					}

					try {
						bundleContext.addServiceListener(
							new ServiceListener() {

								@Override
								public void serviceChanged(
									ServiceEvent serviceEvent) {

									if (serviceEvent.getType() ==
											ServiceEvent.UNREGISTERING) {

										serviceDCLSingleton.destroy(null);

										bundleContext.removeServiceListener(
											this);
									}
								}

							},
							StringBundler.concat(
								"(", Constants.SERVICE_ID, "=",
								serviceReference.getProperty(
									Constants.SERVICE_ID),
								")"));
					}
					catch (InvalidSyntaxException invalidSyntaxException) {
						return ReflectionUtil.throwException(
							invalidSyntaxException);
					}

					return bundleContext.getService(serviceReference);
				});
		}
	}

	public T get() {
		return _serviceSupplier.get();
	}

	private BundleContext _getBundleContext(Class<?> holderClass) {
		Bundle bundle = FrameworkUtil.getBundle(holderClass);

		if (bundle == null) {
			return SystemBundleUtil.getBundleContext();
		}

		return bundle.getBundleContext();
	}

	private ServiceReference<T> _getServiceReference(
		BundleContext bundleContext, Class<T> serviceClass,
		String filterString) {

		if (filterString == null) {
			return bundleContext.getServiceReference(serviceClass);
		}

		try {
			Collection<ServiceReference<T>> serviceReferences =
				bundleContext.getServiceReferences(serviceClass, filterString);

			if (serviceReferences.isEmpty()) {
				return null;
			}

			Iterator<ServiceReference<T>> iterator =
				serviceReferences.iterator();

			return iterator.next();
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			return ReflectionUtil.throwException(invalidSyntaxException);
		}
	}

	private final Supplier<T> _serviceSupplier;

	private static class ServiceTracker<S, T>
		extends org.osgi.util.tracker.ServiceTracker<S, T> {

		@Override
		public T addingService(ServiceReference<S> serviceReference) {
			T service = super.addingService(serviceReference);

			_count.incrementAndGet();

			return service;
		}

		public BundleContext getContext() {
			return context;
		}

		@Override
		public T getService() {
			if (_count.get() == 0) {
				return null;
			}

			return super.getService();
		}

		@Override
		public void removedService(
			ServiceReference<S> serviceReference, T service) {

			_count.decrementAndGet();

			super.removedService(serviceReference, service);
		}

		private ServiceTracker(BundleContext bundleContext, Class<S> clazz) {
			super(bundleContext, clazz, null);
		}

		private ServiceTracker(BundleContext bundleContext, Filter filter) {
			super(bundleContext, filter, null);
		}

		private final AtomicInteger _count = new AtomicInteger();

	}

}