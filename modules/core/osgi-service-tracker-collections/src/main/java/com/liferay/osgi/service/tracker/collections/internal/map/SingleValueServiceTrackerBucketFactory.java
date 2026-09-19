/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.service.tracker.collections.internal.map;

import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.internal.ServiceReferenceServiceTupleComparator;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerBucket;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerBucketFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.osgi.framework.ServiceReference;

/**
 * @author Carlos Sierra Andrés
 */
public class SingleValueServiceTrackerBucketFactory<SR, TS>
	implements ServiceTrackerBucketFactory<SR, TS, TS> {

	public SingleValueServiceTrackerBucketFactory() {
		_comparator = Collections.reverseOrder();
	}

	public SingleValueServiceTrackerBucketFactory(
		Comparator<ServiceReference<SR>> comparator) {

		_comparator = comparator;
	}

	@Override
	public ServiceTrackerBucket<SR, TS, TS> create() {
		return new SingleBucket();
	}

	private final Comparator<ServiceReference<SR>> _comparator;

	private class SingleBucket implements ServiceTrackerBucket<SR, TS, TS> {

		@Override
		public TS getContent() {
			return _service;
		}

		@Override
		public synchronized boolean isDisposable() {
			return _serviceReferences.isEmpty();
		}

		@Override
		public synchronized void remove(
			ServiceReferenceServiceTuple<SR, TS> serviceReferenceServiceTuple) {

			_serviceReferences.remove(serviceReferenceServiceTuple);

			if (_serviceReferences.isEmpty()) {
				_service = null;
			}
			else {
				ServiceReferenceServiceTuple<SR, TS>
					headServiceReferenceServiceTuple = _serviceReferences.get(
						0);

				_service = headServiceReferenceServiceTuple.getService();
			}
		}

		@Override
		public synchronized void store(
			ServiceReferenceServiceTuple<SR, TS> serviceReferenceServiceTuple) {

			int index = Collections.binarySearch(
				_serviceReferences, serviceReferenceServiceTuple,
				_serviceReferenceServiceTupleComparator);

			if (index < 0) {
				index = -index - 1;
			}

			_serviceReferences.add(index, serviceReferenceServiceTuple);

			ServiceReferenceServiceTuple<SR, TS>
				headServiceReferenceServiceTuple = _serviceReferences.get(0);

			_service = headServiceReferenceServiceTuple.getService();
		}

		private TS _service;
		private final ServiceReferenceServiceTupleComparator<SR>
			_serviceReferenceServiceTupleComparator =
				new ServiceReferenceServiceTupleComparator<>(_comparator);
		private final List<ServiceReferenceServiceTuple<SR, TS>>
			_serviceReferences = new ArrayList<>(1);

	}

}