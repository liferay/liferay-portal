/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.entry.type.registry;

import com.liferay.launch.entry.type.LaunchEntryType;
import com.liferay.launch.entry.type.registry.LaunchEntryTypeRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alejandro Tardín
 */
@Component(service = LaunchEntryTypeRegistry.class)
public class LaunchEntryTypeRegistryImpl implements LaunchEntryTypeRegistry {

	@Override
	public LaunchEntryType getLaunchEntryType(String className) {
		return _serviceTrackerMap.getService(className);
	}

	@Override
	public List<LaunchEntryType> getLaunchEntryTypes() {
		return new ArrayList<>(_serviceTrackerMap.values());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, LaunchEntryType.class,
			"launch.entry.type.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, LaunchEntryType> _serviceTrackerMap;

}