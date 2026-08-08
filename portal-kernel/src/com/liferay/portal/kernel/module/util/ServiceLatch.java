/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.module.util;

import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Shuyang Zhou
 */
public class ServiceLatch {

	public ServiceLatch(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	public void openOn(Consumer<BundleContext> consumer) {
		openOn(() -> consumer.accept(_bundleContext));
	}

	public void openOn(Runnable runnable) {
		_openRunnable = runnable;

		for (ServiceTracker<?, ?> serviceTracker : _serviceTrackers) {
			serviceTracker.open();
		}
	}

	public <S> ServiceLatch waitFor(Class<S> serviceClass) {
		return waitFor(
			serviceClass,
			s -> {
			});
	}

	public <S> ServiceLatch waitFor(
		Class<S> serviceClass, Consumer<S> serviceConsumer) {

		_serviceTrackers.add(
			new ServiceTracker<>(
				_bundleContext, serviceClass,
				new CapturingServiceTrackerCustomizer<>(serviceConsumer)));

		_serviceTrackersCount.incrementAndGet();

		return this;
	}

	public <S> ServiceLatch waitFor(String filterString) {
		return waitFor(
			filterString,
			s -> {
			});
	}

	public <S> ServiceLatch waitFor(
		String filterString, Consumer<S> serviceConsumer) {

		try {
			_serviceTrackers.add(
				new ServiceTracker<>(
					_bundleContext, _bundleContext.createFilter(filterString),
					new CapturingServiceTrackerCustomizer<>(serviceConsumer)));

			_serviceTrackersCount.incrementAndGet();
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new RuntimeException(invalidSyntaxException);
		}

		return this;
	}

	private final BundleContext _bundleContext;
	private Runnable _openRunnable;
	private final Queue<ServiceTracker<?, ?>> _serviceTrackers =
		new ConcurrentLinkedQueue<>();
	private final AtomicInteger _serviceTrackersCount = new AtomicInteger();

	private class CapturingServiceTrackerCustomizer<S>
		implements ServiceTrackerCustomizer<S, S> {

		@Override
		public S addingService(ServiceReference<S> serviceReference) {
			S service = _bundleContext.getService(serviceReference);

			_serviceConsumer.accept(service);

			if (_serviceTrackersCount.decrementAndGet() == 0) {
				_openRunnable.run();

				DependencyManagerSyncUtil.registerSyncCallable(
					() -> {
						for (ServiceTracker<?, ?> serviceTracker :
								_serviceTrackers) {

							serviceTracker.close();
						}

						return null;
					});
			}

			return service;
		}

		@Override
		public void modifiedService(
			ServiceReference<S> serviceReference, S service) {
		}

		@Override
		public void removedService(
			ServiceReference<S> serviceReference, S service) {

			_bundleContext.ungetService(serviceReference);
		}

		private CapturingServiceTrackerCustomizer(Consumer<S> serviceConsumer) {
			_serviceConsumer = serviceConsumer;
		}

		private final Consumer<S> _serviceConsumer;

	}

}