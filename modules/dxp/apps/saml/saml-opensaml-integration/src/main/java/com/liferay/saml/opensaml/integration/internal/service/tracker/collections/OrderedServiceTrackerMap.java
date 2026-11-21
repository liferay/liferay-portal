/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.service.tracker.collections;

import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Stian Sigvartsen
 */
public class OrderedServiceTrackerMap<T> {

	public OrderedServiceTrackerMap(
		BundleContext bundleContext, Class<T> clazz, String propertyKey) {

		_propertyKey = propertyKey;

		_defaultServiceTracker = ServiceTrackerFactory.open(
			bundleContext,
			StringBundler.concat(
				"(&(objectClass=", clazz.getName(), ")(default=true))"));
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, clazz, propertyKey,
			ServiceTrackerCustomizerFactory.serviceReferenceServiceTuple(
				bundleContext));
	}

	public void close() {
		_serviceTrackerMap.close();
	}

	public String getDefaultServiceKey() {
		ServiceReference<T> serviceReference =
			_defaultServiceTracker.getServiceReference();

		if (serviceReference == null) {
			return StringPool.BLANK;
		}

		return GetterUtil.getString(serviceReference.getProperty(_propertyKey));
	}

	public List<Map.Entry<String, T>> getOrderedServices() {
		Set<String> prefixes = _serviceTrackerMap.keySet();

		List<ServiceReferenceServiceTuple<T, T>> serviceReferenceServiceTuples =
			TransformUtil.transform(prefixes, _serviceTrackerMap::getService);

		Collections.sort(
			serviceReferenceServiceTuples,
			Comparator.comparing(
				serviceReferenceServiceTuple -> {
					ServiceReference<?> serviceReference =
						serviceReferenceServiceTuple.getServiceReference();

					return GetterUtil.getInteger(
						serviceReference.getProperty("display.index"));
				}));

		return TransformUtil.transform(
			serviceReferenceServiceTuples,
			serviceReferenceServiceTuple -> {
				ServiceReference<T> serviceReference =
					serviceReferenceServiceTuple.getServiceReference();

				return new AbstractMap.SimpleEntry<String, T>(
					(String)serviceReference.getProperty(_propertyKey),
					serviceReferenceServiceTuple.getService());
			});
	}

	public List<String> getOrderedServicesKeys() {
		List<ServiceReference<T>> serviceReferences = TransformUtil.transform(
			_serviceTrackerMap.keySet(),
			prefix -> {
				ServiceReferenceServiceTuple<T, T>
					serviceReferenceServiceTuple =
						_serviceTrackerMap.getService(prefix);

				return serviceReferenceServiceTuple.getServiceReference();
			});

		Collections.sort(
			serviceReferences,
			Comparator.comparing(
				serviceReference -> GetterUtil.getInteger(
					serviceReference.getProperty("display.index"))));

		return TransformUtil.transform(
			serviceReferences,
			serviceReference -> GetterUtil.getString(
				serviceReference.getProperty(_propertyKey)));
	}

	public T getService(String key) {
		ServiceReferenceServiceTuple<T, T> serviceReferenceServiceTuple =
			_serviceTrackerMap.getService(key);

		if (serviceReferenceServiceTuple != null) {
			return serviceReferenceServiceTuple.getService();
		}

		return _defaultServiceTracker.getService();
	}

	public Set<String> getServicesKeys() {
		return _serviceTrackerMap.keySet();
	}

	private final ServiceTracker<T, T> _defaultServiceTracker;
	private final String _propertyKey;
	private final ServiceTrackerMap<String, ServiceReferenceServiceTuple<T, T>>
		_serviceTrackerMap;

}