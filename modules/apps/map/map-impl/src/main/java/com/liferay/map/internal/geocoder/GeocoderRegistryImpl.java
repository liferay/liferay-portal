/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.internal.geocoder;

import com.liferay.map.geocoder.Geocoder;
import com.liferay.map.geocoder.GeocoderRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Carolina Barbosa
 */
@Component(service = GeocoderRegistry.class)
public class GeocoderRegistryImpl implements GeocoderRegistry {

	@Override
	public Geocoder getGeocoder(String key) {
		return _serviceTrackerMap.getService(key);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, Geocoder.class, "geocoder.key");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, Geocoder> _serviceTrackerMap;

}