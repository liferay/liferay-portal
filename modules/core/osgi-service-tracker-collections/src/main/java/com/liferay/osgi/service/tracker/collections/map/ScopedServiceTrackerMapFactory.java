/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.service.tracker.collections.map;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;

/**
 * @author Carlos Sierra Andrés
 */
public class ScopedServiceTrackerMapFactory<T> {

	public static <T> ScopedServiceTrackerMap<T> create(
		BundleContext bundleContext, Class<T> clazz, String property,
		String filterString, Supplier<T> defaultServiceSupplier) {

		return create(
			bundleContext, clazz, property, filterString,
			defaultServiceSupplier,
			() -> {
			});
	}

	public static <T> ScopedServiceTrackerMap<T> create(
		BundleContext bundleContext, Class<T> clazz, String property,
		String filterString, Supplier<T> defaultServiceSupplier,
		Consumer<String> onChangeConsumer) {

		return new ScopedServiceTrackerMapImpl<>(
			bundleContext, clazz, property, filterString,
			defaultServiceSupplier, onChangeConsumer);
	}

	public static <T> ScopedServiceTrackerMap<T> create(
		BundleContext bundleContext, Class<T> clazz, String property,
		String filterString, Supplier<T> defaultServiceSupplier,
		Runnable onChangeRunnable) {

		return create(
			bundleContext, clazz, property, filterString,
			defaultServiceSupplier, key -> onChangeRunnable.run());
	}

	public static <T> ScopedServiceTrackerMap<T> create(
		BundleContext bundleContext, Class<T> clazz, String property,
		Supplier<T> defaultServiceSupplier) {

		return create(
			bundleContext, clazz, property, "", defaultServiceSupplier,
			() -> {
			});
	}

	private static class ScopedServiceTrackerMapImpl<T>
		implements ScopedServiceTrackerMap<T> {

		@Override
		public void close() {
			_servicesByCompany.close();
			_servicesByCompanyAndKey.close();
			_servicesByKey.close();
		}

		@Override
		public T getService(long companyId, String key) {
			String companyIdString = String.valueOf(companyId);

			List<T> services = _servicesByCompanyAndKey.getService(
				companyIdString + "-" + key);

			if ((services != null) && !services.isEmpty()) {
				return services.get(0);
			}

			services = _servicesByKey.getService(key);

			if ((services != null) && !services.isEmpty()) {
				return services.get(0);
			}

			services = _servicesByCompany.getService(companyIdString);

			if ((services != null) && !services.isEmpty()) {
				return services.get(0);
			}

			return _defaultServiceSupplier.get();
		}

		private ScopedServiceTrackerMapImpl(
			BundleContext bundleContext, Class<T> clazz, String property,
			String filterString, Supplier<T> defaultServiceSupplier,
			Consumer<String> onChangeConsumer) {

			_defaultServiceSupplier = defaultServiceSupplier;
			_onChangeConsumer = onChangeConsumer;

			_servicesByCompany = ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, clazz,
				"(&(companyId=*)(!(" + property + "=*))" + filterString + ")",
				new PropertyServiceReferenceMapper<>("companyId"),
				new ServiceTrackerMapListenerImpl(key -> null));
			_servicesByCompanyAndKey =
				ServiceTrackerMapFactory.openMultiValueMap(
					bundleContext, clazz,
					"(&(companyId=*)(" + property + "=*)" + filterString + ")",
					(serviceReference, emitter) -> {
						ServiceReferenceMapper<String, T> companyMapper =
							new PropertyServiceReferenceMapper<>("companyId");
						ServiceReferenceMapper<String, T> nameMapper =
							new PropertyServiceReferenceMapper<>(property);

						companyMapper.map(
							serviceReference,
							key1 -> nameMapper.map(
								serviceReference,
								key2 -> emitter.emit(key1 + "-" + key2)));
					},
					new ServiceTrackerMapListenerImpl(
						key -> key.substring(key.indexOf('-') + 1)));
			_servicesByKey = ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, clazz,
				"(&(" + property + "=*)(|(!(companyId=*))(companyId=0))" +
					filterString + ")",
				new PropertyServiceReferenceMapper<>(property),
				new ServiceTrackerMapListenerImpl(key -> key));
		}

		private final Supplier<T> _defaultServiceSupplier;
		private final Consumer<String> _onChangeConsumer;
		private final ServiceTrackerMap<String, List<T>> _servicesByCompany;
		private final ServiceTrackerMap<String, List<T>>
			_servicesByCompanyAndKey;
		private final ServiceTrackerMap<String, List<T>> _servicesByKey;

		private class ServiceTrackerMapListenerImpl
			implements ServiceTrackerMapListener<String, T, List<T>> {

			@Override
			public void keyEmitted(
				ServiceTrackerMap<String, List<T>> serviceTrackerMap,
				String key, T service, List<T> content) {

				_onChangeConsumer.accept(_keyFunction.apply(key));
			}

			@Override
			public void keyRemoved(
				ServiceTrackerMap<String, List<T>> serviceTrackerMap,
				String key, T service, List<T> content) {

				_onChangeConsumer.accept(_keyFunction.apply(key));
			}

			private ServiceTrackerMapListenerImpl(
				Function<String, String> keyFunction) {

				_keyFunction = keyFunction;
			}

			private final Function<String, String> _keyFunction;

		}

	}

}