/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.service.tracker.collections.internal.map;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.internal.ServiceTrackerManager;
import com.liferay.osgi.service.tracker.collections.internal.ServiceTrackerUtil;
import com.liferay.osgi.service.tracker.collections.map.KeyedServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerBucket;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerBucketFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
public class ServiceTrackerMapImpl<K, SR, TS, R>
	implements ServiceTrackerMap<K, R> {

	public ServiceTrackerMapImpl(
		BundleContext bundleContext, Class<SR> clazz, String filterString,
		ServiceReferenceMapper<K, ? super SR> serviceReferenceMapper,
		ServiceTrackerCustomizer<SR, TS> serviceTrackerCustomizer,
		ServiceTrackerBucketFactory<SR, TS, R> serviceTrackerMapBucketFactory,
		ServiceTrackerMapListener<K, TS, R> serviceTrackerMapListener) {

		_serviceReferenceMapper = serviceReferenceMapper;
		_serviceTrackerCustomizer = serviceTrackerCustomizer;
		_serviceTrackerMapBucketFactory = serviceTrackerMapBucketFactory;
		_serviceTrackerMapListener = serviceTrackerMapListener;

		_serviceTracker = ServiceTrackerUtil.createServiceTracker(
			bundleContext, clazz, filterString,
			new ServiceReferenceServiceTrackerCustomizer());

		boolean trackAllServices = false;

		if (clazz == null) {
			trackAllServices = true;
		}

		_serviceTrackerManager = new ServiceTrackerManager(
			_serviceTracker, trackAllServices);

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
	public boolean containsKey(K key) {
		_serviceTrackerManager.open();

		return _serviceTrackerBuckets.containsKey(key);
	}

	@Override
	public R getService(K key) {
		_serviceTrackerManager.open();

		ServiceTrackerBucket<SR, TS, R> serviceTrackerBucket =
			_serviceTrackerBuckets.get(key);

		if (serviceTrackerBucket == null) {
			return null;
		}

		return serviceTrackerBucket.getContent();
	}

	@Override
	public Set<K> keySet() {
		_serviceTrackerManager.open();

		return Collections.unmodifiableSet(_serviceTrackerBuckets.keySet());
	}

	@Override
	public Collection<R> values() {
		_serviceTrackerManager.open();

		return Collections.unmodifiableCollection(_getServices());
	}

	private Collection<R> _getServices() {
		Collection<R> services = new ArrayList<>();

		for (ServiceTrackerBucket<SR, TS, R> serviceTrackerBucket :
				_serviceTrackerBuckets.values()) {

			services.add(serviceTrackerBucket.getContent());
		}

		return services;
	}

	private void _removeKeys(
		KeyedServiceReferenceServiceTuple<SR, TS, K>
			keyedServiceReferenceServiceTuple) {

		List<K> emittedKeys =
			keyedServiceReferenceServiceTuple.getEmittedKeys();

		for (K emittedKey : emittedKeys) {
			ServiceTrackerBucket<SR, TS, R> serviceTrackerBucket =
				_serviceTrackerBuckets.get(emittedKey);

			if (serviceTrackerBucket == null) {
				continue;
			}

			serviceTrackerBucket.remove(keyedServiceReferenceServiceTuple);

			if (serviceTrackerBucket.isDisposable()) {
				_serviceTrackerBuckets.remove(emittedKey);
			}

			if (_serviceTrackerMapListener == null) {
				continue;
			}

			_serviceTrackerMapListener.keyRemoved(
				ServiceTrackerMapImpl.this, emittedKey,
				keyedServiceReferenceServiceTuple.getService(),
				serviceTrackerBucket.getContent());
		}

		emittedKeys.clear();
	}

	private void _storeKey(
		K key,
		KeyedServiceReferenceServiceTuple<SR, TS, K>
			keyedServiceReferenceServiceTuple) {

		ServiceTrackerBucket<SR, TS, R> serviceTrackerBucket =
			_serviceTrackerBuckets.get(key);

		if (serviceTrackerBucket == null) {
			ServiceTrackerBucket<SR, TS, R> newServiceTrackerBucket =
				_serviceTrackerMapBucketFactory.create();

			serviceTrackerBucket = _serviceTrackerBuckets.putIfAbsent(
				key, newServiceTrackerBucket);

			if (serviceTrackerBucket == null) {
				serviceTrackerBucket = newServiceTrackerBucket;
			}
		}

		serviceTrackerBucket.store(keyedServiceReferenceServiceTuple);

		keyedServiceReferenceServiceTuple.addEmittedKey(key);
	}

	private final ServiceReferenceMapper<K, ? super SR> _serviceReferenceMapper;
	private final ServiceTracker
		<SR, KeyedServiceReferenceServiceTuple<SR, TS, K>> _serviceTracker;
	private final ConcurrentMap<K, ServiceTrackerBucket<SR, TS, R>>
		_serviceTrackerBuckets = new ConcurrentHashMap<>();
	private final ServiceTrackerCustomizer<SR, TS> _serviceTrackerCustomizer;
	private final ServiceTrackerManager _serviceTrackerManager;
	private final ServiceTrackerBucketFactory<SR, TS, R>
		_serviceTrackerMapBucketFactory;
	private final ServiceTrackerMapListener<K, TS, R>
		_serviceTrackerMapListener;

	private class DefaultEmitter implements ServiceReferenceMapper.Emitter<K> {

		public DefaultEmitter(ServiceReference<SR> serviceReference) {
			_serviceReference = serviceReference;
		}

		@Override
		public void emit(K key) {
			if (_keyedServiceReferenceServiceTuple == null) {
				if (!_invokedServiceTrackerCustomizer) {
					TS service = _serviceTrackerCustomizer.addingService(
						_serviceReference);

					_invokedServiceTrackerCustomizer = true;

					if (service == null) {
						return;
					}

					_keyedServiceReferenceServiceTuple =
						new KeyedServiceReferenceServiceTuple<>(
							_serviceReference, service);
				}
				else {
					return;
				}
			}

			_storeKey(key, _keyedServiceReferenceServiceTuple);

			if (_serviceTrackerMapListener != null) {
				ServiceTrackerBucket<SR, TS, R> serviceTrackerBucket =
					_serviceTrackerBuckets.get(key);

				_serviceTrackerMapListener.keyEmitted(
					ServiceTrackerMapImpl.this, key,
					_keyedServiceReferenceServiceTuple.getService(),
					serviceTrackerBucket.getContent());
			}
		}

		public KeyedServiceReferenceServiceTuple<SR, TS, K>
			getServiceReferenceServiceTuple() {

			return _keyedServiceReferenceServiceTuple;
		}

		private boolean _invokedServiceTrackerCustomizer;
		private KeyedServiceReferenceServiceTuple<SR, TS, K>
			_keyedServiceReferenceServiceTuple;
		private final ServiceReference<SR> _serviceReference;

	}

	private class ServiceReferenceServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<SR, KeyedServiceReferenceServiceTuple<SR, TS, K>> {

		@Override
		@SuppressWarnings({"rawtypes", "unchecked"})
		public KeyedServiceReferenceServiceTuple<SR, TS, K> addingService(
			ServiceReference<SR> serviceReference) {

			DefaultEmitter defaultEmitter = new DefaultEmitter(
				serviceReference);

			_serviceReferenceMapper.map(
				(ServiceReference)serviceReference, defaultEmitter);

			return defaultEmitter.getServiceReferenceServiceTuple();
		}

		@Override
		@SuppressWarnings({"rawtypes", "unchecked"})
		public void modifiedService(
			ServiceReference<SR> serviceReference,
			final KeyedServiceReferenceServiceTuple<SR, TS, K>
				keyedServiceReferenceServiceTuple) {

			_removeKeys(keyedServiceReferenceServiceTuple);

			_serviceTrackerCustomizer.modifiedService(
				serviceReference,
				keyedServiceReferenceServiceTuple.getService());

			_serviceReferenceMapper.map(
				(ServiceReference)serviceReference,
				new ServiceReferenceMapper.Emitter<K>() {

					@Override
					public void emit(K key) {
						_storeKey(key, keyedServiceReferenceServiceTuple);
					}

				});
		}

		@Override
		public void removedService(
			ServiceReference<SR> serviceReference,
			KeyedServiceReferenceServiceTuple<SR, TS, K>
				keyedServiceReferenceServiceTuple) {

			_removeKeys(keyedServiceReferenceServiceTuple);

			_serviceTrackerCustomizer.removedService(
				serviceReference,
				keyedServiceReferenceServiceTuple.getService());
		}

	}

}