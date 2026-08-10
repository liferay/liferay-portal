/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.link;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.link.PIMLinkType;
import com.liferay.site.pim.site.initializer.link.PIMLinkTypeRegistry;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Stefano Motta
 */
@Component(service = PIMLinkTypeRegistry.class)
public class PIMLinkTypeRegistryImpl implements PIMLinkTypeRegistry {

	@Override
	public PIMLinkType getPIMLinkType(String type) {
		return _serviceTrackerMap.getService(type);
	}

	@Override
	public List<PIMLinkType> getPIMLinkTypes() {
		return ListUtil.fromCollection(_serviceTrackerMap.values());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, PIMLinkType.class, null,
			(serviceReference, emitter) -> {
				PIMLinkType pimLinkType = bundleContext.getService(
					serviceReference);

				try {
					if (Validator.isNotNull(pimLinkType.getType())) {
						emitter.emit(pimLinkType.getType());
					}
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, PIMLinkType> _serviceTrackerMap;

}