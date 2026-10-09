/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order.importer.type;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterType;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterTypeRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceOrderImporterTypeRegistry.class)
public class CommerceOrderImporterTypeRegistryImpl
	implements CommerceOrderImporterTypeRegistry {

	@Override
	public CommerceOrderImporterType getCommerceOrderImporterType(String key) {
		ServiceWrapper<CommerceOrderImporterType>
			commerceOrderImporterTypeServiceWrapper =
				_serviceTrackerMap.getService(key);

		if (commerceOrderImporterTypeServiceWrapper == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No commerce order importer type registered with key " +
						key);
			}

			return null;
		}

		return commerceOrderImporterTypeServiceWrapper.getService();
	}

	@Override
	public List<CommerceOrderImporterType> getCommerceOrderImporterTypes(
			CommerceOrder commerceOrder)
		throws PortalException {

		List<CommerceOrderImporterType> commerceOrderImporterTypes =
			new ArrayList<>();

		List<ServiceWrapper<CommerceOrderImporterType>>
			commerceOrderImporterTypeServiceWrappers = ListUtil.fromCollection(
				_serviceTrackerMap.values());

		for (ServiceWrapper<CommerceOrderImporterType>
				commerceOrderImporterTypeServiceWrapper :
					commerceOrderImporterTypeServiceWrappers) {

			CommerceOrderImporterType commerceOrderImporterType =
				commerceOrderImporterTypeServiceWrapper.getService();

			if (!commerceOrderImporterType.isActive(commerceOrder)) {
				continue;
			}

			commerceOrderImporterTypes.add(commerceOrderImporterType);
		}

		return Collections.unmodifiableList(commerceOrderImporterTypes);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, CommerceOrderImporterType.class,
			"commerce.order.importer.type.key",
			ServiceTrackerCustomizerFactory.
				<CommerceOrderImporterType>serviceWrapper(bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderImporterTypeRegistryImpl.class);

	private ServiceTrackerMap<String, ServiceWrapper<CommerceOrderImporterType>>
		_serviceTrackerMap;

}