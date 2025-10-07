/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cache;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tina Tian
 */
public class PortalCacheManagerProvider {

	public static PortalCacheManager<? extends Serializable, ?>
		getPortalCacheManager(String portalCacheManagerName) {

		return _dynamicPortalCacheManagers.computeIfAbsent(
			portalCacheManagerName,
			key -> new DynamicPortalCacheManager<>(key));
	}

	public static Collection<PortalCacheManager<? extends Serializable, ?>>
		getPortalCacheManagers() {

		return Collections.unmodifiableCollection(
			_dynamicPortalCacheManagers.values());
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final Map
		<String, DynamicPortalCacheManager<? extends Serializable, ?>>
			_dynamicPortalCacheManagers = new ConcurrentHashMap<>();
	private static final ServiceTracker
		<PortalCacheManager<? extends Serializable, ?>,
		 DynamicPortalCacheManager<? extends Serializable, ?>> _serviceTracker;

	private static class PortalCacheProviderServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<PortalCacheManager<? extends Serializable, ?>,
			 DynamicPortalCacheManager<? extends Serializable, ?>> {

		@Override
		public DynamicPortalCacheManager<? extends Serializable, ?>
			addingService(
				ServiceReference<PortalCacheManager<? extends Serializable, ?>>
					serviceReference) {

			PortalCacheManager<? extends Serializable, ?> portalCacheManager =
				_bundleContext.getService(serviceReference);

			DynamicPortalCacheManager<? extends Serializable, ?>
				dynamicPortalCacheManager =
					_dynamicPortalCacheManagers.computeIfAbsent(
						portalCacheManager.getPortalCacheManagerName(),
						key -> new DynamicPortalCacheManager<>(key));

			dynamicPortalCacheManager.setPortalCacheManager(portalCacheManager);

			return dynamicPortalCacheManager;
		}

		@Override
		public void modifiedService(
			ServiceReference<PortalCacheManager<? extends Serializable, ?>>
				serviceReference,
			DynamicPortalCacheManager<? extends Serializable, ?>
				dynamicPortalCacheManager) {
		}

		@Override
		public void removedService(
			ServiceReference<PortalCacheManager<? extends Serializable, ?>>
				serviceReference,
			DynamicPortalCacheManager<? extends Serializable, ?>
				dynamicPortalCacheManager) {

			_bundleContext.ungetService(serviceReference);

			dynamicPortalCacheManager.setPortalCacheManager(null);
		}

	}

	static {
		_serviceTracker = new ServiceTracker<>(
			_bundleContext,
			(Class<PortalCacheManager<? extends Serializable, ?>>)
				(Class<?>)PortalCacheManager.class,
			new PortalCacheProviderServiceTrackerCustomizer());

		_serviceTracker.open();
	}

}