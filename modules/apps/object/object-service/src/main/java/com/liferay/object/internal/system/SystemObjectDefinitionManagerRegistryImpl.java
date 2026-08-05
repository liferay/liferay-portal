/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system;

import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Rodrigo Paulino
 */
@Component(service = SystemObjectDefinitionManagerRegistry.class)
public class SystemObjectDefinitionManagerRegistryImpl
	implements SystemObjectDefinitionManagerRegistry {

	@Override
	public SystemObjectDefinitionManager getSystemObjectDefinitionManager(
		String name) {

		return _serviceTrackerMap.getService(name);
	}

	@Override
	public Collection<SystemObjectDefinitionManager>
		getSystemObjectDefinitionManagers() {

		return _serviceTrackerMap.values();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SystemObjectDefinitionManager.class, null,
			ServiceReferenceMapperFactory.createFromFunction(
				bundleContext, SystemObjectDefinitionManager::getName));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, SystemObjectDefinitionManager>
		_serviceTrackerMap;

}