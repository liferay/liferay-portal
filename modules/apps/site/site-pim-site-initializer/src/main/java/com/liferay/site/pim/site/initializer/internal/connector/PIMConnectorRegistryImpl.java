/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Andrea Sbarra
 */
@Component(service = PIMConnectorRegistry.class)
public class PIMConnectorRegistryImpl implements PIMConnectorRegistry {

	@Override
	public PIMConnector getPIMConnector(String key) {
		return _serviceTrackerMap.getService(key);
	}

	@Override
	public List<PIMConnector> getPIMConnectors(long companyId) {
		return ListUtil.fromCollection(_serviceTrackerMap.values());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, PIMConnector.class, null,
			(serviceReference, emitter) -> {
				PIMConnector pimConnector = bundleContext.getService(
					serviceReference);

				try {
					if (Validator.isNotNull(pimConnector.getKey())) {
						emitter.emit(pimConnector.getKey());
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

	private ServiceTrackerMap<String, PIMConnector> _serviceTrackerMap;

}