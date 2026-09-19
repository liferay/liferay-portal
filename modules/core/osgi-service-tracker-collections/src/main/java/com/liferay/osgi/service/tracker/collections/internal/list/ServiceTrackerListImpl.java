/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.service.tracker.collections.internal.list;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.internal.ServiceReferenceServiceTupleComparator;
import com.liferay.osgi.service.tracker.collections.internal.ServiceTrackerManager;
import com.liferay.osgi.service.tracker.collections.internal.ServiceTrackerUtil;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Adolfo Pérez
 */
public class ServiceTrackerListImpl<S, T> implements ServiceTrackerList<T> {

	public ServiceTrackerListImpl(
		BundleContext bundleContext, Class<S> clazz, String filterString,
		ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer,
		Comparator<ServiceReference<S>> comparator) {

		_bundleContext = bundleContext;
		_serviceTrackerCustomizer = serviceTrackerCustomizer;

		if (comparator == null) {
			_comparator = Collections.reverseOrder();
		}
		else {
			_comparator = new ServiceReferenceServiceTupleComparator<>(
				comparator);
		}

		_serviceTracker = ServiceTrackerUtil.createServiceTracker(
			_bundleContext, clazz, filterString,
			new ServiceReferenceServiceTrackerCustomizer());

		_serviceTrackerManager = new ServiceTrackerManager(
			_serviceTracker, false);

		if (_serviceTrackerCustomizer instanceof
				EagerServiceTrackerCustomizer) {

			_serviceTrackerManager.open();
		}
	}

	@Override
	public void close() {
		_serviceTrackerManager.close();
	}

	@Override
	public void forEach(Consumer<? super T> consumer) {
		_serviceTrackerManager.open();

		_services.forEach(
			serviceReferenceServiceTuple -> consumer.accept(
				serviceReferenceServiceTuple.getService()));
	}

	@Override
	public Iterator<T> iterator() {
		_serviceTrackerManager.open();

		return new ServiceTrackerListIterator<>(_services.iterator());
	}

	@Override
	public int size() {
		_serviceTrackerManager.open();

		return _services.size();
	}

	@Override
	public <E> E[] toArray(E[] array) {
		List<T> list = toList();

		return list.toArray(array);
	}

	@Override
	public List<T> toList() {
		_serviceTrackerManager.open();

		List<T> list = new ArrayList<>(_services.size());

		_services.forEach(
			serviceReferenceServiceTuple -> list.add(
				serviceReferenceServiceTuple.getService()));

		return list;
	}

	private final BundleContext _bundleContext;
	private final Comparator<ServiceReferenceServiceTuple<S, ?>> _comparator;
	private final ServiceTracker<S, T> _serviceTracker;
	private final ServiceTrackerCustomizer<S, T> _serviceTrackerCustomizer;
	private final ServiceTrackerManager _serviceTrackerManager;
	private final List<ServiceReferenceServiceTuple<S, T>> _services =
		new CopyOnWriteArrayList<>();

	private static class ServiceTrackerListIterator<S, T>
		implements Iterator<T> {

		@Override
		public boolean hasNext() {
			return _iterator.hasNext();
		}

		@Override
		public T next() {
			ServiceReferenceServiceTuple<S, T> serviceReferenceServiceTuple =
				_iterator.next();

			return serviceReferenceServiceTuple.getService();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private ServiceTrackerListIterator(
			Iterator<ServiceReferenceServiceTuple<S, T>> iterator) {

			_iterator = iterator;
		}

		private final Iterator<ServiceReferenceServiceTuple<S, T>> _iterator;

	}

	private class ServiceReferenceServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<S, T> {

		@Override
		public T addingService(ServiceReference<S> serviceReference) {
			T service = _serviceTrackerCustomizer.addingService(
				serviceReference);

			if (service == null) {
				return null;
			}

			_update(serviceReference, service, false);

			return service;
		}

		@Override
		public void modifiedService(
			ServiceReference<S> serviceReference, T service) {

			_serviceTrackerCustomizer.modifiedService(
				serviceReference, service);

			synchronized (_services) {
				_services.sort(_comparator);
			}
		}

		@Override
		public void removedService(
			ServiceReference<S> serviceReference, T service) {

			_serviceTrackerCustomizer.removedService(serviceReference, service);

			_update(serviceReference, service, true);
		}

		private void _update(
			ServiceReference<S> serviceReference, T service, boolean remove) {

			ServiceReferenceServiceTuple<S, T> serviceReferenceServiceTuple =
				new ServiceReferenceServiceTuple<>(serviceReference, service);

			synchronized (_services) {
				int index = Collections.binarySearch(
					_services, serviceReferenceServiceTuple, _comparator);

				if (remove) {
					if (index >= 0) {
						_services.remove(index);
					}
				}
				else if (index < 0) {
					_services.add(-index - 1, serviceReferenceServiceTuple);
				}
			}
		}

	}

}