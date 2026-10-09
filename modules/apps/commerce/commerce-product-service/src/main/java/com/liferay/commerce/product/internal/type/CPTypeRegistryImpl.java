/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.type;

import com.liferay.commerce.product.internal.type.comparator.CPTypeServiceWrapperDisplayOrderComparator;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory.ServiceWrapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Marco Leo
 */
@Component(service = CPTypeRegistry.class)
public class CPTypeRegistryImpl implements CPTypeRegistry {

	@Override
	public CPType getCPType(String name) {
		if (Validator.isNull(name)) {
			return null;
		}

		ServiceWrapper<CPType> cpTypeServiceWrapper =
			_serviceTrackerMap.getService(name);

		if (cpTypeServiceWrapper == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No commerce product type registered with name " + name);
			}

			return null;
		}

		return cpTypeServiceWrapper.getService();
	}

	@Override
	public Set<String> getCPTypeNames() {
		return _serviceTrackerMap.keySet();
	}

	@Override
	public List<CPType> getCPTypes() {
		List<CPType> cpTypes = new ArrayList<>();

		List<ServiceWrapper<CPType>> cpTypeServiceWrappers =
			ListUtil.fromCollection(_serviceTrackerMap.values());

		Collections.sort(
			cpTypeServiceWrappers, _cpTypeServiceWrapperDisplayOrderComparator);

		for (ServiceWrapper<CPType> cpTypeServiceWrapper :
				cpTypeServiceWrappers) {

			CPType cpType = cpTypeServiceWrapper.getService();

			if (!cpType.isActive()) {
				continue;
			}

			cpTypes.add(cpType);
		}

		return Collections.unmodifiableList(cpTypes);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, CPType.class, "commerce.product.type.name",
			ServiceTrackerCustomizerFactory.<CPType>serviceWrapper(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPTypeRegistryImpl.class);

	private final Comparator<ServiceWrapper<CPType>>
		_cpTypeServiceWrapperDisplayOrderComparator =
			new CPTypeServiceWrapperDisplayOrderComparator();
	private ServiceTrackerMap<String, ServiceWrapper<CPType>>
		_serviceTrackerMap;

}